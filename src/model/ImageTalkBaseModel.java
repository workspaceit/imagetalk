package model;

import controller.service.ImageTalkBaseController;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class ImageTalkBaseModel {
//    static final private String DBDriver = "com.mysql.jdbc.Driver";
//    static final private String DBHost = "127.0.0.1";
//    static final private String DBPort = "3306";
//    static final private String DBName = "imagetalk";
//    static final private String Url_Prefix = "jdbc:mysql:";
//    static final private String timeZoneParam = "?characterEncoding=UTF-8";
//    static final private String DBUrl = Url_Prefix + "//" + DBHost + "/" + DBName + timeZoneParam; //Url_Prefix + "//" + DBHost + ":" + DBPort + "/" + DBName;
//    static final private String DBUser = "root";
//    static final private String DBPassword = "wsit97480";

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

    private ArrayList<ModelError> errorQueue;

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
    public class ModelError {
        private String msg;
        private String param;

        public ModelError() {
            this.msg = "";
            this.param = "";
        }
        private void setError(String param,String msg){
            this.msg = msg;
            this.param = param;
        }
        public String getMsg(){
            return this.msg;
        }
        public String getParam(){
            return this.param;
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

        this.errorQueue = new ArrayList<ModelError>();
        this.limit = -1;
        this.offset = -1;

        this.currentUserId = 0;
    }
    public boolean setReqParamObj(String params,Object obj){
        if(obj==null){
            this.setError(params,params+" is required");
            return false;
        }
        try{
            String tmpStr = (String)obj;
            if(tmpStr.equals("")){
                this.setError(params,params+" is required");
            }

        }catch (ClassCastException ex){
            System.out.println(ex.getMessage());
            this.setError(params,params+" is required");
            return false;
        }
        return true;

    }
    public void setError(String param,String msg){
        ModelError modelError = new ModelError();
        modelError.setError(param,msg);
        this.errorQueue.add(modelError);
    }
    public boolean hasError(){
        return (this.errorQueue.size()>0)?true:false;
    }
    public ModelError getFirstError(){
        return  this.errorQueue.get(0);
    }

    public void setLimit(Object limit){
        try{
            this.limit = Integer.parseInt((String)limit);
            if (this.limit <= 0){
                this.setError("limit","limit must be greater zero required");
                return;
            }

        }catch (ClassCastException ex){
            System.out.println(ex.getMessage());
            this.setError("limit", "limit int required");
            this.limit = -1;
            return;
        }
        this.limit = (this.limit>20)?20:this.limit;
    }
    public void setOffset(Object offset){
        try{
            this.offset = Integer.parseInt((String)offset);
            if (this.offset < 0){
                this.setError("offset","offset must be positive value");
                return;
            }

        }catch (ClassCastException ex){
            System.out.println(ex.getMessage());
            this.setError("offset", "offset int required");
            this.offset = -1;
            return;
        }
    }

    public static void dbConnectionClose(){
        if(dbCon.get()==null){
            return;
        }
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
            System.out.println("at insert");
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