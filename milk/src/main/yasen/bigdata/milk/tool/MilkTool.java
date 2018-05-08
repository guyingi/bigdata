package yasen.bigdata.milk.tool;


/**
 * @Title: MilkTool.java
 * @Package yasen.bigdata.milk.tool
 * @Description: 该工具类主要包含全局可用的一些工具函数
 * @author weiguangwu
 * @date  2018/4/23 14:13
 * @version V1.0
 */

import yasen.bigdata.milk.conf.SysConstants;

public class MilkTool {

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

}
