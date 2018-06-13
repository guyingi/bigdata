package qed.bigdata.es.controller;

import java.io.*;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;

import qed.bigdata.es.consts.ESConstants;
import qed.bigdata.es.service.DataDownloadService;
import qed.bigdata.es.service.SearchService;
import qed.bigdata.es.consts.SysConstants;
import qed.bigdata.es.tool.MilkTool;

@Controller
@RequestMapping("")
public class DicomSearchController {

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
    public JSONObject ajaxSearch(HttpServletRequest request, HttpServletResponse response,@RequestBody Map<String, String> parametr) {
        System.out.println("ajaxSearch is called");

        JSONObject result = new JSONObject();
        JSONArray param = formatParameter(parametr);
        System.out.println(param.toJSONString());
        HttpSession session = request.getSession();
        session.setAttribute("searchParam", param);

        JSONArray backfields = new JSONArray();
        backfields.add(ESConstants.InstitutionName_ES);
        backfields.add(ESConstants.PatientName_ES);
        backfields.add(ESConstants.SeriesDate_ES);
        backfields.add(ESConstants.ManufacturerModelName_ES);
        backfields.add(ESConstants.NumberOfSlices_ES);
        backfields.add(ESConstants.TAG_ES);
        backfields.add(ESConstants.ID_ES);
//
        JSONArray sortfields = new JSONArray();
        sortfields.add(ESConstants.InstitutionName_ES);
        sortfields.add(ESConstants.SeriesDescription_ES);
        sortfields.add(ESConstants.PatientName_ES);
        sortfields.add(ESConstants.SeriesDate_ES);
        sortfields.add(ESConstants.TAG_ES);
        sortfields.add(ESConstants.NumberOfSlices_ES);

        JSONObject tempResult = searchService.searchDicomByPaging(param, backfields,sortfields,1, SysConstants.DEFAULT_PAGE_SIZE);
        result.put("total",tempResult.getLong("total"));
        result.put("rows",tempResult.getJSONArray("data"));
        System.out.println("total:"+tempResult.getLong("total"));
//        result.put("total",0);
//        result.put("rows","[]");
        return result;
    }

    @RequestMapping(value = "ajaxPage", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject ajaxPage(HttpServletRequest request, HttpServletResponse response,
                               @RequestParam(value="page", required=false) String page,
                               @RequestParam(value="rows", required=false) String rows) {
        System.out.println("ajaxPage  is called");

        JSONObject result = new JSONObject();
        Integer pageid = Integer.parseInt(page);
        HttpSession session = request.getSession();
        JSONArray param = (JSONArray)session.getAttribute("searchParam");

        JSONArray backfields = new JSONArray();
        backfields.add(ESConstants.InstitutionName_ES);
        backfields.add(ESConstants.PatientName_ES);
        backfields.add(ESConstants.SeriesDate_ES);
        backfields.add(ESConstants.ManufacturerModelName_ES);
        backfields.add(ESConstants.NumberOfSlices_ES);
        backfields.add(ESConstants.TAG_ES);
        backfields.add(ESConstants.ID_ES);
//
        JSONArray sortfields = new JSONArray();
        sortfields.add(ESConstants.InstitutionName_ES);
        sortfields.add(ESConstants.SeriesDescription_ES);
        sortfields.add(ESConstants.PatientName_ES);
        sortfields.add(ESConstants.SeriesDate_ES);
        sortfields.add(ESConstants.TAG_ES);
        sortfields.add(ESConstants.NumberOfSlices_ES);

        JSONObject tempResult = searchService.searchDicomByPaging(param, backfields,sortfields,pageid, SysConstants.DEFAULT_PAGE_SIZE);
        System.out.println(tempResult.toJSONString());
        result.put("total",tempResult.getLong("total"));
        result.put("rows",tempResult.getJSONArray("data"));
        System.out.println("total:"+tempResult.getLong("total"));
        return result;
    }

    @RequestMapping(value="exportallpath")
    public void exportallpath(HttpServletRequest request, HttpServletResponse response) {
        String projectPath = request.getSession().getServletContext().getRealPath("/");
        String tempDir = projectPath+"temp"+MilkTool.getDelimiter();
        HttpSession session = request.getSession();
        JSONArray param = (JSONArray)session.getAttribute("searchParam");
        String tempFilePath = null;
        if(param!=null)
            tempFilePath = searchService.getDownloadFile(param, tempDir);
        MilkTool.doDownload(response,tempFilePath,"json");
    }


    //导出部分选中项路径
    @RequestMapping(value="exportdsomepath")
    public void exportdsomepath(HttpServletRequest request, HttpServletResponse response) {
        int times = 10;
        String projectPath = request.getSession().getServletContext().getRealPath("/");
        String tempDir = projectPath+"temp"+MilkTool.getDelimiter();
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
        MilkTool.doDownload(response,tempFilePath,"json");
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
        String projectPath = request.getSession().getServletContext().getRealPath("/");
        String tempDir = projectPath+"temp"+MilkTool.getDelimiter();
        HttpSession session = request.getSession();
        JSONArray param = (JSONArray)session.getAttribute("searchParam");
        String tempFilePath = null;
        if(param!=null)
            tempFilePath = searchService.exportExcel(param, tempDir);
        MilkTool.doDownload(response,tempFilePath,"excel");
    }

    @RequestMapping(value="downloaddicom")
    public void downloadDicom(HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("downloadDicom is called");
        int times = 10;
        String projectPath = request.getSession().getServletContext().getRealPath("/");
        String tempDir = projectPath+"temp";
        String name = "dicom"+MilkTool.getRandonNumber(3);
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

        MilkTool.zipCompress(zipSrcDir,tempDir,name+".zip");
        String zipFilePath = tempDir+File.separator+name+".zip";
        MilkTool.doDownload(response,zipFilePath,"zip");

        //删除临时目录，zip文件
        MilkTool.delFolder(zipSrcDir);
        MilkTool.delFile(zipFilePath);

    }

    @RequestMapping(value = "downloaddicomhelp", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject downloadDicomHelp(HttpServletRequest request, HttpServletResponse response,@RequestBody Map<String, Object> parameter) {
        System.out.println("downloadDicomHelp is called");
        List<String> ids = (List<String>)parameter.get("ids");
        for(String e : ids){
            System.out.println(e);
        }
        HttpSession session = request.getSession();
        session.setAttribute("idsForDicomFileDownload", ids);
        return new JSONObject();
    }

    //assSearchHospital 是个ajax请求方法，医院做成联想搜索
    @ResponseBody
    @RequestMapping(value = "associativeSearchHospital", method = RequestMethod.GET)
    public List<Map<String,String>> associativeSearchHospital() {
        System.out.println("assSearchHospital is called");
        List<Map<String,String>> list = new ArrayList<Map<String,String>>();
        Map<String,String> map;

        String[] organ = new String[]{"PLAGH","PUMCH"};
        for(String e : organ){
            map = new HashMap<String,String>();
            map.put("lable",e);
            map.put("text",e);
            list.add(map);
        }
        return list;
    }

    //assSearchHospital 是个ajax请求方法，医院做成联想搜索
    @ResponseBody
    @RequestMapping(value = "associativeSearchOrgan", method = RequestMethod.GET)
    public List<Map<String,String>> associativeSearchOrgan() {
        System.out.println("associativeSearchOrgan is called");
        List<Map<String,String>> list = new ArrayList<Map<String,String>>();
        Map<String,String> map;

        String[] organ = new String[]{"brain","lungs","heart"};
        for(String e : organ){
            map = new HashMap<String,String>();
            map.put("lable",e);
            map.put("text",e);
            list.add(map);
        }
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

	public JSONArray formatParameter(Map<String, String> parametr){
        JSONArray param = new JSONArray();

        Map<String,List<String>> tempMap = new HashMap<>();
        for(Map.Entry<String, String> entry : parametr.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if(StringUtils.isBlank(value)){
                continue;
            }
            List<String> tempList = null;
            String keyword;
            if(key.indexOf(SysConstants.UNDERLINE)>0){
                String[] split = key.split(SysConstants.UNDERLINE);
                keyword = split[0];
                String suffix = split[1];
                if(tempMap.containsKey(keyword)){
                    tempList = tempMap.get(keyword);
                    tempList.add(suffix+SysConstants.UNDERLINE+value);
                }else{
                    tempList = new ArrayList<>();
                    tempList.add(suffix+SysConstants.UNDERLINE+value);
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
            if(values.get(0).indexOf(SysConstants.UNDERLINE)>0){
                //为区间参数
                item.put("section","yes");
                item.put("keyword",key);
                for(String value : values){
                    if(value.startsWith("start")){
                        item.put("start",value.substring(value.indexOf(SysConstants.UNDERLINE)+1,value.length()));
                    }
                    if(value.startsWith("end")){
                        item.put("end",value.substring(value.indexOf(SysConstants.UNDERLINE)+1,value.length()));
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
        //projectPath是工程绝对路径 C://.../../es
        String projectRealPath = request.getSession().getServletContext().getRealPath(SysConstants.LEFT_SLASH);
        //工程下面temp目录绝对路径，用于存放临时文件，操作需要
        String tempRealPath = projectRealPath+SysConstants.TEMP_STRING;

        //contextPath是上下文相对路径，/es，获取这个是因为页面显示需要显示为/es/temp/2323/00001.jpg
        String contextPath = request.getContextPath();
        //上下文路径在后面新增一个临时目录/temp
        String tempContextPath = contextPath+SysConstants.LEFT_SLASH+SysConstants.TEMP_STRING;

        String id = parametr.get("id");
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

        System.out.println(result.toJSONString());
        return result;
    }

    private void printResult(JSONObject result){
        System.out.println("即将显示页面的数据："+result.toJSONString());
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
