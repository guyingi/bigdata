package yasen.bigdata.milk.service;

import java.util.List;

public interface DataDownloadService {

    /**
     * 输入某个SeriesUID,返回该序列下所有dicom图片的缩略图的本地临时目录路径
     * @param id
     * @return
     */
    List<String> downloadDicomThumbnail(String id,String tempRealDir,String tempContextPath) throws Exception;


    /**
     *
     * @param tag
     * @return
     */
    String downloadDesensitizeDdicomByTag(String tag,String tempRealDir);
}
