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
public interface BreastRoiDao {

    public Map<String,BreastRoiInfoBean> getBreastRoiInfoBySeriesuid(List<String> seriesuids);

}
