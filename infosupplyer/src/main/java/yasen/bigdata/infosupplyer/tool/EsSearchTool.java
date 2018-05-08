//package yasen.bigdata.infosupplyer.tool;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import org.apache.http.HttpHost;
//import org.apache.log4j.Logger;
//import org.elasticsearch.action.search.*;
//import org.elasticsearch.client.RestClient;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.client.transport.TransportClient;
//import org.elasticsearch.common.unit.TimeValue;
//import org.elasticsearch.index.query.*;
//import org.elasticsearch.search.SearchHit;
//import org.elasticsearch.search.SearchHits;
//import org.elasticsearch.search.builder.SearchSourceBuilder;
//import yasen.bigdata.infosupplyer.conf.InfosupplyerConfiguration;
//
//import java.io.IOException;
//import java.util.*;
//
//public class EsSearchTool {
//
//    static Logger logger = Logger.getLogger(EsSearchTool.class);
//    public static void main(String[] args) throws IOException {
//        JSONObject json = new JSONObject();
//        boolean flag = isExists("12840113619236314770990321484028207253990");
//        System.out.println(flag);
////        String hos = "PUMCH";
//        String se = "M";
////        String mri = "OAx T2 fs Flair";
////        json.put(ESConstant.HOSPITAL,hos);
//        json.put(ESConstant.SEX,se);
////        json.put(ESConstant.MRISEQ,mri);
////        List<JSONObject> jsonObjects = queryPaths(json);
//        JSONObject result = queryForDownload(json);
////        System.out.println(result.toString());
////        for(JSONObject e : jsonObjects){
////            System.out.println(e.toString());
////        }
//    }
//    static InfosupplyerConfiguration infosupplyerConf = null;
//    static{
//        infosupplyerConf = new InfosupplyerConfiguration();
//    }
//
//    /**
//     * 正在使用中
//     * 该方法专门为下载提供查询服务，可以使用查询的结果进行数据下载。
//     * key为下载批次号，标识一个用户，后面是他该次投影的所有图片地址。
//     * {
//     "44105556799475": ["hdfs://hadoop1:8020/yasen/soucedata/2017/12/29/PANG GUI RONG-44105556799475"],
//     "44105556736592": ["hdfs://hadoop1:8020/yasen/soucedata/2017/12/29/PANG GUI RONG-44105556736592"],
//     "44105556817615": ["hdfs://hadoop1:8020/yasen/soucedata/2017/12/29/PANG GUI RONG-44105556817615"],
//     }
//     * queryForDownload
//     * @param searchFieldJson
//     * @return
//     */
//    public static JSONObject queryForDownload(JSONObject searchFieldJson){
//        Map<String,List<Object>> map = new HashMap<String,List<Object>>();
//        TransportClient transportClient = EsClientFactory.getTransportClient();
//       if(transportClient==null){
//           return null;
//       }
//        BoolQueryBuilder boolQueryBuilder = createQueryBuilder(searchFieldJson);
//        SearchResponse response=transportClient.prepareSearch(infosupplyerConf.getIndex())
//                .setTypes(infosupplyerConf.getType())
//                // 设置查询类型
//                // 1.SearchType.DFS_QUERY_THEN_FETCH = 精确查询
//                // 2.SearchType.SCAN = 扫描查询,无序
//                // 3.SearchType.COUNT = 不设置的话,这个为默认值,还有的自己去试试吧
//                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
//                // 设置查询关键词
//                .setQuery(boolQueryBuilder)
//                .setScroll(new TimeValue(20000))
//                // 设置查询数据的位置,分页用
//                .setFrom(0)
//                // 设置查询结果集的最大条数
//                .setSize(1000)
//                // 设置是否按查询匹配度排序
//                .setExplain(true)
//                // 最后就是返回搜索响应信息
//                .execute()
//                .actionGet();
//
//        String scrollId = response.getScrollId();
////        System.out.println(scrollId);
//        long totalCount = response.getHits().getTotalHits();
//        logger.info("共匹配到:"+totalCount+"条记录!");
////        System.out.println("共匹配到:"+totalCount+"条记录!");
//        if(totalCount>1000){
//            int page=(int)totalCount/(1000);//计算总页数,每次搜索数量为分片数*设置的size大小
//            for (int i = 0; i <= page; i++) {
//                //再次发送请求,并使用上次搜索结果的ScrollId
//                SearchResponse searchResponse = transportClient
//                        .prepareSearchScroll(scrollId)
//                        .setScroll(new TimeValue(20000)).execute()
//                        .actionGet();
//                SearchHit[] hits = searchResponse.getHits().getHits();
//                System.out.println("page=" + i + "," + hits.length);
//                for (SearchHit searchHit : hits) {
//                    Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
//                    String batchseq = (String) sourceAsMap.get(ESConstant.BATCHSEQ);
//                    List<Object> list = map.get(batchseq);
//                    if(batchseq==null){
//                        continue;
//                    }
//                    if (list == null) {
//                        list = new LinkedList<Object>();
//                        list.add(sourceAsMap.get(ESConstant.HDFSPATH_ES));
//                        map.put(batchseq, list);
//                    } else {
//                        list.add(sourceAsMap.get(ESConstant.HDFSPATH_ES));
//                        map.put(batchseq, list);
//                    }
//                }
//            }
//        }else{
//            SearchHit[]hits=response.getHits().getHits();
//            for(SearchHit searchHit:hits){
//                Map<String, Object> sourceAsMap=searchHit.getSourceAsMap();
//                String batchseq = (String)sourceAsMap.get(ESConstant.BATCHSEQ);
//                if(batchseq==null){
//                    continue;
//                }
//                List<Object> list = map.get(batchseq);
//                if(list==null){
//                    list = new LinkedList<Object>();
//                    list.add(sourceAsMap.get(ESConstant.HDFSPATH_ES));
//                    map.put(batchseq,list);
//                }else{
//                    list.add(sourceAsMap.get(ESConstant.HDFSPATH_ES));
//                    map.put(batchseq,list);
//                }
//            }
//        }
//
////        SearchHit[]hits=searchHits.getHits();
////        for(SearchHit searchHit:hits){
////            Map<String, Object> sourceAsMap=searchHit.getSourceAsMap();
////            String batchseq = (String)sourceAsMap.get(ESConstant.BATCHSEQ);
////            List<Object> list = map.get(batchseq);
////            if(list==null){
////                list = new LinkedList<Object>();
////                list.add(sourceAsMap.get(ESConstant.HDFSPATH_ES));
////                map.put(batchseq,list);
////            }else{
////                list.add(sourceAsMap.get(ESConstant.HDFSPATH_ES));
////                map.put(batchseq,list);
////            }
////        }
//        JSONObject json = new JSONObject();
//        for(Map.Entry<String,List<Object>> entry : map.entrySet()){
//            List<Object> value = entry.getValue();
//            JSONArray jsonArray = new JSONArray(value);
//            json.put(entry.getKey(),jsonArray);
//        }
//        return json;
////        return null;
//    }
//
//    private static void tes(SearchResponse searchResponse){
//        long totalHits = searchResponse.getHits().totalHits;
//        System.out.println(totalHits);
//
//        SearchHit[]hits=searchResponse.getHits().getHits();
//        long i = 0;
//        for(SearchHit searchHit:hits){
//            i++;
//        }
//        System.out.println("tes:"+i);
//    }
//
//
//    private static BoolQueryBuilder createQueryBuilder(JSONObject searchFieldJson){
//        List<QueryBuilder> matchQueryList = new ArrayList<QueryBuilder>();
//        //创建查询条件
//        //精确值的字段：MRI序列，性别
//        //如果有该查询条件则加到querybuilder中，否则则不加
//        String mriSeq = searchFieldJson.getString(ESConstant.MRISEQ);
//        if(mriSeq != null && mriSeq.length()!=0){
//            //此处使用termsQuery(String name,String value)精确匹配单个值
//            matchQueryList.add(QueryBuilders.matchQuery(ESConstant.MRISEQ, mriSeq));
//        }
//        String sex = searchFieldJson.getString(ESConstant.SEX);
//        if(sex != null && sex.length()!=0){
//            //此处使用termsQuery(String name,String value)精确匹配单个值
//            matchQueryList.add(QueryBuilders.matchQuery(ESConstant.SEX, sex));
//        }
//
//        //范围值的字段：年龄段，检查日期段，数据收入日期段
//        // 查询在时间区间范围内的结果
//        //年龄段
//        String ageSection = searchFieldJson.getString(ESConstant.AGE_SECTION);
//        if(ageSection != null && ageSection.length()!=0){
//            RangeQueryBuilder rangbuilder = QueryBuilders.rangeQuery(ESConstant.AGE);
//            String section[] = ageSection.split("\\-");
//            rangbuilder.gte(section[0]);
//            rangbuilder.lte(section[1]);
//            matchQueryList.add(rangbuilder);
//        }
//        //检查日期段20170811-20180201
//        String studyDateSection = searchFieldJson.getString(ESConstant.STUDYDATE_SECTION);
//        if(studyDateSection != null && studyDateSection.length()!=0){
//            RangeQueryBuilder rangbuilder = QueryBuilders.rangeQuery(ESConstant.STUDYDATE);
//            String section[] = studyDateSection.split("\\-");
//            rangbuilder.gte(section[0]);
//            rangbuilder.lte(section[1]);
//            matchQueryList.add(rangbuilder);
//        }
//        //数据收入日期段
//        String entryDateSection = searchFieldJson.getString(ESConstant.ENTRYDATE_SECTION);
//        if(entryDateSection !=null && entryDateSection.length()!=0){
//            RangeQueryBuilder rangbuilder = QueryBuilders.rangeQuery(ESConstant.ENTRYDATE_ES);
//            String section[] = entryDateSection.split("\\-");
//            rangbuilder.gte(section[0]);
//            rangbuilder.lte(section[1]);
//            matchQueryList.add(rangbuilder);
//        }
//
//        //枚举值的字段：设备种类，数据来源（医院），扫描部位
//        String deviceName = searchFieldJson.getString(ESConstant.DEVICENAME);
//        if(deviceName != null && deviceName.length()!=0){
//            //此处使用termsQuery(String name,String ...values)同时匹配多个值
//            matchQueryList.add(QueryBuilders.matchQuery(ESConstant.DEVICENAME,deviceName ));
//        }
//        String hospital = searchFieldJson.getString(ESConstant.HOSPITAL);
//        if(hospital != null && hospital.length()!=0){
//            //此处使用termsQuery(String name,String ...values)同时匹配多个值
//            matchQueryList.add(QueryBuilders.matchQuery(ESConstant.HOSPITAL, hospital));
//        }
//        String organ = searchFieldJson.getString(ESConstant.ORGAN_ES);
//        if(organ != null && organ.length()!=0){
//            //此处使用termsQuery(String name,String ...values)同时匹配多个值
//            matchQueryList.add(QueryBuilders.matchQuery(ESConstant.ORGAN_ES, organ));
//        }
//
//        // 等同于bool，将两个查询合并
//        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
//        int i = 0;
//        for(QueryBuilder e : matchQueryList) {
//            i++;
//            boolBuilder.must(e);
//        }
//        return boolBuilder;
//    }
//
//
//
//    /***
//     * 当前使用中
//     * esSearch,该方法使用了调用ES rest api的方法来查询数据.
//     * @param searchFieldJson
//     * @return
//     */
//    public static List<JSONObject> esSearch(JSONObject searchFieldJson){
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//
//        List<QueryBuilder> matchQueryList = new ArrayList<QueryBuilder>();
//        //创建查询条件
//        //精确值的字段：MRI序列，性别
//        //如果有该查询条件则加到querybuilder中，否则则不加
//        String mriSeq = searchFieldJson.getString(ESConstant.MRISEQ);
//        if(mriSeq != null && mriSeq.length()!=0){
//            //此处使用termsQuery(String name,String value)精确匹配单个值
//            matchQueryList.add(QueryBuilders.matchQuery(ESConstant.MRISEQ, mriSeq));
//        }
//        String sex = searchFieldJson.getString(ESConstant.SEX);
//        if(sex != null && sex.length()!=0){
//            //此处使用termsQuery(String name,String value)精确匹配单个值
//            matchQueryList.add(QueryBuilders.matchQuery(ESConstant.SEX, sex));
//        }
//
//        //范围值的字段：年龄段，检查日期段，数据收入日期段
//        // 查询在时间区间范围内的结果
//        //年龄段
//        String ageSection = searchFieldJson.getString(ESConstant.AGE_SECTION);
//        if(ageSection != null && ageSection.length()!=0){
//            RangeQueryBuilder rangbuilder = QueryBuilders.rangeQuery(ESConstant.AGE);
//            String section[] = ageSection.split("\\-");
//            rangbuilder.gte(section[0]);
//            rangbuilder.lte(section[1]);
//            matchQueryList.add(rangbuilder);
//        }
//        //检查日期段20170811-20180201
//        String studyDateSection = searchFieldJson.getString(ESConstant.STUDYDATE_SECTION);
//        if(studyDateSection != null && studyDateSection.length()!=0){
//            RangeQueryBuilder rangbuilder = QueryBuilders.rangeQuery(ESConstant.STUDYDATE);
//            String section[] = studyDateSection.split("\\-");
//            rangbuilder.gte(section[0]);
//            rangbuilder.lte(section[1]);
//            matchQueryList.add(rangbuilder);
//        }
//        //数据收入日期段
//        String entryDateSection = searchFieldJson.getString(ESConstant.ENTRYDATE_SECTION);
//        if(entryDateSection !=null && entryDateSection.length()!=0){
//            RangeQueryBuilder rangbuilder = QueryBuilders.rangeQuery(ESConstant.ENTRYDATE_ES);
//            String section[] = entryDateSection.split("\\-");
//            rangbuilder.gte(section[0]);
//            rangbuilder.lte(section[1]);
//            matchQueryList.add(rangbuilder);
//        }
//
//        //枚举值的字段：设备种类，数据来源（医院），扫描部位
//
//        String deviceType = searchFieldJson.getString(ESConstant.DEVICENAME);
//        if(deviceType != null && deviceType.length()!=0){
//            //此处使用termsQuery(String name,String ...values)同时匹配多个值
//            matchQueryList.add(QueryBuilders.matchQuery(ESConstant.DEVICENAME,deviceType ));
//        }
//        String hospital = searchFieldJson.getString(ESConstant.HOSPITAL);
//        if(hospital != null && hospital.length()!=0){
//            //此处使用termsQuery(String name,String ...values)同时匹配多个值
//            matchQueryList.add(QueryBuilders.matchQuery(ESConstant.HOSPITAL, hospital));
//        }
//        String organ = searchFieldJson.getString(ESConstant.ORGAN_ES);
//        if(organ != null && organ.length()!=0){
//            //此处使用termsQuery(String name,String ...values)同时匹配多个值
//            matchQueryList.add(QueryBuilders.matchQuery(ESConstant.ORGAN_ES, organ));
//        }
//
//        // 等同于bool，将两个查询合并
//        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
//        int i = 0;
//        for(QueryBuilder e : matchQueryList) {
//            i++;
//            boolBuilder.must(e);
//        }
//        System.out.println(i);
//
//        searchSourceBuilder.query(boolBuilder);
////        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
////        searchSourceBuilder.aggregation(AggregationBuilders.terms("top_10_states").field("state").size(10));
//        return doSearch(searchSourceBuilder);
//    }
//
//    /***
//     * esSearch()方法的帮助类
//     * @param searchSourceBuilder
//     * @return
//     */
//    private static List<JSONObject> doSearch(SearchSourceBuilder searchSourceBuilder){
//        List<JSONObject> resultList = new LinkedList<JSONObject>();
//        SearchResponse searchResponse = null;
//        SearchHits hits;
//        try (RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
//                new HttpHost(infosupplyerConf.getEsip(),Integer.parseInt(infosupplyerConf.getEshttpport()), "http")))) {
//            SearchRequest searchRequest = new SearchRequest();
////        searchRequest.indices("social-*");
//            searchRequest.source(searchSourceBuilder);
//            searchResponse = client.search(searchRequest);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        hits = searchResponse.getHits();
//        //hits.getHits().length
//        for(int i = 0; i < 10; i++) {
//            String result = hits.getHits()[i].getSourceAsString();
////            System.out.println(result);
//            JSONObject jsonObject = JSON.parseObject(result);
//            resultList.add(jsonObject);
//        }
//        return resultList;
//    }
//
//    public static boolean isExists(String id){
////        JsonObject json = new JsonObject();
////        json.addProperty(ESConstant.ID_ES,id);
////        List<JSONObject> results = esSearch(json);
////        return results.size()!=0;
//        return true;
//    }
//
//}
//
