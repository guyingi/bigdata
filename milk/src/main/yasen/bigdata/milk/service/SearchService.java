package yasen.bigdata.milk.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import yasen.bigdata.milk.pojo.Dicom;

public interface SearchService {

	
	//该方法为直接从es中查询，不建议，测试完后会被废弃
//	List<Dicom> getAllMsgDirectFromES(JsonObject json);
	
	
	/**该方法是从infosupplyer微服务获取，分页查询**/
	/**
	* @Author:weiguangwu
	* @Description:
	* @params: searchcondition是查询条件，由页面采集
	 * pageid：需要返回的页码
	 * pagesize：页面大小
	* @return:
	* @Date: 2018/4/24 15:11
	*/
	JSONObject searchByPaging(JSONObject searchcondition,int pageid,int pagesize);

	/**
	* @Author:weiguangwu
	* @Description:该接口为获取用于下载的json文件，下载所用数据会保存到tempDir下面，
	* @params:searchcondition;查询条件
	* @return:返回json文件绝对路径
	* @Date: 2018/4/24 15:11
	*/
	String getDownloadFile(JSONObject searchcondition, String tempDir);

	//获取json下载文件
	/**
	* @Author:weiguangwu
	* @Description:该接口为获取用于下载的json文件，下载所用数据会保存到tempDir下面，只是查询条件为数id
	* @params:list：id列表
	* @return: 返回json文件绝对路径
	* @Date: 2018/4/24 15:14
	*/
	String getDownloadFileByIds(List<String> list,String tempDir);

	/**
	* @Author:weiguangwu
	* @Description: 该接口gen据查询条件将查询的结果数据写入一个excel表格里面，xsl文件绝对路径
	* @params:searchcondition:查询条件
	* @return: 返回excel文件绝对路径
	* @Date: 2018/4/24 15:16
	*/
	String exportExcel(JSONObject searchcondition,String tempDir);

	/**
	* @Author:weiguangwu
	* @Description:该接口根据序列id下载该序列的dicom文件到本地，并压缩为zip文件，返回zip文件绝对路径
	* @params:
	* @return:返回zip文件绝对路径
	* @Date: 2018/4/24 15:17
	*/
	String getDicomZipByIds(List<String> list,String tempDir);
	
}
