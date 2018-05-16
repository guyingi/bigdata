package yasen.bigdata.infosupplyer.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.BufferedMutator;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.log4j.Logger;
import yasen.bigdata.infosupplyer.conf.InfosupplyerConfiguration;

import java.io.IOException;

public class HBaseUtil {
    static Logger logger = Logger.getLogger(HBaseUtil.class.getName());
    static InfosupplyerConfiguration infosupplyerConfiguration = null;
    static BufferedMutator.ExceptionListener listener = null;
    static Connection conn = null;
    static Configuration conf = HBaseConfiguration.create();

    static{
        infosupplyerConfiguration = new InfosupplyerConfiguration();
        conf.set("hbase.rootdir", "/hbase");
        conf.set("hbase.zookeeper.quorum", infosupplyerConfiguration.getZookeeperQuorum());
        conf.set("hbase.zookeeper.property.clientPort", infosupplyerConfiguration.getZookeeperClientPort());
        try {
            conn = ConnectionFactory.createConnection(conf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection(){
        return conn;
    }
}
