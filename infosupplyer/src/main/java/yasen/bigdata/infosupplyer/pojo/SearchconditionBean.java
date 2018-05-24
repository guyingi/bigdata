package yasen.bigdata.infosupplyer.pojo;

import com.alibaba.fastjson.JSONObject;
import yasen.bigdata.infosupplyer.consts.SysConstants;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package yasen.bigdata.infosupplyer.pojo
 * @Description: ${todo}
 * @date 2018/5/17 16:27
 */
public class SearchconditionBean {
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
    private Double slicethicknessMin;
    private Double slicethicknessMax;
    private String tag;
    private boolean devicephrase = false;//设备是否整个串匹配，拆分,默认模糊匹配

    public SearchconditionBean(JSONObject searchcondition){
        parseSearchcondition(searchcondition);
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
        Double slicethicknessMinParam = param.getDouble(SysConstants.SLICE_THICKNESS_MIN_PARAM);
        if(slicethicknessMinParam != null){
            slicethicknessMin = slicethicknessMinParam;
        }
        Double slicethicknessMaxParam = param.getDouble(SysConstants.SLICE_THICKNESS_MAX_PARAM);
        if(slicethicknessMaxParam != null){
            slicethicknessMax = slicethicknessMaxParam;
        }
        String tagParam = param.getString(SysConstants.TAG_PARAM);
        if(tagParam != null){
            tag = tagParam;
        }

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
    public boolean isSlicethicknessMinAvailable(){
        return slicethicknessMin != null;
    }
    public boolean isSlicethicknessMaxAvailable(){
        return slicethicknessMax != null;
    }
    public boolean isdevicePhrase(){
        return devicephrase;
    }
    public boolean isTagAvailable(){
        return tag != null;
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

    public Double getSlicethicknessMin() {
        return slicethicknessMin;
    }

    public Double getSlicethicknessMax() {
        return slicethicknessMax;
    }

    public String getTag(){ return tag; }

    @Override
    public String toString() {
        return "SearchconditionBean{" +
                "device='" + device + '\'' +
                ", organ='" + organ + '\'' +
                ", seriesdescription='" + seriesdescription + '\'' +
                ", institution='" + institution + '\'' +
                ", sex='" + sex + '\'' +
                ", ageStart=" + ageStart +
                ", ageEnd=" + ageEnd +
                ", studydateStart='" + studydateStart + '\'' +
                ", studydateEnd='" + studydateEnd + '\'' +
                ", entrydateStart='" + entrydateStart + '\'' +
                ", entrydateEnd='" + entrydateEnd + '\'' +
                ", imagecountMin=" + imagecountMin +
                ", imagecountMax=" + imagecountMax +
                ", slicethicknessMin=" + slicethicknessMin +
                ", slicethicknessMax=" + slicethicknessMax +
                ", tag='" + tag + '\'' +
                ", devicephrase=" + devicephrase +
                '}';
    }
}
