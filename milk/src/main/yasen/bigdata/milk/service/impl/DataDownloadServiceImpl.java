package yasen.bigdata.milk.service.impl;

import org.springframework.stereotype.Service;
import yasen.bigdata.milk.conf.MilkConfiguration;
import yasen.bigdata.milk.consts.SysConstants;
import yasen.bigdata.milk.service.DataDownloadService;
import yasen.bigdata.milk.tool.MilkTool;
import yasen.bigdata.milk.tool.ZipUtil;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataDownloadServiceImpl implements DataDownloadService {
    public static void main(String[] args) {
        String projectPath = "C:\\Users\\WeiGuangWu\\IdeaProjects\\bigdata\\milk\\target\\milk\\temp";
        String tempDir = projectPath+MilkTool.getDelimiter()+"a.zip";
        String id = "128401136192363104997433902406223511521416862616";

        boolean b = new DataDownloadServiceImpl().doCallAndWriteToDisk(id, "/data/downloadDicomNail", tempDir);
    }


    /**
     *
     * @param id
     * @param tempRealDir   都是temp临时目录，这是绝对路径，末尾不带分隔符
     * @param tempContextPath  都是temp临时目录，这是相对路径，末尾不带分隔符
     * @return
     * @throws Exception
     */
    @Override
    public List<String> downloadDicomThumbnail(String id,String tempRealDir,String tempContextPath) throws Exception {
        //存放图片在web工程下的相对路径，前端根据此路径直接获取图片并显示
        List<String> result = new ArrayList<String>();

        //infosupplyer提供的下载接口
        String interfaceStr = "/data/downloadDicomNail";
        //本地临时压缩文件名称，6位随机数
        String zipTempName = MilkTool.getRandonNumber(6)+"";
        //zipFilePath是zip文件全路径
        String zipFilePath = tempRealDir+MilkTool.getDelimiter()+zipTempName+".zip";
        //调用下载方法，做具体下载工作
        doCallAndWriteToDisk(id,interfaceStr,zipFilePath);
        //做解压操作，zipFilePath是zip文件全路径，tempRealDir是存放解压后的文件全路径，
        // true意为将解压后的文件存放在以压缩文件名命名的目录下面
        ZipUtil.unzip(zipFilePath,tempRealDir,true);
        //存放jpg图片的目录
        String unzipDir = tempRealDir+MilkTool.getDelimiter()+zipTempName;
        File file = new File(unzipDir);
        for(File e : file.listFiles()){
            //生成缩略图相对路径，存入结果list
            result.add(tempContextPath+SysConstants.LEFT_SLASH+zipTempName+SysConstants.LEFT_SLASH+e.getName());
        }
        return result;
    }

    @Override
    public String downloadDesensitizeDdicomByTag(String tag,String tempRealDir) {
        String zipFilePath = tempRealDir+MilkTool.getDelimiter()+tag+".zip";
        //infosupplyer提供的下载接口
        String interfaceStr = "/data/downloadDesensitizeDdicomByTag";
        //调用下载方法，做具体下载工作
        doCallAndWriteToDisk(tag,interfaceStr,zipFilePath);

        return zipFilePath;
    }

    public boolean doCallAndWriteToDisk(String id,String interfaceStr,String filepath){
        MilkConfiguration conf = new MilkConfiguration();
        boolean isSuccess = false;
        try {
            String url = SysConstants.HTTP_HEAD+conf.getInfosupplyerip()+":"+conf.getInfosupplyerport()+interfaceStr+"?id="+id;
            System.out.println("url:"+url);
            URL restServiceURL = new URL(url);

            HttpURLConnection httpConnection = (HttpURLConnection) restServiceURL.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("Accept", "application/octet-stream");
            httpConnection.setRequestProperty("Content-Type", "text/html; charset=UTF-8");
            //httpConnection.setRequestProperty("Connection", "Keep-Alive");
            httpConnection.setRequestProperty("Charset", "UTF-8");
//            httpConnection.setDoOutput(true);
            httpConnection.setDoInput(true);
            //传递参数
            System.out.println("返回码："+httpConnection.getResponseCode());
            if (httpConnection.getResponseCode() == 200) {
                InputStream inputStream = httpConnection.getInputStream();
                File file = new File(filepath);
                if(!file.exists()) {
                    file.createNewFile();
                    file.setWritable(true, false);
                }
                FileOutputStream fout = new FileOutputStream(file);
                byte []tmp = new byte[1024];
                int n = -1;
                while((n=inputStream.read(tmp))!=-1) {
                    fout.write(tmp, 0, n);
                }
                fout.close();
                inputStream.close();
                isSuccess = true;
            } else {
                System.out.println("从infosupplyer下载文件失败");
                isSuccess = false;
            }
            httpConnection.disconnect();
        }catch (IOException e){
            e.printStackTrace();
        }
        return isSuccess;
    }



}
