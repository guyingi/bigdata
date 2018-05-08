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
import yasen.bigdata.infosupplyer.conf.ESConstant;
import yasen.bigdata.infosupplyer.conf.SysConstants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PageSearchParamBean {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private JSONObject searchcondition = null;
    private Integer pageid = 1;
    private Integer pagesize = 0;
    private List<String> backfields = null;
    private List<String> sortfields = null;

    private String device;
    private String organ;
    private String seriesdescription;
    private String institution;
    private String sex;
    private Integer ageStart;
    private Integer ageEnd;
    private String studydateStart;
    private String studydateEnd;
    private String entrydateStart;
    private String entrydateEnd;
    private Integer imagecountMin;
    private Integer imagecountMax;
    private boolean paging = false;
    private boolean devicephrase = false;//设备是否整个串匹配，拆分,默认模糊匹配
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
            parseSearchcondition(searchcondition);
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

    private void parseSearchcondition(JSONObject param){
        String deviceParam = param.getString(SysConstants.DEVICE_PARAM);
        if(deviceParam!=null && deviceParam.length()!=0){
            device = deviceParam;
            if(device.indexOf("/")>0)
                devicephrase = true;
        }
        String organParam = param.getString(SysConstants.ORGAN_PARAM);
        if(organParam!=null && organParam.length()!=0){
            organ = organParam;
        }
        String seriesdescriptionParam = param.getString(SysConstants.SERIES_DESCRIPTION_PARAM);
        if(seriesdescriptionParam!=null && seriesdescriptionParam.length()!=0){
            seriesdescription = seriesdescriptionParam;
        }
        String institutionParam = param.getString(SysConstants.INSTITUTION_PARAM);
        if(institutionParam!=null && institutionParam.length()!=0){
            institution = institutionParam;
        }
        String sexParam = param.getString(SysConstants.SEX_PARAM);
        if(sexParam!=null && sexParam.length()!=0){
            sex = sexParam;
        }
        Integer ageStartParam = param.getInteger(SysConstants.AGE_START_PARAM);
        if(ageStartParam!=null && ageStartParam>=0){
            ageStart = ageStartParam;
        }
        Integer ageEndParam = param.getInteger(SysConstants.AGE_END_PARAM);
        if(ageEndParam!=null && ageEndParam>=0){
            ageEnd = ageEndParam;
        }
        String studyDateStartParam = param.getString(SysConstants.STUDYDATE_START_PARAM);
        if(studyDateStartParam!=null && studyDateStartParam.length()!=0){
            studydateStart = studyDateStartParam;
        }
        String studyDateEndParam = param.getString(SysConstants.STUDYDATE_END_PARAM);
        if(studyDateEndParam!=null && studyDateEndParam.length()!=0){
            studydateEnd = studyDateEndParam;
        }
        String entrydateStartParam = param.getString(SysConstants.ENTRYDATE_START_PARAM);
        if(entrydateStartParam!=null && entrydateStartParam.length()!=0){
            entrydateStart = entrydateStartParam;
        }
        String entryDateEndParam = param.getString(SysConstants.ENTRYDATE_END_PARAM);
        if(entryDateEndParam!=null && entryDateEndParam.length()!=0){
            entrydateEnd = entryDateEndParam;
        }
        Integer imageCountMinParam = param.getInteger(SysConstants.IMAGECOUNT_MIN_PARAM);
        if(imageCountMinParam!=null){
            imagecountMin = imageCountMinParam;
        }
        Integer imageCountMaxParam = param.getInteger(SysConstants.IMAGECOUNT_MAX_PARAM);
        if(imageCountMaxParam!=null){
            imagecountMax = imageCountMaxParam;
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
        return device != null;
    }
    public boolean isOrganAvailable(){
        return organ != null;
    }
    public boolean isSeriesdescriptionAvailable(){
        return seriesdescription != null;
    }
    public boolean isInstitutionAvailable(){
        return institution != null;
    }

    //sex:M,F,U    U:unknown
    public boolean isSexAvailable(){
        return sex != null;
    }
    public boolean isAgeStartAvailable(){
        return ageStart != null;
    }
    public boolean isAgeEndAvailable(){
        return ageEnd != null;
    }
    public boolean isStudydateStartAvailable(){
        return studydateStart != null;
    }
    public boolean isStudydateEndAvailable(){
        return studydateEnd != null;
    }
    public boolean isEntrydateStartAvailable(){
        return entrydateStart != null;
    }
    public boolean isEntrydateEndAvailable(){
        return entrydateEnd != null;
    }
    public boolean isImagecountMinAvailable(){
        return imagecountMin != null;
    }
    public boolean isImagecountMaxAvailable(){
        return imagecountMax != null;
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
    public boolean isdevicePhrase(){
        return devicephrase;
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

    public String getDevice() {
        return device;
    }

    public String getOrgan() {
        return organ;
    }

    public String getSeriesdescription() {
        return seriesdescription;
    }

    public String getInstitution() {
        return institution;
    }

    public String getSex() {
        return sex;
    }

    public Integer getAgeStart() {
        return ageStart;
    }

    public Integer getAgeEnd() {
        return ageEnd;
    }

    public String getStudydateStart() {
        return studydateStart;
    }

    public String getStudydateEnd() {
        return studydateEnd;
    }

    public String getEntrydateStart() {
        return entrydateStart;
    }

    public String getEntrydateEnd() {
        return entrydateEnd;
    }

    public Integer getImagecountMin() {
        return imagecountMin;
    }

    public Integer getImagecountMax() {
        return imagecountMax;
    }

    @Override
    public String toString() {
        return "PageSearchParamBean{" +
                "searchcondition=" + searchcondition +
                ", pageid=" + pageid +
                ", pagesize=" + pagesize +
                ", backfields=" + backfields +
                ", sortfields=" + sortfields +
                ", device='" + device + '\'' +
                ", organ='" + organ + '\'' +
                ", seriesdescription='" + seriesdescription + '\'' +
                ", institution='" + institution + '\'' +
                ", sex='" + sex + '\'' +
                ", ageStart=" + ageStart +
                ", ageEnd=" + ageEnd +
                ", studydateStart=" + studydateStart +
                ", studydateEnd=" + studydateEnd +
                ", entrydateStart=" + entrydateStart +
                ", entrydateEnd=" + entrydateEnd +
                ", imagecountMin=" + imagecountMin +
                ", imagecountMax=" + imagecountMax +
                ", isPaging=" + paging +
                '}';
    }
}
