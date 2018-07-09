package qed.bigdata.infosupplyer.pojo.bigdata;

import java.sql.Date;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package qed.bigdata.infosupplyer.pojo.bigdata
 * @Description: ${todo}
 * @date 2018/7/9 15:15
 */
public class CreateRawTaskInfo {

    public Integer id;
    public String tag;
    public Integer status;
    public Date createtime; // 这两个字段表自动维护
    public Date dealtime;

    public CreateRawTaskInfo() {
    }

    public CreateRawTaskInfo(Integer id, String tag, Integer status, Date createtime, Date dealtime) {
        this.id = id;
        this.tag = tag;
        this.status = status;
        this.createtime = createtime;
        this.dealtime = dealtime;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public void setDealtime(Date dealtime) {
        this.dealtime = dealtime;
    }

    public Integer getId() {
        return id;
    }

    public String getTag() {
        return tag;
    }

    public Integer getStatus() {
        return status;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public Date getDealtime() {
        return dealtime;
    }
}
