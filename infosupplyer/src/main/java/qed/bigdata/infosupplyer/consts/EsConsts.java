package qed.bigdata.infosupplyer.consts;

/**
 * @Title: EsConsts.java
 * @Package yasen.bigdata.infosupplyer.conf
 * @Description: 全局常量
 * @author weiguangwu
 * @date  2018/4/23 14:13
 * @version V1.0
 */

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class EsConsts {


    /*********ES中字段***************/
    public static String StudyID_ES_DCM	                = "StudyID";
    public static String PatientAge_ES_DCM		 		= "PatientAge";
    public static String SeriesNumber_ES_DCM	        = "SeriesNumber";
    public static String NumberOfSlices_ES_DCM	        = "NumberOfSlices";
    public static String StudyTime_ES_DCM	            = "StudyTime";
    public static String SeriesTime_ES_DCM	            = "SeriesTime";
    public static String AcquisitionTime_ES_DCM	        = "AcquisitionTime";
    public static String ContentTime_ES_DCM	            = "ContentTime";
    public static String PatientSize_ES_DCM	            = "PatientSize";
    public static String PatientWeight_ES_DCM	        = "PatientWeight";
    public static String SliceThickness_ES_DCM	        = "SliceThickness";
    public static String ReconstructionDiameter_ES_DCM	= "ReconstructionDiameter";
    public static String SliceLocation_ES_DCM	        = "SliceLocation";
    public static String StudyInstanceUID_ES_DCM		= "StudyInstanceUID";
    public static String SeriesInstanceUID_ES_DCM		= "SeriesInstanceUID";
    public static String InstitutionName_ES_DCM			= "InstitutionName";
    public static String ManufacturerModelName_ES_DCM	= "ManufacturerModelName";
    public static String SeriesDescription_ES_DCM	    = "SeriesDescription";
    public static String SeriesDate_ES_DCM	            = "SeriesDate";
    public static String PatientSex_ES_DCM	            = "PatientSex";
    public static String PatientName_ES_DCM		 		= "PatientName";

    /*********ES 中字段，自定义通用***************/
    public static String ID         = "id";         //一个索引的_id
    public static String ROWKEY		= "rowkey";     //dicom序列的hbase元数据表rowkey
    public static String PatientUID = "PatientUID";  //医院8位PatientID+姓名拼音+出生日期+性别(F/M)最后求MD5,需要在多医院中唯一
    public static String SeriesUID  = "SeriesUID";
    public static String HDFSPATH   = "hdfspath";   //hdfs文件路径
    public static String ENTRYDATE  = "entrydate";  //录入日期
    public static String TAG        = "tag";       //给一批同类别的dicom人为打上一个标签


    /*********************ES中edf类型存储的字段***************************/
    public static String PatientUID_ES_ELECTRIC = "PatientUID"; //医院8位PatientID+姓名拼音+出生日期+性别(F/M)最后求MD5,需要在多医院中唯一
    public static String PatientName_ES_ELECTRIC = "PatientName";
    public static String PatientsAge_ES_ELECTRIC = "PatientsAge";
    public static String CreateDate_ES_ELECTRIC = "createdate";
    public static String InstitutionName_ES_ELECTRIC = "InstitutionName";
    public static String ENTRYDATE_ES_ELECTRIC = "entrydate";//录入日期
    public static String HDFSPATH_ES_ELECTRIC = "hdfspath";//hdfs文件路径

    /****************查询电信号数据返回字段集合************************/
    public static HashSet<String> ES_ELECTRIC_FIELD = new HashSet<String>();
    static {
        ES_ELECTRIC_FIELD.add("PatientUID");
        ES_ELECTRIC_FIELD.add("id");
        ES_ELECTRIC_FIELD.add("PatientName");
        ES_ELECTRIC_FIELD.add("PatientsAge");
        ES_ELECTRIC_FIELD.add("createdate");
        ES_ELECTRIC_FIELD.add("InstitutionName");
        ES_ELECTRIC_FIELD.add("entrydate");
        ES_ELECTRIC_FIELD.add("hdfspath");
    }
    /***************查询电信号默认返回字段****************/
    public static List<String> ELECTRIC_DEFAULT_BACK_FIELD = new ArrayList<String>();
    static{
        ELECTRIC_DEFAULT_BACK_FIELD.add("SeriesUID");
        ELECTRIC_DEFAULT_BACK_FIELD.add("InstitutionName");
        ELECTRIC_DEFAULT_BACK_FIELD.add("organ");
        ELECTRIC_DEFAULT_BACK_FIELD.add("SeriesDescription");
        ELECTRIC_DEFAULT_BACK_FIELD.add("SeriesDate");
        ELECTRIC_DEFAULT_BACK_FIELD.add("PatientName");
        ELECTRIC_DEFAULT_BACK_FIELD.add("NumberOfSlices");
        ELECTRIC_DEFAULT_BACK_FIELD.add("tag");
    }


    /*******************字段map**********************/
    /**********查询dicom参数中的字段必须要是如下字段的子集**********/
    public static HashSet<String> ES_DCM_FIELD = new HashSet<String>();
    static{
        ES_DCM_FIELD.add("MediaStorageSOPClassUID");
        ES_DCM_FIELD.add("MediaStorageSOPInstUID");
        ES_DCM_FIELD.add("TransferSyntaxUID");
        ES_DCM_FIELD.add("ImplementationClassUID");
        ES_DCM_FIELD.add("ImplementationVersionName");
        ES_DCM_FIELD.add("SourceApplicationEntityTit");
        ES_DCM_FIELD.add("SpecificCharacterSet");
        ES_DCM_FIELD.add("ImageType");
        ES_DCM_FIELD.add("InstanceCreationDate");
        ES_DCM_FIELD.add("InstanceCreationTime");
        ES_DCM_FIELD.add("InstanceCreatorUID");
        ES_DCM_FIELD.add("SOPClassUID");
        ES_DCM_FIELD.add("StudyDate");
        ES_DCM_FIELD.add("SeriesDate");
        ES_DCM_FIELD.add("AcquisitionDate");
        ES_DCM_FIELD.add("ImageDate");
        ES_DCM_FIELD.add("StudyTime");
        ES_DCM_FIELD.add("SeriesTime");
        ES_DCM_FIELD.add("AcquisitionTime");
        ES_DCM_FIELD.add("ImageTime");
        ES_DCM_FIELD.add("AccessionNumber");
        ES_DCM_FIELD.add("Modality");
        ES_DCM_FIELD.add("Manufacturer");
        ES_DCM_FIELD.add("InstitutionName");
        ES_DCM_FIELD.add("ReferringPhysiciansName");
        ES_DCM_FIELD.add("TimezoneOffsetFromUTC");
        ES_DCM_FIELD.add("StationName");
        ES_DCM_FIELD.add("StudyDescription");
        ES_DCM_FIELD.add("SeriesDescription");
        ES_DCM_FIELD.add("ManufacturersModelName");
        ES_DCM_FIELD.add("ReferencedSOPClassUID");
        ES_DCM_FIELD.add("ReferencedSOPInstanceUID");
        ES_DCM_FIELD.add("PatientName");
        ES_DCM_FIELD.add("PatientID");
        ES_DCM_FIELD.add("PatientsBirthDate");
        ES_DCM_FIELD.add("PatientsSex");
        ES_DCM_FIELD.add("PatientsAge");
        ES_DCM_FIELD.add("PatientsSize");
        ES_DCM_FIELD.add("PatientsWeight");
        ES_DCM_FIELD.add("SliceThickness");
        ES_DCM_FIELD.add("SoftwareVersions");
        ES_DCM_FIELD.add("ReconstructionDiameter");
        ES_DCM_FIELD.add("GantryDetectorTilt");
        ES_DCM_FIELD.add("FieldOfViewShape");
        ES_DCM_FIELD.add("FieldOfViewDimensions");
        ES_DCM_FIELD.add("CollimatorType");
        ES_DCM_FIELD.add("ConvolutionKernel");
        ES_DCM_FIELD.add("ActualFrameDuration");
        ES_DCM_FIELD.add("PatientPosition");
        ES_DCM_FIELD.add("StudyInstanceUID");
        ES_DCM_FIELD.add("SeriesInstanceUID");
        ES_DCM_FIELD.add("StudyID");
        ES_DCM_FIELD.add("SeriesNumber");
        ES_DCM_FIELD.add("ImagePositionPatient");
        ES_DCM_FIELD.add("ImageOrientationPatient");
        ES_DCM_FIELD.add("rameofReferenceUID");
        ES_DCM_FIELD.add("PositionReferenceIndicator");
        ES_DCM_FIELD.add("SliceLocation");
        ES_DCM_FIELD.add("id");
        ES_DCM_FIELD.add("SeriesUID");
        ES_DCM_FIELD.add("PatientUID");
        ES_DCM_FIELD.add("NumberOfSlices");
        ES_DCM_FIELD.add("organ");
        ES_DCM_FIELD.add("entrydate");
        ES_DCM_FIELD.add("hdfspath");
        ES_DCM_FIELD.add("rowkey");
        ES_DCM_FIELD.add("tag");
    }




    /*********************ES中的文本排序需要拼接子field的字段**********************/
    public static HashSet<String> DCM_ES_TEXT_SORT_CHILD_FIELD = new HashSet<String>();
    static{
        DCM_ES_TEXT_SORT_CHILD_FIELD.add("InstitutionName");
        DCM_ES_TEXT_SORT_CHILD_FIELD.add("organ");
        DCM_ES_TEXT_SORT_CHILD_FIELD.add("PatientName");
        DCM_ES_TEXT_SORT_CHILD_FIELD.add("SeriesDescription");
        DCM_ES_TEXT_SORT_CHILD_FIELD.add("tag");
    }

    /***************查询dicom默认返回字段****************/
    public static List<String> DCM_DEFAULT_BACK_FIELD = new ArrayList<String>();
    static{
        DCM_DEFAULT_BACK_FIELD.add("SeriesUID");
        DCM_DEFAULT_BACK_FIELD.add("InstitutionName");
        DCM_DEFAULT_BACK_FIELD.add("organ");
        DCM_DEFAULT_BACK_FIELD.add("SeriesDescription");
        DCM_DEFAULT_BACK_FIELD.add("SeriesDate");
        DCM_DEFAULT_BACK_FIELD.add("PatientName");
        DCM_DEFAULT_BACK_FIELD.add("NumberOfSlices");
        DCM_DEFAULT_BACK_FIELD.add("tag");
    }

    /***************接收参数中的字段****************/
    public static String BACKFIELDS = "backfields";
    public static String IDS = "ids";
    public static String DATA = "data";


    /**************多个器官名称******************/
    public static String BREAST = "breast";
    public static String LUNG = "lung";
    public static String BRAIN = "brain";


    /**********dicom 元数据tag-keyword对照表*******************/
    public static List<String> DCM_META_KEYWORD = new ArrayList<String>();

    /********************下面这个是dicom元数据标准**************************************/
    static{
        InputStreamReader reader = new InputStreamReader(EsConsts.class.getClassLoader().getResourceAsStream("DicomMetaElements.properties"));
        Properties props = new Properties();
        try {
            props.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Enumeration<?> enumeration = props.propertyNames();
        while(enumeration.hasMoreElements()){
            String name = (String)enumeration.nextElement();
            DCM_META_KEYWORD.add(props.getProperty(name));
        }
        //添加自定义字段
        DCM_META_KEYWORD.add(ID);
        DCM_META_KEYWORD.add(SeriesUID);
        DCM_META_KEYWORD.add(PatientUID);
        DCM_META_KEYWORD.add(HDFSPATH);
        DCM_META_KEYWORD.add(ROWKEY);
        DCM_META_KEYWORD.add(ENTRYDATE);
        DCM_META_KEYWORD.add(TAG);
    }


    /*********************ES中dicom类型存储的字段***************************/
    public static List<String> IntegerFieldList = new ArrayList<String>();
    public static List<String> LongFieldList = new ArrayList<String>();
    public static List<String> DoubleFieldList = new ArrayList<String>();
    static{
        IntegerFieldList.add("PatientAge");
        IntegerFieldList.add("SeriesNumber");
        IntegerFieldList.add("NumberOfSlices");

        LongFieldList.add("StudyTime");
        LongFieldList.add("SeriesTime");
        LongFieldList.add("AcquisitionTime");
        LongFieldList.add("ContentTime");

        DoubleFieldList.add("PatientSize");
        DoubleFieldList.add("PatientWeight");
        DoubleFieldList.add("SliceThickness");
        DoubleFieldList.add("ReconstructionDiameter");
        DoubleFieldList.add("SliceLocation");
    }




}
