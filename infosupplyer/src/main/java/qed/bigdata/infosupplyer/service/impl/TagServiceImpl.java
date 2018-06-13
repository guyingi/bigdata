package qed.bigdata.infosupplyer.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import qed.bigdata.infosupplyer.conf.InfosupplyerConfiguration;
import qed.bigdata.infosupplyer.consts.DataTypeEnum;
import qed.bigdata.infosupplyer.consts.EsConsts;
import qed.bigdata.infosupplyer.consts.SysConsts;
import qed.bigdata.infosupplyer.pojo.db.DicomTag;
import qed.bigdata.infosupplyer.dao.DicomTagDao;
import qed.bigdata.infosupplyer.service.ElasticSearchService;
import qed.bigdata.infosupplyer.service.HBaseService;
import qed.bigdata.infosupplyer.service.HdfsService;
import qed.bigdata.infosupplyer.service.TagService;

import java.io.IOException;
import java.util.*;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package yasen.bigdata.infosupplyer.service.impl
 * @Description: ${todo}
 * @date 2018/5/17 17:12
 */
@Service("TagService")
public class TagServiceImpl implements TagService {

    static Logger logger = Logger.getLogger(TagServiceImpl.class);

    InfosupplyerConfiguration infosupplyerConfiguration = null;

    @Autowired
    ElasticSearchService elasticSearchService;

    @Autowired
    HBaseService hBaseService;

    @Autowired
    HdfsService hdfsService;

    @Autowired
    DicomTagDao dicomTagDao;

    public TagServiceImpl(){
        infosupplyerConfiguration = new InfosupplyerConfiguration();
    }

    @Override
    public Integer signForDicom(JSONObject param) {
        long total = 0;
        //1.得到tag,searchcondition
        String tag = param.getString(SysConsts.TAG_PARAM);
        JSONArray criteria = param.getJSONArray(SysConsts.CRITERIA);

        //2.下面两段是构造新查询条件，以及返回值给分页查询接口，获得所有序列的SeriesUID,rowkey.
        JSONArray backfields = new JSONArray();
        backfields.add(EsConsts.ID);
        backfields.add(EsConsts.ROWKEY);

        JSONObject json = new JSONObject();
        json.put(SysConsts.CRITERIA,criteria);
        json.put(SysConsts.BACKFIELDS,backfields);
        DataTypeEnum type=  DataTypeEnum.DICOM;
        JSONObject result = elasticSearchService.searchByPaging(json,type);

        //遍历序列，每个序列做修改打上标签
        List<String>  esids = new LinkedList<String>();
        List<String>  rowkeys = new LinkedList<String>();
        String code = result.getString(SysConsts.CODE);
        if(SysConsts.CODE_000.equals(code)){
            JSONArray data = result.getJSONArray(SysConsts.DATA);
            total = data.size();
            for(int i = 0; i < total; i++){
                JSONObject one = data.getJSONObject(i);
                String esid = one.getString(EsConsts.ID);
                esids.add(esid);
                String rowkey = one.getString(EsConsts.ROWKEY);
                rowkeys.add(rowkey);
            }
        }
        //3.修改hbase
        try {
            hBaseService.updateTagForDicom(infosupplyerConfiguration.getDicomTablename(),rowkeys,
                    infosupplyerConfiguration.getDicomCf(),SysConsts.TAG,tag);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //4.修改es
        for(String id : esids){
            elasticSearchService.updateField(infosupplyerConfiguration.getIndexDicom(),
                    infosupplyerConfiguration.getTypeDicom(),id,EsConsts.TAG,tag);
        }

        //4.该tag，以及旗下序列数量需要记录到关系型数据库
        DicomTag dicomTag = new DicomTag();
        dicomTag.setTagname(tag);
        dicomTag.setCount(total);
        dicomTag.setDesensitize(0);
        dicomTag.setDescribe("未脱敏");
        dicomTagDao.insert(dicomTag);

        return esids.size();
    }

    @Override
    public JSONObject searchtags(String tag) {
        JSONObject result = null;
        if(tag != null && tag.length() != 0){
            Map<String,String> map = new HashMap<String,String>();
            map.put(SysConsts.TAG,tag);
            result = elasticSearchService.searchAggregation(infosupplyerConfiguration.getIndexDicomDisensitization(),
                    infosupplyerConfiguration.getTypeDicomDisensitization(), map, SysConsts.TAG);
        }else{
            result = elasticSearchService.searchAggregation(infosupplyerConfiguration.getIndexDicomDisensitization(),
                    infosupplyerConfiguration.getTypeDicomDisensitization(), null, SysConsts.TAG);
        }
        return result;
    }

}
