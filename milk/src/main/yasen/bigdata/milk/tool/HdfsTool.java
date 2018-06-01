package yasen.bigdata.milk.tool;

/**
 * @Title: HdfsTool.java
 * @Package yasen.bigdata.milk.util
 * @Description: 该类包含与hdfs文件下载相关的方法
 * @author weiguangwu
 * @date  2018/4/23 14:13
 * @version V1.0
 */

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.apache.poi.ss.formula.functions.LinearRegressionFunction;
import yasen.bigdata.milk.conf.MilkConfiguration;
import yasen.dicom.DicomWritable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class HdfsTool {

    static Logger logger = Logger.getLogger(HdfsTool.class);

    /**
     * @Author:weiguangwu
     * @Description:paths参数为hdfs路径，lcoalPath为本地存放dicom文件的目录，下载一批序列dicom文件到本地
     * @params:[paths, localPath]
     * @return: boolean
     * @Date: 2018/4/23 17:23
     */
    public static boolean downloadDicom(List<String> paths,String localPath){
        if(paths==null || paths.size()==0)
            return false;
        boolean isSuccess = false;
        for(String e : paths){
            //有一个下载成功就判定为成功
            System.out.println("路径："+e);
            if(downloadSingleSeries(e,localPath)){
                isSuccess = true;
            }
        }
        return isSuccess;
    }


    /**
     * @Author:weiguangwu
     * @Description:下载单个序列到本地目录,路径末尾自带分隔符
     * @params:[hdfsPath, localPath]
     * @return: boolean
     * @Date: 2018/4/23 17:24
     */
    private static boolean downloadSingleSeries(String hdfsPath,String tempDir){
        boolean isSuccess = true;
        String dirname = hdfsPath.substring(hdfsPath.lastIndexOf("/")+1,hdfsPath.length());

        String seriesDir = tempDir+File.separator+dirname;
        if(!new File(seriesDir).exists()) {
            new File(seriesDir).mkdirs();
        }

        Configuration conf = new Configuration();
        MilkConfiguration milkConfiguration = new MilkConfiguration();
        Path path = new Path(milkConfiguration.getDefaultFs()+hdfsPath);
        SequenceFile.Reader.Option option = SequenceFile.Reader.file(path);
        SequenceFile.Reader reader = null;
        try {
            reader = new SequenceFile.Reader(conf,option);
            Text key = (Text) ReflectionUtils.newInstance(
                    reader.getKeyClass(), conf);
            DicomWritable value = (DicomWritable) ReflectionUtils.newInstance(
                    reader.getValueClass(), conf);
            FileOutputStream fout = null;
            int count = 0;
            while (reader.next(key, value)) {

                String filepath = seriesDir+File.separator+key.toString();
                if(!new File(filepath).exists())
                    new File(filepath).createNewFile();
                fout = new FileOutputStream(new File(filepath));
                logger.log(Level.INFO,"本地dicom路径："+filepath);
                System.out.println("本地路径dicom："+filepath);

                byte[] data = value.getData();
                fout.write(data);
                fout.flush();
                fout.close();
                count++;
            }
            logger.log(Level.INFO,"下载文件数："+count);
//            System.out.println("下载文件数："+count);
        } catch (IOException e) {
            isSuccess = false;
            logger.log(Level.INFO,"下载失败文件："+hdfsPath);
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(reader);
        }
        return isSuccess;
    }


    public static boolean downloadElectric(List<String> paths,String localPath){
        if(StringUtils.isBlank(localPath) || paths==null || paths.size()==0)
            return false;
        boolean isSuccess = true;
        Configuration hdfsconf = new Configuration();
        hdfsconf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
        FileSystem fs = null;
        try {
            fs = FileSystem.get(hdfsconf);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if(!new File(localPath).exists()){
            new File(localPath).mkdirs();
        }
        for(String path : paths){
            //有一个下载成功就判定为成功
            String dirname = path.substring(path.lastIndexOf("/")+1,path.length());
            String des = localPath + File.separator + dirname;
            try {
                fs.copyToLocalFile(new Path(path),new Path(des));
            } catch (IOException e) {
                e.printStackTrace();
                isSuccess = false;
            }
        }
        return isSuccess;
    }
}
