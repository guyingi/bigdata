package qed.bigdata.infosupplyer.dao;

import qed.bigdata.infosupplyer.pojo.BreastRoiInfoBean;
import qed.bigdata.infosupplyer.pojo.marktool.BreastRoiEntity;

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

    Map<String,BreastRoiInfoBean> getBreastRoiInfoBySeriesuid(List<String> seriesuids);

    List<BreastRoiEntity> getEntityBySeriesuid(String seriesuid);
}
