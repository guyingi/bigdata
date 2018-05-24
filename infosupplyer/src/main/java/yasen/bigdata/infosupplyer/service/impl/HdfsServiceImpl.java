package yasen.bigdata.infosupplyer.service.impl;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import yasen.bigdata.infosupplyer.consts.SysConstants;
import yasen.bigdata.infosupplyer.service.HdfsService;
import yasen.dicom.DicomWritable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package yasen.bigdata.infosupplyer.service.impl
 * @Description: ${todo}
 * @date 2018/5/20 10:34
 */
@Service("HdfsService")
public class HdfsServiceImpl implements HdfsService {

    static Logger logger = Logger.getLogger(HdfsServiceImpl.class);

    public boolean downloadDicom(List<String> paths, String localPath){
        if(paths==null || paths.size()==0)
            return false;
        boolean isSuccess = true;
        if(localPath.endsWith(File.separator)){
            localPath = localPath.substring(0,localPath.length()-1);
        }
        for(String e : paths){
            if(downloadSingleSeries(e,localPath)){
                System.out.println("下载："+e);
            }else{
                isSuccess = false;
                System.out.println("失败："+e);
            }
        }
        return isSuccess;
    }

    private boolean downloadSingleSeries(String hdfsPath,String localPath){
        boolean isSuccess = true;
        String dirname = hdfsPath.substring(hdfsPath.lastIndexOf("/")+1,hdfsPath.length());
//        System.out.println(localPath+dirname);
        File localSeriaDir = new File(localPath+File.separator+dirname);
        if(!localSeriaDir.exists()) {
            localSeriaDir.mkdirs();
        }
        Configuration conf = new Configuration();
        conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
        String hdfspre = conf.get("fs.defaultFS");
        Path path = new Path(hdfspre+hdfsPath);
        SequenceFile.Reader.Option option = SequenceFile.Reader.file(path);
        SequenceFile.Reader reader = null;
        try {
            reader = new SequenceFile.Reader(conf,option);
            Text key = (Text) ReflectionUtils.newInstance(
                    reader.getKeyClass(), conf);
            DicomWritable value = (DicomWritable)ReflectionUtils.newInstance(
                    reader.getValueClass(), conf);
            if(value==null){
            }
            FileOutputStream fout = null;
            int count = 0;
            while (reader.next(key, value)) {

                String filepath = localPath+File.separator+dirname+File.separator+key.toString();
                File file = new File(filepath);
                if(!file.exists())
                    file.createNewFile();
                fout = new FileOutputStream(file);
                logger.log(Level.INFO,"本地dicom路径："+filepath);

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

    @Override
    public int upDicomDesensitization(String localDir, String remoteDir, Configuration hdfsconf) throws IOException {
        File localDirFile = new File(localDir);
        FileSystem fs = FileSystem.get(hdfsconf);
        for(File file : localDirFile.listFiles()){
            fs.copyFromLocalFile(new Path(file.getAbsolutePath()),new Path(remoteDir+SysConstants.LEFT_SLASH+file.getName()));
        }
        return SysConstants.SUCCESS;
    }

    @Override
    public String[] downDicomDesensitization(String localDir, String remoteDir, Configuration hdfsconf) throws IOException {
        String desensitizedFileName = remoteDir.substring(remoteDir.lastIndexOf(SysConstants.LEFT_SLASH), remoteDir.length());
        String[] localFilePath = new String[2];
        localFilePath[0] = localDir+File.separator+desensitizedFileName+".mhd";
        localFilePath[1] = localDir+File.separator+desensitizedFileName+".raw";
        FileSystem fs = FileSystem.get(hdfsconf);
        fs.copyToLocalFile(new Path(remoteDir+SysConstants.LEFT_SLASH+desensitizedFileName+".mhd"),
                new Path(localDir+File.separator+desensitizedFileName+".mhd"));
        fs.copyToLocalFile(new Path(remoteDir+SysConstants.LEFT_SLASH+desensitizedFileName+".raw"),
                new Path(localDir+File.separator+desensitizedFileName+".raw"));
        return localFilePath;
    }


}
