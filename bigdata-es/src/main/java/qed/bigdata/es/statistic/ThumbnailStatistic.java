package qed.bigdata.es.statistic;

import qed.bigdata.es.tool.Tool;

import java.util.ArrayList;
import java.util.List;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package qed.bigdata.es.statistic
 * @Description: ${todo}
 * @date 2018/7/9 10:19
 */
public class ThumbnailStatistic {
   static Integer thumbnailCachePathCount = 0;
    static List<String> thumbnailCachePathList = new ArrayList<>();

    public static synchronized void add(String pathStr){
        thumbnailCachePathList.add(pathStr);
        thumbnailCachePathCount++;
    }

    public static synchronized void clearCache(){
        for(String path : thumbnailCachePathList){
            Tool.delFolder(path);
        }
        thumbnailCachePathList.clear();
        thumbnailCachePathCount = 0;
    }

    public static boolean isFull(){
        return thumbnailCachePathCount == 50;
    }

}
