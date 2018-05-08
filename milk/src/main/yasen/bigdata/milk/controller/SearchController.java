package yasen.bigdata.milk.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;

import yasen.bigdata.milk.conf.SysConstants;
import yasen.bigdata.milk.pojo.Dicom;
import yasen.bigdata.milk.service.SearchService;
import yasen.bigdata.milk.tool.MilkTool;

@Controller
@RequestMapping("")
public class SearchController {

	@Autowired
	SearchService searchService;

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
        JSONObject param = formatParameter(parametr);
        HttpSession session = request.getSession();
        session.setAttribute("searchParam", param);

        JSONObject tempResult = searchService.searchByPaging(param, 1, SysConstants.DEFAULT_PAGE_SIZE);
        result.put("total",tempResult.getLong("total"));
        result.put("rows",tempResult.getJSONArray("data"));
        System.out.println("total:"+tempResult.getLong("total"));
        return result;
    }

    @RequestMapping(value = "ajaxPage", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject ajaxPage(HttpServletRequest request, HttpServletResponse response,
                               @RequestParam(value="page", required=false) String page,
                               @RequestParam(value="rows", required=false) String rows) {
        System.out.println("ajaxPage");

        JSONObject result = new JSONObject();
        int pageid = Integer.parseInt(page);
        HttpSession session = request.getSession();
        JSONObject param = (JSONObject)session.getAttribute("searchParam");
        JSONObject tempResult = searchService.searchByPaging(param, pageid, SysConstants.DEFAULT_PAGE_SIZE);
        result.put("total",tempResult.getLong("total"));
        result.put("rows",tempResult.getJSONArray("data"));
        System.out.println("total:"+tempResult.getLong("total"));
        return result;
    }

//    @RequestMapping(value = "ajaxSearch", method = RequestMethod.POST)
//    @ResponseBody
//    public JSONObject ajaxSearch(HttpServletRequest request, HttpServletResponse response,@RequestBody Map<String, String> parametr) {
//        System.out.println("ajaxSearch is called");
//
//	    JSONObject param = formatParameter(parametr);
//        HttpSession session = request.getSession();
//        session.setAttribute("searchParam", param);
//        JSONObject result = searchService.searchByPaging(param, 1, SysConstants.DEFAULT_PAGE_SIZE);
//        result.put(SysConstants.PAGEID_PAGEPARAMNAME,1);
//        return result;
//    }

    //ajax search,能正常使用
//    @RequestMapping(value = "ajaxPage", method = RequestMethod.POST)
//    @ResponseBody
//    public JSONObject ajaxPage(HttpServletRequest request, HttpServletResponse response,@RequestBody Map<String, Integer> parameter) {
//        System.out.println("ajaxPage is called");
//	    HttpSession session = request.getSession();
//        JSONObject param = (JSONObject)session.getAttribute("searchParam");
//
//        Integer pageid = 0;
//        if(parameter!=null && parameter.size()==1){
//            pageid = parameter.get("pageid");
//            pageid = pageid<1?1:pageid;
//        }
//        System.out.println("ajaxPage从sesssion中获取的参数："+param.toJSONString());
//
//        JSONObject result = searchService.searchByPaging(param, pageid, SysConstants.DEFAULT_PAGE_SIZE);
//        result.put(SysConstants.PAGEID_PAGEPARAMNAME,pageid);
//        printResult(result);
//        return result;
//    }

    @RequestMapping(value="exportallpath")
    public void exportallpath(HttpServletRequest request, HttpServletResponse response) {
        String projectPath = request.getSession().getServletContext().getRealPath("/");
        String tempDir = projectPath+"temp"+MilkTool.getDelimiter();
        HttpSession session = request.getSession();
        JSONObject param = (JSONObject)session.getAttribute("searchParam");
        String tempFilePath = null;
        if(param!=null)
            tempFilePath = searchService.getDownloadFile(param, tempDir);
        doDownload(response,tempFilePath,"json");
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
        if(ids!=null)
            tempFilePath = searchService.getDownloadFileByIds(ids,tempDir);
        doDownload(response,tempFilePath,"json");
    }

    /**
     * 在下载部分选中项的json文件时，需要传选中项的id值过来，但是ajax方式不能下载，因为
     * javascript不能直接操作磁盘，传给页面的数据存放JavaScript的内存中。所以我采用了使用ajax提交id参数给exportSomeHelp，
     * 在exportSomeHelp中将参数解析出来存储到Session中，页面同时请求另一个地址/milk/downloadsome,这个直接访问，可以触发页面
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
        JSONObject param = (JSONObject)session.getAttribute("searchParam");
        String tempFilePath = null;
        if(param!=null)
            tempFilePath = searchService.exportExcel(param, tempDir);
        doDownload(response,tempFilePath,"excel");
    }

    @RequestMapping(value="downloaddicom")
    public void downloadDicom(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("downloadDicom is called");
        int times = 10;
        String projectPath = request.getSession().getServletContext().getRealPath("/");
        String tempDir = projectPath+"temp"+MilkTool.getDelimiter();
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
        String tempFilePath = null;
        if(ids!=null && ids.size()!=0) {
            tempFilePath = searchService.getDicomZipByIds(ids, tempDir);
        }
        doDownload(response,tempFilePath,"zip");
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

    //做具体的下载操作，将文件写出给前端
    private void doDownload(HttpServletResponse response,String tempFilePath,String filetype){
        if(tempFilePath!=null) {
			System.out.println("临时文件目录："+tempFilePath);
			response.setCharacterEncoding("utf-8");
			if(filetype.equals("json"))
		        response.setContentType("multipart/form-data");
			else if(filetype.equals("xls"))
                response.setContentType("application/msexcel");
			else if(filetype.equals("zip"))
                response.setContentType("application/zip");
			else
			    ;
            System.out.println("A");
		    response.setHeader("Content-Disposition", "attachment;fileName="
                    + tempFilePath.substring(tempFilePath.lastIndexOf(MilkTool.getDelimiter())+1, tempFilePath.length()));
		    long downloadedLength = 0l;
		    long available = 0l;
		    try {
		        //打开本地文件流
		        InputStream inputStream = new FileInputStream(tempFilePath);
		        available = inputStream.available();
		        OutputStream os = response.getOutputStream();
		        byte[] b = new byte[2048];
		        int length;
		        while ((length = inputStream.read(b)) > 0) {
		            os.write(b, 0, length);
		            downloadedLength += b.length;
		        }
		        os.close();
		        inputStream.close();
                System.out.println("B");
		    } catch (Exception e){
		    	e.printStackTrace();
		    }
            System.out.println("C");
		}else {
            System.out.println("D");
            response.setCharacterEncoding("utf-8");
			response.setContentType("multipart/form-data");
            response.setHeader("Content-Disposition", "attachment;fileName=download failed");

            try {
                OutputStream os = response.getOutputStream();
                os.write((new String("下载失败")).getBytes("utf-8"));
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

	public JSONObject formatParameter(Map<String, String> parametr){
	    JSONObject param = new JSONObject();
        for(Map.Entry<String, String> entry : parametr.entrySet()) {
            if(entry.getValue().length()!=0) {
                if(entry.getKey().equals(SysConstants.DEVICE_PAGEPARAMNAME)){
                    param.put(SysConstants.DEVICE_PARAM, entry.getValue());
                }else if(entry.getKey().equals(SysConstants.ORGAN_PAGEPARAMNAME)){
                    param.put(SysConstants.ORGAN_PARAM, entry.getValue());
                }else if(entry.getKey().equals(SysConstants.SERIES_DESCRIPTION_PAGEPARAMNAME)){
                    param.put(SysConstants.SERIES_DESCRIPTION_PARAM, entry.getValue());
                }else if(entry.getKey().equals(SysConstants.INSTITUTION_PAGEPARAMNAME)){
                    param.put(SysConstants.INSTITUTION_PARAM, entry.getValue());
                }else if(entry.getKey().equals(SysConstants.SES_PAGEPARAMNAME)){
                    param.put(SysConstants.SEX_PARAM, entry.getValue());
                }else if(entry.getKey().equals(SysConstants.AGE_START_PAGEPARAMNAME)){
                    param.put(SysConstants.AGE_START_PARAM, entry.getValue());
                }else if(entry.getKey().equals(SysConstants.AGE_END_PAGEPARAMNAME)){
                    param.put(SysConstants.AGE_END_PARAM, entry.getValue());
                }else if(entry.getKey().equals(SysConstants.STUDYDATE_START_PAGEPARAMNAME)){
                    param.put(SysConstants.STUDYDATE_START_PARAM, MilkTool.formatData(entry.getValue()));
                }else if(entry.getKey().equals(SysConstants.STUDYDATE_END_PAGEPARAMNAME)){
                    param.put(SysConstants.STUDYDATE_END_PARAM, MilkTool.formatData(entry.getValue()));
                }else if(entry.getKey().equals(SysConstants.ENTRYDATE_START_PAGEPARAMNAME)){
                    param.put(SysConstants.ENTRYDATE_START_PARAM, MilkTool.formatData(entry.getValue()));
                }else if(entry.getKey().equals(SysConstants.ENTRYDATE_END_PAGEPARAMNAME)){
                    param.put(SysConstants.ENTRYDATE_END_PARAM, MilkTool.formatData(entry.getValue()));
                }else if(entry.getKey().equals(SysConstants.IMAGECOUNT_MIN_PAGEPARAMNAME)) {
                    param.put(SysConstants.IMAGECOUNT_MIN_PARAM, entry.getValue());
                }else if(entry.getKey().equals(SysConstants.IMAGECOUNT_MAX_PAGEPARAMNAME)){
                    param.put(SysConstants.IMAGECOUNT_MAX_PARAM, entry.getValue());
                }
            }
        }
        return param;
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
