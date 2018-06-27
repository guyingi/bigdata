package qed.bigdata.es.controller;

import java.io.*;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;

import qed.bigdata.es.consts.DataTypeEnum;
import qed.bigdata.es.consts.ESConsts;
import qed.bigdata.es.consts.SysConsts;
import qed.bigdata.es.factory.ConfFactory;
import qed.bigdata.es.service.DataDownloadService;
import qed.bigdata.es.service.SearchService;
import qed.bigdata.es.tool.Tool;

@Controller
@RequestMapping("")
public class DicomSearchController {
    static Logger logger = Logger.getLogger(DicomSearchController.class);

	@Autowired
    SearchService searchService;

	@Autowired
    DataDownloadService dataDownloadService;

	@RequestMapping("search")
	public ModelAndView jump() {
		System.out.println("jump");
		ModelAndView mav = new ModelAndView();
		mav.setViewName("search");
		return mav;
	}

    @RequestMapping(value = "ajaxSearch", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject ajaxSearch(HttpServletRequest request, HttpServletResponse response,@RequestBody Map<String, String> parameter) {
        logger.log(Level.INFO,"controller:ajaxSearch 被调用");
        JSONObject paramJson = JSONObject.parseObject(JSON.toJSONString(parameter));
        logger.log(Level.INFO,"接口接收的参数:"+paramJson.toJSONString());

        JSONObject result = new JSONObject();
        JSONArray param = formatParameter(parameter);
        System.out.println(param.toJSONString());
        HttpSession session = request.getSession();
        session.setAttribute("searchParam", param);

        JSONArray backfields = new JSONArray();
        backfields.add(ESConsts.InstitutionName_ES);
        backfields.add(ESConsts.PatientName_ES);
        backfields.add(ESConsts.SeriesDate_ES);
        backfields.add(ESConsts.ManufacturerModelName_ES);
        backfields.add(ESConsts.SeriesDescription_ES);
        backfields.add(ESConsts.NumberOfSlices_ES);
        backfields.add(ESConsts.TAG_ES);
        backfields.add(ESConsts.ID_ES);
//
        JSONArray sortfields = new JSONArray();
        sortfields.add(ESConsts.InstitutionName_ES);
        sortfields.add(ESConsts.SeriesDescription_ES);
        sortfields.add(ESConsts.PatientName_ES);
        sortfields.add(ESConsts.SeriesDate_ES);
        sortfields.add(ESConsts.TAG_ES);
        sortfields.add(ESConsts.NumberOfSlices_ES);

        JSONObject tempResult = searchService.searchDicomByPaging(param, backfields,sortfields,1, SysConsts.DEFAULT_PAGE_SIZE);
        Long total = tempResult.getLong("total");
        if(total==null || total==0L)
            result.put("total",0L);
        else
            result.put("total",total);

        result.put("rows",tempResult.getJSONArray("data"));

        logger.log(Level.DEBUG,"接口返回结果:"+result.toJSONString());
        logger.log(Level.INFO,"接口返回结果:"+"total:"+tempResult.getLong("total"));
        return result;
    }

    @RequestMapping(value = "ajaxPage", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject ajaxPage(HttpServletRequest request, HttpServletResponse response,
                               @RequestParam(value="page", required=false) String page,
                               @RequestParam(value="rows", required=false) String rows) {
        logger.log(Level.INFO,"controller:ajaxPage 被调用");


        JSONObject result = new JSONObject();
        Integer pageid = Integer.parseInt(page);
        HttpSession session = request.getSession();
        JSONArray param = (JSONArray)session.getAttribute("searchParam");

        logger.log(Level.INFO,"接口接收的参数:pageid:"+pageid+",searchParam"+param.toJSONString());

        JSONArray backfields = new JSONArray();
        backfields.add(ESConsts.InstitutionName_ES);
        backfields.add(ESConsts.PatientName_ES);
        backfields.add(ESConsts.SeriesDate_ES);
        backfields.add(ESConsts.ManufacturerModelName_ES);
        backfields.add(ESConsts.SeriesDescription_ES);
        backfields.add(ESConsts.NumberOfSlices_ES);
        backfields.add(ESConsts.TAG_ES);
        backfields.add(ESConsts.ID_ES);
//
        JSONArray sortfields = new JSONArray();
        sortfields.add(ESConsts.InstitutionName_ES);
        sortfields.add(ESConsts.SeriesDescription_ES);
        sortfields.add(ESConsts.PatientName_ES);
        sortfields.add(ESConsts.SeriesDate_ES);
        sortfields.add(ESConsts.TAG_ES);
        sortfields.add(ESConsts.NumberOfSlices_ES);

        JSONObject tempResult = searchService.searchDicomByPaging(param, backfields,sortfields,pageid, SysConsts.DEFAULT_PAGE_SIZE);
        System.out.println(tempResult.toJSONString());
        result.put("total",tempResult.getLong("total"));
        result.put("rows",tempResult.getJSONArray("data"));
        System.out.println("total:"+tempResult.getLong("total"));

        logger.log(Level.DEBUG,"接口返回结果:"+result.toJSONString());
        logger.log(Level.INFO,"接口返回结果:"+"total:"+tempResult.getLong("total"));

        return result;
    }

    @RequestMapping(value="exportallpath")
    public void exportallpath(HttpServletRequest request, HttpServletResponse response) {
        logger.log(Level.INFO,"controller:exportallpath 被调用");
        String projectPath = request.getSession().getServletContext().getRealPath("/");
        String tempDir = projectPath+"temp"+Tool.getDelimiter();
        HttpSession session = request.getSession();
        JSONArray param = (JSONArray)session.getAttribute("searchParam");
        String tempFilePath = null;
        if(param!=null)
            tempFilePath = searchService.getDownloadFile(param, tempDir);
        Tool.doDownload(response,tempFilePath,"json");
        logger.log(Level.INFO,"controller:exportallpath 调用结束");
    }


    //导出部分选中项路径
    @RequestMapping(value="exportdsomepath")
    public void exportdsomepath(HttpServletRequest request, HttpServletResponse response) {
        logger.log(Level.INFO,"controller:exportdsomepath 被调用");

        int times = 10;
        String projectPath = request.getSession().getServletContext().getRealPath("/");
        String tempDir = projectPath+"temp"+Tool.getDelimiter();
        HttpSession session = request.getSession();
        List<String> ids = ( List<String>)session.getAttribute("ids");
        while(ids==null && times-->0){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ids = ( List<String>)session.getAttribute("ids");
        }
        String tempFilePath = null;
        if(ids != null)
            tempFilePath = searchService.getDownloadFileByIds(ids,tempDir);
        if( !StringUtils.isBlank(tempFilePath) ){
            Tool.doDownload(response,tempFilePath,"json");
            logger.log(Level.DEBUG,"返回结果成功:"+tempFilePath);
        }else{
            logger.log(Level.DEBUG,"返回结果失败:"+tempFilePath);
        }
    }
    /**
     * 在下载部分选中项的json文件时，需要传选中项的id值过来，但是ajax方式不能下载，因为
     * javascript不能直接操作磁盘，传给页面的数据存放JavaScript的内存中。所以我采用了使用ajax提交id参数给exportSomeHelp，
     * 在exportSomeHelp中将参数解析出来存储到Session中，页面同时请求另一个地址/es/downloadsome,这个直接访问，可以触发页面
     * 文件保存弹窗。在downloadsome方法中将Session的ids参数取出用于查询获取结果，然后下载给页面，由于两个方法同时访问，先后差别。
     * 所以在downloadsome中使用轮询机制，轮询10次，每次100ms,如果还不能在Session获取参数则返回失败。
     *
     * @param request
     * @param response
     * @param parameter
     * @return
     */
    @RequestMapping(value = "exportdsomepathhelp", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject exportSomePathHelp(HttpServletRequest request, HttpServletResponse response,@RequestBody Map<String, Object> parameter) {
        logger.log(Level.INFO,"controller:exportdsomepathhelp 被调用");

        String type = (String)parameter.get("type");
        System.out.println("type:"+type);
        //调用_searchpaging接口
        if(type!=null && type.equals("some")){   //调用
            List<String> ids = (List<String>)parameter.get("ids");
            for(String e : ids){
                System.out.println(e);
            }
            HttpSession session = request.getSession();
            session.setAttribute("ids", ids);

        }
        return new JSONObject();
    }

    @RequestMapping(value="exportexcel")
    public void exportexcel(HttpServletRequest request, HttpServletResponse response) {
        logger.log(Level.INFO,"controller:exportexcel 被调用");

        String projectPath = request.getSession().getServletContext().getRealPath("/");
        String tempDir = projectPath+"temp"+Tool.getDelimiter();
        HttpSession session = request.getSession();
        JSONArray param = (JSONArray)session.getAttribute("searchParam");
        String tempFilePath = null;
        if(param!=null)
            tempFilePath = searchService.exportExcel(param, tempDir);
        Tool.doDownload(response,tempFilePath,"excel");
    }

    @RequestMapping(value="downloaddicom")
    public void downloadDicom(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.log(Level.INFO,"controller:downloaddicom 被调用");

        //步骤一、获取本地临时目录以及待下载id，这里的id是elasticsearch的_id字段
        int times = 10;
        String projectPath = request.getSession().getServletContext().getRealPath("/");
        //C:\Users\WeiGuangWu\IdeaProjects\bigdata\es\target\es\temp
        String tempDir = projectPath+"temp";
        String name = "dicom"+Tool.getRandonNumber(3);
        String dicomTempDir = tempDir + File.separator + name;
        HttpSession session = request.getSession();
        List<String> ids = ( List<String>)session.getAttribute("idsForDicomFileDownload");
        while(ids==null && times-->0){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ids = ( List<String>)session.getAttribute("idsForDicomFileDownload");
        }

        String zipSrcDir = null;
        if(ids!=null && ids.size()!=0) {
            zipSrcDir = dataDownloadService.downloadDicomByIds(ids, dicomTempDir);
        }

        if( !StringUtils.isBlank(zipSrcDir) ){
            Tool.zipCompress(zipSrcDir,tempDir,name+".zip");
            String zipFilePath = tempDir+File.separator+name+".zip";
            Tool.doDownload(response,zipFilePath,"zip");

            //删除临时目录，zip文件
            Tool.delFolder(zipSrcDir);
            Tool.delFile(zipFilePath);
            logger.log(Level.DEBUG,"dicom下载成功");
        }else{
            logger.log(Level.DEBUG,"dicom下载失败");
        }
    }

    @RequestMapping(value = "downloaddicomhelp", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject downloadDicomHelp(HttpServletRequest request, HttpServletResponse response,@RequestBody Map<String, Object> parameter) {
        logger.log(Level.INFO,"controller:downloaddicomhelp 被调用");

        List<String> ids = (List<String>)parameter.get(SysConsts.IDS);
        JSONArray idsJson = JSON.parseArray(JSON.toJSONString(ids));
        logger.log(Level.INFO,"接收参数:"+idsJson);

        JSONObject result = new JSONObject();
        HttpSession session = request.getSession();
        session.setAttribute("idsForDicomFileDownload", ids);

        List<String> hdfsPathByIds = searchService.getHdfsPathByIds(ids,DataTypeEnum.DICOM);
        Long sizeOfDicom = searchService.getSizeForData(hdfsPathByIds);
        Long downloadThreshhold = ConfFactory.getMilkConfiguration().getDownloadThreshhold();
        if(sizeOfDicom > downloadThreshhold){
            result.put("result",false);
        }else{
            result.put("result",true);
        }
        return result;
    }

    //assSearchHospital 是个ajax请求方法，医院做成联想搜索
    @ResponseBody
    @RequestMapping(value = "associativeSearchManufacturerModelName", method = RequestMethod.GET)
    public List<Map<String,String>> associativeSearchManufacturerModelName() {
        logger.log(Level.INFO,"controller:associativeSearchManufacturerModelName 被调用");

        List<Map<String,String>> list = new ArrayList<Map<String,String>>();
        Map<String,String> map;

        List<String> manufacturerModelNameList = searchService.listManufacturerModelName();

        for(String e : manufacturerModelNameList){
            map = new HashMap<String,String>();
            map.put("lable",e);
            map.put("text",e);
            list.add(map);
        }
        JSONArray resultJson = JSON.parseArray(JSON.toJSONString(list));
        logger.log(Level.INFO,"controller:associativeSearchManufacturerModelName反与前数据:"+resultJson.toJSONString());
        return list;
    }

    //assSearchHospital 是个ajax请求方法，医院做成联想搜索
    @ResponseBody
    @RequestMapping(value = "associativeSearchInstitutionName", method = RequestMethod.GET)
    public List<Map<String,String>> associativeSearchInstitutionName() {
        logger.log(Level.INFO,"controller:associativeSearchInstitutionName 被调用");

        List<String> institutionNameList = searchService.listInstitutionName();

        List<Map<String,String>> list = new ArrayList<Map<String,String>>();
        Map<String,String> map;
        for(String e : institutionNameList){
            map = new HashMap<String,String>();
            map.put("lable",e);
            map.put("text",e);
            list.add(map);
        }

        JSONArray resultJson = JSON.parseArray(JSON.toJSONString(list));
        logger.log(Level.INFO,"controller:associativesearchinstitution反与前端数据:"+resultJson.toJSONString());

        return list;
    }

    //assSearchHospital 是个ajax请求方法，医院做成联想搜索
    @ResponseBody
    @RequestMapping(value = "associativeSearchSeriesDescription", method = RequestMethod.GET)
    public List<Map<String,String>> associativeSearchSeriesDescription() {
        logger.log(Level.INFO,"controller:associativeSearchSeriesDescription 被调用");
        List<String> seriesDescriptionList = searchService.listSeriesDescription();

        List<Map<String,String>> list = new ArrayList<Map<String,String>>();
        Map<String,String> map;
        for(String e : seriesDescriptionList){
            map = new HashMap<String,String>();
            map.put("lable",e);
            map.put("text",e);
            list.add(map);
        }
        JSONArray resultJson = JSON.parseArray(JSON.toJSONString(list));
        logger.log(Level.INFO,"controller:associativeSearchSeriesDescription:"+resultJson.toJSONString());
        return list;
    }



	private JSONObject parseParameter(HttpServletRequest request) {
        JSONObject json = new JSONObject();
		Enumeration<String> enumeration = request.getParameterNames();
		while(enumeration.hasMoreElements()) {
			String paramName = enumeration.nextElement();
			String value = request.getParameter(paramName);
			if(value.length()!=0)
				json.put(paramName, value);
		}
		return json;
	}

	public JSONArray formatParameter(Map<String, String> parameter){
        JSONArray param = new JSONArray();

        Map<String,List<String>> tempMap = new HashMap<>();
        for(Map.Entry<String, String> entry : parameter.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if(StringUtils.isBlank(value)){
                continue;
            }

            if(key.indexOf("Date")>0 || key.indexOf("date")>0){
                value = formatDate(value);
            }

            List<String> tempList = null;
            String keyword;
            if(key.indexOf(SysConsts.UNDERLINE)>0){
                String[] split = key.split(SysConsts.UNDERLINE);
                keyword = split[0];
                String suffix = split[1];
                if(tempMap.containsKey(keyword)){
                    tempList = tempMap.get(keyword);
                    tempList.add(suffix+SysConsts.UNDERLINE+value);
                }else{
                    tempList = new ArrayList<>();
                    tempList.add(suffix+SysConsts.UNDERLINE+value);
                }
                tempMap.put(keyword,tempList);
            }else{
                keyword = key;
                if(tempMap.containsKey(keyword)){
                    tempList = tempMap.get(keyword);
                    tempList.add(value);
                }else{
                    tempList = new ArrayList<>();
                    tempList.add(value);
                }
            }
            tempMap.put(keyword,tempList);
        }

        for(Map.Entry<String,List<String>> entry : tempMap.entrySet()) {
            String key = entry.getKey();
            List<String> values= entry.getValue();
            JSONObject item = new JSONObject();
            if(values.get(0).indexOf(SysConsts.UNDERLINE)>0){
                //为区间参数
                item.put("section","yes");
                item.put("keyword",key);
                for(String value : values){
                    if(value.startsWith("start")){
                        item.put("start",value.substring(value.indexOf(SysConsts.UNDERLINE)+1,value.length()));
                    }
                    if(value.startsWith("end")){
                        item.put("end",value.substring(value.indexOf(SysConsts.UNDERLINE)+1,value.length()));
                    }
                }
            }else{
                item.put("section","no");
                item.put("keyword",key);
                item.put("value",values.get(0));
            }
            param.add(item);
        }
        return param;
    }

    @ResponseBody
    @RequestMapping(value = "getdicomThumbnail", method = RequestMethod.POST)
    public JSONObject getDicomThumbnail(HttpServletRequest request,@RequestBody Map<String, String> parametr) {
        logger.log(Level.INFO,"controller:getdicomThumbnail 被调用");
        String id = parametr.get("id");

        
        //projectPath是工程绝对路径 C://.../../es
        String projectRealPath = request.getSession().getServletContext().getRealPath(SysConsts.LEFT_SLASH);
        //工程下面temp目录绝对路径，用于存放临时文件，操作需要
        String tempRealPath = projectRealPath+SysConsts.TEMP_STRING;

        //contextPath是上下文相对路径，/es，获取这个是因为页面显示需要显示为/es/temp/2323/00001.jpg
        String contextPath = request.getContextPath();
        //上下文路径在后面新增一个临时目录/temp
        String tempContextPath = contextPath+SysConsts.LEFT_SLASH+SysConsts.TEMP_STRING;


        List<String> picturePathList = null;
        try {
            picturePathList = dataDownloadService.downloadDicomThumbnail(id,tempRealPath,tempContextPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject result = new JSONObject();
        JSONArray tempArray = new JSONArray();
        long total = picturePathList.size();
        result.put("total",total);

        //下面代码块将缩略图相对路径存储到JSONObject中然后返回给前端
        JSONObject temp = null;
        for(int i = 0; i < total; i++){
            if(i % 3 == 0){
                if(temp != null && temp.size() != 0)
                    tempArray.add(temp);
                temp = new JSONObject();
            }
            temp.put("image"+(i%3),picturePathList.get(i));
        }
        if(temp != null && temp.size() != 0)
            tempArray.add(temp);
        result.put("rows",tempArray);

        logger.log(Level.INFO,"controller:getdicomThumbnail返与前端结果"+result.toJSONString());
        return result;
    }

    private void printResult(JSONObject result){
        System.out.println("即将显示页面的数据："+result.toJSONString());
    }


    //将日期格式从12/04/2017格式化为2017-12-04
    private String formatDate(String date){
        String result = "";
        if(!StringUtils.isBlank(date)){
            String[] temp = date.split("/");
            result = temp[2]+SysConsts.LINE+temp[0]+SysConsts.LINE+temp[1];
        }
        return result;
    }

    /*	//老用法1
	@RequestMapping(value = "search", method = RequestMethod.POST)
	@ResponseBody
	public List<Map<String,String>> search(@RequestBody Map<String, String> paramJson) {
		ModelAndView mad = new ModelAndView("search");
		JsonObject param = new JsonObject();
		for(Map.Entry<String, String> entry : paramJson.entrySet()) {
			param.addProperty(entry.getKey(),entry.getValue());
			System.out.println(entry.getKey()+":"+entry.getValue());
		}
		List<Dicom> data = searchService.getAllMsgDirectFromES(param);
		 ModelAndView mad = new ModelAndView("search");
		 mad.addObject("dicoms", data);
		List<Map<String,String>> list = new LinkedList<Map<String,String>>();
		return list;
	}*/

    /*	//老用法2
	@RequestMapping(value="show")
	public ModelAndView showData(HttpServletRequest request, HttpServletResponse response){
		JSONObject param = parseParameter(request);
		System.out.println("show接收的参数："+param.toString());
		HttpSession session = request.getSession();
		session.setAttribute("searchParam", param);
	    JSONObject data = searchService.searchByPaging(param);
	    ModelAndView mad = new ModelAndView("search");
	    //将数据存入modelMap
	    mad.addObject("dicoms", data);
	    mad.addObject("imacount",100);
	    mad.addObject("personcount",20);
	    return mad;
	}*/

    /*    //老用法3
	// 可用，暂时不适用，paramJson接收到json数据,request中没有数据
	@RequestMapping(value = "form", method = RequestMethod.POST)
	public List<Dicom> formData(HttpServletRequest request, HttpServletResponse response,@RequestBody Map<String, String> paramJson) {
		JsonObject param = new JsonObject();
		for(Map.Entry<String, String> entry : paramJson.entrySet()) {
			if(entry.getValue().length()!=0)
				param.addProperty(entry.getKey(),entry.getValue());
		}
		HttpSession session = request.getSession();
		session.setAttribute("searchParam", param);
		JSONObject json = new JSONObject();
		List<Dicom> list = new ArrayList<Dicom>();
		return list;
	}*/
}
