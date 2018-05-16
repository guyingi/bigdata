package yasen.bigdata.infosupplyer.service.impl;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.stereotype.Service;
import yasen.bigdata.infosupplyer.conf.InfosupplyerConfiguration;
import yasen.bigdata.infosupplyer.consts.SysConstants;
import yasen.bigdata.infosupplyer.service.HBaseService;
import yasen.bigdata.infosupplyer.util.HBaseUtil;
import yasen.bigdata.infosupplyer.util.InfoSupplyerTool;
import yasen.bigdata.infosupplyer.util.ZipUtil;

import java.io.File;
import java.io.FileOutputStream;

@Service("HBaseService")
public class HBaseServiceImpl implements HBaseService {

    Connection conn = null;
    InfosupplyerConfiguration infosupplyerConfiguration = null;

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

        File tempDir = new File(path+rowkey.substring(0,10));
        if(!tempDir.exists()){
            tempDir.mkdir();
        }
        FileOutputStream fout = null;

        ResultScanner scanner = table.getScanner(scan);
        for(Result result : scanner){
            Cell[] cells = result.rawCells();
            for(Cell cell : cells){
                String tempRowkey = Bytes.toString(CellUtil.cloneRow(cell));
                String qualify = Bytes.toString(CellUtil.cloneQualifier(cell));
                if(qualify.equals(SysConstants.THUMBNAIL)){
                    byte[] temp = CellUtil.cloneValue(cell);
                    String filename = tempRowkey.substring(tempRowkey.length()-6,tempRowkey.length());
                    fout = new FileOutputStream(new File(tempDir.getAbsolutePath()+InfoSupplyerTool.getDelimiter()+filename+".jpg"));
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
        tempDir.deleteOnExit();
        return path+File.separator+filename;
    }
}
