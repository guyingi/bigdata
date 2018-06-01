package yasen.bigdata.infosupplyer.dao.impl;

import org.springframework.stereotype.Service;
import yasen.bigdata.infosupplyer.dao.PatientDao;
import yasen.bigdata.infosupplyer.factory.DBFactory;
import yasen.bigdata.infosupplyer.pojo.db.Patient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package yasen.bigdata.infosupplyer.dao.impl
 * @Description: ${todo}
 * @date 2018/5/30 21:39
 */
@Service("PatientDao")
public class PatientDaoImpl implements PatientDao {

    Connection bigdataConnection;

    public PatientDaoImpl(){
        bigdataConnection = DBFactory.getBigdataConnection();
    }


    @Override
    public List<Patient> getPatientByName(String name) {
        List<Patient> result = new ArrayList<>();
        if(bigdataConnection == null){
            System.out.println("没有获取到连接");
            return result;
        }
        String sql = "select * from patient where name=?";
        try {
            PreparedStatement preparedStatement = bigdataConnection.prepareStatement(sql);
            preparedStatement.setString(1,name);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                System.out.println("wocao ");
                Patient patient = new Patient();
                patient.setPatientname(resultSet.getString("name"));
                patient.setAge(resultSet.getInt("age"));
                patient.setSex(resultSet.getString("sex"));
                patient.setHospital(resultSet.getString("hospital"));
                result.add(patient);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

}
