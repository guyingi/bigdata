package yasen.bigdata.milk.service;

import com.alibaba.fastjson.JSONObject;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package yasen.bigdata.milk.service
 * @Description: 这个是给数据打标记的一个service，很多查询数据以及打标记操作都在这个里面完成
 * @date 2018/5/17 15:04
 */
public interface TagService {

    /**
     * 该方法用于给满足条件param的所有dicom数据都打上标签tag
     * @param param
     * @param tag
     * @return
     */
    Long signTag(JSONObject param,String tag);

    /**
     * 做实际的脱敏操作
     * @param tag
     * @return
     */
    boolean doDesensitize(String tag);


}
