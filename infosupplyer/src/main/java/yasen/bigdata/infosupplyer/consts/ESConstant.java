package yasen.bigdata.infosupplyer.consts;

/**
 * @Title: ESConstant.java
 * @Package yasen.bigdata.infosupplyer.conf
 * @Description: 全局常量
 * @author weiguangwu
 * @date  2018/4/23 14:13
 * @version V1.0
 */

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ESConstant {

    //ES中存储的key值
    /*********ES中字段***************/
//    public static String ID_ES_DCM = "id";//一个dicom文件的id，使用seriesuid+做CRC(用户名年龄性别)+图片序号，生成唯一id
//    public static String HOSPITAL = "hospital";//医院
//    public static String MRISEQ = "mriseq";//MRI序列
//    public static String MANUFACTURER = "manufacturer";//设备厂商
//    public static String DEVICENAME = "devicename";//设备名称
//    public static String PATIENTID = "patientid";//病人id
//    public static String NAME = "name";//病人姓名
//    public static String SEX = "sex";//性别
//    public static String AGE = "age";//年龄
//    public static String STUDYDATE = "studydate";//检查日期PatientUID
//    public static String SERIESUID = "seriesuid";//序列号
//    public static String ORGAN_ES_DCM = "organ";//器官
//    public static String ENTRYDATE_ES_DCM = "entrydate";//录入日期
//    public static String PICCOUNT = "piccount"; //病人此批次图片总量
//    public static String PICSERIALNUMBER = "picserialnumber";//图片序号
//    public static String BATCHSEQ = "batchseq"; //批次序号,patientid+CRC32(seriesuid)%1000000
//    public static String SUFFIX = "suffix"; //后缀，可以为空
//    public static String HDFSPATH_ES = "hdfspath";//hdfs文件路径

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


    /*********************ES中dicom类型存储的字段***************************/
    public static String MediaStorageSOPClassUID_ES_DCM	  = "MediaStorageSOPClassUID";
    public static String MediaStorageSOPInstUID_ES_DCM       = "MediaStorageSOPInstUID";
    public static String TransferSyntaxUID_ES_DCM            = "TransferSyntaxUID";
    public static String ImplementationClassUID_ES_DCM       = "ImplementationClassUID";
    public static String ImplementationVersionName_ES_DCM	  = "ImplementationVersionName";
    public static String SourceApplicationEntityTitle_ES_DCM = "SourceApplicationEntityTit";
    public static String SpecificCharacterSet_ES_DCM         = "SpecificCharacterSet";
    public static String ImageType_ES_DCM                    = "ImageType";
    public static String InstanceCreationDate_ES_DCM         = "InstanceCreationDate";
    public static String InstanceCreationTime_ES_DCM         = "InstanceCreationTime";
    public static String InstanceCreatorUID_ES_DCM           = "InstanceCreatorUID";
    public static String SOPClassUID_ES_DCM                  = "SOPClassUID";
    //    public static String SOPInstanceUID_ES_DCM               = "SOPInstanceUID"; //一个序列为单位，不需要单张图片标识号
    public static String StudyDate_ES_DCM                    = "StudyDate";
    public static String SeriesDate_ES_DCM = "SeriesDate";
    public static String AcquisitionDate_ES_DCM              = "AcquisitionDate";
    public static String ImageDate_ES_DCM                    = "ImageDate";
    public static String StudyTime_ES_DCM                    = "StudyTime";
    public static String SeriesTime_ES_DCM                   = "SeriesTime";
    public static String AcquisitionTime_ES_DCM = "AcquisitionTime";
    public static String ImageTime_ES_DCM = "ImageTime";
    public static String AccessionNumber_ES_DCM              = "AccessionNumber";
    public static String Modality_ES_DCM                     = "Modality";
    public static String Manufacturer_ES_DCM                 = "Manufacturer";
    public static String InstitutionName_ES_DCM = "InstitutionName";
    public static String ReferringPhysiciansName_ES_DCM     = "ReferringPhysiciansName";
    public static String TimezoneOffsetFromUTC_ES_DCM        = "TimezoneOffsetFromUTC";
    public static String StationName_ES_DCM                  = "StationName";
    public static String StudyDescription_ES_DCM             = "StudyDescription";
    public static String SeriesDescription_ES_DCM = "SeriesDescription";
    public static String ManufacturersModelName_ES_DCM = "ManufacturersModelName";
    public static String ReferencedSOPClassUID_ES_DCM        = "ReferencedSOPClassUID";
    public static String ReferencedSOPInstanceUID_ES_DCM     = "ReferencedSOPInstanceUID";
    public static String PatientName_ES_DCM = "PatientName";
    public static String PatientID_ES_DCM                    = "PatientID";
    public static String PatientsBirthDate_ES_DCM            = "PatientsBirthDate";
    public static String PatientsSex_ES_DCM = "PatientsSex";
    public static String PatientsAge_ES_DCM = "PatientsAge";
    public static String PatientsSize_ES_DCM = "PatientsSize";
    public static String PatientsWeight_ES_DCM = "PatientsWeight";
    public static String SliceThickness_ES_DCM = "SliceThickness";
    public static String SoftwareVersions_ES_DCM             = "SoftwareVersions";
    public static String ReconstructionDiameter_ES_DCM = "ReconstructionDiameter";
    public static String GantryDetectorTilt_ES_DCM           = "GantryDetectorTilt";
    public static String FieldOfViewShape_ES_DCM             = "FieldOfViewShape";
    public static String FieldOfViewDimensions_ES_DCM        = "FieldOfViewDimensions";
    public static String CollimatorType_ES_DCM               = "CollimatorType";
    public static String ConvolutionKernel_ES_DCM            = "ConvolutionKernel";
    public static String ActualFrameDuration_ES_DCM          = "ActualFrameDuration";
    public static String PatientPosition_ES_DCM              = "PatientPosition";
    public static String StudyInstanceUID_ES_DCM = "StudyInstanceUID";
    public static String SeriesInstanceUID_ES_DCM = "SeriesInstanceUID";
    public static String StudyID_ES_DCM = "StudyID";
    public static String SeriesNumber_ES_DCM = "SeriesNumber";
    public static String ImagePositionPatient_ES_DCM         = "ImagePositionPatient";
    public static String ImageOrientationPatient_ES_DCM      = "ImageOrientationPatient";
    public static String FrameOfReferenceUID_ES_DCM         = "Frameof ReferenceUID";
    public static String PositionReferenceIndicator_ES_DCM   = "PositionReferenceIndicator";
    public static String SliceLocation_ES_DCM = "SliceLocation";

    public static String ID_ES_DCM = "id";//一个索引的_id

    public static String ImageNumber_ES_DCM = "ImageNumber";//图片在该序列中序号

    public static String SeriesUID_ES_DCM = "SeriesUID";//一个dicom文件的id，使用seriesuid+做CRC(用户名年龄性别)+图片序号，生成唯一id
    public static String PatientUID_ES_DCM = "PatientUID"; //医院8位PatientID+姓名拼音+出生日期+性别(F/M)最后求MD5,需要在多医院中唯一
    public static String NumberOfSlices_ES_DCM = "NumberOfSlices"; //病人此序列图片总量
    public static String ORGAN_ES_DCM = "organ";//器官
    public static String ENTRYDATE_ES_DCM = "entrydate";//录入日期
    public static String HDFSPATH_ES_DCM = "hdfspath";//hdfs文件路径
    public static String ROWKEY_ES_DCM =  "rowkey"; //dicom序列的hbase元数据表rowkey
    public static String TAG_ES_DCM =  "tag"; //给一批同类别的dicom人为打上一个标签


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


}
