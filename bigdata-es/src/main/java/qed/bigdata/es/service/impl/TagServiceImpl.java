package qed.bigdata.es.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import qed.bigdata.es.consts.ESConsts;
import qed.bigdata.es.consts.SysConsts;
import qed.bigdata.es.service.TagService;
import qed.bigdata.es.consts.DataTypeEnum;
import qed.bigdata.es.tool.Tool;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package yasen.bigdata.es.service.impl
 * @Description: ${todo}
 * @date 2018/5/17 16:48
 */
@Service
public class TagServiceImpl implements TagService {
    static Logger logger = Logger.getLogger(TagServiceImpl.class);

    @Override
    public JSONObject signTag(JSONArray param, String tag) {
        logger.log(Level.INFO,"调用方法:signTag,参数{param:"+param.toJSONString()
        +",tag:"+tag);

        //调用infosupplyer的接口【signfordiom】，将这两个参数封装到一个json对象里面，
        // 传过去，接收一个json对象，里面包含返回码，以及成功打了标记的条数
        JSONObject result = new JSONObject();
        JSONObject json = new JSONObject();
        if(param ==null || param.size()==0 || tag == null || tag.length()==0) {
            result.put(SysConsts.CODE,SysConsts.CODE_999);
        } else {
            json.put(SysConsts.DATATYPE,SysConsts.TYPE_DICOM);
            json.put(SysConsts.CRITERIA, param);
            json.put(SysConsts.TAG, tag);
        }

        String interfaceStr = "/info/tagfordicom";
        DataTypeEnum dataTypeEnum = DataTypeEnum.OTHER;
        JSONObject tempResult = null;
        try {
            tempResult = Tool.doCallAndGetResult(json, interfaceStr,dataTypeEnum);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(SysConsts.CODE_000.equals(tempResult.getString(SysConsts.CODE))){
            result.put("result",true);
            result.put("content","数量:"+tempResult.getLong(SysConsts.TOTAL));
        }else if("001".equals(tempResult.getString(SysConsts.CODE))){
            result.put("result",false);
            result.put("content","冲突tag:["+tempResult.getJSONArray(SysConsts.DATA)+"]");
        }else if("999".equals(tempResult.getString(SysConsts.CODE))){
            result.put("result",false);
            result.put("content","打标签失败");
        }
        result.put("tag",tag);
        logger.log(Level.INFO,"调用 signTag 结束,返回结果:"+result);
        return result;
    }

    @Override
    public boolean removeTag(String tag, DataTypeEnum typeEnum) {
        logger.log(Level.INFO,"调用方法:removeTag,参数{tag:"+tag);

        boolean issuccess =  false;
        if(typeEnum == DataTypeEnum.DICOM) {
            String interfaceStr = "/info/removetag";
            JSONObject param = new JSONObject();
            param.put(ESConsts.TAG_ES, tag);
            param.put(SysConsts.DATATYPE, SysConsts.TYPE_DICOM);
            DataTypeEnum dataTypeEnum = DataTypeEnum.OTHER;
            JSONObject result = null;
            try {
                result = Tool.doCallAndGetResult(param, interfaceStr, dataTypeEnum);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (SysConsts.CODE_000.equals(result.getString(SysConsts.CODE))) {
                issuccess = true;
            }
        }
        return issuccess;
    }

    @Override
    public Integer doDesensitize(String tag) {
        logger.log(Level.INFO,"调用方法:doDesensitize,参数{tag:"+tag);

        Integer status = 0;  //此次任务提交状态码，0:成功，1：此任务已经存在，2：任务提交失败
        String interfaceStr = "/info/desensitizedicom";
        JSONObject param = new JSONObject();
        param.put("tag",tag);
        DataTypeEnum dataTypeEnum = DataTypeEnum.OTHER;
        JSONObject result = null;
        try {
            result = Tool.doCallAndGetResult(param, interfaceStr,dataTypeEnum);
        } catch (Exception e) {
            e.printStackTrace();
            status = 2;
        }
        if(SysConsts.CODE_000.equals(result.getString(SysConsts.CODE))){
            status = result.getInteger(SysConsts.STATUS);
        }
        logger.log(Level.INFO,"调用 doDesensitize 结束,返回结果:"+status);
        return status;
    }

    @Override
    public JSONObject searchTags(String tag) {
        logger.log(Level.INFO,"调用方法:searchTags,参数{tag:"+tag+"}");

        JSONObject result = new JSONObject();

        String interfaceStr = "/info/searchtags";
        JSONObject param = new JSONObject();
        param.put(SysConsts.TAG,tag);
        DataTypeEnum dataTypeEnum = DataTypeEnum.OTHER;
        JSONObject tempResult = null;
        try {
            tempResult = Tool.doCallAndGetResult(param, interfaceStr,dataTypeEnum);
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
        if(SysConsts.CODE_000.equals(tempResult.getString(SysConsts.CODE))){
            result.put(SysConsts.TOTAL,tempResult.getLong(SysConsts.TOTAL));
            JSONArray jsonArray = tempResult.getJSONArray(SysConsts.DATA);
            result.put(SysConsts.ROWS,jsonArray);
        }
        logger.log(Level.INFO,"调用 searchTags 结束,返回结果:"+result.toJSONString());
        return result;
    }

    @Override
    public JSONObject listTags() {
        logger.log(Level.INFO,"调用方法:listTags,");

        JSONObject result = new JSONObject();
        String interfaceStr = "/info/listtags";
        JSONObject param = new JSONObject();
        DataTypeEnum dataTypeEnum = DataTypeEnum.OTHER;
        JSONObject tempResult = Tool.doCallAndGetResult(param, interfaceStr,dataTypeEnum);
        if(SysConsts.CODE_000.equals(tempResult.getString(SysConsts.CODE))){
            result.put(SysConsts.TOTAL,tempResult.getLong(SysConsts.TOTAL));
            JSONArray tempArr = tempResult.getJSONArray(SysConsts.DATA);
            JSONArray data = new JSONArray();
            int size = tempArr.size();
            for(int i=0; i<size; i++){
                JSONObject jsonObject = tempArr.getJSONObject(i);
                Integer desensitize = jsonObject.getInteger("desensitize");
                if(1 == desensitize){
                    jsonObject.put("desensitize","已脱敏");
                }else{
                    jsonObject.put("desensitize","未脱敏");
                }
                data.add(jsonObject);
            }
            result.put(SysConsts.ROWS,data);
        }
        logger.log(Level.INFO,"调用 listTags 结束,返回结果:"+result.toJSONString());
        return result;
    }

    @Override
    public boolean isTagDisensitized(String tag) {
        logger.log(Level.INFO,"调用方法:isTagDisensitized,参数{tag:"+tag+"}");

        String interfaceStr = "/info/listtags";
        JSONObject param = new JSONObject();
        DataTypeEnum dataTypeEnum = DataTypeEnum.OTHER;
        JSONObject tempResult = Tool.doCallAndGetResult(param, interfaceStr,dataTypeEnum);
        if(SysConsts.CODE_000.equals(tempResult.getString(SysConsts.CODE)) && tempResult.getLong(SysConsts.TOTAL)>0) {
            JSONArray tempArr = tempResult.getJSONArray(SysConsts.DATA);
            int size = tempArr.size();
            for (int i = 0; i < size; i++) {
                JSONObject jsonObject = tempArr.getJSONObject(i);
                if (tag.equals(jsonObject.getString("tagname"))) {
                    Integer desensitize = jsonObject.getInteger("desensitize");
                    if (1 == desensitize) {
                        logger.log(Level.INFO,"调用 listTags 结束,返回结果:"+true);
                        return true;
                    }
                }
            }
        }
        logger.log(Level.INFO,"调用 listTags 结束,返回结果:"+false);
        return false;
    }

    public static void main(String[] args) {
        Long desensitizedCount = 0L;
        String interfaceStr = "/info/desensitizedicom";
        JSONObject param = new JSONObject();
        param.put("tag","LUN");
        DataTypeEnum dataTypeEnum = DataTypeEnum.OTHER;
        JSONObject result = null;
        try {
            result = Tool.doCallAndGetResult(param, interfaceStr,dataTypeEnum);
        } catch (Exception e) {
            e.printStackTrace();
            desensitizedCount = 0L;
        }
        if(SysConsts.CODE_000.equals(result.getString(SysConsts.CODE))){
            desensitizedCount = result.getLong(SysConsts.TOTAL);
        }
        logger.log(Level.INFO,"调用 doDesensitize 结束,返回结果:"+desensitizedCount);
    }
}
