package yasen.bigdata.milk.consts;
/**
 * @Title: ESConstants.java
 * @Package yasen.bigdata.milk.conf
 * @Description: 该类包含与ES相关的一些常量
 * @author weiguangwu
 * @date  2018/4/23 14:13
 * @version V1.0
 */

public class ESConstants {

    /*********************ES中edf类型存储的字段***************************/
    public static String PatientUID_ES_ELECTRIC = "PatientUID"; //医院8位PatientID+姓名拼音+出生日期+性别(F/M)最后求MD5,需要在多医院中唯一
    public static String PatientName_ES_ELECTRIC = "PatientName";
    public static String PatientsAge_ES_ELECTRIC = "PatientsAge";
    public static String CreateDate_ES_ELECTRIC = "createdate";
    public static String InstitutionName_ES_ELECTRIC = "InstitutionName";
    public static String ENTRYDATE_ES_ELECTRIC = "entrydate";//录入日期
    public static String HDFSPATH_ES_ELECTRIC = "hdfspath";//hdfs文件路径

    /*********************ES中存储的字段***************************/
    public static String MediaStorageSOPClassUID_ES	  = "MediaStorageSOPClassUID";
    public static String MediaStorageSOPInstanceUID_ES       = "MediaStorageSOPInstanceUID";
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
    public static String ContentDate_ES                    = "ContentDate";
    public static String StudyTime_ES                    = "StudyTime";
    public static String SeriesTime_ES                   = "SeriesTime";
    public static String AcquisitionTime_ES              = "AcquisitionTime";
    public static String ContentTime_ES                    = "ContentTime";
    public static String AccessionNumber_ES              = "AccessionNumber";
    public static String Modality_ES                     = "Modality";
    public static String Manufacturer_ES                 = "Manufacturer";
    public static String InstitutionName_ES = "InstitutionName";
    public static String ReferringPhysicianName_ES     = "ReferringPhysicianName";
    public static String TimezoneOffsetFromUTC_ES        = "TimezoneOffsetFromUTC";
    public static String StationName_ES                  = "StationName";
    public static String StudyDescription_ES             = "StudyDescription";
    public static String SeriesDescription_ES = "SeriesDescription";
    public static String ManufacturerModelName_ES = "ManufacturerModelName";
    public static String ReferencedSOPClassUID_ES        = "ReferencedSOPClassUID";
    public static String ReferencedSOPInstanceUID_ES     = "ReferencedSOPInstanceUID";
    public static String PatientName_ES = "PatientName";
    public static String PatientID_ES                    = "PatientID";
    public static String PatientBirthDate_ES            = "PatientBirthDate";
    public static String PatientsSex_ES = "PatientSex";
    public static String PatientsAge_ES = "PatientAge";
    public static String PatientsSize_ES                 = "PatientSize";
    public static String PatientsWeight_ES               = "PatientWeight";
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
    public static String FrameOfReferenceUID_ES         = "FrameOfReferenceUID";
    public static String PositionReferenceIndicator_ES   = "PositionReferenceIndicator";
    public static String SliceLocation_ES                = "SliceLocation";

    public static String ID_ES = "id";//一个索引的_id

    public static String ImageNumber_ES = "ImageNumber";//图片在该序列中序号

    public static String SeriesUID_ES = "SeriesUID";//一个dicom文件的id，使用seriesuid+做CRC(用户名年龄性别)+图片序号，生成唯一id
    public static String PatientUID_ES = "PatientUID"; //医院8位PatientID+姓名拼音+出生日期+性别(F/M)最后求MD5,需要在多医院中唯一
    public static String NumberOfSlices_ES = "NumberOfSlices"; //病人此序列图片总量
    public static String ENTRYDATE_ES = "entrydate";//录入日期
    public static String HDFSPATH = "hdfspath";//hdfs文件路径
    public static String ROWKEY =  "rowkey"; //dicom序列的hbase元数据表rowkey
    public static String TAG_ES =  "tag"; //给一批具有相同属性的数据打上一个标签


}
