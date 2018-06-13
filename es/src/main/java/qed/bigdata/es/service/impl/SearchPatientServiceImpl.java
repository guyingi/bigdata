package qed.bigdata.es.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import qed.bigdata.es.consts.ESConstants;
import qed.bigdata.es.service.SearchPatientService;
import qed.bigdata.es.consts.SysConstants;
import qed.bigdata.es.service.SearchService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package yasen.bigdata.es.service.impl
 * @Description: ${todo}
 * @date 2018/5/31 10:31
 */
@Service("SearchPatientService")
public class SearchPatientServiceImpl implements SearchPatientService {

    @Autowired
    SearchService searchService;

    @Override
    public JSONObject getPatients(String name) {

        JSONObject result = new JSONObject();

        JSONObject param = new JSONObject();
        JSONArray criteria = new JSONArray();
        JSONObject obj = new JSONObject();
        obj.put(SysConstants.SECTION,SysConstants.NO);
        obj.put(SysConstants.KEYWORD,ESConstants.PatientName_ES);
        obj.put(SysConstants.VALUE,name);
        criteria.add(obj);
        param.put(SysConstants.CRITERIA,criteria);

        JSONArray backfields = new JSONArray();
        backfields.add(ESConstants.PatientName_ES);
        backfields.add(ESConstants.PatientsAge_ES);
        backfields.add(ESConstants.PatientsSex_ES);
        backfields.add(ESConstants.InstitutionName_ES);
        param.put(SysConstants.BACKFIELDS,backfields);

        JSONObject jsonObject = searchService.searchDicomByPaging(criteria, backfields, null, 0, 0);
        if(SysConstants.CODE_000.equals(jsonObject.getString(SysConstants.CODE))){
            result.put(SysConstants.TOTAL,jsonObject.getLong(SysConstants.TOTAL));
            result.put(SysConstants.ROWS,jsonObject.getJSONArray(SysConstants.DATA));
        }
        return result;
        /*//得到的结果是直接可用的
        解释：最初设计是从数据库查询患者信息，但是目前还没建立患者患者数据库，所以下面代码注释掉，采用直接查询ES的方式
        String interfaceStr = "/info/getpatients";
        JSONObject param = new JSONObject();
        param.put("patientname",name);
        DataTypeEnum type = DataTypeEnum.OTHER;
        JSONObject jsonObject = MilkTool.doCallAndGetResult(param, interfaceStr, type);
        JSONObject result = new JSONObject();
        if(SysConstants.CODE_000.equals(jsonObject.getString(SysConstants.CODE))){
            result.put(SysConstants.TOTAL,jsonObject.getLong(SysConstants.TOTAL));
            result.put(SysConstants.ROWS,jsonObject.getJSONArray(SysConstants.DATA));
        }*/

    }

    @Override
    public List<String> getDataTypesOfPatient(String patientname) {
        List<String> typeList = new ArrayList<String>();

        //查询有没有dicom
//        JSONObject criteriaDicom = new JSONObject();
        JSONArray criteria = new JSONArray();
        JSONObject item = new JSONObject();
        item.put(SysConstants.SECTION,SysConstants.NO);
        item.put(SysConstants.KEYWORD,ESConstants.PatientName_ES);
        item.put(SysConstants.VALUE,patientname);
        criteria.add(item);

        JSONObject tempDicom = searchService.searchDicomByPaging(criteria, null, null, 1, 1);
        if(tempDicom.getLong(SysConstants.TOTAL)>0){
            typeList.add(SysConstants.TYPE_DICOM);
        }

        //查询有没有edf
        JSONArray criteriaElectric = new JSONArray();
        JSONObject itemElectri = new JSONObject();
        itemElectri.put(SysConstants.SECTION,SysConstants.NO);
        itemElectri.put(SysConstants.KEYWORD,ESConstants.PatientName_ES);
        itemElectri.put(SysConstants.VALUE,patientname);
        criteriaElectric.add(itemElectri);
        JSONObject tempElectric = searchService.searchElectricByPaging(criteriaElectric, null, null, 1, 1);
        if(tempElectric.getLong(SysConstants.TOTAL)>0){
            typeList.add(SysConstants.TYPE_ELECTRIC);
        }

        //查询有没有量表


        return typeList;
    }


    public static void main(String[] args) {
//        JSONObject result = new SearchPatientServiceImpl().getPatients("Xu^Su");
//        System.out.println(result.toJSONString());
        JSONArray criteriaElectric = new JSONArray();
        JSONObject itemElectri = new JSONObject();
        itemElectri.put(SysConstants.SECTION,SysConstants.NO);
        itemElectri.put(SysConstants.KEYWORD,ESConstants.PatientName_ES);
        itemElectri.put(SysConstants.VALUE,"Xu^Su");
        criteriaElectric.add(itemElectri);
        JSONObject tempElectric = new SearchServiceImpl().searchElectricByPaging(criteriaElectric, null, null, 1, 1);
        if(tempElectric.getLong(SysConstants.TOTAL)>0){
            System.out.println("dsds");
        }
    }
}
