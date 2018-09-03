package qed.bigdata.es.service.impl;

/**
 * @Title: SearchServiceImpl.java
 * @Package yasen.bigdata.es.service.impl
 * @Description: SearchService实现类
 * @author weiguangwu
 * @date  2018/4/23 14:13
 * @version V1.0
 */

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import qed.bigdata.es.consts.ESConsts;
import qed.bigdata.es.consts.DataTypeEnum;
import qed.bigdata.es.conf.MilkConfiguration;
import qed.bigdata.es.consts.SysConsts;
import qed.bigdata.es.service.SearchService;
import qed.bigdata.es.tool.HdfsTool;
import qed.bigdata.es.tool.Tool;

@Service
public class SearchServiceImpl implements SearchService {
    static Logger logger = Logger.getLogger(SearchServiceImpl.class);
	public static void main(String[] args) {
//		JSONObject json = new JSONObject();
//		JSONArray criteria = new JSONArray();
//		JSONObject obj1 = new JSONObject();
//        obj1.put("keyword","InstitutionName");
//        obj1.put("value","Ningbo No.2 Hosp");
//        obj1.put("section","no");
//
//        JSONObject obj2 = new JSONObject();
//        obj2.put("keyword","PatientAge");
//        obj2.put("start","0");
//        obj2.put("end","23");
//        obj2.put("section","yes");
//        criteria.add(obj1);
//        criteria.add(obj2);
//		searchcondition.put(SysConsts.DEVICE_PARAM,"PET/MR");
//        searchcondition.put(SysConsts.SERIES_DESCRIPTION_PARAM,"Flair");
//        searchcondition.put(SysConsts.ORGAN_PARAM,"brain");
//        searchcondition.put(SysConsts.INSTITUTION_PARAM,"PUMCH-GX");
//        searchcondition.put(SysConsts.SEX_PARAM,"M");
//        searchcondition.put(SysConsts.AGE_START_PARAM,"85");
//        searchcondition.put(SysConsts.AGE_END_PARAM,86);
//        searchcondition.put(SysConsts.STUDYDATE_START_PARAM,"2018-01-01");
//        searchcondition.put(SysConsts.STUDYDATE_END_PARAM,"2018-01-26");
//        searchcondition.put(SysConsts.ENTRYDATE_START_PARAM,"2018-04-20");
//        searchcondition.put(SysConsts.ENTRYDATE_END_PARAM,"2018-04-20");
//        searchcondition.put(SysConsts.IMAGECOUNT_MIN_PARAM,121);
//        searchcondition.put(SysConsts.IMAGECOUNT_MAX_PARAM,121);
//		json.put("searchcondition",searchcondition);

//		JSONArray backfields = new JSONArray();
//        backfields.add(ESConsts.InstitutionName_ES);
//        backfields.add(ESConsts.ORGAN_ES);
//		backfields.add(ESConsts.PatientName_ES);
//		backfields.add(ESConsts.PatientsAge_ES);
//		backfields.add(ESConsts.PatientsAge_ES);
//        backfields.add(ESConsts.SeriesDescription_ES);
//        backfields.add(ESConsts.SeriesDate_ES);
//        backfields.add(ESConsts.NumberOfSlices_ES);
//        backfields.add(ESConsts.ID_ES);

//
//		JSONArray sortfields = new JSONArray();
//		sortfields.add(ESConsts.SeriesDate_ES);
//		sortfields.add(ESConsts.PatientName_ES);
//        json.put("pageid",1);
//        json.put("pagesize",3);
//        json.put("backfields",backfields);
//		json.put("sortfields",sortfields);
//        json.put("criteria",criteria);
//        json.put("datatype",SysConsts.TYPE_DICOM);
//        String interfaceStr = "/info/searchpaging";
//        JSONObject searchPagingResult = null;
//        try {
//            searchPagingResult = Tool.doCallAndGetResult(json,interfaceStr,DataTypeEnum.DICOM);
//            System.out.println(searchPagingResult.toJSONString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//		JSONObject jsonObject = new SearchServiceImpl().searchDicomByPaging(searchcondition,backfields,sortfields,1,3);
//		System.out.println(jsonObject.toJSONString());
//		String code = jsonObject.getString("code");
//		System.out.println("返回码："+code);
        /***构建kfb数据查询测试*/
        JSONObject json = new JSONObject();
        JSONArray backfields = new JSONArray();
        backfields.add("PatientUID");
        backfields.add("InstitutionName");
        backfields.add("PatientName");
        backfields.add("entrydate");
        backfields.add("hdfspath");
        json.put(SysConsts.BACKFIELDS,backfields);

        json.put(SysConsts.DATATYPE,SysConsts.TYPE_KFB);
        String interfaceStr = "/info/searchpaging";
        DataTypeEnum dataTypeEnum = DataTypeEnum.OTHER;
        JSONObject searchPagingResult = null;
        try {
            searchPagingResult = Tool.doCallAndGetResult(json,interfaceStr,dataTypeEnum);
            System.out.println(searchPagingResult.toJSONString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

	@Override
	public JSONObject searchDicomByPaging(JSONArray criteria,JSONArray backfields,JSONArray sortfields,Integer pageid,Integer pagesize) {
	    String criteriaLog = criteria!=null?criteria.toJSONString():"";
	    String backfieldsLog = backfields!=null?backfields.toJSONString():"";
	    String sortfieldsLog = sortfields!=null?sortfields.toJSONString():"";
        logger.log(Level.INFO,"调用方法:searchDicomByPaging,参数{criteria:"+criteriaLog
                        +"backfields:"+backfieldsLog
                        +"sortfields:"+sortfieldsLog
                        +"pageid:"+pageid
                        +"pagesize:"+pagesize);

		JSONObject json = new JSONObject();
		if (criteria == null || criteria.size() == 0){
		    return new JSONObject();
        } else {
            json.put(SysConsts.CRITERIA,criteria);
        }
        if(backfields != null && backfields.size() > 0) {
            json.put(SysConsts.BACKFIELDS, backfields);
        }
        if(sortfields != null && sortfields.size() > 0) {
            json.put(SysConsts.SORTFIELDS,sortfields);
		}
        if(pageid != null) {
            json.put(SysConsts.PAGE_ID, pageid);
        }
		if(pagesize != null) {
            json.put(SysConsts.PAGE_SIZE, pagesize);
        }
        json.put(SysConsts.DATATYPE,SysConsts.TYPE_DICOM);
		String interfaceStr = "/info/searchpaging";
        DataTypeEnum dataTypeEnum = DataTypeEnum.OTHER;
        JSONObject searchPagingResult = null;
        try {
            searchPagingResult = Tool.doCallAndGetResult(json,interfaceStr,dataTypeEnum);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.log(Level.INFO,"调用 searchDicomByPaging 结束,返回结果:"+searchPagingResult.toJSONString());
        return searchPagingResult;
	}

    @Override
    public JSONObject searchElectricByPaging(JSONArray criteria, JSONArray backfields, JSONArray sortfields, Integer pageid, Integer pagesize) {
        String criteriaLog = criteria!=null?criteria.toJSONString():"";
        String backfieldsLog = backfields!=null?backfields.toJSONString():"";
        String sortfieldsLog = sortfields!=null?sortfields.toJSONString():"";
        logger.log(Level.INFO,"调用方法:searchElectricByPaging,参数{criteria:"+criteriaLog
                +"backfields:"+backfieldsLog
                +"sortfields:"+sortfieldsLog
                +"pageid:"+pageid
                +"pagesize:"+pagesize);

        JSONObject json = new JSONObject();
        if (criteria != null && criteria.size() != 0){
            json.put(SysConsts.CRITERIA,criteria);
        }
        if(backfields != null && backfields.size() > 0) {
            json.put(SysConsts.BACKFIELDS, backfields);
        }
        if(sortfields != null && sortfields.size() > 0) {
            json.put(SysConsts.SORTFIELDS,sortfields);
        }
        if(pageid != null) {
            json.put(SysConsts.PAGE_ID, pageid);
        }
        if(pagesize != null) {
            json.put(SysConsts.PAGE_SIZE, pagesize);
        }
        json.put(SysConsts.DATATYPE,SysConsts.TYPE_ELECTRIC);
        String interfaceStr = "/info/searchpaging";
        DataTypeEnum dataTypeEnum = DataTypeEnum.ELECTRIC;
        JSONObject searchPagingResult = null;
        try {
            searchPagingResult = Tool.doCallAndGetResult(json,interfaceStr,dataTypeEnum);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.log(Level.INFO,"调用 searchElectricByPaging 结束,返回结果:"+searchPagingResult.toJSONString());
        return searchPagingResult;
    }

    @Override
    public JSONObject searchKfbcByPaging(JSONArray criteria, JSONArray backfields, JSONArray sortfields, Integer pageid, Integer pagesize) {
        String criteriaLog = criteria!=null?criteria.toJSONString():"";
        String backfieldsLog = backfields!=null?backfields.toJSONString():"";
        String sortfieldsLog = sortfields!=null?sortfields.toJSONString():"";
        logger.log(Level.INFO,"调用方法:searchKfbcByPaging,参数{criteria:"+criteriaLog
                +"backfields:"+backfieldsLog
                +"sortfields:"+sortfieldsLog
                +"pageid:"+pageid
                +"pagesize:"+pagesize);

        JSONObject json = new JSONObject();
        if (criteria != null && criteria.size() != 0){
            json.put(SysConsts.CRITERIA,criteria);
        }
        if(backfields != null && backfields.size() > 0) {
            json.put(SysConsts.BACKFIELDS, backfields);
        }
        if(sortfields != null && sortfields.size() > 0) {
            json.put(SysConsts.SORTFIELDS,sortfields);
        }
        if(pageid != null) {
            json.put(SysConsts.PAGE_ID, pageid);
        }
        if(pagesize != null) {
            json.put(SysConsts.PAGE_SIZE, pagesize);
        }
        json.put(SysConsts.DATATYPE,SysConsts.TYPE_KFB);
        String interfaceStr = "/info/searchpaging";
        DataTypeEnum dataTypeEnum = DataTypeEnum.OTHER;
        JSONObject searchPagingResult = null;
        try {
            searchPagingResult = Tool.doCallAndGetResult(json,interfaceStr,dataTypeEnum);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.log(Level.INFO,"调用 searchKfbcByPaging 结束,返回结果:"+searchPagingResult.toJSONString());
        return searchPagingResult;
    }

    @Override
	public String getDownloadFile(JSONArray criteria,String tempDir) {
        logger.log(Level.INFO,"调用方法:getDownloadFile,参数{criteria:"+criteria.toJSONString()
                +"tempDir:"+tempDir);

		//service接收json参数，获取将Json结果文件写入本地临时目录，把相对路径返回，
		String tempFileName = Tool.getRandonNumber(6)+Tool.getRandonNumber(6)+".json";
		String filepath = tempDir+tempFileName;

		JSONArray backfields = new JSONArray();
		backfields.add(ESConsts.HDFSPATH);

		JSONObject json = new JSONObject();
        json.put(SysConsts.DATATYPE,SysConsts.TYPE_DICOM);
		json.put(SysConsts.CRITERIA,criteria);
		json.put(SysConsts.BACKFIELDS,backfields);

        String interfaceStr = "/info/searchpaging";
        if(!doCallAndWriteToDisk(json,interfaceStr,filepath)){
            return null;
        }
        logger.log(Level.INFO,"调用 getDownloadFile 结束,返回结果:"+filepath);
        return filepath;
	}

    @Override
    public String getDownloadFileByIds(List<String> list,String tempDir) {
        JSONArray listJson = JSON.parseArray(JSON.toJSONString(list));
        logger.log(Level.INFO,"调用方法:getDownloadFileByIds,参数{list:"+listJson.toJSONString()
                +"tempDir:"+tempDir);

        String tempFileName = Tool.getRandonNumber(6)+Tool.getRandonNumber(6)+".json";
        String filepath = tempDir+tempFileName;
        JSONArray backfields = new JSONArray();
        backfields.add(ESConsts.HDFSPATH);

        JSONArray ids = new JSONArray();
        for(String e : list){
            ids.add(e);
        }
        JSONObject json = new JSONObject();
        json.put(SysConsts.IDS,ids);
        json.put(SysConsts.DATATYPE,SysConsts.TYPE_DICOM);
        json.put(SysConsts.BACKFIELDS,backfields);

        String interfaceStr = "/info/searchByIds";
        if(!doCallAndWriteToDisk(json,interfaceStr,filepath)){
            return null;
        }
        logger.log(Level.INFO,"调用 getDownloadFileByIds 结束,返回结果:"+filepath);
        return filepath;
    }

    @Override
    public String getDownloadFileByTag(String tag,String tempDir) {
        logger.log(Level.INFO,"调用方法:getDownloadFileByTag,参数{tag:"+tag
                +"tempDir:"+tempDir);

        String tempFileName = Tool.getRandonNumber(6)+Tool.getRandonNumber(6)+".json";
        String filepath = tempDir+tempFileName;

        JSONArray criteria = new JSONArray();
        JSONObject obj = new JSONObject();
        obj.put(SysConsts.SECTION,SysConsts.NO);
        obj.put(SysConsts.KEYWORD,ESConsts.TAG_ES);
        obj.put(SysConsts.VALUE,tag);
        criteria.add(obj);

        JSONArray backfields = new JSONArray();
        backfields.add(ESConsts.ID_ES);
        backfields.add(ESConsts.HDFSPATH);

        JSONObject json = new JSONObject();
        json.put(SysConsts.DATATYPE,SysConsts.TYPE_DICOM);
        json.put(SysConsts.CRITERIA,criteria);
        json.put(SysConsts.BACKFIELDS,backfields);

        String interfaceStr = "/info/searchpaging";

        if(!doCallAndWriteToDisk(json,interfaceStr,filepath)){
            return null;
        }
        logger.log(Level.INFO,"调用 getDownloadFileByIds 结束,返回结果:"+filepath);
        return filepath;
    }

    @Override
    public String getDesensitizeDownloadFileByTag(List<String> tags, String tempDir) {
        JSONArray tagJson = JSON.parseArray(JSON.toJSONString(tags));
        logger.log(Level.INFO,"调用方法:getDesensitizeDownloadFileByTag,参数{tag:"+tagJson
                +"tempDir:"+tempDir);

        String tempFileName = Tool.getRandonNumber(6)+Tool.getRandonNumber(6)+".json";
        String filepath = tempDir+File.separator+tempFileName;

        JSONObject param = new JSONObject();
        JSONArray tagsArr = new JSONArray();
        for(String tag : tags){
            tagsArr.add(tag);
        }
        param.put("tags",tagsArr);

        String interfaceStr = "/data/exportDesensitizeDicomByTag";

        if(!doCallAndWriteToDisk(param,interfaceStr,filepath)){
            return null;
        }
        logger.log(Level.INFO,"调用 getDesensitizeDownloadFileByTag 结束,返回结果:"+filepath);
        return filepath;
    }

    @Override
    public List<String> listInstitutionName() {
        logger.log(Level.INFO,"调用方法:listInstitutionName");

        String interfaceStr = "/info/listValueRange";
        JSONObject param = new JSONObject();
        param.put(SysConsts.DATATYPE,SysConsts.TYPE_DICOM);
        param.put("field",ESConsts.InstitutionName_ES);
        JSONObject jsonObject = Tool.doCallAndGetResult(param, interfaceStr, DataTypeEnum.OTHER);
        List<String> resultList = parseValueRange(jsonObject,ESConsts.InstitutionName_ES);

        JSONArray resultListJson = JSON.parseArray(JSON.toJSONString(resultList));
        logger.log(Level.INFO,"调用方法:listInstitutionName结束，返回结果："+resultListJson.toJSONString());
        return resultList;
    }

    @Override
    public List<String> listModality(){
        logger.log(Level.INFO,"调用方法:listModality");

        String interfaceStr = "/info/listValueRange";
        JSONObject param = new JSONObject();
        param.put(SysConsts.DATATYPE,SysConsts.TYPE_DICOM);
        param.put("field",ESConsts.Modality_ES);
        JSONObject jsonObject = Tool.doCallAndGetResult(param, interfaceStr, DataTypeEnum.OTHER);
        List<String> resultList = parseValueRange(jsonObject,ESConsts.Modality_ES);

        JSONArray resultListJson = JSON.parseArray(JSON.toJSONString(resultList));
        logger.log(Level.INFO,"调用方法:listModality，返回结果："+resultListJson.toJSONString());

        return resultList;
    }

    @Override
    public List<String> listSeriesDescription(){
        logger.log(Level.INFO,"调用方法:listSeriesDescription");

        String interfaceStr = "/info/listValueRange";
        JSONObject param = new JSONObject();
        param.put(SysConsts.DATATYPE,SysConsts.TYPE_DICOM);
        param.put("field",ESConsts.SeriesDescription_ES);
        JSONObject jsonObject = Tool.doCallAndGetResult(param, interfaceStr, DataTypeEnum.OTHER);

        List<String> resultList = parseValueRange(jsonObject,ESConsts.SeriesDescription_ES);

        JSONArray resultListJson = JSON.parseArray(JSON.toJSONString(resultList));
        logger.log(Level.INFO,"调用方法:listSeriesDescription，返回结果："+resultListJson.toJSONString());
        return resultList;
    }

    @Override
    public List<String> getHdfsPathByIds(List<String> idList, DataTypeEnum typeEnum) {
        JSONArray listJson = JSON.parseArray(JSON.toJSONString(idList));
        logger.log(Level.INFO,"调用方法:getHdfsPathByIds,参数{idList:"+listJson.toJSONString()
                +"typeEnum:"+typeEnum);

        if(idList==null || idList.size()==0){
            return null;
        }

        List<String> hdfspaths = new ArrayList<String>();

        if(DataTypeEnum.DICOM == typeEnum){
            JSONArray backfields = new JSONArray();
            backfields.add(ESConsts.HDFSPATH);
            JSONArray ids = new JSONArray();
            for(String e : idList){
                ids.add(e);
            }
            JSONObject param = new JSONObject();
            param.put(SysConsts.IDS,ids);
            param.put(SysConsts.DATATYPE,SysConsts.TYPE_DICOM);
            param.put(SysConsts.BACKFIELDS,backfields);
            String interfaceStr = "/info/searchByIds";
            DataTypeEnum dataTypeEnum = DataTypeEnum.DICOM;
            JSONObject result = null;
            try {
                result = Tool.doCallAndGetResult(param, interfaceStr,dataTypeEnum);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            logger.log(Level.INFO,"查询的结果："+result.toJSONString());

            String code = result.getString(SysConsts.CODE);
            if(code!=null && code.equals(SysConsts.CODE_000)){
                JSONArray data = result.getJSONArray(SysConsts.DATA);
                int size = data.size();
                for(int i=0;i<size;i++){
                    JSONObject jsonObject = data.getJSONObject(i);
                    hdfspaths.add(jsonObject.getString(ESConsts.HDFSPATH));
                }
            }
        }

        return hdfspaths;
    }

    @Override
    public Long getSizeForData(List<String> paths) {
        try {
            return  HdfsTool.getSizeOfDicom(paths);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    @Override
    public String exportExcel(JSONArray criteria,String tempDir) {
        logger.log(Level.INFO,"调用方法:exportExcel,参数{list:"+criteria.toJSONString()
                +"tempDir:"+tempDir);
        String tempFileName = Tool.getRandonNumber(6)+Tool.getRandonNumber(6)+".xls";
        String filepath = tempDir+tempFileName;

        JSONArray backfields = new JSONArray();
        backfields.add(ESConsts.ID_ES);
        backfields.add(ESConsts.InstitutionName_ES);
        backfields.add(ESConsts.PatientName_ES);
        backfields.add(ESConsts.ManufacturerModelName_ES);
        backfields.add(ESConsts.SeriesDescription_ES);
        backfields.add(ESConsts.SeriesDate_ES);
        backfields.add(ESConsts.TAG_ES);
        backfields.add(ESConsts.NumberOfSlices_ES);

        JSONObject json = new JSONObject();
        json.put(SysConsts.DATATYPE,SysConsts.TYPE_DICOM);
        json.put(SysConsts.CRITERIA,criteria);
        json.put(SysConsts.BACKFIELDS,backfields);
        String interfaceStr = "/info/searchpaging";
        DataTypeEnum dataTypeEnum = DataTypeEnum.OTHER;
        JSONObject searchPagingResult;
        try {
            searchPagingResult = Tool.doCallAndGetResult(json,interfaceStr,dataTypeEnum);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if(createExcelTempFile(searchPagingResult,filepath)){
            logger.log(Level.INFO,"调用 exportExcel 结束,结果:"+filepath);
            return filepath;
        }
        logger.log(Level.INFO,"调用 exportExcel 结束,返回结果:"+null);
        return null;
    }


	public boolean doCallAndWriteToDisk(JSONObject parameter,String interfaceStr,String filepath){
        logger.log(Level.INFO,"调用方法:doCallAndWriteToDisk,参数{parameter:"+parameter.toJSONString()
                +"interfaceStr:"+interfaceStr
                +"filepath:"+filepath
        );
        MilkConfiguration conf = new MilkConfiguration();
        boolean isSuccess = false;
        try {
            byte[] param = parameter.toString().getBytes("UTF-8");
            String url = SysConsts.HTTP_HEAD+conf.getInfosupplyerip()+":"+conf.getInfosupplyerport()+interfaceStr;
            URL restServiceURL = new URL(url);

            HttpURLConnection httpConnection = (HttpURLConnection) restServiceURL.openConnection();
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("Accept", "application/json");
            httpConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            //httpConnection.setRequestProperty("Connection", "Keep-Alive");
            httpConnection.setRequestProperty("Charset", "UTF-8");
            httpConnection.setDoOutput(true);
            httpConnection.setDoInput(true);

            //传递参数
            httpConnection.setRequestProperty("Content-Length", String.valueOf(param));
            OutputStream outputStream = httpConnection.getOutputStream();
            outputStream.write(param);
            outputStream.flush();
            outputStream.close();
            logger.log(Level.INFO,"返回码："+httpConnection.getResponseCode());
            if (httpConnection.getResponseCode() == 200) {
                InputStream inputStream = httpConnection.getInputStream();
                File file = new File(filepath);
                if(!file.exists()) {
                    file.createNewFile();
                    file.setWritable(true, false);
                }
                FileOutputStream fout = new FileOutputStream(file);
                byte []tmp = new byte[1024];
                int n = -1;
                while((n=inputStream.read(tmp))!=-1) {
                    fout.write(tmp, 0, n);
                }
                fout.close();
                inputStream.close();
                isSuccess = true;
            } else {
                System.out.println("从infosupplyer下载文件失败");
                isSuccess = false;
            }
            httpConnection.disconnect();
        }catch (IOException e){
            e.printStackTrace();
        }
        logger.log(Level.INFO,"调用 doCallAndWriteToDisk 结束,返回结果:"+isSuccess);
        return isSuccess;
    }

    private boolean createExcelTempFile(JSONObject parameter,String filepath){
        logger.log(Level.INFO,"调用方法:createExcelTempFile,参数:filepath:"+filepath);
        String code = parameter.getString("code");

        if(!SysConsts.CODE_000.equals(code)){
            return false;
        }
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("sheet1");
        HSSFRow headrow = sheet.createRow(0);
        headrow.createCell(0).setCellValue(ESConsts.InstitutionName_ES);
        headrow.createCell(1).setCellValue(ESConsts.PatientName_ES);
        headrow.createCell(2).setCellValue(ESConsts.SeriesDescription_ES);
        headrow.createCell(3).setCellValue(ESConsts.SeriesDate_ES); //这里采用名称为studydate,实际是seriesDate
        headrow.createCell(4).setCellValue(ESConsts.NumberOfSlices_ES);
        headrow.createCell(5).setCellValue(ESConsts.TAG_ES);
        headrow.createCell(6).setCellValue(ESConsts.ID_ES);

        JSONArray data = parameter.getJSONArray("data");
        if(data.size() == 0)
            return false;
        int size = data.size();

        JSONObject jsonObject = data.getJSONObject(0);
        List<String> keyList = new ArrayList<String>();
        for(String key : jsonObject.keySet()){
            keyList.add(key);
        }

        int i=0;
        for(String str : keyList){
            headrow.createCell(i++).setCellValue(str);
        }

        logger.log(Level.INFO,"数据数量:"+size);

        for(int j=1; j<=size; j++){
            JSONObject item = data.getJSONObject(j-1);
            HSSFRow rows = sheet.createRow(j);
            int k = 0;
            for(String key : keyList){
                rows.createCell(k++).setCellValue(item.getString(key));
            }
        }
        File xlsFile = new File(filepath);
        FileOutputStream xlsStream = null;
        try {
            xlsStream = new FileOutputStream(xlsFile);
            workbook.write(xlsStream);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean createDicomZipTempFile(String srcDir,String zipPath,String zipName){
        return Tool.zipCompress(srcDir,zipPath,zipName);
    }

    private List<String> parseValueRange(JSONObject param,String field){
	    List<String> list = new ArrayList<>();
        if(SysConsts.CODE_000.equals(param.getString(SysConsts.CODE))) {
            Long total = param.getLong(SysConsts.TOTAL);
            if (total > 0) {
                JSONArray data = param.getJSONArray(SysConsts.DATA);
                int size = data.size();
                for (int i = 0; i < size; i++) {
                    JSONObject obj = data.getJSONObject(i);
                    String value = obj.getString(field);
                    list.add(value);
                }
            }
        }
        return list;
    }



}