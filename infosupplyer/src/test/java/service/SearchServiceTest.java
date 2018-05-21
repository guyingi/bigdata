package service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import yasen.bigdata.infosupplyer.consts.SysConstants;
import yasen.bigdata.infosupplyer.service.impl.ElasticSearchServiceImpl;
import yasen.bigdata.infosupplyer.consts.ESConstant;

import java.util.HashSet;
import java.util.Set;

public class SearchServiceTest {
    public static void main(String[] args) {
        JSONObject json = new JSONObject();
//        searchAllTest(json);
        searchByPagingTest();
//        searchByIds();
    }


    public static void searchByPagingTest(){

        JSONObject json = new JSONObject();
//        "searchcondition":
//        "pageid":可缺省
//        "pagesize":可缺省
//        "backfields":可缺省
//        "sortfields":可缺省'
        JSONObject searchcondition = new JSONObject();
//        searchcondition.put(SysConstants.DEVICE_PARAM,"PET/MR");
//        searchcondition.put(SysConstants.SERIES_DESCRIPTION_PARAM,"Flair");
//        searchcondition.put(SysConstants.ORGAN_PARAM,"brain");
//        searchcondition.put(SysConstants.INSTITUTION_PARAM,"PUMCH");
//        searchcondition.put(SysConstants.SEX_PARAM,"F");
//        searchcondition.put(SysConstants.AGE_START_PARAM,"85");
//        searchcondition.put(SysConstants.AGE_END_PARAM,86);
//        searchcondition.put(SysConstants.STUDYDATE_START_PARAM,"2018-01-01");
//        searchcondition.put(SysConstants.STUDYDATE_END_PARAM,"2018-01-26");
//        searchcondition.put(SysConstants.ENTRYDATE_START_PARAM,"2018-04-10");
//        searchcondition.put(SysConstants.ENTRYDATE_END_PARAM,"2018-04-20");
//        searchcondition.put(SysConstants.IMAGECOUNT_MIN_PARAM,121);
//        searchcondition.put(SysConstants.IMAGECOUNT_MAX_PARAM,121);
        searchcondition.put(SysConstants.SLICE_THICKNESS_MIN_PARAM,0.0);
        searchcondition.put(SysConstants.SLICE_THICKNESS_MAX_PARAM,4.0);
        json.put("searchcondition",searchcondition);
        json.put("pageid",1);
        json.put("pagesize",10);
        JSONArray backfields = new JSONArray();
//        backfields.add(SysConstants.InstitutionName_ES);
//        backfields.add(SysConstants.ORGAN_ES);
//        backfields.add(ESConstant.PatientName_ES);
//        backfields.add(ESConstant.PatientsSex_ES);
//        backfields.add(ESConstant.PatientsAge_ES);
//        backfields.add(ESConstant.SeriesDescription_ES);
//        backfields.add(ESConstant.SeriesDate_ES);
        backfields.add(ESConstant.ID_ES);
//        backfields.add(ESConstant.ID_ES);
        json.put("backfields",backfields);

        JSONArray sortfields = new JSONArray();
        sortfields.add(ESConstant.SeriesDate_ES);
        sortfields.add(ESConstant.PatientName_ES);
        json.put("sortfields",sortfields);
        /******************************************/
        System.out.println(json.toJSONString());
        ElasticSearchServiceImpl searchService = new ElasticSearchServiceImpl();
        JSONObject jsonObject = searchService.searchByPaging(json);

        printResult(jsonObject);
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
            hospitalSet.add(jsonObject1.getString(ESConstant.InstitutionName_ES));
            deviceSet.add(jsonObject1.getString(ESConstant.ManufacturersModelName_ES));
            organSet.add(jsonObject1.getString(ESConstant.ORGAN_ES));
            seriesDescriptionSet.add(jsonObject1.getString(ESConstant.SeriesDescription_ES));
            sexSet.add(jsonObject1.getString(ESConstant.PatientsSex_ES));
            ageSet.add(jsonObject1.getString(ESConstant.PatientsAge_ES));
            studydateSet.add(jsonObject1.getString(ESConstant.SeriesDate_ES));
            imageCountSet.add(jsonObject1.getInteger(ESConstant.NumberOfSlices_ES));
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
        backfields.add(ESConstant.InstitutionName_ES);
        backfields.add(ESConstant.ORGAN_ES);
        backfields.add(ESConstant.PatientName_ES);
        backfields.add(ESConstant.PatientsSex_ES);
        backfields.add(ESConstant.PatientsAge_ES);
        backfields.add(ESConstant.SeriesDescription_ES);
        backfields.add(ESConstant.SeriesDate_ES);
        backfields.add(ESConstant.NumberOfSlices_ES);
        backfields.add(ESConstant.ID_ES);
        json.put("backfields",backfields);

        ElasticSearchServiceImpl searchService = new ElasticSearchServiceImpl();
        JSONObject jsonObject = searchService.searchByIds(json);

        printResult(jsonObject);
    }

}
