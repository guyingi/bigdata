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
//    public static String ID_ES = "id";//一个dicom文件的id，使用seriesuid+做CRC(用户名年龄性别)+图片序号，生成唯一id
//    public static String HOSPITAL = "hospital";//医院
//    public static String MRISEQ = "mriseq";//MRI序列
//    public static String MANUFACTURER = "manufacturer";//设备厂商
//    public static String DEVICENAME = "devicename";//设备名称
//    public static String PATIENTID = "patientid";//病人id
//    public static String NAME = "name";//病人姓名
//    public static String SEX = "sex";//性别
//    public static String AGE = "age";//年龄
//    public static String STUDYDATE = "studydate";//检查日期
//    public static String SERIESUID = "seriesuid";//序列号
//    public static String ORGAN_ES = "organ";//器官
//    public static String ENTRYDATE_ES = "entrydate";//录入日期
//    public static String PICCOUNT = "piccount"; //病人此批次图片总量
//    public static String PICSERIALNUMBER = "picserialnumber";//图片序号
//    public static String BATCHSEQ = "batchseq"; //批次序号,patientid+CRC32(seriesuid)%1000000
//    public static String SUFFIX = "suffix"; //后缀，可以为空
//    public static String HDFSPATH_ES = "hdfspath";//hdfs文件路径


    /*********************ES中存储的字段***************************/
    public static String MediaStorageSOPClassUID_ES	  = "MediaStorageSOPClassUID";
    public static String MediaStorageSOPInstUID_ES       = "MediaStorageSOPInstUID";
    public static String TransferSyntaxUID_ES            = "TransferSyntaxUID";
    public static String ImplementationClassUID_ES       = "ImplementationClassUID";
    public static String ImplementationVersionName_ES	  = "ImplementationVersionName";
    public static String SourceApplicationEntityTitle_ES = "SourceApplicationEntityTit";
    public static String SpecificCharacterSet_ES         = "SpecificCharacterSet";
    public static String ImageType_ES                    = "ImageType";
    public static String InstanceCreationDate_ES         = "InstanceCreationDate";
    public static String InstanceCreationTime_ES         = "InstanceCreationTime";
    public static String InstanceCreatorUID_ES           = "InstanceCreatorUID";
    public static String SOPClassUID_ES                  = "SOPClassUID";
    //    public static String SOPInstanceUID_ES               = "SOPInstanceUID"; //一个序列为单位，不需要单张图片标识号
    public static String StudyDate_ES                    = "StudyDate";
    public static String SeriesDate_ES = "SeriesDate";
    public static String AcquisitionDate_ES              = "AcquisitionDate";
    public static String ImageDate_ES                    = "ImageDate";
    public static String StudyTime_ES                    = "StudyTime";
    public static String SeriesTime_ES                   = "SeriesTime";
    public static String AcquisitionTime_ES              = "AcquisitionTime";
    public static String ImageTime_ES                    = "ImageTime";
    public static String AccessionNumber_ES              = "AccessionNumber";
    public static String Modality_ES                     = "Modality";
    public static String Manufacturer_ES                 = "Manufacturer";
    public static String InstitutionName_ES = "InstitutionName";
    public static String ReferringPhysiciansName_ES     = "ReferringPhysiciansName";
    public static String TimezoneOffsetFromUTC_ES        = "TimezoneOffsetFromUTC";
    public static String StationName_ES                  = "StationName";
    public static String StudyDescription_ES             = "StudyDescription";
    public static String SeriesDescription_ES = "SeriesDescription";
    public static String ManufacturersModelName_ES = "ManufacturersModelName";
    public static String ReferencedSOPClassUID_ES        = "ReferencedSOPClassUID";
    public static String ReferencedSOPInstanceUID_ES     = "ReferencedSOPInstanceUID";
    public static String PatientName_ES = "PatientName";
    public static String PatientID_ES                    = "PatientID";
    public static String PatientsBirthDate_ES            = "PatientsBirthDate";
    public static String PatientsSex_ES = "PatientsSex";
    public static String PatientsAge_ES = "PatientsAge";
    public static String PatientsSize_ES                 = "PatientsSize";
    public static String PatientsWeight_ES               = "PatientsWeight";
    public static String SliceThickness_ES               = "SliceThickness";
    public static String SoftwareVersions_ES             = "SoftwareVersions";
    public static String ReconstructionDiameter_ES       = "ReconstructionDiameter";
    public static String GantryDetectorTilt_ES           = "GantryDetectorTilt";
    public static String FieldOfViewShape_ES             = "FieldOfViewShape";
    public static String FieldOfViewDimensions_ES        = "FieldOfViewDimensions";
    public static String CollimatorType_ES               = "CollimatorType";
    public static String ConvolutionKernel_ES            = "ConvolutionKernel";
    public static String ActualFrameDuration_ES          = "ActualFrameDuration";
    public static String PatientPosition_ES              = "PatientPosition";
    public static String StudyInstanceUID_ES             = "StudyInstanceUID";
    public static String SeriesInstanceUID_ES            = "SeriesInstanceUID";
    public static String StudyID_ES                      = "StudyID";
    public static String SeriesNumber_ES                 = "SeriesNumber";
    public static String ImagePositionPatient_ES         = "ImagePositionPatient";
    public static String ImageOrientationPatient_ES      = "ImageOrientationPatient";
    public static String FrameOfReferenceUID_ES         = "Frameof ReferenceUID";
    public static String PositionReferenceIndicator_ES   = "PositionReferenceIndicator";
    public static String SliceLocation_ES                = "SliceLocation";

    public static String ID_ES = "id";//一个索引的_id

    public static String ImageNumber_ES = "ImageNumber";//图片在该序列中序号

    public static String SeriesUID_ES = "SeriesUID";//一个dicom文件的id，使用seriesuid+做CRC(用户名年龄性别)+图片序号，生成唯一id
    public static String PatientUID_ES = "PatientUID"; //医院8位PatientID+姓名拼音+出生日期+性别(F/M)最后求MD5,需要在多医院中唯一
    public static String NumberOfSlices_ES = "NumberOfSlices"; //病人此序列图片总量
    public static String ORGAN_ES = "organ";//器官
    public static String ENTRYDATE_ES = "entrydate";//录入日期
    public static String HDFSPATH = "hdfspath";//hdfs文件路径
    public static String ROWKEY =  "rowkey"; //dicom序列的hbase元数据表rowkey
    public static String TAG_ES =  "tag"; //给一批同类别的dicom人为打上一个标签


    /*******************字段map**********************/
    /**********参数中的字段必须要是如下字段的子集**********/
    public static HashSet<String> ESFIELD = new HashSet<String>();
    static{
        ESFIELD.add("MediaStorageSOPClassUID");
        ESFIELD.add("MediaStorageSOPInstUID");
        ESFIELD.add("TransferSyntaxUID");
        ESFIELD.add("ImplementationClassUID");
        ESFIELD.add("ImplementationVersionName");
        ESFIELD.add("SourceApplicationEntityTit");
        ESFIELD.add("SpecificCharacterSet");
        ESFIELD.add("ImageType");
        ESFIELD.add("InstanceCreationDate");
        ESFIELD.add("InstanceCreationTime");
        ESFIELD.add("InstanceCreatorUID");
        ESFIELD.add("SOPClassUID");
        ESFIELD.add("StudyDate");
        ESFIELD.add("SeriesDate");
        ESFIELD.add("AcquisitionDate");
        ESFIELD.add("ImageDate");
        ESFIELD.add("StudyTime");
        ESFIELD.add("SeriesTime");
        ESFIELD.add("AcquisitionTime");
        ESFIELD.add("ImageTime");
        ESFIELD.add("AccessionNumber");
        ESFIELD.add("Modality");
        ESFIELD.add("Manufacturer");
        ESFIELD.add("InstitutionName");
        ESFIELD.add("ReferringPhysiciansName");
        ESFIELD.add("TimezoneOffsetFromUTC");
        ESFIELD.add("StationName");
        ESFIELD.add("StudyDescription");
        ESFIELD.add("SeriesDescription");
        ESFIELD.add("ManufacturersModelName");
        ESFIELD.add("ReferencedSOPClassUID");
        ESFIELD.add("ReferencedSOPInstanceUID");
        ESFIELD.add("PatientName");
        ESFIELD.add("PatientID");
        ESFIELD.add("PatientsBirthDate");
        ESFIELD.add("PatientsSex");
        ESFIELD.add("PatientsAge");
        ESFIELD.add("PatientsSize");
        ESFIELD.add("PatientsWeight");
        ESFIELD.add("SliceThickness");
        ESFIELD.add("SoftwareVersions");
        ESFIELD.add("ReconstructionDiameter");
        ESFIELD.add("GantryDetectorTilt");
        ESFIELD.add("FieldOfViewShape");
        ESFIELD.add("FieldOfViewDimensions");
        ESFIELD.add("CollimatorType");
        ESFIELD.add("ConvolutionKernel");
        ESFIELD.add("ActualFrameDuration");
        ESFIELD.add("PatientPosition");
        ESFIELD.add("StudyInstanceUID");
        ESFIELD.add("SeriesInstanceUID");
        ESFIELD.add("StudyID");
        ESFIELD.add("SeriesNumber");
        ESFIELD.add("ImagePositionPatient");
        ESFIELD.add("ImageOrientationPatient");
        ESFIELD.add("rameofReferenceUID");
        ESFIELD.add("PositionReferenceIndicator");
        ESFIELD.add("SliceLocation");
        ESFIELD.add("id");
        ESFIELD.add("SeriesUID");
        ESFIELD.add("PatientUID");
        ESFIELD.add("NumberOfSlices");
        ESFIELD.add("organ");
        ESFIELD.add("entrydate");
        ESFIELD.add("hdfspath");
        ESFIELD.add("rowkey");
    }

    /*********************ES中的文本排序需要拼接子field的字段**********************/
    public static HashSet<String> ESTEXT_SORT_CHILD_FIELD = new HashSet<String>();
    static{
        ESTEXT_SORT_CHILD_FIELD.add("InstitutionName");
        ESTEXT_SORT_CHILD_FIELD.add("organ");
        ESTEXT_SORT_CHILD_FIELD.add("PatientName");
        ESTEXT_SORT_CHILD_FIELD.add("SeriesDescription");
    }

    /***************默认返回字段****************/
    public static List<String> DEFAULT_BACK_FIELD = new ArrayList<String>();
    static{
        DEFAULT_BACK_FIELD.add("SeriesUID");
        DEFAULT_BACK_FIELD.add("InstitutionName");
        DEFAULT_BACK_FIELD.add("organ");
        DEFAULT_BACK_FIELD.add("SeriesDescription");
        DEFAULT_BACK_FIELD.add("SeriesDate");
        DEFAULT_BACK_FIELD.add("PatientName");
        DEFAULT_BACK_FIELD.add("NumberOfSlices");
    }

    /***************接收参数中的字段****************/
    public static String BACKFIELDS = "backfields";
    public static String IDS = "ids";
    public static String DATA = "data";


    /**************多个器官名称******************/
    public static String BREAST = "breast";
    public static String LUNG = "lung";


}
