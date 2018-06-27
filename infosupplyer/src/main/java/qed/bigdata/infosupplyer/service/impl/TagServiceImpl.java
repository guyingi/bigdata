package qed.bigdata.infosupplyer.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Level;
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

    InfosupplyerConfiguration infoConf = null;

    @Autowired
    ElasticSearchService elasticSearchService;

    @Autowired
    HBaseService hBaseService;

    @Autowired
    HdfsService hdfsService;

    @Autowired
    DicomTagDao dicomTagDao;

    public TagServiceImpl(){
        infoConf = new InfosupplyerConfiguration();
    }

    @Override
    public Integer signForDicom(JSONObject param) {
        logger.log(Level.INFO,"方法:signForDicom 被调用,参数{"+param.toJSONString()+"}");

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
            hBaseService.updateTagForDicom(infoConf.getDicomTablename(),rowkeys,
                    infoConf.getDicomCf(),SysConsts.TAG,tag);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //4.修改es
        for(String id : esids){
            elasticSearchService.updateField(infoConf.getIndexDicom(),
                    infoConf.getTypeDicom(),id,EsConsts.TAG,tag);
        }

        //4.该tag，以及旗下序列数量需要记录到关系型数据库
        DicomTag dicomTag = new DicomTag();
        dicomTag.setTagname(tag);
        dicomTag.setCount(total);
        dicomTag.setDesensitize(0);
        dicomTag.setDescribe("未脱敏");
        dicomTagDao.insert(dicomTag);
        logger.log(Level.INFO,"方法:signForDicom 调用结束");
        return esids.size();
    }

    @Override
    public Set<String> checkTagConflictForDicom(JSONObject param) {
        Set<String> oldTagSet = new HashSet<>();
        //1.得到tag,searchcondition
        String tag = param.getString(SysConsts.TAG_PARAM);
        JSONArray criteria = param.getJSONArray(SysConsts.CRITERIA);

        //2.下面两段是构造新查询条件，以及返回值给分页查询接口，获得所有序列的SeriesUID,rowkey.
        JSONArray backfields = new JSONArray();
        backfields.add(EsConsts.ID);
        backfields.add(EsConsts.ROWKEY);
        backfields.add(EsConsts.TAG);

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
            long total = data.size();
            for(int i = 0; i < total; i++){
                JSONObject one = data.getJSONObject(i);
                String esid = one.getString(EsConsts.ID);
                esids.add(esid);
                String rowkey = one.getString(EsConsts.ROWKEY);
                rowkeys.add(rowkey);
                String oldTag = one.getString(EsConsts.TAG);
                if(!StringUtils.isBlank(oldTag)){
                    oldTagSet.add(oldTag);
                }
            }
        }
        return oldTagSet;
    }

    @Override
    public JSONObject searchtags(String tag) {
        logger.log(Level.INFO,"方法:searchtags 被调用,参数{tag:"+tag+"}");
        JSONObject result = null;
        if(tag != null && tag.length() != 0){
            Map<String,String> map = new HashMap<String,String>();
            map.put(SysConsts.TAG,tag);
            result = elasticSearchService.searchAggregation(infoConf.getIndexDicomDisensitization(),
                    infoConf.getTypeDicomDisensitization(), map, SysConsts.TAG);
        }else{
            result = elasticSearchService.searchAggregation(infoConf.getIndexDicomDisensitization(),
                    infoConf.getTypeDicomDisensitization(), null, SysConsts.TAG);
        }
        logger.log(Level.INFO,"方法:searchtags 调用结束");
        return result;
    }

    @Override
    public boolean removeTag(String tag, DataTypeEnum type) {
        logger.log(Level.INFO,"方法:removeTag 被调用,参数{tag:"+tag+"}");

        boolean success = true;
        List<String> rowkeys = new ArrayList<String>();
        List<String> hdfspaths = new ArrayList<String>();
        List<String> ids = new ArrayList<String>();

        if(type == DataTypeEnum.DICOM) {
            //步骤一、查询ES，获取必要信息。
            JSONObject param = new JSONObject();
            JSONArray criteria = new JSONArray();
            JSONObject obj = new JSONObject();
            obj.put(SysConsts.SECTION, SysConsts.NO);
            obj.put(SysConsts.KEYWORD, EsConsts.TAG);
            obj.put(SysConsts.VALUE, tag);
            criteria.add(obj);
            JSONArray backfields = new JSONArray();
            backfields.add(EsConsts.ROWKEY);
            backfields.add(EsConsts.HDFSPATH);
            backfields.add(EsConsts.ID);
            param.put(SysConsts.DATATYPE,SysConsts.TYPE_DICOM);
            param.put(SysConsts.CRITERIA, criteria);
            param.put(SysConsts.BACKFIELDS, backfields);

            JSONObject jsonObject = elasticSearchService.searchByPaging(param, DataTypeEnum.DICOM);
            if (SysConsts.CODE_000.equals(jsonObject.getString(SysConsts.CODE))) {
                JSONArray data = jsonObject.getJSONArray(SysConsts.DATA);
                int size = data.size();
                for (int i = 0; i < size; i++) {
                    JSONObject elem = data.getJSONObject(i);
                    String rowkey = elem.getString(EsConsts.ROWKEY);
                    String hdfspath = elem.getString(EsConsts.HDFSPATH);
                    String id = elem.getString(EsConsts.ID);
                    if (rowkey != null) {
                        rowkeys.add(rowkey);
                    }
                    if (hdfspath != null) {
                        hdfspaths.add(hdfspath);
                    }
                    if (id != null) {
                        ids.add(id);
                    }
                }
            } else {
                success = false;
            }
            logger.log(Level.INFO,"步骤一、获取必要信息:"+success);

            //步骤二、删除raw,mhd数据
            if (success) {
                String dirPrefixDesensitization = infoConf.getDirPrefixDesensitization();
                String tagPath = dirPrefixDesensitization + SysConsts.LEFT_SLASH + tag;
                Path path = new Path(tagPath);
                hdfsService.delFile(path);
            }
            logger.log(Level.INFO,"步骤二、删除raw,mhd数据:"+success);

            //步骤三、将elasticsearch中的dicomindex中tag置空
            if (success) {
                for (String id : ids) {
                    elasticSearchService.updateField(infoConf.getIndexDicom(), infoConf.getTypeDicom(), id, EsConsts.TAG, null);
                }
            }
            logger.log(Level.INFO,"步骤三、将elasticsearch中的tag置空:"+success);

            //步骤四、将elasticsearch中的dicomdisensitizationindex中索引删除
            List<String> idsInDesensitize = elasticSearchService.getIdByField(DataTypeEnum.MULTIDIMENSION, EsConsts.TAG, tag);
            if(idsInDesensitize != null && idsInDesensitize.size()!=0){
                elasticSearchService.deleteIndex(infoConf.getIndexDicomDisensitization(),
                        infoConf.getTypeDicomDisensitization(),idsInDesensitize);
            }

            //步骤四、将hbase中tag置空
            if (success) {
                try {
                    for (String rowkey : rowkeys) {
                        hBaseService.updateColumn(infoConf.getDicomTablename(), rowkey, infoConf.getDicomCf(), EsConsts.TAG, null);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    success = false;
                }
            }
            logger.log(Level.INFO,"步骤四、将hbase中tag置空:"+success);

            //步骤五、删除关系型数据库中的tag.
            if (success) {
                boolean result = dicomTagDao.deleteByTag(tag);
                if (result == false) {
                    success = false;
                }
            }
            logger.log(Level.INFO,"步骤五、删除关系型数据库中的tag:"+success);
        }
        return success;
    }


    public static void main(String[] args) {
        ElasticSearchService elasticSearchService = new ElasticSearchServiceImpl();
        InfosupplyerConfiguration conf = new InfosupplyerConfiguration();
        String ids[] = new String[]{
                "tGOuFWQBBNjORWhHryqi"};
        for(String id : ids) {
            elasticSearchService.updateField(conf.getIndexDicom(),
                    conf.getTypeDicom(), id, EsConsts.TAG, "LUN");
        }
    }
}
