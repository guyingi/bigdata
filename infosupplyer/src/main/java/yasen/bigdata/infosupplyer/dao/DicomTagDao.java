package yasen.bigdata.infosupplyer.dao;

import yasen.bigdata.infosupplyer.pojo.db.DicomTag;

import java.sql.SQLException;
import java.util.List;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package yasen.bigdata.infosupplyer.dao
 * @Description: ${todo}
 * @date 2018/5/28 13:48
 */
public interface DicomTagDao {
    boolean insert(DicomTag dicomTag);
    boolean updateDesensitize(DicomTag dicomTag);
    List<DicomTag> list();
}
