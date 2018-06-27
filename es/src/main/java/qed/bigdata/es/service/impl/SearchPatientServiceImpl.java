package qed.bigdata.es.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import qed.bigdata.es.consts.ESConsts;
import qed.bigdata.es.consts.SysConsts;
import qed.bigdata.es.controller.DicomSearchController;
import qed.bigdata.es.service.SearchPatientService;
import qed.bigdata.es.service.SearchService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package yasen.bigdata.es.service.impl
 * @Description: ${todo}
 * @date 2018/5/31 10:31
 */
@Service("SearchPatientService")
public class SearchPatientServiceImpl implements SearchPatientService {
    static Logger logger = Logger.getLogger(SearchPatientServiceImpl.class);

    @Autowired
    SearchService searchService;

    @Override
    public JSONObject getPatients(String name) {
        logger.log(Level.INFO,"调用方法:getPatients,参数{name:"+name+"}");

        JSONObject result = new JSONObject();

        JSONObject param = new JSONObject();
        JSONArray criteria = new JSONArray();
        JSONObject obj = new JSONObject();
        obj.put(SysConsts.SECTION,SysConsts.NO);
        obj.put(SysConsts.KEYWORD,ESConsts.PatientName_ES);
        obj.put(SysConsts.VALUE,name);
        criteria.add(obj);
        param.put(SysConsts.CRITERIA,criteria);

        JSONArray backfields = new JSONArray();
        backfields.add(ESConsts.PatientName_ES);
        backfields.add(ESConsts.PatientsAge_ES);
        backfields.add(ESConsts.PatientsSex_ES);
        backfields.add(ESConsts.InstitutionName_ES);
        backfields.add(ESConsts.PatientUID_ES);
        param.put(SysConsts.BACKFIELDS,backfields);

        JSONObject jsonObject = searchService.searchDicomByPaging(criteria, backfields, null, 0, 0);
        if(SysConsts.CODE_000.equals(jsonObject.getString(SysConsts.CODE))){
            result.put(SysConsts.TOTAL,jsonObject.getLong(SysConsts.TOTAL));
            JSONArray tempData = jsonObject.getJSONArray(SysConsts.DATA);
            JSONArray data = removeDuplicate(tempData);
            result.put(SysConsts.ROWS,data);
        }
        logger.log(Level.INFO,"调用 getPatients 结束,返回结果:"+result.toJSONString());
        return result;
        /*//得到的结果是直接可用的
        解释：最初设计是从数据库查询患者信息，但是目前还没建立患者患者数据库，所以下面代码注释掉，采用直接查询ES的方式
        String interfaceStr = "/info/getpatients";
        JSONObject param = new JSONObject();
        param.put("patientname",name);
        DataTypeEnum type = DataTypeEnum.OTHER;
        JSONObject jsonObject = Tool.doCallAndGetResult(param, interfaceStr, type);
        JSONObject result = new JSONObject();
        if(SysConsts.CODE_000.equals(jsonObject.getString(SysConsts.CODE))){
            result.put(SysConsts.TOTAL,jsonObject.getLong(SysConsts.TOTAL));
            result.put(SysConsts.ROWS,jsonObject.getJSONArray(SysConsts.DATA));
        }*/

    }

    @Override
    public List<String> getDataTypesOfPatient(String patientname) {
        logger.log(Level.INFO,"调用方法:getDataTypesOfPatient,参数{patientname:"+patientname);

        List<String> typeList = new ArrayList<String>();

        //查询有没有dicom
//        JSONObject criteriaDicom = new JSONObject();
        JSONArray criteria = new JSONArray();
        JSONObject item = new JSONObject();
        item.put(SysConsts.SECTION,SysConsts.NO);
        item.put(SysConsts.KEYWORD,ESConsts.PatientName_ES);
        item.put(SysConsts.VALUE,patientname);
        criteria.add(item);

        JSONObject tempDicom = searchService.searchDicomByPaging(criteria, null, null, 1, 1);
        if(tempDicom.getLong(SysConsts.TOTAL)>0){
            typeList.add(SysConsts.TYPE_DICOM);
        }

        //查询有没有edf
        JSONArray criteriaElectric = new JSONArray();
        JSONObject itemElectri = new JSONObject();
        itemElectri.put(SysConsts.SECTION,SysConsts.NO);
        itemElectri.put(SysConsts.KEYWORD,ESConsts.PatientName_ES);
        itemElectri.put(SysConsts.VALUE,patientname);
        criteriaElectric.add(itemElectri);
        JSONObject tempElectric = searchService.searchElectricByPaging(criteriaElectric, null, null, 1, 1);
        if(tempElectric.getLong(SysConsts.TOTAL)>0){
            typeList.add(SysConsts.TYPE_ELECTRIC);
        }

        //查询有没有量表
        JSONArray typeListJson = JSON.parseArray(JSON.toJSONString(typeList));
        logger.log(Level.INFO,"调用 getDataTypesOfPatient 结束,返回结果:"+typeListJson.toJSONString());

        return typeList;
    }

    /**查询患者的时候调用分页查询接口，每个序列都会返回一条数据，下面的方法是将这些数据里面相同patientUID的去重*/
    private JSONArray removeDuplicate(JSONArray tempData){
        Set<String> set = new HashSet<String>();
        JSONArray data = new JSONArray();
        int size = tempData.size();
        for(int i=0; i<size;i++){
            JSONObject tempObj = tempData.getJSONObject(i);
            String patientUID = tempObj.getString(ESConsts.PatientUID_ES);
            if(!set.contains(patientUID)){
                set.add(patientUID);
                data.add(tempObj);
            }
        }
        return data;
    }

    public static void main(String[] args) {
//        JSONObject result = new SearchPatientServiceImpl().getPatients("Xu^Su");
//        System.out.println(result.toJSONString());
        JSONArray criteriaElectric = new JSONArray();
        JSONObject itemElectri = new JSONObject();
        itemElectri.put(SysConsts.SECTION,SysConsts.NO);
        itemElectri.put(SysConsts.KEYWORD,ESConsts.PatientName_ES);
        itemElectri.put(SysConsts.VALUE,"Xu^Su");
        criteriaElectric.add(itemElectri);
        JSONObject tempElectric = new SearchServiceImpl().searchElectricByPaging(criteriaElectric, null, null, 1, 1);
        if(tempElectric.getLong(SysConsts.TOTAL)>0){
            System.out.println("dsds");
        }
    }
}
