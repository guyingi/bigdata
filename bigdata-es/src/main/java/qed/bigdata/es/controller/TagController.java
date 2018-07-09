package qed.bigdata.es.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import qed.bigdata.es.consts.DataTypeEnum;
import qed.bigdata.es.consts.ESConsts;
import qed.bigdata.es.service.DataDownloadService;
import qed.bigdata.es.service.SearchService;
import qed.bigdata.es.service.TagService;
import qed.bigdata.es.consts.SysConsts;
import qed.bigdata.es.tool.Tool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("")
public class TagController {

    static Logger logger = Logger.getLogger(TagController.class);

    @Autowired
    TagService tagService;

    @Autowired
    SearchService searchService;

    @Autowired
    DataDownloadService dataDownloadService;

    @ResponseBody
    @RequestMapping(value = "dosigntag", method = RequestMethod.POST)
    public JSONObject dosigntag(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, String> parametr) {
        logger.log(Level.INFO,"controller:dosigntag 被调用");

        //打标签
        String tag = parametr.get("tag");
        HttpSession session = request.getSession();
        JSONArray param = (JSONArray)session.getAttribute("searchParam");

        logger.log(Level.INFO,"接收的参数:tag:"+tag+"\t searchParam:"+param.toJSONString());

        JSONObject result = tagService.signTag(param, tag);

        logger.log(Level.INFO,"返与前端结果:"+result.toJSONString());

        return result;
    }

    @ResponseBody
    @RequestMapping(value = "removedcmtag", method = RequestMethod.POST)
    public JSONObject removedcmtag(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, String> parameter) {
        logger.log(Level.INFO,"controller:removedcmtag 被调用");

        String tag = parameter.get("tag");

        logger.log(Level.INFO,"接收的参数:tag:"+tag);

        JSONObject result = new JSONObject();

        boolean success = tagService.removeTag(tag, DataTypeEnum.DICOM);
        result.put(SysConsts.RESULT,success);

        logger.log(Level.INFO,"返与前端结果:"+result.toJSONString());

        return result;
    }

    //根据tag查询属于这个标签的dicom 序列
    @ResponseBody
    @RequestMapping(value = "searchDicomByTag", method = RequestMethod.POST)
    public JSONObject searchDicomByTag(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, String> parametr) {
        logger.log(Level.INFO,"controller:searchDicomByTag 被调用");

        String tag = parametr.get("tag");

        logger.log(Level.INFO,"接收的参数:tag:"+tag);

        JSONObject result = new JSONObject();

        JSONArray criteria = new JSONArray();
        JSONObject obj = new JSONObject();
        obj.put(SysConsts.SECTION,SysConsts.NO);
        obj.put(SysConsts.KEYWORD,SysConsts.TAG);
        obj.put(SysConsts.VALUE,tag);
        criteria.add(obj);

        JSONArray backfields = new JSONArray();
        backfields.add(ESConsts.InstitutionName_ES);
        backfields.add(ESConsts.SeriesDescription_ES);
        backfields.add(ESConsts.PatientName_ES);
        backfields.add(ESConsts.SeriesDate_ES);
        backfields.add(ESConsts.NumberOfSlices_ES);
        backfields.add(ESConsts.TAG_ES);
        backfields.add(ESConsts.ID_ES);

        JSONArray sortfields = new JSONArray();
        sortfields.add(ESConsts.InstitutionName_ES);
        sortfields.add(ESConsts.SeriesDescription_ES);
        sortfields.add(ESConsts.PatientName_ES);
        sortfields.add(ESConsts.SeriesDate_ES);
        sortfields.add(ESConsts.NumberOfSlices_ES);

        JSONObject tempResult = searchService.searchDicomByPaging(criteria, backfields,sortfields,1, SysConsts.DEFAULT_PAGE_SIZE);
        result.put("total",tempResult.getLong("total"));
        result.put("rows",tempResult.getJSONArray("data"));
        System.out.println("total:"+tempResult.getLong("total"));

        logger.log(Level.DEBUG,"返与前端结果:"+result.toJSONString());
        logger.log(Level.INFO,"返与前端结果:total:"+tempResult.getLong("total"));

        return result;
    }

    //请求做脱敏操作
    @ResponseBody
    @RequestMapping(value = "desensitize", method = RequestMethod.POST)
    public JSONObject desensitize(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, String> parametr) {
        logger.log(Level.INFO,"controller:desensitize 被调用");

        String tag = parametr.get("tag");

        logger.log(Level.INFO,"接收的参数:tag:"+tag);

        boolean tagDisensitized = tagService.isTagDisensitized(tag);

        logger.log(Level.INFO,"已被脱敏："+tagDisensitized);

        JSONObject result = new JSONObject();
        if(tagService.isTagDisensitized(tag)){
            result.put("result",3);         //已做脱敏，无需再做
            return result;
        }

        Integer status = tagService.doDesensitize(tag);
        if(status == 0){
            result.put("result",status);         //脱敏任务提交成功
        }else if(status == 1){
            result.put("result",status);       //任务已经存在，不允许重复提交
        }else{
            result.put("result",status);       //任务提交失败，此时status=2
        }
        logger.log(Level.INFO,"返与前端结果:"+result.toJSONString());

        return result;
    }


    //根据tag查询脱敏数据
    @ResponseBody
    @RequestMapping(value = "searchTag", method = RequestMethod.POST)
    public JSONObject searchTag(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, String> parametr) {
        logger.log(Level.INFO,"controller:searchTag 被调用");

        String tag = parametr.get("tag");

        logger.log(Level.INFO,"接收的参数:tag:"+tag);

        JSONObject result = tagService.searchTags(tag);

        logger.log(Level.INFO,"返与前端结果:"+result.toJSONString());

        return result;
    }

    //根据tag查询脱敏数据
    @ResponseBody
    @RequestMapping(value = "listtags", method = RequestMethod.POST)
    public JSONObject listTags() {
        logger.log(Level.INFO,"controller:listTags 被调用");

        JSONObject result = tagService.listTags();

        logger.log(Level.DEBUG,"返与前端结果:"+result.toJSONString());

        return result;
    }

    //接收多个tag，生成可以用来下载其对应脱敏数据的json文件，使用第三方软件读取json文件下载脱敏数据
    @ResponseBody
    @RequestMapping(value = "exportDesensitizeByTag", method = RequestMethod.POST)
    public void exportDesensitizeByTag(HttpServletRequest request, HttpServletResponse response) {
        logger.log(Level.INFO,"controller:exportDesensitizeByTag 被调用");

        String tagStr = request.getParameter("tags");

        logger.log(Level.INFO,"接收的参数:tags:"+tagStr);

        String[] tags = tagStr.split("-");

        List<String> tagList = new ArrayList<String>();
        for(String tag : tags){
            tagList.add(tag);
        }

        //projectPath是工程绝对路径 C://.../../es
        String projectRealPath = request.getSession().getServletContext().getRealPath(SysConsts.LEFT_SLASH);
        //工程下面temp目录绝对路径，用于存放临时文件，操作需要
        String tempRealPath = projectRealPath+SysConsts.TEMP_STRING;
        String jsonFilePath = searchService.getDesensitizeDownloadFileByTag(tagList,tempRealPath);

        if(!StringUtils.isBlank(jsonFilePath)){
            Tool.doDownload(response,jsonFilePath,"json");
            new File(jsonFilePath).delete();
        }

        logger.log(Level.INFO,"控制流程结束:exportDesensitizeByTag");
    }

    //根据tag下载脱敏数据
    @ResponseBody
    @RequestMapping(value = "downloadDesensitizeByTag", method = RequestMethod.POST)
    public void downloadDesensitizeByTag(HttpServletRequest request, HttpServletResponse response) {
        logger.log(Level.INFO,"controller:downloadDesensitizeByTag 被调用");

        String tag = request.getParameter("tag");

        logger.log(Level.INFO,"接收的参数:tag:"+tag);

        //projectPath是工程绝对路径 C://.../../es
        String projectRealPath = request.getSession().getServletContext().getRealPath(SysConsts.LEFT_SLASH);
        //工程下面temp目录绝对路径，用于存放临时文件，操作需要
        String tempRealPath = projectRealPath+SysConsts.TEMP_STRING;
        String zipFilePath = dataDownloadService.downloadDesensitizeDdicomByTag(tag, tempRealPath);
//        String zipFilePath = "C:\\Users\\WeiGuangWu\\IdeaProjects\\bigdata\\es\\web\\temp\\0001.zip";

        if(!StringUtils.isBlank(zipFilePath)){
            Tool.doDownload(response,zipFilePath,"zip");
            new File(zipFilePath).delete();
        }

        logger.log(Level.INFO,"控制流程结束:downloadbypatient");
    }

    //根据tag下载dicom
    @ResponseBody
    @RequestMapping(value = "getDcmDownloadFileByTag", method = RequestMethod.POST)
    public void downloadDicomByTag(HttpServletRequest request, HttpServletResponse response) {
        logger.log(Level.INFO,"controller:downloadDicomByTag 被调用");
        String tag = request.getParameter("tag");
        logger.log(Level.INFO,"接收的参数:tag:"+tag);

        String projectPath = request.getSession().getServletContext().getRealPath("/");
        //C:\Users\WeiGuangWu\IdeaProjects\bigdata\es\target\es\temp
        String tempDir = projectPath+"temp";

        String tempFilePath = null;
        if(tag != null) {
            tempFilePath = searchService.getDownloadFileByTag(tag, tempDir);
            Tool.doDownload(response, tempFilePath, "json");
        }
        Tool.delFile(tempFilePath);

        logger.log(Level.INFO,"控制流程结束:downloadbypatient");
    }
}
