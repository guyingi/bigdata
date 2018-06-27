package qed.bigdata.infosupplyer.dao.impl;

import org.springframework.stereotype.Service;
import qed.bigdata.infosupplyer.consts.SysConsts;
import qed.bigdata.infosupplyer.dao.Roi3dDao;
import qed.bigdata.infosupplyer.factory.DBFactory;
import qed.bigdata.infosupplyer.pojo.marktool.BreastRoiEntity;
import qed.bigdata.infosupplyer.pojo.marktool.Roi3dEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package qed.bigdata.infosupplyer.dao.impl
 * @Description: ${todo}
 * @date 2018/6/21 16:37
 */
@Service("Roi3dDao")
public class Roi3dDaoImpl implements Roi3dDao {

    @Override
    public List<Roi3dEntity> getEntityBySeriesuid(String seriesuid) {
        List<Roi3dEntity> result = new ArrayList<>();
        Connection connection = DBFactory.getMarkToolConnection();
        String sql = "select * from roi3d where roi3d_series_uid=? order by roi3d_num";
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1,seriesuid);
            ResultSet resultSet = ps.executeQuery();
            while(resultSet.next()){
                Roi3dEntity entity = new Roi3dEntity();
                entity.setRoi3d_color(resultSet.getString(SysConsts.ROI3D_COLOR));
                entity.setRoi3d_follow_up(resultSet.getInt(SysConsts.ROI3D_FOLLOW_UP));
                entity.setRoi3d_nodal_position(resultSet.getInt(SysConsts.ROI3D_NODAL_POSITION));
                entity.setRoi3d_nodule_analysis(resultSet.getInt(SysConsts.ROI3D_NODULE_ANALYSIS));
                entity.setRoi3d_num(resultSet.getInt(SysConsts.ROI3D_NUM));
                entity.setRoi3d_risk_assessment(resultSet.getInt(SysConsts.ROI3D_RISK_ASSESSMENT));
                entity.setRoi3d_series_uid(resultSet.getString(SysConsts.ROI3D_SERIES_UID));
                entity.setRoi3d_signs(resultSet.getInt(SysConsts.ROI3D_SIGNS));
                entity.setRoi3d_type(resultSet.getInt(SysConsts.ROI3D_TYPE));
                entity.setRoi3d_width(resultSet.getInt(SysConsts.ROI3D_WIDTH));
                result.add(entity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
