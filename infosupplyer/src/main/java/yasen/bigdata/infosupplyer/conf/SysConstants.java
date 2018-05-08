package yasen.bigdata.infosupplyer.conf;

public class SysConstants {
    /***************字段名称常量******************/
    public static String SEARCH_CONDITION = "searchcondition";
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
    public static String CODE_000 = "000";  //查询条件参数为空
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
    public static String PAGE_ID = "pageid";
    public static String PAGE_SIZE = "pagesize";

    /***************ES配置信息的字段******************/
    public static String ES_CLUSTER = "es.cluster";
    public static String ES_IP = "es.ip";
    public static String ES_HOST = "es.host";
    public static String ES_TCPPORT = "es.tcpport";
    public static String ES_HTTPPORT = "es.httpport";
    public static String ES_INDEX = "es.index";
    public static String ES_TYPE = "es.type";






}
