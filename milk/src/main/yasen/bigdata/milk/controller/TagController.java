package yasen.bigdata.milk.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import yasen.bigdata.milk.consts.ESConstants;
import yasen.bigdata.milk.consts.SysConstants;
import yasen.bigdata.milk.service.DataDownloadService;
import yasen.bigdata.milk.service.SearchService;
import yasen.bigdata.milk.service.TagService;
import yasen.bigdata.milk.tool.MilkTool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("")
public class TagController {

    @Autowired
    TagService tagService;

    @Autowired
    SearchService searchService;

    @Autowired
    DataDownloadService dataDownloadService;

    @ResponseBody
    @RequestMapping(value = "dosigntag", method = RequestMethod.POST)
    public JSONObject dosigntag(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, String> parametr) {
        //打标签
        System.out.println("dosigntag is called");
        String tag = parametr.get("tag");
        JSONObject result = new JSONObject();
        HttpSession session = request.getSession();
        JSONObject param = (JSONObject)session.getAttribute("searchParam");

        Long count = tagService.signTag(param, tag);
        System.out.println("此次打标签的数量："+count);
        result.put("result",true);
        result.put("total",count);
        result.put("tag",tag);
        return result;
    }

    //根据tag查询属于这个标签的dicom 序列
    @ResponseBody
    @RequestMapping(value = "searchDicomByTag", method = RequestMethod.POST)
    public JSONObject searchDicomByTag(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, String> parametr) {
        System.out.println("searchDicomByTag is called");
        String tag = parametr.get("tag");

        JSONObject result = new JSONObject();

        JSONObject searchcondition = new JSONObject();
        searchcondition.put(SysConstants.TAG,tag);

        JSONArray backfields = new JSONArray();
        backfields.add(ESConstants.InstitutionName_ES);
        backfields.add(ESConstants.ORGAN_ES);
        backfields.add(ESConstants.PatientName_ES);
        backfields.add(ESConstants.SeriesDescription_ES);
        backfields.add(ESConstants.SeriesDate_ES);
        backfields.add(ESConstants.NumberOfSlices_ES);
        backfields.add(ESConstants.ID_ES);

        JSONArray sortfields = new JSONArray();
        sortfields.add(ESConstants.InstitutionName_ES);
        sortfields.add(ESConstants.SeriesDescription_ES);
        sortfields.add(ESConstants.PatientName_ES);
        sortfields.add(ESConstants.SeriesDate_ES);
        sortfields.add(ESConstants.NumberOfSlices_ES);

        JSONObject tempResult = searchService.searchByPaging(searchcondition, backfields,sortfields,1, SysConstants.DEFAULT_PAGE_SIZE);
        result.put("total",tempResult.getLong("total"));
        result.put("rows",tempResult.getJSONArray("data"));
        System.out.println("total:"+tempResult.getLong("total"));
        return result;
    }

    //请求做脱敏操作
    @ResponseBody
    @RequestMapping(value = "desensitize", method = RequestMethod.POST)
    public JSONObject desensitize(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, String> parametr) {
        System.out.println("desensitize is called");
        String tag = parametr.get("tag");

        long count = tagService.doDesensitize(tag);
        JSONObject result = new JSONObject();
        if(count>0){
            result.put("result",true);
        }else{
            result.put("result",false);
        }
        return result;
    }


    //根据tag查询脱敏数据
    @ResponseBody
    @RequestMapping(value = "searchTag", method = RequestMethod.POST)
    public JSONObject searchTag(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, String> parametr) {
        System.out.println("searchTag is called");
        String tag = parametr.get("tag");
        JSONObject result = tagService.searchTags(tag);
        System.out.println(result);
        return result;
    }

    //根据tag下载脱敏数据
    @ResponseBody
    @RequestMapping(value = "downloadDesensitizeByTag", method = RequestMethod.GET)
    public void downloadDesensitizeByTag(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, String> parametr) {
        System.out.println("downloadDesensitizeByTag is called");
        String tag = request.getParameter("tag");
        //projectPath是工程绝对路径 C://.../../milk
        String projectRealPath = request.getSession().getServletContext().getRealPath(SysConstants.LEFT_SLASH);
        //工程下面temp目录绝对路径，用于存放临时文件，操作需要
        String tempRealPath = projectRealPath+SysConstants.TEMP_STRING;
        String zipFilePath = dataDownloadService.downloadDesensitizeDdicomByTag(tag, tempRealPath);
        MilkTool.doDownload(response,zipFilePath,"zip");
        System.out.println(tag);
    }
}
