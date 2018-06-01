package yasen.bigdata.infosupplyer.dao;

import org.springframework.stereotype.Service;
import yasen.bigdata.infosupplyer.pojo.db.Patient;

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
