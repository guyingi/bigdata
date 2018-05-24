package yasen.bigdata.infosupplyer.dao;

import yasen.bigdata.infosupplyer.consts.SysConstants;
import yasen.bigdata.infosupplyer.factory.DBFactory;
import yasen.bigdata.infosupplyer.pojo.BreastRoiInfoBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package yasen.bigdata.infosupplyer.dao
 * @Description: ${todo}
 * @date 2018/5/23 15:21
 */
public class Rio2dDao {

    /**
     * 此处认为表roi2d中的roi2d_series_uid字段与表series中的series_uid对应。
     * @param seriesuids
     * @return
     */
    public Map<String,String> getRoiCoordinateBySeriesuid(List<String> seriesuids){
        Map<String,String> result = new HashMap<String,String>();
        if(seriesuids == null || seriesuids.size()==0){
            return result;
        }
        Connection connection = DBFactory.getConnection();
        String param = "";
        for(String temp : seriesuids){
            param += temp+",";
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
