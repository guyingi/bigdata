package yasen.bigdata.infosupplyer.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yasen.bigdata.infosupplyer.conf.InfosupplyerConfiguration;
import yasen.bigdata.infosupplyer.consts.ESConstant;
import yasen.bigdata.infosupplyer.consts.SysConstants;
import yasen.bigdata.infosupplyer.controller.TagController;
import yasen.bigdata.infosupplyer.dao.DicomTagDao;
import yasen.bigdata.infosupplyer.pojo.PageSearchParamBean;
import yasen.bigdata.infosupplyer.pojo.db.DicomTag;
import yasen.bigdata.infosupplyer.service.*;

import java.io.File;
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
        String tag = param.getString(SysConstants.TAG_PARAM);
        JSONObject searchcondition = param.getJSONObject(SysConstants.SEARCH_CONDITION);

        //2.下面两段是构造新查询条件，以及返回值给分页查询接口，获得所有序列的SeriesUID,rowkey.
        JSONArray backfields = new JSONArray();
        backfields.add(ESConstant.SeriesUID_ES);
        backfields.add(ESConstant.ROWKEY);

        JSONObject json = new JSONObject();
        json.put(SysConstants.SEARCH_CONDITION,searchcondition);
        json.put(SysConstants.BACKFIELDS,backfields);
        JSONObject result = elasticSearchService.searchByPaging(json);

        //遍历序列，每个序列做修改打上标签
        List<String>  seriesids = new LinkedList<String>();
        List<String>  rowkeys = new LinkedList<String>();
        String code = result.getString(SysConstants.CODE);
        if(SysConstants.CODE_000.equals(code)){
            JSONArray data = result.getJSONArray(SysConstants.DATA);
            total = data.size();
            for(int i = 0; i < total; i++){
                JSONObject one = data.getJSONObject(i);
                String seriesUID = one.getString(ESConstant.SeriesUID_ES);
                seriesids.add(seriesUID);
                String rowkey = one.getString(ESConstant.ROWKEY);
                rowkeys.add(rowkey);
            }
        }
        //3.修改hbase
        try {
            hBaseService.updateTagForDicom(infosupplyerConfiguration.getDicomTablename(),rowkeys,
                    infosupplyerConfiguration.getDicomCf(),SysConstants.TAG,tag);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(String id : seriesids){
            elasticSearchService.updateField(infosupplyerConfiguration.getIndexDicom(),
                    infosupplyerConfiguration.getTypeDicom(),id,ESConstant.TAG_ES,tag);
        }

        //4.该tag，以及旗下序列数量需要记录到关系型数据库
        DicomTag dicomTag = new DicomTag();
        dicomTag.setTagname(tag);
        dicomTag.setCount(total);
        dicomTag.setDesensitize(0);
        dicomTag.setDescribe("未脱敏");
        dicomTagDao.insert(dicomTag);

        return seriesids.size();
    }

    @Override
    public JSONObject searchtags(String tag) {
        JSONObject result = null;
        if(tag != null && tag.length() != 0){
            Map<String,String> map = new HashMap<String,String>();
            map.put(SysConstants.TAG,tag);
            result = elasticSearchService.searchAggregation(infosupplyerConfiguration.getIndexDicomDisensitization(),
                    infosupplyerConfiguration.getTypeDicomDisensitization(), map, SysConstants.TAG);
        }else{
            result = elasticSearchService.searchAggregation(infosupplyerConfiguration.getIndexDicomDisensitization(),
                    infosupplyerConfiguration.getTypeDicomDisensitization(), null, SysConstants.TAG);
        }
        return result;
    }

}
