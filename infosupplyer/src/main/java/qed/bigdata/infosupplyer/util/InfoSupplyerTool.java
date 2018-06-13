package qed.bigdata.infosupplyer.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import qed.bigdata.infosupplyer.consts.SysConsts;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.CRC32;

public class InfoSupplyerTool {
    static Random random = new Random(System.currentTimeMillis());
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    static Logger logger = Logger.getLogger(InfoSupplyerTool.class);

    public static void main(String[] args) {

        System.out.println(getRunnerPath());
    }

    synchronized public static int formatInteger(String data){
        if(data!=null && data.length()!=0){
            try{
                return Integer.parseInt(data);
            }catch (NumberFormatException e){
                return 0;
            }
        }
        return 0;
    }

    synchronized public static long formatLong(String data){
        if(data!=null && data.length()!=0){
            try{
                return Long.parseLong(data);
            }catch (NumberFormatException e){
                return 0l;
            }
        }
        return 0l;
    }

    synchronized public static double formatDouble(String data){
        if(data!=null && data.length()!=0){
            try{
                return Double.parseDouble(data);
            }catch (NumberFormatException e){
                return 0d;
            }
        }
        return 0d;
    }

    synchronized public static void recordLog(Logger logger, String seriesDir, boolean isSuccess, String msg){
        if(isSuccess){
            logger.log(Level.INFO,seriesDir+";"+msg);
        }else{
            logger.log(Level.ERROR,"失败序列目录："+seriesDir+";失败原因："+msg);
        }
    }

    /**
     * 判断是不是dicom文件，如果是则返回true,否则返回false
     * @param source
     * @return
     */

    public static String getMD5(String source){
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(source.getBytes("UTF-8"));
            byte[] md5Array = md5.digest();
            return bytesToHexString(md5Array);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String getDelimiter(){
        String delimiter = SysConsts.LEFT_SLASH;
        String osType = System.getProperty(SysConsts.OS_NAME);
        if(osType.startsWith(SysConsts.WINDOWS)){
            delimiter = SysConsts.RIGHT_SLASH;
        }
        return delimiter;
    }
    private static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 生成n位随机数
     * @return
     */
    public static int generateRandonNumber(int n){
        int bound = 1;
        while(n-->1)
            bound*=10;
        int temp = 0;
        while(bound>(temp=random.nextInt(bound*10))){}
        return temp;
    }

    //CRC32,获取字符串source n位循环冗余数
    public static String getCRC32(String source,int n){
        long width = 1;
        while(n-->0) width*=10;
        CRC32 crc32 = new CRC32();
        long result = 0;
        try {
            crc32.update(source.getBytes("UTF-8"));
            result = crc32.getValue()%width;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result+"";
    }

    public static long getTimeStamp(){
        return new Date().getTime();
    }

    public static String getTodayDate(){
        return sdf.format(new Date());
    }

    //n不能大于9，因为int类型位数限制
    public static String formatDigitalToNBit(String numberStr,int n){
        String result = "0000000000"+numberStr;
        result = result.substring(result.length()-n,result.length());
        return result;
    }

    /**
     * @Author:weiguangwu
     * @Description:格式化参数，将接口接收的参数转化为PageSearchParamBean能解析的内容
     * @params:[parameter]
     * @return: com.alibaba.fastjson.JSONObject
     * @Date: 2018/4/23 14:42
     */
    public static JSONObject formatParameter(Map<String, Object> parameter) {
        JSONObject param = new JSONObject();
        for (Map.Entry<String, Object> entry : parameter.entrySet()) {
            if (entry.getKey().equals(SysConsts.CRITERIA)) {
                JSONArray criteriaArr = new JSONArray();
                List<Map<String,String>> criteriaList = (List<Map<String,String>>)entry.getValue();
                for(Map<String,String> map : criteriaList){
                    JSONObject criteriaObj = new JSONObject();
                    String section = map.get("section");
                    if(section.equals("yes")){
                        if(map.keySet().contains("start")){
                            criteriaObj.put("start",map.get("start"));
                        }
                        if(map.keySet().contains("end")){
                            criteriaObj.put("end",map.get("end"));
                        }
                    }else{
                        String value = map.get("value");
                        criteriaObj.put("value",value);
                    }
                    criteriaObj.put("section",section);
                    criteriaObj.put("keyword",map.get("keyword"));

                    criteriaArr.add(criteriaObj);
                }
                param.put(SysConsts.CRITERIA, criteriaArr);
            } else if (SysConsts.BACKFIELDS.equals(entry.getKey())) {
                List<String> list = (List<String>) entry.getValue();
                JSONArray arr = new JSONArray();
                for (String e : list) {
                    arr.add(e);
                }
                param.put(SysConsts.BACKFIELDS, arr);
            } else if (SysConsts.SORTFIELDS.equals(entry.getKey())) {
                List<String> list = (List<String>) entry.getValue();
                JSONArray arr = new JSONArray();
                for (String e : list) {
                    arr.add(e);
                }
                param.put(SysConsts.SORTFIELDS, arr);
            } else {
                if (entry.getKey().equals(SysConsts.PAGE_ID)) {
                    param.put(SysConsts.PAGE_ID, Integer.parseInt(entry.getValue().toString()));
                } else if (entry.getKey().equals(SysConsts.PAGE_SIZE)) {
                    param.put(SysConsts.PAGE_SIZE, Integer.parseInt(entry.getValue().toString()));
                } else {
                    param.put(entry.getKey(), entry.getValue().toString());
                }
            }
        }
        return param;
    }

    /**
     * 格式化字符串日期20170302 to /2017/03/02
     * 如果解析出错，时间默认为19270/01/01
     * @param dateString
     * @return
     */
    public static String parseDateToPath(String dateString){
        String year = "1970";
        String month = "01";
        String day = "01";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date date = null;
        try {
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            logger.log(Level.ERROR,e);
            e.printStackTrace();
        }
        if(date!=null){
            Calendar instance = Calendar.getInstance();
            instance.setTime(date);
            year = instance.get(Calendar.YEAR)+"";
            month = instance.get(Calendar.MONTH)+1+"";
            day = instance.get(Calendar.DAY_OF_MONTH)+"";
        }
        return SysConsts.LEFT_SLASH+year+ SysConsts.LEFT_SLASH+month+ SysConsts.LEFT_SLASH+day;
    }

    /**
     * 清空文件夹，只是删除子文件，传入的目录不做删除
     */
    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);//再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }
    /*********删除整个文件夹，包括所有子文件和本文件夹************/
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); //删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete(); //删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void delSingleFile(String filePath){
        if(new File(filePath).exists()){
            new File(filePath).delete();
        }
    }


    public static String getRunnerPath(){
        String rootPath = "";
        String path = InfoSupplyerTool.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if("windows".equals(getOS())){
            rootPath = path.substring(1, path.length()-1);
            rootPath = rootPath.replace(SysConsts.LEFT_SLASH,"\\");
        }else if("linux".equals(getOS())){
            //file:/home/ms/project/microservice/infosupplyer-1.0-SNAPSHOT.jar!/BOOT-INF/classes!/
            String temp1 = path.split(":")[1].split("!")[0];
            String tempArr[] = temp1.split("/");
            for(String e : tempArr){
                if(!e.endsWith(".jar")){
                    if(e.length()!=0)
                        rootPath += "/"+e;
                }else{
                    break;
                }
            }
        }
        return rootPath;
    }

    public static String getOS(){
        Properties prop = System.getProperties();
        String os = prop.getProperty("os.name");
        if(os.startsWith("win")|| os.startsWith("Win")){
            return "windows";
        }else if(os.startsWith("Linux")|| os.startsWith("linux")){
            return "linux";
        }else{
            return "";
        }
    }

}
