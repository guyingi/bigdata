package yasen.bigdata.infosupplyer.service;

import java.io.IOException;

public interface HBaseService {

    /**
     *将下载的一个序列的所有缩略图打成zip压缩文件，放置在临时目录，当使用完之后删除即可。
     * @param rowkey
     * @return  返回存放缩略图的压缩文件绝对路径
     */
    String downloadThumbnailByRowkey(String rowkey,String path) throws Exception;
}
