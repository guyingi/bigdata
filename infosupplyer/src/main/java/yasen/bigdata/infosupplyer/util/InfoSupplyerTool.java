package yasen.bigdata.infosupplyer.util;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import yasen.bigdata.infosupplyer.consts.SysConstants;

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

        try {
            Date parse = sdf.parse("20100204");
            System.out.println(parse.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
        String delimiter = SysConstants.LEFT_SLASH;
        String osType = System.getProperty(SysConstants.OS_NAME);
        if(osType.startsWith(SysConstants.WINDOWS)){
            delimiter = SysConstants.RIGHT_SLASH;
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
}
