package qed.bigdata.infosupplyer.pojo;

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
import qed.bigdata.infosupplyer.consts.DataTypeEnum;
import qed.bigdata.infosupplyer.consts.EsConsts;
import qed.bigdata.infosupplyer.consts.SysConsts;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PageSearchParamBean {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private JSONArray criteria = null;
    private Integer pageid = 1;
    private Integer pagesize = 0;
    private List<String> backfields = null;
    private List<String> sortfields = null;
    private DataTypeEnum type;



    private boolean paging = false;
    private boolean parseError = false;


    public PageSearchParamBean(JSONObject param){
        init();

        try {
            JSONArray jsonArray = param.getJSONArray(SysConsts.CRITERIA);
            parseCriteria(jsonArray);

            Integer pageidParam = param.getInteger("pageid");
            Integer pagesizeParam = param.getInteger("pagesize");
            parsePageParam(pageidParam,pagesizeParam);

            String datatype = param.getString(SysConsts.DATATYPE);
            if(SysConsts.TYPE_DICOM.equals(datatype)){
                type = DataTypeEnum.DICOM;
            }else if(SysConsts.TYPE_ELECTRIC.equals(datatype)){
                type = DataTypeEnum.ELECTRIC;
            }else if(SysConsts.TYPE_KFB.equals(datatype)){
                type = DataTypeEnum.KFB;
            }else if(SysConsts.TYPE_GUAGE.equals(datatype)){
                type = DataTypeEnum.GUAGE;
            }else if(SysConsts.TYPE_MULTIDIMENSION.equals(datatype)){
                type = DataTypeEnum.MULTIDIMENSION;
            }

            JSONArray backfieldsParam = param.getJSONArray("backfields");
            parseBackfields(backfieldsParam,type);

            JSONArray sortfieldsParam = param.getJSONArray("sortfields");
            parseSortfields(sortfieldsParam,type);
        } catch (Exception e) {
            e.printStackTrace();
            parseError = true;
        }
    }

    /**
     * 下面方法的作用是将区间参数格式化为标准的区间参数，添加is等boolean数据。同时将int,long,double等数据进行格式转换
     * @param param
     */
    private void parseCriteria(JSONArray param){
        if(param==null)
            return;
        int size = param.size();
        for(int i=0; i<size; i++){
            JSONObject item = param.getJSONObject(i);
            if(item.getString(SysConsts.SECTION).equals(SysConsts.YES)){
                if(item.keySet().contains(SysConsts.START)){
                    item.put(SysConsts.IS_START_AVAILABLE,true);
                }else{
                    item.put(SysConsts.IS_START_AVAILABLE,false);
                }
                if(item.keySet().contains(SysConsts.END)){
                    item.put(SysConsts.IS_END_AVAILABLE,true);
                }else{
                    item.put(SysConsts.IS_END_AVAILABLE,false);
                }
                item.put(SysConsts.IS_SECTION,true);
            }else{
                item.put(SysConsts.IS_SECTION,false);
            }

            String keyword = item.getString(SysConsts.KEYWORD);

            if(item.getBoolean(SysConsts.IS_SECTION)){
                if(item.getBoolean(SysConsts.IS_START_AVAILABLE)){
                    String start = item.getString(SysConsts.START);
                    if (EsConsts.IntegerFieldList.contains(keyword)) {
                        item.replace(SysConsts.START, Integer.parseInt(start));
                    } else if (EsConsts.LongFieldList.contains(keyword)) {
                        item.replace(SysConsts.START, Long.parseLong(start));
                    } else if (EsConsts.DoubleFieldList.contains(keyword)) {
                        item.replace(SysConsts.START, Double.parseDouble(start));
                    }
                }
                if(item.getBoolean(SysConsts.IS_END_AVAILABLE)){
                    String end = item.getString(SysConsts.END);
                    if (EsConsts.IntegerFieldList.contains(keyword)) {
                        item.replace(SysConsts.END, Integer.parseInt(end));
                    } else if (EsConsts.LongFieldList.contains(keyword)) {
                        item.replace(SysConsts.END, Long.parseLong(end));
                    } else if (EsConsts.DoubleFieldList.contains(keyword)) {
                        item.replace(SysConsts.END, Double.parseDouble(end));
                    }
                }
            }else {
                String value = item.getString(SysConsts.VALUE);
                if (EsConsts.IntegerFieldList.contains(keyword)) {
                    item.replace(SysConsts.VALUE, Integer.parseInt(value));
                } else if (EsConsts.LongFieldList.contains(keyword)) {
                    item.replace(SysConsts.VALUE, Long.parseLong(value));
                } else if (EsConsts.DoubleFieldList.contains(keyword)) {
                    item.replace(SysConsts.VALUE, Double.parseDouble(value));
                }
            }
            criteria.add(item);
        }
    }

    private void parseBackfields(JSONArray backfieldsParam,DataTypeEnum type){
        //下面这两段if语句逻辑：如果backfields为空则使用默认返回字段，如果不为空，取backfields，sortfields交集为排序字段
        if(backfieldsParam != null && backfieldsParam.size() != 0){
            List<String> backfieldsList = backfieldsParam.toJavaList(String.class);
            if(backfieldsList.size()==1 && backfieldsList.get(0).equals("all")){
                backfields = null;
            }else {
                if(type == DataTypeEnum.DICOM) {
                    for (String e : backfieldsList) {
                        if (EsConsts.DCM_META_KEYWORD.contains(e)) {
                            backfields.add(e);
                        }
                    }
                }else if(type == DataTypeEnum.ELECTRIC) {
                    for (String e : backfieldsList) {
                        if (EsConsts.ES_ELECTRIC_FIELD.contains(e)) {
                            backfields.add(e);
                        }
                    }
                }
            }
        }else{
            if(type == DataTypeEnum.DICOM){
                backfields.addAll(EsConsts.DCM_DEFAULT_BACK_FIELD);
            }
        }
    }

    private void parseSortfields(JSONArray sortfieldsParam, DataTypeEnum type){
        if(type == DataTypeEnum.DICOM){
            if(sortfieldsParam!=null && sortfieldsParam.size()!=0){
                List<String> sortfieldsList = sortfieldsParam.toJavaList(String.class);
                for(String e : sortfieldsList){
                    if(backfields.contains(e)){
                        //解释：这里拼接.keyword，是因为es mapping中将keyword作为子field.添加之后会根据多单词文本的第一个单词字母表排序
                        //如果不添加，则会乱序
                        if(EsConsts.DCM_ES_TEXT_SORT_CHILD_FIELD.contains(e)) {
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

    }
    private void init(){
        backfields = new ArrayList<String>();
        sortfields = new ArrayList<String>();
        criteria = new JSONArray();
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


    public boolean isPageIdAvailable(){
        return pageid!=null && pageid>0;
    }
    public boolean isCriteriaAvailable(){
        return criteria!=null && criteria.size()>0;
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
    public JSONArray getCriteria() {
        return criteria;
    }

    public DataTypeEnum getType() {
        return type;
    }


    @Override
    public String toString() {
        String backfieldsStr = "";
        String sortfieldsStr = "";
        if(backfields!=null) {
            for (String e : backfields) {
                backfieldsStr += e;
            }
        }
        if(sortfields!=null) {
            for (String e : sortfields) {
                sortfieldsStr += e;
            }
        }
        return "PageSearchParamBean{" +
                "criteria=" + criteria +
                ", pageid=" + pageid +
                ", pagesize=" + pagesize +
                ", backfields=" + backfieldsStr +
                ", sortfields=" + sortfieldsStr +
                ", type=" + type +
                ", paging=" + paging +
                ", parseError=" + parseError +
                '}';
    }
}
