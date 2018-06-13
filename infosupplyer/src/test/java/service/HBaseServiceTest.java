package service;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import qed.bigdata.infosupplyer.conf.InfosupplyerConfiguration;
import qed.bigdata.infosupplyer.consts.SysConsts;
import qed.bigdata.infosupplyer.util.HBaseUtil;
import qed.bigdata.infosupplyer.util.InfoSupplyerTool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package service
 * @Description: ${todo}
 * @date 2018/5/24 16:24
 */
public class HBaseServiceTest {
    public static void main(String[] args) throws IOException {
        InfosupplyerConfiguration infosupplyerConfiguration = new InfosupplyerConfiguration();
        Connection conn = HBaseUtil.getConnection();
        Table table = conn.getTable(TableName.valueOf(infosupplyerConfiguration.getDicomThumbnailTablename()));

        String rowkey = "2592930b48f9021e934bfa290737203";
        Scan scan  = new Scan();
        scan.setStartRow(Bytes.toBytes(rowkey+"0"));
        scan.setStopRow(Bytes.toBytes(rowkey+"9"));

        String path = "C:\\Users\\WeiGuangWu\\IdeaProjects\\bigdata\\es\\target\\es\\temp\\";
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
                if(qualify.equals(SysConsts.THUMBNAIL)){
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
//        ZipUtil.zip(tempDir.getAbsolutePath(),path,filename);
//        tempDir.deleteOnExit();
    }
}
