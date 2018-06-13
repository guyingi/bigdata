package qed.bigdata.infosupplyer.factory;

import org.apache.hadoop.conf.Configuration;
import qed.bigdata.infosupplyer.conf.InfosupplyerConfiguration;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package yasen.bigdata.infosupplyer.factory
 * @Description: ${todo}
 * @date 2018/5/20 12:22
 */
public class ConfigFactory {
    static InfosupplyerConfiguration infosupplyerConfiguration;
    static Configuration hdfsConf;

    public static Configuration getHdfsConfiguration(){
        if(hdfsConf == null){
            synchronized (ConfigFactory.class){
                if(hdfsConf == null){
                    hdfsConf = new Configuration();
                    hdfsConf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
                }
            }
        }
        return hdfsConf;
    }

    public static InfosupplyerConfiguration getInfosupplyerConfiguration(){
        if(infosupplyerConfiguration == null){
            synchronized (ConfigFactory.class){
                if(infosupplyerConfiguration == null){
                    infosupplyerConfiguration = new InfosupplyerConfiguration();
                }
            }
        }
        return infosupplyerConfiguration;
    }
}
