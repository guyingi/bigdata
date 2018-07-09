package qed.bigdata.infosupplyer.dao;

import qed.bigdata.infosupplyer.pojo.bigdata.Patient;

import java.util.List;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package yasen.bigdata.infosupplyer.dao
 * @Description: bigdata库中patient表
 * @date 2018/5/30 17:31
 */
public interface PatientDao {

    List<Patient> getPatientByName(String name);


}
