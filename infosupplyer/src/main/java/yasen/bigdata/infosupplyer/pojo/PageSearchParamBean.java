package yasen.bigdata.infosupplyer.pojo;

/**
 * @Title: IdSearchParamBean.java
 * @Package yasen.bigdata.infosupplyer.pojo
 * @Description: 查询参数封装类，分页查询参数封装类
 * 接口传入参数格式例子
 * {
 *  "searchcondition": {
 *   "organ": "brain",
 *   "age_end": 86,
 *   "age_start": 85,
 *   "imagecount_min": 121,
 *   "sex": "M",
 *   "studydate_start": "2018-01-01",
 *   "entrydate_end": "2018-04-18",
 *   "entrydate_start": "2018-04-18",
 *   "institution": "PUMCH-GX",
 *   "seriesdescription": "Flair",
 *   "studydate_end": "2018-01-26",
 *   "imagecount_max": 121,
 *   "device": "PET/MR"
 *  },
 *  "pagesize": 200,
 *  "pageid": 1,
 *  "backfields": ["InstitutionName_ES_DCM","organ","PatientName_ES_DCM","PatientsSex_ES_DCM","PatientsAge_ES_DCM","SeriesDescription_ES_DCM","SeriesDate_ES_DCM","NumberOfSlices_ES_DCM","id"],
 *  "sortfields": ["SeriesDate_ES_DCM","PatientName_ES_DCM"]
 * }
 * @author weiguangwu
 * @date  2018/4/23 14:13
 * @version V1.0
 */

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import yasen.bigdata.infosupplyer.consts.DataTypeEnum;
import yasen.bigdata.infosupplyer.consts.ESConstant;
import yasen.bigdata.infosupplyer.consts.SysConstants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PageSearchParamBean {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private JSONObject searchcriteria = null;
    private DicomSearchCriteriaBean dicomSearchCriteriaBean;
    private ElectricSignalSearchCriteriaBean electricSignalSearchCriteriaBean;
    private Integer pageid = 1;
    private Integer pagesize = 0;
    private List<String> backfields = null;
    private List<String> sortfields = null;



    private boolean paging = false;
    private boolean parseError = false;


    public PageSearchParamBean(JSONObject param,DataTypeEnum type){
        init();
        try {
            if(type == DataTypeEnum.DICOM) {
                parseDicomParameter(param);
            }else if(type == DataTypeEnum.ELECTRIC){
                parseElectricSignalParameter(param);
            }else if(type == DataTypeEnum.KFB){
//                parsKfbParameter(param);
            }else if(type == DataTypeEnum.GUAGE){
//                parsGuageParameter(param);
            }
        } catch (Exception e) {
            e.printStackTrace();
            parseError = true;
        }
    }

    private void init(){
        backfields = new ArrayList<String>();
        sortfields = new ArrayList<String>();
    }
    private void parseDicomParameter(JSONObject param)throws Exception{
        searchcriteria = param.getJSONObject(SysConstants.SEARCH_CRITERIA);
        if(searchcriteria!=null) {
            dicomSearchCriteriaBean = new DicomSearchCriteriaBean(searchcriteria);
        }
        Integer pageidParam = param.getInteger("pageid");
        Integer pagesizeParam = param.getInteger("pagesize");
        parsePageParam(pageidParam,pagesizeParam);

        JSONArray backfieldsParam = param.getJSONArray("backfields");

        //下面这两段if语句逻辑：如果backfields为空则使用默认返回字段，如果不为空，取backfields，sortfields交集为排序字段
        if(backfieldsParam!=null){
            List<String> backfieldsList = backfieldsParam.toJavaList(String.class);
            if(backfieldsList.size()==1 && backfieldsList.get(0).equals("all")){
                backfields = null;
            }else {
                for (String e : backfieldsList) {
                    if (ESConstant.ES_DCM_FIELD.contains(e)) {
                        backfields.add(e);
                    }
                }
            }
        }else{
            backfields.addAll(ESConstant.DCM_DEFAULT_BACK_FIELD);
        }

        JSONArray sortfieldsParam = param.getJSONArray("sortfields");
        if(sortfieldsParam!=null && sortfieldsParam.size()!=0){
            List<String> sortfieldsList = sortfieldsParam.toJavaList(String.class);
            for(String e : sortfieldsList){
                if(backfields.contains(e)){
                    //解释：这里拼接.keyword，是因为es mapping中将keyword作为子field.添加之后会根据多单词文本的第一个单词字母表排序
                    //如果不添加，则会乱序
                    if(ESConstant.DCM_ES_TEXT_SORT_CHILD_FIELD.contains(e)) {
                        sortfields.add(e + ".keyword");
                    }else{
                        sortfields.add(e);
                    }
                }
            }
        }else{
            sortfields = null;
        }

    }

    private void parseElectricSignalParameter(JSONObject param){
        searchcriteria = param.getJSONObject(SysConstants.SEARCH_CRITERIA);
        if(searchcriteria!=null) {
            electricSignalSearchCriteriaBean = new ElectricSignalSearchCriteriaBean(searchcriteria);
        }
        Integer pageidParam = param.getInteger("pageid");
        Integer pagesizeParam = param.getInteger("pagesize");
        parsePageParam(pageidParam,pagesizeParam);

        JSONArray backfieldsParam = param.getJSONArray(SysConstants.BACKFIELDS);

        if(backfieldsParam!=null){
            List<String> backfieldsList = backfieldsParam.toJavaList(String.class);
            if(backfieldsList.size()==1 && backfieldsList.get(0).equals("all")){
                backfields = null;
            }else {
                for (String e : backfieldsList) {
                    if (ESConstant.ES_ELECTRIC_FIELD.contains(e)) {
                        backfields.add(e);
                    }
                }
            }
        }
    }



    private void parsePageParam(Integer pageidParam, Integer pagesizeParam){
        if(pageidParam!=null && pageidParam>0){
            pageid = pageidParam;
        }
        if(pagesizeParam!=null && pagesizeParam>0){
            pagesize = pagesizeParam;
            paging = true;
        }else{
            paging = false;
        }

    }
    public boolean isDicomSearchCriteriaAvailable(){
        return dicomSearchCriteriaBean != null;
    }

    public boolean isElectricSignalSearchCriteriaAvailable(){
        return electricSignalSearchCriteriaBean != null;
    }

    public boolean isPageIdAvailable(){
        return pageid!=null && pageid>0;
    }
    public boolean isPageSizeAvailable(){
        return pagesize!=null && pagesize>0;
    }

    public boolean isBackfieldsAvailable(){
        return backfields!=null;
    }
    public boolean isSortfieldsAvailable(){
        return sortfields!=null;
    }
    public boolean isPaging(){
        return paging;
    }

    public boolean isParseError(){
        return parseError;
    }



    public Integer getPageid() {
        return pageid;
    }

    public Integer getPagesize() {
        return pagesize;
    }

    public List<String> getBackfields() {
        return backfields;
    }

    public List<String> getSortfields() {
        return sortfields;
    }

    public DicomSearchCriteriaBean getDicomSearchCriteriaBean() {
        return dicomSearchCriteriaBean;
    }

    public ElectricSignalSearchCriteriaBean getElectricSignalSearchCriteriaBean() {
        return electricSignalSearchCriteriaBean;
    }
}
