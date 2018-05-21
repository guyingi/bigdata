package yasen.bigdata.milk.tool;

/**
 * @Title: HdfsTool.java
 * @Package yasen.bigdata.milk.util
 * @Description: 该类没什么用，实验代码
 * @author weiguangwu
 * @date  2018/4/23 14:13
 * @version V1.0
 */

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.util.ReflectionUtils;
import yasen.bigdata.milk.consts.ESConstants;
import yasen.bigdata.milk.service.impl.SearchServiceImpl;
import yasen.dicom.DicomWritable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Demo {

    public static void main(String[] args) {
//        readSequenceFileTest();
        downloadJson();
    }
    private static void downloadJson(){
        JSONArray backfields = new JSONArray();
        backfields.add(ESConstants.HDFSPATH);

        JSONObject json = new JSONObject();
        JSONObject searchcondition = new JSONObject();
        searchcondition.put("sex","M");
        json.put("searchcondition",searchcondition);
        json.put("backfields",backfields);
        String interfaceStr = "/info/_searchByIds";
        String fileStr = "C:\\Users\\WeiGuangWu\\IdeaProjects\\bigdata\\milk\\target\\milk\\temp\\a.json";
        new SearchServiceImpl().doCallAndWriteToDisk(json,interfaceStr,fileStr);
    }

    public static void readSequenceFileTest(){
        //String uri = args[0];
        String hdfsPath = "hdfs://192.168.1.217:8020/yasen/soucedata/2010/12/1/1312211075232351373201101101309486350967808100099380-232019";
        String localPath = "F:\\临时文件";

        String dirname = hdfsPath.substring(hdfsPath.lastIndexOf("/")+1,hdfsPath.length());

        File localDir = new File(localPath+"\\"+dirname);
        localDir.mkdir();

        Configuration conf = new Configuration();
        Path path = new Path(hdfsPath);
        SequenceFile.Reader.Option option1 = SequenceFile.Reader.file(path);

        SequenceFile.Reader reader = null;
        try {
            reader = new SequenceFile.Reader(conf,option1);

            Text key = (Text) ReflectionUtils.newInstance(
                    reader.getKeyClass(), conf);
            DicomWritable value = (DicomWritable) ReflectionUtils.newInstance(
                    reader.getValueClass(), conf);

            long position = reader.getPosition();
            FileOutputStream fout = null;
            int count = 0;
            while (reader.next(key, value)) {

                String filepath = localPath+"\\" +dirname+"\\"+key.toString()+".IMA";
                File file = new File(filepath);
                file.createNewFile();
                fout = new FileOutputStream(file);
                System.out.println("本地路径dicom："+filepath);

                byte []data = value.getData();
                fout.write(data);
                fout.flush();
                fout.close();
                count++;
            }
            System.out.println("下载文件："+count);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(reader);
        }
    }
}
