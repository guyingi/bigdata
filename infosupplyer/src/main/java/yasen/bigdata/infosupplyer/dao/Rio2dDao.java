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
public interface Rio2dDao {

    /**
     * 此处认为表roi2d中的roi2d_series_uid字段与表series中的series_uid对应。
     * @param seriesuids
     * @return
     */
    public Map<String,String> getRoiCoordinateBySeriesuid(List<String> seriesuids);
}
