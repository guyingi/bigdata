package yasen.bigdata.infosupplyer.service;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface DesensitizationService {

    /**给属于tag的dicom数据做脱敏操作
     * @param tag  给tag标签的这批数据做脱敏处理
     * @return     返回做脱敏成功的个数
     */
    Long desensitizedicom(String tag);

    /**
     *传脱敏数据的方法，传入存放脱敏数据的目录，该目录下面每个序列存放一个目录，
     * 每个序列下面就是该序列的脱敏数据，该序列目录名称为SeriesUID。该目录名承担着重要信息传递的作用
     *  yasen/bigdata/raw /标签/ year/month/day/文件
     *  文件：标签_患者名拼音年龄性别_【Info.csv、LCC.mhd、LCC.raw、LMLO.mhd、LMLO.raw、RCC.mhd、RCC.raw、RMLO.mhd、RMLO.raw、ROI.csv】
     * @param desensitizationDir
     * @param tag     外部传入标记，可能提供一个页面，在页面查询出一批数据，然后将这批数据脱敏，然后输入一个标记，打上去，完美
     * @return  返回上传成功的个数
     */
     int uploadDicomDesensitization(String desensitizationDir, String tag) throws IOException;


    /**
     * 这个方法是根据标签名下载所有属于该标签的脱敏数据，包括标记数据。
     * @param tag
     * @return 返回本地临时目录中的zip压缩文件
     */
     String downloadDesensitizeDicomByTag(String tag) throws Exception;
}
