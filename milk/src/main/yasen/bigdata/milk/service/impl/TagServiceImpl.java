package yasen.bigdata.milk.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import org.springframework.stereotype.Service;
import yasen.bigdata.milk.conf.MilkConfiguration;
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
    public Long signTag(JSONObject param, String tag) {
        //调用infosupplyer的接口【signfordiom】，将这两个参数封装到一个json对象里面，
        // 传过去，接收一个json对象，里面包含返回码，以及成功打了标记的条数
        JSONObject json = new JSONObject();
        if(param !=null && param.size()>0) {
            json.put(SysConstants.SEARCH_CONDITION, param);
        } else {
            return  0L;
        }
        if(tag != null && tag.length()>0) {
            json.put(SysConstants.TAG, tag);
        } else {
            return 0L;
        }
        String interfaceStr = "/info/tagfordicom";
        String calltype = "tag";
        JSONObject result = MilkTool.doCallAndGetResult(json, interfaceStr,calltype);
        if(SysConstants.CODE_000.equals(result.getString(SysConstants.CODE))){
            return result.getLong(SysConstants.TOTAL);
        }
        return 0L;
    }

    @Override
    public Long doDesensitize(String tag) {
        String interfaceStr = "/info/desensitizedicom";
        JSONObject param = new JSONObject();
        param.put("tag",tag);
        String calltype = "tag";
        JSONObject result = MilkTool.doCallAndGetResult(param, interfaceStr,calltype);
        if(SysConstants.CODE_000.equals(result.getString(SysConstants.CODE))){
            return result.getLong(SysConstants.TOTAL);
        }
        return 0L;
    }

    @Override
    public JSONObject searchTags(String tag) {
        JSONObject result = new JSONObject();

        String interfaceStr = "/info/searchtags";
        JSONObject param = new JSONObject();
        param.put(SysConstants.TAG,tag);
        String calltype = "tag";
        JSONObject tempResult = MilkTool.doCallAndGetResult(param, interfaceStr,calltype);
        System.out.println("milk收到的结果"+tempResult.toJSONString());
        if(SysConstants.CODE_000.equals(tempResult.getString(SysConstants.CODE))){
            result.put(SysConstants.TOTAL,tempResult.getLong(SysConstants.TOTAL));
            JSONArray jsonArray = tempResult.getJSONArray(SysConstants.DATA);
            result.put(SysConstants.ROWS,jsonArray);
        }
        System.out.println(result.toJSONString());
        return result;
    }


}
