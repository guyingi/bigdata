package qed.bigdata.infosupplyer.dao.impl;

import org.springframework.stereotype.Service;
import qed.bigdata.infosupplyer.dao.CreateRawTaskInfoDao;
import qed.bigdata.infosupplyer.factory.DBFactory;
import qed.bigdata.infosupplyer.pojo.bigdata.CreateRawTaskInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package qed.bigdata.infosupplyer.dao.impl
 * @Description: ${todo}
 * @date 2018/7/9 15:13
 */

@Service("CreateRawTaskInfoDaoImpl")
public class CreateRawTaskInfoDaoImpl implements CreateRawTaskInfoDao {
    @Override
    public List<CreateRawTaskInfo> listNew() {
        List<CreateRawTaskInfo> result = new ArrayList<CreateRawTaskInfo>();
        Connection bigdataConnection = DBFactory.getBigdataConnection();
        String sql = "select * from createraw_taskinfo where status=0";
        try {
            PreparedStatement preparedStatement = bigdataConnection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                CreateRawTaskInfo createRawTaskInfo = new CreateRawTaskInfo();
                createRawTaskInfo.setId(resultSet.getInt("id"));
                createRawTaskInfo.setTag(resultSet.getString("tag"));
                createRawTaskInfo.setStatus(resultSet.getInt("status"));
                result.add(createRawTaskInfo);
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void updateStatus(Integer id, Integer status) {
        Connection bigdataConnection = DBFactory.getBigdataConnection();
        String sql = "update createraw_taskinfo set status=? where id=? ";
        try {
            PreparedStatement preparedStatement = bigdataConnection.prepareStatement(sql);
            preparedStatement.setInt(1,status);
            preparedStatement.setInt(2,id);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insert(String tag) {
        Connection bigdataConnection = DBFactory.getBigdataConnection();
        String sql = "insert into createraw_taskinfo(`tag`) values(?)";
        try {
            PreparedStatement preparedStatement = bigdataConnection.prepareStatement(sql);
            preparedStatement.setString(1,tag);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isExists(String tag) {
        Connection bigdataConnection = DBFactory.getBigdataConnection();
        String sql = "select * from createraw_taskinfo where tag=? and status=0";
        try {
            PreparedStatement preparedStatement = bigdataConnection.prepareStatement(sql);
            preparedStatement.setString(1,tag);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                preparedStatement.close();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
