package qed.bigdata.infosupplyer.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import qed.bigdata.infosupplyer.conf.InfosupplyerConfiguration;
import qed.bigdata.infosupplyer.consts.DataTypeEnum;
import qed.bigdata.infosupplyer.consts.EsConsts;
import qed.bigdata.infosupplyer.consts.SysConsts;
import qed.bigdata.infosupplyer.dao.*;
import qed.bigdata.infosupplyer.help.StreamGobbler;
import qed.bigdata.infosupplyer.pojo.db.DicomTag;
import qed.bigdata.infosupplyer.pojo.marktool.BreastRoiEntity;
import qed.bigdata.infosupplyer.pojo.marktool.Roi2dEntity;
import qed.bigdata.infosupplyer.pojo.marktool.Roi3dEntity;
import qed.bigdata.infosupplyer.util.InfoSupplyerTool;
import qed.bigdata.infosupplyer.factory.ConfigFactory;
import qed.bigdata.infosupplyer.service.DesensitizationService;
import qed.bigdata.infosupplyer.service.ElasticSearchService;
import qed.bigdata.infosupplyer.service.HBaseService;
import qed.bigdata.infosupplyer.service.HdfsService;
import qed.bigdata.infosupplyer.util.ZipUtil;

import javax.sound.sampled.Line;
import java.io.*;
import java.util.*;

@Service("DesensitizationService")
public class DesensitizationServiceImpl implements DesensitizationService {
    static Logger logger = Logger.getLogger(DesensitizationServiceImpl.class);

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
    Roi3dDao roi3dDao;

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

    //上传单个study【乳腺】，series【肺】的脱敏数据
    private int uploadDicomDesensitization(File combineTemp,String seriesIdOrStudyId,String tag,String type) throws IOException {
        logger.log(Level.INFO,"方法:uploadDicomDesensitization 被调用，参数:{seriesDir="+combineTemp.getAbsolutePath()
                +",tag="+tag
                +",seriesIdOrStudyId="+seriesIdOrStudyId
                +",tag="+tag
                +",type="+type
                +"}");

        boolean success = true;

        /**步骤1：先获取SeriesUID,即目录名称。*/
        String StudyInstanceUID = null;
        String SeriesInstanceUID = null;
        if(SysConsts.BREAST.equals(type)){
            StudyInstanceUID = seriesIdOrStudyId;
        }else if(SysConsts.LUNG.equals(type)){
            SeriesInstanceUID = seriesIdOrStudyId;
        }
        logger.log(Level.INFO,"步骤1:StudyInstanceUID="+StudyInstanceUID+",SeriesInstanceUID="+SeriesInstanceUID);

        /**步骤2：生成rowkey 30位
         * 沿用dicom序列rowkey：rowkey:3位盐值+4位检查+MD5(seriesUID).sub(0,16)+CRC32(时间戳)
         **/
        String rowkey = InfoSupplyerTool.generateRandonNumber(3)+InfoSupplyerTool.getMD5(seriesIdOrStudyId).substring(0,16)
                +InfoSupplyerTool.getTimeStamp();

        /**
         * 步骤3：目录/yasen/bigdata/raw/tag/year/month/day/seriesUID下面存放raw+mhd文件
         */
        String entryDate  = InfoSupplyerTool.getTodayDate();
        String dirPrefixDesensitization = infosupplyerConfiguration.getDirPrefixDesensitization();
        String datePath = InfoSupplyerTool.parseDateToPath(entryDate);
        String hdfspath = null;
        if(SysConsts.BREAST.equals(type)){
            hdfspath = dirPrefixDesensitization+SysConsts.LEFT_SLASH+tag+datePath+SysConsts.LEFT_SLASH+StudyInstanceUID.replaceAll("\\.","x");
        }else if(SysConsts.LUNG.equals(type)){
            hdfspath = dirPrefixDesensitization+SysConsts.LEFT_SLASH+tag+datePath+SysConsts.LEFT_SLASH+SeriesInstanceUID.replaceAll("\\.","x");
        }

        Map<String,String> metaData = new HashMap<String,String>();
        metaData.put(EsConsts.ROWKEY,rowkey);
        metaData.put(EsConsts.SeriesInstanceUID_ES_DCM,SeriesInstanceUID);
        metaData.put(EsConsts.StudyInstanceUID_ES_DCM,StudyInstanceUID);
        metaData.put(EsConsts.HDFSPATH,hdfspath);
        metaData.put(EsConsts.ENTRYDATE,entryDate);
        metaData.put(EsConsts.TAG,tag);

        JSONObject printJson = JSONObject.parseObject(JSON.toJSONString(metaData));
        logger.log(Level.INFO,"步骤3、生成的元数据:"+printJson.toJSONString());

        /**步骤4：写入hbase表格中 */
        success = SysConsts.FAILED != hBaseService.putOne(infosupplyerConfiguration.getDicomDisensitizationTablename(),
                infosupplyerConfiguration.getDicomDisensitizationCf(), metaData);

        logger.log(Level.INFO,"步骤4、元数据写入hbase:"+success);

        /**步骤5：缩略图暂时不做 */

        /**步骤6：上传hdfs */
        if(success) {
            success = SysConsts.SUCCESS == hdfsService.upDicomDesensitization(combineTemp.getAbsolutePath(), hdfspath, hdfsConf);
        }else{
            hBaseService.delete(infosupplyerConfiguration.getDicomDisensitizationTablename(),rowkey);
            return SysConsts.FAILED;
        }

        logger.log(Level.INFO,"步骤6、上传hdfs:"+success);

        /**步骤6：写入es索引中*/
        if(success) {
            success = SysConsts.FAILED != elasticSearchService.insertOne(infosupplyerConfiguration.getIndexDicomDisensitization(),
                    infosupplyerConfiguration.getTypeDicomDisensitization(),null, metaData);
        }
        logger.log(Level.INFO,"步骤6、写入es:"+success);
        logger.log(Level.INFO,"方法 uploadDicomDesensitization 流程结束");
        return SysConsts.SUCCESS;
    }

    @Override
    public String downloadDesensitizeDicomByTag(String tag) throws Exception {
        logger.log(Level.INFO,"方法:downloadDesensitizeDicomByTag 被调用，参数:{tag="+tag+"}");

        boolean success = true;
        String organ = null;
        List<String> hdfspaths = new ArrayList<>();
        Map<String,List<JSONObject>> studies = new HashMap<>();
        JSONObject result = null;
        if(tag == null || tag.length() == 0){
            return null;
        }

        /**步骤一：准备临时目录*/
        String desensitizeDownloadTemp = infosupplyerConfiguration.getDesensitizeDownloadTempPath();
        String desensitizeDownloadTagPath = desensitizeDownloadTemp + File.separator + tag;
        String desensitizeDownloadTagCombinePath = desensitizeDownloadTemp + File.separator + tag+"_combine";
        if(!(new File(desensitizeDownloadTagPath).exists())){
            new File(desensitizeDownloadTagPath).mkdirs();
        }
        logger.log(Level.INFO,"临时目录："+desensitizeDownloadTagPath);

        /**步骤二：查询es 索引dicomdisensitizationindex获得该tag下面所有SeriesInstanceUID,StudyInstanceUID，hdfs路径*/
        //1.构造查询条件
        if(success) {
            JSONObject param = new JSONObject();
            JSONArray criteria = new JSONArray();
            JSONObject obj = new JSONObject();
            obj.put(SysConsts.SECTION,SysConsts.NO);
            obj.put(SysConsts.KEYWORD,EsConsts.TAG);
            obj.put(SysConsts.VALUE,tag);
            criteria.add(obj);
            param.put(SysConsts.CRITERIA,criteria);

            JSONArray backfields = new JSONArray();
            backfields.add(EsConsts.StudyInstanceUID_ES_DCM);
            backfields.add(EsConsts.SeriesInstanceUID_ES_DCM);
            backfields.add(EsConsts.HDFSPATH);
            param.put(SysConsts.CRITERIA, criteria);
            param.put(SysConsts.BACKFIELDS,backfields);
            param.put(SysConsts.DATATYPE,SysConsts.TYPE_MULTIDIMENSION);
            DataTypeEnum type = DataTypeEnum.MULTIDIMENSION;
            result = elasticSearchService.searchByPaging(param,type);
            if(result == null || !SysConsts.CODE_000.equals(result.getString(SysConsts.CODE))){
                success = false;
            }else{
                JSONArray data = result.getJSONArray(SysConsts.DATA);
                int size = data.size();
                /***这里之所以敢用这种方式判断这个tag的所有序列是乳腺还是肺部，是因为通常一个tag里面的序列类别都相同**/
                if(size>0){
                    JSONObject tempObj = data.getJSONObject(0);
                    String studyInstanceUID = tempObj.getString(EsConsts.StudyInstanceUID_ES_DCM);
                    String seriesInstanceUID = tempObj.getString(EsConsts.SeriesInstanceUID_ES_DCM);
                    if(!StringUtils.isBlank(studyInstanceUID) && StringUtils.isBlank(seriesInstanceUID)){
                        organ = SysConsts.BREAST;
                    }else if(StringUtils.isBlank(studyInstanceUID) && !StringUtils.isBlank(seriesInstanceUID)){
                        organ = SysConsts.LUNG;
                    }else{
                        success = false;
                    }
                }
                for(int i=0;i<size;i++){
                    JSONObject tempObj = data.getJSONObject(i);
                    String hdfspath = tempObj.getString(EsConsts.HDFSPATH);
                    hdfspaths.add(hdfspath);
                }
            }
        }

        logger.log(Level.INFO,"查询es获得所属tag["+tag+"]所有序列:"+(success?result.getLong(SysConsts.TOTAL):success));

        /**步骤三、下载脱敏数据到本地，如果是乳腺则以study为单位，如果时肺结节则以series为单位。**/
        if(success){
            for(String hdfspath : hdfspaths){
                hdfsService.copyDirToLocal(hdfspath,desensitizeDownloadTagPath,ConfigFactory.getHdfsConfiguration());
            }
            //删除hdfs api产生的crc校验文件，
            for(File seriesFile : new File(desensitizeDownloadTagPath).listFiles()){
                for(File e : seriesFile.listFiles()){
                    if(e.getName().endsWith("crc")){
                        e.delete();
                    }
                }
            }
        }

        /**步骤四、遍历本地存放脱敏数据的文件夹，先改名，序号从1开始，*/
        File tagFile = new File(desensitizeDownloadTagPath);
        if(success){
            if(SysConsts.BREAST.equals(organ)){
                int seq = 1;
                for(File file : tagFile.listFiles()){
                    Set<String> nameSet = new HashSet<>();
                    for(File subFile : file.listFiles()){
                        String tempName = subFile.getName();
                        if(!tempName.endsWith("csv"))
                            nameSet.add(tempName.substring(0,tempName.indexOf(".")));
                    }
                    String seriesOrStudyPath = file.getAbsolutePath();

                    String prefixName = tag+SysConsts.UNDER_LINE+InfoSupplyerTool.formatDigitalToNBit(seq+"",6)
                            +SysConsts.UNDER_LINE;
                    //改raw,mhd的名字
                    for(String oldname : nameSet){
                        String newname = prefixName+oldname;
                        rename(oldname,newname,seriesOrStudyPath);
                    }
                    //改info.csv,roi.csv的名字
                    String infocsvOldname = seriesOrStudyPath+File.separator+"info.csv";
                    String infocsvNewname = seriesOrStudyPath+File.separator+prefixName+"info.csv";
                    String roicsvOldname = seriesOrStudyPath+File.separator+"ROI.csv";
                    String roicsvNewname = seriesOrStudyPath+File.separator+prefixName+"ROI.csv";
                    new File(infocsvOldname).renameTo(new File(infocsvNewname));
                    new File(roicsvOldname).renameTo(new File(roicsvNewname));
                    seq++;
                }
            }else if(SysConsts.LUNG.equals(organ)){
                int seq = 1;
                for(File file : tagFile.listFiles()){
                    String prefixName = tag+SysConsts.UNDER_LINE+InfoSupplyerTool.formatDigitalToNBit(seq+"",6);
                    String oldname = null;
                    for(File subFile : file.listFiles()){
                        String tempName = subFile.getName();
                        if(!tempName.endsWith("csv")){
                            oldname = tempName.substring(0,tempName.indexOf("."));
                        }
                    }
                    String seriesOrStudyPath = file.getAbsolutePath();
                    //改raw,mhd的名字
                    String newname = prefixName;
                    rename(oldname,newname,seriesOrStudyPath);
                    //改info.csv,roi.csv的名字
                    String infocsvOldname = seriesOrStudyPath+File.separator+"info.csv";
                    String infocsvNewname = seriesOrStudyPath+File.separator+prefixName+SysConsts.UNDER_LINE+"info.csv";
                    String roicsvOldname = seriesOrStudyPath+File.separator+"ROI.csv";
                    String roicsvNewname = seriesOrStudyPath+File.separator+prefixName+SysConsts.UNDER_LINE+"ROI.csv";
                    new File(infocsvOldname).renameTo(new File(infocsvNewname));
                    new File(roicsvOldname).renameTo(new File(roicsvNewname));
                    seq++;
                }
            }
        }


        /**步骤五、然后拷贝到一个汇总的临时目录*/
        for(File file : tagFile.listFiles()){
            InfoSupplyerTool.copyDir(file.getAbsolutePath(),desensitizeDownloadTagCombinePath);
        }

        /**步骤五、然后将该临时目录打包发送给调用方*/
        String zipFilePath = null;
        if(success) {
            ZipUtil.zip(desensitizeDownloadTagCombinePath, desensitizeDownloadTemp, tag + ".zip");
            zipFilePath = desensitizeDownloadTemp + File.separator + tag + ".zip";
            if (new File(desensitizeDownloadTagPath).exists()) {
                InfoSupplyerTool.delFolder(desensitizeDownloadTagPath);
            }
            if(new File(desensitizeDownloadTagCombinePath).exists()){
                InfoSupplyerTool.delFolder(desensitizeDownloadTagCombinePath);
            }
        }

        logger.log(Level.INFO,"zip压缩文件路径:"+zipFilePath);
        logger.log(Level.INFO,"方法:downloadDesensitizeDicomByTag 流程结束:");

        return zipFilePath;
    }

    @Override
    public JSONObject exportDesensitizeDicomByTag(List<String> tags) throws Exception {
        JSONArray tagsJson = JSON.parseArray(JSON.toJSONString(tags));
        logger.log(Level.INFO,"方法:exportDesensitizeDicomByTag 被调用，参数:{tag="+tagsJson+"}");

        JSONObject result = new JSONObject();
        JSONObject tagData = new JSONObject();
        Integer countOfSuccess = 0;


        /**步骤二：查询es 索引dicomdisensitizationindex获得该tag下面所有SeriesInstanceUID,StudyInstanceUID，hdfs路径*/
        //1.构造查询条件
        for(String tag : tags){
            JSONArray tagArr = new JSONArray();

            JSONObject param = new JSONObject();
            JSONArray criteria = new JSONArray();
            JSONObject obj = new JSONObject();
            obj.put(SysConsts.SECTION,SysConsts.NO);
            obj.put(SysConsts.KEYWORD,EsConsts.TAG);
            obj.put(SysConsts.VALUE,tag);
            criteria.add(obj);
            param.put(SysConsts.CRITERIA,criteria);

            JSONArray backfields = new JSONArray();
            backfields.add(EsConsts.StudyInstanceUID_ES_DCM);
            backfields.add(EsConsts.SeriesInstanceUID_ES_DCM);
            backfields.add(EsConsts.HDFSPATH);
            param.put(SysConsts.CRITERIA, criteria);
            param.put(SysConsts.BACKFIELDS,backfields);
            param.put(SysConsts.DATATYPE,SysConsts.TYPE_MULTIDIMENSION);
            DataTypeEnum type = DataTypeEnum.MULTIDIMENSION;
            JSONObject tempResult = elasticSearchService.searchByPaging(param,type);
            if(tempResult == null || !SysConsts.CODE_000.equals(tempResult.getString(SysConsts.CODE))){
                continue;   //如果该tag失败则跳过继续处理下一个tag
            }else{
                JSONArray data = tempResult.getJSONArray(SysConsts.DATA);
                int size = data.size();
                if(size>0){
                    countOfSuccess++;
                    for(int i=0;i<size;i++){
                        JSONObject temp = new JSONObject();
                        JSONObject tempObj = data.getJSONObject(i);
                        String studyInstanceUID = tempObj.getString(EsConsts.StudyInstanceUID_ES_DCM);
                        String seriesInstanceUID = tempObj.getString(EsConsts.SeriesInstanceUID_ES_DCM);
                        String hdfspath = tempObj.getString(EsConsts.HDFSPATH);
                        if(!StringUtils.isBlank(studyInstanceUID) && StringUtils.isBlank(seriesInstanceUID)){
                            temp.put("organ",SysConsts.BREAST);
                            temp.put("InstanceId",studyInstanceUID);
                            temp.put("hdfspath",hdfspath);
                        }else if(StringUtils.isBlank(studyInstanceUID) && !StringUtils.isBlank(seriesInstanceUID)){
                            temp.put("organ",SysConsts.LUNG);
                            temp.put("InstanceId",seriesInstanceUID);
                            temp.put("hdfspath",hdfspath);
                        }
                        tagArr.add(temp);
                    }
                }
            }
            tagData.put(tag,tagArr);
        }
        result.put(SysConsts.CODE,SysConsts.CODE_000);
        result.put(SysConsts.TOTAL,countOfSuccess);
        result.put(SysConsts.DATA,tagData);

        logger.log(Level.INFO,"返回结果:"+result.toJSONString());
        logger.log(Level.INFO,"方法:downloadDesensitizeDicomByTag 流程结束:");
        return result;
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


    /***********************************************/
    @Override
    public Long desensitizedicom(String tag) {
        logger.log(Level.INFO,"方法:desensitizedicom 被调用，参数:{tag="+tag+"}");
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
        Map<String,String> hdfspath2Studyid = new HashMap<String,String>();
        if(success) {
            JSONObject param = new JSONObject();
            JSONArray criteria = new JSONArray();
            JSONObject obj = new JSONObject();
            obj.put(SysConsts.SECTION,SysConsts.NO);
            obj.put(SysConsts.KEYWORD,EsConsts.TAG);
            obj.put(SysConsts.VALUE,tag);
            criteria.add(obj);
            JSONArray backfields = new JSONArray();
            backfields.add(EsConsts.StudyInstanceUID_ES_DCM);
            backfields.add(EsConsts.SeriesUID);
            backfields.add(EsConsts.HDFSPATH);
            param.put(SysConsts.DATATYPE,SysConsts.TYPE_DICOM);
            param.put(SysConsts.CRITERIA, criteria);
            param.put(SysConsts.BACKFIELDS, backfields);
            DataTypeEnum type = DataTypeEnum.DICOM;
            JSONObject metaMsg = elasticSearchService.searchByPaging(param,type);
            if (SysConsts.CODE_000.equals(metaMsg.getString(SysConsts.CODE))) {
                total = metaMsg.getLong("total");
                JSONArray data = metaMsg.getJSONArray("data");
                for (int i = 0; i < total; i++) {
                    JSONObject jsonObject = data.getJSONObject(i);
                    String hdfspath = jsonObject.getString(EsConsts.HDFSPATH);
                    String studyid = jsonObject.getString(EsConsts.StudyInstanceUID_ES_DCM);
                    hdfspaths.add(hdfspath);
                    hdfspath2Studyid.put(hdfspath,studyid);
                }
            }
            if(hdfspaths.size() == 0)
                success = false;
        }

        logger.log(Level.INFO,"序列数量:"+hdfspaths.size());

        //创建本地临时目录
        if(success) {
            if(! new File(desensitizeBeforeTagPath).exists()){
                new File(desensitizeBeforeTagPath).mkdirs();
            }
            //下载dicom文件到本地，存放于临时目录
            if(!hdfsService.downloadDicom(hdfspaths, desensitizeBeforeTagPath))
                success = false;
        }
        logger.log(Level.INFO,"下载dicom到本地：success:"+success+","+desensitizeBeforeTagPath);

        //将上一步临时目录中的数据脱敏处理并存放于另一个临时目录
        if(success){
            //存放raw,mhd的目录必须被清空，否则会脱敏失败，因为py脚本中创建已经存在的目录会抛出异常
            InfoSupplyerTool.delAllFile(desensitizeAfterTagPath);
            String[] args = new String[4];
            args[0] = infosupplyerConfiguration.getPythoncmd();
            args[1] = infosupplyerConfiguration.getPythonscript();
            args[2] = desensitizeBeforeTagPath;
            args[3] = desensitizeAfterTagPath;
            int returnvalue = -2;
            try {
                Process p = Runtime.getRuntime().exec(args);
                try {
                    StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "Error");
                    StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "Output");
                    errorGobbler.start();
                    outputGobbler.start();
                    returnvalue = p.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                success = false;
                e.printStackTrace();
            }
//            if(returnvalue == 0 &&
//                    new File(desensitizeBeforeTagPath).listFiles().length
//                            ==new File(desensitizeAfterTagPath).listFiles().length) {
//                success = true;
//            }else {
//                success = false;
//            }
        }

        logger.log(Level.INFO,"生成raw，mhd数据完成，结果:"+success);

        //将同一个study的序列归到一起
        Map<String,List<String>> studies = new HashMap<String,List<String>>();
        for(Map.Entry<String,String> entry : hdfspath2Studyid.entrySet()){
            String hdfspath = entry.getKey();
            String studyid =  entry.getValue();
            if(studies.containsKey(studyid)){
                List<String> hdfspathList = studies.get(studyid);
                hdfspathList.add(hdfspath);
                studies.put(studyid,hdfspathList);
            }else{
                List<String> hdfspathList = new ArrayList<>();
                hdfspathList.add(hdfspath);
                studies.put(studyid,hdfspathList);
            }
        }

        //一个用于融合csv,raw,mhd的临时目录
        String combinePath = infosupplyerConfiguration.getDesensitizeCombineTempPath();
        InfoSupplyerTool.delAllFile(combinePath);

        for(Map.Entry<String,List<String>> entry : studies.entrySet()){
            String studyid = entry.getKey();
            List<String> hdfspathList = entry.getValue();

            String hdfspathTemp = hdfspathList.get(0);
            String seriesInstanceUIDTemp = hdfspathTemp.substring(hdfspathTemp.lastIndexOf(SysConsts.LEFT_SLASH)+1, hdfspathTemp.length()).replace("x",".");
            String series_modality = seriesDao.searchSingleFieldBySeriessop(seriesInstanceUIDTemp, SysConsts.SERIES_MODALITY);
            InfoSupplyerTool.delAllFile(combinePath);
            //根据series_uid,查series表中series_modality【设备类型】MG的暂时为乳腺，CT的暂时为肺，
            if(SysConsts.MG.equals(series_modality)){
                logger.log(Level.INFO,"进入乳腺");
                //1.2.840.113704.1.111.13500.1510890558.7
                List<String> seriesInstanceUIDList = new ArrayList<>();
                //1x2x840x113704x1x111x13500x1510890558x7
                List<String> nameList = new ArrayList<String>();
                //遍历一个study下面几个序列，提取他们的SeriesUID,SeriesInstanceUID,并把他们拷贝到combinepath目录中
                for(String hdfspath : hdfspathList){
                    String seriesUID = hdfspath.substring(hdfspath.lastIndexOf(SysConsts.LEFT_SLASH)+1, hdfspath.length());
                    String desentizedSeriesPath = desensitizeAfterTagPath+File.separator+seriesUID;
                    InfoSupplyerTool.copyDir(desentizedSeriesPath,combinePath);
                    nameList.add(seriesUID);
                    String seriesInstanceUID = seriesUID.replace("x",".");
                    seriesInstanceUIDList.add(seriesInstanceUID);
                }

                //乳腺
                try {
                    createCsvForBreast(seriesInstanceUIDList,combinePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //改名
                for(String oldname : nameList){
                    String seriessop = oldname.replace("x",".");
                    String series_des = seriesDao.searchSingleFieldBySeriessop(seriessop, SysConsts.SERIES_DES);
                    String newname = series_des.replace(" ","").trim();
                    //因为位置类型的可能有多个，所以文件名命名需要在末尾加个数据
                    int tempnumber = 1;
                    String tempName = newname;
                    while(new File(combinePath+File.separator+newname+".mhd").exists()){
                        newname = tempName+"-"+tempnumber;
                        tempnumber++;
                    }
                    rename(oldname, newname, combinePath);
                }

                File combinePathFile = new File(combinePath);

                List<String> idByField = elasticSearchService.getIdByField(DataTypeEnum.DICOM, EsConsts.SeriesInstanceUID_ES_DCM, seriesInstanceUIDList.get(0));
                String id = idByField.get(0);
                String studyInstanceUID = (String)elasticSearchService.getFieldById(infosupplyerConfiguration.getIndexDicom(),
                        infosupplyerConfiguration.getTypeDicom(), id, EsConsts.StudyInstanceUID_ES_DCM);
                //上传
                try {
                    uploadDicomDesensitization(combinePathFile,studyInstanceUID,tag,SysConsts.BREAST);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                InfoSupplyerTool.delAllFile(combinePath);
            }else if(SysConsts.CT.equals(series_modality)){
                //肺
                logger.log(Level.INFO,"进入肺");
                File combinePathFile = new File(combinePath);
                //1.2.840.113704.1.111.13500.1510890558.7
                //遍历一个study下面几个序列，提取他们的SeriesUID,SeriesInstanceUID,并把他们拷贝到combinepath目录中
                for(String hdfspath : hdfspathList){
                    String seriesUID = hdfspath.substring(hdfspath.lastIndexOf(SysConsts.LEFT_SLASH)+1, hdfspath.length());
                    String desentizedSeriesPath = desensitizeAfterTagPath+File.separator+seriesUID;
                    //这是因为有的dicom缺少字段在生成raw的时候出现异常，那么在aftertemp目录中就没有这个序列。
                    // 所以需要检查这个目录是否存在，对这种情况的解决办法就是跳过。
                    if(!(new File(desentizedSeriesPath).exists())){
                        continue;
                    }
                    InfoSupplyerTool.copyDir(desentizedSeriesPath,combinePath);
                    String seriesInstanceUID = seriesUID.replace("x",".");
                    try {
                        createCsvForLung(seriesInstanceUID,combinePath);
                        //肺不用改名，下载的时候改名
                        uploadDicomDesensitization(combinePathFile,seriesInstanceUID,tag,SysConsts.LUNG);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    InfoSupplyerTool.delAllFile(combinePath);
                }
            }
        }

        logger.log(Level.INFO,"生成csv文件完成，结果:"+success);

        //调用上传接口，上传存放脱敏数据的临时目录

        /*********删除临时目录*************************/
        InfoSupplyerTool.delFolder(desensitizeBeforeTagPath);
        InfoSupplyerTool.delFolder(desensitizeAfterTagPath);
        InfoSupplyerTool.delAllFile(combinePath);
        logger.log(Level.INFO,"删除临时目:desensitizeBeforeTagPath:"+desensitizeBeforeTagPath);
        logger.log(Level.INFO,"删除临时目:desensitizeBeforeTagPath:"+desensitizeAfterTagPath);
        logger.log(Level.INFO,"删除临时目:combinePath:"+combinePath);

        /**********修改tag状态为已脱敏***********************/
        if(success) {
            DicomTag dicomTag = new DicomTag();
            dicomTag.setTagname(tag);
            dicomTag.setDesensitize(1);
            dicomTagDao.updateDesensitize(dicomTag);
            return total;
        }
        logger.log(Level.INFO,"脱敏结束");
        return 0L;
    }

    /**
     * 为乳腺类型raw,mhd创建csv文件
     * @param seriesInstanceUIDs  该study下面的所有序列实例号
     * @param path  存放raw,mhd的临时目录，待会生成的csv文件也要放在这个里面
     */
    private void createCsvForBreast(List<String> seriesInstanceUIDs,String path) throws IOException {
        JSONArray paramJson = JSONArray.parseArray(JSON.toJSONString(seriesInstanceUIDs));
        logger.log(Level.INFO,"createCsvForBreast，参数:"+paramJson.toJSONString()+"path:"+path);

        //步骤一、逐个处理每个序列，在series表中根据series_sop=SeriesInstanceUID,查询到数据库中通用的series_uid.
        List<String> seriesuidList = new ArrayList<>();
        for(String seriesInstanceUID : seriesInstanceUIDs){
            String seriesuid = seriesDao.searchSingleFieldBySeriessop(seriesInstanceUID, SysConsts.SERIES_UID);
            seriesuidList.add(seriesuid);
        }

        //步骤二、逐个处理每个序列，根据series_uid到breast_roi表中查询病灶的定性信息及位置信息，存储到List中，一行代表一个病灶
        List<BreastRoiEntity> breastRoiEntityList = new ArrayList<>();
        for(String series_uid : seriesuidList){
            List<BreastRoiEntity> entityBySeriesuid = breastRoiDao.getEntityBySeriesuid(series_uid);
            breastRoiEntityList.addAll(entityBySeriesuid);
        }


        //步骤三、遍历存放病灶的List,逐个写入Info.csv.
        //序号(1-N)【自然数递增】，位置信息【location】，乳腺分型(1-4)【classification】，形状特征(1-3)【shape】，
        // 边界特征(1,2)【boundary1】，边界特征(1-3)【boundary2】，密度特征(1-4)【density】，
        // 象限定位(1-6)【quadrant】，恶性风险评估(1-2)【risk】
        String roiinfoName = "info";
        String roiinfoFilePath = path+File.separator+roiinfoName+".csv";
        HSSFWorkbook roiinfoworkbook = new HSSFWorkbook();
        HSSFSheet roiinfosheet = roiinfoworkbook.createSheet(roiinfoName);
        int count = 1;
        for(BreastRoiEntity entity : breastRoiEntityList){
            HSSFRow roiinforow = roiinfosheet.createRow(count-1);
            roiinforow.createCell(0).setCellValue(count);
            roiinforow.createCell(1).setCellValue(entity.getLocation());
            roiinforow.createCell(2).setCellValue(entity.getClassification());
            roiinforow.createCell(3).setCellValue(entity.getShape());
            roiinforow.createCell(4).setCellValue(entity.getBoundary1()); //这里采用名称为studydate,实际是seriesDate
            roiinforow.createCell(5).setCellValue(entity.getBoundary2());
            roiinforow.createCell(6).setCellValue(entity.getDensity());
            roiinforow.createCell(7).setCellValue(entity.getQuadrant());
            roiinforow.createCell(8).setCellValue(entity.getRisk());
            count ++;
        }
        FileOutputStream roiinfofout = new FileOutputStream(new File(roiinfoFilePath));
        roiinfoworkbook.write(roiinfofout);
        roiinfoworkbook.close();
        roiinfofout.close();

        //步骤四、遍历存放病灶的List,逐个写入ROI.csv.
        String roiName = "ROI";
        String roiFilePath = path+File.separator+roiName+".csv";
        HSSFWorkbook roiworkbook = new HSSFWorkbook();
        HSSFSheet roisheet = roiworkbook.createSheet(roiName);
        count = 1;
        for(BreastRoiEntity entity : breastRoiEntityList){
            HSSFRow roirow = roisheet.createRow(count-1);
            roirow.createCell(0).setCellValue(count);

            String restore_data = entity.getRestore_data();
            JSONObject jsonObject = JSON.parseObject(restore_data);
            JSONArray handles = jsonObject.getJSONArray("handles");
            int size = handles.size();
            for (int i = 0; i < size; i++) {
                JSONObject obj = handles.getJSONObject(i);
                Double x = obj.getDouble("x");
                Double y = obj.getDouble("y");
                roirow.createCell(2 * i + 1).setCellValue(x);
                roirow.createCell(2 * i + 2).setCellValue(y);
            }
            count++;
        }
        FileOutputStream roifout = new FileOutputStream(new File(roiFilePath));
        roiworkbook.write(roifout);
        roiworkbook.close();
        roifout.close();
    }

    /**
     * 肺结节的是以序列为单位，
     * 1.到series表中根据series_sop查到series_uid.
     * 2.到roi3d表中根据roi3d_series_uid【即上一步series_uid】查询到各种要求的定性信息，以及roi3d_num.
     *      1.将查询到的定性信息写入info.csv.
     * 3.以roi3d_num为单位逐个处理，一个代表3维空间中一个结节，涉及到多个层。
     *      1.根据roi3d_num，series_uid，到roi2d表查询到一个病灶在多层图片上的位置。逐条写入csv.
     * @param seriesInstanceUID dicom文件中的序列号
     * @param path
     * @throws IOException
     */
    private void createCsvForLung(String seriesInstanceUID,String path) throws IOException {
        logger.log(Level.INFO,"createCsvForBreast，参数:"+seriesInstanceUID+"path:"+path);

        int seq = 0;

        //步骤一、查询到数据库中的series_uid,这个不是原生的SeriesInstanceUID,作用相同，数据库中通用。
        String series_uid = seriesDao.searchSingleFieldBySeriessop(seriesInstanceUID,SysConsts.SERIES_UID);

        //步骤二、根据series_uid=roi3d_series_uid查询定性信息，即有几个病灶。
        //查询roi3d得到info.csv，可能有多行。一行代表一个结节。
        //roi3dEntityList = 数据库查询出来，按照roi3d_num自然排序
        List<Roi3dEntity> roi3dEntityList = roi3dDao.getEntityBySeriesuid(series_uid);


        //步骤三、提取如下字段一次写入info.csv
        //序号(1-N)【roi3d_num】，结节分析(1-4)【roi3d_nodule_analysis】，分叶(1,2)【roi3d_signs】，
        // 毛刺(1,2)【roi3d_signs】，胸膜牵拉(1,2)【roi3d_signs】，含气细支气管征或小泡征(1,2)【roi3d_signs】，
        // 空洞征(1,2)【roi3d_signs】，
        // 结节位置(1-3)【roi3d_nodal_position】，随访时间(1-3)【roi3d_follow_up】，恶性风险评估(1-4)【roi3d_risk_assessment】
        String roiinfoName = "info";
        String roiinfoFilePath = path+File.separator+roiinfoName+".csv";
        HSSFWorkbook roiinfoworkbook = new HSSFWorkbook();
        HSSFSheet roiinfosheet = roiinfoworkbook.createSheet(roiinfoName);
        int count = 1;
        for(Roi3dEntity entity : roi3dEntityList){
            HSSFRow roiinforow = roiinfosheet.createRow(count-1);
            roiinforow.createCell(0).setCellValue(entity.getRoi3d_num());
            roiinforow.createCell(1).setCellValue(entity.getRoi3d_nodule_analysis());
            roiinforow.createCell(2).setCellValue(entity.getRoi3d_signs());
            roiinforow.createCell(3).setCellValue(entity.getRoi3d_signs());
            roiinforow.createCell(4).setCellValue(entity.getRoi3d_signs()); //这里采用名称为studydate,实际是seriesDate
            roiinforow.createCell(5).setCellValue(entity.getRoi3d_signs());
            roiinforow.createCell(6).setCellValue(entity.getRoi3d_signs());
            roiinforow.createCell(7).setCellValue(entity.getRoi3d_nodal_position());
            roiinforow.createCell(8).setCellValue(entity.getRoi3d_follow_up());
            roiinforow.createCell(9).setCellValue(entity.getRoi3d_risk_assessment());
            count ++;
        }
        FileOutputStream roiinfofout = new FileOutputStream(new File(roiinfoFilePath));
        roiinfoworkbook.write(roiinfofout);
        roiinfoworkbook.close();
        roiinfofout.close();


        //步骤四、根据series_uid=roi2d_series_uid查询ROI位置信息，一个病灶【roi3d_num】有多个位置【slice】。
        List<Roi2dEntity> roi2dEntityList = rio2dDao.getEntityBySeriesuid(series_uid);

        //步骤五、位置信息写入ROI.csv
        //序号(1-N)【roi3d_num】、层号(1-N)【roi2d_slice】，类型【roi2d_dim】	X1	Y1	X2	Y2	… …  	Xn	Yn
        //根据roi3d_num排序，一次写入ROI.csv
        String roiName = "ROI";
        String roiFilePath = path+File.separator+roiName+".csv";
        HSSFWorkbook roiworkbook = new HSSFWorkbook();
        HSSFSheet roisheet = roiworkbook.createSheet(roiName);
        count = 1;
        for(Roi2dEntity entity : roi2dEntityList){
            HSSFRow roirow = roisheet.createRow(count-1);
            roirow.createCell(0).setCellValue(count);

            String roi2d_points = entity.getRoi2d_points();
            JSONArray jsonArray = JSON.parseArray(roi2d_points);
            int size = jsonArray.size();
            for (int i = 0; i < size; i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                Double x = obj.getDouble("x");
                Double y = obj.getDouble("y");
                roirow.createCell(2 * i + 1).setCellValue(x);
                roirow.createCell(2 * i + 2).setCellValue(y);
            }
        }
        FileOutputStream roifout = new FileOutputStream(new File(roiFilePath));
        roiworkbook.write(roifout);
        roiworkbook.close();
        roifout.close();
    }


    /**
     *
     * @param oldname raw,mhd这样的数据对名字
     * @param newname 新的名字
     * @param path  这个目录下面存放着raw,mhd这样的数据
     * @return
     */
    private boolean rename(String oldname,String newname,String path){
        if(StringUtils.isBlank(oldname) || StringUtils.isBlank(newname) || StringUtils.isBlank(path)){
            return false;
        }

        //读取mhd文件，修改其中的ElementDataFile属性，这个属性是raw文件名。
        String mhdFilePath = path+File.separator+oldname+".mhd";
        String rawFilePath = path+File.separator+oldname+".raw";
        BufferedReader br = null;
        LineNumberReader lnr = null;
        try {
            br = new BufferedReader(new FileReader(mhdFilePath));
            lnr = new LineNumberReader(new FileReader(mhdFilePath));
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

            mhdtemp[positionline] = "ElementDataFile = "+ newname+".raw";
            FileWriter fw = new FileWriter(mhdFilePath);
            for(String line : mhdtemp){
                fw.write(line);
                fw.write("\n");
            }
            fw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //修改raw文件名
        new File(rawFilePath).renameTo(new File(path+File.separator+newname+".raw"));
        new File(mhdFilePath).renameTo(new File(path+File.separator+newname+".mhd"));

        return true;
    }

}


/*private void  createDesensitizeDataOnLocalForBreast(String tag,Map<String,List<JSONObject>> studies,String tagtemp) throws IOException {
        int seq = 0;
        for(Map.Entry<String,List<JSONObject>> entry : studies.entrySet()){
            seq++;
            //1.得到一个study下所有SeriesInstanceUID,存入list
            List<String> seriesList = new ArrayList<>();
            List<JSONObject> value = entry.getValue();
            for(JSONObject jsonObject : value){
                seriesList.add(jsonObject.getString(EsConsts.SeriesInstanceUID_ES_DCM));
            }
            //2.访问数据库，查询info.csv需要的数据
            List<String> seriesuids = new ArrayList<String>();
            for(String seriessop : seriesList){
                String seriesuid = seriesDao.searchSingleFieldBySeriessop(seriessop,SysConsts.SERIES_UID);
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
                String seriessop = jsonObject.getString(EsConsts.SeriesInstanceUID_ES_DCM);
                String seriesUID = jsonObject.getString(EsConsts.SeriesUID);
                String series_des = seriesDao.searchSingleFieldBySeriessop(seriessop,SysConsts.SERIES_DES);
                series_des = series_des.replace(SysConsts.SPACE,"").trim();

                String name = tag+"_"+InfoSupplyerTool.formatDigitalToNBit(seq+"",6)+"_"+series_des;
                //因为LMLO类型的可能有多个，所以文件名命名需要在末尾加个数据
                int tempnumber = 1;
                while(new File(tagtemp+File.separator+name+".mhd").exists()){
                    name += "-"+tempnumber;
                }

                String hdfspath = (String)elasticSearchService.getFieldById(infosupplyerConfiguration.getIndexDicomDisensitization(),
                        infosupplyerConfiguration.getTypeDicomDisensitization(),seriesUID,EsConsts.HDFSPATH);
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
    }*/

   /* private void createDesensitizeDataOnLocalForLung(String tag,Map<String,List<JSONObject>> studies,String tagtemp) throws IOException {
        int seq = 0;

        for (Map.Entry<String, List<JSONObject>> entry : studies.entrySet()) {
            seq++;
            List<String> hdfs = new ArrayList<>();
            List<JSONObject> values = entry.getValue();
            for (JSONObject value : values) {
                String seriesUID = value.getString(EsConsts.SeriesUID);
                String hdfspath = (String) elasticSearchService.getFieldById(infosupplyerConfiguration.getIndexDicomDisensitization(),
                        infosupplyerConfiguration.getTypeDicomDisensitization(), seriesUID, EsConsts.HDFSPATH);
                hdfs.add(hdfspath);
            }
            *//**步骤四：从hdfs下载.mhd,.raw数据到本地临时目录*//*
            for (String hdfspath : hdfs) {
                String name = tag + "_" + InfoSupplyerTool.formatDigitalToNBit(seq + "", 6);
                String[] desensitizedFileLocal = hdfsService.downDicomDesensitization(tagtemp, hdfspath, hdfsConf);
                //读取mhd文件，修改其中的ElementDataFile属性，这个属性是raw文件名。
                String mhdFilePath = desensitizedFileLocal[0];
                String rawFilePath = desensitizedFileLocal[1];
                BufferedReader br = new BufferedReader(new FileReader(mhdFilePath));
                LineNumberReader lnr = new LineNumberReader(new FileReader(mhdFilePath));
                lnr.skip(new File(mhdFilePath).length());
                String[] mhdtemp = new String[lnr.getLineNumber()];
                String temp = null;
                int number = 0;
                int positionline = -1;
                while ((temp = br.readLine()) != null) {
                    if (temp.startsWith("ElementDataFile"))
                        positionline = number;
                    mhdtemp[number++] = temp;
                }
                br.close();
                lnr.close();
                mhdtemp[positionline] = "ElementDataFile = " + name + ".raw";
                FileWriter fw = new FileWriter(mhdFilePath);
                for (String line : mhdtemp) {
                    fw.write(line);
                    fw.write("\n");
                }
                fw.close();

                //修改raw文件名
                new File(rawFilePath).renameTo(new File(tagtemp + File.separator + name + ".raw"));
                new File(mhdFilePath).renameTo(new File(tagtemp + File.separator + name + ".mhd"));
            }
        }
    }*/

/*@Override
 public Long desensitizedicom(String tag) {
        logger.log(Level.INFO,"方法:desensitizedicom 被调用，参数:{tag="+tag+"}");
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

        *//**********根据tag字段查询属于这批tag的所有dicom序列的唯一id,以及hdfs路径******//*
        List<String> hdfspaths = new ArrayList<>();
        if(success) {
            JSONObject param = new JSONObject();
            JSONArray criteria = new JSONArray();
            JSONObject obj = new JSONObject();
            obj.put(SysConsts.SECTION,SysConsts.NO);
            obj.put(SysConsts.KEYWORD,EsConsts.TAG);
            obj.put(SysConsts.VALUE,tag);
            criteria.add(obj);
            JSONArray backfields = new JSONArray();
            backfields.add(EsConsts.SeriesUID);
            backfields.add(EsConsts.HDFSPATH);
            param.put(SysConsts.DATATYPE,SysConsts.TYPE_DICOM);
            param.put(SysConsts.CRITERIA, criteria);
            param.put(SysConsts.BACKFIELDS, backfields);
            DataTypeEnum type = DataTypeEnum.DICOM;
            JSONObject metaMsg = elasticSearchService.searchByPaging(param,type);
            if (SysConsts.CODE_000.equals(metaMsg.getString(SysConsts.CODE))) {
                total = metaMsg.getLong("total");
                JSONArray data = metaMsg.getJSONArray("data");
                for (int i = 0; i < total; i++) {
                    JSONObject jsonObject = data.getJSONObject(i);
                    hdfspaths.add(jsonObject.getString(EsConsts.HDFSPATH));
                }
            }
            if(hdfspaths.size() == 0)
                success = false;
        }

        logger.log(Level.INFO,"序列数量:"+hdfspaths.size());

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

        logger.log(Level.INFO,"生成raw，mhd数据完成，结果:"+success);


        //调用上传接口，上传存放脱敏数据的临时目录
        if(success){
            try {
                success = SysConsts.SUCCESS == uploadDicomDesensitization(desensitizeAfterTagPath,tag);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        *//*********删除临时目录*************************//*
        InfoSupplyerTool.delFolder(desensitizeBeforeTagPath);
        InfoSupplyerTool.delFolder(desensitizeAfterTagPath);
        logger.log(Level.INFO,"删除临时目:desensitizeBeforeTagPath:"+desensitizeBeforeTagPath);
        logger.log(Level.INFO,"删除临时目:desensitizeBeforeTagPath:"+desensitizeAfterTagPath);

        *//**********修改tag状态为已脱敏***********************//*
        if(success) {
            DicomTag dicomTag = new DicomTag();
            dicomTag.setTagname(tag);
            dicomTag.setDesensitize(1);
            dicomTagDao.updateDesensitize(dicomTag);
        }
        logger.log(Level.INFO,"脱敏结束");
        return total;
    }*/

/*    @Override
    public int uploadDicomDesensitization(String desensitizationDir,String tag) throws IOException {
        logger.log(Level.INFO,"方法:uploadDicomDesensitization 被调用，参数:{desensitizationDir="+desensitizationDir+",tag="+tag+"}");
        if(!validateDir(desensitizationDir))
            return 0;
        List<File> seriesDirs = listDir(desensitizationDir);
        for(File seriesDir : seriesDirs){
            //逐个上传
            if(SysConsts.FAILED == uploadDicomDesensitization(seriesDir,tag)){
                return SysConsts.FAILED;
            }
        }
        return 0;
    }*/