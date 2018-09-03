package qed.bigdata.es.service;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import qed.bigdata.es.consts.DataTypeEnum;

/**
 *
 */
public interface SearchService {

	
	//该方法为直接从es中查询，不建议，测试完后会被废弃
//	List<Dicom> getAllMsgDirectFromES(JsonObject json);
	
	
	/**该方法是从infosupplyer微服务获取，分页查询**/
	/**
	* @Author:weiguangwu
	* @Description:
	* @params: criteria是查询条件，由页面采集
	 * pageid：需要返回的页码,传数字或者不传，不要传null
	 * pagesize：页面大小，如果大于零才分页，否则部分也，页码如果有问题自动默认为0
	* @return:
	* @Date: 2018/4/24 15:11
	*/
	JSONObject searchDicomByPaging(JSONArray criteria, JSONArray backfields, JSONArray sortfields, Integer pageid, Integer pagesize);

	/**该方法是从infosupplyer微服务获取，分页查询**/
	/**
	 * @Author:weiguangwu
	 * @Description:
	 * @params: criteria是查询条件，由页面采集
	 * pageid：需要返回的页码,传数字或者不传，不要传null
	 * pagesize：页面大小，如果大于零才分页，否则部分也，页码如果有问题自动默认为0
	 * @return:
	 * @Date: 2018/4/24 15:11
	 */
	JSONObject searchElectricByPaging(JSONArray criteria, JSONArray backfields, JSONArray sortfields, Integer pageid, Integer pagesize);

    /**该方法是从infosupplyer微服务获取，分页查询**/
	/**
	 * @Author:weiguangwu
	 * @Description:
	 * @params: criteria是查询条件，由页面采集
	 * pageid：需要返回的页码,传数字或者不传，不要传null
	 * pagesize：页面大小，如果大于零才分页，否则部分也，页码如果有问题自动默认为0
	 * @return:
	 * @Date: 2018/4/24 15:11
	 */
	public JSONObject searchKfbcByPaging(JSONArray criteria, JSONArray backfields, JSONArray sortfields, Integer pageid, Integer pagesize);

	/**
	* @Author:weiguangwu
	* @Description:该接口为获取用于下载的json文件，下载所用数据会保存到tempDir下面，
	* @params:searchcondition;查询条件
	* @return:返回json文件绝对路径
	* @Date: 2018/4/24 15:11
	*/
	String getDownloadFile(JSONArray criteria, String tempDir);

	//获取json下载文件
	/**
	* @Author:weiguangwu
	* @Description:该接口为获取用于下载的json文件，下载所用数据会保存到tempDir下面，只是查询条件为数id
	* @params:list：id列表
	* @return: 返回json文件绝对路径
	* @Date: 2018/4/24 15:14
	*/
	String getDownloadFileByIds(List<String> list, String tempDir);


	/**
	 *
	 * @param ids
	 * @return
	 */
	List<String> getHdfsPathByIds(List<String> ids, DataTypeEnum typeEnum);

	/**
	* @Author:weiguangwu
	* @Description: 该接口gen据查询条件将查询的结果数据写入一个excel表格里面，xsl文件绝对路径
	* @params:searchcondition:查询条件
	* @return: 返回excel文件绝对路径
	* @Date: 2018/4/24 15:16
	*/
	String exportExcel(JSONArray criteria, String tempDir);

	/**
	 * 传入hdfs路径，返回这批数据在hdfs上的大小
	 * @param paths
	 * @return
	 */
	Long getSizeForData(List<String> paths);

	/**
	 * 根据tag下载包含hdfs路径的json文件
	 * @param tag
	 * @param tempDir
	 * @return
	 */
	String getDownloadFileByTag(String tag,String tempDir);

	/**
	 * 根据tag下载包含hdfs路径的json文件,这个脱敏数据的，以tag为单位，同时下载多个tag
	 * @param tags
	 * @param tempDir
	 * @return
	 */
	String getDesensitizeDownloadFileByTag(List<String> tags,String tempDir);


	/**
	 * 从es的dicom表中查询所有医院名称
	 * @return
	 */
	List<String> listInstitutionName();

	/**
	 * 从es的dicom表中查询所有ManufacturerModelName名称
	 * @return
	 */
	List<String> listModality();


	/**
	 * 从es的dicom表中查询所有SeriesDescription名称
	 * @return
	 */
	List<String> listSeriesDescription();
}
