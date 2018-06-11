package yasen.bigdata.milk.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import org.springframework.stereotype.Service;
import yasen.bigdata.milk.conf.MilkConfiguration;
import yasen.bigdata.milk.consts.DataTypeEnum;
import yasen.bigdata.milk.consts.SysConstants;
import yasen.bigdata.milk.pojo.Dicom;
import yasen.bigdata.milk.service.TagService;
import yasen.bigdata.milk.tool.MilkTool;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package yasen.bigdata.milk.service.impl
 * @Description: ${todo}
 * @date 2018/5/17 16:48
 */
@Service
public class TagServiceImpl implements TagService {

    @Override
    public Long signTag(JSONArray param, String tag) {
        //调用infosupplyer的接口【signfordiom】，将这两个参数封装到一个json对象里面，
        // 传过去，接收一个json对象，里面包含返回码，以及成功打了标记的条数
        Long signedCount = 0L;
        JSONObject json = new JSONObject();
        if(param !=null && param.size()>0) {
            json.put(SysConstants.CRITERIA, param);
        } else {
            return 0L;
        }
        if(tag != null && tag.length()>0) {
            json.put(SysConstants.TAG, tag);
        } else {
            return 0L;
        }
        String interfaceStr = "/info/tagfordicom";
        DataTypeEnum dataTypeEnum = DataTypeEnum.OTHER;
        JSONObject result = null;
        try {
            result = MilkTool.doCallAndGetResult(json, interfaceStr,dataTypeEnum);
        } catch (Exception e) {
            signedCount = 0L;
            e.printStackTrace();
        }
        if(SysConstants.CODE_000.equals(result.getString(SysConstants.CODE))){
            signedCount = result.getLong(SysConstants.TOTAL);
        }
        return signedCount;
    }

    @Override
    public Long doDesensitize(String tag) {
        Long desensitizedCount = 0L;
        String interfaceStr = "/info/desensitizedicom";
        JSONObject param = new JSONObject();
        param.put("tag",tag);
        DataTypeEnum dataTypeEnum = DataTypeEnum.OTHER;
        JSONObject result = null;
        try {
            result = MilkTool.doCallAndGetResult(param, interfaceStr,dataTypeEnum);
        } catch (Exception e) {
            e.printStackTrace();
            desensitizedCount = 0L;
        }
        if(SysConstants.CODE_000.equals(result.getString(SysConstants.CODE))){
            desensitizedCount = result.getLong(SysConstants.TOTAL);
        }
        return desensitizedCount;
    }

    @Override
    public JSONObject searchTags(String tag) {
        JSONObject result = new JSONObject();

        String interfaceStr = "/info/searchtags";
        JSONObject param = new JSONObject();
        param.put(SysConstants.TAG,tag);
        DataTypeEnum dataTypeEnum = DataTypeEnum.OTHER;
        JSONObject tempResult = null;
        try {
            tempResult = MilkTool.doCallAndGetResult(param, interfaceStr,dataTypeEnum);
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
        System.out.println("milk收到的结果"+tempResult.toJSONString());
        if(SysConstants.CODE_000.equals(tempResult.getString(SysConstants.CODE))){
            result.put(SysConstants.TOTAL,tempResult.getLong(SysConstants.TOTAL));
            JSONArray jsonArray = tempResult.getJSONArray(SysConstants.DATA);
            result.put(SysConstants.ROWS,jsonArray);
        }
        System.out.println(result.toJSONString());
        return result;
    }

    @Override
    public JSONObject listTags() {
        JSONObject result = new JSONObject();
        String interfaceStr = "/info/listtags";
        JSONObject param = new JSONObject();
        DataTypeEnum dataTypeEnum = DataTypeEnum.OTHER;
        JSONObject tempResult = MilkTool.doCallAndGetResult(param, interfaceStr,dataTypeEnum);
        if(SysConstants.CODE_000.equals(tempResult.getString(SysConstants.CODE))){
            result.put(SysConstants.TOTAL,tempResult.getLong(SysConstants.TOTAL));
            JSONArray tempArr = tempResult.getJSONArray(SysConstants.DATA);
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
            result.put(SysConstants.ROWS,data);
        }
        return result;
    }

    @Override
    public boolean isTagDisensitized(String tag) {
        String interfaceStr = "/info/listtags";
        JSONObject param = new JSONObject();
        DataTypeEnum dataTypeEnum = DataTypeEnum.OTHER;
        JSONObject tempResult = MilkTool.doCallAndGetResult(param, interfaceStr,dataTypeEnum);
        if(SysConstants.CODE_000.equals(tempResult.getString(SysConstants.CODE)) && tempResult.getLong(SysConstants.TOTAL)>0) {
            JSONArray tempArr = tempResult.getJSONArray(SysConstants.DATA);
            int size = tempArr.size();
            for (int i = 0; i < size; i++) {
                JSONObject jsonObject = tempArr.getJSONObject(i);
                if (tag.equals(jsonObject.getString("tagname"))) {
                    Integer desensitize = jsonObject.getInteger("desensitize");
                    if (1 == desensitize) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        JSONObject result = new JSONObject();
        String interfaceStr = "/info/listtags";
//        String interfaceStr = "/info/searchtags";
        JSONObject param = new JSONObject();
        DataTypeEnum dataTypeEnum = DataTypeEnum.OTHER;
        JSONObject tempResult = MilkTool.doCallAndGetResult(param, interfaceStr,dataTypeEnum);
        if(SysConstants.CODE_000.equals(tempResult.getString(SysConstants.CODE))){
            result.put(SysConstants.TOTAL,tempResult.getLong(SysConstants.TOTAL));
            JSONArray jsonArray = tempResult.getJSONArray(SysConstants.DATA);
            result.put(SysConstants.ROWS,jsonArray);
        }
        System.out.println("listtags:"+result.toJSONString());
    }
}
