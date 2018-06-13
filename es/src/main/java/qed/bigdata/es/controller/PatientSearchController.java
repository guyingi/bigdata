package qed.bigdata.es.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import qed.bigdata.es.consts.ESConstants;
import qed.bigdata.es.service.DataDownloadService;
import qed.bigdata.es.service.SearchPatientService;
import qed.bigdata.es.service.SearchService;
import qed.bigdata.es.consts.SysConstants;
import qed.bigdata.es.tool.MilkTool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package yasen.bigdata.es.controller
 * @Description: 这个controller主要负责患者关联查询的一些请求控制
 * @date 2018/5/30 11:32
 */
@Controller
@RequestMapping("")
public class PatientSearchController {

    @Autowired
    SearchPatientService searchPatientService;

    @Autowired
    SearchService searchService;

    @Autowired
    DataDownloadService dataDownloadService;

    /**
     * 接收患者姓名，根据姓名到mysql数据库中查询该患者信息，只是返回用户基本信息
     * @param request
     * @param response
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getpatient", method = RequestMethod.POST)
    public JSONObject getpatient(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, String> param) {
        System.out.println("getpatient is called");
        String patientname = param.get("patientname");
        System.out.println("getpatient:"+patientname);

        JSONObject patients = searchPatientService.getPatients(patientname);

        System.out.println(patients.toJSONString());
//        JSONObject json = new JSONObject();
//        json.put("total",1);
//        JSONArray arr = new JSONArray();
//        JSONObject p = new JSONObject();
//        p.put("patientname","小明");
//        p.put("age",12);
//        p.put("sex","男");
//        p.put("hospital","协和医院");
//        JSONObject p1 = new JSONObject();
//        p1.put("patientname","小明");
//        p1.put("age",12);
//        p1.put("sex","男");
//        p1.put("hospital","协和医院");
//        arr.add(p);
//        arr.add(p1);
//        json.put("rows",arr);

        return patients;
//        return json;
    }

    /**
     * 查询该患者有哪几种数据，dicom，edf,量表或者其他类型，实现方式为在各种es表中分别查询，如果返回值不为空则认为有该数据，但是灵活性差，如果有新类别的数据，需要修改代码
     * @param request
     * @param response
     * @param parametr
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getpatientdatatype", method = RequestMethod.POST)
    public JSONObject getpatientdatatype(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, String> parametr) {
        System.out.println("getpatient is called");
        JSONObject result = new JSONObject();
        String patientname = parametr.get("patientname");
        System.out.println("getpatientdatatype接收参数:"+patientname);

        List<String> datatypes = searchPatientService.getDataTypesOfPatient(patientname);
        result.put(SysConstants.TOTAL,datatypes.size());
        JSONArray arr = new JSONArray();
        for(String type : datatypes){
            JSONObject temp = new JSONObject();
            temp.put("patientname",patientname);
            temp.put("datatype",type);
            arr.add(temp);
        }
        result.put(SysConstants.ROWS,arr);

//        JSONObject json = new JSONObject();
//        json.put("total",3);
//        JSONArray arr = new JSONArray();
//        JSONObject dcm = new JSONObject();
//        dcm.put("patientname",patientname);
//        dcm.put("type","dicom");
//        JSONObject edf = new JSONObject();
//        edf.put("patientname",patientname);
//        edf.put("type","edf");
//        JSONObject guage = new JSONObject();
//        guage.put("patientname",patientname);
//        guage.put("type","guage");
//        arr.add(dcm);
//        arr.add(edf);
//        arr.add(guage);
//        json.put("rows",arr);
//        return json;
        System.out.println(result.toJSONString());
        return result;
    }

    /**
     * 这个方式是查询某个患者某个具体类型的所有数据
     * @param request
     * @param response
     * @param parametr
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getdetail", method = RequestMethod.POST)
    public JSONObject getdetail(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, String> parametr) {
        //TODO 应该做成分页的，因为可能有很多。暂时没有分页，过多则只显示默认的20条数据
        System.out.println("getdetail is called");
        JSONObject result = new JSONObject();
        String patientname = parametr.get("patientname");
        String datatype = parametr.get("datatype");
        System.out.println("getdetail接收参数:"+patientname+","+datatype);
        if(SysConstants.TYPE_DICOM.equals(datatype)){
            //查询序列描述，该序列下dicom数量。
            //TODO 记得改
//            JSONObject criteria = new JSONObject();
            JSONArray criteria = new JSONArray();
            JSONObject item = new JSONObject();
            item.put(SysConstants.SECTION,SysConstants.NO);
            item.put(SysConstants.KEYWORD,ESConstants.PatientName_ES);
            item.put(SysConstants.VALUE,patientname);
            criteria.add(item);

            JSONArray backfields = new JSONArray();
            backfields.add(ESConstants.ID_ES);
            backfields.add(ESConstants.SeriesDescription_ES);
            backfields.add(ESConstants.NumberOfSlices_ES);
            JSONObject jsonObject = searchService.searchDicomByPaging(criteria, backfields, null, 1, SysConstants.DEFAULT_PAGE_SIZE);
            if(SysConstants.CODE_000.equals(jsonObject.getString(SysConstants.CODE))) {
                result.put(SysConstants.TOTAL, jsonObject.getLong(SysConstants.TOTAL));
                JSONArray data = jsonObject.getJSONArray(SysConstants.DATA);
                JSONArray rows = new JSONArray();
                for (int i = 0; i < data.size(); i++) {
                    JSONObject obj = new JSONObject();
                    JSONObject tempItem = data.getJSONObject(i);
                    obj.put("id", tempItem.getString(ESConstants.ID_ES));
                    obj.put("patientname",patientname);
                    obj.put("datatype",datatype);
                    obj.put("describe", tempItem.getString(ESConstants.SeriesDescription_ES));
                    obj.put("count",tempItem.getString(ESConstants.NumberOfSlices_ES));
                    rows.add(obj);
                }
                result.put(SysConstants.ROWS, rows);
            }
        }else if(SysConstants.TYPE_ELECTRIC.equals(datatype)){
            JSONArray criteria = new JSONArray();
            JSONObject item = new JSONObject();
            item.put(SysConstants.SECTION,SysConstants.NO);
            item.put(SysConstants.KEYWORD,ESConstants.PatientName_ES);
            item.put(SysConstants.VALUE,patientname);
            criteria.add(item);

            JSONArray backfields = new JSONArray();
            backfields.add(ESConstants.ID_ES);
            backfields.add(ESConstants.HDFSPATH_ES_ELECTRIC);
            JSONObject jsonObject = searchService.searchElectricByPaging(criteria, backfields, null, 1, SysConstants.DEFAULT_PAGE_SIZE);
            if(SysConstants.CODE_000.equals(jsonObject.getString(SysConstants.CODE))) {
                result.put(SysConstants.TOTAL, jsonObject.getLong(SysConstants.TOTAL));
                JSONArray data = jsonObject.getJSONArray(SysConstants.DATA);
                JSONArray rows = new JSONArray();
                for (int i = 0; i < data.size(); i++) {
                    JSONObject obj = new JSONObject();
                    String hdfspath = data.getJSONObject(i).getString(ESConstants.HDFSPATH);
                    String a = data.getJSONObject(i).getString(ESConstants.ID_ES);
                    obj.put("id", data.getJSONObject(i).getString(ESConstants.ID_ES));
                    obj.put("datatype",datatype);
                    obj.put("patientname",patientname);
                    obj.put("describe",hdfspath.substring(hdfspath.lastIndexOf("/")+1,hdfspath.length()));
                    obj.put("count", 1);
                    rows.add(obj);
                }
                result.put(SysConstants.ROWS, rows);
            }
        }else if(SysConstants.TYPE_GUAGE.equals(datatype)){

        }else if(SysConstants.TYPE_KFB.equals(datatype)){

        }

//        JSONObject json = new JSONObject();
//        json.put("total",1);
//        JSONArray arr = new JSONArray();
//        JSONObject data = new JSONObject();
//        data.put("describe","aa");
//        data.put("organ","brain");
//        data.put("tag",type);
//        data.put("hospital","协和");
//        arr.add(data);
//        json.put("rows",arr);

//        return json;
        return result;
    }

    //根据患者名下载该患者多维度数据，dicom，量表，脑电等
    @ResponseBody
    @RequestMapping(value = "downloadbypatient", method = RequestMethod.POST)
    public void downloadbypatient(HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("downloadbypatient is called");
        String patientnameParam = request.getParameter("patientname");
        System.out.println("downloadbypatient接收参数:"+patientnameParam);

        //此处设计的是多个患者名字使用"-"连接然后直接拼接在url后面，关于url长度有限可以携带多少患者名字暂时没考虑，此处可能是个坑
        String patientname = patientnameParam.split("#")[0];

        //查询这个人有哪些类型的数据，
        List<String> datatypes = searchPatientService.getDataTypesOfPatient(patientname);

        //生成本地临时以该患者命名的目录，供以临时存放下载下来的各种类别的压缩文件。
        //projectPath是工程绝对路径 C:\Users\WeiGuangWu\IdeaProjects\bigdata\es\target\es\
        String projectRealPath = request.getSession().getServletContext().getRealPath(SysConstants.LEFT_SLASH);
        String tempPath = projectRealPath+SysConstants.TEMP_STRING;

        String zipFilePath = dataDownloadService.downloadMutilTypeDataForPatient(patientname, datatypes, tempPath);
        //输出压缩文件
        String filetype="zip";
        MilkTool.doDownload(response,zipFilePath,filetype);

        //删除压缩文件
        new File(zipFilePath).delete();

    }

    @ResponseBody
    @RequestMapping(value = "downloadbytype", method = RequestMethod.POST)
    public void downloadtype(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String patientname = request.getParameter("patientname");
        String typeParam = request.getParameter("datatype");
        System.out.println(patientname+"\t"+typeParam);
        //一次下载这几种类型的数据，下载完打包给出前端
        //查询这个人有哪些类型的数据，
        String[] types = typeParam.split("#");
        List<String> datatypes = new ArrayList<String>();
        for(String type : types){
            datatypes.add(type);
        }

        //生成本地临时以该患者命名的目录，供以临时存放下载下来的各种类别的压缩文件。
        //projectPath是工程绝对路径 C:\Users\WeiGuangWu\IdeaProjects\bigdata\es\target\es\
        String projectRealPath = request.getSession().getServletContext().getRealPath(SysConstants.LEFT_SLASH);
        String tempPath = projectRealPath+SysConstants.TEMP_STRING;

        String zipFilePath = dataDownloadService.downloadMutilTypeDataForPatient(patientname, datatypes, tempPath);

        //输出压缩文件
        String filetype="zip";
        MilkTool.doDownload(response,zipFilePath,filetype);

        //删除压缩文件
        new File(zipFilePath).delete();
    }

    @ResponseBody
    @RequestMapping(value = "downloaddetail", method = RequestMethod.POST)
    public void downloaddetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //得到患者姓名，数据类别，以及该类别的数据id.
        String patientname = request.getParameter("patientname");
        String datatype = request.getParameter("datatype");
        String idsStr = request.getParameter("ids");
        System.out.println("patientname:"+patientname+"\t datatype:"+datatype+"\t idsStr:"+idsStr);
        List<String> ids = new ArrayList<>();
        for(String id : idsStr.split("#")){
            ids.add(id);
        }

        //projectRealPath路径末尾带斜线
        String projectRealPath = request.getSession().getServletContext().getRealPath(SysConstants.LEFT_SLASH);
        String tempPath = projectRealPath+SysConstants.TEMP_STRING;

        //存放具体数据的目录，将会压缩该目录给前端
        String zipSrcDir = null;
        String zipName = null;
        //依次下载每种类型的数据，下载完打包，给出到前端
        if(SysConstants.TYPE_DICOM.equals(datatype)){
            //下载该患者所有的dicom文件到本地的一个以该患者名字命名的文件夹里面。
            //根据ids查询hdfspath,并且下载
            String dicomPath = tempPath+File.separator+"dicom_"+MilkTool.getRandonNumber(3);
            zipSrcDir = dataDownloadService.downloadDicomByIds(ids, dicomPath);
            zipName = patientname+"_dicom"+MilkTool.getRandonNumber(3)+".zip";
        }else if(SysConstants.TYPE_ELECTRIC.equals(datatype)){
            //下载该患者所有电信号数据到本地的一个以该患者名在命名的文件夹里面。
            //根据ids查询hdfspath,并且下载格式为/temp/patient
            String electricPath = tempPath+File.separator+"electric_"+MilkTool.getRandonNumber(3);
            zipSrcDir = dataDownloadService.downloadElectricByIds(ids,electricPath);
            zipName = patientname+"_electric"+MilkTool.getRandonNumber(3)+".zip";
        }else if(SysConstants.TYPE_KFB.equals(datatype)){

        }else if(SysConstants.TYPE_GUAGE.equals(datatype)){

        }else{

        }

        String zipFilePath = null;
        if(!StringUtils.isBlank(zipSrcDir)) {
            if (MilkTool.zipCompress(zipSrcDir, tempPath,zipName)) {
                zipFilePath = tempPath+File.separator+zipName;
            }
        }
        //输出压缩文件
        String filetype="zip";
        MilkTool.doDownload(response,zipFilePath,filetype);

        //删除压缩文件
        MilkTool.delFolder(zipSrcDir);
        new File(zipFilePath).delete();

    }

}
