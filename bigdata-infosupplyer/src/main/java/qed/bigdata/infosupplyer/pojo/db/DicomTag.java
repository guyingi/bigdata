package qed.bigdata.infosupplyer.pojo.db;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package yasen.bigdata.infosupplyer.pojo
 * @Description: bigdata库中dicomtag表
 * @date 2018/5/28 13:49
 */
public class DicomTag {
    Integer id;
    String tagname;
    Long count;
    Integer desensitize;  //是否脱敏，0：否，1：是
    String describe;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTagname(String tagname) {
        this.tagname = tagname;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public void setDesensitize(Integer desensitize) {
        this.desensitize = desensitize;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public Integer getId() {
        return id;
    }

    public String getTagname() {
        return tagname;
    }

    public Long getCount() {
        return count;
    }

    public Integer getDesensitize() {
        return desensitize;
    }

    public String getDescribe() {
        return describe;
    }

    public boolean isDesensitize(){
        if(desensitize==1){
            return true;
        }else{
            return false;
        }
    }
}
