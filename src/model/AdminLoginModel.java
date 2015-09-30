package model;

import model.datamodel.Login;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by mi on 8/21/15.
 */
public class AdminLoginModel extends ImageTalkBaseModel {

    public Login login;

    public int id = 0;
    public String email       = null;
    public String password    = null;
    public String accessToken = null;
    public String activationCode = null;
    public int type = 3;
    public int u_id;

    public AdminLoginModel() {
        super();
        this.login = new Login();
        this.tableName = "admin_login";
    }
    public String getActivationCodeByEmail(String email) {
        String sql = "select activation_code from "+this.tableName+" where email = '" + email + "' limit 1";
        this.getData(sql);

        try {
            while (this.resultSet.next()) {
                return this.resultSet.getString("activation_code");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return null;
    }
    public String getPasswordByEmail(String email) {
        String sql = "select password from "+this.tableName+" where email = '" + email + "' limit 1";
        this.getData(sql);

        try {
            while (this.resultSet.next()) {
                return this.resultSet.getString("password");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return null;
    }
    public int getCount() {
        String sql = "select count(id) as count from "+this.tableName+" ";
        int count = 0;
        this.resultSet = this.getData(sql);
        try {
            while (this.resultSet.next()) {
                count = this.resultSet.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return count;
    }
    public int getCountOfAdminUser() {
        String sql = "select count(id) as count from "+this.tableName+" where type=1";
        int count = 0;
        this.resultSet = this.getData(sql);
        try {
            while (this.resultSet.next()) {
                count = this.resultSet.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return count;
    }
    public Login getAllById(int id) {
        String sql = "select id,email,password,u_id,access_token,type,created_date from "+this.tableName+" where id = " + id + " limit 1";
        Login login = new Login();
        this.getData(sql);
        try {
            while (this.resultSet.next()) {
                login.id = this.resultSet.getInt("id");
                login.email = this.resultSet.getString("email");
                login.u_id = this.resultSet.getInt("u_id");
                login.access_token = this.resultSet.getString("access_token");
                Byte typeByte = this.resultSet.getByte("type");
                login.type =typeByte.intValue();
                login.created_date = this.resultSet.getString("created_date");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return login;
    }

    public ArrayList<Login> getAllExceptMe(int u_id) {
        String sql = "select * from "+this.tableName+" where u_id !="+u_id;
        this.getData(sql);
        ArrayList<Login> loginsList = new ArrayList<Login>();
        try {
            while (this.resultSet.next()) {
                Login login = new Login();
                login.id = this.resultSet.getInt("id");
                login.email = this.resultSet.getString("email");
                this.login.access_token = this.resultSet.getString("access_token");
                login.created_date = this.resultSet.getString("created_date");
                login.u_id = this.resultSet.getInt("u_id");
                Byte typeByte = this.resultSet.getByte("type");
                login.type =typeByte.intValue();
                UserInfModel userInfModel = new UserInfModel();
                login.user = userInfModel.getById(login.u_id);

                loginsList.add(login);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            this.closeConnection();
        }finally {
            this.closeConnection();
        }
        return loginsList;

    }
    public ArrayList<Login> getAllTeamLead() {
        String sql = "select * from "+this.tableName+" where type = 2";
        this.getData(sql);
        ArrayList<Login> loginsList = new ArrayList<Login>();
        try {
            while (this.resultSet.next()) {
                Login login = new Login();
                login.id = this.resultSet.getInt("id");
                login.email = this.resultSet.getString("email");
                this.login.access_token = this.resultSet.getString("access_token");
                login.created_date = this.resultSet.getString("created_date");
                login.u_id = this.resultSet.getInt("u_id");
                Byte typeByte = this.resultSet.getByte("type");
                login.type =typeByte.intValue();
                UserInfModel userInfModel = new UserInfModel();
                login.user = userInfModel.getById(login.u_id);

                loginsList.add(login);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            this.closeConnection();
        }finally {
            this.closeConnection();
        }
        return loginsList;

    }
    public boolean isValidLogin(String email, String password) {
        String sql = "select * from "+this.tableName+" where email = '" + email + "' and password ='" + password + "' and type > 1 limit 1";
        this.getData(sql);

        try {
            while (this.resultSet.next()) {
                this.login.id = this.resultSet.getInt("id");
                this.login.email = this.resultSet.getString("email");
                this.login.access_token = this.resultSet.getString("access_token");
                this.login.created_date = this.resultSet.getString("created_date");
                this.login.u_id = this.resultSet.getInt("u_id");
                Byte typeByte = this.resultSet.getByte("type");
                login.type =typeByte.intValue();
                UserInfModel userInfModel = new UserInfModel();
                this.login.user = userInfModel.getById(this.login.u_id);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            this.closeConnection();
        }finally {
            this.closeConnection();
        }

        return false;
    }
    public boolean isValidAdminLogin(String email, String password) {
        String sql = "select * from "+this.tableName+" where email = '" + email + "' and password ='" + password + "' and type = 1 limit 1";
        this.getData(sql);

        try {
            while (this.resultSet.next()) {
                this.login.id = this.resultSet.getInt("id");
                this.login.email = this.resultSet.getString("email");
                this.login.access_token = this.resultSet.getString("access_token");
                this.login.created_date = this.resultSet.getString("created_date");
                this.login.u_id = this.resultSet.getInt("u_id");
                Byte typeByte = this.resultSet.getByte("type");
                login.type =typeByte.intValue();
                UserInfModel userInfModel = new UserInfModel();
                this.login.user = userInfModel.getById(this.login.u_id);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            this.closeConnection();
        }finally {
            this.closeConnection();
        }

        return false;
    }
    public boolean isValidLoginByAccessToken(String accessToken) {
        String sql = "select * from "+this.tableName+" where access_token = '" + accessToken + "' limit 1";
        this.getData(sql);
        try {
            while (this.resultSet.next()) {
                this.login.id = this.resultSet.getInt("id");
                this.login.email = this.resultSet.getString("email");
                this.login.access_token = this.resultSet.getString("access_token");
                this.login.created_date = this.resultSet.getString("created_date");
                this.login.u_id = this.resultSet.getInt("u_id");
                Byte typeByte = this.resultSet.getByte("type");
                login.type =typeByte.intValue();
                UserInfModel userInfModel = new UserInfModel();
                this.login.user = userInfModel.getById(this.login.u_id );

                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return false;
    }
    public boolean isActive() {
        String sql = "select active from "+this.tableName+" where id = " + this.login.id + " limit 1";
        System.out.println(sql);
        this.getData(sql);
        try {
            while (this.resultSet.next()) {
                int active = this.resultSet.getInt("active");
                if(active==1){
                    return true;
                }else{
                    return false;
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return false;
    }
    public boolean isEmailExist(String email) {
        String sql = "select email from "+this.tableName+" where email = '" + email + "'  limit 1";
        this.getData(sql);
        try {
            while (this.resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return false;
    }
    public boolean isTeamLead() {
        String sql = "select id from "+this.tableName+" where id = "+this.id+" and type = 2 limit 1";
        this.getData(sql);

        try {
            while (this.resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            this.closeConnection();
        }finally {
            this.closeConnection();
        }

        return false;
    }
    public int insertData(int userId) {
        this.u_id = userId;
        Random ran = new Random();
        int accessTokenRand = ran.nextInt(9999) + 1111;
        System.out.println("AccessTokenRand :"+accessTokenRand);
        this.accessToken = email + password;
        this.activationCode = email + accessTokenRand;
//        String sql = "INSERT INTO " + this.tableName + " VALUES (null, '" + this.email + "', '" + this.password +
//                     "', '" + this.u_id + "', md5('" + this.accessToken + "'),md5('" + this.accessToken + "')" + this.type + ", SYSDATE())";

        String sql = "INSERT INTO " + this.tableName + " ( `email`, `password`, `u_id`, `access_token`, `activation_code`, `type`, `created_date`) " +
                "VALUES ('" + this.email + "','" + this.password +"','" + this.u_id + "', md5('" + this.accessToken + "'),md5('" + this.activationCode + "')," + this.type +", SYSDATE())";
        return this.insertData(sql);
    }
    public boolean updatePassword(){
        // Need `login` object to perform oparation
        if(this.login.id<=0){
            return false;
        }
        this.accessToken = this.login.email+this.password;
        String query = "UPDATE " + this.tableName + " SET `password`='" + this.password + "',access_token=md5('" + this.accessToken + "') WHERE `id`="+this.login.id;

        return this.updateData(query);
    }
    public boolean updateTypeToTeamLead(){
        // Need `login` object to perform oparation
        if(this.login.id<=0){
            return false;
        }

        String query = "UPDATE " + this.tableName + " SET `type`=2 WHERE `id`="+this.login.id;

        return this.updateData(query);
    }
    public boolean updateTypeToUser(){
        // Need `login` object to perform oparation
        if(this.login.id<=0){
            return false;
        }

        String query = "UPDATE " + this.tableName + " SET `type`=3 WHERE `id`="+this.login.id;

        return this.updateData(query);
    }
    public boolean updateActiveToActivated(){
        // Need `login` object to perform oparation
        if(this.login.activation_code==""){
            return false;
        }

        String query = "UPDATE " + this.tableName + " SET `active`=1 WHERE `activation_code`='"+this.login.activation_code+"'";

        return this.updateData(query);
    }
    public boolean updateActiveToDeactivated(){
        // Need `login` object to perform oparation
        if(this.login.activation_code==""){
            return false;
        }

        String query = "UPDATE " + this.tableName + " SET `active`=0 WHERE `activation_code`='"+this.login.activation_code+"'";

        return this.updateData(query);
    }
    public int deleteData(){
        String sql = "DELETE FROM " + this.tableName + " WHERE id = '"+ this.login.id +"'";
        return  this.deleteData(sql);
    }
    public int deleteById(){
        String sql = "DELETE FROM " + this.tableName + " WHERE id = '"+ this.login.id +"'";
        return  this.deleteData(sql);
    }
}
