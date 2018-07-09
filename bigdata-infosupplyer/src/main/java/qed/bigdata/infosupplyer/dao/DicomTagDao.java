package qed.bigdata.infosupplyer.dao;

import qed.bigdata.infosupplyer.pojo.bigdata.DicomTag;

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
    boolean deleteByTag(String tag);
    List<DicomTag> list();
}
