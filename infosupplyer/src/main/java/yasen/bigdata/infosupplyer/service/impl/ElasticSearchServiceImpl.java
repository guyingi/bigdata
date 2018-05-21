package yasen.bigdata.infosupplyer.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;
import yasen.bigdata.infosupplyer.conf.InfosupplyerConfiguration;
import yasen.bigdata.infosupplyer.consts.ESConstant;
import yasen.bigdata.infosupplyer.consts.SysConstants;
import yasen.bigdata.infosupplyer.factory.EsClientFactory;
import yasen.bigdata.infosupplyer.pojo.IdSearchParamBean;
import yasen.bigdata.infosupplyer.pojo.PageSearchParamBean;
import yasen.bigdata.infosupplyer.service.ElasticSearchService;

import java.util.*;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package yasen.bigdata.infosupplyer.service.impl
 * @Description: ${todo}
 * @date 2018/5/17 18:12
 */
@Service("ElasticSearchService")
public class ElasticSearchServiceImpl implements ElasticSearchService {


    @Override
    public JSONObject searchByPaging(JSONObject param) {
        PageSearchParamBean pageSearchParamBean = new PageSearchParamBean(param);
        if(!pageSearchParamBean.isSearchconditionAvailable()){
            return createErrorMsg(SysConstants.CODE_010,"searchByPaging","查询条件参数为空");
        }else if(pageSearchParamBean.isParseError()){
            return createErrorMsg(SysConstants.CODE_011,"searchByPaging","参数解析错误");
        }

        InfosupplyerConfiguration conf = new InfosupplyerConfiguration();
        TransportClient transportClient = EsClientFactory.getTransportClient();

        //创建查询条件
        QueryBuilder queryBuilder = createQueryBuilder(pageSearchParamBean);
        //封装请求
        SearchRequestBuilder searchRequestBuilder = createSearchRequestBuilder(transportClient, pageSearchParamBean,
                conf, queryBuilder);
        //拿取结果
        JSONObject result = createResult(transportClient,searchRequestBuilder,
                pageSearchParamBean.getBackfields(),pageSearchParamBean.isPaging(),pageSearchParamBean.getPagesize());

        return result;
    }

    private QueryBuilder createQueryBuilder(PageSearchParamBean pageSearchParamBean){
        if(!pageSearchParamBean.isSearchconditionAvailable()){
            return QueryBuilders.matchAllQuery();
        }

        List<QueryBuilder> matchQueryList = new ArrayList<QueryBuilder>();
        //创建查询条件
        //精确值的字段：MRI序列，性别
        //如果有该查询条件则加到querybuilder中，否则则不加
        //医院
        if(pageSearchParamBean.isInstitutionAvailable()){
            //此处使用termsQuery(String name,String value)精确匹配单个值
            matchQueryList.add(QueryBuilders.matchQuery(ESConstant.InstitutionName_ES, pageSearchParamBean.getInstitution()));
        }
        //器官精确查询
        if(pageSearchParamBean.isOrganAvailable()){
            matchQueryList.add(QueryBuilders.termQuery(ESConstant.ORGAN_ES,pageSearchParamBean.getOrgan()));
        }
        //序列描述
        if(pageSearchParamBean.isSeriesdescriptionAvailable()){
            matchQueryList.add(QueryBuilders.matchQuery(ESConstant.SeriesDescription_ES,pageSearchParamBean.getSeriesdescription()));
        }
        //设备
        if(pageSearchParamBean.isDeviceAvailable()){
            if(pageSearchParamBean.isdevicePhrase()){
                matchQueryList.add(QueryBuilders.matchPhraseQuery(ESConstant.ManufacturersModelName_ES,pageSearchParamBean.getDevice()));
            }
            matchQueryList.add(QueryBuilders.matchQuery(ESConstant.ManufacturersModelName_ES,pageSearchParamBean.getDevice()));
        }
        //性别
        if(pageSearchParamBean.isSexAvailable()){
            matchQueryList.add(QueryBuilders.matchQuery(ESConstant.PatientsSex_ES,pageSearchParamBean.getSex()));
        }

        //年龄段
        if(pageSearchParamBean.isAgeStartAvailable() || pageSearchParamBean.isAgeEndAvailable()){
            RangeQueryBuilder rangbuilder = QueryBuilders.rangeQuery(ESConstant.PatientsAge_ES);
            if(pageSearchParamBean.isAgeStartAvailable()){
                rangbuilder.gte(pageSearchParamBean.getAgeStart());
            }
            if(pageSearchParamBean.isAgeEndAvailable()){
                rangbuilder.lte(pageSearchParamBean.getAgeEnd());
            }
            matchQueryList.add(rangbuilder);
        }

        //检查日期段20170811-20180201
        if(pageSearchParamBean.isStudydateStartAvailable() || pageSearchParamBean.isStudydateEndAvailable()){
            RangeQueryBuilder rangbuilder = QueryBuilders.rangeQuery(ESConstant.SeriesDate_ES);
            if(pageSearchParamBean.isStudydateStartAvailable()){
                rangbuilder.gte(pageSearchParamBean.getStudydateStart());
            }
            if(pageSearchParamBean.isStudydateEndAvailable()){
                rangbuilder.lte(pageSearchParamBean.getStudydateEnd());
            }
            matchQueryList.add(rangbuilder);
        }

        //数据收入日期段
        if(pageSearchParamBean.isEntrydateStartAvailable() || pageSearchParamBean.isEntrydateEndAvailable()){
            RangeQueryBuilder rangbuilder = QueryBuilders.rangeQuery(ESConstant.ENTRYDATE_ES);
            if(pageSearchParamBean.isEntrydateStartAvailable()){
                rangbuilder.gte(pageSearchParamBean.getEntrydateStart());
            }
            if(pageSearchParamBean.isEntrydateEndAvailable()){
                rangbuilder.lte(pageSearchParamBean.getEntrydateEnd());
            }
            matchQueryList.add(rangbuilder);
        }

        if(pageSearchParamBean.isImagecountMinAvailable() || pageSearchParamBean.isImagecountMaxAvailable()){
            RangeQueryBuilder rangbuilder = QueryBuilders.rangeQuery(ESConstant.NumberOfSlices_ES);
            if(pageSearchParamBean.isImagecountMinAvailable()){
                rangbuilder.gte(pageSearchParamBean.getImagecountMin());
            }
            if(pageSearchParamBean.isImagecountMaxAvailable()){
                rangbuilder.lte(pageSearchParamBean.getImagecountMax());
            }
            matchQueryList.add(rangbuilder);
        }
        //层厚
        if(pageSearchParamBean.isSlicethicknessMinAvailable() || pageSearchParamBean.isSlicethicknessMaxAvailable()){
            RangeQueryBuilder rangbuilder = QueryBuilders.rangeQuery(ESConstant.SliceThickness_ES);
            if(pageSearchParamBean.isSlicethicknessMinAvailable()){
                rangbuilder.gte(pageSearchParamBean.getSlicethicknessMin());
            }
            if(pageSearchParamBean.isSlicethicknessMaxAvailable()){
                rangbuilder.lte(pageSearchParamBean.getSlicethicknessMax());
            }
            matchQueryList.add(rangbuilder);
        }

        // 等同于bool，将两个查询合并
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        for(QueryBuilder e : matchQueryList) {
            boolBuilder.must(e);
        }
        return boolBuilder;
    }

    private SearchRequestBuilder createSearchRequestBuilder(
            TransportClient transportClient,PageSearchParamBean pageSearchParamBean,InfosupplyerConfiguration conf,
            QueryBuilder queryBuilder){
        SearchRequestBuilder searchRequestBuilder = transportClient.prepareSearch(conf.getIndexDicom())
                .setTypes(conf.getTypeDicom())
                // 设置查询类型
                .setSearchType(SearchType.DEFAULT)
                // 设置查询关键词
                .setQuery(queryBuilder)
                // 设置是否按查询匹配度排序
                .setExplain(true);

        //设置排序字段
        if(pageSearchParamBean.isSortfieldsAvailable()){
            List<String> sortfields = pageSearchParamBean.getSortfields();
            for(String e : sortfields){
                searchRequestBuilder.addSort(new FieldSortBuilder(e).order(SortOrder.ASC));
            }
        }
        if(pageSearchParamBean.isPaging()){
            int pagesize = pageSearchParamBean.getPagesize();
            int pageid = pageSearchParamBean.getPageid();
            int from  = (pageid-1)*pagesize;
            searchRequestBuilder.setFrom(from).setSize(pagesize);
        }else{
            //设置保留分屏数据时间100s,如果不分页则设置scroll获取全部数据。
            searchRequestBuilder.setScroll(new TimeValue(100000)).setSize(1000);
        }
        return searchRequestBuilder;
    }


    /**
     * 拿取结果方法：
     * @param transportClient
     * @param searchRequestBuilder
     * @param backfields  结果中需要返回的字段
     * @param isPaging   是否分页
     * @param pagesize   如果分页，那么页的大小
     * @return
     */
    private JSONObject createResult(TransportClient transportClient, SearchRequestBuilder searchRequestBuilder,
                                    List<String> backfields,boolean isPaging,int pagesize){
        JSONObject result = new JSONObject();
        JSONArray data = new JSONArray();
        List<JSONObject> tempDataList = new LinkedList<JSONObject>();
        result.put(SysConstants.CODE,SysConstants.CODE_000);

        SearchResponse response = searchRequestBuilder.execute().actionGet();
        long totalHits = response.getHits().getTotalHits();

        if(isPaging){
            SearchHits searchHits = response.getHits();
            tempDataList = parseSearchHits(searchHits,backfields);
            result.put(SysConstants.PAGECONTENT,totalHits/pagesize+1);
        }else{
            SearchHits searchHits = response.getHits();
            tempDataList.addAll(parseSearchHits(searchHits,backfields));
            if(totalHits>1000) {
                int page = (int) totalHits / (1000);//计算总页数,每次搜索数量为分片数*设置的size大小
                for (int i = 0; i <= page; i++) {
                    //再次发送请求,并使用上次搜索结果的ScrollId
                    response = transportClient
                            .prepareSearchScroll(response.getScrollId())
                            .setScroll(new TimeValue(100000)).execute()
                            .actionGet();
                    tempDataList.addAll(parseSearchHits(searchHits,backfields));
                }
            }
        }
        for(JSONObject e : tempDataList){
            data.add(e);
        }

        result.put(SysConstants.TOTAL,totalHits);
        result.put(SysConstants.DATA,data);
        return result;
    }
    private List<JSONObject> parseSearchHits(SearchHits searchHits,List<String> backfields){
        List<JSONObject> tempList = new ArrayList<JSONObject>();
        JSONObject tempdata = null;
        for(SearchHit hit : searchHits.getHits()){
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            tempdata = new JSONObject();
            if(backfields!=null && backfields.size()!=0) {          //如果设定了返回字段，只返回指定的字段
                for (String backfield : backfields) {
                    Object o = sourceAsMap.get(backfield);
                    tempdata.put(backfield, o);
                }
            }else{                  //如果没有设定则返回所有字段
                for(Map.Entry<String,Object> entry : sourceAsMap.entrySet()){
                    tempdata.put(entry.getKey(),entry.getValue());
                }
            }
            tempdata.put("id",hit.getId());
            tempList.add(tempdata);
        }
        return tempList;
    }



    @Override
    public JSONObject searchTotalRecord(JSONObject param) {
        PageSearchParamBean pageSearchParamBean = new PageSearchParamBean(param);
        if(!pageSearchParamBean.isSearchconditionAvailable()){
            return createErrorMsg(SysConstants.CODE_010,"searchTotalRecord","查询条件参数为空");
        }else if(pageSearchParamBean.isParseError()){
            return createErrorMsg(SysConstants.CODE_011,"searchTotalRecord","参数解析错误");
        }

        InfosupplyerConfiguration conf = new InfosupplyerConfiguration();
        TransportClient transportClient = EsClientFactory.getTransportClient();
        //创建查询条件
        QueryBuilder queryBuilder = createQueryBuilder(pageSearchParamBean);
        //封装请求
        SearchRequestBuilder searchRequestBuilder = createSearchRequestBuilder(
                transportClient, pageSearchParamBean, conf, queryBuilder);
        long totalHits = searchRequestBuilder.execute().actionGet().getHits().getTotalHits();
        JSONObject result = new JSONObject();
        result.put(SysConstants.TOTAL,totalHits);
        return result;
    }

    @Override
    public JSONObject searchByIds(JSONObject param) {
        IdSearchParamBean idSearchParamBean = new IdSearchParamBean(param);
        if(!idSearchParamBean.isIdsAvailable()){
            return createErrorMsg(SysConstants.CODE_010,"searchByIds","id参数为空");
        }else if(idSearchParamBean.isParseError()){
            return createErrorMsg(SysConstants.CODE_011,"searchByIds","参数解析错误");
        }
        InfosupplyerConfiguration conf = new InfosupplyerConfiguration();
        TransportClient transportClient = EsClientFactory.getTransportClient();
        SearchRequestBuilder searchRequestBuilderForIds = createSearchRequestBuilderForIds(
                transportClient, idSearchParamBean, conf);
        JSONObject result = createResult(transportClient, searchRequestBuilderForIds, idSearchParamBean.getBackfields(),
                false,SysConstants.DEFAULT_PAGESIZE);
        return result;
    }

    @Override
    public JSONObject searchAll() {
        PageSearchParamBean pageSearchParamBean = new PageSearchParamBean(new JSONObject());

        InfosupplyerConfiguration conf = new InfosupplyerConfiguration();
        TransportClient transportClient = EsClientFactory.getTransportClient();

        //创建查询条件
        QueryBuilder queryBuilder = createQueryBuilder(pageSearchParamBean);
        //封装请求
        SearchRequestBuilder searchRequestBuilder = createSearchRequestBuilder(
                transportClient, pageSearchParamBean, conf, queryBuilder);
        //拿取结果
        JSONObject result = createResult(transportClient,searchRequestBuilder,
                pageSearchParamBean.getBackfields(),pageSearchParamBean.isPaging(),pageSearchParamBean.getPagesize());

        return result;
    }

    @Override
    public void updateField(String index, String type, String id,String field, String value) {
        TransportClient transportClient = EsClientFactory.getTransportClient();
        UpdateRequestBuilder updateRequestBuilder = transportClient.prepareUpdate(index, type, id);
        Map<String,String> map = new HashMap<String,String>();
        map.put(field,value);
        updateRequestBuilder.setUpsert(map);
        UpdateResponse updateResponse = updateRequestBuilder.execute().actionGet();
        System.out.println("update status:"+updateResponse.status());
    }

    @Override
    public Object getField(String index, String type, String id, String field) {
        TransportClient transportClient = EsClientFactory.getTransportClient();
        IdsQueryBuilder idsQueryBuilder = QueryBuilders.idsQuery(type);
        idsQueryBuilder.addIds(id);

        SearchRequestBuilder searchRequestBuilder = transportClient.prepareSearch(index)
                .setTypes(type)
                .setQuery(idsQueryBuilder)
                .setSearchType(SearchType.DEFAULT)
                .setSize(1000)
                // 设置是否按查询匹配度排序
                .setExplain(false);
        SearchResponse response = searchRequestBuilder.execute().actionGet();
        long len = response.getHits().getTotalHits();
        for(SearchHit hit : response.getHits().getHits()){
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            Object fieldValue = sourceAsMap.get(field);
            return (String)fieldValue;
        }
        return null;
    }

    private SearchRequestBuilder createSearchRequestBuilderForIds(
            TransportClient transportClient,IdSearchParamBean idSearchParamBean,InfosupplyerConfiguration conf){
        String[] params = new String[idSearchParamBean.getIds().size()];
        idSearchParamBean.getIds().toArray(params);
        QueryBuilder queryBuilder = QueryBuilders.idsQuery(conf.getTypeDicom());
        ((IdsQueryBuilder) queryBuilder).addIds(params);

        SearchRequestBuilder searchRequestBuilder = transportClient.prepareSearch(conf.getIndexDicom())
                .setTypes(conf.getTypeDicom())
                .setQuery(queryBuilder)
                .setSearchType(SearchType.DEFAULT)
                .setScroll(new TimeValue(100000))
                .setSize(1000)
                // 设置是否按查询匹配度排序
                .setExplain(false);
        return searchRequestBuilder;
    }

    @Override
    public int insertOne(String index,String type,String id,JSONObject docJson) {
        if(index==null||type==null){
            return SysConstants.FAILED;
        }

        TransportClient transportClient = EsClientFactory.getTransportClient();
        BulkRequestBuilder bulkRequest = transportClient.prepareBulk().setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        Map resultMap = new HashMap();

        //没有指定idName 那就让Elasticsearch自动生成
        if(id!=null && id.length()!=0){
//            if(isExists(idValue)){ 这里其实不需要判断是否已经存在，如果存在ES默认是替换旧的
//                return UploaderConstants.EXISTS;
//            }
            IndexRequestBuilder lrb = transportClient.prepareIndex(index, type,id).setSource(docJson);
            bulkRequest.add(lrb);
        }else{
//            DataUploaderTool.recordLog(logger,"",false,"id自动生成");
//            return UploaderConstants.FAILED;
            IndexRequestBuilder lrb = transportClient.prepareIndex(index, type).setSource(docJson);
            bulkRequest.add(lrb);
        }
        BulkResponse bulkResponse = bulkRequest.execute().actionGet();

        if (bulkResponse.hasFailures()) {
            System.out.println("message"+bulkResponse.buildFailureMessage());
            return SysConstants.FAILED;
        }
        return SysConstants.SUCCESS;
    }

    @Override
    public int insertOne(String index, String type, String id, Map<String, String> metaMsg) {
        JSONObject metaJSON = new JSONObject();
        for(Map.Entry<String,String> entry : metaMsg.entrySet()){
            metaJSON.put(entry.getKey(),entry.getValue());
        }
        return insertOne(index, type, id, metaJSON);
    }

    private JSONObject createErrorMsg(String code,String interfaceStr,String msg){
        JSONObject result = new JSONObject();
        result.put(SysConstants.CODE,code);
        JSONObject error = new JSONObject();
        error.put(SysConstants.MSG,msg);
        error.put(SysConstants.INTERFACE,interfaceStr);
        result.put(SysConstants.ERROR,error);
        return result;
    }
}
