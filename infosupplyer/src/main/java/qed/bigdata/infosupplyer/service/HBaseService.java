package qed.bigdata.infosupplyer.service;

import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface HBaseService {

    /**
     *将下载的一个序列的所有缩略图打成zip压缩文件，放置在临时目录，当使用完之后删除即可。
     * @param rowkey
     * @param path 存放缩略图的总临时目录，/temp/thumbnailtemp，jar文件与temp同一个目录
     * @return  返回存放缩略图的压缩文件绝对路径
     */
    String downloadThumbnailByRowkey(String rowkey, String path) throws Exception;

    /**
     * 跟新某个表某个指定单元格
     * @param tablename
     * @param rowkey
     * @param cf
     * @param qualify
     * @param value
     */
    void updateColumn(String tablename, String rowkey, String cf, String qualify, String value) throws IOException;

    void updateTagForDicom(String tablename, List<String> rowkeys, String cf, String qualify, String value) throws IOException;

    /**插入单条数据：参数：表名，列簇，列，值 */
    int putOne(String tableName, String cf, JSONObject metaJson) throws IOException;

    /**插入单条数据：参数：表名，列簇，列，值 */
    int putOne(String tableName, String cf, Map<String, String> metaMap) throws IOException;

    /**插入多条数据：参数：表名，列簇，List<列，值>*/
    int putBatch(String tableName, String cf, Map<String, String> colvalues);

    /**插入一个单元格**/
    int putCell(String tableName, String rowkey, String cf, String col, byte[] value) throws IOException;

    /**删除单条数据，根据rowkey*/
    boolean delete(String tableName, String rowkey);

    boolean isExists(String tableName, String rowkey) throws IOException;
}
