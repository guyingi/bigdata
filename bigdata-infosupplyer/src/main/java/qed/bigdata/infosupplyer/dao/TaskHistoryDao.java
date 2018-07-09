package qed.bigdata.infosupplyer.dao;

import qed.bigdata.infosupplyer.pojo.bigdata.TaskHistory;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package qed.bigdata.infosupplyer.dao
 * @Description: ${todo}
 * @date 2018/7/9 15:16
 */
public interface TaskHistoryDao {

    /**
     * 在taskhistory表中创建一条记录，返回该记录id.
     * @return
     */
    void insert(TaskHistory taskHistory);

}
