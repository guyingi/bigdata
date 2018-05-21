package yasen.bigdata.infosupplyer.consts;

public class SysConstants {

    public static String LINE = "-";
    public static String LEFT_SLASH = "/";
    public static String RIGHT_SLASH = "\\";
    public static String OS_NAME = "os.name";
    public static String WINDOWS = "Windows";
    public static String DICOM_TEMP = "dicomtemp";
    public static String DESENSITIZE_TEMP = "desensitizetemp";


    public static Integer SUCCESS = 0;
    public static Integer EXISTS = 1;
    public static Integer FAILED = -1;

    /***************字段名称常量******************/
    public static String SEARCH_CONDITION = "searchcondition";
    public static String BACKFIELDS = "backfields";
    public static String SORTFIELDS = "sortfields";
    public static String CODE = "code";
    public static String PAGECONTENT = "pagecount";
    public static String TOTAL = "total";
    public static String DATA = "data";
    public static String ERROR = "error";
    public static String INTERFACE = "interface";
    public static String MSG = "msg";


    /****************其他常量字符串*******************/
    public static String RECORD_TOTAL = "recordtotal";
    public static Integer DEFAULT_PAGESIZE = 1000;

    /****************返回码常量*******************/
    public static String CODE_000 = "000";  //查询成功
    public static String CODE_010 = "010";  //查询条件参数为空
    public static String CODE_011 = "011";  //参数解析错误

    /***************查询条件中的字段******************/
    public static String DEVICE_PARAM = "device";
    public static String ORGAN_PARAM = "organ";
    public static String SERIES_DESCRIPTION_PARAM = "seriesdescription";
    public static String INSTITUTION_PARAM = "institution";
    public static String SEX_PARAM = "sex";
    public static String AGE_START_PARAM = "age_start";
    public static String AGE_END_PARAM = "age_end";
    public static String STUDYDATE_START_PARAM = "studydate_start";
    public static String STUDYDATE_END_PARAM = "studydate_end";
    public static String ENTRYDATE_START_PARAM = "entrydate_start";
    public static String ENTRYDATE_END_PARAM = "entrydate_end";
    public static String IMAGECOUNT_MIN_PARAM = "imagecount_min";
    public static String IMAGECOUNT_MAX_PARAM = "imagecount_max";
    public static String SLICE_THICKNESS_MIN_PARAM = "slicethickness_min";
    public static String SLICE_THICKNESS_MAX_PARAM = "slicethickness_max";

    public static String PAGE_ID = "pageid";
    public static String PAGE_SIZE = "pagesize";

    public static String TAG_PARAM = "TAG";

    /***************ES配置信息的字段******************/

    public static String DIR_PREFIX_DICOM = "dir.prefix.dicom";
    public static String DIR_PREFIX_DESENSITIZATION = "dir.prefix.desensitization";
    public static String ES_CLUSTER = "es.cluster";
    public static String ES_IP = "es.ip";
    public static String ES_HOST = "es.host";
    public static String ES_TCPPORT = "es.tcpport";
    public static String ES_HTTPPORT = "es.httpport";
    public static String ES_DICOM_INDEX = "es.dicom.index";
    public static String ES_DESENSITIZATION_INDEX = "es.disensitization.index";
    public static String ES_DICOM_TYPE = "es.dicom.type";
    public static String ES_DESENSITIZATION_TYPE = "es.disensitization.type";

    public static String DICOM_THUMBNAIL_TABLENAME = "dicom.thumbnail.tablename";
    public static String DICOM_THUMBNAIL_CF = "dicom.thumbnail.cf";
    public static String DICOM_TABLENAME = "dicom.tablename";
    public static String DICOM_CF = "dicom.cf";
    public static String DICOM_DESENSITIZATION_TABLENAME = "dicom.disensitization.tablename";
    public static String DICOM_DESENSITIZATION_CF = "dicom.disensitization.cf";



    /**************HBase表列簇字段****************************/
    public static String THUMBNAIL = "thumbnail";
    public static String TAG = "tag"; //脱敏数据的表中使用该字段
    public static String CREATE_DATE = "createdate"; //kfb元数据字段中有使用
    public static String BARCODE = "barcode"; //kfb元数据字段中有使用



}
