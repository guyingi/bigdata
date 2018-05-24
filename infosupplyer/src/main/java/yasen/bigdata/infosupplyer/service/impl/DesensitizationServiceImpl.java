package yasen.bigdata.infosupplyer.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.hadoop.conf.Configuration;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yasen.bigdata.infosupplyer.conf.InfosupplyerConfiguration;
import yasen.bigdata.infosupplyer.consts.ESConstant;
import yasen.bigdata.infosupplyer.consts.SysConstants;
import yasen.bigdata.infosupplyer.dao.BreastRoiDao;
import yasen.bigdata.infosupplyer.dao.Rio2dDao;
import yasen.bigdata.infosupplyer.dao.SeriesDao;
import yasen.bigdata.infosupplyer.factory.ConfigFactory;
import yasen.bigdata.infosupplyer.pojo.BreastRoiInfoBean;
import yasen.bigdata.infosupplyer.service.DesensitizationService;
import yasen.bigdata.infosupplyer.service.ElasticSearchService;
import yasen.bigdata.infosupplyer.service.HBaseService;
import yasen.bigdata.infosupplyer.service.HdfsService;
import yasen.bigdata.infosupplyer.util.InfoSupplyerTool;
import yasen.bigdata.infosupplyer.util.ZipUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("DesensitizationService")
public class DesensitizationServiceImpl implements DesensitizationService {

    InfosupplyerConfiguration infosupplyerConfiguration = null;
    Configuration hdfsConf = ConfigFactory.getHdfsConfiguration();

//    @Autowired
    ElasticSearchService elasticSearchService;

//    @Autowired
    HBaseService hBaseService;

//    @Autowired
    HdfsService hdfsService;
//    ElasticSearchService elasticSearchService = null;
//    UploaderConfiguration uploaderConf = null;
//    Configuration hdfsConf = ConfigFactory.getHdfsConfiguration();
//    HbaseService hbaseService = null;
//    HdfsService hdfsService = null;

    public DesensitizationServiceImpl(){
        elasticSearchService = new ElasticSearchServiceImpl();
        hBaseService = new HBaseServiceImpl();
        hdfsService = new HdfsServiceImpl();
        infosupplyerConfiguration = new InfosupplyerConfiguration();
    }

    public static void main(String[] arg) throws IOException {
//        String dicomTempDir = "C:\\Users\\WeiGuangWu\\IdeaProjects\\bigdata\\infosupplyer\\target\\classes\\dicomtemp";
        String desensitizetemp = "C:\\Users\\WeiGuangWu\\IdeaProjects\\bigdata\\infosupplyer\\target\\classes\\desensitizetemp";
//        String[] args = new String[4];
//        args[0] = "python";
//        args[1] = "E:\\Users\\WeiGuangWu\\PycharmProjects\\ExportRaw\\py\\demo.py";
//        args[2] = dicomTempDir;
//        args[3] = desensitizetemp;
//        int returnvalue = -2;
//        try {
//            Process p = Runtime.getRuntime().exec(args);
//            try {
//                returnvalue = p.waitFor();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println("returnvalue:"+returnvalue);
        DesensitizationServiceImpl desensitizationService = new DesensitizationServiceImpl();
        desensitizationService.uploadDicomDesensitization(desensitizetemp,"SB");
    }

    @Override
    public Long desensitizedicom(String tag) {

        boolean success = true;
        Long total = 0L;

        String path = TagServiceImpl.class.getClass().getResource("/").getPath();
        path = path.substring(1, path.length());
        path = path.substring(0, path.length() - 1);
        path = path.replace("/",File.separator);

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
        args[0] = "python";
        args[1] = "E:\\Users\\WeiGuangWu\\PycharmProjects\\ExportRaw\\py\\demo.py";
        args[2] = dicomTempDir;
        args[3] = desensitizetemp;
        int returnvalue = -2;
        try {
            Process p = Runtime.getRuntime().exec(args);
            try {
                returnvalue = p.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        }
        if(returnvalue == 0)
            success = true;

        System.out.println("脱敏结果："+success);
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
        System.out.println("步骤1:"+seriesUID);

        /**步骤2：查询ES，获取必要数据:【PatientUID】,【StudyID】【rowkey】,
         * 沿用dicom序列rowkey：rowkey:3位盐值+4位检查+MD5(seriesUID).sub(0,16)+CRC32(时间戳)
         **/
        String patientUID = (String)elasticSearchService.getField(infosupplyerConfiguration.getIndexDicom(), infosupplyerConfiguration.getTypeDicom(), seriesUID, ESConstant.PatientUID_ES);
        String studyID = (String)elasticSearchService.getField(infosupplyerConfiguration.getIndexDicom(), infosupplyerConfiguration.getTypeDicom(), seriesUID, ESConstant.StudyID_ES);
        String rowkey = (String)elasticSearchService.getField(infosupplyerConfiguration.getIndexDicom(), infosupplyerConfiguration.getTypeDicom(), seriesUID, ESConstant.ROWKEY);
        System.out.println("步骤2:patientUID"+patientUID+",studyID:"+studyID+",rowkey:"+rowkey);

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
        System.out.println("步骤3");

        /**步骤4：写入hbase表格中 */
        success = SysConstants.FAILED != hBaseService.putOne(infosupplyerConfiguration.getDicomDisensitizationTablename(),
                infosupplyerConfiguration.getDicomDisensitizationCf(), metaData);

        System.out.println("步骤4："+success);
        /**步骤5：缩略图暂时不做 */

        /**步骤6：上传hdfs */
        if(success) {
            success = SysConstants.SUCCESS == hdfsService.upDicomDesensitization(seriesDir.getAbsolutePath(), hdfspath, hdfsConf);
        }else{
            hBaseService.delete(infosupplyerConfiguration.getDicomDisensitizationTablename(),rowkey);
            return SysConstants.FAILED;
        }
        System.out.println("步骤6："+success);
        /**步骤6：写入es索引中*/
        if(success) {
            success = SysConstants.FAILED != elasticSearchService.insertOne(infosupplyerConfiguration.getIndexDicomDisensitization(),
                    infosupplyerConfiguration.getTypeDicomDisensitization(), seriesUID, metaData);
        }
        System.out.println("es结果："+success);
        return SysConstants.SUCCESS;
    }

    @Override
    public String downloadDesensitizeDicomByTag(String tag) throws Exception {
        boolean success = true;
        String organ = null;
        Map<String,List<JSONObject>> studies = new HashMap<>();
        JSONObject result = null;
        if(tag == null || tag.length() == 0){
            return null;
        }

        /**步骤零：准备工作*/
        //1.获得本地临时目录
        String rootpath = TagServiceImpl.class.getClass().getResource("/").getPath();
        rootpath = rootpath.substring(1, rootpath.length());
        rootpath = rootpath.substring(0, rootpath.length() - 1);
        rootpath = rootpath.replace("/",File.separator);
        String tagtemp = rootpath+File.separator+SysConstants.TAG_TEMP+File.separator+tag;
        if(!(new File(tagtemp).exists())){
            new File(tagtemp).mkdirs();
        }


        /**步骤一：查询es获得该tag下面所有series_UID,SeriesInstanceUID,StudyInstanceUID，organ，hdfs路径*/
        //1.构造查询条件
        if(success) {
            JSONObject searchcondition = new JSONObject();
            searchcondition.put(SysConstants.TAG_PARAM, tag);
            JSONArray backfields = new JSONArray();
            backfields.add(ESConstant.StudyInstanceUID_ES);
            backfields.add(ESConstant.SeriesInstanceUID_ES);
            backfields.add(ESConstant.SeriesUID_ES);
            backfields.add(ESConstant.ORGAN_ES);
            backfields.add(ESConstant.HDFSPATH);
            JSONObject param = new JSONObject();
            param.put(SysConstants.SEARCH_CONDITION, searchcondition);
            param.put(SysConstants.BACKFIELDS, backfields);
            result = elasticSearchService.searchByPaging(param);
            if(result == null || !SysConstants.CODE_000.equals(result.getString(SysConstants.CODE))){
                success = false;
            }
        }

        /**步骤二：手动聚合分类，把属于同一个study的series放在同一个组中，下面以study为单位逐个处理*/
        if(success){
            JSONArray jsonArray = result.getJSONArray(SysConstants.DATA);
            int size = jsonArray.size();
            for(int i = 0; i < size; i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String studyInstanceUID = jsonObject.getString(ESConstant.StudyInstanceUID_ES);
                if(studies.containsKey(studyInstanceUID)){
                    List<JSONObject> jsonObjects = studies.get(studyInstanceUID);
                    jsonObjects.add(jsonObject);
                    studies.put(studyInstanceUID,jsonObjects);
                }else{
                    List<JSONObject> jsonObjects = new ArrayList<JSONObject>();
                    studies.put(studyInstanceUID,jsonObjects);
                }
            }
            organ = jsonArray.getJSONObject(0).getString(ESConstant.ORGAN_ES);
            if(studies.size() == 0){
                success = false;
            }

            //这是对上段代码的测试代码
            for(Map.Entry<String,List<JSONObject>> entry : studies.entrySet()){
                List<JSONObject> values = entry.getValue();
                String printstr = "";
                for(JSONObject value : values){
                    printstr += value.toJSONString();
                }

                System.out.println(entry.getKey()+":"+printstr);
            }
        }


        //下面几个步骤对于不同类型的数据处理方式不同，需要另外写方法分别处理。

        /**步骤三：逐个处理study，查询数据库，对于乳腺而言，得到info.cvs中的数据，得到ROI坐标数据，并写入本地临时文件
         * 将mhd,raw数据从hdfs下载下来名字都根据需要命好，最后这个文件都存放在tagtemp文件夹中。
         * */
        if(success){
           if(organ.equals(ESConstant.BREAST)){
               //对于乳腺而言
               createDesensitizeDataOnLocalForBreast(tag,studies,tagtemp);
           }else if(organ.equals(ESConstant.LUNG)){
               //对于肺而言
               createDesensitizeDataOnLocalForLung(tag,studies,tagtemp);
           }
        }

        /**步骤四：将临时文件目录tagtemp压缩为zip,返回压缩后的文件的绝对路径*/
        ZipUtil.zip(tagtemp,rootpath,tag+".zip");
        String zipFilePath = rootpath + File.separator + tag+".zip";
        return zipFilePath;
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

    private void  createDesensitizeDataOnLocalForBreast(String tag,Map<String,List<JSONObject>> studies,String tagtemp) throws IOException {
        int seq = 1;
        for(Map.Entry<String,List<JSONObject>> entry : studies.entrySet()){
            //1.得到一个study下所有SeriesInstanceUID,存入list
            List<String> seriesList = new ArrayList<>();
            List<JSONObject> value = entry.getValue();
            for(JSONObject jsonObject : value){
                seriesList.add(jsonObject.getString(ESConstant.SeriesInstanceUID_ES));
            }
            //2.访问数据库，查询info.csv需要的数据
            SeriesDao seriesDao = new SeriesDao();
            List<String> seriesuids = new ArrayList<String>();
            for(String seriessop : seriesList){
                String seriesuid = seriesDao.searchSingleFieldBySeriessop(seriessop,SysConstants.SERIES_UID);
                seriesuids.add(seriesuid);
            }
            BreastRoiDao breastRoiDao = new BreastRoiDao();
            Map<String, BreastRoiInfoBean> breastRoiInfoBySeriesuid = breastRoiDao.getBreastRoiInfoBySeriesuid(seriesuids);

            //3.访问数据，查询ROI.csv需要的数据
            Rio2dDao rio2dDao = new Rio2dDao();
            Map<String, String> roiCoordinateBySeriesuid = rio2dDao.getRoiCoordinateBySeriesuid(seriesuids);

            //4.创建csv文件名，写入本地临时目录
            String roiinfoName = tag+"_"+InfoSupplyerTool.formatDigitalToNBit(seq+"",6)+"_info.csv";
            String roiinfoFilePath = tagtemp+File.separator+roiinfoName;
            String roiName = tag+"_"+InfoSupplyerTool.formatDigitalToNBit(seq+"",6)+"ROI.csv";
            String roiFilePath = tagtemp+File.separator+roiName;


            HSSFWorkbook roiinfoworkbook = new HSSFWorkbook();
            HSSFSheet roiinfosheet = roiinfoworkbook.createSheet(roiinfoName);
            HSSFWorkbook roiworkbook = new HSSFWorkbook();
            HSSFSheet roisheet = roiworkbook.createSheet(roiinfoName);
            int count = 1;
            for(String seriesuid : seriesuids){

                BreastRoiInfoBean breastRoiInfoBean = breastRoiInfoBySeriesuid.get(seriesuid);
                HSSFRow roiinforow = roiinfosheet.createRow(count-1);
                roiinforow.createCell(0).setCellValue(InfoSupplyerTool.formatDigitalToNBit(count+"",4));
                roiinforow.createCell(1).setCellValue(breastRoiInfoBean.getLocation());
                roiinforow.createCell(2).setCellValue(breastRoiInfoBean.getClassification());
                roiinforow.createCell(3).setCellValue(breastRoiInfoBean.getShape());
                roiinforow.createCell(4).setCellValue(breastRoiInfoBean.getBoundary1()); //这里采用名称为studydate,实际是seriesDate
                roiinforow.createCell(5).setCellValue(breastRoiInfoBean.getBoundary2());
                roiinforow.createCell(6).setCellValue(breastRoiInfoBean.getDensity());
                roiinforow.createCell(7).setCellValue(breastRoiInfoBean.getQuadrant());
                roiinforow.createCell(8).setCellValue(breastRoiInfoBean.getRisk());


                String roiCoordinate = roiCoordinateBySeriesuid.get(seriesuid);
                JSONArray jsonArray = (JSONArray)JSON.parse(roiCoordinate);
                int size = jsonArray.size();
                HSSFRow roirow = roisheet.createRow(count - 1);
                roirow.createCell(0).setCellValue(InfoSupplyerTool.formatDigitalToNBit(count+"",4));
                for(int i=0; i < size; i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Double x = jsonObject.getDouble("x");
                    Double y = jsonObject.getDouble("y");
                    roirow.createCell(2*i+1).setCellValue(x);
                    roirow.createCell(2*i+2).setCellValue(y);
                }
            }
            FileOutputStream roiinfofout = new FileOutputStream(new File(roiinfoFilePath));
            FileOutputStream roifout = new FileOutputStream(new File(roiFilePath));
            roiinfoworkbook.write(roiinfofout);
            roiworkbook.write(roifout);

            //5.循环处理series，查询series对应部位，创建.mhd，.raw文件名,从hdfs拷贝数据到本地
            for(JSONObject jsonObject : value){
                //es中的seriesinstanceuid与数据库表series中seriessop对应
                String seriessop = jsonObject.getString(ESConstant.SeriesInstanceUID_ES);
                String series_des = seriesDao.searchSingleFieldBySeriessop(seriessop,SysConstants.SERIES_DES);
                String name = tag+"_"+InfoSupplyerTool.formatDigitalToNBit(seq+"",6)+"_"+series_des;
                String hdfspath = jsonObject.getString(ESConstant.HDFSPATH);
                String[] desensitizedFileLocal = hdfsService.downDicomDesensitization(hdfspath, tagtemp,hdfsConf);
                String mhdFilePath = desensitizedFileLocal[0];
                String rawFilePath = desensitizedFileLocal[1];

                //读取mhd文件，修改其中的ElementDataFile属性，这个属性是raw文件名。
                BufferedReader br = new BufferedReader(new FileReader(mhdFilePath));
                LineNumberReader lnr = new LineNumberReader(new FileReader(mhdFilePath));
                lnr.skip(new File(mhdFilePath).length());
                String []mhdtemp = new String[lnr.getLineNumber()];
                String temp = null;
                int number = 0;
                int positionline = -1;
                while((temp = br.readLine()) != null){
                    if(temp.startsWith("ElementDataFile"))
                        positionline = number;
                    mhdtemp[number++] = temp;
                }
                mhdtemp[positionline] = "ElementDataFile = "+ name+".raw";
                FileWriter fw = new FileWriter(mhdFilePath);
                for(String line : mhdtemp){
                    fw.write(line);
                }
                fw.close();

                //修改raw文件名
                new File(rawFilePath).renameTo(new File(tagtemp+File.separator+name+".raw"));
            }
        }
    }

    private void createDesensitizeDataOnLocalForLung(String tag,Map<String,List<JSONObject>> studies,String tagtemp) throws IOException {
        int seq = 1;

        for (Map.Entry<String, List<JSONObject>> entry : studies.entrySet()) {
            List<String> hdfs = new ArrayList<>();
            List<JSONObject> values = entry.getValue();
            for(JSONObject value : values){
                hdfs.add(value.getString(ESConstant.HDFSPATH));
            }
            /**步骤四：从hdfs下载.mhd,.raw数据到本地临时目录*/
            for(String hdfspath : hdfs){
                String name = tag+"_"+InfoSupplyerTool.formatDigitalToNBit(seq+"",6);
                String[] desensitizedFileLocal = hdfsService.downDicomDesensitization(hdfspath, tagtemp,hdfsConf);
                //读取mhd文件，修改其中的ElementDataFile属性，这个属性是raw文件名。
                String mhdFilePath = desensitizedFileLocal[0];
                String rawFilePath = desensitizedFileLocal[1];
                BufferedReader br = new BufferedReader(new FileReader(mhdFilePath));
                LineNumberReader lnr = new LineNumberReader(new FileReader(mhdFilePath));
                lnr.skip(new File(mhdFilePath).length());
                String []mhdtemp = new String[lnr.getLineNumber()];
                String temp = null;
                int number = 0;
                int positionline = -1;
                while((temp = br.readLine()) != null){
                    if(temp.startsWith("ElementDataFile"))
                        positionline = number;
                    mhdtemp[number++] = temp;
                }
                mhdtemp[positionline] = "ElementDataFile = "+ name+".raw";
                FileWriter fw = new FileWriter(mhdFilePath);
                for(String line : mhdtemp){
                    fw.write(line);
                }
                fw.close();

                //修改raw文件名
                new File(rawFilePath).renameTo(new File(tagtemp+File.separator+name+".raw"));
            }
        }
    }

}
