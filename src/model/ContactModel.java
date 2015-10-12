package model;

import com.google.gson.Gson;
import model.datamodel.app.AppCredential;
import model.datamodel.photo.Pictures;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by mi on 10/12/15.
 */
public class ContactModel extends ImageTalkBaseModel {

    private int id;
    private String nickname;
    private int owner_id;
    private int contact_id;
    private boolean favorites;
    private boolean is_block;
    private String created_date;
    private int  rating;

    private Gson gson;
    private ArrayList<Integer> contactIdList;


    public ContactModel() {
        this.tableName = "contact";

        this.id = 0;
        this.nickname = "";
        this.owner_id = 0;
        this.contact_id = 0;
        this.favorites = false;
        this.is_block = false;
        this.created_date = "";
        this.rating = 0;

        this.gson = new Gson();
        this.contactIdList= new ArrayList();
    }

    public int getId() {
        return id;
    }

    public boolean setId(int id) {
        this.id = id;
        return true;
    }

    public String getNickname() {
        return nickname;
    }

    public boolean setNickname(String nickname) {
        this.nickname = nickname;
        return true;
    }

    public int getOwner_id() {
        return owner_id;
    }

    public boolean setOwner_id(int owner_id) {
        this.owner_id = owner_id;
        return true;
    }

    public int getContact_id() {
        return contact_id;
    }

    public boolean setContact_id(int contact_id) {
        this.contact_id = contact_id;
        return true;
    }

    public boolean isFavorites() {
        return favorites;
    }

    public void setFavorites(boolean favorites) {
        this.favorites = favorites;
    }

    public boolean is_block() {
        return is_block;
    }

    public boolean setIs_block(boolean is_block) {
        this.is_block = is_block;
        return true;
    }

    public String getCreated_date() {
        return created_date;
    }

    public boolean setCreated_date(String created_date) {
        this.created_date = created_date;
        return true;
    }

    public int getRating() {
        return rating;
    }

    public boolean setRating(int rating) {
        this.rating = rating;
        return true;
    }

    public ArrayList<Integer> getContactIdList() {
        return contactIdList;
    }

    public boolean setContactIdList(ArrayList<Integer> contactList) {
        this.contactIdList = contactList;
        return true;
    }

    public int insert(){
        String query = "INSERT INTO `contact`(`nickname`, `owner_id`, `contact_id`, `favorites`, `is_block`, `rating`) " +
                "VALUES ('"+this.nickname+"',"+this.owner_id+","+this.contact_id+","+this.favorites+","+this.is_block+","+this.rating+")";
        this.id = this.insertData(query);
        return this.id;
    }
    public boolean isExist(){
        String query = "select id from " + this.tableName + " where contact_id = " + this.contact_id + " and owner_id = "+this.owner_id+" limit 1";

        this.setQuery(query);
        this.getData();
        System.out.println(query);
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
    public String getContactInStrArray(String keyword) {
        String contactStr = "";
        int i = 0;
        String query = "select contact_id  from " + super.tableName + " " +
                " where  " + super.tableName + ".owner_id="+ this.owner_id;


        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
                i++;
                if(i<this.resultSet.)
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeConnection();
        }

        return appCredentialList;
    }
    public ArrayList<AppCredential> getContactByKeyword(String keyword) {
        ArrayList<AppCredential> appCredentialList = new ArrayList<AppCredential>();
        String query = "select user_inf.id as user_inf_id," +
                " user_inf.created_date as user_inf_c_date,user_inf.f_name,user_inf.l_name,user_inf.pic_path," +
                " location.id as location_id,location.lat,location.lng,location.formatted_address,location.country,location.created_date as location_c_date," +
                " app_login_credential.id as app_login_credential_id,app_login_credential.text_status,app_login_credential.access_token,app_login_credential.phone_number," +
                " app_login_credential.created_date as app_login_credential_c_date" +
                "  from " + super.tableName + " " +
                " join app_login_credential on  app_login_credential.id  = " + super.tableName + ".contact_id  "+
                " join user_inf on user_inf.id = app_login_credential.u_id  " +
                " left join location on location.id = user_inf.address_id " +
                " where  " + super.tableName + ".owner_id="+ this.owner_id;

        if (keyword != null && keyword != "") {
            query += " and user_inf.f_name like '%" + keyword + "%'";
        }
        if (this.limit > 0) {
            this.offset = this.offset * this.limit;
            query += " LIMIT " + this.offset + " ," + this.limit + " ";
        }
        System.out.println(query);
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

                appCredentialList.add(appCredential);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeConnection();
        }

        return appCredentialList;
    }
    public boolean addContact(){
        this.startTransaction();
        AppLoginCredentialModel appLoginCredentialModel = new AppLoginCredentialModel();
        for(int contactId : this.contactIdList ){
            this.contact_id = contactId;
            appLoginCredentialModel.setId(this.contact_id);
            if(this.contact_id == this.owner_id){
                this.rollBack();
                this.errorObj.errStatus = false;
                this.errorObj.msg = "You are not allowed to add yourself in contact";
                return false;
            }
            if(!appLoginCredentialModel.isIdExist()){
                this.rollBack();
                this.errorObj.errStatus = false;
                this.errorObj.msg = "Contact id ' "+this.contact_id +" ' not found in system";
                return false;
            }
            if(this.isExist()){
                this.rollBack();
                this.errorObj.errStatus = false;
                this.errorObj.msg = "Already in contact list, id "+this.contact_id;
                return false;
            }
            if(this.insert()==0){
                this.errorObj.errStatus = false;
                this.errorObj.msg = "Internal server error";
                return false;
            }
        }
        this.commitTransaction();
        return true;
    }
}
