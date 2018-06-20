package qed.bigdata.infosupplyer.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import qed.bigdata.infosupplyer.consts.DataTypeEnum;
import qed.bigdata.infosupplyer.consts.SysConsts;
import qed.bigdata.infosupplyer.pojo.db.DicomTag;
import qed.bigdata.infosupplyer.util.InfoSupplyerTool;
import qed.bigdata.infosupplyer.dao.DicomTagDao;
import qed.bigdata.infosupplyer.service.DesensitizationService;
import qed.bigdata.infosupplyer.service.ElasticSearchService;
import qed.bigdata.infosupplyer.service.TagService;
import qed.bigdata.infosupplyer.service.impl.DesensitizationServiceImpl;

import java.util.List;
import java.util.Map;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package yasen.bigdata.infosupplyer.controller
 * @Description: ${todo}
 * @date 2018/5/17 16:57
 */
@RestController
@RequestMapping("/info")
public class TagController{
    static Logger logger = Logger.getLogger(TagController.class);

    @Autowired
    TagService tagService;

    @Autowired
    ElasticSearchService elasticSearchService;

    @Autowired
    DesensitizationService desensitizationService;

    @Autowired
    DicomTagDao dicomTagDao;

    @PostMapping("/tagfordicom")
    public JSONObject tagfordicom(@RequestBody Map<String, Object> parameter) {
        logger.log(Level.INFO,"接口:tagfordicom 被调用");
        JSONObject paramJson = JSONObject.parseObject(JSON.toJSONString(parameter));
        logger.log(Level.INFO,"接口接收的参数:"+paramJson.toJSONString());

        JSONObject param = InfoSupplyerTool.formatParameter(parameter);

        logger.log(Level.INFO,"接口接收的参数"+param.toJSONString());

        Integer count = tagService.signForDicom(param);
        JSONObject result = new JSONObject();
        result.put(SysConsts.CODE,SysConsts.CODE_000);
        result.put("total",count);

        logger.log(Level.INFO,"接口返回结果"+result.toJSONString());

        return result;
    }



    @PostMapping("/desensitizedicom")
    public JSONObject desensitizedicom(@RequestBody Map<String, Object> parameter){
        logger.log(Level.INFO,"接口:desensitizedicom 被调用");
        JSONObject paramJson = JSONObject.parseObject(JSON.toJSONString(parameter));
        logger.log(Level.INFO,"接口接收的参数:"+paramJson.toJSONString());

        String tag = (String)parameter.get(SysConsts.TAG);
        Long count = desensitizationService.desensitizedicom(tag);
        JSONObject result = new JSONObject();
        result.put(SysConsts.CODE,SysConsts.CODE_000);
        result.put("total",count);

        logger.log(Level.INFO,"接口返回结果"+result.toJSONString());

        return result;
    }


    /**
     * 查询标签以及该标签下面序列数量
     * @param parameter
     * @return
     */
    @PostMapping("/searchtags")
    public JSONObject searchtags(@RequestBody Map<String, Object> parameter){
        logger.log(Level.INFO,"接口:searchtags 被调用");
        JSONObject paramJson = JSONObject.parseObject(JSON.toJSONString(parameter));
        logger.log(Level.INFO,"接口接收的参数:"+paramJson.toJSONString());

        JSONObject result = new JSONObject();
        String tag = (String)parameter.get(SysConsts.TAG_PARAM);

        if(tag != null && tag.length() != 0){
            JSONObject tags = tagService.searchtags(tag);
            if(SysConsts.CODE_000.equals(tags.getString(SysConsts.CODE))){
                result.put(SysConsts.CODE,SysConsts.CODE_000);
                result.put(SysConsts.TOTAL,tags.getLong(SysConsts.TOTAL));
                result.put(SysConsts.DATA,tags.getJSONArray(SysConsts.DATA));
            }
        }else{
            JSONObject tags = tagService.searchtags(null);
            if(SysConsts.CODE_000.equals(tags.getString(SysConsts.CODE))){
                result.put(SysConsts.CODE,SysConsts.CODE_000);
                result.put(SysConsts.TOTAL,tags.getLong(SysConsts.TOTAL));
                result.put(SysConsts.DATA,tags.getJSONArray(SysConsts.DATA));
            }
        }

        logger.log(Level.INFO,"接口返回结果"+result.toJSONString());

        return result;
    }

    /**
     * 移除标签
     * @param parameter
     * @return
     */
    @PostMapping("/removetag")
    public JSONObject removetag(@RequestBody Map<String, Object> parameter){
        logger.log(Level.INFO,"接口:removetag 被调用");
        JSONObject paramJson = JSONObject.parseObject(JSON.toJSONString(parameter));
        logger.log(Level.INFO,"接口接收的参数:"+paramJson.toJSONString());

        JSONObject result = new JSONObject();
        String tag = (String)parameter.get(SysConsts.TAG_PARAM);
        String datatype = (String)parameter.get(SysConsts.DATATYPE);

        if(SysConsts.TYPE_DICOM.equals(datatype)){
            boolean success = tagService.removeTag(tag,DataTypeEnum.DICOM);
            if(success){
                result.put(SysConsts.CODE,SysConsts.CODE_000);
            }else{
                result.put(SysConsts.CODE,SysConsts.CODE_999);
            }
        }

        logger.log(Level.INFO,"接口返回结果"+result.toJSONString());

        return result;
    }

    /**
     * 查询标签以及该标签下面序列数量
     * @param
     * @return
     */
    @PostMapping("/listtags")
    public JSONObject listtags(@RequestBody Map<String, Object> parameter){
        logger.log(Level.INFO,"接口:listtags 被调用");
        List<DicomTag> list = dicomTagDao.list();
        JSONObject result = new JSONObject();
        JSONArray data = new JSONArray();

        result.put(SysConsts.CODE,SysConsts.CODE_000);
        result.put(SysConsts.TOTAL,list.size());

        for(DicomTag dicomTag : list){
            JSONObject element = new JSONObject();
            element.put("tagname",dicomTag.getTagname());
            element.put("count",dicomTag.getCount());
            element.put("desensitize",dicomTag.getDesensitize());
            data.add(element);
        }
        result.put(SysConsts.DATA,data);

        logger.log(Level.INFO,"接口返回结果"+result.toJSONString());

        return result;
    }



    public static void main(String[] args) {
        String tag = "tag";
        DesensitizationService desensitizationService = new DesensitizationServiceImpl();
        Long count = desensitizationService.desensitizedicom(tag);
        JSONObject result = new JSONObject();
        result.put(SysConsts.CODE,SysConsts.CODE_000);
        result.put("total",count);
    }

}
