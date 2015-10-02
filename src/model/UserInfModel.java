package model;

import model.datamodel.app.Login;
import model.datamodel.app.User;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by mi on 8/20/15.
 */
public class UserInfModel extends ImageTalkBaseModel {
    private int    id;
    private String f_name;
    private String l_name;
    private String address;
    private String created_date;
    private String picPath;

    public UserInfModel() {
        super.tableName = "user_inf";

        this.id = 0;
        this.f_name = "";
        this.l_name = "";
        this.address = "";
        this.created_date = "";
        this.picPath = "";
    }

    public String getPicPath() {
        return picPath;
    }

    public boolean setPicPath(String picPath) {
        this.picPath = picPath;
        return true;
    }

    public int getId() {
        return id;
    }

    public boolean setId(int id) {
        this.id = id;
        return true;
    }

    public String getF_name() {
        return f_name;
    }

    public boolean setF_name(String f_name) {
        this.f_name = f_name;
        return true;
    }

    public String getL_name() {
        return l_name;
    }

    public boolean setL_name(String l_name) {
        this.l_name = l_name;
        return true;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreated_date() {
        return created_date;
    }

    public boolean setCreated_date(String created_date) {
        this.created_date = created_date;
        return true;
    }

    public ArrayList<User> getAll() {
        String query = "select * from " + super.tableName;
        this.setQuery(query);
        this.getData();
        ArrayList<User> userList = new ArrayList<User>();

        try {
            while (this.resultSet.next()) {
                User user = new User();
                user.firstName = this.resultSet.getString("f_name");
                user.lastName = this.resultSet.getString("l_name");
                userList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return userList;
    }

    public ArrayList<User> getAllByKeyword(String keyword) {
        String query="";
        if(keyword==null || keyword ==""){
            query = "select * from " + super.tableName  +" where id !=  "+this.id;
        }else{
            query = "select * from " + super.tableName+" where ( f_name like '%"+keyword+"%' or l_name like '%"+keyword+"%' )  and id !=  "+this.id;
        }

        this.setQuery(query);
        ArrayList<User> userList = new ArrayList<User>();
        this.getData( );
        try {
            while (this.resultSet.next()) {
                User user = new User();

                user.id = this.resultSet.getInt("id");
                user.firstName = this.resultSet.getString("f_name");
                user.lastName = this.resultSet.getString("l_name");
                user.createdDate = this.resultSet.getString("created_date");
                userList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return userList;
    }

    public ArrayList<Login> getAllUserLoginByKeyword(String keyword) {
        String query = "";
        if(keyword==null || keyword ==""){
            query = "select user_inf.id," +
                    "user_inf.f_name," +
                    "user_inf.l_name," +
                    "user_inf.address," +
                    "user_inf.created_date as user_inf_created_date," +
                    "login.id as login_id," +
                    "login.email," +
                    "login.u_id," +
                    "login.type,"  +
                    "login.created_date as login_created_date " +
                    "from " + super.tableName  +" join login on login.u_id = user_inf.id " +
                    "where user_inf.id !=  "+this.id;
        }else{
            query =  "select user_inf.id," +
                    "user_inf.f_name," +
                    "user_inf.l_name," +
                    "user_inf.address," +
                    "user_inf.created_date as user_inf_created_date," +
                    "login.id as login_id," +
                    "login.email," +
                    "login.u_id," +
                    "login.type,"  +
                    "login.created_date as login_created_date " +
                    "from " + super.tableName  +" join login on login.u_id = user_inf.id " +
                    " where ( f_name like '%"+keyword+"%' or l_name like '%"+keyword+"%' )  and user_inf.id !=  "+this.id;
        }

        ArrayList<Login> loginList = new ArrayList<Login>();
        this.setQuery(query);
        this.getData( );
        try {
            while (this.resultSet.next()) {
                User user = new User();
                Login login = new Login();

                user.id = this.resultSet.getInt("id");
                user.firstName = this.resultSet.getString("f_name");
                user.lastName = this.resultSet.getString("l_name");
                user.createdDate = this.resultSet.getString("user_inf_created_date");


                login.id = this.resultSet.getInt("login_id");
                login.email = this.resultSet.getString("email");
                login.created_date = this.resultSet.getString("login_created_date");
                login.u_id = this.resultSet.getInt("u_id");
                Byte typeByte = this.resultSet.getByte("type");
                login.type =typeByte.intValue();

                login.user = user;

                loginList.add(login);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return loginList;
    }

    public User getById(int id) {
        String query ="select * from " + super.tableName+" where id="+id+" limit 1 ";

        this.setQuery(query);
        this.getData();
        User user = new User();

        try {
            while (this.resultSet.next()) {
                user.id = this.resultSet.getInt("id");
                user.firstName = this.resultSet.getString("f_name");
                user.lastName = this.resultSet.getString("l_name");

                user.createdDate = this.resultSet.getString("created_date");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }

        return user;
    }

    public int insertData() {
        String query = "INSERT INTO " + tableName + " (f_name,l_name) VALUES ('" + this.f_name + "', '" + this.l_name + "')";
        this.id = this.insertData(query);
        return  this.id;
    }

    public boolean update(){
        String query = "UPDATE " + this.tableName + " SET `f_name`='" + this.f_name + "',`l_name`='" + this.l_name + "',`address`='" + this.address + "' WHERE `id`="+this.id;

        return this.updateData(query);
    }

    public boolean updatePicPath(){
        String query = "UPDATE " + this.tableName + " SET `pic_path`='" + this.picPath + "' WHERE `id`="+this.id;

        return this.updateData(query);
    }

    public Login getProfileInformation(int u_id){
        Login login = new Login();
        String query = "SELECT login.id as login_id,\n" +
                        " login.email as email,\n" +
                        " login.type as user_type,\n" +
                        " login.u_id as u_id,\n" +
                        " login.created_date as login_c_date,\n" +
                        " user_inf.f_name as user_f_name,\n" +
                        " user_inf.l_name as user_l_name,\n" +
                        " user_inf.address as user_address,\n" +
                        " user_inf.created_date as user_c_date\n" +
                        " FROM `login`join user_inf on login.u_id = user_inf.id " +
                        " where user_inf.id = "+u_id+" limit 1";
        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
                login.id = this.resultSet.getInt("login_id");
                login.email = this.resultSet.getString("email");
                login.created_date = this.resultSet.getString("login_c_date");
                login.type = this.resultSet.getInt("user_type");
                login.u_id = this.resultSet.getInt("u_id");

                login.user.id = this.resultSet.getInt("u_id");
                login.user.firstName = this.resultSet.getString("user_f_name");
                login.user.lastName = this.resultSet.getString("user_l_name");
                login.user.createdDate = this.resultSet.getString("user_c_date");

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }


        return login;
    }

    public int deleteData(int userId) {
        this.id = userId;
        String sql = "DELETE FROM " + this.tableName + " WHERE id = '" + this.id + "'";
        return this.deleteData(sql);
    }

    public int deleteById() {
        String sql = "DELETE FROM " + this.tableName + " WHERE id = '" + this.id + "'";
        return this.deleteData(sql);
    }
}
