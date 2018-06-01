package yasen.bigdata.infosupplyer.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.hadoop.conf.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yasen.bigdata.infosupplyer.conf.InfosupplyerConfiguration;
import yasen.bigdata.infosupplyer.consts.DataTypeEnum;
import yasen.bigdata.infosupplyer.consts.ESConstant;
import yasen.bigdata.infosupplyer.consts.SysConstants;
import yasen.bigdata.infosupplyer.factory.ConfigFactory;
import yasen.bigdata.infosupplyer.service.DownloadService;
import yasen.bigdata.infosupplyer.service.ElasticSearchService;
import yasen.bigdata.infosupplyer.service.HdfsService;
import yasen.bigdata.infosupplyer.util.InfoSupplyerTool;
import yasen.bigdata.infosupplyer.util.ZipUtil;

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

    @Autowired
    ElasticSearchService elasticSearchService;

    @Autowired
    HdfsService hdfsService;

    @Override
    public String downloadElectricByPatientname(String patientname) throws Exception {
        InfosupplyerConfiguration infosupplyerConfiguration = ConfigFactory.getInfosupplyerConfiguration();
        Configuration hdfsconf = ConfigFactory.getHdfsConfiguration();
        boolean success = true;

        List<String> paths = new ArrayList<String>();

        //查询es，获得电信号的path信息。

        JSONObject criteria = new JSONObject();
        criteria.put(ESConstant.PatientName_ES_ELECTRIC,patientname);
        JSONArray backfields = new JSONArray();
        backfields.add(ESConstant.HDFSPATH_ES_ELECTRIC);
        JSONObject param = new JSONObject();
        param.put(SysConstants.DATATYPE,SysConstants.TYPE_ELECTRIC);
        param.put(SysConstants.SEARCH_CRITERIA,criteria);
        param.put(SysConstants.SEARCH_CRITERIA,criteria);
        DataTypeEnum type = DataTypeEnum.ELECTRIC;
        JSONObject jsonObject = elasticSearchService.searchByPaging(param, type);
        if(SysConstants.CODE_000.equals(jsonObject.getString(SysConstants.CODE))){
            JSONArray jsonArray = jsonObject.getJSONArray(SysConstants.DATA);
            int size = jsonArray.size();
            for(int i = 0; i< size; i++){
                JSONObject temp = jsonArray.getJSONObject(i);
                paths.add(temp.getString(ESConstant.HDFSPATH_ES_ELECTRIC));
            }
        }else{
            success = false;
        }
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

        //压缩下载后的文件
        String zipFilePath = null;
        if(success){
            String zipname = patientname+"_electric.zip";
            ZipUtil.zip(downloadFilePath,electrictempPath,zipname);
            zipFilePath = electrictempPath+File.separator+zipname;
        }

        InfoSupplyerTool.delFolder(downloadFilePath);
        return zipFilePath;
    }
}
