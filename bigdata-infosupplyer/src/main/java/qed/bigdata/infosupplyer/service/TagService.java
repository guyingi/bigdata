package qed.bigdata.infosupplyer.service;

import com.alibaba.fastjson.JSONObject;
import qed.bigdata.infosupplyer.consts.DataTypeEnum;

import java.util.Set;

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

    /**
     * 检查dicom数据tag冲突，这个操作通常在打tag之前，
     * @param param
     * @return  返回的是冲突的tag
     */
    Set<String> checkTagConflictForDicom(JSONObject param);

    /**
     * 查询所有dicom序列的标签，以及每个标签下序列的数量
     * @return
     */
    JSONObject searchtags(String tag);


    boolean removeTag(String tag, DataTypeEnum type);

}
