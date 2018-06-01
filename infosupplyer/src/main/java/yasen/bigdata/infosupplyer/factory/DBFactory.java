package yasen.bigdata.infosupplyer.factory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package yasen.bigdata.infosupplyer.factory
 * @Description: ${todo}
 * @date 2018/5/23 13:37
 */
public class DBFactory {
    // JDBC 驱动名及数据库 URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String MARKTOOL_DB_URL = "jdbc:mysql://192.168.1.228:3306/mark_tools";
    static final String BIGDATA_DB_URL = "jdbc:mysql://192.168.1.228:3306/bigdata?useUnicode=true&characterEncoding=UTF-8";

    // 数据库的用户名与密码，需要根据自己的设置
    static final String USER = "mysql";
    static final String PASS = "123456";
    static Connection connMarkTool = null;
    static Connection connBigData = null;
    static {
        try {
            Class.forName(JDBC_DRIVER);
            connMarkTool = DriverManager.getConnection(MARKTOOL_DB_URL,USER,PASS);
            connBigData = DriverManager.getConnection(BIGDATA_DB_URL,USER,PASS);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getMarkToolConnection(){
        return connMarkTool;
    }
    public static Connection getBigdataConnection(){
        return connBigData;
    }


}
