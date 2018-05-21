package yasen.bigdata.infosupplyer.service;

import org.apache.hadoop.conf.Configuration;

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

    int upDicomDesensitization(String localDir,String remoteDir,Configuration hdfsconf) throws IOException;
}
