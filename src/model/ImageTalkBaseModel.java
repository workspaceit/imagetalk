package model;

import controller.service.ImageTalkBaseController;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class ImageTalkBaseModel {
    static final private String DBDriver = "com.mysql.jdbc.Driver";
    static final private String DBHost = "127.0.0.1";
    static final private String DBPort = "3306";
    static final private String DBName = "imagetalk";
    static final private String Url_Prefix = "jdbc:mysql:";
    static final private String timeZoneParam = "?characterEncoding=UTF-8";
    static final private String DBUrl = Url_Prefix + "//" + DBHost + "/" + DBName + timeZoneParam; //Url_Prefix + "//" + DBHost + ":" + DBPort + "/" + DBName;
    static final private String DBUser = "root";
    static final private String DBPassword = "";

    public final static ThreadLocal<Connection> dbCon = new ThreadLocal<Connection>();

    protected String tableName = null;
    protected Connection con = null;
    protected Statement stmt = null;
    protected boolean autoCommit = true;
    private String query = null;
    public ResultSet resultSet = null;
    public BaseErrorManager errorObj;
    public BaseOperationManager operationStatus;
    private int currentUserId;
    public int limit;
    public int offset;

    public class BaseErrorManager {
        public String msg;
        public boolean errStatus;

        public BaseErrorManager() {
            this.msg = "";
            this.errStatus = false;
        }
    }

    public class BaseOperationManager {
        public String msg;
        public boolean status;

        public BaseOperationManager() {
            this.msg = "";
            this.status = true;
        }
    }

    public ImageTalkBaseModel() {
        try {
            Class.forName(DBDriver);
            establishDbCon(); //DriverManager.getConnection(DBUrl, DBUser, DBPassword);
            stmt = con.createStatement();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.errorObj = new BaseErrorManager();
        this.operationStatus = new BaseOperationManager();
        this.limit = -1;
        this.offset = -1;

        this.currentUserId = 0;
    }
    public static void dbConnectionClose(){
        Connection con = dbCon.get();
        try {
            con.close();
            dbCon.set(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void establishDbCon(){
        if(dbCon.get() == null){
            try {
                dbCon.set(DriverManager.getConnection(DBUrl, DBUser, DBPassword));
            }catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        con = dbCon.get();
    }
    public void setCurrentUserId(int currentUserId){
        this.currentUserId = currentUserId;
    }
    public int getCurrentUserId(){
        return this.currentUserId;
    }
    public void startTransaction() {
        this.dbConnectionRecheck();
        try {
            autoCommit = false;
            this.con.setAutoCommit(autoCommit);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void commitTransaction() {
        try {
            this.con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeConnection();
        }
    }

    public void rollBack() {
        try {
            autoCommit = true;
            this.con.rollback();


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeConnection();
        }
    }

    public void setQuery(String query) {
        this.query = query;
    }



    protected void getData() {
        this.dbConnectionRecheck();
        try {
            this.resultSet = stmt.executeQuery(this.query);

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
        }
    }

    //    protected ResultSet getData(String sql) {
//        this.dbConnectionRecheck();
//        try {
//            this.query = sql;
//            this.resultSet = stmt.executeQuery(this.query);
//
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//        }finally {
//        }
//
//        return this.resultSet;
//    }
    public void dbConnectionRecheck() {

        try {
            if (this.con == null) {
                //    System.out.println("con Recheck "+con);
               // Class.forName(DBDriver);
                this.con = dbCon.get();//DriverManager.getConnection(DBUrl, DBUser, DBPassword);
            }
            if (this.stmt == null) {
                //    System.out.println("stmt Recheck "+stmt);
                this.stmt = this.con.createStatement();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    protected int insertData(String query) {
        this.dbConnectionRecheck();
        this.query = query;
        int id = 0;
        try {
            stmt.executeUpdate(this.query, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = stmt.getGeneratedKeys();

            if (rs.next()) {
                id = rs.getInt(1);
            }
            return id;

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (autoCommit) {
                this.closeConnection();
            } else {
                System.out.println("Auto Commit False Connection is open");
            }

        }


        return id;
    }
    protected int insertDataDemo(String query) {

        this.query = query;
        int id = 0;
        try {
            stmt.executeUpdate(this.query, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = stmt.getGeneratedKeys();

            if (rs.next()) {
                id = rs.getInt(1);
            }
            return id;

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (autoCommit) {
                this.closeConnection();
            } else {
                System.out.println("Auto Commit False Connection is open");
            }

        }


        return id;
    }

    protected boolean updateData(String query) {
        int status = 0;
        this.dbConnectionRecheck();
        try {
            status = stmt.executeUpdate(query);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            this.closeConnection();
        }

        return (status > 0) ? true : false;
    }

    protected int deleteData(String sql) {
        if (updateData(sql)) {
            return 1;
        }
        return 0;
    }

    public void closeConnection() {

        try {

            if (this.resultSet != null) {
                //   System.out.println("resultSet Closed "+resultSet);
                this.resultSet.close();
            }
            if (this.stmt != null) {
                //   System.out.println("stmt Closed "+stmt);
                this.stmt.close();
                this.stmt = null;
            }
//            if (this.con != null && this.autoCommit) {
//                //   System.out.println("con Closed "+con);
//                this.con.close();
//                this.con = null;
//            }
//
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public Calendar getUTCCal(){
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        return cal;
    }
    public String getProcessedDateTime(String dateTime) {


        return dateTime;
    }
    public String getPrcessedTimeStamp(Timestamp timeStamp) {
        String processedTime = "";
        if(timeStamp!=null){

            Long longTime = timeStamp.getTime() / 1000;
            processedTime = Long.toString(longTime);
        }

        return processedTime;
    }

    public String getUTCTimeStamp(String dateStr){
        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            java.util.Date parsedDate = dateFormat.parse(dateStr);
            System.out.println(parsedDate);
            Long longTime = parsedDate.getTime()/ 1000;
            return Long.toString(longTime);
        }catch(Exception e){//this generic but you can control another types of exception
            e.printStackTrace();
        }
        return "";
    }
    public String getUtcDateTime(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        return dateFormat.format(new java.util.Date());
    }
}