package qed.bigdata.infosupplyer.pojo;

import com.alibaba.fastjson.JSONArray;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package yasen.bigdata.infosupplyer.pojo
 * @Description: ${todo}
 * @date 2018/5/17 16:27
 */
public class DicomSearchCriteriaBean {

    private boolean devicephrase = false;//设备是否整个串匹配，phrase查询为匹配整个串，默认是拆分的,默认模糊匹配
    private JSONArray criteria = new JSONArray();

    public DicomSearchCriteriaBean(JSONArray searchcondition){
        parseSearchcondition(searchcondition);
    }

    private void parseSearchcondition(JSONArray param){
        int size = param.size();
//        for(int i=0; i<size; i++){
//            JSONObject item = param.getJSONObject(i);
//            if(item.getString(SysConsts.SECTION).equals(SysConsts.YES)){
//                item.put(SysConsts.IS_SECTION,true);
//                if(item.keySet().contains(SysConsts.START)){
//                    item.put(SysConsts.IS_START_AVAILABLE,true);
//                }else{
//                    item.put(SysConsts.IS_START_AVAILABLE,false);
//                }
//                if(item.keySet().contains(SysConsts.END)){
//                    item.put(SysConsts.IS_END_AVAILABLE,true);
//                }else{
//                    item.put(SysConsts.IS_END_AVAILABLE,false);
//                }
//            }else{
//                item.put(SysConsts.IS_SECTION,false);
//            }
//            criteria.add(item);
//        }

    }

    public boolean isdevicePhrase(){
        return devicephrase;
    }
}
