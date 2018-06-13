package qed.bigdata.infosupplyer.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
        logger.info("tagfordicom is called");
        System.out.println("tagfordicom is called");
        JSONObject param = InfoSupplyerTool.formatParameter(parameter);
        System.out.println("tagfordicom接口接收的参数："+param.toJSONString());
        Integer count = tagService.signForDicom(param);
        JSONObject result = new JSONObject();
        result.put(SysConsts.CODE,SysConsts.CODE_000);
        result.put("total",count);
        return result;
    }

    @PostMapping("/desensitizedicom")
    public JSONObject desensitizedicom(@RequestBody Map<String, Object> parameter){
        String tag = (String)parameter.get("tag");
        Long count = desensitizationService.desensitizedicom(tag);
        JSONObject result = new JSONObject();
        result.put(SysConsts.CODE,SysConsts.CODE_000);
        result.put("total",count);
        return result;
    }


    /**
     * 查询标签以及该标签下面序列数量
     * @param parameter
     * @return
     */
    @PostMapping("/searchtags")
    public JSONObject searchtags(@RequestBody Map<String, Object> parameter){
        System.out.println("searchtags is called");
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
        System.out.println("info 发送："+result.toJSONString());
        return result;
    }

    /**
     * 查询标签以及该标签下面序列数量
     * @param
     * @return
     */
    @PostMapping("/listtags")
    public JSONObject listtags(@RequestBody Map<String, Object> parameter){
        System.out.println("listtags is called");
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
        System.out.println("info 发送："+result.toJSONString());
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