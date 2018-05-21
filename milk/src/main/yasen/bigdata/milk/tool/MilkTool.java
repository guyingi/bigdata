package yasen.bigdata.milk.tool;


/**
 * @Title: MilkTool.java
 * @Package yasen.bigdata.milk.util
 * @Description: 该工具类主要包含全局可用的一些工具函数
 * @author weiguangwu
 * @date  2018/4/23 14:13
 * @version V1.0
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import yasen.bigdata.milk.conf.MilkConfiguration;
import yasen.bigdata.milk.consts.SysConstants;
import yasen.bigdata.milk.pojo.Dicom;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MilkTool {
    static Random random = new Random();

    /**
     * @Author:weiguangwu
     * @Description:获取操作系统路径分隔符
     * @params:[]
     * @return: java.lang.String
     * @Date: 2018/4/24 14:58
     */
	public static String getDelimiter(){
        String delimiter = SysConstants.LEFT_SLASH;
        String osType = System.getProperty(SysConstants.OS_NAME);
        if(osType.startsWith(SysConstants.WINDOWS)){
            delimiter = SysConstants.RIGHT_SLASH;
        }
        return delimiter;
    }

    /**
     * @Author:weiguangwu
     * @Description:zipCompress,该方法将文件或者文件夹压缩为zip文件
     * sourceDir：压缩文件源路径，
     * zipPath：压缩文件存放路径
     * zipFileName：压缩文件名字
     * @params:[sourceDir, zipPath, zipFileName]
     * @return: boolean
     * @Date: 2018/4/24 15:00
     */
    public static boolean zipCompress(String sourceDir,String zipPath, String zipFileName){
        boolean isSuccess = true;
        try {
            ZipUtil.zip(sourceDir,zipPath,zipFileName);
        } catch (Exception e) {
            isSuccess = false;
            e.printStackTrace();
        }
        return isSuccess;
    }

    /**
     * @Author:weiguangwu
     * @Description: 该方法将字符串类型日期格式化，02/31/2017格式化为2017-02-31
     * @params:[date]
     * @return: java.lang.String
     * @Date: 2018/4/24 15:02
     */
    public static String formatData(String date){
	    if(date==null || date.length()==0)
	        return "";
	    String arr[] = date.split(SysConstants.LEFT_SLASH);
	    if(arr.length!=3)
	        return "";

	    return arr[2]+SysConstants.LINE+arr[0]+SysConstants.LINE+arr[1];
    }

    /**
     * 生成n位随机数
     * @return
     */
    public static int getRandonNumber(int n){
        int bound = 1;
        while(n-->1)
            bound*=10;
        int temp = 0;
        while(bound>(temp=random.nextInt(bound*10))){}
        return temp;
    }

    public static JSONObject doCallAndGetResult(JSONObject parameter, String interfaceStr){
        MilkConfiguration conf = new MilkConfiguration();
        JSONObject result = new JSONObject();
        JSONArray data = new JSONArray();
        List<Dicom> dicomList = new ArrayList<Dicom>();
        StringBuilder builder = new StringBuilder();
        boolean isSuccess = false;
        System.out.println(parameter.toJSONString());
        try {
            byte[] param = parameter.toString().getBytes("UTF-8");
            String url = SysConstants.HTTP_HEAD+conf.getInfosupplyerip()+":"+conf.getInfosupplyerport()+interfaceStr;
            URL restServiceURL = new URL(url);

            HttpURLConnection httpConnection = (HttpURLConnection) restServiceURL.openConnection();
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("Accept", "application/json");
            httpConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            //httpConnection.setRequestProperty("Connection", "Keep-Alive");
            httpConnection.setRequestProperty("Charset", "UTF-8");
            httpConnection.setDoOutput(true);
            httpConnection.setDoInput(true);

            //传递参数
            httpConnection.setRequestProperty("Content-Length", String.valueOf(param));
            OutputStream outputStream = httpConnection.getOutputStream();
            outputStream.write(param);
            outputStream.flush();
            outputStream.close();
            System.out.println("返回码："+httpConnection.getResponseCode());

            if (httpConnection.getResponseCode() == 200) {
                InputStream inputStream = httpConnection.getInputStream();
                BufferedReader tBufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String tempStr = null;
                while ((tempStr = tBufferedReader.readLine()) != null) {
                    builder.append(tempStr);
                }
                inputStream.close();
                isSuccess = true;
            } else {
                System.out.println("从infosupplyer获取文件失败");
            }
//		    System.out.println("接收到的数据："+builder.toString());
            httpConnection.disconnect();
        }catch (IOException e){
            e.printStackTrace();
        }

        if(isSuccess) {
            JSONReader reader = new JSONReader(new StringReader(builder.toString()));
            reader.startObject();
            while (reader.hasNext()){
                String key = reader.readString();
                if(key.equals("code")){
                    result.put("code",reader.readObject(String.class));
                }else if(key.equals("pagecount")){
                    result.put("pagecount",reader.readObject(Long.class));
                }else if(key.equals("total")){
                    result.put("total",reader.readObject(Long.class));
                }else if(key.equals("data")){
                    reader.startArray();
                    while(reader.hasNext()){
                        Dicom dicom = JSON.parseObject(reader.readObject().toString(), Dicom.class);
                        dicomList.add(dicom);
                    }
                    reader.endArray();
                    result.put("data",dicomList);
                }
            }
            reader.endObject();
        }
        return result;
    }
}
