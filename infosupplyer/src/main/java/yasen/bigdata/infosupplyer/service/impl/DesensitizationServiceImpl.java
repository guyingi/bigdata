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
import yasen.bigdata.infosupplyer.dao.DicomTagDao;
import yasen.bigdata.infosupplyer.dao.Rio2dDao;
import yasen.bigdata.infosupplyer.dao.SeriesDao;
import yasen.bigdata.infosupplyer.factory.ConfigFactory;
import yasen.bigdata.infosupplyer.pojo.BreastRoiInfoBean;
import yasen.bigdata.infosupplyer.pojo.db.DicomTag;
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

    @Autowired
    ElasticSearchService elasticSearchService;

    @Autowired
    HBaseService hBaseService;

    @Autowired
    HdfsService hdfsService;

    @Autowired
    Rio2dDao rio2dDao;

    @Autowired
    SeriesDao seriesDao;

    @Autowired
    BreastRoiDao breastRoiDao;

    @Autowired
    DicomTagDao dicomTagDao;

    public DesensitizationServiceImpl() {
        elasticSearchService = new ElasticSearchServiceImpl();
        hBaseService = new HBaseServiceImpl();
        hdfsService = new HdfsServiceImpl();
        infosupplyerConfiguration = new InfosupplyerConfiguration();
    }

    @Override
    public Long desensitizedicom(String tag) {

        boolean success = true;
        Long total = 0L;

        //path是工程的类路径
        String path = null;
        // /temp/desensitizetemp/desensitizebeforetemp 临时存放下载下来的待脱敏数据目录
        String desensitizeBeforeTempPath = infosupplyerConfiguration.getDesensitizeBeforeTempPath();
        ///temp/desensitizetemp/desensitizeaftertemp 是存放脱敏后的数据的临时目录
        String desensitizeAfterTempPath = infosupplyerConfiguration.getDesensitizeAfterTempPath();

        String desensitizeBeforeTagPath = desensitizeBeforeTempPath + File.separator + tag;
        String desensitizeAfterTagPath = desensitizeAfterTempPath + File.separator + tag;

        /**********根据tag字段查询属于这批tag的所有dicom序列的唯一id,以及hdfs路径******/
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
        if(success) {
            if(! new File(desensitizeBeforeTagPath).exists()){
                new File(desensitizeBeforeTagPath).mkdirs();
            }
            //下载dicom文件到本地，存放于临时目录
            if(!hdfsService.downloadDicom(hdfspaths, desensitizeBeforeTagPath))
                success = false;
        }

        //将上一步临时目录中的数据脱敏处理并存放于另一个临时目录
        if(success){
            String[] args = new String[4];
            args[0] = infosupplyerConfiguration.getPythoncmd();
            args[1] = infosupplyerConfiguration.getPythonscript();
            args[2] = desensitizeBeforeTagPath;
            args[3] = desensitizeAfterTagPath;
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
            if(returnvalue == 0 &&
                    new File(desensitizeBeforeTagPath).listFiles().length
                            ==new File(desensitizeAfterTagPath).listFiles().length) {
                success = true;
            }else {
                success = false;
            }
        }

        System.out.println("脱敏结果："+success);
        //调用上传接口，上传存放脱敏数据的临时目录
        if(success){
            try {
                uploadDicomDesensitization(desensitizeAfterTagPath,tag);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /*********删除临时目录*************************/
        InfoSupplyerTool.delFolder(desensitizeBeforeTagPath);
        InfoSupplyerTool.delFolder(desensitizeAfterTagPath);

        /**********修改tag状态为已脱敏***********************/
        DicomTag dicomTag = new DicomTag();
        dicomTag.setTagname(tag);
        dicomTag.setDesensitize(1);
        dicomTagDao.updateDesensitize(dicomTag);

        System.out.println("脱敏结束");
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
        if(rowkey == null){
            System.out.println("文件名为："+seriesDir.getAbsolutePath()+"的脱敏数据在es中无数据，该次上传失败");
            return SysConstants.FAILED;
        }

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
        String desensitizeDownloadTemp = infosupplyerConfiguration.getDesensitizeDownloadTempPath();
        String desensitizeDownloadTagPath = desensitizeDownloadTemp + File.separator + tag;
        if(!(new File(desensitizeDownloadTagPath).exists())){
            new File(desensitizeDownloadTagPath).mkdirs();
        }
        System.out.println("临时目录："+desensitizeDownloadTagPath);

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
                    jsonObjects.add(jsonObject);
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
               createDesensitizeDataOnLocalForBreast(tag,studies,desensitizeDownloadTagPath);
           }else if(organ.equals(ESConstant.LUNG)){
               //对于肺而言
               createDesensitizeDataOnLocalForLung(tag,studies,desensitizeDownloadTagPath);
           }
        }
        //删除hdfs api产生的crc校验文件，
        {
            File tagtempfile = new File(desensitizeDownloadTagPath);
            for(File e : tagtempfile.listFiles()){
                System.out.println(e.getName());
                if(e.getName().endsWith("crc")){
                    e.delete();
                }
            }
        }

        /**步骤四：将临时文件目录tagtemp压缩为zip,返回压缩后的文件的绝对路径*/
        ZipUtil.zip(desensitizeDownloadTagPath,desensitizeDownloadTemp,tag+".zip");
        String zipFilePath = desensitizeDownloadTemp + File.separator + tag+".zip";
        if(new File(desensitizeDownloadTagPath).exists()){
            InfoSupplyerTool.delFolder(desensitizeDownloadTagPath);
        }
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
        int seq = 0;
        for(Map.Entry<String,List<JSONObject>> entry : studies.entrySet()){
            seq++;
            //1.得到一个study下所有SeriesInstanceUID,存入list
            List<String> seriesList = new ArrayList<>();
            List<JSONObject> value = entry.getValue();
            for(JSONObject jsonObject : value){
                seriesList.add(jsonObject.getString(ESConstant.SeriesInstanceUID_ES));
            }
            //2.访问数据库，查询info.csv需要的数据
            List<String> seriesuids = new ArrayList<String>();
            for(String seriessop : seriesList){
                String seriesuid = seriesDao.searchSingleFieldBySeriessop(seriessop,SysConstants.SERIES_UID);
                System.out.println("seriesuid:"+seriesuid);
                seriesuids.add(seriesuid);
            }
            Map<String, BreastRoiInfoBean> breastRoiInfo = breastRoiDao.getBreastRoiInfoBySeriesuid(seriesuids);

            //3.访问数据，查询ROI.csv需要的数据
            Map<String, String> roiCoordinates = rio2dDao.getRoiCoordinateBySeriesuid(seriesuids);

            //4.创建csv文件名，写入本地临时目录
            String roiinfoName = tag+"_"+InfoSupplyerTool.formatDigitalToNBit(seq+"",6)+"_info.csv";
            String roiinfoFilePath = tagtemp+File.separator+roiinfoName;
            String roiName = tag+"_"+InfoSupplyerTool.formatDigitalToNBit(seq+"",6)+"_ROI.csv";
            String roiFilePath = tagtemp+File.separator+roiName;


            HSSFWorkbook roiinfoworkbook = new HSSFWorkbook();
            HSSFSheet roiinfosheet = roiinfoworkbook.createSheet(roiinfoName);
            HSSFWorkbook roiworkbook = new HSSFWorkbook();
            HSSFSheet roisheet = roiworkbook.createSheet(roiinfoName);
            int count = 1;
            for(String seriesuid : seriesuids){
                if(breastRoiInfo.size() != 0) {
                    BreastRoiInfoBean breastRoiInfoBean = breastRoiInfo.get(seriesuid);
                    if(breastRoiInfoBean != null) {
                        HSSFRow roiinforow = roiinfosheet.createRow(count - 1);
                        roiinforow.createCell(0).setCellValue(InfoSupplyerTool.formatDigitalToNBit(count + "", 4));
                        roiinforow.createCell(1).setCellValue(breastRoiInfoBean.getLocation());
                        roiinforow.createCell(2).setCellValue(breastRoiInfoBean.getClassification());
                        roiinforow.createCell(3).setCellValue(breastRoiInfoBean.getShape());
                        roiinforow.createCell(4).setCellValue(breastRoiInfoBean.getBoundary1()); //这里采用名称为studydate,实际是seriesDate
                        roiinforow.createCell(5).setCellValue(breastRoiInfoBean.getBoundary2());
                        roiinforow.createCell(6).setCellValue(breastRoiInfoBean.getDensity());
                        roiinforow.createCell(7).setCellValue(breastRoiInfoBean.getQuadrant());
                        roiinforow.createCell(8).setCellValue(breastRoiInfoBean.getRisk());
                    }
                }


                if(roiCoordinates.size() != 0) {
                    String roiCoordinate = roiCoordinates.get(seriesuid);
                    if(roiCoordinate != null){
                        JSONArray jsonArray = (JSONArray) JSON.parse(roiCoordinate);
                        int size = jsonArray.size();
                        HSSFRow roirow = roisheet.createRow(count - 1);
                        roirow.createCell(0).setCellValue(InfoSupplyerTool.formatDigitalToNBit(count + "", 4));
                        for (int i = 0; i < size; i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Double x = jsonObject.getDouble("x");
                            Double y = jsonObject.getDouble("y");
                            roirow.createCell(2 * i + 1).setCellValue(x);
                            roirow.createCell(2 * i + 2).setCellValue(y);
                        }
                    }
                }
            }
            FileOutputStream roiinfofout = new FileOutputStream(new File(roiinfoFilePath));
            roiinfoworkbook.write(roiinfofout);
            roiinfoworkbook.close();
            roiinfofout.close();
            FileOutputStream roifout = new FileOutputStream(new File(roiFilePath));
            roiworkbook.write(roifout);
            roiworkbook.close();
            roifout.close();

            //5.循环处理series，查询series对应部位，创建.mhd，.raw文件名,从hdfs拷贝数据到本地
            for(JSONObject jsonObject : value){
                //es中的seriesinstanceuid与数据库表series中seriessop对应
                String seriessop = jsonObject.getString(ESConstant.SeriesInstanceUID_ES);
                String seriesUID = jsonObject.getString(ESConstant.SeriesUID_ES);
                String series_des = seriesDao.searchSingleFieldBySeriessop(seriessop,SysConstants.SERIES_DES);
                series_des = series_des.replace(SysConstants.SPACE,"").trim();

                String name = tag+"_"+InfoSupplyerTool.formatDigitalToNBit(seq+"",6)+"_"+series_des;
                //因为LMLO类型的可能有多个，所以文件名命名需要在末尾加个数据
                int tempnumber = 1;
                while(new File(tagtemp+File.separator+name+".mhd").exists()){
                    name += "-"+tempnumber;
                }

                String hdfspath = (String)elasticSearchService.getField(infosupplyerConfiguration.getIndexDicomDisensitization(),
                        infosupplyerConfiguration.getTypeDicomDisensitization(),seriesUID,ESConstant.HDFSPATH);
                String[] desensitizedFileLocal = hdfsService.downDicomDesensitization(tagtemp,hdfspath,hdfsConf);
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
                br.close();
                lnr.close();
                mhdtemp[positionline] = "ElementDataFile = "+ name+".raw";
                FileWriter fw = new FileWriter(mhdFilePath);
                for(String line : mhdtemp){
                    fw.write(line);
                    fw.write("\n");
                }
                fw.close();

                //修改raw文件名
                new File(rawFilePath).renameTo(new File(tagtemp+File.separator+name+".raw"));
                new File(mhdFilePath).renameTo(new File(tagtemp+File.separator+name+".mhd"));
            }
        }
    }

    private void createDesensitizeDataOnLocalForLung(String tag,Map<String,List<JSONObject>> studies,String tagtemp) throws IOException {
        int seq = 0;

        for (Map.Entry<String, List<JSONObject>> entry : studies.entrySet()) {
            seq++;
            List<String> hdfs = new ArrayList<>();
            List<JSONObject> values = entry.getValue();
            for(JSONObject value : values){
                String seriesUID = value.getString(ESConstant.SeriesUID_ES);
                String hdfspath = (String)elasticSearchService.getField(infosupplyerConfiguration.getIndexDicomDisensitization(),
                        infosupplyerConfiguration.getTypeDicomDisensitization(),seriesUID,ESConstant.HDFSPATH);
                hdfs.add(hdfspath);
            }
            /**步骤四：从hdfs下载.mhd,.raw数据到本地临时目录*/
            for(String hdfspath : hdfs){
                String name = tag+"_"+InfoSupplyerTool.formatDigitalToNBit(seq+"",6);
                String[] desensitizedFileLocal = hdfsService.downDicomDesensitization(tagtemp,hdfspath, hdfsConf);
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
                br.close();
                lnr.close();
                mhdtemp[positionline] = "ElementDataFile = "+ name+".raw";
                FileWriter fw = new FileWriter(mhdFilePath);
                for(String line : mhdtemp){
                    fw.write(line);
                    fw.write("\n");
                }
                fw.close();

                //修改raw文件名
                new File(rawFilePath).renameTo(new File(tagtemp+File.separator+name+".raw"));
                new File(mhdFilePath).renameTo(new File(tagtemp+File.separator+name+".mhd"));
            }
        }
    }

}
