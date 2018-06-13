package qed.bigdata.infosupplyer.conf;

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
import qed.bigdata.infosupplyer.util.InfoSupplyerTool;
import qed.bigdata.infosupplyer.consts.SysConsts;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class InfosupplyerConfiguration {
    Logger logger = Logger.getLogger(InfosupplyerConfiguration.class.getName());

    private String dirPrefixDicom = null;
    private String dirPrefixDesensitization = null; //存放脱敏数据的目录前缀

    private String escluster = null;
    private String esip = null;
    private String eshost = null;
    private String estcpport = null;
    private String eshttpport = null;
    private String indexDicom = null;
    private String indexDicomDisensitization = null;
    private String indexElectric = null;
    private String typeDicom = null;
    private String typeDicomDisensitization = null;
    private String typeElectric = null;


    private String zookeeperQuorum = null;
    private String zookeeperClientPort = null;

    private String dicomThumbnailTablename = null;
    private String dicomDisensitizationTablename = null;
    private String dicomThumbnailCf = null;
    private String dicomDisensitizationCf = null;
    private String dicomTablename = null;
    private String dicomCf = null;


    private String pythoncmd = null;
    private String pythonscript = null;


    /**********华丽丽的分割线：各种临时目录***********************/
    String thumbnailTempPath = null;
    String desensitizeBeforeTempPath = null;  //脱敏操作之前临时存放dicom的目录
    String desensitizeAfterTempPath = null;  //脱敏操作之后临时存放.mhd,.raw的目录
    String desensitizeDownloadTempPath = null;  //下载脱敏数据临时存放.mhd,.raw,.csv文件的目录
    String electrictempPath = null; //下载电信号edf文件临时目录。


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

        dirPrefixDicom = props.getProperty(SysConsts.DIR_PREFIX_DICOM);
        dirPrefixDesensitization = props.getProperty(SysConsts.DIR_PREFIX_DESENSITIZATION);
        escluster = props.getProperty(SysConsts.ES_CLUSTER);
        esip = props.getProperty(SysConsts.ES_IP);
        eshost = props.getProperty(SysConsts.ES_HOST);
        estcpport = props.getProperty(SysConsts.ES_TCPPORT);
        eshttpport = props.getProperty(SysConsts.ES_HTTPPORT);
        indexDicom = props.getProperty(SysConsts.ES_DICOM_INDEX);
        indexDicomDisensitization = props.getProperty(SysConsts.ES_DESENSITIZATION_INDEX);
        indexElectric = props.getProperty(SysConsts.ES_ELECTRIC_INDEX);
        typeDicom = props.getProperty(SysConsts.ES_DICOM_TYPE);
        typeDicomDisensitization = props.getProperty(SysConsts.ES_DESENSITIZATION_TYPE);
        typeElectric = props.getProperty(SysConsts.ES_ELECTRIC_TYPE);

        zookeeperQuorum = props.getProperty("hbase.zookeeper.quorum");
        zookeeperClientPort = props.getProperty("hbase.zookeeper.property.clientPort");

        dicomThumbnailTablename = props.getProperty(SysConsts.DICOM_THUMBNAIL_TABLENAME);
        dicomThumbnailCf = props.getProperty(SysConsts.DICOM_THUMBNAIL_CF);
        dicomTablename = props.getProperty(SysConsts.DICOM_TABLENAME);
        dicomCf = props.getProperty(SysConsts.DICOM_CF);
        dicomDisensitizationTablename = props.getProperty(SysConsts.DICOM_DESENSITIZATION_TABLENAME);
        dicomDisensitizationCf = props.getProperty(SysConsts.DICOM_DESENSITIZATION_CF);

        pythoncmd = props.getProperty(SysConsts.PYTHON_CMD);
        pythonscript = props.getProperty(SysConsts.PYTHON_SCRIPT);

        if((typeDicom==null || indexDicom==null || escluster==null) ||(esip==null && eshost==null)){
            logger.log(Level.ERROR,"必要配置加载失败，系统启动失败,退出系统");
//            System.out.println("必要配置加载失败，系统启动失败,退出系统");
            System.exit(0);
        }

        /**************获取jar文件目录，创建临时文件需要在这创建临时目录*******************/
        //缩略图的目录,/temp/thumbnailtemp
        thumbnailTempPath = InfoSupplyerTool.getRunnerPath()+ File.separator+SysConsts.TEMP_DIRNAME+File.separator
                +SysConsts.THUMBNAIL_TEMP_DIRNAME;
        //做脱敏操作的两个个目录,脱敏之前dicom存放目录，/temp/desensitizetemp/desensitizebeforetemp
        desensitizeBeforeTempPath = InfoSupplyerTool.getRunnerPath()+ File.separator+SysConsts.TEMP_DIRNAME+File.separator+
                SysConsts.DESENSITIZE_TEMP_DIRNAME+File.separator+SysConsts.DESENSITIZE_BEFORE_TEMP_DIRNAME;

        // 脱敏之后文件存放目录/temp/desensitizetemp/desensitizeaftertemp
        desensitizeAfterTempPath = InfoSupplyerTool.getRunnerPath()+ File.separator+SysConsts.TEMP_DIRNAME+File.separator+
                SysConsts.DESENSITIZE_TEMP_DIRNAME+File.separator+SysConsts.DESENSITIZE_AFTER_TEMP_DIRNAME;

        //下载脱敏数据的临时目录//temp/desensitizetemp/desensitizedownloadtemp
        desensitizeDownloadTempPath = InfoSupplyerTool.getRunnerPath()+ File.separator+SysConsts.TEMP_DIRNAME+File.separator+
                SysConsts.DESENSITIZE_TEMP_DIRNAME+File.separator+SysConsts.DESENSITIZE_TDOWNLOAD_TEMP_DIRNAME;

        //下载电信号edf文件临时目录//temp/electrictemp
        electrictempPath = InfoSupplyerTool.getRunnerPath()+ File.separator+SysConsts.TEMP_DIRNAME+File.separator+
                SysConsts.ELECTRIC_TEMP_DIRNAME;
        if(! new File(thumbnailTempPath).exists()){
            new File(thumbnailTempPath).mkdirs();
        }
        if(! new File(desensitizeBeforeTempPath).exists()){
            new File(desensitizeBeforeTempPath).mkdirs();
        }
        if(! new File(desensitizeAfterTempPath).exists()){
            new File(desensitizeAfterTempPath).mkdirs();
        }
        if(! new File(desensitizeDownloadTempPath).exists()){
            new File(desensitizeDownloadTempPath).mkdirs();
        }
        if(! new File(electrictempPath).exists()){
            new File(electrictempPath).mkdirs();
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

    public String getDicomTablename() {
        return dicomTablename;
    }

    public String getDicomCf() {
        return dicomCf;
    }

    public String getZookeeperQuorum() {
        return zookeeperQuorum;
    }

    public String getZookeeperClientPort() {
        return zookeeperClientPort;
    }

    public String getDirPrefixDesensitization() {
        return dirPrefixDesensitization;
    }

    public String getIndexDicomDisensitization() {
        return indexDicomDisensitization;
    }

    public String getTypeDicomDisensitization() {
        return typeDicomDisensitization;
    }

    public String getDicomDisensitizationTablename() {
        return dicomDisensitizationTablename;
    }

    public String getDicomDisensitizationCf() {
        return dicomDisensitizationCf;
    }

    public String getPythoncmd() {
        return pythoncmd;
    }

    public String getPythonscript() {
        return pythonscript;
    }


    public String getThumbnailTempPath() {
        return thumbnailTempPath;
    }

    public String getDesensitizeBeforeTempPath() {
        return desensitizeBeforeTempPath;
    }

    public String getDesensitizeAfterTempPath() {
        return desensitizeAfterTempPath;
    }

    public String getDesensitizeDownloadTempPath() {
        return desensitizeDownloadTempPath;
    }

    public String getElectrictempPath() {
        return electrictempPath;
    }

    public String getIndexElectric() {
        return indexElectric;
    }

    public String getTypeElectric() {
        return typeElectric;
    }

    @Override
    public String toString() {
        return "InfosupplyerConfiguration{" +
                "escluster='" + escluster + '\'' +
                ", esip='" + esip + '\'' +
                ", eshost='" + eshost + '\'' +
                ", estcpport='" + estcpport + '\'' +
                ", eshttpport='" + eshttpport + '\'' +
                ", indexDicom='" + indexDicom + '\'' +
                ", typeDicom='" + typeDicom + '\'' +
                ", zookeeperQuorum='" + zookeeperQuorum + '\'' +
                ", zookeeperClientPort='" + zookeeperClientPort + '\'' +
                ", dicomThumbnailTablename='" + dicomThumbnailTablename + '\'' +
                ", dicomThumbnailCf='" + dicomThumbnailCf + '\'' +
                ", dicomTablename='" + dicomTablename + '\'' +
                ", dicomCf='" + dicomCf + '\'' +
                '}';
    }
}
