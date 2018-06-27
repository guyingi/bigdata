package qed.bigdata.es.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import qed.bigdata.es.consts.ESConsts;
import qed.bigdata.es.consts.SysConsts;
import qed.bigdata.es.service.DataDownloadService;
import qed.bigdata.es.service.SearchService;
import qed.bigdata.es.conf.MilkConfiguration;
import qed.bigdata.es.consts.DataTypeEnum;
import qed.bigdata.es.tool.HdfsTool;
import qed.bigdata.es.tool.Tool;
import qed.bigdata.es.tool.ZipUtil;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataDownloadServiceImpl implements DataDownloadService {
    static Logger logger = Logger.getLogger(DataDownloadServiceImpl.class);

    @Autowired
    SearchService searchService;


    /**
     *
     * @param id
     * @param tempRealDir   都是temp临时目录，这是绝对路径，末尾不带分隔符
     * @param tempContextPath  都是temp临时目录，这是相对路径，末尾不带分隔符
     * @return
     * @throws Exception
     */
    @Override
    public List<String> downloadDicomThumbnail(String id,String tempRealDir,String tempContextPath) throws Exception {
        logger.log(Level.INFO,"调用方法:downloadDicomThumbnail,参数{id:"+id
        +"tempRealDir:"+tempRealDir
        +"tempContextPath:"+tempContextPath);

        //存放图片在web工程下的相对路径，前端根据此路径直接获取图片并显示
        List<String> result = new ArrayList<String>();

        //infosupplyer提供的下载接口
        String interfaceStr = "/data/downloadDicomNail";
        //本地临时压缩文件名称，6位随机数
        String zipTempName = Tool.getRandonNumber(6)+"";
        //zipFilePath是zip文件全路径
        String zipFilePath = tempRealDir+Tool.getDelimiter()+zipTempName+".zip";
        //调用下载方法，做具体下载工作
        doCallAndWriteToDisk(id,interfaceStr,zipFilePath);
        //做解压操作，zipFilePath是zip文件全路径，tempRealDir是存放解压后的文件全路径，
        // true意为将解压后的文件存放在以压缩文件名命名的目录下面
        ZipUtil.unzip(zipFilePath,tempRealDir,true);
        //存放jpg图片的目录
        String unzipDir = tempRealDir+Tool.getDelimiter()+zipTempName;
        File file = new File(unzipDir);
        for(File e : file.listFiles()){
            //生成缩略图相对路径，存入结果list
            result.add(tempContextPath+SysConsts.LEFT_SLASH+zipTempName+SysConsts.LEFT_SLASH+e.getName());
        }
        logger.log(Level.INFO,"调用 downloadDicomThumbnail 结束，结果:"+result.size());
        return result;
    }

    @Override
    public String downloadDesensitizeDdicomByTag(String tag,String tempRealDir) {
        logger.log(Level.INFO,"调用方法:downloadDesensitizeDdicomByTag,参数{tag:"+tag
                +"tempRealDir:"+tempRealDir);

        String zipFilePath = tempRealDir+File.separator+tag+".zip";
        //infosupplyer提供的下载接口
        String interfaceStr = "/data/downloadDesensitizeDicomByTag";
        //调用下载方法，做具体下载工作
        doCallAndWriteToDisk(tag,interfaceStr,zipFilePath);

        logger.log(Level.INFO,"调用 downloadDicomThumbnail 结束，结果:"+zipFilePath);
        return zipFilePath;
    }

    public static boolean doCallAndWriteToDisk(String id,String interfaceStr,String filepath){
        logger.log(Level.INFO,"调用方法:doCallAndWriteToDisk,参数{id:"+id
                +"interfaceStr:"+interfaceStr
                +"filepath:"+filepath);

        MilkConfiguration conf = new MilkConfiguration();
        boolean isSuccess = false;
        try {
            String url = SysConsts.HTTP_HEAD+conf.getInfosupplyerip()+":"+conf.getInfosupplyerport()+interfaceStr+"?id="+id;
            System.out.println("url:"+url);
            URL restServiceURL = new URL(url);

            HttpURLConnection httpConnection = (HttpURLConnection) restServiceURL.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("Accept", "application/octet-stream");
            httpConnection.setRequestProperty("Content-Type", "text/html; charset=UTF-8");
            //httpConnection.setRequestProperty("Connection", "Keep-Alive");
            httpConnection.setRequestProperty("Charset", "UTF-8");
//            httpConnection.setDoOutput(true);
            httpConnection.setDoInput(true);
            //传递参数
            System.out.println("返回码："+httpConnection.getResponseCode());
            if (httpConnection.getResponseCode() == 200) {
                InputStream inputStream = httpConnection.getInputStream();
                File file = new File(filepath);
                if(!file.exists()) {
                    file.createNewFile();
                    file.setWritable(true, false);
                }
                FileOutputStream fout = new FileOutputStream(file);
                byte []tmp = new byte[1024];
                int n = -1;
                while((n=inputStream.read(tmp))!=-1) {
                    fout.write(tmp, 0, n);
                }
                fout.close();
                inputStream.close();
                isSuccess = true;
            } else {
                System.out.println("从infosupplyer下载文件失败");
                isSuccess = false;
            }
            httpConnection.disconnect();
        }catch (IOException e){
            e.printStackTrace();
        }
        logger.log(Level.INFO,"调用 doCallAndWriteToDisk 结束,结果:"+isSuccess);
        return isSuccess;
    }


    public String downloadMutilTypeDataForPatient(String patientname,List<String> datatypes,String tempPath) throws Exception {
        JSONArray datatypesJson = JSON.parseArray(JSON.toJSONString(datatypes));
        logger.log(Level.INFO,"调用方法:downloadMutilTypeDataForPatient,参数{patientname:"+patientname
                +"datatypes:"+datatypesJson
                +"tempPath:"+tempPath);
        //downloadPath中存放下载下来的各种类型数据的zip文件
        String downloadPath = tempPath+File.separator+patientname;

        /********依次下载每种类型的数据，下载完打包，给出到前端***********/
        for(String type : datatypes){
            if(SysConsts.TYPE_DICOM.equals(type)){
                //下载该患者所有的dicom文件到本地的一个以该患者名字命名的文件夹里面。从milk直接访问hdfs
                List<String> paths = new ArrayList<>();
                JSONArray criteria = new JSONArray();
                JSONObject item = new JSONObject();
                item.put(SysConsts.KEYWORD,ESConsts.PatientName_ES);
                item.put(SysConsts.SECTION,SysConsts.NO);
                item.put(SysConsts.VALUE,patientname);
                criteria.add(item);
                JSONArray backfields = new JSONArray();
                backfields.add(ESConsts.HDFSPATH);
                JSONObject jsonObject = searchService.searchDicomByPaging(criteria, backfields, null, 0, 0);
                if(SysConsts.CODE_000.equals(jsonObject.getString(SysConsts.CODE))) {
                    JSONArray data = jsonObject.getJSONArray(SysConsts.DATA);
                    for (int i = 0; i < data.size(); i++) {
                        String hdfspath = data.getJSONObject(i).getString(ESConsts.HDFSPATH);
                        paths.add(hdfspath);
                    }
                }

                //temp/小明/dicom
                String dicomPath = downloadPath+File.separator+"dicom";
                HdfsTool.downloadDicom(paths,dicomPath);

            }
            if(SysConsts.TYPE_ELECTRIC.equals(type)){
                //下载该患者所有电信号数据到本地的一个以该患者名i在命名的文件夹里面。
                //从milk直接访问hdfs，符合设计思想
                List<String> paths = new ArrayList<>();

                JSONArray criteriaElectric = new JSONArray();
                JSONObject itemElectri = new JSONObject();
                itemElectri.put(SysConsts.SECTION,SysConsts.NO);
                itemElectri.put(SysConsts.KEYWORD,ESConsts.PatientName_ES);
                itemElectri.put(SysConsts.VALUE,patientname);
                criteriaElectric.add(itemElectri);

                JSONArray backfields = new JSONArray();
                backfields.add(ESConsts.HDFSPATH_ES_ELECTRIC);

                JSONObject jsonObject = searchService.searchElectricByPaging(criteriaElectric, backfields, null, 0, 0);
                if(SysConsts.CODE_000.equals(jsonObject.getString(SysConsts.CODE))) {
                    JSONArray data = jsonObject.getJSONArray(SysConsts.DATA);
                    for (int i = 0; i < data.size(); i++) {
                        String hdfspath = data.getJSONObject(i).getString(ESConsts.HDFSPATH_ES_ELECTRIC);
                        paths.add(hdfspath);
                    }
                }
                //temp/小明/electric
                String electricPath = downloadPath+File.separator+"electric";
                HdfsTool.downloadElectric(paths,electricPath);
                //删除crc校验文件
                for(File file : new File(electricPath).listFiles()){
                    if(file.getName().endsWith("crc"))
                        file.delete();
                }
            }
            if(SysConsts.TYPE_KFB.equals(type)){

            }
            if(SysConsts.TYPE_GUAGE.equals(type)){

            }
        }

        //将以患者命名的本地临时文件压缩。
        String zipFileName = patientname+"_"+Tool.getRandonNumber(3)+".zip";
        ZipUtil.zip(downloadPath,tempPath,zipFileName);
        String zipFilePath = tempPath+File.separator+zipFileName;

        //删除临时目录，删除压缩文件
        Tool.delFolder(downloadPath);

        logger.log(Level.INFO,"调用 downloadMutilTypeDataForPatient 结束,zipFilePath:"+zipFilePath);

        return zipFilePath;
    }

    /**
     *
     * @param idList
     * @param tempDir  将下载的edf文件放到tempDir就可以了，不需要压缩，上层调用方法压缩
     * @return
     */
    @Override
    public String downloadElectricByIds(List<String> idList,String tempDir) throws Exception {
        JSONArray idListJson = JSON.parseArray(JSON.toJSONString(idList));
        logger.log(Level.INFO,"调用方法:downloadElectricByIds,参数{idList:"+idListJson
                +"tempDir:"+tempDir);

        if(idList==null || idList.size()==0){
            return null;
        }
        if(tempDir.endsWith(SysConsts.LEFT_SLASH)){
            tempDir = tempDir.substring(0,tempDir.length()-1);
        }

        //tempDir结尾自带斜线,
        JSONArray backfields = new JSONArray();
        backfields.add(ESConsts.HDFSPATH);
        JSONArray ids = new JSONArray();
        for(String e : idList){
            ids.add(e);
        }
        JSONObject json = new JSONObject();
        json.put(SysConsts.IDS,ids);
        json.put(SysConsts.DATATYPE,SysConsts.TYPE_ELECTRIC);
        json.put(SysConsts.BACKFIELDS,backfields);
        String interfaceStr = "/info/searchByIds";
        DataTypeEnum dataTypeEnum = DataTypeEnum.OTHER;
        JSONObject result = null;
        try {
            result = Tool.doCallAndGetResult(json, interfaceStr,dataTypeEnum);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        System.out.println("查询的结果："+result.toJSONString());

        List<String> hdfspaths = new ArrayList<String>();
        if(SysConsts.CODE_000.equals(result.getString(SysConsts.CODE))){
            JSONArray data = result.getJSONArray(SysConsts.DATA);
            int size = data.size();
            for(int i=0;i<size;i++){
                JSONObject jsonObject = data.getJSONObject(i);
                hdfspaths.add(jsonObject.getString(ESConsts.HDFSPATH));
            }
        }
        boolean isSuccess = false;


        if(hdfspaths.size()!=0)
            isSuccess = HdfsTool.downloadElectric(hdfspaths, tempDir);
        //删除crc校验文件
        for(File file : new File(tempDir).listFiles()){
            if(file.getName().endsWith("crc"))
                file.delete();
        }
        logger.log(Level.INFO,"调用 downloadElectricByIds 结束,tempDir:"+tempDir);
        return tempDir;
    }

    @Override
    public String downloadDicomByIds(List<String> list, String tempDir) throws Exception {
        JSONArray idListJson = JSON.parseArray(JSON.toJSONString(list));
        logger.log(Level.INFO,"调用方法:downloadDicomByIds,参数{idList:"+idListJson
                +"tempDir:"+tempDir);

        if(list==null || list.size()==0){
            return null;
        }
        if(tempDir.endsWith(SysConsts.LEFT_SLASH)){
            tempDir = tempDir.substring(0,tempDir.length()-1);
        }

        JSONArray backfields = new JSONArray();
        backfields.add(ESConsts.HDFSPATH);
        JSONArray ids = new JSONArray();
        for(String e : list){
            ids.add(e);
        }
        JSONObject json = new JSONObject();
        json.put(SysConsts.IDS,ids);
        json.put(SysConsts.DATATYPE,SysConsts.TYPE_DICOM);
        json.put(SysConsts.BACKFIELDS,backfields);
        String interfaceStr = "/info/searchByIds";
        DataTypeEnum dataTypeEnum = DataTypeEnum.DICOM;
        JSONObject result = null;
        try {
            result = Tool.doCallAndGetResult(json, interfaceStr,dataTypeEnum);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        System.out.println("查询的结果："+result.toJSONString());

        List<String> hdfspaths = new ArrayList<String>();
        String code = result.getString(SysConsts.CODE);
        if(code!=null && code.equals(SysConsts.CODE_000)){
            JSONArray data = result.getJSONArray(SysConsts.DATA);
            int size = data.size();
            for(int i=0;i<size;i++){
                JSONObject jsonObject = data.getJSONObject(i);
                hdfspaths.add(jsonObject.getString(ESConsts.HDFSPATH));
            }
        }
        boolean isSuccess = false;
        if(hdfspaths.size()!=0)
            isSuccess = HdfsTool.downloadDicom(hdfspaths, tempDir);

        logger.log(Level.INFO,"调用 downloadElectricByIds 结束,tempDir:"+tempDir);

        return tempDir;
    }


    public static void main(String[] args) {
        String zipFilePath = "F:\\实验室\\6"+File.separator+"LUN"+".zip";
        //infosupplyer提供的下载接口
        String interfaceStr = "/data/downloadDesensitizeDicomByTag";
        //调用下载方法，做具体下载工作
        doCallAndWriteToDisk("LUN",interfaceStr,zipFilePath);
    }

}
