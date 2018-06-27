package qed.bigdata.infosupplyer.dao.impl;

import org.springframework.stereotype.Service;
import qed.bigdata.infosupplyer.consts.SysConsts;
import qed.bigdata.infosupplyer.pojo.BreastRoiInfoBean;
import qed.bigdata.infosupplyer.dao.BreastRoiDao;
import qed.bigdata.infosupplyer.factory.DBFactory;
import qed.bigdata.infosupplyer.pojo.marktool.BreastRoiEntity;

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
 * @Package yasen.bigdata.infosupplyer.dao.impl
 * @Description: ${todo}
 * @date 2018/5/28 13:38
 */
@Service("BreastRoiDao")
public class BreastRoiDaoImpl implements BreastRoiDao {
    public Map<String,BreastRoiInfoBean> getBreastRoiInfoBySeriesuid(List<String> seriesuids){
        Map<String,BreastRoiInfoBean> result = new HashMap<>();
        if(seriesuids == null || seriesuids.size()==0){
            return result;
        }
        Connection connection = DBFactory.getMarkToolConnection();
        String param = "";
        for(String temp : seriesuids){
            param += "'"+temp+"',";
        }
        param = param.substring(0,param.length()-1);
        String sql = "select series_uid,location,classification,shape,boundary1,boundary2,density,quadrant,risk " +
                "from breast_roi " +
                "where series_uid in ("+param+")";
        System.out.println(sql);
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            while(resultSet.next()){
                BreastRoiInfoBean breastRoiInfoBean = new BreastRoiInfoBean();
                breastRoiInfoBean.setSeries_uid(resultSet.getString(SysConsts.SERIES_UID));
                breastRoiInfoBean.setLocation(resultSet.getString(SysConsts.LOCATION));
                breastRoiInfoBean.setClassification(resultSet.getInt(SysConsts.CLASSIFICATION));
                breastRoiInfoBean.setShape(resultSet.getInt(SysConsts.SHAPE));
                breastRoiInfoBean.setBoundary1(resultSet.getInt(SysConsts.BOUNDARY1));
                breastRoiInfoBean.setBoundary2(resultSet.getInt(SysConsts.BOUNDARY2));
                breastRoiInfoBean.setDensity(resultSet.getInt(SysConsts.DENSITY));
                breastRoiInfoBean.setQuadrant(resultSet.getInt(SysConsts.QUADRANT));
                breastRoiInfoBean.setRisk(resultSet.getInt(SysConsts.RISK));
                result.put(breastRoiInfoBean.getSeries_uid(),breastRoiInfoBean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<BreastRoiEntity> getEntityBySeriesuid(String seriesuid) {
        List<BreastRoiEntity> result = new ArrayList<>();
        Connection connection = DBFactory.getMarkToolConnection();
        String sql = "select * from breast_roi where series_uid=?";
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1,seriesuid);
            ResultSet resultSet = ps.executeQuery();
            while(resultSet.next()){
                BreastRoiEntity entity = new BreastRoiEntity();

                entity.setSeries_uid(resultSet.getString(SysConsts.SERIES_UID));
                entity.setInstances_uid(resultSet.getString(SysConsts.INSTANCES_UID));
                entity.setLocation(resultSet.getString(SysConsts.LOCATION));
                entity.setClassification(resultSet.getInt(SysConsts.CLASSIFICATION));
                entity.setShape(resultSet.getInt(SysConsts.SHAPE));
                entity.setBoundary(resultSet.getString(SysConsts.BOUNDARY));
                entity.setBoundary1(resultSet.getInt(SysConsts.BOUNDARY1));
                entity.setBoundary2(resultSet.getInt(SysConsts.BOUNDARY2));
                entity.setDensity(resultSet.getInt(SysConsts.DENSITY));
                entity.setQuadrant(resultSet.getInt(SysConsts.QUADRANT));
                entity.setRisk(resultSet.getInt(SysConsts.RISK));
                entity.setPoints(resultSet.getString(SysConsts.POINTS));
                entity.setType(resultSet.getString(SysConsts.TYPE));
                entity.setUid(resultSet.getInt(SysConsts.UID));
                entity.setSeries_description(resultSet.getString(SysConsts.SERIES_DESCRIPTION));
                entity.setTool_state_manager(resultSet.getString(SysConsts.TOOL_STATE_MANAGER));
                entity.setRestore_data(resultSet.getString(SysConsts.RESTORE_DATA));

                result.add(entity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }


}
