package service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.index.get.GetResult;
import qed.bigdata.infosupplyer.consts.DataTypeEnum;
import qed.bigdata.infosupplyer.consts.SysConsts;
import qed.bigdata.infosupplyer.factory.EsClientFactory;
import qed.bigdata.infosupplyer.service.impl.ElasticSearchServiceImpl;
import qed.bigdata.infosupplyer.consts.EsConsts;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SearchServiceTest {
    public static void main(String[] args) {
        test();
    }


    public static void searchByPagingTest(){

        JSONObject json = new JSONObject();
        JSONArray criteria = new JSONArray();
        JSONObject obj1 = new JSONObject();
        obj1.put("keyword","SeriesDescription");
        obj1.put("value","L MLO");
        obj1.put("section","no");

        JSONObject obj2 = new JSONObject();
        obj2.put("keyword","SliceThickness");
        obj2.put("start","0.0");
        obj2.put("end","100.0");
        obj2.put("section","yes");
        criteria.add(obj1);
        criteria.add(obj2);
        JSONArray backfields = new JSONArray();
        backfields.add(EsConsts.InstitutionName_ES_DCM);
        JSONArray sortfields = new JSONArray();
        sortfields.add(EsConsts.SeriesDate_ES_DCM);
//		sortfields.add(ESConstants.PatientName_ES);
        json.put("pageid",1);
        json.put("pagesize",3);
        json.put("backfields",backfields);
        json.put("sortfields",sortfields);
        json.put("criteria",criteria);
        json.put("datatype",SysConsts.TYPE_DICOM);
        String interfaceStr = "/info/searchpaging";
        JSONObject searchPagingResult = null;
        try {
            searchPagingResult = new ElasticSearchServiceImpl().searchByPaging(json, DataTypeEnum.DICOM);
            System.out.println(searchPagingResult.toJSONString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static void searchAllTest(JSONObject json){
        ElasticSearchServiceImpl searchService = new ElasticSearchServiceImpl();
        JSONObject jsonObject = searchService.searchAll();

        Set<String> hospitalSet = new HashSet<String>();
        Set<String> deviceSet = new HashSet<String>();
        Set<String> organSet = new HashSet<String>();
        Set<String> seriesDescriptionSet = new HashSet<String>();
        Set<String> sexSet = new HashSet<String>();
        Set<String> ageSet = new HashSet<String>();
        Set<String> studydateSet = new HashSet<String>();
        Set<Integer> imageCountSet = new HashSet<Integer>();

        long total = jsonObject.getLong("total");
        JSONArray data = jsonObject.getJSONArray("data");
        int size = data.size();
        System.out.println("total:"+total+"\t size:"+size);
        for(int i=0;i<size;i++){
            JSONObject jsonObject1 = data.getJSONObject(i);
            hospitalSet.add(jsonObject1.getString(EsConsts.InstitutionName_ES_DCM));
            deviceSet.add(jsonObject1.getString(EsConsts.ManufacturerModelName_ES_DCM));
            seriesDescriptionSet.add(jsonObject1.getString(EsConsts.SeriesDescription_ES_DCM));
            sexSet.add(jsonObject1.getString(EsConsts.PatientSex_ES_DCM));
            ageSet.add(jsonObject1.getString(EsConsts.PatientAge_ES_DCM));
            studydateSet.add(jsonObject1.getString(EsConsts.SeriesDate_ES_DCM));
            imageCountSet.add(jsonObject1.getInteger(EsConsts.NumberOfSlices_ES_DCM));
            System.out.println(jsonObject1);
        }
        for(String e : hospitalSet){
            System.out.print(e+",");
        }
        System.out.println();
        for(String e : deviceSet){
            System.out.print(e+",");
        }
        System.out.println();
        for(String e : organSet){
            System.out.print(e+",");
        }
        System.out.println();
        for(String e : seriesDescriptionSet){
            System.out.print(e+",");
        }
        System.out.println();
        for(String e : sexSet){
            System.out.print(e+",");
        }
        System.out.println();
        for(String e : ageSet){
            System.out.print(e+",");
        }
        System.out.println();
        for(String e : studydateSet){
            System.out.print(e+",");
        }
        System.out.println();
        for(Integer e : imageCountSet){
            System.out.print(e+",");
        }

    }

    public static void printResult(JSONObject jsonObject){
        System.out.println(jsonObject.toJSONString());
        if(jsonObject.getInteger("code")!=null && jsonObject.getInteger("code")==0){
            return ;
        }
        long total = jsonObject.getLong("total");
        JSONArray data = jsonObject.getJSONArray("data");
        int size = data.size();

        for(int i=0;i<size;i++){
            JSONObject jsonObject1 = data.getJSONObject(i);
            System.out.println(jsonObject1);
        }
        System.out.println("total:"+total+"\t size:"+size);
    }

    public static void searchByIds(){
        String ids[] = new String[]{"128401136192363104997433902406225161515976885250288678",
        "128401136192363314770990328001514764853913365774",
        "128401136192363104997433902406216031514851443576182270",
                "128401136192363314770990328001514764853912365774",
        "128401136192363314770990328001514764853905182270",
        "128401136192363104997433902406225161515976885239288678"};

        JSONObject json = new JSONObject();
        //ids
        JSONArray idsArr = new JSONArray();
        for(String e : ids){
            idsArr.add(e);
        }
//        json.put("ids",idsArr);

        JSONArray backfields = new JSONArray();
        backfields.add(EsConsts.InstitutionName_ES_DCM);
        backfields.add(EsConsts.PatientName_ES_DCM);
        backfields.add(EsConsts.PatientSex_ES_DCM);
        backfields.add(EsConsts.PatientAge_ES_DCM);
        backfields.add(EsConsts.SeriesDescription_ES_DCM);
        backfields.add(EsConsts.SeriesDate_ES_DCM);
        backfields.add(EsConsts.NumberOfSlices_ES_DCM);
        backfields.add(EsConsts.ID);
        json.put("backfields",backfields);

        ElasticSearchServiceImpl searchService = new ElasticSearchServiceImpl();
        JSONObject jsonObject = searchService.searchByIds(json);
        printResult(jsonObject);

    }

    public static void searchAggregationTest(){
        ElasticSearchServiceImpl searchService = new ElasticSearchServiceImpl();
        JSONObject jsonObject = searchService.searchAggregation("dicomindex", "dicomtype", null, "tag");
        System.out.println(jsonObject.toJSONString());
    }

    public static void test(){
        ElasticSearchServiceImpl searchService = new ElasticSearchServiceImpl();
        searchService.updateField("dicomindex","dicomtype","dJy-FWQB8hiI7_ZQO49k","tag",null);
    }


}
