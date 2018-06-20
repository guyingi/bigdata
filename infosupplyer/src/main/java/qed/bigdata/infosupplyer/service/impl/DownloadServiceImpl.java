package qed.bigdata.infosupplyer.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.hadoop.conf.Configuration;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import qed.bigdata.infosupplyer.conf.InfosupplyerConfiguration;
import qed.bigdata.infosupplyer.consts.DataTypeEnum;
import qed.bigdata.infosupplyer.consts.EsConsts;
import qed.bigdata.infosupplyer.consts.SysConsts;
import qed.bigdata.infosupplyer.util.InfoSupplyerTool;
import qed.bigdata.infosupplyer.factory.ConfigFactory;
import qed.bigdata.infosupplyer.service.DownloadService;
import qed.bigdata.infosupplyer.service.ElasticSearchService;
import qed.bigdata.infosupplyer.service.HdfsService;
import qed.bigdata.infosupplyer.util.ZipUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package yasen.bigdata.infosupplyer.service.impl
 * @Description: ${todo}
 * @date 2018/5/31 20:12
 */
@Service("DownloadService")
public class DownloadServiceImpl implements DownloadService {
    static Logger logger = Logger.getLogger(DownloadServiceImpl.class);

    InfosupplyerConfiguration infosupplyerConfiguration = null;
    Configuration hdfsconf = null;

    @Autowired
    ElasticSearchService elasticSearchService;

    @Autowired
    HdfsService hdfsService;

    public DownloadServiceImpl(){
        infosupplyerConfiguration = ConfigFactory.getInfosupplyerConfiguration();
        hdfsconf = ConfigFactory.getHdfsConfiguration();
    }

    @Override
    public String downloadElectricByPatientname(String patientname) throws Exception {
        logger.log(Level.INFO,"方法:downloadElectricByPatientname 被调用，参数:{patientname="+patientname+"}");

        boolean success = true;

        List<String> paths = new ArrayList<String>();

        //查询es，获得电信号的path信息。

        JSONObject criteria = new JSONObject();
        criteria.put(EsConsts.PatientName_ES_ELECTRIC,patientname);
        JSONArray backfields = new JSONArray();
        backfields.add(EsConsts.HDFSPATH_ES_ELECTRIC);
        JSONObject param = new JSONObject();
        param.put(SysConsts.DATATYPE,SysConsts.TYPE_ELECTRIC);
        param.put(SysConsts.CRITERIA,criteria);
        param.put(SysConsts.CRITERIA,criteria);
        DataTypeEnum type = DataTypeEnum.ELECTRIC;
        JSONObject jsonObject = elasticSearchService.searchByPaging(param, type);
        if(SysConsts.CODE_000.equals(jsonObject.getString(SysConsts.CODE))){
            JSONArray jsonArray = jsonObject.getJSONArray(SysConsts.DATA);
            int size = jsonArray.size();
            for(int i = 0; i< size; i++){
                JSONObject temp = jsonArray.getJSONObject(i);
                paths.add(temp.getString(EsConsts.HDFSPATH_ES_ELECTRIC));
            }
        }else{
            success = false;
        }

        logger.log(Level.INFO,"获取电信号存储路径:"+success);
        //测试代码，是否获取路径成功
        for(String e : paths){
            System.out.println(e);
        }

        //通过配置文件得到临时文件目录,/temp/electrictemp
        String electrictempPath = infosupplyerConfiguration.getElectrictempPath();

        //localpath这个以患者名字命名的文件目录中存放着他的电信号文件
        String downloadFilePath = electrictempPath+ File.separator+patientname;

        //调用hdfsserver下载文件到本地一个临时目录。
        if(success){
            try {
                if(!hdfsService.downloadElectric(paths,downloadFilePath,hdfsconf)){
                    success = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logger.log(Level.INFO,"下载电信号文件到本地:"+success+",downloadFilePath:"+downloadFilePath);

        //压缩下载后的文件
        String zipFilePath = null;
        if(success){
            String zipname = patientname+"_electric.zip";
            ZipUtil.zip(downloadFilePath,electrictempPath,zipname);
            zipFilePath = electrictempPath+File.separator+zipname;
        }

        InfoSupplyerTool.delFolder(downloadFilePath);
        logger.log(Level.INFO,"zip压缩文件:"+zipFilePath);
        logger.log(Level.INFO,"方法:downloadElectricByPatientname 流程结束");

        return zipFilePath;
    }
}
