package qed.bigdata.es.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import qed.bigdata.es.consts.ESConstants;
import qed.bigdata.es.service.DataDownloadService;
import qed.bigdata.es.service.SearchService;
import qed.bigdata.es.conf.MilkConfiguration;
import qed.bigdata.es.consts.DataTypeEnum;
import qed.bigdata.es.consts.SysConstants;
import qed.bigdata.es.tool.HdfsTool;
import qed.bigdata.es.tool.MilkTool;
import qed.bigdata.es.tool.ZipUtil;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataDownloadServiceImpl implements DataDownloadService {

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
        //存放图片在web工程下的相对路径，前端根据此路径直接获取图片并显示
        List<String> result = new ArrayList<String>();

        //infosupplyer提供的下载接口
        String interfaceStr = "/data/downloadDicomNail";
        //本地临时压缩文件名称，6位随机数
        String zipTempName = MilkTool.getRandonNumber(6)+"";
        //zipFilePath是zip文件全路径
        String zipFilePath = tempRealDir+MilkTool.getDelimiter()+zipTempName+".zip";
        //调用下载方法，做具体下载工作
        doCallAndWriteToDisk(id,interfaceStr,zipFilePath);
        //做解压操作，zipFilePath是zip文件全路径，tempRealDir是存放解压后的文件全路径，
        // true意为将解压后的文件存放在以压缩文件名命名的目录下面
        ZipUtil.unzip(zipFilePath,tempRealDir,true);
        //存放jpg图片的目录
        String unzipDir = tempRealDir+MilkTool.getDelimiter()+zipTempName;
        File file = new File(unzipDir);
        for(File e : file.listFiles()){
            //生成缩略图相对路径，存入结果list
            result.add(tempContextPath+SysConstants.LEFT_SLASH+zipTempName+SysConstants.LEFT_SLASH+e.getName());
        }
        return result;
    }

    @Override
    public String downloadDesensitizeDdicomByTag(String tag,String tempRealDir) {
        String zipFilePath = tempRealDir+File.separator+tag+".zip";
        //infosupplyer提供的下载接口
        String interfaceStr = "/data/downloadDesensitizeDicomByTag";
        //调用下载方法，做具体下载工作
        doCallAndWriteToDisk(tag,interfaceStr,zipFilePath);

        return zipFilePath;
    }

    public static boolean doCallAndWriteToDisk(String id,String interfaceStr,String filepath){
        MilkConfiguration conf = new MilkConfiguration();
        boolean isSuccess = false;
        try {
            String url = SysConstants.HTTP_HEAD+conf.getInfosupplyerip()+":"+conf.getInfosupplyerport()+interfaceStr+"?id="+id;
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
        return isSuccess;
    }


    public String downloadMutilTypeDataForPatient(String patientname,List<String> datatypes,String tempPath) throws Exception {
        //downloadPath中存放下载下来的各种类型数据的zip文件
        String downloadPath = tempPath+File.separator+patientname;

        /********依次下载每种类型的数据，下载完打包，给出到前端***********/
        for(String type : datatypes){
            if(SysConstants.TYPE_DICOM.equals(type)){
                //下载该患者所有的dicom文件到本地的一个以该患者名字命名的文件夹里面。从milk直接访问hdfs
                List<String> paths = new ArrayList<>();
                JSONArray criteria = new JSONArray();
                JSONObject item = new JSONObject();
                item.put(SysConstants.KEYWORD,ESConstants.PatientName_ES);
                item.put(SysConstants.SECTION,SysConstants.NO);
                item.put(SysConstants.VALUE,patientname);
                criteria.add(item);
                JSONArray backfields = new JSONArray();
                backfields.add(ESConstants.HDFSPATH);
                JSONObject jsonObject = searchService.searchDicomByPaging(criteria, backfields, null, 0, 0);
                if(SysConstants.CODE_000.equals(jsonObject.getString(SysConstants.CODE))) {
                    JSONArray data = jsonObject.getJSONArray(SysConstants.DATA);
                    for (int i = 0; i < data.size(); i++) {
                        String hdfspath = data.getJSONObject(i).getString(ESConstants.HDFSPATH);
                        paths.add(hdfspath);
                    }
                }

                //temp/小明/dicom
                String dicomPath = downloadPath+File.separator+"dicom";
                HdfsTool.downloadDicom(paths,dicomPath);

            }
            if(SysConstants.TYPE_ELECTRIC.equals(type)){
                //下载该患者所有电信号数据到本地的一个以该患者名i在命名的文件夹里面。
                //从milk直接访问hdfs，符合设计思想
                List<String> paths = new ArrayList<>();

                JSONArray criteriaElectric = new JSONArray();
                JSONObject itemElectri = new JSONObject();
                itemElectri.put(SysConstants.SECTION,SysConstants.NO);
                itemElectri.put(SysConstants.KEYWORD,ESConstants.PatientName_ES);
                itemElectri.put(SysConstants.VALUE,patientname);
                criteriaElectric.add(itemElectri);

                JSONArray backfields = new JSONArray();
                backfields.add(ESConstants.HDFSPATH_ES_ELECTRIC);

                JSONObject jsonObject = searchService.searchElectricByPaging(criteriaElectric, backfields, null, 0, 0);
                if(SysConstants.CODE_000.equals(jsonObject.getString(SysConstants.CODE))) {
                    JSONArray data = jsonObject.getJSONArray(SysConstants.DATA);
                    for (int i = 0; i < data.size(); i++) {
                        String hdfspath = data.getJSONObject(i).getString(ESConstants.HDFSPATH_ES_ELECTRIC);
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
            if(SysConstants.TYPE_KFB.equals(type)){

            }
            if(SysConstants.TYPE_GUAGE.equals(type)){

            }
        }

        //将以患者命名的本地临时文件压缩。
        String zipFileName = patientname+"_"+MilkTool.getRandonNumber(3)+".zip";
        ZipUtil.zip(downloadPath,tempPath,zipFileName);
        String zipFilePath = tempPath+File.separator+zipFileName;

        //删除临时目录，删除压缩文件
        MilkTool.delFolder(downloadPath);

        return zipFilePath;
    }

    /**
     *
     * @param list
     * @param tempDir  将下载的edf文件放到tempDir就可以了，不需要压缩，上层调用方法压缩
     * @return
     */
    @Override
    public String downloadElectricByIds(List<String> list,String tempDir) throws Exception {
        if(list==null || list.size()==0){
            return null;
        }
        if(tempDir.endsWith(SysConstants.LEFT_SLASH)){
            tempDir = tempDir.substring(0,tempDir.length()-1);
        }

        //tempDir结尾自带斜线,
        JSONArray backfields = new JSONArray();
        backfields.add(ESConstants.HDFSPATH);
        JSONArray ids = new JSONArray();
        for(String e : list){
            ids.add(e);
        }
        JSONObject json = new JSONObject();
        json.put(SysConstants.IDS,ids);
        json.put(SysConstants.DATATYPE,SysConstants.TYPE_ELECTRIC);
        json.put(SysConstants.BACKFIELDS,backfields);
        String interfaceStr = "/info/_searchByIds";
        DataTypeEnum dataTypeEnum = DataTypeEnum.OTHER;
        JSONObject result = null;
        try {
            result = MilkTool.doCallAndGetResult(json, interfaceStr,dataTypeEnum);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        System.out.println("查询的结果："+result.toJSONString());

        List<String> hdfspaths = new ArrayList<String>();
        if(SysConstants.CODE_000.equals(result.getString(SysConstants.CODE))){
            JSONArray data = result.getJSONArray(SysConstants.DATA);
            int size = data.size();
            for(int i=0;i<size;i++){
                JSONObject jsonObject = data.getJSONObject(i);
                hdfspaths.add(jsonObject.getString(ESConstants.HDFSPATH));
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

        return tempDir;
    }

    @Override
    public String downloadDicomByIds(List<String> list, String tempDir) throws Exception {
        if(list==null || list.size()==0){
            return null;
        }
        if(tempDir.endsWith(SysConstants.LEFT_SLASH)){
            tempDir = tempDir.substring(0,tempDir.length()-1);
        }

        JSONArray backfields = new JSONArray();
        backfields.add(ESConstants.HDFSPATH);
        JSONArray ids = new JSONArray();
        for(String e : list){
            ids.add(e);
        }
        JSONObject json = new JSONObject();
        json.put(SysConstants.IDS,ids);
        json.put(SysConstants.DATATYPE,SysConstants.TYPE_DICOM);
        json.put(SysConstants.BACKFIELDS,backfields);
        String interfaceStr = "/info/_searchByIds";
        DataTypeEnum dataTypeEnum = DataTypeEnum.DICOM;
        JSONObject result = null;
        try {
            result = MilkTool.doCallAndGetResult(json, interfaceStr,dataTypeEnum);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        System.out.println("查询的结果："+result.toJSONString());

        List<String> hdfspaths = new ArrayList<String>();
        String code = result.getString(SysConstants.CODE);
        if(code!=null && code.equals(SysConstants.CODE_000)){
            JSONArray data = result.getJSONArray(SysConstants.DATA);
            int size = data.size();
            for(int i=0;i<size;i++){
                JSONObject jsonObject = data.getJSONObject(i);
                hdfspaths.add(jsonObject.getString(ESConstants.HDFSPATH));
            }
        }
        boolean isSuccess = false;
        if(hdfspaths.size()!=0)
            isSuccess = HdfsTool.downloadDicom(hdfspaths, tempDir);

        return tempDir;
    }


    public static void main(String[] args) {
        String temp = "C:\\Users\\WeiGuangWu\\IdeaProjects\\bigdata\\es\\target\\es\\temp";
        String tag = "NB";
        String zipFilePath = temp+MilkTool.getDelimiter()+tag+".zip";
        //infosupplyer提供的下载接口
        String interfaceStr = "/data/downloadDesensitizeDdicomByTag";
        //调用下载方法，做具体下载工作
        doCallAndWriteToDisk(tag,interfaceStr,zipFilePath);
    }

}
