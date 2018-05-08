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
    private String index = null;
    private String type = null;

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
        index = props.getProperty(SysConstants.ES_INDEX);
        type = props.getProperty(SysConstants.ES_TYPE);

        if((type==null || index==null || escluster==null) ||(esip==null && eshost==null)){
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

    public String getIndex() {
        return index;
    }

    public String getType() {
        return type;
    }


    @Override
    public String toString() {
        return "InfosupplyerConfiguration{" +
                "escluster='" + escluster + '\'' +
                ", esip='" + esip + '\'' +
                ", eshost='" + eshost + '\'' +
                ", esport='" + estcpport + '\'' +
                ", index='" + index + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
