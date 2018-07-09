package qed.bigdata.infosupplyer.dao.impl;

import org.springframework.stereotype.Service;
import qed.bigdata.infosupplyer.dao.TaskHistoryDao;
import qed.bigdata.infosupplyer.factory.DBFactory;
import qed.bigdata.infosupplyer.pojo.bigdata.TaskHistory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package qed.bigdata.infosupplyer.dao.impl
 * @Description: ${todo}
 * @date 2018/7/9 15:16
 */

@Service("TaskHistoryDao")
public class TaskHistoryDaoImpl implements TaskHistoryDao {


    @Override
    public void insert(TaskHistory taskHistory) {
        Connection bigdataConnection = DBFactory.getBigdataConnection();
        String sql = "insert into taskhistory(`starttime`,`endtime`,`description`) values(?,?,?)";
        try {
            PreparedStatement preparedStatement = bigdataConnection.prepareStatement(sql);
            preparedStatement.setTimestamp(1,taskHistory.getStarttime());
            preparedStatement.setTimestamp(2,taskHistory.getEndtime());
            preparedStatement.setString(3,taskHistory.getDescription());
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
