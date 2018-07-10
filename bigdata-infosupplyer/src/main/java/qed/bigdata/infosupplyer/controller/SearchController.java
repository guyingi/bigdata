package qed.bigdata.infosupplyer.controller;

/**
 * @Title: SearchController.java
 * @Package yasen.bigdata.infosupplyer.controller
 * @Description: 查询接口类
 * @author weiguangwu
 * @date 2018/4/23 14:13
 * @version V1.0
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import qed.bigdata.infosupplyer.conf.InfosupplyerConfiguration;
import qed.bigdata.infosupplyer.consts.DataTypeEnum;
import qed.bigdata.infosupplyer.consts.SysConsts;
import qed.bigdata.infosupplyer.factory.ConfigFactory;
import qed.bigdata.infosupplyer.pojo.bigdata.Patient;
import qed.bigdata.infosupplyer.util.InfoSupplyerTool;
import qed.bigdata.infosupplyer.dao.PatientDao;
import qed.bigdata.infosupplyer.service.ElasticSearchService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/info")
public class SearchController {
    static Logger logger = Logger.getLogger(SearchController.class);
    static InfosupplyerConfiguration infoConf = ConfigFactory.getInfosupplyerConfiguration();

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
    @PostMapping("/searchpaging")
    public JSONObject searchPaging(@RequestBody Map<String, Object> parameter) {

        logger.log(Level.INFO,"接口:searchpaging 被调用");

        JSONObject paramJson = JSONObject.parseObject(JSON.toJSONString(parameter));
        logger.log(Level.INFO,"接口接收的参数:"+paramJson.toJSONString());

        JSONObject result = new JSONObject();
        JSONObject param = InfoSupplyerTool.formatParameter(parameter);

        logger.log(Level.INFO,"格式化后的参数:"+param.toJSONString());

        if(SysConsts.TYPE_DICOM.equals(param.getString(SysConsts.DATATYPE))){
            System.out.println(param.toJSONString());
            DataTypeEnum type = DataTypeEnum.DICOM;
            result = elasticSearchService.searchByPaging(param,type);
        }else if(SysConsts.TYPE_ELECTRIC.equals(param.getString(SysConsts.DATATYPE))){
            DataTypeEnum type = DataTypeEnum.ELECTRIC;
            result = elasticSearchService.searchByPaging(param,type);
        }

        logger.log(Level.INFO,"接口返回结果"+result.toJSONString());

        return result;
    }

    /**
     * @Author:weiguangwu
     * @Description:根据es id批量查询接口 _searchByIds
     * @params:[parameter]
     * @return: com.alibaba.fastjson.JSONObject
     * @Date: 2018/4/23 14:38
     */
    @PostMapping("/searchByIds")
    public JSONObject searchByIds(@RequestBody Map<String, Object> parameter) {
        logger.log(Level.INFO,"接口:searchByIds 被调用");
        JSONObject paramJson = JSONObject.parseObject(JSON.toJSONString(parameter));
        logger.log(Level.INFO,"接口接收的参数:"+paramJson.toJSONString());

        JSONObject param = new JSONObject();
        List<String> idsList = (List<String>) parameter.get(SysConsts.IDS);
        String datatype = (String)parameter.get(SysConsts.DATATYPE);
        List<String> backfields = (List<String>) parameter.get(SysConsts.BACKFIELDS);
        if (idsList != null && idsList.size() != 0) {
            JSONArray tempArr = new JSONArray();
            for (String e : idsList) {
                tempArr.add(e);
            }
            param.put(SysConsts.IDS, tempArr);
        } else {
            return new JSONObject();
        }
        param.put(SysConsts.DATATYPE,datatype);
        if (backfields != null) {
            JSONArray tempArr = new JSONArray();
            for (String e : backfields) {
                tempArr.add(e);
            }
            param.put(SysConsts.BACKFIELDS, tempArr);
        }
        JSONObject result = elasticSearchService.searchByIds(param);

        logger.log(Level.INFO,"接口返回结果"+result.toJSONString());

        return result;
    }

    /**
     * @Author:weiguangwu
     * @Description:返回该参数对应结果总数
     * @params:[parameter]
     * @return: com.alibaba.fastjson.JSONObject
     * @Date: 2018/4/23 15:14
     */
    @PostMapping("/searchtotal")
    public JSONObject searchtotal(@RequestBody Map<String, Object> parameter) {
        logger.log(Level.INFO,"接口:searchtotal 被调用");
        JSONObject paramJson = JSONObject.parseObject(JSON.toJSONString(parameter));
        logger.log(Level.INFO,"接口接收的参数:"+paramJson.toJSONString());

        JSONObject param = InfoSupplyerTool.formatParameter(parameter);

        logger.log(Level.INFO,"格式化后的参数:"+param.toJSONString());

        DataTypeEnum type = DataTypeEnum.DICOM;
        JSONObject result = elasticSearchService.searchTotalRecord(param,type);

        logger.log(Level.INFO,"接口返回结果"+result.toJSONString());

        return result;
    }

    /**
     * @Author:weiguangwu
     * @Description:返回es数据库中所有数据，慎用
     * @return: java.lang.String
     * @Date: 2018/4/23 15:16
     */
    @RequestMapping("/searchall")
    public String searchall() {
        logger.log(Level.INFO,"接口:searchall 被调用");
//        elasticSearchService.searchAll();

        return "this is searchall";
    }


    /**
     * 一定要查询数据库，因为这个人可能只有电信号或者量表，没有dicom，这个接口暂时没有使用
     * @param parameter
     * @return
     */
    @PostMapping("/getpatients")
    public JSONObject searchPatients(@RequestBody Map<String, Object> parameter){
        logger.log(Level.INFO,"接口:getpatients 被调用");
        JSONObject paramJson = JSONObject.parseObject(JSON.toJSONString(parameter));
        logger.log(Level.INFO,"接口接收的参数:"+paramJson.toJSONString());

        String patientname = (String)parameter.get(SysConsts.PATIENTNAME);
        List<Patient> patients = patientDao.getPatientByName(patientname);
        JSONObject result = new JSONObject();
        result.put(SysConsts.CODE,SysConsts.CODE_000);
        if(patients==null)
            result.put(SysConsts.TOTAL,0L);
        else
            result.put(SysConsts.TOTAL,patients.size());
        result.put(SysConsts.DATA,patients);

        logger.log(Level.INFO,"接口返回结果"+result.toJSONString());

        return result;
    }

    /**
     * 列出dicom表中的某个字段的值域，即某个字段所有种类的值
     * @param parameter
     * @return
     */
    @PostMapping("/listValueRange")
    public JSONObject listValueRange(@RequestBody Map<String, Object> parameter){
        logger.log(Level.INFO,"接口:listValueRange 被调用");
        JSONObject paramJson = JSONObject.parseObject(JSON.toJSONString(parameter));
        logger.log(Level.INFO,"listValueRange:"+paramJson.toJSONString());

        JSONObject result = new JSONObject();
        String datatype = (String)parameter.get(SysConsts.DATATYPE);
        String field = (String)parameter.get(SysConsts.FIELD_PARAM);

        if(SysConsts.TYPE_DICOM.equals(datatype)){
            if(!StringUtils.isBlank(field)){
                JSONObject tempResult = elasticSearchService.searchAggregation(infoConf.getIndexDicom(),infoConf.getTypeDicom(),null,field);
                if(SysConsts.CODE_000.equals(tempResult.getString(SysConsts.CODE))){
                    result.put(SysConsts.CODE,SysConsts.CODE_000);
                    result.put(SysConsts.TOTAL,tempResult.getLong(SysConsts.TOTAL));
                    result.put(SysConsts.DATA,tempResult.getJSONArray(SysConsts.DATA));
                }else{
                    result.put(SysConsts.CODE,SysConsts.CODE_999);
                    result.put(SysConsts.ERROR,"查询失败");
                }
            }
        }

        logger.log(Level.INFO,"接口返回结果"+result.toJSONString());

        return result;
    }
}
