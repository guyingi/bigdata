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
import yasen.bigdata.infosupplyer.consts.DataTypeEnum;
import yasen.bigdata.infosupplyer.consts.SysConstants;
import yasen.bigdata.infosupplyer.dao.PatientDao;
import yasen.bigdata.infosupplyer.pojo.db.Patient;
import yasen.bigdata.infosupplyer.service.ElasticSearchService;
import yasen.bigdata.infosupplyer.service.SearchService;
import yasen.bigdata.infosupplyer.consts.ESConstant;
import yasen.bigdata.infosupplyer.service.impl.ElasticSearchServiceImpl;
import yasen.bigdata.infosupplyer.util.InfoSupplyerTool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/info")
public class SearchController {
    static Logger logger = Logger.getLogger(SearchController.class);

    @Autowired
    ElasticSearchService elasticSearchService;

    @Autowired
    PatientDao patientDao;

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
        JSONObject reuslt = new JSONObject();
        JSONObject param = InfoSupplyerTool.formatParameter(parameter);
        if(SysConstants.TYPE_DICOM.equals(param.getString(SysConstants.DATATYPE))){
            System.out.println(param.toJSONString());
            DataTypeEnum type = DataTypeEnum.DICOM;
            reuslt = elasticSearchService.searchByPaging(param,type);
        }else if(SysConstants.TYPE_ELECTRIC.equals(param.getString(SysConstants.DATATYPE))){
            DataTypeEnum type = DataTypeEnum.ELECTRIC;
            reuslt = elasticSearchService.searchByPaging(param,type);
        }

        return reuslt;
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
        String datatype = (String)parameter.get("datatype");
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
        param.put("datatype",datatype);
        if (backfields != null) {
            JSONArray tempArr = new JSONArray();
            for (String e : backfields) {
                tempArr.add(e);
            }
            param.put("backfields", tempArr);
        }
        JSONObject result = elasticSearchService.searchByIds(param);
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
        JSONObject param = InfoSupplyerTool.formatParameter(parameter);
        DataTypeEnum type = DataTypeEnum.DICOM;
        JSONObject jsonObject = elasticSearchService.searchTotalRecord(param,type);
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
        elasticSearchService.searchAll();
        return "this is _searchall";
    }


    //一定要查询数据库，因为这个人可能只有电信号或者量表，没有dicom
    @PostMapping("/getpatients")
    public JSONObject searchPatients(@RequestBody Map<String, Object> parameter){
        String patientname = (String)parameter.get("patientname");
        System.out.println("getpatients 接收到的参数："+patientname);
        List<Patient> patients = patientDao.getPatientByName(patientname);
        System.out.println(patients.size());
        JSONObject result = new JSONObject();
        result.put(SysConstants.CODE,SysConstants.CODE_000);
        result.put(SysConstants.TOTAL,patients.size());
        result.put(SysConstants.DATA,patients);
        System.out.println("getpatients返回的数据："+result.toJSONString());
        return result;
    }





}
