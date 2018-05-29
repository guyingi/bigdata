package yasen.bigdata.infosupplyer.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yasen.bigdata.infosupplyer.conf.InfosupplyerConfiguration;
import yasen.bigdata.infosupplyer.consts.ESConstant;
import yasen.bigdata.infosupplyer.factory.ConfigFactory;
import yasen.bigdata.infosupplyer.service.DesensitizationService;
import yasen.bigdata.infosupplyer.service.ElasticSearchService;
import yasen.bigdata.infosupplyer.service.HBaseService;
import yasen.bigdata.infosupplyer.service.SearchService;
import yasen.bigdata.infosupplyer.service.impl.ElasticSearchServiceImpl;
import yasen.bigdata.infosupplyer.service.impl.HBaseServiceImpl;
import yasen.bigdata.infosupplyer.util.InfoSupplyerTool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.AccessControlException;

@RestController
@RequestMapping("/data")
public class DownloadController {
    static Logger logger = Logger.getLogger(DownloadController.class);

    @Autowired
    ElasticSearchService elasticSearchService;

    @Autowired
    HBaseService hBaseService;

    @Autowired
    DesensitizationService desensitizationService;

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
//        back.add(ESConstant.ROWKEY);
//        ids.add(id);
//        param.put(ESConstant.IDS,ids);
//        param.put(ESConstant.BACKFIELDS,back);
//        JSONObject result = searchService.searchByIds(param);
//        String rowkey = null;
//        if(result.getLong("total")>0){
//            rowkey = result.getJSONArray(ESConstant.DATA).getJSONObject(0).getString(ESConstant.ROWKEY);
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


    //下载dicom文件缩略图
    @RequestMapping("/downloadDicomNail")
    public String downloadDicomNail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("id");
        System.out.println("id:"+id);
        //访问ES获取rowkey
        JSONObject param = new JSONObject();
        JSONArray ids = new JSONArray();
        JSONArray backfields = new JSONArray();
        ids.add(id);
        backfields.add(ESConstant.ROWKEY);
        param.put(ESConstant.IDS,ids);
        param.put(ESConstant.BACKFIELDS,backfields);

        JSONObject result = elasticSearchService.searchByIds(param);
        String rowkey = null;
        if(result.getLong("total")>0){
            rowkey = result.getJSONArray(ESConstant.DATA).getJSONObject(0).getString(ESConstant.ROWKEY);
            System.out.println("rowkey:"+rowkey);
        }else{
            return null;
        }

        //访问hbase下载文件到临时目录，并生成zip压缩文件，返回压缩文件路径
        InfosupplyerConfiguration infosupplyerConfiguration = ConfigFactory.getInfosupplyerConfiguration();

        //存放缩略图文件夹的临时目录
        String tempPath = infosupplyerConfiguration.getThumbnailTempPath();
        String zipFilePath = null;
        try {
            System.out.println("tempPath:"+tempPath+",rowkey:"+rowkey);
            zipFilePath = hBaseService.downloadThumbnailByRowkey(rowkey,tempPath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("压缩文件路径："+zipFilePath);
        //读取压缩文件，写出输出流
        writeOutZip(zipFilePath,response);

        //删除压缩文件
        InfoSupplyerTool.delSingleFile(zipFilePath);

        return null;
    }



    @RequestMapping("/downloadDesensitizeDdicomByTag")
    public String downloadDesensitizeDdicomByTag(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String tag = request.getParameter("id");
        System.out.println("tag:"+tag);

        String zipFilePath = null;
        try {
            zipFilePath = desensitizationService.downloadDesensitizeDicomByTag(tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("压缩文件路径："+zipFilePath);
        //读取压缩文件，写出输出流
        writeOutZip(zipFilePath,response);

        //删除压缩文件
        InfoSupplyerTool.delSingleFile(zipFilePath);

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

}
