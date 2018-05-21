package yasen.bigdata.infosupplyer.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.hadoop.conf.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yasen.bigdata.infosupplyer.conf.InfosupplyerConfiguration;
import yasen.bigdata.infosupplyer.consts.ESConstant;
import yasen.bigdata.infosupplyer.consts.SysConstants;
import yasen.bigdata.infosupplyer.factory.ConfigFactory;
import yasen.bigdata.infosupplyer.service.DesensitizationService;
import yasen.bigdata.infosupplyer.service.ElasticSearchService;
import yasen.bigdata.infosupplyer.service.HBaseService;
import yasen.bigdata.infosupplyer.service.HdfsService;
import yasen.bigdata.infosupplyer.util.InfoSupplyerTool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("DesensitizationService")
public class DesensitizationServiceImpl implements DesensitizationService {

    InfosupplyerConfiguration infosupplyerConfiguration = null;
    Configuration hdfsConf = ConfigFactory.getHdfsConfiguration();

    @Autowired
    ElasticSearchService elasticSearchService;

    @Autowired
    HBaseService hBaseService;

    @Autowired
    HdfsService hdfsService;
//    ElasticSearchService elasticSearchService = null;
//    UploaderConfiguration uploaderConf = null;
//    Configuration hdfsConf = ConfigFactory.getHdfsConfiguration();
//    HbaseService hbaseService = null;
//    HdfsService hdfsService = null;

    public DesensitizationServiceImpl(){
//        elasticSearchService = ElasticSearchServiceFactory.getElasticSearchService();
//        hbaseService = HbaseServiceFactory.getHbaseService();
//        hdfsService = HdfsServiceFactory.getHdfsService();
        infosupplyerConfiguration = new InfosupplyerConfiguration();
    }

    @Override
    public Long desensitizedicom(String tag) {

        boolean success = true;
        Long total = 0L;

        String path = TagServiceImpl.class.getClass().getResource("/").getPath();
        path = path.substring(1, path.length());
        path = path.substring(0, path.length() - 1);

        //获取序列id以及hdfs路径
        List<String> hdfspaths = new ArrayList<>();
        if(success) {
            JSONObject param = new JSONObject();
            JSONObject searchcondition = new JSONObject();
            searchcondition.put("tag", tag);
            JSONArray backfields = new JSONArray();
            backfields.add(ESConstant.SeriesUID_ES);
            backfields.add(ESConstant.HDFSPATH);
            param.put(SysConstants.SEARCH_CONDITION, searchcondition);
            param.put(SysConstants.BACKFIELDS, backfields);
            JSONObject metaMsg = elasticSearchService.searchByPaging(param);
            if (SysConstants.CODE_000.equals(metaMsg.getString(SysConstants.CODE))) {
                total = metaMsg.getLong("total");
                JSONArray data = metaMsg.getJSONArray("data");
                for (int i = 0; i < total; i++) {
                    JSONObject jsonObject = data.getJSONObject(i);
                    hdfspaths.add(jsonObject.getString(ESConstant.HDFSPATH));
                }
            }
            success = success;
            if(hdfspaths.size() == 0)
                success = false;
        }

        //创建本地临时目录

        String dicomTempDir = null;
        if(success) {
            String dicomtemp = path + File.separator + SysConstants.DICOM_TEMP;
            File file = new File(dicomtemp);
            file.mkdir();
            dicomTempDir = file.getAbsolutePath();  //C:\\xx\temp

            //下载dicom文件到本地，存放于临时目录
            if(!hdfsService.downloadDicom(hdfspaths, dicomTempDir))
                success = false;
        }

        //将上一步临时目录中的数据脱敏处理存放于另一个临时目录
        String desensitizetemp = path + File.separator + SysConstants.DESENSITIZE_TEMP;
        String[] args = new String[4];
        args[0] = "python3";
        args[1] = "E:\\Users\\WeiGuangWu\\PycharmProjects\\ExportRaw\\py";
        args[2] = dicomTempDir;
        args[3] = desensitizetemp;
        try {
            Runtime.getRuntime().exec(args);
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        }

        //调用上传接口，上传存放脱敏数据的临时目录
        if(success){
            try {
                uploadDicomDesensitization(desensitizetemp,tag);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return total;
    }


    @Override
    public int uploadDicomDesensitization(String desensitizationDir,String tag) throws IOException {
        if(!validateDir(desensitizationDir))
            return 0;
        List<File> seriesDirs = listDir(desensitizationDir);
        for(File seriesDir : seriesDirs){
            //逐个上传
            uploadDicomDesensitization(seriesDir,tag);
        }
        return 0;
    }

    private int uploadDicomDesensitization(File seriesDir,String tag) throws IOException {
        boolean success = true;

        /**步骤1：先获取SeriesUID,即目录名称。*/
        String seriesUID = seriesDir.getName();

        /**步骤2：查询ES，获取必要数据:【PatientUID】,【StudyID】【rowkey】,
         * 沿用dicom序列rowkey：rowkey:3位盐值+4位检查+MD5(seriesUID).sub(0,16)+CRC32(时间戳)
         **/
        String patientUID = (String)elasticSearchService.getField(infosupplyerConfiguration.getIndexDicom(), infosupplyerConfiguration.getTypeDicom(), seriesUID, ESConstant.PatientUID_ES);
        String studyID = (String)elasticSearchService.getField(infosupplyerConfiguration.getIndexDicom(), infosupplyerConfiguration.getTypeDicom(), seriesUID, ESConstant.StudyID_ES);
        String rowkey = (String)elasticSearchService.getField(infosupplyerConfiguration.getIndexDicom(), infosupplyerConfiguration.getTypeDicom(), seriesUID, ESConstant.ROWKEY);

        /**
         * 步骤3：目录/yasen/bigdata/raw/tag/year/month/day/seriesUID下面存放raw+mhd文件
         */
        String entryDate  = InfoSupplyerTool.getTodayDate();
        String dirPrefixDesensitization = infosupplyerConfiguration.getDirPrefixDesensitization();
        String datePath = InfoSupplyerTool.parseDateToPath(entryDate);
        String hdfspath = dirPrefixDesensitization+SysConstants.LEFT_SLASH+tag+datePath+SysConstants.LEFT_SLASH+seriesUID;

        Map<String,String> metaData = new HashMap<String,String>();
        metaData.put(ESConstant.ROWKEY,rowkey);
        metaData.put(ESConstant.PatientUID_ES,patientUID);
        metaData.put(ESConstant.SeriesUID_ES,seriesUID);
        metaData.put(ESConstant.StudyID_ES,studyID);
        metaData.put(ESConstant.HDFSPATH,hdfspath);
        metaData.put(ESConstant.ENTRYDATE_ES,entryDate);
        metaData.put(ESConstant.TAG_ES,tag);

        for(Map.Entry<String,String> entry : metaData.entrySet()){
            System.out.println(entry.getKey()+":"+entry.getValue());
        }

        /**步骤4：写入hbase表格中 */
        success = SysConstants.FAILED != hBaseService.putOne(infosupplyerConfiguration.getDicomDisensitizationTablename(),
                infosupplyerConfiguration.getDicomDisensitizationCf(), metaData);

        System.out.println("hbase结果："+success);
        /**步骤5：缩略图暂时不做 */

        /**步骤6：上传hdfs */
        if(success) {
            success = SysConstants.SUCCESS == hdfsService.upDicomDesensitization(seriesDir.getAbsolutePath(), hdfspath, hdfsConf);
        }else{
            hBaseService.delete(infosupplyerConfiguration.getDicomDisensitizationTablename(),rowkey);
            return SysConstants.FAILED;
        }
        System.out.println("hdfs结果："+success);
        /**步骤6：写入es索引中*/
        if(success) {
            success = SysConstants.FAILED != elasticSearchService.insertOne(infosupplyerConfiguration.getIndexDicomDisensitization(),
                    infosupplyerConfiguration.getTypeDicomDisensitization(), seriesUID, metaData);
        }
        System.out.println("es结果："+success);
        return SysConstants.SUCCESS;
    }

    /**************下面是内部方法****************/
    private List<File> listDir(String path){
        File file = new File(path);
        List<File> dirs = new ArrayList<File>();
        for(File e : file.listFiles()){
            dirs.add(e);
        }
        return dirs;
    }

    private boolean validateDir(String path){
        File file = new File(path);
        if(file.exists()&&file.isDirectory()){
            return true;
        }
        return false;
    }
    private String generateFileHdfsPosition(){
        return null;
    }
}
