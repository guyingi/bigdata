package qed.bigdata.es.conf;

/**
 * @Title: ESConsts.java
 * @Package yasen.bigdata.es.conf
 * @Description: milk项目配置类
 * @author weiguangwu
 * @date  2018/4/23 14:13
 * @version V1.0
 */

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import qed.bigdata.es.consts.SysConsts;

public class MilkConfiguration {

	Logger logger = Logger.getLogger(MilkConfiguration.class.getName());


	private String infosupplyerip = null;
	private String infosupplyerport = null;
	private String infosupplyername = null;//微服务项目名称
	private String defaultFs = null;

	private Long downloadThreshhold = null;

	public MilkConfiguration(){
	        init();
	    }

	public void init(){

		InputStreamReader reader =new InputStreamReader(MilkConfiguration.class.getClassLoader().getResourceAsStream("es.properties"));
		Properties props = new Properties();
		try {
			props.load(reader);
		} catch (IOException e) {
			e.printStackTrace();
		}
		infosupplyerip = props.getProperty(SysConsts.INFOSUPPLYER_IP);
		infosupplyerport = props.getProperty(SysConsts.INFOSUPPLYER_PORT);
		defaultFs = props.getProperty(SysConsts.FS__DEFAULTFS);
		downloadThreshhold = Long.parseLong(props.getProperty(SysConsts.DOWNLOAD_THRESHHOLD));

		if((infosupplyerip==null || infosupplyerport==null)){
			logger.log(Level.ERROR,"必要配置加载失败，系统启动失败,退出系统");
			System.exit(0);
		}
	}

	public String getInfosupplyerip() {
		return infosupplyerip;
	}


	public String getInfosupplyerport() {
		return infosupplyerport;
	}


	public String getInfosupplyername() {
		return infosupplyername;
	}
	public String getDefaultFs() {
		return defaultFs;
	}

	public Long getDownloadThreshhold() {
		return downloadThreshhold;
	}

	@Override
	public String toString() {
		return "MilkConfiguration [infosupplyerip=" + infosupplyerip + ", infosupplyerport=" + infosupplyerport
				+ ", infosupplyername=" + infosupplyername + "]";
	}
		

}	
