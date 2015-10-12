package model.test;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Locale;

class ImageTalkBaseModel {
    static final private String DBDriver   = "com.mysql.jdbc.Driver";
    static final private String DBHost     = "127.0.0.1";
    static final private String DBPort     = "3306";
    static final private String DBName     = "imagetalk";
    static final private String Url_Prefix = "jdbc:mysql:";
    static final private String timeZoneParam = "?useLegacyDatetimeCode=false&0serverTimezone=America/New_York";
    static final private String DBUrl      = Url_Prefix + "//" + DBHost  + "/" + DBName+timeZoneParam; //Url_Prefix + "//" + DBHost + ":" + DBPort + "/" + DBName;
    static final private String DBUser     = "root";
    static final private String DBPassword = "";

    protected String tableName = null;

    protected Connection con  = null;
    protected Statement  stmt = null;
    protected boolean autoCommit = true;
    private String    query       = null;
    public  ResultSet resultSet = null;
    public  BaseErrorManager errorObj;

    public  int    limit;
    public  int    offset;

    public class BaseErrorManager{
        public String msg;
        public boolean errStatus;

        public BaseErrorManager() {
            this.msg = "";
            this.errStatus = false;
        }
    }
    public ImageTalkBaseModel() {
        try {
            Class.forName(DBDriver);
            con = DriverManager.getConnection(DBUrl, DBUser, DBPassword);
            stmt = con.createStatement();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.errorObj = new BaseErrorManager();

        this.limit = -1;
        this.offset = -1;


    }

    public void startTransaction(){
        try {
            autoCommit = false;
            this.con.setAutoCommit(autoCommit);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void commitTransaction(){
        try {
            this.con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
    }
    public void rollBack(){
        try {
            autoCommit = true;
            this.con.rollback();


        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
    }
    public void setQuery(String query){
        this.query = query;
    }
    protected void getData() {
        this.dbConnectionRecheck();
        try {
            this.resultSet = stmt.executeQuery(this.query);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }finally {
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
    public void dbConnectionRecheck(){

            try {
                if(this.con==null) {
                    System.out.println("con Recheck "+con);
                    Class.forName(DBDriver);
                    this.con = DriverManager.getConnection(DBUrl, DBUser, DBPassword);
                }
                if (this.stmt == null) {
                    System.out.println("stmt Recheck "+stmt);
                    this.stmt = this.con.createStatement();
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }

    }
    protected int insertData(String query) {
        this.dbConnectionRecheck();
        this.query = query;
        int id =0;
        try {
            stmt.executeUpdate(this.query, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = stmt.getGeneratedKeys();

            if (rs.next()){
                id = rs.getInt(1);
            }
            return id;

        } catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            if(autoCommit){
                this.closeConnection();
            }else{
                System.out.println("Auto Commit False Connection is open");
            }

        }


        return id;
    }
    protected boolean updateData(String query) {
        int status=0;
        this.dbConnectionRecheck();
        try {
            status = stmt.executeUpdate(query);
            System.out.println("Return Update "+status);
        } catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            this.closeConnection();
        }

        return (status>0)?true:false;
    }
    protected int deleteData(String sql){
        if(updateData(sql)){
            return 1;
        }
        return  0;
    }
    public void closeConnection(){

        try {

            if(this.resultSet!=null) {
                System.out.println("resultSet Closed "+resultSet);
                this.resultSet.close();
            }
            if(this.stmt!=null) {
                System.out.println("stmt Closed "+stmt);
                this.stmt.close();
                this.stmt = null;
            }
            if(this.con!=null && this.autoCommit) {
                System.out.println("con Closed "+con);
                this.con.close();
                this.con =null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}
