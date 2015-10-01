package model;

import java.sql.*;

public class ImageTalkBaseModel {
    static final private String DBDriver   = "com.mysql.jdbc.Driver";
    static final private String DBHost     = "127.0.0.1";
    static final private String DBPort     = "3306";
    static final private String DBName     = "imagetalk";
    static final private String Url_Prefix = "jdbc:mysql:";
    static final private String DBUrl      = Url_Prefix + "//" + DBHost  + "/" + DBName; //Url_Prefix + "//" + DBHost + ":" + DBPort + "/" + DBName;
    static final private String DBUser     = "root";
    static final private String DBPassword = "";

    protected String tableName = null;

    protected Connection con  = null;
    protected Statement  stmt = null;

    private String    query       = null;
    public  ResultSet resultSet = null;
    public static String errorMsg = null;
    public ImageTalkBaseModel() {
        try {
            Class.forName(DBDriver);
            con = DriverManager.getConnection(DBUrl, DBUser, DBPassword);
            stmt = con.createStatement();
        } catch (Exception ex) {
            ex.printStackTrace();
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
        if(this.con==null){
            try {
                Class.forName(DBDriver);
                this.con = DriverManager.getConnection(DBUrl, DBUser, DBPassword);
                if(this.stmt==null){
                    this.stmt = this.con.createStatement();
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
            this.closeConnection();
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
                this.resultSet.close();
            }
            if(this.stmt!=null) {
                this.stmt.close();
                this.stmt = null;
            }
            if(this.con!=null) {
                this.con.close();
                this.con =null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}
