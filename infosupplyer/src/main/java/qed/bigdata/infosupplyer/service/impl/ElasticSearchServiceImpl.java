package qed.bigdata.infosupplyer.service.impl;

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
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;
import qed.bigdata.infosupplyer.conf.InfosupplyerConfiguration;
import qed.bigdata.infosupplyer.consts.DataTypeEnum;
import qed.bigdata.infosupplyer.consts.EsConsts;
import qed.bigdata.infosupplyer.consts.SysConsts;
import qed.bigdata.infosupplyer.pojo.IdSearchParamBean;
import qed.bigdata.infosupplyer.pojo.PageSearchParamBean;
import qed.bigdata.infosupplyer.factory.EsClientFactory;
import qed.bigdata.infosupplyer.service.ElasticSearchService;

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

    InfosupplyerConfiguration conf = null;

    public ElasticSearchServiceImpl(){
        conf = new InfosupplyerConfiguration();
    }

    @Override
    public JSONObject searchByPaging(JSONObject param, DataTypeEnum type) {
        PageSearchParamBean pageSearchParamBean = new PageSearchParamBean(param);
        JSONObject result = null;
        TransportClient transportClient = EsClientFactory.getTransportClient();

        //创建查询条件

        if(type == DataTypeEnum.DICOM){
            if(!pageSearchParamBean.isCriteriaAvailable()){
                return createErrorMsg(SysConsts.CODE_010,"searchByPaging","查询条件参数为空");
            }else if(pageSearchParamBean.isParseError()){
                return createErrorMsg(SysConsts.CODE_011,"searchByPaging","参数解析错误");
            }
            //创建查询条件
            QueryBuilder queryBuilder = createQueryBuilder(pageSearchParamBean);
            //封装请求
            SearchRequestBuilder searchRequestBuilder = createSearchRequestBuilder(transportClient, pageSearchParamBean,
                    conf.getIndexDicom(),conf.getTypeDicom(), queryBuilder);
            //拿取结果
            result = createResult(transportClient,searchRequestBuilder,
                    pageSearchParamBean.getBackfields(),pageSearchParamBean.isPaging(),pageSearchParamBean.getPagesize());
        }else if(type == DataTypeEnum.ELECTRIC){
            if(!pageSearchParamBean.isCriteriaAvailable()){
                return createErrorMsg(SysConsts.CODE_010,"searchByPaging","查询条件参数为空");
            }else if(pageSearchParamBean.isParseError()){
                return createErrorMsg(SysConsts.CODE_011,"searchByPaging","参数解析错误");
            }
            //创建查询条件
            QueryBuilder queryBuilder = createQueryBuilder(pageSearchParamBean);
            //封装请求
            SearchRequestBuilder searchRequestBuilder = createSearchRequestBuilder(transportClient, pageSearchParamBean,
                    conf.getIndexElectric(),conf.getTypeElectric(), queryBuilder);
            //拿取结果
            result = createResult(transportClient,searchRequestBuilder,
                    pageSearchParamBean.getBackfields(),pageSearchParamBean.isPaging(),pageSearchParamBean.getPagesize());
        }
        return result;
    }

    private QueryBuilder createQueryBuilder(PageSearchParamBean pageSearchParamBean){
        if(!pageSearchParamBean.isCriteriaAvailable()){
            return QueryBuilders.matchAllQuery();
        }

        List<QueryBuilder> matchQueryList = new ArrayList<QueryBuilder>();
        JSONArray criteria = pageSearchParamBean.getCriteria();

        int size = criteria.size();
        for(int i=0; i<size; i++){
            JSONObject obj = criteria.getJSONObject(i);
            Boolean isSection = obj.getBoolean(SysConsts.IS_SECTION);
            String keyword = obj.getString(SysConsts.KEYWORD);
            if(isSection){
                RangeQueryBuilder rangbuilder = QueryBuilders.rangeQuery(keyword);
                if(EsConsts.IntegerFieldList.contains(keyword)) {
                    if (obj.getBoolean(SysConsts.IS_START_AVAILABLE)) {
                        rangbuilder.gte(obj.getInteger(SysConsts.START));
                    }
                    if (obj.getBoolean(SysConsts.IS_END_AVAILABLE)) {
                        rangbuilder.lte(obj.getInteger(SysConsts.END));
                    }
                }else if(EsConsts.LongFieldList.contains(keyword)){
                    if (obj.getBoolean(SysConsts.IS_START_AVAILABLE)) {
                        rangbuilder.gte(obj.getLong(SysConsts.START));
                    }
                    if (obj.getBoolean(SysConsts.IS_END_AVAILABLE)) {
                        rangbuilder.lte(obj.getLong(SysConsts.END));
                    }
                }else if(EsConsts.DoubleFieldList.contains(keyword)){
                    if (obj.getBoolean(SysConsts.IS_START_AVAILABLE)) {
                        rangbuilder.gte(obj.getDouble(SysConsts.START));
                    }
                    if (obj.getBoolean(SysConsts.IS_END_AVAILABLE)) {
                        rangbuilder.lte(obj.getDouble(SysConsts.END));
                    }
                }else{
                    if (obj.getBoolean(SysConsts.IS_START_AVAILABLE)) {
                        rangbuilder.gte(obj.getDouble(SysConsts.START));
                    }
                    if (obj.getBoolean(SysConsts.IS_END_AVAILABLE)) {
                        rangbuilder.lte(obj.getDouble(SysConsts.END));
                    }
                }
                matchQueryList.add(rangbuilder);
            }else{
                if(EsConsts.IntegerFieldList.contains(keyword)) {
                    matchQueryList.add(QueryBuilders.matchQuery(keyword, obj.getInteger(SysConsts.VALUE)));
                }else if(EsConsts.LongFieldList.contains(keyword)){
                    matchQueryList.add(QueryBuilders.matchQuery(keyword, obj.getLong(SysConsts.VALUE)));
                }else if(EsConsts.DoubleFieldList.contains(keyword)){
                    matchQueryList.add(QueryBuilders.matchQuery(keyword, obj.getDouble(SysConsts.VALUE)));
                }else {
                    matchQueryList.add(QueryBuilders.matchQuery(keyword, obj.getString(SysConsts.VALUE)));
                }
            }
        }
        // 等同于bool，将两个查询合并
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        for(QueryBuilder e : matchQueryList) {
            boolBuilder.must(e);
        }
        return boolBuilder;
    }

//    private QueryBuilder createQueryBuilderForDicom(PageSearchParamBean pageSearchParamBean){
//        if(!pageSearchParamBean.isCriteriaAvailable()){
//            return QueryBuilders.matchAllQuery();
//        }
//
//        List<QueryBuilder> matchQueryList = new ArrayList<QueryBuilder>();
//        JSONArray criteria = pageSearchParamBean.getCriteria();
//
//        int size = criteria.size();
//        for(int i=0; i<size; i++){
//            JSONObject obj = criteria.getJSONObject(i);
//            Boolean isSection = obj.getBoolean(SysConsts.SECTION);
//            String keyword = obj.getString(SysConsts.KEYWORD);
//            if(isSection){
//                RangeQueryBuilder rangbuilder = QueryBuilders.rangeQuery(keyword);
//                if(EsConsts.IntegerFieldList.contains(keyword)) {
//                    if (obj.getBoolean(SysConsts.IS_START_AVAILABLE)) {
//                        rangbuilder.gte(obj.getInteger(SysConsts.START));
//                    }
//                    if (obj.getBoolean(SysConsts.IS_END_AVAILABLE)) {
//                        rangbuilder.lte(obj.getInteger(SysConsts.END));
//                    }
//                }else if(EsConsts.LongFieldList.contains(keyword)){
//                    if (obj.getBoolean(SysConsts.IS_START_AVAILABLE)) {
//                        rangbuilder.gte(obj.getLong(SysConsts.START));
//                    }
//                    if (obj.getBoolean(SysConsts.IS_END_AVAILABLE)) {
//                        rangbuilder.lte(obj.getLong(SysConsts.END));
//                    }
//                }else if(EsConsts.DoubleFieldList.contains(keyword)){
//                    if (obj.getBoolean(SysConsts.IS_START_AVAILABLE)) {
//                        rangbuilder.gte(obj.getDouble(SysConsts.START));
//                    }
//                    if (obj.getBoolean(SysConsts.IS_END_AVAILABLE)) {
//                        rangbuilder.lte(obj.getDouble(SysConsts.END));
//                    }
//                }
//                matchQueryList.add(rangbuilder);
//            }else{
//                if(EsConsts.IntegerFieldList.contains(keyword)) {
//                    matchQueryList.add(QueryBuilders.matchQuery(keyword, obj.getInteger(SysConsts.VALUE)));
//                }else if(EsConsts.LongFieldList.contains(keyword)){
//                    matchQueryList.add(QueryBuilders.matchQuery(keyword, obj.getLong(SysConsts.VALUE)));
//                }else if(EsConsts.DoubleFieldList.contains(keyword)){
//                    matchQueryList.add(QueryBuilders.matchQuery(keyword, obj.getDouble(SysConsts.VALUE)));
//                }
//            }
//        }
//        // 等同于bool，将两个查询合并
//        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
//        for(QueryBuilder e : matchQueryList) {
//            boolBuilder.must(e);
//        }
//        return boolBuilder;

/*
模板
   //标签
        if(dicomSearchCriteriaBean.isTagAvailable()){
            matchQueryList.add(QueryBuilders.matchQuery(EsConsts.TAG_ES_DCM, dicomSearchCriteriaBean.getTag()));
        }
        //设备
        if(dicomSearchCriteriaBean.isDeviceAvailable()){
            if(dicomSearchCriteriaBean.isdevicePhrase()){
                matchQueryList.add(QueryBuilders.matchPhraseQuery(EsConsts.ManufacturersModelName_ES_DCM,dicomSearchCriteriaBean.getDevice()));
            }
            matchQueryList.add(QueryBuilders.matchQuery(EsConsts.ManufacturersModelName_ES_DCM,dicomSearchCriteriaBean.getDevice()));
        }
        //性别
        if(dicomSearchCriteriaBean.isSexAvailable()){
            matchQueryList.add(QueryBuilders.matchQuery(EsConsts.PatientsSex_ES_DCM,dicomSearchCriteriaBean.getSex()));
        }
        //年龄段
        if(dicomSearchCriteriaBean.isAgeStartAvailable() || dicomSearchCriteriaBean.isAgeEndAvailable()){
            RangeQueryBuilder rangbuilder = QueryBuilders.rangeQuery(EsConsts.PatientsAge_ES_DCM);
            if(dicomSearchCriteriaBean.isAgeStartAvailable()){
                rangbuilder.gte(dicomSearchCriteriaBean.getAgeStart());
            }
            if(dicomSearchCriteriaBean.isAgeEndAvailable()){
                rangbuilder.lte(dicomSearchCriteriaBean.getAgeEnd());
            }
            matchQueryList.add(rangbuilder);
        }*/
//    }

    /*private QueryBuilder createQueryBuilderForElectric(PageSearchParamBean pageSearchParamBean){
        if(!pageSearchParamBean.isCriteriaAvailable()){
            return QueryBuilders.matchAllQuery();
        }

        List<QueryBuilder> matchQueryList = new ArrayList<QueryBuilder>();
        ElectricSignalSearchCriteriaBean electricSignalSearchCriteriaBean = pageSearchParamBean.getElectricSignalSearchCriteriaBean();
        //PatientUID精确查询
        if(electricSignalSearchCriteriaBean.isPatientUIDAvailable()){
            matchQueryList.add(QueryBuilders.matchQuery(EsConsts.PatientUID_ES_ELECTRIC, electricSignalSearchCriteriaBean.getPatientUID()));
        }
        //PatientName精确查询
        if(electricSignalSearchCriteriaBean.isPatientNameAvailable()){
            matchQueryList.add(QueryBuilders.matchQuery(EsConsts.PatientName_ES_ELECTRIC, electricSignalSearchCriteriaBean.getPatientName()));
        }
        //PatientsAge范围查询
        if(electricSignalSearchCriteriaBean.isAgeStartAvailable() || electricSignalSearchCriteriaBean.isAgeEndAvailable()){
            RangeQueryBuilder rangbuilder = QueryBuilders.rangeQuery(EsConsts.PatientsAge_ES_ELECTRIC);
            if(electricSignalSearchCriteriaBean.isAgeStartAvailable()){
                rangbuilder.gte(electricSignalSearchCriteriaBean.getAgeStart());
            }
            if(electricSignalSearchCriteriaBean.isAgeEndAvailable()){
                rangbuilder.lte(electricSignalSearchCriteriaBean.getAgeEnd());
            }
            matchQueryList.add(rangbuilder);
        }

        //createdate范围查询
        if(electricSignalSearchCriteriaBean.isCreatedateStartAvailable() || electricSignalSearchCriteriaBean.isCreatedateEndAvailable()){
            RangeQueryBuilder rangbuilder = QueryBuilders.rangeQuery(EsConsts.CreateDate_ES_ELECTRIC);
            if(electricSignalSearchCriteriaBean.isCreatedateStartAvailable()){
                rangbuilder.gte(electricSignalSearchCriteriaBean.getCreatedateStart());
            }
            if(electricSignalSearchCriteriaBean.isCreatedateEndAvailable()){
                rangbuilder.lte(electricSignalSearchCriteriaBean.getCreatedateEnd());
            }
            matchQueryList.add(rangbuilder);
        }
        //InstitutionName精确查询
        if(electricSignalSearchCriteriaBean.isInstitutionNameAvailable()){
            matchQueryList.add(QueryBuilders.matchQuery(EsConsts.InstitutionName_ES_ELECTRIC, electricSignalSearchCriteriaBean.getInstitutionName()));
        }
        //entrydate范围查询
        if(electricSignalSearchCriteriaBean.isEntrydateStartAvailable() || electricSignalSearchCriteriaBean.isEntrydateEndAvailable()){
            RangeQueryBuilder rangbuilder = QueryBuilders.rangeQuery(EsConsts.ENTRYDATE_ES_ELECTRIC);
            if(electricSignalSearchCriteriaBean.isEntrydateStartAvailable()){
                rangbuilder.gte(electricSignalSearchCriteriaBean.getEntrydateStart());
            }
            if(electricSignalSearchCriteriaBean.isEntrydateEndAvailable()){
                rangbuilder.lte(electricSignalSearchCriteriaBean.getEntrydateEnd());
            }
            matchQueryList.add(rangbuilder);
        }
        // 等同于bool，将两个查询合并
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        for(QueryBuilder e : matchQueryList) {
            boolBuilder.must(e);
        }
        return boolBuilder;
    }*/

    private SearchRequestBuilder createSearchRequestBuilder(
            TransportClient transportClient,PageSearchParamBean pageSearchParamBean,String index,String type,
            QueryBuilder queryBuilder){
        SearchRequestBuilder searchRequestBuilder = transportClient.prepareSearch(index)
                .setTypes(type)
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
        result.put(SysConsts.CODE,SysConsts.CODE_000);

        SearchResponse response = searchRequestBuilder.execute().actionGet();
        long totalHits = response.getHits().getTotalHits();

        if(isPaging){
            SearchHits searchHits = response.getHits();
            tempDataList = parseSearchHits(searchHits,backfields);
            result.put(SysConsts.PAGECONTENT,totalHits/pagesize+1);
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

        result.put(SysConsts.TOTAL,totalHits);
        result.put(SysConsts.DATA,data);
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
    public JSONObject searchTotalRecord(JSONObject param, DataTypeEnum type) {
        PageSearchParamBean pageSearchParamBean = new PageSearchParamBean(param);
        if(!pageSearchParamBean.isCriteriaAvailable()){
            return createErrorMsg(SysConsts.CODE_010,"searchTotalRecord","查询条件参数为空");
        }else if(pageSearchParamBean.isParseError()){
            return createErrorMsg(SysConsts.CODE_011,"searchTotalRecord","参数解析错误");
        }

        TransportClient transportClient = EsClientFactory.getTransportClient();
        JSONObject result = new JSONObject();
        if(type == DataTypeEnum.DICOM){

            //创建查询条件
            QueryBuilder queryBuilder = createQueryBuilder(pageSearchParamBean);
            //封装请求
            SearchRequestBuilder searchRequestBuilder = createSearchRequestBuilder(
                    transportClient, pageSearchParamBean, conf.getIndexDicom(),conf.getTypeDicom(), queryBuilder);
            long totalHits = searchRequestBuilder.execute().actionGet().getHits().getTotalHits();
            result = new JSONObject();
            result.put(SysConsts.TOTAL,totalHits);
        }else if(type == DataTypeEnum.ELECTRIC){
            //创建查询条件
            QueryBuilder queryBuilder = createQueryBuilder(pageSearchParamBean);
            //封装请求
            SearchRequestBuilder searchRequestBuilder = createSearchRequestBuilder(
                    transportClient, pageSearchParamBean, conf.getIndexDicom(),conf.getTypeDicom(), queryBuilder);
            long totalHits = searchRequestBuilder.execute().actionGet().getHits().getTotalHits();
            result.put(SysConsts.CODE,SysConsts.CODE_000);
            result.put(SysConsts.TOTAL,totalHits);
        }
        return result;
    }

    @Override
    public JSONObject searchByIds(JSONObject param) {
        IdSearchParamBean idSearchParamBean = new IdSearchParamBean(param);
        if(!idSearchParamBean.isIdsAvailable()){
            return createErrorMsg(SysConsts.CODE_010,"searchByIds","id参数为空");
        }else if(idSearchParamBean.isParseError()){
            return createErrorMsg(SysConsts.CODE_011,"searchByIds","参数解析错误");
        }

        JSONObject result = null;

        InfosupplyerConfiguration conf = new InfosupplyerConfiguration();
        TransportClient transportClient = EsClientFactory.getTransportClient();
        if(DataTypeEnum.DICOM == idSearchParamBean.getType()){
            SearchRequestBuilder searchRequestBuilderForIds = createSearchRequestBuilderForIds(
                    transportClient, idSearchParamBean, conf.getIndexDicom(),conf.getTypeDicom());
            result = createResult(transportClient, searchRequestBuilderForIds, idSearchParamBean.getBackfields(),
                    false,SysConsts.DEFAULT_PAGESIZE);
        }else if(DataTypeEnum.ELECTRIC == idSearchParamBean.getType()){
            SearchRequestBuilder searchRequestBuilderForIds = createSearchRequestBuilderForIds(
                    transportClient, idSearchParamBean, conf.getIndexElectric(),conf.getTypeElectric());
            result = createResult(transportClient, searchRequestBuilderForIds, idSearchParamBean.getBackfields(),
                    false,SysConsts.DEFAULT_PAGESIZE);
        }

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
                transportClient, pageSearchParamBean, conf.getIndexDicom(),conf.getTypeDicom(), queryBuilder);
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
        updateRequestBuilder.setDoc(map);
        UpdateResponse updateResponse = updateRequestBuilder.execute().actionGet();
        System.out.println("update status:"+updateResponse.status());
    }

    @Override
    public Object getFieldById(String index, String type, String id, String field) {
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
            TransportClient transportClient,IdSearchParamBean idSearchParamBean,String index,String type){
        String[] params = new String[idSearchParamBean.getIds().size()];
        idSearchParamBean.getIds().toArray(params);
        QueryBuilder queryBuilder = QueryBuilders.idsQuery(type);
        ((IdsQueryBuilder) queryBuilder).addIds(params);

        SearchRequestBuilder searchRequestBuilder = transportClient.prepareSearch(index)
                .setTypes(type)
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
            return SysConsts.FAILED;
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
            return SysConsts.FAILED;
        }
        return SysConsts.SUCCESS;
    }

    @Override
    public int insertOne(String index, String type, String id, Map<String, String> metaMsg) {
        JSONObject metaJSON = new JSONObject();
        for(Map.Entry<String,String> entry : metaMsg.entrySet()){
            metaJSON.put(entry.getKey(),entry.getValue());
        }
        return insertOne(index, type, id, metaJSON);
    }

    @Override
    public JSONObject searchAggregation(String index, String type,Map<String,String> searchcondition, String aggrfield) {
        TransportClient transportClient = EsClientFactory.getTransportClient();

//        AggregationBuilders
//                .global("agg")
//                .subAggregation(AggregationBuilders.terms("genders").field("gender"));
        AbstractAggregationBuilder aggregation = AggregationBuilders.terms("per_count").field(aggrfield+".keyword");
        SearchRequestBuilder searchRequestBuilder = transportClient.prepareSearch(index).setTypes(type)
                .addAggregation(aggregation);

        if(searchcondition != null && searchcondition.size()!=0) {
            BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
            for (Map.Entry<String, String> entry : searchcondition.entrySet()) {
                QueryBuilder matchQuery = QueryBuilders.matchQuery(entry.getKey(), entry.getValue());
                boolBuilder.must(matchQuery);
            }
            searchRequestBuilder.setQuery(boolBuilder);
        }else{
            searchRequestBuilder.setQuery(QueryBuilders.matchAllQuery());
        }
        SearchResponse response = searchRequestBuilder.execute().actionGet();

        JSONObject result = new JSONObject();
        JSONArray data = new JSONArray();
        Terms terms = response.getAggregations().get("per_count");
        List<? extends Terms.Bucket> buckets = terms.getBuckets();
        for(Terms.Bucket bucket:buckets){
            JSONObject temp = new JSONObject();
            temp.put(aggrfield,bucket.getKey());
            temp.put(SysConsts.COUNT,bucket.getDocCount());
            data.add(temp);
        }
        long total = buckets.size();
        result.put(SysConsts.TOTAL,total);
        result.put(SysConsts.CODE,SysConsts.CODE_000);
        result.put(SysConsts.DATA,data);
        System.out.println(result.toJSONString());
        transportClient.close();
        return result;
    }

    private JSONObject createErrorMsg(String code,String interfaceStr,String msg){
        JSONObject result = new JSONObject();
        result.put(SysConsts.CODE,code);
        JSONObject error = new JSONObject();
        error.put(SysConsts.MSG,msg);
        error.put(SysConsts.INTERFACE,interfaceStr);
        result.put(SysConsts.ERROR,error);
        return result;
    }
}
