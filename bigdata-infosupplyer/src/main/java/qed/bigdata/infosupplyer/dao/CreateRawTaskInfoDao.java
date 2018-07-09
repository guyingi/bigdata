package qed.bigdata.infosupplyer.dao;

import qed.bigdata.infosupplyer.pojo.bigdata.CreateRawTaskInfo;

import java.util.List;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package qed.bigdata.infosupplyer.dao
 * @Description: ${todo}
 * @date 2018/7/9 15:12
 */
public interface CreateRawTaskInfoDao {

    /**
     * 列出新提交的脱敏任务
     * @return
     */
    List<CreateRawTaskInfo> listNew();

    /**
     * 更新tag的状态，如果脱敏成功状态为1，脱敏失败状态为2;
     * @param id
     * @param status
     */
    void updateStatus(Integer id,Integer status);

    /**
     * 提交一个脱敏任务到数据库，只需要指定tag即可staus默认为0
     * @param tag
     */
    void insert(String tag);

    /**
     * 查看对某个标签的脱敏任务是否已经存在，存在返回true,否则返回false
     * @param tag
     * @return
     */
    boolean isExists(String tag);
}
