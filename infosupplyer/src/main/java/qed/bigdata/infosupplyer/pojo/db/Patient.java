package qed.bigdata.infosupplyer.pojo.db;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package yasen.bigdata.infosupplyer.pojo.db
 * @Description: ${todo}
 * @date 2018/5/30 17:32
 */
public class Patient {
    Integer id;
    String patientname;
    Integer age;
    String sex;
    String hospital;

    public Patient(){};
    public Patient(Integer id, String patientname, Integer age, String sex, String hospital) {
        this.id = id;
        this.patientname = patientname;
        this.age = age;
        this.sex = sex;
        this.hospital = hospital;
    }

    public Integer getId() {
        return id;
    }

    public String getPatientname() {
        return patientname;
    }

    public Integer getAge() {
        return age;
    }

    public String getSex() {
        return sex;
    }

    public String getHospital() {
        return hospital;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setPatientname(String patientname) {
        this.patientname = patientname;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }
}
