package qed.bigdata.infosupplyer.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import qed.bigdata.infosupplyer.conf.InfosupplyerConfiguration;
import qed.bigdata.infosupplyer.consts.EsConsts;
import qed.bigdata.infosupplyer.consts.SysConsts;
import qed.bigdata.infosupplyer.util.InfoSupplyerTool;
import qed.bigdata.infosupplyer.factory.ConfigFactory;
import qed.bigdata.infosupplyer.service.DesensitizationService;
import qed.bigdata.infosupplyer.service.DownloadService;
import qed.bigdata.infosupplyer.service.ElasticSearchService;
import qed.bigdata.infosupplyer.service.HBaseService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@RestController
@RequestMapping("/data")
public class DownloadController {
    static Logger logger = Logger.getLogger(DownloadController.class);

    InfosupplyerConfiguration infosupplyerConfiguration;

    @Autowired
    ElasticSearchService elasticSearchService;

    @Autowired
    HBaseService hBaseService;

    @Autowired
    DesensitizationService desensitizationService;

    @Autowired
    DownloadService downloadService;

    //下载dicom文件缩略图
    @RequestMapping("/downloadDicomNail")
    public String downloadDicomNail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.log(Level.INFO,"接口:downloadDicomNail 被调用");

        String id = request.getParameter("id");

        logger.log(Level.INFO,"接口接收的参数:id"+id);

        //访问ES获取rowkey
        JSONObject param = new JSONObject();
        JSONArray ids = new JSONArray();
        JSONArray backfields = new JSONArray();
        ids.add(id);
        backfields.add(EsConsts.ROWKEY);
        param.put(SysConsts.DATATYPE,SysConsts.TYPE_DICOM);
        param.put(SysConsts.IDS,ids);
        param.put(SysConsts.BACKFIELDS,backfields);

        JSONObject result = elasticSearchService.searchByIds(param);
        String rowkey = null;
        if(result.getLong("total")>0){
            rowkey = result.getJSONArray(EsConsts.DATA).getJSONObject(0).getString(EsConsts.ROWKEY);
            System.out.println("rowkey:"+rowkey);
        }else{
            return null;
        }

        logger.log(Level.INFO,"查询到rowkey:"+rowkey);
        //访问hbase下载文件到临时目录，并生成zip压缩文件，返回压缩文件路径
        InfosupplyerConfiguration infosupplyerConfiguration = ConfigFactory.getInfosupplyerConfiguration();

        //存放缩略图文件夹的临时目录
        String tempPath = infosupplyerConfiguration.getThumbnailTempPath();
        logger.log(Level.INFO,"存放缩略图文件夹的临时目录:"+tempPath);
        String zipFilePath = null;
        try {
            System.out.println("tempPath:"+tempPath+",rowkey:"+rowkey);
            zipFilePath = hBaseService.downloadThumbnailByRowkey(rowkey,tempPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.log(Level.INFO,"输出zip压缩文件:"+zipFilePath);

        //读取压缩文件，写出输出流
        writeOutZip(zipFilePath,response);

        //删除压缩文件
        InfoSupplyerTool.delSingleFile(zipFilePath);
        logger.log(Level.INFO,"删除zip压缩文件:"+zipFilePath);

        return null;
    }



    @RequestMapping("/downloadDesensitizeDicomByTag")
    public String downloadDesensitizeDicomByTag(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.log(Level.INFO,"接口:downloadDesensitizeDicomByTag 被调用");

        String tag = request.getParameter("id");
        logger.log(Level.INFO,"接口接收的参数:tag"+tag);

        String zipFilePath = null;
        try {
            zipFilePath = desensitizationService.downloadDesensitizeDicomByTag(tag);
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.log(Level.INFO,"输出zip压缩文件:"+zipFilePath);
        //读取压缩文件，写出输出流
        if(zipFilePath != null){
            writeOutZip(zipFilePath,response);

            //删除压缩文件
            InfoSupplyerTool.delSingleFile(zipFilePath);
            logger.log(Level.INFO,"删除zip压缩文件:"+zipFilePath);
        }else{
            logger.log(Level.INFO,"生成脱敏数据失败:"+zipFilePath);
            response.setStatus(500);
        }

        return null;
    }

    @RequestMapping("/downloadElectricByName")
    public String downloadElectricByName(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.log(Level.INFO,"接口:downloadElectricByName 被调用");

        String patientname = request.getParameter(SysConsts.PATIENTNAME);

        logger.log(Level.INFO,"接口接收的参数:patientname"+patientname);

        String zipFilePath = downloadService.downloadElectricByPatientname(patientname);

        logger.log(Level.INFO,"输出zip压缩文件:"+zipFilePath);

        //读取压缩文件，写出输出流
        if( !StringUtils.isBlank(zipFilePath )){
            writeOutZip(zipFilePath,response);
            //删除压缩文件
            InfoSupplyerTool.delSingleFile(zipFilePath);
            logger.log(Level.INFO,"删除zip压缩文件:"+zipFilePath);
        }else{
            logger.log(Level.INFO,"下载电信号数据失败:"+zipFilePath);
            response.setStatus(500);
        }
        return null;
    }

    private static void writeOutZip(String filepath,HttpServletResponse response){
        if (filepath != null) {
            File file = new File(filepath);
            if (file.exists()) {
                response.setContentType("application/force-download");// 设置强制下载不打开
                response.addHeader("Content-Disposition",
                        "attachment;fileName=" +  file.getName());// 设置文件名
                byte[] buffer = new byte[1024];
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                try {
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    OutputStream os = response.getOutputStream();
                    int i = bis.read(buffer);
                    while (i != -1) {
                        os.write(buffer, 0, i);
                        i = bis.read(buffer);
                    }
                    System.out.println("success");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }


    public static void main(String[] args) {
        System.out.println(StringUtils.isBlank("dd  s"));
//        ElasticSearchService searchService = new ElasticSearchServiceImpl();
//        HBaseService hBaseService = new HBaseServiceImpl();
//        String id = request.getParameter("id");
//        System.out.println("id:"+id);
//        String id = "128401136192363104997433902406223511521416862616";
//        //访问ES获取rowkey
//        JSONObject param = new JSONObject();
//        JSONArray ids = new JSONArray();
//        JSONArray back = new JSONArray();
//        back.add(EsConsts.ROWKEY_ES_DCM);
//        ids.add(id);
//        param.put(EsConsts.IDS,ids);
//        param.put(EsConsts.BACKFIELDS,back);
//        JSONObject result = searchService.searchByIds(param);
//        String rowkey = null;
//        if(result.getLong("total")>0){
//            rowkey = result.getJSONArray(EsConsts.DATA).getJSONObject(0).getString(EsConsts.ROWKEY_ES_DCM);
//            System.out.println(rowkey);
//        }else{
//            System.out.println("结果为空");
//        }
//
//
//        //访问hbase下载文件到临时目录，并生成zip压缩文件，返回压缩文件路径
//        String tempPath = "C:\\Users\\WeiGuangWu\\IdeaProjects\\bigdata\\infosupplyer\\target\\temp\\";
//        String zipFilePath = null;
//        try {
//            zipFilePath = hBaseService.downloadThumbnailByRowkey(rowkey,tempPath);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.out.println(zipFilePath);
        //读取压缩文件，写出输出流
//        writeOutZip(zipFilePath,response);

        //删除压缩文件
//        deleteTempFile(zipFilePath);

    }

}
