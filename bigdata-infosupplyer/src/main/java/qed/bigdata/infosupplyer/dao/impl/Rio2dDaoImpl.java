package qed.bigdata.infosupplyer.dao.impl;

import org.springframework.stereotype.Service;
import qed.bigdata.infosupplyer.consts.SysConsts;
import qed.bigdata.infosupplyer.dao.Rio2dDao;
import qed.bigdata.infosupplyer.factory.DBFactory;
import qed.bigdata.infosupplyer.pojo.marktool.Roi2dEntity;

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
 * @date 2018/5/28 13:40
 */
@Service("Rio2dDao")
public class Rio2dDaoImpl implements Rio2dDao {

    @Override
    public List<Roi2dEntity> getEntityBySeriesuid(String seriesuid) {
        List<Roi2dEntity> result = new ArrayList<>();
        Connection connection = DBFactory.getMarkToolConnection();
        String sql = "select * from roi2d where roi2d_series_uid=? order by roi3d_num";
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1,seriesuid);
            ResultSet resultSet = ps.executeQuery();
            while(resultSet.next()){
                Roi2dEntity entity = new Roi2dEntity();
                entity.setRoi2d_dim(resultSet.getInt(SysConsts.ROI2D_DIM));
                entity.setRoi2d_instances_uid(resultSet.getString(SysConsts.ROI2D_INSTANCES_UID));
                entity.setRoi2d_points(resultSet.getString(SysConsts.ROI2D_POINTS));
                entity.setRoi2d_series_uid(resultSet.getString(SysConsts.ROI2D_SERIES_UID));
                entity.setRoi2d_slice(resultSet.getInt(SysConsts.ROI2D_SLICE));
                entity.setRoi3d_num(resultSet.getInt(SysConsts.ROI3D_NUM));
                result.add(entity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
