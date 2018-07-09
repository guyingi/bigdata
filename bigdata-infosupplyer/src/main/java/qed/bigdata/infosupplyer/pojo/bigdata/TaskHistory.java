package qed.bigdata.infosupplyer.pojo.bigdata;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package qed.bigdata.infosupplyer.pojo.bigdata
 * @Description: ${todo}
 * @date 2018/7/9 15:17
 */
public class TaskHistory {

    public Integer id;
    public Timestamp starttime;
    public Timestamp endtime;
    public String description;

    public TaskHistory() {
    }

    public TaskHistory(Integer id, Timestamp starttime, Timestamp endtime, String description) {
        this.id = id;
        this.starttime = starttime;
        this.endtime = endtime;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public Timestamp getStarttime() {
        return starttime;
    }

    public Timestamp getEndtime() {
        return endtime;
    }

    public String getDescription() {
        return description;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setStarttime(Timestamp starttime) {
        this.starttime = starttime;
    }

    public void setEndtime(Timestamp endtime) {
        this.endtime = endtime;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
