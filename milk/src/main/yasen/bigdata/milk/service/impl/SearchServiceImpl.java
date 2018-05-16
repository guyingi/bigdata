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
	public JSONObject searchByPaging(JSONObject searchcondition,int pageid,int pagesize) {
		MilkConfiguration conf = new MilkConfiguration();
		List<Dicom> dicomList = new ArrayList<Dicom>();
		JSONObject result = new JSONObject();
		JSONArray data = new JSONArray();
		StringBuilder builder = new StringBuilder();
		boolean isSuccess = false;

		JSONArray backfields = new JSONArray();
		backfields.add(ESConstants.InstitutionName_ES);
		backfields.add(ESConstants.ORGAN_ES);
		backfields.add(ESConstants.PatientName_ES);
		backfields.add(ESConstants.SeriesDescription_ES);
		backfields.add(ESConstants.SeriesDate_ES);
		backfields.add(ESConstants.NumberOfSlices_ES);
		backfields.add(ESConstants.ID_ES);

		JSONArray sortfields = new JSONArray();
		sortfields.add(ESConstants.InstitutionName_ES);
		sortfields.add(ESConstants.SeriesDescription_ES);
		sortfields.add(ESConstants.PatientName_ES);
		sortfields.add(ESConstants.SeriesDate_ES);
		sortfields.add(ESConstants.NumberOfSlices_ES);

		JSONObject json = new JSONObject();
		json.put(SysConstants.SEARCH_CONDITION,searchcondition);
		json.put(SysConstants.PAGE_ID,pageid);
		json.put(SysConstants.PAGE_SIZE,pagesize);
		json.put(SysConstants.BACKFIELDS,backfields);
		json.put(SysConstants.SORTFIELDS,sortfields);
		String interfaceStr = "/info/_searchpaging";
        JSONObject searchPagingResult = doCallAndGetResult(json,interfaceStr);
		return searchPagingResult;
	}

	@Override
	public String getDownloadFile(JSONObject searchcondition,String tempDir) {
		//service接收json参数，获取将Json结果文件写入本地临时目录，把相对路径返回，
		MilkConfiguration conf = new MilkConfiguration();
		String tempFileName = generateRandon()+generateRandon()+".json";
		String filepath = tempDir+tempFileName;

		boolean isSuccess = false;

		JSONArray backfields = new JSONArray();
		backfields.add(ESConstants.HDFSPATH_ES);

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
        String tempFileName = generateRandon()+generateRandon()+".json";
        String filepath = tempDir+tempFileName;
        boolean isSuccess = false;
        JSONArray backfields = new JSONArray();
        backfields.add(ESConstants.HDFSPATH_ES);

        JSONArray ids = new JSONArray();
        for(String e : list){
            ids.add(e);
        }
        JSONObject json = new JSONObject();
        json.put(SysConstants.IDS,ids);
        json.put(SysConstants.BACKFIELDS,backfields);

        String interfaceStr = "/info/_searchByIds";
        if(!doCallAndWriteToDisk(json,interfaceStr,filepath)){
            return null;
        }
        return filepath;
    }

    @Override
    public String exportExcel(JSONObject searchcondition,String tempDir) {
        String tempFileName = generateRandon()+generateRandon()+".xls";
        String filepath = tempDir+tempFileName;

        JSONArray backfields = new JSONArray();
        backfields.add(ESConstants.ID_ES);
        backfields.add(ESConstants.InstitutionName_ES);
        backfields.add(ESConstants.ORGAN_ES);
        backfields.add(ESConstants.PatientName_ES);
        backfields.add(ESConstants.SeriesDescription_ES);
        backfields.add(ESConstants.SeriesDate_ES);
        backfields.add(ESConstants.NumberOfSlices_ES);

        JSONObject json = new JSONObject();
        json.put(SysConstants.SEARCH_CONDITION,searchcondition);
        json.put(SysConstants.BACKFIELDS,backfields);
        String interfaceStr = "/info/_searchpaging";
        JSONObject searchPagingResult = doCallAndGetResult(json,interfaceStr);
        System.out.println(searchPagingResult.toJSONString());
        if(createExcelTempFile(searchPagingResult,filepath)){
            return filepath;
        }
        return null;
    }

    @Override
    public String getDicomZipByIds(List<String> list, String tempDir) {
	    if(list==null || list.size()==0){
	        return null;
        }
	    //tempDir结尾自带斜线
        String name = generateRandon()+generateRandon();
        String zipSrcDir = tempDir+name+MilkTool.getDelimiter();
        String zipPath = tempDir;
        String zipName = name+".zip";

        boolean isSuccess = false;

        JSONArray backfields = new JSONArray();
        backfields.add(ESConstants.HDFSPATH_ES);
        JSONArray ids = new JSONArray();
        for(String e : list){
            ids.add(e);
        }
        JSONObject json = new JSONObject();
        json.put(SysConstants.IDS,ids);
        json.put(SysConstants.BACKFIELDS,backfields);
        String interfaceStr = "/info/_searchByIds";

        JSONObject result = doCallAndGetResult(json, interfaceStr);
        System.out.println("查询的结果："+result.toJSONString());

        List<String> hdfspaths = new ArrayList<String>();
        String code = result.getString(SysConstants.CODE);
        if(code!=null && code.equals(SysConstants.CODE_000)){
            JSONArray data = result.getJSONArray(SysConstants.DATA);
            int size = data.size();
            for(int i=0;i<size;i++){
                JSONObject jsonObject = data.getJSONObject(i);
                hdfspaths.add(jsonObject.getString(ESConstants.HDFSPATH_ES));
            }
        }
        boolean download = false;
        if(hdfspaths.size()!=0)
            download = HdfsTool.download(hdfspaths, zipSrcDir);
        if(download) {
            if (createDicomZipTempFile(zipSrcDir, zipPath,zipName)) {
                return zipPath+zipName;
            }
        }
        return null;
    }


    //生成6位数
	private String generateRandon() {
		Random rand = new Random();
		int result = 0;
		while(100000>=(result=rand.nextInt(1000000))) {}
		return result+"";
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


	private JSONObject doCallAndGetResult(JSONObject parameter,String interfaceStr){
        MilkConfiguration conf = new MilkConfiguration();
        JSONObject result = new JSONObject();
        JSONArray data = new JSONArray();
        List<Dicom> dicomList = new ArrayList<Dicom>();
        StringBuilder builder = new StringBuilder();
        boolean isSuccess = false;
        System.out.println(parameter.toJSONString());
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
                BufferedReader tBufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String tempStr = null;
                while ((tempStr = tBufferedReader.readLine()) != null) {
                    builder.append(tempStr);
                }
                inputStream.close();
                isSuccess = true;
            } else {
                System.out.println("从infosupplyer获取文件失败");
            }
//		    System.out.println("接收到的数据："+builder.toString());
            httpConnection.disconnect();
        }catch (IOException e){
            e.printStackTrace();
        }

        if(isSuccess) {
            JSONReader reader = new JSONReader(new StringReader(builder.toString()));
            reader.startObject();
            while (reader.hasNext()){
                String key = reader.readString();
                if(key.equals("code")){
                    result.put("code",reader.readObject(String.class));
                }else if(key.equals("pagecount")){
                    result.put("pagecount",reader.readObject(Long.class));
                }else if(key.equals("total")){
                    result.put("total",reader.readObject(Long.class));
                }else if(key.equals("data")){
                    reader.startArray();
                    while(reader.hasNext()){
                        Dicom dicom = JSON.parseObject(reader.readObject().toString(), Dicom.class);
                        dicomList.add(dicom);
                    }
                    reader.endArray();
                    result.put("data",dicomList);
                }
            }
            reader.endObject();
        }
        return result;
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