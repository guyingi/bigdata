package yasen.bigdata.milk.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yasen.bigdata.milk.consts.DataTypeEnum;
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
        JSONObject criteriaDicom = new JSONObject();
        criteriaDicom.put(SysConstants.PATIENTNAME_PARAM,patientname);
        JSONObject tempDicom = searchService.searchDicomByPaging(criteriaDicom, null, null, 1, 1);
        if(tempDicom.getLong(SysConstants.TOTAL)>0){
            typeList.add(SysConstants.TYPE_DICOM);
        }

        //查询有没有edf
        JSONObject criteriaElectric = new JSONObject();
        criteriaElectric.put(SysConstants.PATIENTNAME_PARAM,patientname);
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
        JSONObject criteriaElectric = new JSONObject();
        criteriaElectric.put(SysConstants.PATIENTNAME_PARAM,"Xu^Su");
        JSONObject tempElectric = new SearchServiceImpl().searchElectricByPaging(criteriaElectric, null, null, 1, 1);
        if(tempElectric.getLong(SysConstants.TOTAL)>0){
            System.out.println("dsds");
        }
    }
}
