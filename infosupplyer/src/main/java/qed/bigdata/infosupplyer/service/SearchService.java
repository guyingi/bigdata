package qed.bigdata.infosupplyer.service;

/**
 * @Title: SearchServiceImpl.java
 * @Package yasen.bigdata.infosupplyer.service.impl
 * @Description: SearchService接口类
 * @author weiguangwu
 * @date  2018/4/23 14:13
 * @version V1.0
 */

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public interface SearchService {


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
    JSONObject searchByPaging(JSONObject param);

    /**
     * 介绍：传入查询条件参数，返回结果总数据
     * @param  param
     * @return ES中查询的结果总数量
     */
    JSONObject searchTotalRecord(JSONObject param);


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

}
