package qed.bigdata.infosupplyer.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import qed.bigdata.infosupplyer.conf.InfosupplyerConfiguration;
import qed.bigdata.infosupplyer.consts.EsConsts;
import qed.bigdata.infosupplyer.consts.SysConsts;
import qed.bigdata.infosupplyer.util.InfoSupplyerTool;
import qed.bigdata.infosupplyer.service.HBaseService;
import qed.bigdata.infosupplyer.util.HBaseUtil;
import qed.bigdata.infosupplyer.util.ZipUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Service("HBaseService")
public class HBaseServiceImpl implements HBaseService {
    static Logger logger = Logger.getLogger(ElasticSearchServiceImpl.class);

    Connection conn = null;
    InfosupplyerConfiguration infosupplyerConfiguration = null;
    static List<String> IntegerFieldList = new ArrayList<String>();
    static List<String> LongFieldList = new ArrayList<String>();
    static List<String> DoubleFieldList = new ArrayList<String>();

    static{
        IntegerFieldList.add(EsConsts.PatientAge_ES_DCM);
        IntegerFieldList.add(EsConsts.SeriesNumber_ES_DCM);
        IntegerFieldList.add(EsConsts.NumberOfSlices_ES_DCM);

        LongFieldList.add(EsConsts.StudyTime_ES_DCM);
        LongFieldList.add(EsConsts.SeriesTime_ES_DCM);
        LongFieldList.add(EsConsts.AcquisitionTime_ES_DCM);
        LongFieldList.add(EsConsts.ContentTime_ES_DCM);

        DoubleFieldList.add(EsConsts.PatientSize_ES_DCM);
        DoubleFieldList.add(EsConsts.PatientWeight_ES_DCM);
        DoubleFieldList.add(EsConsts.SliceThickness_ES_DCM);
        DoubleFieldList.add(EsConsts.ReconstructionDiameter_ES_DCM);
        DoubleFieldList.add(EsConsts.SliceLocation_ES_DCM);
    }

    public HBaseServiceImpl(){
        conn = HBaseUtil.getConnection();
        infosupplyerConfiguration = new InfosupplyerConfiguration();
    }

    @Override
    public String downloadThumbnailByRowkey(String rowkey,String path) throws Exception {
        logger.log(Level.INFO,"方法:downloadThumbnailByRowkey 被调用，参数:{rowkey:"+rowkey
                +"path："+path+"}");

        Table table = conn.getTable(TableName.valueOf(infosupplyerConfiguration.getDicomThumbnailTablename()));
        Scan scan  = new Scan();
        scan.setStartRow(Bytes.toBytes(rowkey+"0"));
        scan.setStopRow(Bytes.toBytes(rowkey+"9"));

        //该rowkey缩略图特有临时目录，生成zip文件后会被删除
        String thumbnailTemp = path+File.separator+rowkey.substring(0,10);
        File tempDir = new File(thumbnailTemp);
        if(!tempDir.exists()){
            tempDir.mkdir();
        }

        logger.log(Level.INFO,"存放缩略图临时目录:"+thumbnailTemp);

        FileOutputStream fout = null;

        ResultScanner scanner = table.getScanner(scan);
        for(Result result : scanner){
            Cell[] cells = result.rawCells();
            for(Cell cell : cells){
                String tempRowkey = Bytes.toString(CellUtil.cloneRow(cell));
                String qualify = Bytes.toString(CellUtil.cloneQualifier(cell));
                if(qualify.equals(SysConsts.THUMBNAIL)){
                    byte[] temp = CellUtil.cloneValue(cell);
                    String filename = tempRowkey.substring(tempRowkey.length()-6,tempRowkey.length());
                    System.out.println(thumbnailTemp+File.separator+filename+".jpg");
                    fout = new FileOutputStream(new File(thumbnailTemp+File.separator+filename+".jpg"));
                    fout.write(temp);
                    fout.close();
                }
            }
        }
        String filename = rowkey.substring(0,10)+".zip";
        /**tempDir.getAbsolutePath()是存放图片的目录，
         * path：存放zip文件的目录
         * filename：zip文件名称
         */
        ZipUtil.zip(tempDir.getAbsolutePath(),path,filename);
        InfoSupplyerTool.delFolder(thumbnailTemp);

        String zipFilePath = path+File.separator+filename;

        logger.log(Level.INFO,"zip压缩略文件路径:"+zipFilePath);

        return zipFilePath;
    }



    @Override
    public void updateColumn(String tablename, String rowkey, String cf, String qualify, String value) throws IOException {
        logger.log(Level.INFO,"方法:updateColumn 被调用，参数:{tablename:"+tablename
                +"rowkey："+rowkey
                +"cf：" + cf
                +"qualify："+qualify
                +"value："+value
                +"}");

        Table table = conn.getTable(TableName.valueOf(tablename));
        Put put = new Put(Bytes.toBytes(rowkey));
        put.addColumn(Bytes.toBytes(cf),Bytes.toBytes(qualify),Bytes.toBytes(value));
        table.put(put);
        logger.log(Level.INFO,"方法：updateColumn 调用结束");
    }

    @Override
    public void updateTagForDicom(String tablename, List<String> rowkeys, String cf, String qualify, String value) throws IOException {
        JSONArray rowkeysJson = JSON.parseArray(JSON.toJSONString(rowkeys));
        logger.log(Level.INFO,"方法:updateTagForDicom 被调用，参数:{tablename:"+tablename
                +"rowkeys："+rowkeysJson
                +"cf：" + cf
                +"qualify："+qualify
                +"value："+value
                +"}");

        Table table = conn.getTable(TableName.valueOf(tablename));
        int batchSize = 1000;
        int counter = 0;
        List<Put> puts = new ArrayList<Put>();
        for(String rowkey : rowkeys){
            counter++;
            Put put = new Put(Bytes.toBytes(rowkey));
            put.addColumn(Bytes.toBytes(cf),Bytes.toBytes(qualify),Bytes.toBytes(value));
            puts.add(put);
            if(counter % batchSize == 0){
                table.put(puts);
                puts = new ArrayList<Put>();
            }
        }
        table.put(puts);
        logger.log(Level.INFO,"方法：updateTagForDicom 调用结束");
    }

    @Override
    public int putOne(String tablename, String cf, JSONObject metaJson) throws IOException {
        logger.log(Level.INFO,"方法:putOne 被调用，参数:{tablename:"+tablename
                +"cf：" + cf
                +"metaJson："+metaJson.toJSONString()
                +"}");

        String rowkey = metaJson.getString(EsConsts.ROWKEY);
        if(isExists(tablename,rowkey)){
            return SysConsts.EXISTS;
        }
        BufferedMutatorParams params = new BufferedMutatorParams(TableName.valueOf(tablename));
        try(BufferedMutator mutator = conn.getBufferedMutator(params)){
            Put put = new Put(Bytes.toBytes(rowkey));
            for(String key : metaJson.keySet()){
                if(IntegerFieldList.contains(key)){
                    put.addColumn(Bytes.toBytes(cf),Bytes.toBytes(key),Bytes.toBytes(metaJson.getInteger(key)));
                }else if(LongFieldList.contains(key)){
                    put.addColumn(Bytes.toBytes(cf),Bytes.toBytes(key),Bytes.toBytes(metaJson.getLong(key)));
                }else if(DoubleFieldList.contains(key)){
                    put.addColumn(Bytes.toBytes(cf),Bytes.toBytes(key),Bytes.toBytes(metaJson.getDouble(key)));
                }else{
                    put.addColumn(Bytes.toBytes(cf),Bytes.toBytes(key),Bytes.toBytes(metaJson.getString(key)));
                }
            }
            mutator.mutate(put);
            mutator.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.log(Level.INFO,"方法：putOne 调用结束");
        return SysConsts.SUCCESS;
    }

    @Override
    public int putOne(String tableName, String cf, Map<String, String> metaMap) throws IOException {
        JSONObject metaJSON = new JSONObject();
        for(Map.Entry<String,String> entry : metaMap.entrySet()){
            metaJSON.put(entry.getKey(),entry.getValue());
        }
        return putOne(tableName, cf,metaJSON);
    }

    @Override
    public int putBatch(String tableName, String cf, Map<String, String> colvalues) {
        return 0;
    }

    @Override
    public int putCell(String tablename, String rowkey, String cf, String col, byte[] value) throws IOException {
        logger.log(Level.INFO,"方法:putCell 被调用，参数:{tablename:"+tablename
                +"rowkey：" + rowkey
                +"cf：" + cf
                +"col：" + col
                +"value："+value.length
                +"}");

        if(isExists(tablename,rowkey)){
            return SysConsts.EXISTS;
        }
        BufferedMutatorParams params = new BufferedMutatorParams(TableName.valueOf(tablename));
        try(BufferedMutator mutator = conn.getBufferedMutator(params)){
            Put put = new Put(Bytes.toBytes(rowkey));
            put.addColumn(Bytes.toBytes(cf),Bytes.toBytes(col),value);
            mutator.mutate(put);
            mutator.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.log(Level.INFO,"方法：putCell 调用结束");
        return SysConsts.SUCCESS;
    }

    @Override
    public boolean delete(String tablename, String rowkey) {
        logger.log(Level.INFO,"方法:delete 被调用，参数:{tablename:"+tablename
                +"rowkey：" + rowkey
                +"}");
        HTable table = null;
        long length = 0;
        try {
            table = (HTable)conn.getTable(TableName.valueOf(tablename));
            Delete delete = new Delete(rowkey.getBytes()); // 根据主键查询
            table.delete(delete);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }finally {
            try {
                table.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logger.log(Level.INFO,"方法：delete 调用结束");
        return true;
    }

    @Override
    public boolean isExists(String tablename, String rowkey){
        logger.log(Level.INFO,"方法:isExists 被调用，参数:{tablename:"+tablename
                +"rowkey：" + rowkey
                +"}");
        HTable table = null;
        long length = 0;
        try {
            table = (HTable)conn.getTable(TableName.valueOf(tablename));
            Get get = new Get(rowkey.getBytes()); // 根据主键查询
            Result result = table.get(get);
            length = result.rawCells().length;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                table.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logger.log(Level.INFO,"方法：isExists 调用结束");
        return length>0;
    }

}
