package yasen.bigdata.infosupplyer.util;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import yasen.bigdata.infosupplyer.consts.ESConstant;
import yasen.bigdata.infosupplyer.conf.InfosupplyerConfiguration;
import yasen.bigdata.infosupplyer.factory.EsClientFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class Demo {
    public static void main(String[] args) {


        String json = "{\"string\": [1,2,3]}";
        String str1 = "{\"a\":\"1\"}";
        // 如果json数据以形式保存在文件中，用FileReader进行流读取，path为json数据文件路径。
        // JSONReader reader = new JSONReader(new FileReader(path));
        // 为了直观，方便运行，就用StringReader做示例！
        JSONReader reader = new JSONReader(new StringReader(str1));
        reader.startObject();
        System.out.print("start read json with fastjson");
        while (reader.hasNext())
        {
            String key = reader.readString();
            System.out.println("key " + key);
            if (key.equals("array"))
            {
                reader.startArray();
                System.out.println("start " + key);
                while (reader.hasNext())
                {
                    String item = reader.readString();
                    System.out.println(item);
                }
                reader.endArray();
                System.out.println("end " + key);
            }
            else if (key.equals("arraylist"))
            {
                reader.startArray();
                System.out.println("start " + key);
                while (reader.hasNext())
                {
                    reader.startObject();
                    System.out.println("start arraylist item");
                    while (reader.hasNext())
                    {
                        String arrayListItemKey = reader.readString();
                        String arrayListItemValue = reader.readObject().toString();
                        System.out.print("key " + arrayListItemKey);
                        System.out.println(":value " + arrayListItemValue);
                    }
                    reader.endObject();
                    System.out.println("end arraylist item");
                }
                reader.endArray();
                System.out.println("end " + key);
            }
            else if (key.equals("object"))
            {
                reader.startObject();
                System.out.println("start object item");
                while (reader.hasNext())
                {
                    String objectKey = reader.readString();
                    String objectValue = reader.readObject().toString();
                    System.out.print("key " + objectKey);
                    System.out.println(":value " + objectValue);
                }
                reader.endObject();
                System.out.println("end object item");
            }
            else if (key.equals("string"))
            {
                System.out.println("start string");
                String value = reader.readObject().toString();
                System.out.println("value " + value);
                System.out.println("end string");
            }
        }
        reader.endObject();
        System.out.println("start fastjson");








//        fun();
//        String str = "{\"total\":0,\"code\":\"000\",\"data\":[],\"pagecount\":1}";
//        String str1 = "{\"a\":\"1\"}";
//        JSONReader reader = new JSONReader(new StringReader(str1));
//        reader.startObject();
//        System.out.println(reader.readString());
//        while (reader.hasNext()){
//            String key = reader.readString();
//            if(key.equals("000")){
//                result.put("code",reader.readObject(String.class));
//            }else if(key.equals("pagecount")){
//                result.put("pagecount",reader.readObject(Long.class));
//            }else if(key.equals("total")){
//                result.put("total",reader.readObject(Long.class));
//            }else if(key.equals("data")){
//                JSONObject temp = new JSONObject();
//                reader.startArray();
//                while(reader.hasNext()){
//                    Dicom dicom = JSON.parseObject(reader.readObject().toString(), Dicom.class);
//                    dicomList.add(dicom);
//                }
//                reader.endArray();
//                result.put("data",dicomList);
//            }
//        }
//        reader.endObject();
    }




    public static void fun(){
        InfosupplyerConfiguration infosupplyerConf = new InfosupplyerConfiguration();
        TransportClient transportClient = EsClientFactory.getTransportClient();

        SortBuilder sortBuilder1 = new FieldSortBuilder(ESConstant.InstitutionName_ES).order(SortOrder.DESC);
        SortBuilder sortBuilder2 = new FieldSortBuilder(ESConstant.ORGAN_ES).order(SortOrder.DESC);
        SortBuilder sortBuilder3 = new FieldSortBuilder(ESConstant.PatientName_ES).order(SortOrder.DESC);
        SortBuilder sortBuilder4 = new FieldSortBuilder(ESConstant.SeriesDate_ES).order(SortOrder.DESC);
        SortBuilder sortBuilder5 = new FieldSortBuilder(ESConstant.SeriesDescription_ES).order(SortOrder.DESC);
        SortBuilder sortBuilder6 = new FieldSortBuilder(ESConstant.NumberOfSlices_ES).order(SortOrder.DESC);



        SearchResponse response=transportClient.prepareSearch("myindex")
                .setTypes("book")
                // 设置查询类型
                // 1.SearchType.DFS_QUERY_THEN_FETCH = 精确查询
                // 2.SearchType.SCAN = 扫描查询,无序
                // 3.SearchType.COUNT = 不设置的话,这个为默认值,还有的自己去试试吧
                .setSearchType(SearchType.DEFAULT)
                // 设置查询关键词
                .setQuery(QueryBuilders.matchAllQuery())
//                .addSort(sortBuilder1)
//                .addSort(sortBuilder2)
//                .addSort(sortBuilder4)
//                .addSort(sortBuilder6)
//                .addSort(sortBuilder5)
//                .setScroll(new TimeValue(100000))
                // 设置查询数据的位置,分页用
                .setFrom(5)
                // 设置查询结果集的最大条数
                .setSize(5)
                // 设置是否按查询匹配度排序
                .setExplain(true)
                // 最后就是返回搜索响应信息
                .execute()
                .actionGet();

//        System.out.println(scrollId);
        long totalCount = response.getHits().getTotalHits();
        System.out.println("共匹配到:"+totalCount+"条记录!");
        for (SearchHit hit : response.getHits().getHits()) {
//            JSONObject jsonObject = parseHit(hit);
//            System.out.println(jsonObject.toJSONString());
            System.out.println(hit.getId());
        }
        System.out.println("=========");
        if(totalCount>5) {
            int page = (int) totalCount / (5);//计算总页数,每次搜索数量为分片数*设置的size大小
            for (int i = 0; i <= page; i++) {
                //再次发送请求,并使用上次搜索结果的ScrollId
                response = transportClient
                        .prepareSearchScroll(response.getScrollId())
                        .setScroll(new TimeValue(100000)).execute()
                        .actionGet();
                SearchHit[] hits = response.getHits().getHits();
                for (SearchHit hit : hits) {
//                    JSONObject jsonObject = parseHit(hit);
                    System.out.println(hit.getId());
                }
                System.out.println("=========");
            }
        }
    }
    private static JSONObject parseHit(SearchHit searchHit){
        JSONObject json = new JSONObject();
        Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
        json.put(ESConstant.InstitutionName_ES,sourceAsMap.get(ESConstant.InstitutionName_ES));
        json.put(ESConstant.ORGAN_ES,sourceAsMap.get(ESConstant.ORGAN_ES));
        json.put(ESConstant.PatientName_ES,sourceAsMap.get(ESConstant.PatientName_ES));
        json.put(ESConstant.SeriesDescription_ES,sourceAsMap.get(ESConstant.SeriesDescription_ES));
        json.put(ESConstant.SeriesDate_ES,sourceAsMap.get(ESConstant.SeriesDate_ES));
        json.put(ESConstant.NumberOfSlices_ES,sourceAsMap.get(ESConstant.NumberOfSlices_ES));
        return json;
    }


    public static void fun1(){
        InfosupplyerConfiguration infosupplyerConf = new InfosupplyerConfiguration();
        RestClientBuilder builder = RestClient.builder(
                new HttpHost(infosupplyerConf.getEsip(), Integer.parseInt(infosupplyerConf.getEshttpport()), "http"));

        //HTTP连接延迟时间配置
        builder.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
            @Override
            public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
                requestConfigBuilder.setConnectTimeout(10000);
                requestConfigBuilder.setSocketTimeout(30000);
                requestConfigBuilder.setConnectionRequestTimeout(10000);
                return requestConfigBuilder;
            }
        });
        //连接数配置
        builder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
            @Override
            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                return httpClientBuilder.setDefaultIOReactorConfig(
                        IOReactorConfig.custom()
                                .setIoThreadCount(100)//线程数配置
                                .setConnectTimeout(10000)
                                .setSoTimeout(10000)
                                .build());
            }
        });
        //设置超时
        builder.setMaxRetryTimeoutMillis(10000);
        RestHighLevelClient client = new RestHighLevelClient(builder);
        /*********************/
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();

//        QueryBuilder queryBuilder = QueryBuilders.termQuery("StudyID","13764");

        /*************************/
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.from(0).size(5);
        searchSourceBuilder.timeout(new TimeValue(60,TimeUnit.SECONDS));
//        searchSourceBuilder.sort("age");

        SearchRequest searchRequest = new SearchRequest("myindex")
                .types("book")
                .searchType(SearchType.QUERY_THEN_FETCH)
                .scroll(new TimeValue(60,TimeUnit.SECONDS));




        searchRequest.source(searchSourceBuilder);
        /*******************************/
        SearchResponse response = null;
        try {
            response = client.search(searchRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long sum = response.getHits().getTotalHits();
        String scrollId = response.getScrollId();
        for(SearchHit e : response.getHits().getHits()){
            System.out.println(e.getId());
        }
        System.out.println("结果："+sum+"\t scrollId:"+scrollId);
        long times = sum/5;
        System.out.println("times:"+times);
        for(int i=0;i<times;i++){
            SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
            SearchResponse response1 = null;
            try {
                response1 = client.searchScroll(scrollRequest);
            } catch (IOException e) {
                e.printStackTrace();
            }
            SearchHit[] hits = response1.getHits().getHits();
            for(SearchHit hit : hits){
                System.out.println(hit.getId());
            }
            System.out.println("===============");
        }

        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
