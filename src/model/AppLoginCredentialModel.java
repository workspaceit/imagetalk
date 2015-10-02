package model;

import model.datamodel.app.AuthCredential;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by mi on 10/1/15.
 */
public class AppLoginCredentialModel extends ImageTalkBaseModel{

    private int id;
    private int u_id;
    private String text_status;
    private String phone_number;
    private String access_token;
    private int active;
    private int banned;
    private String created_date;
    public String token;
    public int limit;
    public int offset;

    public AppLoginCredentialModel(){
        super();
        super.tableName = "app_login_credential";
        this.token = "";
        this.id=0;
        this.u_id=0;
        this.text_status="";
        this.phone_number="";
        this.access_token="";
        this.active=0;
        this.banned=0;
        this.created_date="";
        this.token ="";
        this.limit = -1;
        this.offset= -1;
    }
    public int getId() {
        return id;
    }

    public boolean setId(int id) {
        this.id = id;
        return true;
    }

    public int getU_id() {
        return u_id;
    }

    public boolean setU_id(int u_id) {
        this.u_id = u_id;
        return true;
    }

    public String getText_status() {
        return text_status;
    }

    public boolean setText_status(String text_status) {
        this.text_status = text_status.trim();
        return true;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public boolean setPhone_number(String phone_number) {
        this.phone_number = phone_number.trim();
        return true;
    }

    public String getAccess_token() {
        return access_token;
    }

    public boolean setAccess_token(String access_token) {
        this.access_token = access_token.trim();
        return true;
    }

    public int getActive() {
        return active;
    }

    public boolean setActive(int active) {
        this.active = active;
        return true;
    }

    public int getBanned() {
        return banned;
    }

    public boolean setBanned(int banned) {
        this.banned = banned;
        return true;
    }

    public String getCreated_date() {
        return created_date;
    }

    public boolean setCreated_date(String created_date) {
        this.created_date = created_date.trim();
        return true;
    }
    public boolean isActive() {
        String query = "select active from "+this.tableName+" where id = " + this.id + " limit 1";

        this.setQuery(query);
        this.getData();

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
    public AuthCredential getAuthincatedByAccessToken() {
        AuthCredential authCredential = new AuthCredential();
        String query ="select user_inf.id as user_inf_id," +
                " user_inf.created_date as user_inf_c_date,user_inf.f_name,user_inf.l_name,user_inf.pic_path," +
                " location.id as location_id,location.lat,location.lon,location.formatted_address,location.country,location.created_date as location_c_date," +
                " app_login_credential.id as app_login_credential_id,app_login_credential.text_status,app_login_credential.access_token,app_login_credential.phone_number," +
                " app_login_credential.created_date as app_login_credential_c_date  from " + super.tableName+" " +
                " join user_inf on user_inf.id = app_login_credential.u_id  " +
                " left join location on location.id = user_inf.address_id " +
                " where app_login_credential.id="+this.id+" and app_login_credential.access_token = '" + this.access_token + "' limit 1";

        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
                authCredential.id  = this.resultSet.getInt("app_login_credential_id");
                authCredential.textStatus = (this.resultSet.getString("text_status")==null)?"":this.resultSet.getString("text_status");
                authCredential.accessToken = this.resultSet.getString("access_token");
                authCredential.phoneNumber = this.resultSet.getString("phone_number");
                authCredential.user.id = this.resultSet.getInt("user_inf_id");
                authCredential.user.firstName = this.resultSet.getString("f_name");
                authCredential.user.lastName = this.resultSet.getString("l_name");
                authCredential.user.picPath = this.resultSet.getString("pic_path");
                authCredential.user.createdDate = this.resultSet.getString("app_login_credential_c_date");

                authCredential.user.address.id = (this.resultSet.getObject("location_id")==null)?0:this.resultSet.getInt("location_id");
                authCredential.user.address.lat = (this.resultSet.getObject("lat")==null)?0:this.resultSet.getDouble("lat");
                authCredential.user.address.lon = (this.resultSet.getObject("lon")==null)?0:this.resultSet.getDouble("lon");
                authCredential.user.address.formattedAddress = (this.resultSet.getObject("formatted_address")==null)?"":this.resultSet.getString("formatted_address");
                authCredential.user.address.countryName = (this.resultSet.getObject("country")==null)?"":this.resultSet.getString("country");
                authCredential.user.address.createdDate = (this.resultSet.getObject("location_c_date")==null)?"":this.resultSet.getString("location_c_date");


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }

        return authCredential;
    }
    public boolean isNumberExist(){
        String query ="select * from " + super.tableName+" where phone_number='"+this.phone_number+"' limit 1";

        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
                this.id  = this.resultSet.getInt("id");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }

        return false;
    }
    public AuthCredential getAppCredentialById(){
        AuthCredential authCredential = new AuthCredential();
        String query ="select user_inf.id as user_inf_id," +
                            " user_inf.created_date as user_inf_c_date,user_inf.f_name,user_inf.l_name,user_inf.pic_path," +
                            " location.id as location_id,location.lat,location.lon,location.formatted_address,location.country,location.created_date as location_c_date," +
                            " app_login_credential.id as app_login_credential_id,app_login_credential.text_status,app_login_credential.access_token,app_login_credential.phone_number," +
                            " app_login_credential.created_date as app_login_credential_c_date  from " + super.tableName+" " +
                            " join user_inf on user_inf.id = app_login_credential.u_id  " +
                            " left join location on location.id = user_inf.address_id " +
                            " where app_login_credential.id="+this.id+" limit 1";
        System.out.println(query);
        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
                authCredential.id  = this.resultSet.getInt("app_login_credential_id");
                authCredential.textStatus = (this.resultSet.getString("text_status")==null)?"":this.resultSet.getString("text_status");
                authCredential.accessToken = this.resultSet.getString("access_token");
                authCredential.phoneNumber = this.resultSet.getString("phone_number");
                authCredential.user.id = this.resultSet.getInt("user_inf_id");
                authCredential.user.firstName = this.resultSet.getString("f_name");
                authCredential.user.lastName = this.resultSet.getString("l_name");
                authCredential.user.picPath = this.resultSet.getString("pic_path");
                authCredential.user.createdDate = this.resultSet.getString("app_login_credential_c_date");

                authCredential.user.address.id = (this.resultSet.getObject("location_id")==null)?0:this.resultSet.getInt("location_id");
                authCredential.user.address.lat = (this.resultSet.getObject("lat")==null)?0:this.resultSet.getDouble("lat");
                authCredential.user.address.lon = (this.resultSet.getObject("lon")==null)?0:this.resultSet.getDouble("lon");
                authCredential.user.address.formattedAddress = (this.resultSet.getObject("formatted_address")==null)?"":this.resultSet.getString("formatted_address");
                authCredential.user.address.countryName = (this.resultSet.getObject("country")==null)?"":this.resultSet.getString("country");
                authCredential.user.address.createdDate = (this.resultSet.getObject("location_c_date")==null)?"":this.resultSet.getString("location_c_date");


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }

        return authCredential;
    }
    public ArrayList<AuthCredential> getAppCredentialByKeyword(String keyword){
        ArrayList<AuthCredential> authCredentialList = new ArrayList<AuthCredential>();
        String query ="select user_inf.id as user_inf_id," +
                " user_inf.created_date as user_inf_c_date,user_inf.f_name,user_inf.l_name,user_inf.pic_path," +
                " location.id as location_id,location.lat,location.lon,location.formatted_address,location.country,location.created_date as location_c_date," +
                " app_login_credential.id as app_login_credential_id,app_login_credential.text_status,app_login_credential.access_token,app_login_credential.phone_number," +
                " app_login_credential.created_date as app_login_credential_c_date  from " + super.tableName+" " +
                " join user_inf on user_inf.id = app_login_credential.u_id  " +
                " left join location on location.id = user_inf.address_id ";

        if(keyword!=null && keyword!=""){
            query +=" where user_inf.f_name like '%"+keyword+"%'";
        }
        System.out.println(query);
        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
                AuthCredential authCredential = new AuthCredential();
                authCredential.id  = this.resultSet.getInt("app_login_credential_id");
                authCredential.textStatus = (this.resultSet.getString("text_status")==null)?"":this.resultSet.getString("text_status");
                authCredential.accessToken = this.resultSet.getString("access_token");
                authCredential.phoneNumber = this.resultSet.getString("phone_number");
                authCredential.user.id = this.resultSet.getInt("user_inf_id");
                authCredential.user.firstName = this.resultSet.getString("f_name");
                authCredential.user.lastName = this.resultSet.getString("l_name");
                authCredential.user.picPath = this.resultSet.getString("pic_path");
                authCredential.user.createdDate = this.resultSet.getString("app_login_credential_c_date");

                authCredential.user.address.id = (this.resultSet.getObject("location_id")==null)?0:this.resultSet.getInt("location_id");
                authCredential.user.address.lat = (this.resultSet.getObject("lat")==null)?0:this.resultSet.getDouble("lat");
                authCredential.user.address.lon = (this.resultSet.getObject("lon")==null)?0:this.resultSet.getDouble("lon");
                authCredential.user.address.formattedAddress = (this.resultSet.getObject("formatted_address")==null)?"":this.resultSet.getString("formatted_address");
                authCredential.user.address.countryName = (this.resultSet.getObject("country")==null)?"":this.resultSet.getString("country");
                authCredential.user.address.createdDate = (this.resultSet.getObject("location_c_date")==null)?"":this.resultSet.getString("location_c_date");

                authCredentialList.add(authCredential);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }

        return authCredentialList;
    }
    public int insert(){
        if(this.isNumberExist()){
           return 0;
        }
        this.active = 1;
        this.banned = 0;
        this.access_token = this.phone_number+System.nanoTime();


        String query = "INSERT INTO `app_login_credential` "+
        "(`u_id`, `phone_number`, `access_token`, `active`, `banned`)"+
        " VALUES (" +this.u_id+",'"+this.phone_number+"',md5('"+this.access_token+"'),"+this.active+","+this.banned+")";
        System.out.println(query);
        this.id = this.insertData(query);
        return this.id;
    }
}
