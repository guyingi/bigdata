package qed.bigdata.infosupplyer.pojo;

/**
 * @Title: IdSearchParamBean.java
 * @Package yasen.bigdata.infosupplyer.pojo
 * @Description: 查询参数封装类，该类为根据id查询的参数封装类
 * 参数格式需要为
 * {
 *     ids:[,,,,],
 *     backfields:[,,,]
 * }
 *
 * @author weiguangwu
 * @date  2018/4/23 14:13
 * @version V1.0
 */

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import qed.bigdata.infosupplyer.consts.DataTypeEnum;
import qed.bigdata.infosupplyer.consts.EsConsts;
import qed.bigdata.infosupplyer.consts.SysConsts;

import java.util.ArrayList;
import java.util.List;

public class IdSearchParamBean {
    private List<String> ids = null;
    private List<String> backfields = null;
    private DataTypeEnum type = null;
    private boolean parseError = false;

    public IdSearchParamBean(JSONObject param){
        init();
        try {
            parseParameter(param);
        } catch (Exception e) {
            e.printStackTrace();
            parseError = true;
        }
    }

    private void init(){
        ids = new ArrayList<String>();
        backfields = new ArrayList<String>();
    }

    /**
     * @Author:weiguangwu
     * @Description:该类解析接口传进来的参数，解析赋值给变量
     * @params:[param]
     * @return: void
     * @Date: 2018/4/23 14:52
     */
    private void parseParameter(JSONObject param) throws Exception{
        JSONArray idsParam = param.getJSONArray("ids");
        if(idsParam != null && idsParam.size()!=0){
            ids.addAll(idsParam.toJavaList(String.class));
        }else{
            ids = null;
        }

        String datatype = param.getString("datatype");
        if(SysConsts.TYPE_DICOM.equals(datatype)){
            type = DataTypeEnum.DICOM;
        }else if(SysConsts.TYPE_ELECTRIC.equals(datatype)){
            type = DataTypeEnum.ELECTRIC;
        }else if(SysConsts.TYPE_GUAGE.equals(datatype)){
            type = DataTypeEnum.GUAGE;
        }

        JSONArray backfieldsParam = param.getJSONArray("backfields");
        //下面这两段if语句逻辑：如果backfields为空则使用默认返回字段，如果不为空，取backfields，sortfields交集为排序字段
        if(backfieldsParam!=null){
            int size = backfieldsParam.size();
            if(size==1 && backfieldsParam.get(0).equals("all")){
                //backfields:[all] 这种情况认为返回所有字段。
                backfields = null;
            }else {
                for (int i=0;i<size;i++) {
                    String field = backfieldsParam.getString(i);
                    if (EsConsts.ES_DCM_FIELD.contains(field)) {
                        backfields.add(field);
                    }
                }
            }
        }else{
            backfields.addAll(EsConsts.DCM_DEFAULT_BACK_FIELD);
        }
    }

    public boolean isIdsAvailable(){
        return ids.size()>0;
    }
    public List<String> getIds() {
        return ids;
    }
    public List<String> getBackfields() {
        return backfields;
    }
    public boolean isParseError(){
        return parseError;
    }

    public DataTypeEnum getType() {
        return type;
    }
}
