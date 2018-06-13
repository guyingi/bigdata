package qed.bigdata.infosupplyer.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
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
        System.out.println("thumbnailTemp:"+thumbnailTemp);
        FileOutputStream fout = null;

        ResultScanner scanner = table.getScanner(scan);
        System.out.println("A");
        for(Result result : scanner){
            System.out.println("B");
            Cell[] cells = result.rawCells();
            for(Cell cell : cells){
                String tempRowkey = Bytes.toString(CellUtil.cloneRow(cell));
                String qualify = Bytes.toString(CellUtil.cloneQualifier(cell));
                if(qualify.equals(SysConsts.THUMBNAIL)){
                    byte[] temp = CellUtil.cloneValue(cell);
                    System.out.println("C:"+temp.length);
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
        return path+File.separator+filename;
    }



    @Override
    public void updateColumn(String tablename, String rowkey, String cf, String qualify, String value) throws IOException {
        Table table = conn.getTable(TableName.valueOf(tablename));
        Put put = new Put(Bytes.toBytes(rowkey));
        put.addColumn(Bytes.toBytes(cf),Bytes.toBytes(qualify),Bytes.toBytes(value));
        table.put(put);
    }

    @Override
    public void updateTagForDicom(String tablename, List<String> rowkeys, String cf, String qualify, String value) throws IOException {
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
    }

    @Override
    public int putOne(String tableName, String cf, JSONObject metaJson) throws IOException {
        String rowkey = metaJson.getString(EsConsts.ROWKEY);
        if(isExists(tableName,rowkey)){
            return SysConsts.EXISTS;
        }
        BufferedMutatorParams params = new BufferedMutatorParams(TableName.valueOf(tableName));
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
    public int putCell(String tableName, String rowkey, String cf, String col, byte[] value) throws IOException {
        if(isExists(tableName,rowkey)){
            return SysConsts.EXISTS;
        }
        BufferedMutatorParams params = new BufferedMutatorParams(TableName.valueOf(tableName));
        try(BufferedMutator mutator = conn.getBufferedMutator(params)){
            Put put = new Put(Bytes.toBytes(rowkey));
            put.addColumn(Bytes.toBytes(cf),Bytes.toBytes(col),value);
            mutator.mutate(put);
            mutator.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return SysConsts.SUCCESS;
    }

    @Override
    public boolean delete(String tableName, String rowkey) {
        HTable table = null;
        long length = 0;
        try {
            table = (HTable)conn.getTable(TableName.valueOf(tableName));
            Delete delete = new Delete(rowkey.getBytes()); // 根据主键查询
            table.delete(delete);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                table.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean isExists(String tableName, String rowkey){
        HTable table = null;
        long length = 0;
        try {
            table = (HTable)conn.getTable(TableName.valueOf(tableName));
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
        return length>0;
    }

}
