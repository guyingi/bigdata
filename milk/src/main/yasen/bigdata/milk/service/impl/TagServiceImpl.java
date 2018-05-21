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

        JSONObject result = MilkTool.doCallAndGetResult(json, interfaceStr);
        if(SysConstants.CODE_000.equals(result.getString(SysConstants.CODE))){
            return result.getLong(SysConstants.TOTAL);
        }
        return 0L;
    }

    @Override
    public boolean doDesensitize(String tag) {
        return false;
    }


}
