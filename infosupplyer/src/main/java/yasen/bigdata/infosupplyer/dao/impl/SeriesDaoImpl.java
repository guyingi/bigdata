package yasen.bigdata.infosupplyer.dao.impl;

import org.springframework.stereotype.Service;
import yasen.bigdata.infosupplyer.dao.SeriesDao;
import yasen.bigdata.infosupplyer.factory.DBFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package yasen.bigdata.infosupplyer.dao.impl
 * @Description: ${todo}
 * @date 2018/5/28 13:36
 */
@Service("SeriesDao")
public class SeriesDaoImpl implements SeriesDao {
    public String searchSingleFieldBySeriessop(String seriessop,String field){
        Connection connection = DBFactory.getMarkToolConnection();
        String sql = "select +"+field+" from series where series_sop=?";
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1,seriessop);
            resultSet = ps.executeQuery();
            if(resultSet != null){
                if(resultSet.next()){
                    String series_uid = resultSet.getString(field);
                    return series_uid;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
