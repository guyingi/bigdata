package qed.bigdata.infosupplyer.service;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.util.List;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package yasen.bigdata.infosupplyer.service
 * @Description: ${todo}
 * @date 2018/5/20 10:34
 */
public interface HdfsService {

    boolean downloadDicom(List<String> paths, String localPath);

    boolean downloadElectric(List<String> paths, String localPath, Configuration hdfsconf) throws IOException;

    int upDicomDesensitization(String localDir, String remoteDir, Configuration hdfsconf) throws IOException;

    /**
     *
     * @param localDir
     * @param remoteDir
     * @param hdfsconf
     * @return  返回值的数组中包含两个元素，第一个是下载到本地后.mhd文件的绝对路径，第二个是raw文件在本地的绝对路径
     * @throws IOException
     */
    String[] downDicomDesensitization(String localDir, String remoteDir, Configuration hdfsconf) throws IOException;


    /**
     * 批量删除hdfs文件
     * @param path
     */
    void delFile(Path path);

    void copyDirToLocal(String remote,String local,Configuration hdfsconf);
}
