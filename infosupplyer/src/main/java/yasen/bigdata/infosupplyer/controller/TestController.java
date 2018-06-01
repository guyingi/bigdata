package yasen.bigdata.infosupplyer.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import yasen.bigdata.infosupplyer.service.DesensitizationService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    DesensitizationService desensitizationService;

//    @RequestMapping("/")
//    public ModelAndView index(ModelAndView mav){
//        mav.addObject("hello", "这是项目主页，访问根目录到达~~");
//        mav.setViewName("index");
//        return mav;
//    }

//    @RequestMapping("/")
//    @ResponseBody
//    String home() {
//        return "Hello World!";
//    }

//    @RequestMapping("/getPhoto")
//    public Object doIt(){
//        Map<String, Photo> map = new HashMap<String, Photo>();
//        map.put("photo", testService.getPhotoById(123));
//        return map;
//    }


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

    @RequestMapping("/get")
    public String home() {
        return "hello";
    }

    @RequestMapping("/1")
    public String signtagtest() {
        //打标签测试

        return "hheh";
    }
    @RequestMapping("/2")
    public String desentizedtest() {
        //脱敏测试
        System.out.println("2 脱敏测试");
        Long br = desensitizationService.desensitizedicom("BR");
        return br+"";
    }


    @RequestMapping("/3")
    public String downdesentizedtest() {
        //下载脱敏数据测试
        System.out.println("3 下载脱敏数据测试");
        String br = null;
        try {
            br = desensitizationService.downloadDesensitizeDicomByTag("BR");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return br;
    }

    /**************下面都是一些测试接口，有不同传参方式，保留，用时可以参考************/
 /*    /**
     * 测试可用
     * @return
     *//*
    @RequestMapping("/_search2")
    public String search2() {
        System.out.println("_search2");
        logger.info("_search2 is called");
        return "this is search2";
    }

    *//**
     * 测试可用
     * @return
     *//*
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


    *//**
     * 测试可用
     * @param reqMap
     * @return
     *//*
    @ResponseBody
    @RequestMapping(value = "/_search", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public Map<String, String> _search(@RequestBody Map<String, Object> reqMap) {
//        System.out.println("_search");
        logger.info("_search3 is called");
        Map<String, String> map = new HashMap<String, String>();
        map.put("hehe", "212");
        return map;
    }

    *//**
     *
     * @param param
     * @return
     * @deprecated
     *//*
    @PostMapping("/_searchmsgles")
    public List<JSONObject> _searchmsgles(@RequestBody Map<String, Object> param) {

        return null;
    }

    *//**
     * @param param
     * @return
     * @deprecated
     *//*
    @PostMapping("/_searchfordownload")
    public JSONObject _search2(@RequestBody Map<String, Object> param) {

        return null;
    }

    *//**
     * 测试可用
     * @param param
     * @return
     * @deprecated
     *//*
    @PostMapping("/_search4")
    public String _search4(@RequestBody String param) {
//        System.out.println(param);
        return null;
    }*/


    //参数校验,主要检查是否为空或者字符串长度为0，过时弃用。
//    public JSONObject paramCheck(Map<String,Object> param){
//        JSONObject json = new JSONObject();
//        String deviceType = (String)param.get(ESConstant.DEVICE_PARAM);
//        if(deviceType !=null && deviceType.length()!=0)
//            json.put(ESConstant.ManufacturersModelName_ES_DCM,deviceType);
//        String organ = (String)param.get(ESConstant.ORGAN_ES_DCM);
//        if(organ !=null && organ.length()!=0)
//            json.put(ESConstant.ORGAN_ES_DCM,organ);
//        String hospital =(String)param.get(ESConstant.INSTITUTION_PARAM);
//        if(hospital !=null && hospital.length()!=0)
//            json.put(ESConstant.InstitutionName_ES_DCM,hospital);
//        String mrisequence =(String)param.get(ESConstant.SERIES_DESCRIPTION_PARAM);
//        if(mrisequence !=null && mrisequence.length()!=0)
//            json.put(ESConstant.SeriesDescription_ES_DCM,mrisequence);
//        String sex =(String)param.get(ESConstant.SEX_PARAM);
//        if(sex !=null && sex.length()!=0)
//            json.put(ESConstant.PatientsSex_ES_DCM,sex);
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