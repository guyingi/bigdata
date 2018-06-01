package yasen.bigdata.milk.service.impl;

/**
 * @Title: SearchServiceImpl.java
 * @Package yasen.bigdata.milk.service.impl
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
import java.util.Random;

import com.alibaba.fastjson.JSONArray;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;

import yasen.bigdata.milk.consts.DataTypeEnum;
import yasen.bigdata.milk.consts.ESConstants;
import yasen.bigdata.milk.conf.MilkConfiguration;
import yasen.bigdata.milk.consts.SysConstants;
import yasen.bigdata.milk.pojo.Dicom;
import yasen.bigdata.milk.service.SearchService;
import yasen.bigdata.milk.tool.HdfsTool;
import yasen.bigdata.milk.tool.MilkTool;

@Service
public class SearchServiceImpl implements SearchService {
	public static void main(String[] args) {
//		JSONObject json = new JSONObject();
//		JSONObject searchcondition = new JSONObject();
//		searchcondition.put(SysConstants.DEVICE_PARAM,"PET/MR");
//        searchcondition.put(SysConstants.SERIES_DESCRIPTION_PARAM,"Flair");
//        searchcondition.put(SysConstants.ORGAN_PARAM,"brain");
//        searchcondition.put(SysConstants.INSTITUTION_PARAM,"PUMCH-GX");
//        searchcondition.put(SysConstants.SEX_PARAM,"M");
//        searchcondition.put(SysConstants.AGE_START_PARAM,"85");
//        searchcondition.put(SysConstants.AGE_END_PARAM,86);
//        searchcondition.put(SysConstants.STUDYDATE_START_PARAM,"2018-01-01");
//        searchcondition.put(SysConstants.STUDYDATE_END_PARAM,"2018-01-26");
//        searchcondition.put(SysConstants.ENTRYDATE_START_PARAM,"2018-04-20");
//        searchcondition.put(SysConstants.ENTRYDATE_END_PARAM,"2018-04-20");
//        searchcondition.put(SysConstants.IMAGECOUNT_MIN_PARAM,121);
//        searchcondition.put(SysConstants.IMAGECOUNT_MAX_PARAM,121);
//		json.put("searchcondition",searchcondition);
//		json.put("pageid",1);
//		json.put("pagesize",3);
//		JSONArray backfields = new JSONArray();
//        backfields.add(ESConstants.InstitutionName_ES);
//        backfields.add(ESConstants.ORGAN_ES);
//		backfields.add(ESConstants.PatientName_ES);
//		backfields.add(ESConstants.PatientsAge_ES);
//		backfields.add(ESConstants.PatientsAge_ES);
//        backfields.add(ESConstants.SeriesDescription_ES);
//        backfields.add(ESConstants.SeriesDate_ES);
//        backfields.add(ESConstants.NumberOfSlices_ES);
//        backfields.add(ESConstants.ID_ES);
//		json.put("backfields",backfields);
//
//		JSONArray sortfields = new JSONArray();
//		sortfields.add(ESConstants.SeriesDate_ES);
//		sortfields.add(ESConstants.PatientName_ES);
//		json.put("sortfields",sortfields);
//		JSONObject jsonObject = new SearchServiceImpl().searchByPaging(searchcondition,1,3);
//		System.out.println(jsonObject.toJSONString());
//		String code = jsonObject.getString("code");
//		System.out.println("返回码："+code);

    }

	@Override
	public JSONObject searchDicomByPaging(JSONObject searchcondition,JSONArray backfields,JSONArray sortfields,Integer pageid,Integer pagesize) {
		JSONObject json = new JSONObject();
		if (searchcondition == null || searchcondition.size() == 0){
		    return new JSONObject();
        } else {
            json.put(SysConstants.SEARCH_CONDITION,searchcondition);
        }
        if(backfields != null && backfields.size() > 0) {
            json.put(SysConstants.BACKFIELDS, backfields);
        }
        if(sortfields != null && sortfields.size() > 0) {
            json.put(SysConstants.SORTFIELDS,sortfields);
		}
        if(pageid != null) {
            json.put(SysConstants.PAGE_ID, pageid);
        }
		if(pagesize != null) {
            json.put(SysConstants.PAGE_SIZE, pagesize);
        }
        json.put(SysConstants.DATATYPE,SysConstants.TYPE_DICOM);
		String interfaceStr = "/info/_searchpaging";
        DataTypeEnum dataTypeEnum = DataTypeEnum.DICOM;
        JSONObject searchPagingResult = null;
        try {
            searchPagingResult = MilkTool.doCallAndGetResult(json,interfaceStr,dataTypeEnum);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return searchPagingResult;
	}

    @Override
    public JSONObject searchElectricByPaging(JSONObject criteria, JSONArray backfields, JSONArray sortfields, Integer pageid, Integer pagesize) {
        JSONObject json = new JSONObject();
        if (criteria == null || criteria.size() == 0){
            return new JSONObject();
        } else {
            json.put(SysConstants.SEARCH_CONDITION,criteria);
        }
        if(backfields != null && backfields.size() > 0) {
            json.put(SysConstants.BACKFIELDS, backfields);
        }
        if(sortfields != null && sortfields.size() > 0) {
            json.put(SysConstants.SORTFIELDS,sortfields);
        }
        if(pageid != null) {
            json.put(SysConstants.PAGE_ID, pageid);
        }
        if(pagesize != null) {
            json.put(SysConstants.PAGE_SIZE, pagesize);
        }
        json.put(SysConstants.DATATYPE,SysConstants.TYPE_ELECTRIC);
        String interfaceStr = "/info/_searchpaging";
        DataTypeEnum dataTypeEnum = DataTypeEnum.ELECTRIC;
        JSONObject searchPagingResult = null;
        try {
            searchPagingResult = MilkTool.doCallAndGetResult(json,interfaceStr,dataTypeEnum);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return searchPagingResult;
    }

    @Override
	public String getDownloadFile(JSONObject searchcondition,String tempDir) {
		//service接收json参数，获取将Json结果文件写入本地临时目录，把相对路径返回，
		MilkConfiguration conf = new MilkConfiguration();
		String tempFileName = MilkTool.getRandonNumber(6)+MilkTool.getRandonNumber(6)+".json";
		String filepath = tempDir+tempFileName;

		boolean isSuccess = false;

		JSONArray backfields = new JSONArray();
		backfields.add(ESConstants.HDFSPATH);

		JSONObject json = new JSONObject();
		json.put(SysConstants.SEARCH_CONDITION,searchcondition);
		json.put(SysConstants.BACKFIELDS,backfields);

        String interfaceStr = "/info/_searchpaging";
        if(!doCallAndWriteToDisk(json,interfaceStr,filepath)){
            return null;
        }
        return filepath;
	}

    @Override
    public String getDownloadFileByIds(List<String> list,String tempDir) {
        MilkConfiguration conf = new MilkConfiguration();
        String tempFileName = MilkTool.getRandonNumber(6)+MilkTool.getRandonNumber(6)+".json";
        String filepath = tempDir+tempFileName;
        boolean isSuccess = false;
        JSONArray backfields = new JSONArray();
        backfields.add(ESConstants.HDFSPATH);

        JSONArray ids = new JSONArray();
        for(String e : list){
            ids.add(e);
        }
        JSONObject json = new JSONObject();
        json.put(SysConstants.IDS,ids);
        json.put(SysConstants.DATATYPE,SysConstants.TYPE_DICOM);
        json.put(SysConstants.BACKFIELDS,backfields);

        String interfaceStr = "/info/_searchByIds";
        if(!doCallAndWriteToDisk(json,interfaceStr,filepath)){
            return null;
        }
        return filepath;
    }

    @Override
    public String exportExcel(JSONObject searchcondition,String tempDir) {
        String tempFileName = MilkTool.getRandonNumber(6)+MilkTool.getRandonNumber(6)+".xls";
        String filepath = tempDir+tempFileName;

        JSONArray backfields = new JSONArray();
        backfields.add(ESConstants.ID_ES);
        backfields.add(ESConstants.InstitutionName_ES);
        backfields.add(ESConstants.ORGAN_ES);
        backfields.add(ESConstants.PatientName_ES);
        backfields.add(ESConstants.SeriesDescription_ES);
        backfields.add(ESConstants.SeriesDate_ES);
        backfields.add(ESConstants.TAG_ES);
        backfields.add(ESConstants.NumberOfSlices_ES);

        JSONObject json = new JSONObject();
        json.put(SysConstants.SEARCH_CONDITION,searchcondition);
        json.put(SysConstants.BACKFIELDS,backfields);
        String interfaceStr = "/info/_searchpaging";
        DataTypeEnum dataTypeEnum = DataTypeEnum.DICOM;
        JSONObject searchPagingResult = null;
        try {
            searchPagingResult = MilkTool.doCallAndGetResult(json,interfaceStr,dataTypeEnum);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        System.out.println(searchPagingResult.toJSONString());
        if(createExcelTempFile(searchPagingResult,filepath)){
            return filepath;
        }
        return null;
    }


	public boolean doCallAndWriteToDisk(JSONObject parameter,String interfaceStr,String filepath){
        MilkConfiguration conf = new MilkConfiguration();
        boolean isSuccess = false;
        try {
            byte[] param = parameter.toString().getBytes("UTF-8");
            String url = SysConstants.HTTP_HEAD+conf.getInfosupplyerip()+":"+conf.getInfosupplyerport()+interfaceStr;
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
            System.out.println("返回码："+httpConnection.getResponseCode());
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
        return isSuccess;
    }




    private boolean createExcelTempFile(JSONObject parameter,String filepath){
        String code = parameter.getString("code");

        if(!code.equals("000")){
            return false;
        }
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("sheet1");
        HSSFRow headrow = sheet.createRow(0);
        headrow.createCell(0).setCellValue(ESConstants.InstitutionName_ES);
        headrow.createCell(1).setCellValue(ESConstants.ORGAN_ES);
        headrow.createCell(2).setCellValue(ESConstants.SeriesDescription_ES);
        headrow.createCell(3).setCellValue(ESConstants.PatientName_ES);
        headrow.createCell(4).setCellValue(ESConstants.StudyDate_ES); //这里采用名称为studydate,实际是seriesDate
        headrow.createCell(5).setCellValue(ESConstants.NumberOfSlices_ES);

        JSONArray data = parameter.getJSONArray("data");
        int size = data.size();
        for(int i=1;i<=size;i++){
            JSONObject dicom = data.getJSONObject(i-1);
            HSSFRow rows = sheet.createRow(i);
            rows.createCell(0).setCellValue(dicom.getString("institutionName"));
            rows.createCell(1).setCellValue(dicom.getString("organ"));
            rows.createCell(2).setCellValue(dicom.getString("seriesDescription"));
            rows.createCell(3).setCellValue(dicom.getString("patientName"));
            rows.createCell(4).setCellValue(dicom.getString("seriesDate"));
            rows.createCell(5).setCellValue(dicom.getString("numberOfSlices"));
            rows.createCell(6).setCellValue(dicom.getString("id"));
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
        return MilkTool.zipCompress(srcDir,zipPath,zipName);
    }




}