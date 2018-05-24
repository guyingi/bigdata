package yasen.bigdata.infosupplyer.dao;

import com.sun.jna.StringArray;
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
 * @date 2018/5/23 11:05
 */
public class BreastRoiDao {

    public Map<String,BreastRoiInfoBean> getBreastRoiInfoBySeriesuid(List<String> seriesuids){
        Map<String,BreastRoiInfoBean> result = new HashMap<>();
        if(seriesuids == null || seriesuids.size()==0){
            return result;
        }
        Connection connection = DBFactory.getConnection();
        String param = "";
        for(String temp : seriesuids){
            param += temp+",";
        }
        param = param.substring(0,param.length()-1);
        String sql = "select series_uid,location,classification,shape,boundary1,boundary2,density,quadrant,risk " +
                "from breast_roi " +
                "where series_uid in ("+param+")";
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            while(resultSet.next()){
                BreastRoiInfoBean breastRoiInfoBean = new BreastRoiInfoBean();
                breastRoiInfoBean.setSeries_uid(resultSet.getString(SysConstants.SERIES_UID));
                breastRoiInfoBean.setLocation(resultSet.getString(SysConstants.LOCATION));
                breastRoiInfoBean.setClassification(resultSet.getInt(SysConstants.CLASSIFICATION));
                breastRoiInfoBean.setShape(resultSet.getInt(SysConstants.SHAPE));
                breastRoiInfoBean.setBoundary1(resultSet.getInt(SysConstants.BOUNDARY1));
                breastRoiInfoBean.setBoundary2(resultSet.getInt(SysConstants.BOUNDARY2));
                breastRoiInfoBean.setDensity(resultSet.getInt(SysConstants.DENSITY));
                breastRoiInfoBean.setQuadrant(resultSet.getInt(SysConstants.QUADRANT));
                breastRoiInfoBean.setRisk(resultSet.getInt(SysConstants.RISK));
                result.put(breastRoiInfoBean.getSeries_uid(),breastRoiInfoBean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

}
