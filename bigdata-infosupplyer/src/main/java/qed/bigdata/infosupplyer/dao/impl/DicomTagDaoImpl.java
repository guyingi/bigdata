package qed.bigdata.infosupplyer.dao.impl;

import org.springframework.stereotype.Service;
import qed.bigdata.infosupplyer.pojo.bigdata.DicomTag;
import qed.bigdata.infosupplyer.dao.DicomTagDao;
import qed.bigdata.infosupplyer.factory.DBFactory;

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
 * @date 2018/5/28 14:05
 */
@Service("DicomTagDao")
public class DicomTagDaoImpl implements DicomTagDao {

    Connection bigdataConnection;

    public DicomTagDaoImpl(){
        bigdataConnection = DBFactory.getBigdataConnection();
    }

    @Override
    public boolean insert(DicomTag dicomTag){
        boolean isSuccess = false;
        if(bigdataConnection == null){
            bigdataConnection = DBFactory.getBigdataConnection();
        }
        String sql = "insert into dicomtag(`tagname`,`count`,`desensitize`,`describe`) values(?,?,?,?)";
        try {
            PreparedStatement preparedStatement = bigdataConnection.prepareStatement(sql);
            preparedStatement.setString(1,dicomTag.getTagname());
            preparedStatement.setLong(2,dicomTag.getCount());
            preparedStatement.setInt(3,dicomTag.getDesensitize());
            preparedStatement.setString(4,dicomTag.getDescribe());
            isSuccess = preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    @Override
    public boolean updateDesensitize(DicomTag dicomTag) {
        boolean isSuccess = false;
        if(bigdataConnection == null){
            bigdataConnection = DBFactory.getBigdataConnection();
        }
        String sql = "update dicomtag set desensitize=? where tagname=?";
        try {
            PreparedStatement preparedStatement = bigdataConnection.prepareStatement(sql);
            preparedStatement.setInt(1,dicomTag.getDesensitize());
            preparedStatement.setString(2,dicomTag.getTagname());
            isSuccess = preparedStatement.executeUpdate()>0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    @Override
    public boolean deleteByTag(String tag) {
        if(bigdataConnection == null){
            bigdataConnection = DBFactory.getBigdataConnection();
        }
        String sql = "delete from dicomtag where `tagname`=?";
        String checksql = "select * from dicomtag where `tagname`=?";
        try {
            PreparedStatement preparedStatement = bigdataConnection.prepareStatement(sql);
            preparedStatement.setString(1,tag);
            preparedStatement.execute();

            PreparedStatement preparedStatement1 = bigdataConnection.prepareStatement(checksql);
            preparedStatement1.setString(1,tag);
            ResultSet resultSet = preparedStatement1.executeQuery();
            if(resultSet.next()){
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public List<DicomTag> list() {
        List<DicomTag> result = new ArrayList<>();
        if(bigdataConnection == null){
            bigdataConnection = DBFactory.getBigdataConnection();
        }
        String sql = "select * from dicomtag";
        try {
            PreparedStatement preparedStatement = bigdataConnection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                DicomTag dicomTag = new DicomTag();
                dicomTag.setId(resultSet.getInt("id"));
                dicomTag.setTagname(resultSet.getString("tagname"));
                dicomTag.setCount(resultSet.getLong("count"));
                dicomTag.setDesensitize(resultSet.getInt("desensitize"));
                dicomTag.setDescribe(resultSet.getString("describe"));
                result.add(dicomTag);
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
