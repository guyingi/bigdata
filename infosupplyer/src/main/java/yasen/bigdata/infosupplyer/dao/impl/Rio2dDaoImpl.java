package yasen.bigdata.infosupplyer.dao.impl;

import org.springframework.stereotype.Service;
import yasen.bigdata.infosupplyer.consts.SysConstants;
import yasen.bigdata.infosupplyer.dao.Rio2dDao;
import yasen.bigdata.infosupplyer.factory.DBFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package yasen.bigdata.infosupplyer.dao.impl
 * @Description: ${todo}
 * @date 2018/5/28 13:40
 */
@Service("Rio2dDao")
public class Rio2dDaoImpl implements Rio2dDao {
    public Map<String,String> getRoiCoordinateBySeriesuid(List<String> seriesuids){
        Map<String,String> result = new HashMap<String,String>();
        if(seriesuids == null || seriesuids.size()==0){
            return result;
        }
        Connection connection = DBFactory.getMarkToolConnection();
        String param = "";
        for(String temp : seriesuids){
            param += "'"+temp+"',";
        }
        param = param.substring(0,param.length()-1);
        String sql = "select roi2d_series_uid,roi2d_points " +
                "from roi2d " +
                "where roi2d_series_uid in ("+param+")";
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            while(resultSet.next()){
                result.put(resultSet.getString(SysConstants.ROI2D_SERIES_UID),resultSet.getString(SysConstants.ROI2D_POINTS));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
