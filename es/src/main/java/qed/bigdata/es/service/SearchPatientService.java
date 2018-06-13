package qed.bigdata.es.service;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package yasen.bigdata.es.service
 * @Description: 这个service主要处理患者多维度数据关联查询相关业务
 * @date 2018/5/30 11:37
 */
public interface SearchPatientService {

    /**
     * 该方法接收患者名称，查询该患者基本信息，返回结果包括total,和具体rows数据，因为需要用于页面展示
     * @param name
     * @return
     */
    JSONObject getPatients(String name);


    List<String> getDataTypesOfPatient(String patientname);
}
