package yasen.bigdata.infosupplyer.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yasen.bigdata.infosupplyer.consts.ESConstant;
import yasen.bigdata.infosupplyer.consts.SysConstants;
import yasen.bigdata.infosupplyer.service.DesensitizationService;
import yasen.bigdata.infosupplyer.service.TagService;
import yasen.bigdata.infosupplyer.util.InfoSupplyerTool;

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
    DesensitizationService desensitizationService;

    @PostMapping("/tagfordicom")
    public JSONObject tagfordicom(@RequestBody Map<String, Object> parameter) {
        logger.info("tagfordicom is called");
        System.out.println("tagfordicom is called");
        JSONObject param = InfoSupplyerTool.formatParameter(parameter);
        Integer count = tagService.signForDicom(param);
        JSONObject result = new JSONObject();
        result.put(SysConstants.CODE,SysConstants.CODE_000);
        result.put("total",count);
        return result;
    }

    @PostMapping("/desensitizedicom")
    public void desensitizedicom(@RequestBody Map<String, Object> parameter){
        String tag = (String)parameter.get("tag");
        Long count = desensitizationService.desensitizedicom(tag);
    }

}
