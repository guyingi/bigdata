package qed.bigdata.infosupplyer.dao;

import qed.bigdata.infosupplyer.pojo.marktool.Roi3dEntity;

import java.util.List;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package qed.bigdata.infosupplyer.dao
 * @Description: ${todo}
 * @date 2018/6/21 14:53
 */
public interface Roi3dDao {

    List<Roi3dEntity> getEntityBySeriesuid(String seriesuid);
}
