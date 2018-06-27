package qed.bigdata.es.consts;

/**
 * @Title: SysConsts.java
 * @Package yasen.bigdata.es.conf
 * @Description: 该类包含milk工程相关常量
 * @author weiguangwu
 * @date  2018/4/23 14:13
 * @version V1.0
 */

public class SysConsts {

    public static String LINE = "-";
    public static String UNDERLINE = "_";
    public static String LEFT_SLASH = "/";
    public static String RIGHT_SLASH = "\\";
    public static String OS_NAME = "os.name";
    public static String WINDOWS = "Windows";
    public static String INFOSUPPLYER_IP = "infosupplyer.ip";
    public static String INFOSUPPLYER_PORT = "infosupplyer.port";
    public static String FS__DEFAULTFS = "fs.defaultFS";
    public static String DOWNLOAD_THRESHHOLD = "downlaoddata.threshold";


    public static String HTTP_HEAD = "http://";

    public static String TEMP_STRING = "temp";



    /**********************分页查询默认每页显示数量***************************/
    public static Integer DEFAULT_PAGE_SIZE = 25;

    /*******************页面参数名字*********************/
    public static String DEVICE_PAGEPARAMNAME = "device";
    public static String ORGAN_PAGEPARAMNAME = "organ";
    public static String SERIES_DESCRIPTION_PAGEPARAMNAME = "seriesdescription";
    public static String INSTITUTION_PAGEPARAMNAME = "institution";
    public static String SES_PAGEPARAMNAME = "sex";
    public static String AGE_START_PAGEPARAMNAME = "age_start";
    public static String AGE_END_PAGEPARAMNAME = "age_end";
    public static String STUDYDATE_START_PAGEPARAMNAME = "studydate_start";
    public static String STUDYDATE_END_PAGEPARAMNAME = "studydate_end";
    public static String ENTRYDATE_START_PAGEPARAMNAME = "entrydate_start";
    public static String ENTRYDATE_END_PAGEPARAMNAME = "entrydate_end";
    public static String IMAGECOUNT_MIN_PAGEPARAMNAME = "imagecount_min";
    public static String IMAGECOUNT_MAX_PAGEPARAMNAME = "imagecount_max";
    public static String SLICE_THICKNESS_MIN_PAGEPARAMNAME = "slicethickness_min";
    public static String SLICE_THICKNESS_MAX_PAGEPARAMNAME= "slicethickness_max";
    public static String PAGEID_PAGEPARAMNAME = "pageid";
    public static String PAGESIZE_PAGEPARAMNAME = "pagesize";

    /***************页面datagrid字段************************/
//    public static String DESCRIBE = "describe";
//    public static String ORGAN = "organ";
//    public static String COUNT = "count";

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
    public static String PATIENTNAME_PARAM = "patientname";

    /***************参数与返回值中的一些key名称******************/
    public static String CRITERIA = "criteria";
    public static String SECTION = "section";
    public static String START = "start";
    public static String END = "end";
    public static String VALUE = "value";
    public static String KEYWORD = "keyword";
    public static String YES = "yes";
    public static String NO = "no";

    public static String PAGE_ID = "pageid";
    public static String PAGE_SIZE = "pagesize";
    public static String BACKFIELDS = "backfields";
    public static String SORTFIELDS = "sortfields";
    public static String DATATYPE = "datatype";
    public static String TYPE_DICOM = "dicom";
    public static String TYPE_ELECTRIC = "electric";
    public static String TYPE_GUAGE = "guage";
    public static String TYPE_KFB = "kfb";

    public static String IDS = "ids";
    public static String DATA = "data";
    public static String CODE = "code";
    public static String TOTAL = "total";
    public static String ROWS = "rows";
    public static String TAG = "tag";
    public static String RESULT = "result";


    /****************编码*******************************/
    public static String CODE_000 = "000";  //成功
    public static String CODE_999 = "999";  //失败


    /*******************需要转换日期格式为-的参数***************************/
    public static String StudyDate = "";
    public static String entrydate = "";


}
