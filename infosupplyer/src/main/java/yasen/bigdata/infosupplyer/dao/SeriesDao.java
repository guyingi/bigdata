package yasen.bigdata.infosupplyer.dao;

import yasen.bigdata.infosupplyer.factory.DBFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package yasen.bigdata.infosupplyer.dao
 * @Description: ${todo}
 * @date 2018/5/23 11:04
 */
public interface SeriesDao {

    public String searchSingleFieldBySeriessop(String seriessop,String field);



}
