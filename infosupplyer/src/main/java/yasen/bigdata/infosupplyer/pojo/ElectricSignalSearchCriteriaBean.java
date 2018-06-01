package yasen.bigdata.infosupplyer.pojo;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import yasen.bigdata.infosupplyer.consts.SysConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package yasen.bigdata.infosupplyer.pojo
 * @Description: ${todo}
 * @date 2018/5/29 16:42
 */
public class ElectricSignalSearchCriteriaBean {

    private SimpleDateFormat sdf;
    private String patientUID;
    private String patientName;
    private Integer ageStart;
    private Integer ageEnd;
    private String createdateStart;
    private String createdateEnd;
    private String institutionName;
    private String entrydateStart;
    private String entrydateEnd;

    public ElectricSignalSearchCriteriaBean(JSONObject searchCriteria){
        sdf = new SimpleDateFormat("YYYYMMDD");
        parseSearchCriteria(searchCriteria);
    }

    private void parseSearchCriteria(JSONObject searchCriteria){
        String patientuidParam = searchCriteria.getString(SysConstants.PatientUID_ELECTRIC_PARAM);
        if(!StringUtils.isBlank(patientuidParam)){
            patientUID = patientuidParam;
        }
        String patientNamParam = searchCriteria.getString(SysConstants.PatientName_ELECTRIC_PARAM);
        if(!StringUtils.isBlank(patientNamParam)){
            patientName = patientNamParam;
        }
        String patientAgeStartParam = searchCriteria.getString(SysConstants.AGE_START_ELECTRIC_PARAM);
        if(!StringUtils.isBlank(patientAgeStartParam)){
            ageStart = Integer.parseInt(patientAgeStartParam);
        }
        String patientAgeEndParam = searchCriteria.getString(SysConstants.AGE_END_ELECTRIC_PARAM);
        if(!StringUtils.isBlank(patientAgeEndParam)){
            ageEnd = Integer.parseInt(patientAgeEndParam);
        }
        String createdateStartParam = searchCriteria.getString(SysConstants.CREATEDATE_START_ELECTRIC_PARAM);
        if(!StringUtils.isBlank(createdateStartParam)){
            createdateStart = verificatyDateParam(createdateStartParam);
        }
        String createdateEndParam = searchCriteria.getString(SysConstants.CREATEDATE_END_ELECTRIC_PARAM);
        if(!StringUtils.isBlank(createdateEndParam)){
            createdateEnd = verificatyDateParam(createdateEndParam);
        }
        String institutionNameParam = searchCriteria.getString(SysConstants.InstitutionName_ELECTRIC_PARAM);
        if(!StringUtils.isBlank(institutionNameParam)){
            institutionName = institutionNameParam;
        }
        String entryDateStartParam = searchCriteria.getString(SysConstants.ENTRYDATE_START_ELECTRIC_PARAM);
        if(!StringUtils.isBlank(entryDateStartParam)){
            entrydateStart = entryDateStartParam;
        }
        String entryDateEndParam = searchCriteria.getString(SysConstants.ENTRYDATE_END_ELECTRIC_PARAM);
        if(!StringUtils.isBlank(entryDateEndParam)){
            entrydateEnd = entryDateEndParam;
        }
    }

    public boolean isPatientUIDAvailable(){ return patientUID != null; }
    public boolean isPatientNameAvailable(){ return patientName != null; }
    public boolean isAgeStartAvailable(){ return ageStart != null; }
    public boolean isAgeEndAvailable(){ return ageEnd != null; }
    public boolean isCreatedateStartAvailable(){ return createdateStart != null; }
    public boolean isCreatedateEndAvailable(){ return createdateEnd != null; }
    public boolean isInstitutionNameAvailable(){ return institutionName != null; }
    public boolean isEntrydateStartAvailable(){ return entrydateStart != null; }
    public boolean isEntrydateEndAvailable(){ return entrydateEnd != null; }

    public String getPatientUID() {
        return patientUID;
    }

    public String getPatientName() {
        return patientName;
    }

    public Integer getAgeStart() {
        return ageStart;
    }
    public Integer getAgeEnd() {
        return ageEnd;
    }

    public String getCreatedateStart() {
        return createdateStart;
    }

    public String getCreatedateEnd() {
        return createdateEnd;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public String getEntrydateStart() {
        return entrydateStart;
    }

    public String getEntrydateEnd() {
        return entrydateEnd;
    }

    private String verificatyDateParam(String param){
        try {
            sdf.parse(param);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
        return param;
    }


}
