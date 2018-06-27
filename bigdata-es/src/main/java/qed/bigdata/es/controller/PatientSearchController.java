package qed.bigdata.es.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import qed.bigdata.es.consts.ESConsts;
import qed.bigdata.es.consts.SysConsts;
import qed.bigdata.es.service.DataDownloadService;
import qed.bigdata.es.service.SearchPatientService;
import qed.bigdata.es.service.SearchService;
import qed.bigdata.es.tool.Tool;

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
    static Logger logger = Logger.getLogger(PatientSearchController.class);

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
     * @param parameter
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getpatient", method = RequestMethod.POST)
    public JSONObject getpatient(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, String> parameter) {
        logger.log(Level.INFO,"controller:getpatient 被调用");
        JSONObject paramJson = JSONObject.parseObject(JSON.toJSONString(parameter));
        logger.log(Level.INFO,"接收的参数:"+paramJson.toJSONString());

        String patientname = parameter.get("patientname");

        JSONObject patients = searchPatientService.getPatients(patientname);

        logger.log(Level.INFO,"返与前端结果:"+patients.toJSONString());

        return patients;
    }

    /**
     * 查询该患者有哪几种数据，dicom，edf,量表或者其他类型，实现方式为在各种es表中分别查询，如果返回值不为空则认为有该数据，但是灵活性差，如果有新类别的数据，需要修改代码
     * @param request
     * @param response
     * @param parameter
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getpatientdatatype", method = RequestMethod.POST)
    public JSONObject getpatientdatatype(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, String> parameter) {
        logger.log(Level.INFO,"controller:getpatientdatatype 被调用");
        JSONObject paramJson = JSONObject.parseObject(JSON.toJSONString(parameter));
        logger.log(Level.INFO,"接收的参数:"+paramJson.toJSONString());

        JSONObject result = new JSONObject();
        String patientname = parameter.get("patientname");

        List<String> datatypes = searchPatientService.getDataTypesOfPatient(patientname);
        result.put(SysConsts.TOTAL,datatypes.size());
        JSONArray arr = new JSONArray();
        for(String type : datatypes){
            JSONObject temp = new JSONObject();
            temp.put("patientname",patientname);
            temp.put("datatype",type);
            arr.add(temp);
        }
        result.put(SysConsts.ROWS,arr);

        logger.log(Level.DEBUG,"返与前端结果:"+result.toJSONString());
        return result;
    }

    /**
     * 这个方式是查询某个患者某个具体类型的所有数据
     * @param request
     * @param response
     * @param parameter
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getdetail", method = RequestMethod.POST)
    public JSONObject getdetail(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, String> parameter) {
        //TODO 应该做成分页的，因为可能有很多。暂时没有分页，过多则只显示默认的20条数据
        logger.log(Level.INFO,"controller:getdetail 被调用");
        JSONObject paramJson = JSONObject.parseObject(JSON.toJSONString(parameter));
        logger.log(Level.INFO,"接收的参数:"+paramJson.toJSONString());

        JSONObject result = new JSONObject();
        String patientname = parameter.get("patientname");
        String datatype = parameter.get("datatype");
        if(SysConsts.TYPE_DICOM.equals(datatype)){
            //查询序列描述，该序列下dicom数量。
            JSONArray criteria = new JSONArray();
            JSONObject item = new JSONObject();
            item.put(SysConsts.SECTION,SysConsts.NO);
            item.put(SysConsts.KEYWORD,ESConsts.PatientName_ES);
            item.put(SysConsts.VALUE,patientname);
            criteria.add(item);

            JSONArray backfields = new JSONArray();
            backfields.add(ESConsts.ID_ES);
            backfields.add(ESConsts.SeriesDescription_ES);
            backfields.add(ESConsts.NumberOfSlices_ES);
            JSONObject jsonObject = searchService.searchDicomByPaging(criteria, backfields, null, 1, SysConsts.DEFAULT_PAGE_SIZE);
            if(SysConsts.CODE_000.equals(jsonObject.getString(SysConsts.CODE))) {
                result.put(SysConsts.TOTAL, jsonObject.getLong(SysConsts.TOTAL));
                JSONArray data = jsonObject.getJSONArray(SysConsts.DATA);
                JSONArray rows = new JSONArray();
                for (int i = 0; i < data.size(); i++) {
                    JSONObject obj = new JSONObject();
                    JSONObject tempItem = data.getJSONObject(i);
                    obj.put("id", tempItem.getString(ESConsts.ID_ES));
                    obj.put("patientname",patientname);
                    obj.put("datatype",datatype);
                    obj.put("describe", tempItem.getString(ESConsts.SeriesDescription_ES));
                    obj.put("count",tempItem.getString(ESConsts.NumberOfSlices_ES));
                    rows.add(obj);
                }
                result.put(SysConsts.ROWS, rows);
            }
        }else if(SysConsts.TYPE_ELECTRIC.equals(datatype)){
            JSONArray criteria = new JSONArray();
            JSONObject item = new JSONObject();
            item.put(SysConsts.SECTION,SysConsts.NO);
            item.put(SysConsts.KEYWORD,ESConsts.PatientName_ES);
            item.put(SysConsts.VALUE,patientname);
            criteria.add(item);

            JSONArray backfields = new JSONArray();
            backfields.add(ESConsts.ID_ES);
            backfields.add(ESConsts.HDFSPATH_ES_ELECTRIC);
            JSONObject jsonObject = searchService.searchElectricByPaging(criteria, backfields, null, 1, SysConsts.DEFAULT_PAGE_SIZE);
            if(SysConsts.CODE_000.equals(jsonObject.getString(SysConsts.CODE))) {
                result.put(SysConsts.TOTAL, jsonObject.getLong(SysConsts.TOTAL));
                JSONArray data = jsonObject.getJSONArray(SysConsts.DATA);
                JSONArray rows = new JSONArray();
                for (int i = 0; i < data.size(); i++) {
                    JSONObject obj = new JSONObject();
                    String hdfspath = data.getJSONObject(i).getString(ESConsts.HDFSPATH);
                    String a = data.getJSONObject(i).getString(ESConsts.ID_ES);
                    obj.put("id", data.getJSONObject(i).getString(ESConsts.ID_ES));
                    obj.put("datatype",datatype);
                    obj.put("patientname",patientname);
                    obj.put("describe",hdfspath.substring(hdfspath.lastIndexOf("/")+1,hdfspath.length()));
                    obj.put("count", 1);
                    rows.add(obj);
                }
                result.put(SysConsts.ROWS, rows);
            }
        }else if(SysConsts.TYPE_GUAGE.equals(datatype)){

        }else if(SysConsts.TYPE_KFB.equals(datatype)){

        }
        logger.log(Level.DEBUG,"返与前端结果:"+result.toJSONString());
        return result;
    }

    //根据患者名下载该患者多维度数据，dicom，量表，脑电等
    @ResponseBody
    @RequestMapping(value = "downloadbypatient", method = RequestMethod.POST)
    public void downloadbypatient(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.log(Level.INFO,"controller:downloadbypatient 被调用");

        String patientnameParam = request.getParameter("patientname");
        logger.log(Level.INFO,"接收的参数:"+patientnameParam);

        //此处设计的是多个患者名字使用"-"连接然后直接拼接在url后面，关于url长度有限可以携带多少患者名字暂时没考虑，此处可能是个坑
        String patientname = patientnameParam.split("\\*")[0];

        //查询这个人有哪些类型的数据，
        List<String> datatypes = searchPatientService.getDataTypesOfPatient(patientname);

        //生成本地临时以该患者命名的目录，供以临时存放下载下来的各种类别的压缩文件。
        //projectPath是工程绝对路径 C:\Users\WeiGuangWu\IdeaProjects\bigdata\es\target\es\
        String projectRealPath = request.getSession().getServletContext().getRealPath(SysConsts.LEFT_SLASH);
        String tempPath = projectRealPath+SysConsts.TEMP_STRING;

        logger.log(Level.INFO,"临时目录:"+tempPath);

        String zipFilePath = dataDownloadService.downloadMutilTypeDataForPatient(patientname, datatypes, tempPath);

        logger.log(Level.INFO,"zip文件目录:"+zipFilePath);

        doDownload(zipFilePath,response);

        logger.log(Level.INFO,"控制流程结束:downloadbypatient");
    }

    @ResponseBody
    @RequestMapping(value = "downloadbytype", method = RequestMethod.POST)
    public void downloadtype(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.log(Level.INFO,"controller:downloadbytype 被调用");

        String patientname = request.getParameter("patientname");
        String typeParam = request.getParameter("datatype");

        logger.log(Level.INFO,"接收的参数:" + patientname+"\t"+typeParam);

        //一次下载这几种类型的数据，下载完打包给出前端
        //查询这个人有哪些类型的数据，
        String[] types = typeParam.split("\\*");
        List<String> datatypes = new ArrayList<String>();
        for(String type : types){
            datatypes.add(type);
        }

        //生成本地临时以该患者命名的目录，供以临时存放下载下来的各种类别的压缩文件。
        //projectPath是工程绝对路径 C:\Users\WeiGuangWu\IdeaProjects\bigdata\es\target\es\
        String projectRealPath = request.getSession().getServletContext().getRealPath(SysConsts.LEFT_SLASH);
        String tempPath = projectRealPath+SysConsts.TEMP_STRING;

        String zipFilePath = dataDownloadService.downloadMutilTypeDataForPatient(patientname, datatypes, tempPath);

        logger.log(Level.INFO,"zip文件目录:"+zipFilePath);

        doDownload(zipFilePath,response);

        logger.log(Level.INFO,"控制流程结束:downloadbypatient");
    }

    @ResponseBody
    @RequestMapping(value = "downloaddetail", method = RequestMethod.POST)
    public void downloaddetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.log(Level.INFO,"controller:downloaddetail 被调用");

        //得到患者姓名，数据类别，以及该类别的数据id.
        String patientname = request.getParameter("patientname");
        String datatype = request.getParameter("datatype");
        String idsStr = request.getParameter("ids");

        logger.log(Level.INFO,"接收的参数:" +"patientname:"+patientname+"\t datatype:"+datatype+"\t idsStr:"+idsStr);

        List<String> ids = new ArrayList<>();
        for(String id : idsStr.split("\\*")){
            ids.add(id);
        }

        //projectRealPath路径末尾带斜线
        String projectRealPath = request.getSession().getServletContext().getRealPath(SysConsts.LEFT_SLASH);
        String tempPath = projectRealPath+SysConsts.TEMP_STRING;

        //存放具体数据的目录，将会压缩该目录给前端
        String zipSrcDir = null;
        String zipName = null;
        //依次下载每种类型的数据，下载完打包，给出到前端
        if(SysConsts.TYPE_DICOM.equals(datatype)){
            //下载该患者所有的dicom文件到本地的一个以该患者名字命名的文件夹里面。
            //根据ids查询hdfspath,并且下载
            String dicomPath = tempPath+File.separator+"dicom_"+Tool.getRandonNumber(3);
            zipSrcDir = dataDownloadService.downloadDicomByIds(ids, dicomPath);
            zipName = patientname+"_dicom"+Tool.getRandonNumber(3)+".zip";
        }else if(SysConsts.TYPE_ELECTRIC.equals(datatype)){
            //下载该患者所有电信号数据到本地的一个以该患者名在命名的文件夹里面。
            //根据ids查询hdfspath,并且下载格式为/temp/patient
            String electricPath = tempPath+File.separator+"electric_"+Tool.getRandonNumber(3);
            zipSrcDir = dataDownloadService.downloadElectricByIds(ids,electricPath);
            zipName = patientname+"_electric"+Tool.getRandonNumber(3)+".zip";
        }else if(SysConsts.TYPE_KFB.equals(datatype)){

        }else if(SysConsts.TYPE_GUAGE.equals(datatype)){

        }else{

        }

        String zipFilePath = null;
        if(!StringUtils.isBlank(zipSrcDir)) {
            if (Tool.zipCompress(zipSrcDir, tempPath,zipName)) {
                zipFilePath = tempPath+File.separator+zipName;
            }
        }

        logger.log(Level.INFO,"zip文件目录:"+zipFilePath);

        doDownload(zipFilePath,response);

        logger.log(Level.INFO,"控制流程结束:downloaddetail");
    }

    public void doDownload(String zipFilePath,HttpServletResponse response){
        if(!StringUtils.isBlank(zipFilePath)){
            //输出压缩文件
            String filetype="zip";
            Tool.doDownload(response,zipFilePath,filetype);
            logger.log(Level.INFO,"输出压缩文件至前端完成:"+zipFilePath);
            //删除压缩文件
            new File(zipFilePath).delete();
            logger.log(Level.INFO,"删除压缩文件完成:"+zipFilePath);
        }else{
            logger.log(Level.INFO,"生成压缩文件失败，目录:"+zipFilePath);
        }
    }
}
