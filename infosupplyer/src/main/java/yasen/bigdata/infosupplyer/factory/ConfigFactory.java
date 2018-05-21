package yasen.bigdata.infosupplyer.factory;

import org.apache.hadoop.conf.Configuration;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package yasen.bigdata.infosupplyer.factory
 * @Description: ${todo}
 * @date 2018/5/20 12:22
 */
public class ConfigFactory {
    public static Configuration getHdfsConfiguration(){
        Configuration hdfsConf = new Configuration();
        hdfsConf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
        return hdfsConf;
    }
}
