package yasen.bigdata.infosupplyer.service;

import com.alibaba.fastjson.JSONObject;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package yasen.bigdata.infosupplyer.service
 * @Description: ${todo}
 * @date 2018/5/17 17:11
 */
public interface TagService {


    /**
     * 该方法接收的是一个json格式参数，例子如下
     * {
     *     "tag";"宁波肺"
     *     "searchcondition":{
     *         "name":"XXX",
     *         "age":"XXX"
     *     }
     * }
     * @param param
     * @return
     */
    Integer signForDicom(JSONObject param);

}
