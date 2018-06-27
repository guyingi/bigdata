package qed.bigdata.infosupplyer.consts;

public class SysConsts {

    public static String LINE = "-";
    public static String UNDER_LINE = "_";
    public static String LEFT_SLASH = "/";
    public static String RIGHT_SLASH = "\\";
    public static String SPACE = " ";
    public static String OS_NAME = "os.name";
    public static String WINDOWS = "Windows";
    public static String TEMP_DIRNAME = "temp";
    public static String DICOM_TEMP_DIRNAME = "dicomtemp";  //脱敏操作中临时存放dicom文件的目录
    public static String DESENSITIZE_TEMP_DIRNAME = "desensitizetemp"; //脱敏操作临时存放脱敏后的数据的目录
    public static String DESENSITIZE_BEFORE_TEMP_DIRNAME = "desensitizebeforetemp"; //脱敏操作临时存放脱敏后的数据的目录
    public static String DESENSITIZE_AFTER_TEMP_DIRNAME = "desensitizeaftertemp"; //脱敏操作临时存放脱敏后的数据的目录
    public static String DESENSITIZE_COMBINE_TEMP_DIRNAME = "desensitizecombinetemp"; //脱敏操作临时存放raw,mhd,csv的目录
    public static String DESENSITIZE_TDOWNLOAD_TEMP_DIRNAME = "desensitizedownloadtemp"; //下载脱敏数据，临时存放脱敏数据的目录，因为以tag为单位下载
    public static String THUMBNAIL_TEMP_DIRNAME = "thumbnailtemp"; //下载脱敏数据，临时存放脱敏数据的目录，因为以tag为单位下载
    public static String ELECTRIC_TEMP_DIRNAME = "electrictemp"; //下载脱敏数据，临时存放脱敏数据的目录，因为以tag为单位下载



    public static Integer SUCCESS = 0;
    public static Integer EXISTS = 1;
    public static Integer FAILED = -1;

    /***************字段名称常量******************/
    public static String DATATYPE = "datatype";
    public static String PAGE_ID = "pageid";
    public static String PAGE_SIZE = "pagesize";
    public static String BACKFIELDS = "backfields";
    public static String SORTFIELDS = "sortfields";
    public static String IDS = "ids";
    public static String CRITERIA = "criteria";

    public static String SECTION = "section";
    public static String START = "start";
    public static String END = "end";
    public static String VALUE = "value";
    public static String KEYWORD = "keyword";
    public static String YES = "yes";
    public static String NO = "no";

    public static String IS_SECTION = "isSection";
    public static String IS_START_AVAILABLE = "isStartAvailable";
    public static String IS_END_AVAILABLE = "isEndAvailable";




    public static String CODE = "code";
    public static String PAGECONTENT = "pagecount";
    public static String TOTAL = "total";
    public static String DATA = "data";
    public static String ERROR = "error";
    public static String INTERFACE = "interface";
    public static String MSG = "msg";
    public static String COUNT = "count";


    public static String TYPE_DICOM = "dicom";
    public static String TYPE_ELECTRIC = "electric";
    public static String TYPE_GUAGE = "guage";
    public static String TYPE_KFB = "kfb";
    public static String TYPE_MULTIDIMENSION = "multidimension";



    /****************其他常量字符串*******************/
    public static String RECORD_TOTAL = "recordtotal";
    public static Integer DEFAULT_PAGESIZE = 1000;

    /****************返回码常量*******************/
    public static String CODE_000 = "000";  //查询成功
    public static String CODE_010 = "010";  //查询条件参数为空
    public static String CODE_011 = "011";  //参数解析错误
    public static String CODE_501 = "501";  //标签冲突
    public static String CODE_999 = "999";  //查询失败

    /***************查询条件中的字段******************/

    public static String TAG_PARAM = "tag";

    public static String PatientUID_ELECTRIC_PARAM = "patientuid";
    public static String PatientName_ELECTRIC_PARAM = "patientname";
    public static String AGE_START_ELECTRIC_PARAM = "age_tart";
    public static String AGE_END_ELECTRIC_PARAM = "age_end";
    public static String CREATEDATE_START_ELECTRIC_PARAM = "createdate_start";
    public static String CREATEDATE_END_ELECTRIC_PARAM = "createdate_end";
    public static String ENTRYDATE_START_ELECTRIC_PARAM = "entrydate_start";
    public static String ENTRYDATE_END_ELECTRIC_PARAM = "entrydate_end";
    public static String InstitutionName_ELECTRIC_PARAM = "hospital";

    public static String PATIENTNAME = "patientname";





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
    public static String ES_ELECTRIC_INDEX = "es.electric.index";
    public static String ES_DICOM_TYPE = "es.dicom.type";
    public static String ES_DESENSITIZATION_TYPE = "es.disensitization.type";
    public static String ES_ELECTRIC_TYPE = "es.electric.type";

    public static String DICOM_THUMBNAIL_TABLENAME = "dicom.thumbnail.tablename";
    public static String DICOM_THUMBNAIL_CF = "dicom.thumbnail.cf";
    public static String DICOM_TABLENAME = "dicom.tablename";
    public static String DICOM_CF = "dicom.cf";
    public static String DICOM_DESENSITIZATION_TABLENAME = "dicom.disensitization.tablename";
    public static String DICOM_DESENSITIZATION_CF = "dicom.disensitization.cf";

    public static String PYTHON_CMD = "pythoncmd";
    public static String PYTHON_SCRIPT = "pythonscript";



    /**************HBase表列簇字段****************************/
    public static String THUMBNAIL = "thumbnail";
    public static String TAG = "tag"; //脱敏数据的表中使用该字段
    public static String CREATE_DATE = "createdate"; //kfb元数据字段中有使用
    public static String BARCODE = "barcode"; //kfb元数据字段中有使用


    /**************乳腺脱敏数据定性信息表breast_roi中在数据库中对应的字段******************/
    public static String SERIES_UID = "series_uid";
    public static String INSTANCES_UID = "instances_uid";
    public static String LOCATION = "location";
    public static String CLASSIFICATION = "classification";
    public static String SHAPE = "shape";
    public static String BOUNDARY = "boundary";
    public static String BOUNDARY1 = "boundary1";
    public static String BOUNDARY2 = "boundary2";
    public static String DENSITY = "density";
    public static String QUADRANT = "quadrant";
    public static String RISK = "risk";
    public static String POINTS = "points";
    public static String TYPE = "type";
    public static String UID = "uid";
    public static String SERIES_DESCRIPTION = "series_description";
    public static String TOOL_STATE_MANAGER = "tool_state_manager";
    public static String RESTORE_DATA = "restore_data";

    /**************乳腺脱敏数据定性信息表roi3d中在数据库中对应的字段******************/
    public static String ROI3D_SERIES_UID = "roi3d_series_uid";
    public static String ROI3D_SIGNS = "roi3d_signs";
    public static String ROI3D_NODULE_ANALYSIS = "roi3d_nodule_analysis";
    public static String ROI3D_FOLLOW_UP = "roi3d_follow_up";
    public static String ROI3D_RISK_ASSESSMENT = "roi3d_risk_assessment";
    public static String ROI3D_COLOR = "roi3d_color";
    public static String ROI3D_WIDTH = "roi3d_width";
    public static String ROI3D_TYPE = "roi3d_type";
    public static String ROI3D_NODAL_POSITION = "roi3d_nodal_position";
    public static String ROI3D_NUM = "roi3d_num";


    /***************乳腺脱敏数据病灶轮廓信息表roi2d中在数据库中对应的字段**************/
    public static String ROI2D_SERIES_UID = "roi2d_series_uid";
    public static String ROI2D_INSTANCES_UID = "roi2d_instances_uid";
    public static String ROI2D_DIM = "roi2d_dim";
    public static String ROI2D_SLICE = "roi2d_slice";
    public static String ROI2D_POINTS = "roi2d_points";

    /***************Series详细信息表series中在数据库中对应的字段**************/
    public static String SERIES_DES= "series_des";
    public static String SERIES_MODALITY= "series_modality";

    /***************Series表series_modality字段值域**************/
    public static String MG = "MG";
    public static String CT = "CT";


    /***************部位标识*****************************/
    public static String BREAST = "breast";
    public static String LUNG = "lung";

}
