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
import yasen.bigdata.infosupplyer.consts.SysConstants;
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
        JSONObject param = InfoSupplyerTool.formatParameter(parameter);
        System.out.println(param.toJSONString());
        JSONObject jsonObject = elasticSearchService.searchByPaging(param);
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
        JSONObject jsonObject = elasticSearchService.searchTotalRecord(param);
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

}
