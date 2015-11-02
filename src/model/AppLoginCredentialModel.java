package model;

import com.google.gson.Gson;
import model.datamodel.app.AppCredential;
import model.datamodel.app.AuthCredential;
import model.datamodel.app.OperAppCredential;
import model.datamodel.photo.Pictures;
import org.apache.commons.lang3.StringEscapeUtils;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by mi on 10/1/15.
 */
public class AppLoginCredentialModel extends ImageTalkBaseModel {

    private int    id;
    private int    u_id;
    private String text_status;
    private String phone_number;
    private String access_token;
    private int    active;
    private int    banned;
    private String created_date;
    public  String token;
    private Gson   gson;

    private ArrayList<OperAppCredential> appUserList;

    private ArrayList<String> contactList;


    public AppLoginCredentialModel() {
        super();
        super.tableName = "app_login_credential";
        this.token = "";
        this.id = 0;
        this.u_id = 0;
        this.text_status = "";
        this.phone_number = "";
        this.access_token = "";
        this.active = 0;
        this.banned = 0;
        this.created_date = "";
        this.token = "";

        this.gson = new Gson();
        this.contactList = new ArrayList();

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
        text_status = StringEscapeUtils.escapeEcmaScript(text_status);
        this.text_status = text_status.trim();
        return true;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public boolean setPhone_number(String phone_number) {
        phone_number = phone_number.trim();
        phone_number = phone_number.replaceAll("\\(","");
        phone_number = phone_number.replaceAll("\\+","");
        phone_number = phone_number.replaceAll("\\)","");
        phone_number = phone_number.replaceAll(" ","");
        phone_number = phone_number.replaceAll("-","");

        this.phone_number = phone_number;
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

    public boolean setContactList( ArrayList<String> contactList){

        for(String contact:contactList){
            contact = contact.replaceAll("\\(","");
            contact = contact.replaceAll("\\+","");
            contact = contact.replaceAll("\\)","");
            contact = contact.replaceAll(" ","");
            contact = contact.replaceAll("-","");
            this.contactList.add(contact);
        }
        return true;
    }
    public boolean isPhoneNumberOthers() {
        String query = "select phone_number from " + this.tableName + " where phone_number = " + this.phone_number + " and id != "+this.id+" limit 1";

        this.setQuery(query);
        this.getData();

        try {
            while (this.resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeConnection();
        }
        return false;
    }
    public boolean isPhoneNumberActive() {
        String query = "select phone_number from " + this.tableName + " where phone_number = " + this.phone_number + " limit 1";

        this.setQuery(query);
        this.getData();

        try {
            while (this.resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeConnection();
        }
        return false;
    }
    public boolean isActive() {
        String query = "select active from " + this.tableName + " where id = " + this.id + " limit 1";

        this.setQuery(query);
        this.getData();

        try {
            while (this.resultSet.next()) {
                int active = this.resultSet.getInt("active");
                if (active == 1) {
                    return true;
                } else {
                    return false;
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeConnection();
        }
        return false;
    }
    public boolean isBanned() {
        String query = "select banned from " + this.tableName + " where id = " + this.id + " limit 1";

        this.setQuery(query);
        this.getData();

        try {
            while (this.resultSet.next()) {
                int banned = this.resultSet.getInt("banned");
                if (banned == 1) {
                    return true;
                } else {
                    return false;
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeConnection();
        }
        return false;
    }
    public boolean isIdExist() {
        String query = "select active from " + this.tableName + " where id = " + this.id + " limit 1";

        this.setQuery(query);
        this.getData();

        try {
            while (this.resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeConnection();
        }
        return false;
    }

    public AuthCredential getAuthincatedByAccessToken() {
        AuthCredential authCredential = new AuthCredential();

        String query = "select job.*,user_inf.id as user_inf_id," +
                       " user_inf.created_date as user_inf_c_date,user_inf.f_name,user_inf.l_name,user_inf.pic_path," +
                       " location.id as location_id,location.lat,location.lng,location.formatted_address,location.country,location.created_date as location_c_date," +
                       " app_login_credential.id as app_login_credential_id,app_login_credential.text_status,app_login_credential.access_token,app_login_credential.phone_number," +
                       " app_login_credential.created_date as app_login_credential_c_date  from " + super.tableName + " " +
                       " join user_inf on user_inf.id = app_login_credential.u_id  " +
                       " left join location on location.id = user_inf.address_id " +
                       " left join job on job.app_login_credential_id = app_login_credential.id " +
                       " where app_login_credential.access_token = '" + this.access_token + "' limit 1";

        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
                authCredential.id = this.resultSet.getInt("app_login_credential_id");
                authCredential.textStatus = (this.resultSet.getString("text_status") == null) ? "" : this.resultSet.getString("text_status");
                authCredential.accessToken = this.resultSet.getString("access_token");
                authCredential.phoneNumber = this.resultSet.getString("phone_number");
                authCredential.createdDate = this.getPrcessedTimeStamp(this.resultSet.getTimestamp("app_login_credential_c_date"));

                authCredential.user.id = this.resultSet.getInt("user_inf_id");
                authCredential.user.firstName = this.resultSet.getString("f_name");
                authCredential.user.lastName = this.resultSet.getString("l_name");
                authCredential.user.createdDate = this.getPrcessedTimeStamp(this.resultSet.getTimestamp("user_inf_c_date"));


                try {
                    authCredential.user.picPath = (this.resultSet.getObject("pic_path") == null) ? new Pictures() : this.gson.fromJson(this.resultSet.getString("pic_path"), Pictures.class);
                } catch (Exception ex) {
                    System.out.println("Parse error on picture appCid " + authCredential.id);
                    authCredential.user.picPath.original.path = (this.resultSet.getObject("pic_path") == null) ? "" : this.resultSet.getString("pic_path");
                    ex.printStackTrace();
                }

                authCredential.user.createdDate = this.resultSet.getString("app_login_credential_c_date");

                authCredential.user.address.id = (this.resultSet.getObject("location_id") == null) ? 0 : this.resultSet.getInt("location_id");
                authCredential.user.address.lat = (this.resultSet.getObject("lat") == null) ? 0 : this.resultSet.getDouble("lat");
                authCredential.user.address.lng = (this.resultSet.getObject("lng") == null) ? 0 : this.resultSet.getDouble("lng");
                authCredential.user.address.formattedAddress = (this.resultSet.getObject("formatted_address") == null) ? "" : this.resultSet.getString("formatted_address");
                authCredential.user.address.countryName = (this.resultSet.getObject("country") == null) ? "" : this.resultSet.getString("country");
                try {
                    authCredential.user.address.createdDate = this.getPrcessedTimeStamp(this.resultSet.getTimestamp("location_c_date"));
                }catch(Exception e) {
                    System.out.println(e.getMessage());
                    authCredential.user.address.createdDate = "";
                }

                //job details
                authCredential.job.id = this.resultSet.getInt("job.id");
                authCredential.job.appCredentialId = this.resultSet.getInt("job.app_login_credential_id");
                authCredential.job.title = (this.resultSet.getString("job.title") == null) ? "" : this.resultSet.getString("job.title");
                authCredential.job.description = (this.resultSet.getString("job.description") == null)? "" : this.resultSet.getString("job.description");
                try{
                    authCredential.job.icons = (this.resultSet.getObject("icon")==null || !this.resultSet.getString("icon").trim().startsWith("{"))?new Pictures():this.gson.fromJson(this.resultSet.getString("icon"),Pictures.class);

                }catch (Exception ex){
                    ex.printStackTrace();
                }
                authCredential.job.price = this.resultSet.getFloat("job.price");
                authCredential.job.paymentType = this.resultSet.getInt("job.payment_type");
                try {
                    authCredential.job.createdDate = this.getPrcessedTimeStamp(this.resultSet.getTimestamp("job.created_date"));
                }catch(Exception e) {
                    System.out.println(e.getMessage());
                    authCredential.job.createdDate = "";
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeConnection();
        }
        this.id = authCredential.id;
        return authCredential;
    }

    public boolean isNumberExist() {
        String query = "select * from " + super.tableName + " where phone_number='" + this.phone_number + "' limit 1";

        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
                this.id = this.resultSet.getInt("id");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeConnection();
        }

        return false;
    }

    public AuthCredential getAppCredentialById() {
        AuthCredential authCredential = new AuthCredential();
        String query = "select job.*,user_inf.id as user_inf_id," +
                       " user_inf.created_date as user_inf_c_date,user_inf.f_name,user_inf.l_name,user_inf.pic_path," +
                       " location.id as location_id,location.lat,location.lng,location.formatted_address,location.country,location.created_date as location_c_date," +
                       " app_login_credential.id as app_login_credential_id,app_login_credential.text_status,app_login_credential.access_token,app_login_credential.phone_number," +
                       " app_login_credential.created_date as app_login_credential_c_date  from " + super.tableName + " " +
                       " join user_inf on user_inf.id = app_login_credential.u_id  " +
                       " left join location on location.id = user_inf.address_id " +
                       " left join job on job.app_login_credential_id = app_login_credential.id " +
                       " where app_login_credential.id=" + this.id + " limit 1";

        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
                authCredential.id = this.resultSet.getInt("app_login_credential_id");
                authCredential.textStatus = (this.resultSet.getString("text_status") == null) ? "" : this.resultSet.getString("text_status");
                authCredential.accessToken = this.resultSet.getString("access_token");
                authCredential.phoneNumber = this.resultSet.getString("phone_number");
                authCredential.user.id = this.resultSet.getInt("user_inf_id");
                authCredential.user.firstName = this.resultSet.getString("f_name");
                authCredential.user.lastName = this.resultSet.getString("l_name");
                authCredential.createdDate = this.getPrcessedTimeStamp(this.resultSet.getTimestamp("app_login_credential_c_date"));

                try {
                    authCredential.user.picPath = (this.resultSet.getObject("pic_path") == null) ? new Pictures() : this.gson.fromJson(this.resultSet.getString("pic_path"), Pictures.class);
                } catch (Exception ex) {
                    System.out.println("Parse error on picture appCid " + authCredential.id);
                    authCredential.user.picPath.original.path = (this.resultSet.getObject("pic_path") == null) ? "" : this.resultSet.getString("pic_path");
                    ex.printStackTrace();
                }

                authCredential.user.createdDate = this.resultSet.getString("app_login_credential_c_date");

                authCredential.user.address.id = (this.resultSet.getObject("location_id") == null) ? 0 : this.resultSet.getInt("location_id");
                authCredential.user.address.lat = (this.resultSet.getObject("lat") == null) ? 0 : this.resultSet.getDouble("lat");
                authCredential.user.address.lng = (this.resultSet.getObject("lng") == null) ? 0 : this.resultSet.getDouble("lng");
                authCredential.user.address.formattedAddress = (this.resultSet.getObject("formatted_address") == null) ? "" : this.resultSet.getString("formatted_address");
                authCredential.user.address.countryName = (this.resultSet.getObject("country") == null) ? "" : this.resultSet.getString("country");
                authCredential.user.address.createdDate = (this.resultSet.getObject("location_c_date") == null) ? "" : this.resultSet.getString("location_c_date");

                //job details
                authCredential.job.id = this.resultSet.getInt("job.id");
                authCredential.job.appCredentialId = this.resultSet.getInt("job.app_login_credential_id");
                authCredential.job.title = (this.resultSet.getString("job.title") == null) ? "" : this.resultSet.getString("job.title");
                authCredential.job.description = (this.resultSet.getString("job.description") == null)? "" : this.resultSet.getString("job.description");
                try{
                    authCredential.job.icons = (this.resultSet.getObject("icon")==null || !this.resultSet.getString("icon").trim().startsWith("{"))?new Pictures():this.gson.fromJson(this.resultSet.getString("icon"),Pictures.class);

                }catch (Exception ex){
                    ex.printStackTrace();
                }
                authCredential.job.price = (this.resultSet.getObject("job.price") == null) ? 0 :this.resultSet.getFloat("job.price");
                authCredential.job.paymentType = (this.resultSet.getObject("job.payment_type")== null) ? 0 : this.resultSet.getInt("job.payment_type");
                try {
                    authCredential.job.createdDate = this.getPrcessedTimeStamp(this.resultSet.getTimestamp("job.created_date"));
                }catch(Exception e) {
                    System.out.println(e.getMessage());
                    authCredential.user.address.createdDate = "";
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeConnection();
        }

        return authCredential;
    }

    public ArrayList<AppCredential> getAppCredentialByKeyword(String keyword) {
        ArrayList<AppCredential> appCredentialList = new ArrayList<AppCredential>();
        String query = "select job.*,user_inf.id as user_inf_id," +
                       " user_inf.created_date as user_inf_c_date,user_inf.f_name,user_inf.l_name,user_inf.pic_path," +
                       " location.id as location_id,location.lat,location.lng,location.formatted_address,location.country,location.created_date as location_c_date," +
                       " app_login_credential.id as app_login_credential_id,app_login_credential.text_status,app_login_credential.access_token,app_login_credential.phone_number," +
                       " app_login_credential.created_date as app_login_credential_c_date  from " + super.tableName + " " +
                       " join user_inf on user_inf.id = app_login_credential.u_id  " +
                       " left join location on location.id = user_inf.address_id " +
                       " left join job on job.app_login_credential_id = app_login_credential.id " +
                       " where app_login_credential.id !=" + this.id;

        if (keyword != null && keyword != "") {
            query += " and user_inf.f_name like '%" + keyword + "%'";
        }
        if (this.limit > 0) {
            this.offset = this.offset * this.limit;
            query += " LIMIT " + this.offset + " ," + this.limit + " ";
        }

        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
                AppCredential appCredential = new AppCredential();
                appCredential.id = this.resultSet.getInt("app_login_credential_id");
                appCredential.textStatus = (this.resultSet.getString("text_status") == null) ? "" : this.resultSet.getString("text_status");
                appCredential.phoneNumber = this.resultSet.getString("phone_number");
                appCredential.user.id = this.resultSet.getInt("user_inf_id");
                appCredential.user.firstName = this.resultSet.getString("f_name");
                appCredential.user.lastName = this.resultSet.getString("l_name");
                try {
                    appCredential.user.picPath = (this.resultSet.getObject("pic_path") == null) ? new Pictures() : this.gson.fromJson(this.resultSet.getString("pic_path"), Pictures.class);
                } catch (Exception ex) {
                    System.out.println("Parse error on picture appCid " + appCredential.id);
                    appCredential.user.picPath.original.path = (this.resultSet.getObject("pic_path") == null) ? "" : this.resultSet.getString("pic_path");
                    ex.printStackTrace();
                }
                appCredential.user.createdDate = this.resultSet.getString("app_login_credential_c_date");

                appCredential.user.address.id = (this.resultSet.getObject("location_id") == null) ? 0 : this.resultSet.getInt("location_id");
                appCredential.user.address.lat = (this.resultSet.getObject("lat") == null) ? 0 : this.resultSet.getDouble("lat");
                appCredential.user.address.lng = (this.resultSet.getObject("lng") == null) ? 0 : this.resultSet.getDouble("lng");
                appCredential.user.address.formattedAddress = (this.resultSet.getObject("formatted_address") == null) ? "" : this.resultSet.getString("formatted_address");
                appCredential.user.address.countryName = (this.resultSet.getObject("country") == null) ? "" : this.resultSet.getString("country");
                appCredential.user.address.createdDate = (this.resultSet.getObject("location_c_date") == null) ? "" : this.resultSet.getString("location_c_date");

                //job details
                appCredential.job.id = this.resultSet.getInt("job.id");
                appCredential.job.appCredentialId = this.resultSet.getInt("job.app_login_credential_id");
                appCredential.job.title = (this.resultSet.getString("job.title") == null) ? "" : this.resultSet.getString("job.title");
                appCredential.job.description = (this.resultSet.getString("job.description") == null)? "" : this.resultSet.getString("job.description");
                try{
                    appCredential.job.icons = (this.resultSet.getObject("icon")==null || !this.resultSet.getString("icon").trim().startsWith("{"))?new Pictures():this.gson.fromJson(this.resultSet.getString("icon"),Pictures.class);

                }catch (Exception ex){
                    ex.printStackTrace();
                }
                appCredential.job.price = (this.resultSet.getObject("job.price") == null) ? 0 :this.resultSet.getFloat("job.price");
                appCredential.job.paymentType = (this.resultSet.getObject("job.payment_type")== null) ? 0 : this.resultSet.getInt("job.payment_type");
                try {
                    appCredential.job.createdDate = this.getPrcessedTimeStamp(this.resultSet.getTimestamp("job.created_date"));
                }catch(Exception e) {
                    System.out.println(e.getMessage());
                    appCredential.job.createdDate = "";
                }

                appCredentialList.add(appCredential);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeConnection();
        }

        return appCredentialList;
    }

    public int insert() {

        if (this.isNumberExist()) {
            return 0;
        }
        this.active = 1;
        this.banned = 0;
        this.access_token = this.phone_number + System.nanoTime();


        String query = "INSERT INTO `app_login_credential` " +
                       "(`u_id`, `phone_number`, `access_token`, `active`, `banned`)" +
                       " VALUES (" + this.u_id + ",'" + this.phone_number + "',md5('" + this.access_token + "')," + this.active + "," + this.banned + ")";

        this.id = this.insertData(query);
        return this.id;
    }

    public ArrayList<OperAppCredential> getAppUser(String type) {
        String where;
        switch (type.toLowerCase()) {
            case "active":
                where = "WHERE active = 1";
                break;
            case "inactive":
                where = "WHERE active = 0";
                break;
            case "banned":
                where = "WHERE banned = 1";
                break;
            default:
                where = "";
        }

        this.appUserList = new ArrayList<>();
        String sql = "SELECT * FROM " + this.tableName + " AS ul " +
                     "JOIN user_inf AS ui " +
                     "ON ul.u_id = ui.id " +
                     "LEFT JOIN location AS l " +
                     "ON ui.address_id = l.id " + where;
        this.setQuery(sql);
        this.getData();

        try {
            while (this.resultSet.next()) {
                OperAppCredential appUser = new OperAppCredential();
                appUser.banned = (resultSet.getInt("ul.banned") == 1) ? true : false;
                appUser.active = (resultSet.getInt("ul.active") == 1) ? true : false;
                appUser.id = resultSet.getInt("ul.id");
                appUser.textStatus = resultSet.getString("ul.text_status");
                appUser.phoneNumber = resultSet.getString("ul.phone_number");
                appUser.user.firstName = resultSet.getString("ui.f_name");
                appUser.user.lastName = resultSet.getString("ui.l_name");
                try {

                    appUser.user.picPath = (this.resultSet.getObject("pic_path")==null)?new Pictures():this.gson.fromJson(this.resultSet.getString("pic_path"), Pictures.class);
                }catch (Exception ex){
                    System.out.println("Parse error on picture appCid "+ appUser.id);
                    appUser.user.picPath.original.path =(this.resultSet.getObject("pic_path")==null)?"":this.resultSet.getString("pic_path");
                    ex.printStackTrace();
                }

                appUser.user.createdDate = resultSet.getString("ui.created_date");
                appUser.user.address.id = resultSet.getInt("l.id");
                appUser.user.address.lat = resultSet.getDouble("l.lat");
                appUser.user.address.lng = resultSet.getDouble("l.lng");
                appUser.user.address.formattedAddress = resultSet.getString("l.formatted_address");
                appUser.user.address.countryName = resultSet.getString("l.country");

                this.appUserList.add(appUser);
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }

        return this.appUserList;
    }
    public String getUserTextStatusById() {
        String sql = "SELECT text_status FROM " + this.tableName + " WHERE id = " + id;
        this.setQuery(sql);
        this.getData();

        try {
            while (this.resultSet.next()) {
                return resultSet.getString("text_status");
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }

        return "";
    }
    public int getUserStatusById(int id) {
        String sql = "SELECT * FROM " + this.tableName + " WHERE u_id = " + id;
        this.setQuery(sql);
        this.getData();

        try {
            while (this.resultSet.next()) {
                return resultSet.getInt("banned");
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }

        return 0;
    }
    public ArrayList<AppCredential> getMatchedPhoneNumber(){
        ArrayList<AppCredential> appCredentialList = new ArrayList<>();
        String contactIdIn = "";
        int i = 0;
        for(String contact : this.contactList){
            i++;
            contactIdIn += "'"+contact+"'";
            if(i<this.contactList.size()){
                contactIdIn +=",";
            }

        }
        if(contactIdIn==""){
            return appCredentialList;
        }

        ContactModel contactModel = new ContactModel();
        contactModel.setOwner_id(this.id);
        String query = "select job.*,user_inf.id as user_inf_id," +
                " user_inf.created_date as user_inf_c_date,user_inf.f_name,user_inf.l_name,user_inf.pic_path," +
                " location.id as location_id,location.lat,location.lng,location.formatted_address,location.country,location.created_date as location_c_date," +
                " app_login_credential.id as app_login_credential_id,app_login_credential.text_status,app_login_credential.access_token,app_login_credential.phone_number," +
                " app_login_credential.created_date as app_login_credential_c_date  from " + super.tableName + " " +
                " join user_inf on user_inf.id = app_login_credential.u_id  " +
                " left join location on location.id = user_inf.address_id " +
                " left join job on job.app_login_credential_id = app_login_credential.id " +
                " where app_login_credential.phone_number in  (" + contactIdIn+" ) ";
        String contactIdStr = contactModel.getContactInStrArray();
        if(contactIdStr!=""){
            query += " and app_login_credential.id not in ("+contactIdStr +")";
        }


        this.setQuery(query);
        this.getData();

        try {
            while (this.resultSet.next()) {
                AppCredential appCredential = new AppCredential();
                appCredential.id = this.resultSet.getInt("app_login_credential_id");
                appCredential.textStatus = (this.resultSet.getString("text_status") == null) ? "" : this.resultSet.getString("text_status");
                appCredential.phoneNumber = this.resultSet.getString("phone_number");
                appCredential.user.id = this.resultSet.getInt("user_inf_id");
                appCredential.user.firstName = this.resultSet.getString("f_name");
                appCredential.user.lastName = this.resultSet.getString("l_name");
                try {
                    appCredential.user.picPath = (this.resultSet.getObject("pic_path") == null) ? new Pictures() : this.gson.fromJson(this.resultSet.getString("pic_path"), Pictures.class);
                } catch (Exception ex) {
                    System.out.println("Parse error on picture appCid " + appCredential.id);
                    appCredential.user.picPath.original.path = (this.resultSet.getObject("pic_path") == null) ? "" : this.resultSet.getString("pic_path");
                    ex.printStackTrace();
                }
                appCredential.user.createdDate = this.resultSet.getString("app_login_credential_c_date");

                appCredential.user.address.id = (this.resultSet.getObject("location_id") == null) ? 0 : this.resultSet.getInt("location_id");
                appCredential.user.address.lat = (this.resultSet.getObject("lat") == null) ? 0 : this.resultSet.getDouble("lat");
                appCredential.user.address.lng = (this.resultSet.getObject("lng") == null) ? 0 : this.resultSet.getDouble("lng");
                appCredential.user.address.formattedAddress = (this.resultSet.getObject("formatted_address") == null) ? "" : this.resultSet.getString("formatted_address");
                appCredential.user.address.countryName = (this.resultSet.getObject("country") == null) ? "" : this.resultSet.getString("country");
                appCredential.user.address.createdDate = (this.resultSet.getObject("location_c_date") == null) ? "" : this.resultSet.getString("location_c_date");

                //job details
                appCredential.job.id = this.resultSet.getInt("job.id");
                appCredential.job.appCredentialId = this.resultSet.getInt("job.app_login_credential_id");
                appCredential.job.title = (this.resultSet.getString("job.title") == null) ? "" : this.resultSet.getString("job.title");
                appCredential.job.description = (this.resultSet.getString("job.description") == null)? "" : this.resultSet.getString("job.description");
                try{
                    appCredential.job.icons = (this.resultSet.getObject("icon")==null || !this.resultSet.getString("icon").trim().startsWith("{"))?new Pictures():this.gson.fromJson(this.resultSet.getString("icon"),Pictures.class);

                }catch (Exception ex){
                    ex.printStackTrace();
                }
                appCredential.job.price = (this.resultSet.getObject("job.price") == null) ? 0 :this.resultSet.getFloat("job.price");
                appCredential.job.paymentType = (this.resultSet.getObject("job.payment_type")== null) ? 0 : this.resultSet.getInt("job.payment_type");
                try {
                    appCredential.job.createdDate = this.getPrcessedTimeStamp(this.resultSet.getTimestamp("job.created_date"));
                }catch(Exception e) {
                    System.out.println(e.getMessage());
                    appCredential.job.createdDate = "";
                }

                appCredentialList.add(appCredential);
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return appCredentialList;
    }
    public boolean updateUserStatus() {
        String sql = "UPDATE " + this.tableName + " SET banned = '" + this.banned + "' WHERE  id =" + this.id;
        return this.updateData(sql);
    }
    public boolean updateUserTextStatus() {
        String sql = "UPDATE " + this.tableName + " SET text_status = '" + this.text_status + "' WHERE  id =" + this.id;
        return this.updateData(sql);
    }
    public boolean updatePhoneNumber() {
        String sql = "UPDATE " + this.tableName + " SET phone_number = '" + this.phone_number + "' WHERE  id =" + this.id;

        return this.updateData(sql);
    }
}
