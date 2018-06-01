package yasen.bigdata.infosupplyer.service;

import com.alibaba.fastjson.JSONObject;
import yasen.bigdata.infosupplyer.consts.DataTypeEnum;

import java.util.Map;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package yasen.bigdata.infosupplyer.service
 * @Description: ${todo}
 * @date 2018/5/17 18:12
 */
public interface ElasticSearchService {
    /**
     * 分页查询接口：传入查询条件参数，和指定页码，页大小，返回数据种类字段，需排序字段种类
     * 如果页码字段为空，返回第一页
     * 如果页大小超过结果总数，返回第一页
     * 如果页码不在正常值范围内，返回空json对象
     * 返回字段参数为空，返回默认的一些字段
     * 如果返回字段参数不为空，返回参数中指定的参数。
     * 排序字段参数为空，则不排序；如果排序字段参数不为空，则排序
     * 参数：
     * searchcondition:可缺省
     * pageid:可缺省
     * pagesize:可缺省
     * backfields:可缺省
     * sortfields:可缺省
     * @return 输入条件返回该页数据
     * {total:16,
     * pagecount:4,
     * data:[{},{},{}}]
     * }
     */
    JSONObject searchByPaging(JSONObject param, DataTypeEnum type);

    /**
     * 介绍：传入查询条件参数，返回结果总数据
     * @param  param
     * @return ES中查询的结果总数量
     */
    JSONObject searchTotalRecord(JSONObject param, DataTypeEnum type);


    /**
     * 传入一批id号，将该id号对应的数据查出来，参数中包含返回字段。
     * 参数：
     *
     * @param param
     * @return
     */
    JSONObject searchByIds(JSONObject param);

    /**
     * 查询所有数据所有字段。
     * @return
     */
    JSONObject searchAll();

    /**
     * 更新指定index,type,field的值
     * @param index
     * @param type
     * @param field
     * @param value
     */
    void  updateField(String index,String type,String id,String field,String value);


    /**
     * 查询ES中指定index,type,id,field的值
     * @param index
     * @param type
     * @param id
     * @param field
     * @return
     */
    Object getField(String index, String type, String id, String field);

    /**
     * 在ES指定index,type,id定位的doc插入或者更新某个field,参数为json
     * @param index
     * @param type
     * @param id
     * @param metaMsg
     * @return
     */
    int insertOne(String index,String type,String id,JSONObject metaMsg);

    /**
     * 在ES指定index,type,id定位的doc插入或者更新某个field,参数为map
     * @param index
     * @param type
     * @param id
     * @param metaMsg
     * @return
     */
    int insertOne(String index,String type,String id,Map<String,String> metaMsg);

    /**
     * ES聚合查询，在ES指定index,type中查询符合条件searchcondition的记录，根据aggrfield返回sum聚合
     * @param index
     * @param type
     * @param searchcondition  查询条件
     * @param aggrfield 聚合字段sum聚合
     * @return
     */
    JSONObject searchAggregation(String index,String type,Map<String,String> searchcondition,String aggrfield);

//    JSONObject searchElectricSignal()

}
