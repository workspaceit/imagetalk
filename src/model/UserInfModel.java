package model;

import model.datamodel.Login;
import model.datamodel.User;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by mi on 8/20/15.
 */
public class UserInfModel extends ImageTalkBaseModel {
    public int    id;
    public String f_name;
    public String l_name;
    public String address;
    public String created_date;
    public UserInfModel() {
        super.tableName = "user_inf";

        this.id = 0;
        this.f_name = null;
        this.l_name = null;
        this.address = null;
        this.created_date = null;
    }

    public ArrayList<User> getAll() {
        this.query = "select * from " + super.tableName;
        this.getData(this.query);
        ArrayList<User> userList = new ArrayList<User>();

        try {
            while (this.resultSet.next()) {
                User user = new User();
                user.f_name = this.resultSet.getString("f_name");
                user.l_name = this.resultSet.getString("l_name");
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

        if(keyword==null || keyword ==""){
            this.query = "select * from " + super.tableName  +" where id !=  "+this.id;
        }else{
            this.query = "select * from " + super.tableName+" where ( f_name like '%"+keyword+"%' or l_name like '%"+keyword+"%' )  and id !=  "+this.id;
        }
        ArrayList<User> userList = new ArrayList<User>();
        this.getData( this.query);
        try {
            while (this.resultSet.next()) {
                User user = new User();

                user.id = this.resultSet.getInt("id");
                user.f_name = this.resultSet.getString("f_name");
                user.l_name = this.resultSet.getString("l_name");
                user.address = this.resultSet.getString("address");
                user.created_date = this.resultSet.getString("created_date");
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

        if(keyword==null || keyword ==""){
            this.query = "select user_inf.id," +
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
            this.query =  "select user_inf.id," +
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
        System.out.println(this.query);
        ArrayList<Login> loginList = new ArrayList<Login>();
        this.getData( this.query);
        try {
            while (this.resultSet.next()) {
                User user = new User();
                Login login = new Login();

                user.id = this.resultSet.getInt("id");
                user.f_name = this.resultSet.getString("f_name");
                user.l_name = this.resultSet.getString("l_name");
                user.address = this.resultSet.getString("address");
                user.created_date = this.resultSet.getString("user_inf_created_date");


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
        this.query ="select * from " + super.tableName+" where id="+id+" limit 1 ";
        this.getData( this.query);
        User user = new User();

        try {
            while (this.resultSet.next()) {
                user.id = this.resultSet.getInt("id");
                user.f_name = this.resultSet.getString("f_name");
                user.l_name = this.resultSet.getString("l_name");
                user.address = this.resultSet.getString("address");
                user.created_date = this.resultSet.getString("created_date");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }

        return user;
    }
    public int insertData() {
        String query = "INSERT INTO " + tableName + " VALUES (null, '" + f_name + "', '" + l_name + "', '" + address + "', SYSDATE())";
        return this.insertData(query);
    }
    public boolean update(){
        String query = "UPDATE " + this.tableName + " SET `f_name`='" + this.f_name + "',`l_name`='" + this.l_name + "',`address`='" + this.address + "' WHERE `id`="+this.id;

        return this.updateData(query);
    }
    public Login getProfileInformation(int u_id){
        Login login = new Login();
        String sql = "SELECT login.id as login_id,\n" +
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

        this.getData(sql);
        try {
            while (this.resultSet.next()) {
                login.id = this.resultSet.getInt("login_id");
                login.email = this.resultSet.getString("email");
                login.created_date = this.resultSet.getString("login_c_date");
                login.type = this.resultSet.getInt("user_type");
                login.u_id = this.resultSet.getInt("u_id");

                login.user.id = this.resultSet.getInt("u_id");
                login.user.f_name = this.resultSet.getString("user_f_name");
                login.user.l_name = this.resultSet.getString("user_l_name");
                login.user.address = this.resultSet.getString("user_address");
                login.user.created_date = this.resultSet.getString("user_c_date");

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
