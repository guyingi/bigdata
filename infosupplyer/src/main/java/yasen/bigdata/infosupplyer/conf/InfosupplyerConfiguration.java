package yasen.bigdata.infosupplyer.conf;

/**
 * @Title: InfosupplyerConfiguration.java
 * @Package yasen.bigdata.infosupplyer.conf
 * @Description: infosupplyer微服务项目配置类
 * @author weiguangwu
 * @date  2018/4/23 14:13
 * @version V1.0
 */

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import yasen.bigdata.infosupplyer.consts.SysConstants;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class InfosupplyerConfiguration {
    Logger logger = Logger.getLogger(InfosupplyerConfiguration.class.getName());

    private String escluster = null;
    private String esip = null;
    private String eshost = null;
    private String estcpport = null;
    private String eshttpport = null;
    private String indexDicom = null;
    private String typeDicom = null;

    private String zookeeperQuorum = null;
    private String zookeeperClientPort = null;

    private String dicomThumbnailTablename = null;
    private String dicomThumbnailCf = null;

    public InfosupplyerConfiguration(){
        init();
    }

    public void init(){
        InputStreamReader reader =new InputStreamReader(InfosupplyerConfiguration.class.getClassLoader().getResourceAsStream("infosupplyer.properties"));
        Properties props = new Properties();
        try {
            props.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        escluster = props.getProperty(SysConstants.ES_CLUSTER);
        esip = props.getProperty(SysConstants.ES_IP);
        eshost = props.getProperty(SysConstants.ES_HOST);
        estcpport = props.getProperty(SysConstants.ES_TCPPORT);
        eshttpport = props.getProperty(SysConstants.ES_HTTPPORT);
        indexDicom = props.getProperty(SysConstants.ES_DICOM_INDEX);
        typeDicom = props.getProperty(SysConstants.ES_DICOM_TYPE);

        zookeeperQuorum = props.getProperty("hbase.zookeeper.quorum");
        zookeeperClientPort = props.getProperty("hbase.zookeeper.property.clientPort");

        dicomThumbnailTablename = props.getProperty(SysConstants.DICOM_THUMBNAIL_TABLENAME);
        dicomThumbnailCf = props.getProperty(SysConstants.DICOM_THUMBNAIL_CF);

        if((typeDicom==null || indexDicom==null || escluster==null) ||(esip==null && eshost==null)){
            logger.log(Level.ERROR,"必要配置加载失败，系统启动失败,退出系统");
//            System.out.println("必要配置加载失败，系统启动失败,退出系统");
            System.exit(0);
        }
    }
    public String getEscluster() {
        return escluster;
    }

    public String getEsip() {
        return esip;
    }

    public String getEshost() {
        return eshost;
    }

    public String getEstcpport() {
        return estcpport;
    }
    public String getEshttpport() {
        return eshttpport;
    }

    public String getIndexDicom() {
        return indexDicom;
    }

    public String getTypeDicom() {
        return typeDicom;
    }

    public String getDicomThumbnailTablename() {
        return dicomThumbnailTablename;
    }

    public String getDicomThumbnailCf() {
        return dicomThumbnailCf;
    }

    public String getZookeeperQuorum() {
        return zookeeperQuorum;
    }

    public String getZookeeperClientPort() {
        return zookeeperClientPort;
    }

    @Override
    public String toString() {
        return "InfosupplyerConfiguration{" +
                "escluster='" + escluster + '\'' +
                ", esip='" + esip + '\'' +
                ", eshost='" + eshost + '\'' +
                ", esport='" + estcpport + '\'' +
                ", indexDicom='" + indexDicom + '\'' +
                ", typeDicom='" + typeDicom + '\'' +
                '}';
    }
}
