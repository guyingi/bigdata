package qed.bigdata.es.service;

import java.util.List;

public interface DataDownloadService {

    /**
     * 输入某个SeriesUID,返回该序列下所有dicom图片的缩略图的本地临时目录路径
     * @param id
     * @return
     */
    List<String> downloadDicomThumbnail(String id, String tempRealDir, String tempContextPath) throws Exception;


    /**
     *
     * @param tag
     * @return
     */
    String downloadDesensitizeDdicomByTag(String tag, String tempRealDir);


    /**
     * 下载患者的多维度数据，例如量表，dicom,脑电，等，
     * @param patientname 患者名称
     * @param datatypes  数据类型
     * @param tempPath  临时目录，方法会在该目录下操作，下载给前端后会删除该目录下面的垃圾文件
     * @return
     * @throws Exception
     */
    String downloadMutilTypeDataForPatient(String patientname, List<String> datatypes, String tempPath) throws Exception;

    /**
     * 根据ids下载电信号数据tempDir，返回tempDir目录，不做压缩工作
     * @param ids
     * @param tempDir
     * @return
     * @throws Exception
     */
    String downloadElectricByIds(List<String> ids, String tempDir) throws Exception;

    /**
     * 根据ids下载dicom数据到tempDir,返回tempDir目录，不做压缩工作
     * @param ids
     * @param tempDir
     * @return
     * @throws Exception
     */
    String downloadDicomByIds(List<String> ids, String tempDir) throws Exception;
}
