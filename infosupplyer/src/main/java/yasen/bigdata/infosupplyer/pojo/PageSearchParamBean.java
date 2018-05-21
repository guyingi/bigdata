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
 *  "backfields": ["InstitutionName_ES","organ","PatientName_ES","PatientsSex_ES","PatientsAge_ES","SeriesDescription_ES","SeriesDate_ES","NumberOfSlices_ES","id"],
 *  "sortfields": ["SeriesDate_ES","PatientName_ES"]
 * }
 * @author weiguangwu
 * @date  2018/4/23 14:13
 * @version V1.0
 */

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import yasen.bigdata.infosupplyer.consts.ESConstant;
import yasen.bigdata.infosupplyer.consts.SysConstants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PageSearchParamBean {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private JSONObject searchcondition = null;
    private SearchconditionBean searchconditionBean;
    private Integer pageid = 1;
    private Integer pagesize = 0;
    private List<String> backfields = null;
    private List<String> sortfields = null;



    private boolean paging = false;
    private boolean parseError = false;


    public PageSearchParamBean(JSONObject param){
        init();
        try {
            parseParameter(param);
        } catch (Exception e) {
            e.printStackTrace();
            parseError = true;
        }
    }

    private void init(){
        backfields = new ArrayList<String>();
        sortfields = new ArrayList<String>();
    }
    private void parseParameter(JSONObject param)throws Exception{
        searchcondition = param.getJSONObject("searchcondition");
        if(searchcondition!=null) {
            searchconditionBean = new SearchconditionBean(searchcondition);
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
                    if (ESConstant.ESFIELD.contains(e)) {
                        backfields.add(e);
                    }
                }
            }
        }else{
            backfields.addAll(ESConstant.DEFAULT_BACK_FIELD);
        }
        JSONArray sortfieldsParam = param.getJSONArray("sortfields");
        if(sortfieldsParam!=null && sortfieldsParam.size()!=0){
            List<String> sortfieldsList = sortfieldsParam.toJavaList(String.class);
            for(String e : sortfieldsList){
                if(backfields.contains(e)){
                    //解释：这里拼接.keyword，是因为es mapping中将keyword作为子field.添加之后会根据多单词文本的第一个单词字母表排序
                    //如果不添加，则会乱序
                    if(ESConstant.ESTEXT_SORT_CHILD_FIELD.contains(e)) {
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
    public boolean isSearchconditionAvailable(){
        return searchcondition!=null;
    }

    public boolean isPageIdAvailable(){
        return pageid!=null && pageid>0;
    }
    public boolean isPageSizeAvailable(){
        return pagesize!=null && pagesize>0;
    }
    public boolean isDeviceAvailable(){
        return searchconditionBean.isDeviceAvailable();
    }
    public boolean isOrganAvailable(){ return searchconditionBean.isOrganAvailable(); }
    public boolean isSeriesdescriptionAvailable(){
        return searchconditionBean.isSeriesdescriptionAvailable();
    }
    public boolean isInstitutionAvailable(){
        return searchconditionBean.isInstitutionAvailable();
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

    public boolean isSexAvailable(){
        return searchconditionBean.isSexAvailable();
    }
    public boolean isAgeStartAvailable(){
        return searchconditionBean.isAgeStartAvailable();
    }
    public boolean isAgeEndAvailable(){
        return searchconditionBean.isAgeEndAvailable();
    }
    public boolean isStudydateStartAvailable(){
        return searchconditionBean.isStudydateStartAvailable();
    }
    public boolean isStudydateEndAvailable(){
        return searchconditionBean.isStudydateEndAvailable();
    }
    public boolean isEntrydateStartAvailable(){
        return searchconditionBean.isEntrydateStartAvailable();
    }
    public boolean isEntrydateEndAvailable(){
        return searchconditionBean.isEntrydateEndAvailable();
    }
    public boolean isImagecountMinAvailable(){
        return searchconditionBean.isImagecountMinAvailable();
    }
    public boolean isImagecountMaxAvailable(){
        return searchconditionBean.isImagecountMaxAvailable();
    }
    public boolean isSlicethicknessMinAvailable(){
        return searchconditionBean.isSlicethicknessMinAvailable();
    }
    public boolean isSlicethicknessMaxAvailable(){
        return searchconditionBean.isSlicethicknessMaxAvailable();
    }
    public boolean isdevicePhrase(){
        return searchconditionBean.isdevicePhrase();
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

    public String getDevice() {
        return searchconditionBean.getDevice();
    }

    public String getOrgan() {
        return searchconditionBean.getOrgan();
    }

    public String getSeriesdescription() {
        return searchconditionBean.getSeriesdescription();
    }

    public String getInstitution() {
        return searchconditionBean.getInstitution();
    }

    public String getSex() {
        return searchconditionBean.getSex();
    }

    public Integer getAgeStart() {
        return searchconditionBean.getAgeStart();
    }

    public Integer getAgeEnd() {
        return searchconditionBean.getAgeEnd();
    }

    public String getStudydateStart() {
        return searchconditionBean.getStudydateStart();
    }

    public String getStudydateEnd() {
        return searchconditionBean.getStudydateEnd();
    }

    public String getEntrydateStart() {
        return searchconditionBean.getEntrydateStart();
    }

    public String getEntrydateEnd() {
        return searchconditionBean.getEntrydateEnd();
    }

    public Integer getImagecountMin() {
        return searchconditionBean.getImagecountMin();
    }

    public Integer getImagecountMax() {
        return searchconditionBean.getImagecountMax();
    }

    public Double getSlicethicknessMin() {
        return searchconditionBean.getSlicethicknessMin();
    }

    public Double getSlicethicknessMax() {
        return searchconditionBean.getSlicethicknessMax();
    }

    @Override
    public String toString() {
        return "PageSearchParamBean{" +
                "sdf=" + sdf +
                ", searchconditionBean=" + searchconditionBean.toString() +
                ", searchcondition=" + searchcondition +
                ", pageid=" + pageid +
                ", pagesize=" + pagesize +
                ", backfields=" + backfields +
                ", sortfields=" + sortfields +
                ", paging=" + paging +
                ", parseError=" + parseError +
                '}';
    }
}
