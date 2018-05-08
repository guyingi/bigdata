package yasen.bigdata.infosupplyer.controller;

/**
 * @Title: SearchController.java
 * @Package yasen.bigdata.infosupplyer.controller
 * @Description: 查询接口类
 * @author weiguangwu
 * @date 2018/4/23 14:13
 * @version V1.0
 */

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import yasen.bigdata.infosupplyer.conf.SysConstants;
import yasen.bigdata.infosupplyer.service.SearchService;
import yasen.bigdata.infosupplyer.conf.ESConstant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/info")
public class SearchController {
    static Logger logger = Logger.getLogger(SearchController.class);

    @Autowired
    SearchService searchService;

    /**
     * @Author:weiguangwu
     * @Description:分页查询接口_searchpaging
     * @params:[parameter]
     * @return: com.alibaba.fastjson.JSONObject
     * @Date: 2018/4/23 14:37
     */
    @PostMapping("/_searchpaging")
    public JSONObject searchPaging(@RequestBody Map<String, Object> parameter) {
        logger.info("_searchpaging is called");
        System.out.println("_searchpaging is called");
        JSONObject param = formatParameter(parameter);
        System.out.println(param.toJSONString());
        JSONObject jsonObject = searchService.searchByPaging(param);
        return jsonObject;
    }

    /**
     * @Author:weiguangwu
     * @Description:根据es id批量查询接口 _searchByIds
     * @params:[parameter]
     * @return: com.alibaba.fastjson.JSONObject
     * @Date: 2018/4/23 14:38
     */
    @PostMapping("/_searchByIds")
    public JSONObject searchByIds(@RequestBody Map<String, Object> parameter) {
        logger.info("_searchByIds is called");
        System.out.println("_searchByIds is called");
        JSONObject param = new JSONObject();
        List<String> idsList = (List<String>) parameter.get("ids");
        List<String> backfields = (List<String>) parameter.get("backfields");
        if (idsList != null && idsList.size() != 0) {
            JSONArray tempArr = new JSONArray();
            for (String e : idsList) {
                tempArr.add(e);
            }
            param.put("ids", tempArr);
        } else {
            return new JSONObject();
        }
        if (backfields != null) {
            JSONArray tempArr = new JSONArray();
            for (String e : backfields) {
                tempArr.add(e);
            }
            param.put("backfields", tempArr);
        }
        JSONObject result = searchService.searchByIds(param);
        return result;
    }

    /**
     * @Author:weiguangwu
     * @Description:返回该参数对应结果总数
     * @params:[parameter]
     * @return: com.alibaba.fastjson.JSONObject
     * @Date: 2018/4/23 15:14
     */
    @PostMapping("/_searchtotal")
    public JSONObject searchtotal(@RequestBody Map<String, Object> parameter) {
        logger.info("_searchtotal is called");
        System.out.println("_searchtotal is called");
        JSONObject param = formatParameter(parameter);
        JSONObject jsonObject = searchService.searchTotalRecord(param);
        return jsonObject;
    }

    /**
     * @Author:weiguangwu
     * @Description:返回es数据库中所有数据，慎用
     * @return: java.lang.String
     * @Date: 2018/4/23 15:16
     */
    @RequestMapping("/_searchall")
    public String searchall() {
        logger.info("_searchall is called");
        searchService.searchAll();
        return "this is _searchall";
    }

//    @RequestMapping("/_search1")
//    public Object search1() {
//        JsonObject json = new JsonObject();
//        String hos = "PUMCH";
//        String se = "M";
//        String seq = "1.2.840.113619.2.363.10499743.3902406.15735.1483921213.783";
//        json.addProperty(ESConstant.HOSPITAL,hos);
//        json.addProperty(ESConstant.SEX,se);
//        json.addProperty(ESConstant.MRI_SEQUENCE,seq);
//
//        Map<String, List<String>> map = new HashMap<String, List<String>>();
//        List<String> result = searchService.search1(json);
//        map.put("filepath",result);
//        return result;
//    }


    /**
     * 测试可用
     * @return
     */
    @RequestMapping("/_search2")
    public String search2() {
        System.out.println("_search2");
        logger.info("_search2 is called");
        return "this is search2";
    }

    /**
     * 测试可用
     * @return
     */
    @RequestMapping("/_search3")
    public String search3() {
        logger.info("_search3 is called");
//        System.out.println("测试接口_search3");
        JSONObject json = new JSONObject();
        json.put("sex", "F");
        json.put("mriseq", "Static 3D MAC");
//        List<JSONObject> result = searchService.search1(json);
//        System.out.println("_search3查询结果数量："+result.size());
        return "";
    }


    /**
     * 测试可用
     * @param reqMap
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/_search", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public Map<String, String> _search(@RequestBody Map<String, Object> reqMap) {
//        System.out.println("_search");
        logger.info("_search3 is called");
        Map<String, String> map = new HashMap<String, String>();
        map.put("hehe", "212");
        return map;
    }

    /**
     *
     * @param param
     * @return
     * @deprecated
     */
    @PostMapping("/_searchmsgles")
    public List<JSONObject> _searchmsgles(@RequestBody Map<String, Object> param) {

        return null;
    }

    /**
     * @param param
     * @return
     * @deprecated
     */
    @PostMapping("/_searchfordownload")
    public JSONObject _search2(@RequestBody Map<String, Object> param) {

        return null;
    }

    /**
     * 测试可用
     * @param param
     * @return
     * @deprecated
     */
    @PostMapping("/_search4")
    public String _search4(@RequestBody String param) {
//        System.out.println(param);
        return null;
    }


    /**
     * @Author:weiguangwu
     * @Description:格式化参数，主要提取searchPaging接口参数，转换为SearchService的接口使用的参数格式
     * @params:[parameter]
     * @return: com.alibaba.fastjson.JSONObject
     * @Date: 2018/4/23 14:42
     */
    public JSONObject formatParameter(Map<String, Object> parameter) {
        JSONObject param = new JSONObject();
        for (Map.Entry<String, Object> entry : parameter.entrySet()) {
            if (entry.getKey().equals("searchcondition")) {
                HashMap<String, Object> hashmap = (HashMap<String, Object>) entry.getValue();
                JSONObject obj = new JSONObject();
                for (Map.Entry<String, Object> e : hashmap.entrySet()) {
                    if (e.getKey().equals(SysConstants.DEVICE_PARAM)) {
                        obj.put(ESConstant.ManufacturersModelName_ES, e.getValue().toString());
                    } else if (e.getKey().equals(SysConstants.AGE_START_PARAM)) {
                        obj.put(SysConstants.AGE_START_PARAM, Integer.parseInt(e.getValue().toString()));
                    } else if (e.getKey().equals(SysConstants.AGE_END_PARAM)) {
                        obj.put(SysConstants.AGE_END_PARAM, Integer.parseInt(e.getValue().toString()));
                    } else if (e.getKey().equals(SysConstants.IMAGECOUNT_MIN_PARAM)) {
                        obj.put(SysConstants.IMAGECOUNT_MIN_PARAM, Integer.parseInt(e.getValue().toString()));
                    } else if (e.getKey().equals(SysConstants.IMAGECOUNT_MAX_PARAM)) {
                        obj.put(SysConstants.IMAGECOUNT_MAX_PARAM, Integer.parseInt(e.getValue().toString()));
                    } else {
                        obj.put(e.getKey(), e.getValue().toString());
                    }
                }
                param.put("searchcondition", obj);
            } else if (entry.getKey().equals("backfields")) {
                List<String> list = (List<String>) entry.getValue();
                JSONArray arr = new JSONArray();
                for (String e : list) {
                    arr.add(e);
                }
                param.put("backfields", arr);
            } else if (entry.getKey().equals("sortfields")) {
                List<String> list = (List<String>) entry.getValue();
                JSONArray arr = new JSONArray();
                for (String e : list) {
                    arr.add(e);
                }
                param.put("sortfields", arr);
            } else {
                if (entry.getKey().equals(SysConstants.PAGE_ID)) {
                    param.put(SysConstants.PAGE_ID, Integer.parseInt(entry.getValue().toString()));
                } else if (entry.getKey().equals(SysConstants.PAGE_SIZE)) {
                    param.put(SysConstants.PAGE_SIZE, Integer.parseInt(entry.getValue().toString()));
                } else {
                    param.put(entry.getKey(), entry.getValue().toString());
                }
            }
        }
        return param;
    }


    //参数校验,主要检查是否为空或者字符串长度为0，过时弃用。
//    public JSONObject paramCheck(Map<String,Object> param){
//        JSONObject json = new JSONObject();
//        String deviceType = (String)param.get(ESConstant.DEVICE_PARAM);
//        if(deviceType !=null && deviceType.length()!=0)
//            json.put(ESConstant.ManufacturersModelName_ES,deviceType);
//        String organ = (String)param.get(ESConstant.ORGAN_ES);
//        if(organ !=null && organ.length()!=0)
//            json.put(ESConstant.ORGAN_ES,organ);
//        String hospital =(String)param.get(ESConstant.INSTITUTION_PARAM);
//        if(hospital !=null && hospital.length()!=0)
//            json.put(ESConstant.InstitutionName_ES,hospital);
//        String mrisequence =(String)param.get(ESConstant.SERIES_DESCRIPTION_PARAM);
//        if(mrisequence !=null && mrisequence.length()!=0)
//            json.put(ESConstant.SeriesDescription_ES,mrisequence);
//        String sex =(String)param.get(ESConstant.SEX_PARAM);
//        if(sex !=null && sex.length()!=0)
//            json.put(ESConstant.PatientsSex_ES,sex);
//        String ageStartParam=(String)param.get(ESConstant.AGE_START_PARAM);
//        if(ageStartParam !=null && ageStartParam.length()!=0)
//            json.put(ESConstant.AGE_START_PARAM,ageStartParam);
//        String ageEndParam=(String)param.get(ESConstant.AGE_END_PARAM);
//        if(ageEndParam !=null && ageEndParam.length()!=0)
//            json.put(ESConstant.AGE_END_PARAM,ageEndParam);
//        String studyDateStartParam =(String)param.get(ESConstant.STUDYDATE_START_PARAM);
//        if(studyDateStartParam !=null && studyDateStartParam.length()!=0)
//            json.put(ESConstant.STUDYDATE_START_PARAM,studyDateStartParam);
//        String studyDateEndParam =(String)param.get(ESConstant.STUDYDATE_END_PARAM);
//        if(studyDateEndParam !=null && studyDateEndParam.length()!=0)
//            json.put(ESConstant.STUDYDATE_END_PARAM,studyDateEndParam);
//        String entryDateStartParam =(String)param.get(ESConstant.ENTRYDATE_START_PARAM);
//        if(entryDateStartParam !=null && entryDateStartParam.length()!=0)
//            json.put(ESConstant.ENTRYDATE_START_PARAM,entryDateStartParam);
//        String entryDateEndParam =(String)param.get(ESConstant.ENTRYDATE_END_PARAM);
//        if(entryDateEndParam !=null && entryDateEndParam.length()!=0)
//            json.put(ESConstant.ENTRYDATE_END_PARAM,entryDateEndParam);
//        return json;
//    }
}
