package yasen.bigdata.milk.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yasen.bigdata.milk.consts.DataTypeEnum;
import yasen.bigdata.milk.consts.ESConstants;
import yasen.bigdata.milk.consts.SysConstants;
import yasen.bigdata.milk.service.SearchPatientService;
import yasen.bigdata.milk.service.SearchService;
import yasen.bigdata.milk.tool.MilkTool;

import java.util.ArrayList;
import java.util.List;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package yasen.bigdata.milk.service.impl
 * @Description: ${todo}
 * @date 2018/5/31 10:31
 */
@Service("SearchPatientService")
public class SearchPatientServiceImpl implements SearchPatientService {

    @Autowired
    SearchService searchService;

    @Override
    public JSONObject getPatients(String name) {
        //得到的结果是直接可用的
        String interfaceStr = "/info/getpatients";
        JSONObject param = new JSONObject();
        param.put("patientname",name);
        DataTypeEnum type = DataTypeEnum.OTHER;
        JSONObject jsonObject = MilkTool.doCallAndGetResult(param, interfaceStr, type);
        JSONObject result = new JSONObject();
        if(SysConstants.CODE_000.equals(jsonObject.getString(SysConstants.CODE))){
            result.put(SysConstants.TOTAL,jsonObject.getLong(SysConstants.TOTAL));
            result.put(SysConstants.ROWS,jsonObject.getJSONArray(SysConstants.DATA));
        }
        return result;
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
