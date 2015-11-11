package model;

import com.google.gson.Gson;
import com.sun.org.apache.xpath.internal.operations.Bool;
import model.datamodel.app.AppCredential;
import model.datamodel.app.Contact;
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

    private String keyword;

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
        this.keyword = "";

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

    public boolean getIs_block() {
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

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public ArrayList<Integer> getContactIdList() {
        return contactIdList;
    }

    public boolean setContactIdList(ArrayList<Integer> contactList) {
        this.contactIdList = contactList;
        return true;
    }

    public int insert(){
        String query = "INSERT INTO " + this.tableName + " (`nickname`, `owner_id`, `contact_id`, `favorites`, `is_block`, `rating`) " +
                "VALUES ('"+this.nickname+"',"+this.owner_id+","+this.contact_id+","+this.favorites+","+this.is_block+","+this.rating+")";
        this.id = this.insertData(query);
        return this.id;
    }
    public int delete(){
        String query = "DELETE FROM " + this.tableName + "  " +
                "where owner_id = "+this.owner_id+" and contact_id="+this.contact_id+" limit 1";

        return this.deleteData(query);
    }
    public int updateIsBlock(){
        String query = "update  " + this.tableName + " set is_block =  " +this.is_block +
                " where owner_id = "+this.owner_id+" and contact_id="+this.contact_id+" limit 1";

        return this.deleteData(query);
    }

    public int updateFavorites(){
        String query = "update  " + this.tableName + " set favorites = " +this.favorites+
                " where owner_id = "+this.owner_id+" and contact_id="+this.contact_id+" limit 1";

        return this.deleteData(query);
    }

    public boolean isExist() {
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
    public String getContactInStrArray() {
        String contactStr = "";
        ArrayList<String> contacts = new ArrayList();
        int i = 0;
        String query = "select contact_id  from " + super.tableName + " " +
                " where  " + super.tableName + ".owner_id="+ this.owner_id;
        System.out.println(query);

        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
                contacts.add(String.valueOf(this.resultSet.getInt("contact_id")));
            }
            for(String contact : contacts){
                i++;
                contactStr +=contact;
                if(i<contacts.size()){
                    contactStr +=",";
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeConnection();
        }
        System.out.println(contactStr);
        return contactStr;
    }
    public ArrayList<AppCredential> getContactByOwnerId() {
        ArrayList<AppCredential> appCredentialList = new ArrayList<AppCredential>();
        String query = "select user_inf.id as user_inf_id," +
                " user_inf.created_date as user_inf_c_date,user_inf.f_name,user_inf.l_name,user_inf.pic_path," +
                " location.id as location_id,location.lat,location.lng,location.formatted_address,location.country,location.created_date as location_c_date," +
                " app_login_credential.id as app_login_credential_id,app_login_credential.text_status,app_login_credential.access_token,app_login_credential.phone_number," +
                " app_login_credential.created_date as app_login_credential_c_date,job.*" +
                "  from " + super.tableName + " " +
                " join app_login_credential on  app_login_credential.id  = " + super.tableName + ".contact_id  "+
                " join user_inf on user_inf.id = app_login_credential.u_id  " +
                " left join location on location.id = user_inf.address_id " +
                " left join job on job.app_login_credential_id = app_login_credential.id " +
                " where  " + super.tableName + ".owner_id="+ this.owner_id;


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

                //job details
                appCredential.job.id =(this.resultSet.getObject("job.id")==null)?0:this.resultSet.getInt("job.id");
                appCredential.job.appCredentialId = (this.resultSet.getObject("job.app_login_credential_id")==null)?0:this.resultSet.getInt("job.app_login_credential_id");
                appCredential.job.title = (this.resultSet.getObject("job.title") == null) ? "" : this.resultSet.getString("job.title");
                appCredential.job.description = (this.resultSet.getObject("job.description") == null)? "" : this.resultSet.getString("job.description");
                try{
                    appCredential.job.icons = (this.resultSet.getObject("job.icon")==null || !this.resultSet.getString("job.icon").trim().startsWith("{"))?new Pictures():this.gson.fromJson(this.resultSet.getString("job.icon"),Pictures.class);

                }catch (Exception ex){
                    ex.printStackTrace();
                }
                appCredential.job.price = (this.resultSet.getObject("job.price") == null)?0:this.resultSet.getFloat("job.price");
                appCredential.job.paymentType = (this.resultSet.getObject("job.payment_type") == null)?0:this.resultSet.getInt("job.payment_type");
                try {
                    appCredential.job.createdDate = (this.resultSet.getObject("job.created_date") == null)?"":this.getPrcessedTimeStamp(this.resultSet.getTimestamp("job.created_date"));
                }catch(Exception e) {
                    System.out.println(e.getMessage());
                    appCredential.job.createdDate = "";
                }
                //end job details

                appCredentialList.add(appCredential);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeConnection();
        }

        return appCredentialList;
    }

    public Contact getContactByOwnerId(int owner,int recipient)
    {
        Contact contact = new Contact();
        String query = "select " +this.tableName+".*,"+
                " user_inf.id as user_inf_id," +
                " user_inf.created_date as user_inf_c_date,user_inf.f_name,user_inf.l_name,user_inf.pic_path," +
                " location.id as location_id,location.lat,location.lng,location.formatted_address,location.country,location.created_date as location_c_date," +
                " app_login_credential.id as app_login_credential_id,app_login_credential.text_status,app_login_credential.access_token,app_login_credential.phone_number," +
                " app_login_credential.created_date as app_login_credential_c_date,job.*" +
                "  from " + this.tableName + " " +
                " join app_login_credential on  app_login_credential.id  = " + this.tableName + ".contact_id  "+
                " join user_inf on user_inf.id = app_login_credential.u_id  " +
                " left join location on location.id = user_inf.address_id " +
                " left join job on job.app_login_credential_id = app_login_credential.id " +
                " where  " + this.tableName + ".owner_id="+ owner+" AND "+this.tableName+".contact_id="+recipient;

        System.out.println(query);
        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next())
            {
                Byte tempFavorites = this.resultSet.getByte(this.tableName + ".favorites");
                Byte tempIsBlock =this.resultSet.getByte(this.tableName + ".is_block");



                contact.contactId = this.resultSet.getInt(this.tableName + ".id");
                contact.nickName = this.resultSet.getString(this.tableName + ".nickname");
                contact.favorites = (tempFavorites.intValue()>0);
                contact.isBlock = (tempIsBlock.intValue()>0);
                contact.rating = this.resultSet.getInt(this.tableName + ".rating");

                contact.id = this.resultSet.getInt("app_login_credential_id");
                contact.textStatus = (this.resultSet.getString("text_status") == null) ? "" : this.resultSet.getString("text_status");
                contact.phoneNumber = this.resultSet.getString("phone_number");
                contact.user.id = this.resultSet.getInt("user_inf_id");
                contact.user.firstName = this.resultSet.getString("f_name");
                contact.user.lastName = this.resultSet.getString("l_name");

                try {
                    contact.user.picPath = (this.resultSet.getObject("pic_path") == null) ? new Pictures() : this.gson.fromJson(this.resultSet.getString("pic_path"), Pictures.class);
                } catch (Exception ex) {
                    System.out.println("Parse error on picture appCid " + contact.id);
                    contact.user.picPath.original.path = (this.resultSet.getObject("pic_path") == null) ? "" : this.resultSet.getString("pic_path");
                    ex.printStackTrace();
                }
                contact.user.createdDate = this.resultSet.getString("app_login_credential_c_date");

                contact.user.address.id = (this.resultSet.getObject("location_id") == null) ? 0 : this.resultSet.getInt("location_id");
                contact.user.address.lat = (this.resultSet.getObject("lat") == null) ? 0 : this.resultSet.getDouble("lat");
                contact.user.address.lng = (this.resultSet.getObject("lng") == null) ? 0 : this.resultSet.getDouble("lng");
                contact.user.address.formattedAddress = (this.resultSet.getObject("formatted_address") == null) ? "" : this.resultSet.getString("formatted_address");
                contact.user.address.countryName = (this.resultSet.getObject("country") == null) ? "" : this.resultSet.getString("country");
                contact.user.address.createdDate = (this.resultSet.getObject("location_c_date") == null) ? "" : this.resultSet.getString("location_c_date");

                //job details
                contact.job.id =(this.resultSet.getObject("job.id")==null)?0:this.resultSet.getInt("job.id");
                contact.job.appCredentialId = (this.resultSet.getObject("job.app_login_credential_id")==null)?0:this.resultSet.getInt("job.app_login_credential_id");
                contact.job.title = (this.resultSet.getObject("job.title") == null) ? "" : this.resultSet.getString("job.title");
                contact.job.description = (this.resultSet.getObject("job.description") == null)? "" : this.resultSet.getString("job.description");
                try{
                    contact.job.icons = (this.resultSet.getObject("job.icon")==null || !this.resultSet.getString("job.icon").trim().startsWith("{"))?new Pictures():this.gson.fromJson(this.resultSet.getString("job.icon"),Pictures.class);

                }catch (Exception ex){
                    ex.printStackTrace();
                }
                contact.job.price = (this.resultSet.getObject("job.price") == null)?0:this.resultSet.getFloat("job.price");
                contact.job.paymentType = (this.resultSet.getObject("job.payment_type") == null)?0:this.resultSet.getInt("job.payment_type");
                try {
                    contact.job.createdDate = (this.resultSet.getObject("job.created_date") == null)?"":this.getPrcessedTimeStamp(this.resultSet.getTimestamp("job.created_date"));
                }catch(Exception e) {
                    System.out.println(e.getMessage());
                    contact.job.createdDate = "";
                }
                //end job details

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            this.closeConnection();
        }
        return contact;
    }
    public ArrayList<Contact> getContactByKeyword(String keyword) {
        ArrayList<Contact> contactList = new ArrayList<Contact>();
        String query = "select " +super.tableName+".*,"+
                " user_inf.id as user_inf_id," +
                " user_inf.created_date as user_inf_c_date,user_inf.f_name,user_inf.l_name,user_inf.pic_path," +
                " location.id as location_id,location.lat,location.lng,location.formatted_address,location.country,location.created_date as location_c_date," +
                " app_login_credential.id as app_login_credential_id,app_login_credential.text_status,app_login_credential.access_token,app_login_credential.phone_number," +
                " app_login_credential.created_date as app_login_credential_c_date,job.*" +
                "  from " + super.tableName + " " +
                " join app_login_credential on  app_login_credential.id  = " + super.tableName + ".contact_id  "+
                " join user_inf on user_inf.id = app_login_credential.u_id  " +
                " left join location on location.id = user_inf.address_id " +
                " left join job on job.app_login_credential_id = app_login_credential.id " +
                " where  " + super.tableName + ".owner_id="+ this.owner_id;

        if (keyword != null && keyword != "") {
            query += " and ( user_inf.f_name like '%" + keyword + "%' or user_inf.l_name like '%" + keyword + "%' )";
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

                Byte tempFavorites = this.resultSet.getByte(super.tableName + ".favorites");
                Byte tempIsBlock =this.resultSet.getByte(super.tableName + ".is_block");

                Contact contact = new Contact();

                contact.contactId = this.resultSet.getInt(super.tableName + ".id");
                contact.nickName = this.resultSet.getString(super.tableName + ".nickname");
                contact.favorites = (tempFavorites.intValue()>0);
                contact.isBlock = (tempIsBlock.intValue()>0);
                contact.rating = this.resultSet.getInt(super.tableName + ".rating");

                contact.id = this.resultSet.getInt("app_login_credential_id");
                contact.textStatus = (this.resultSet.getString("text_status") == null) ? "" : this.resultSet.getString("text_status");
                contact.phoneNumber = this.resultSet.getString("phone_number");
                contact.user.id = this.resultSet.getInt("user_inf_id");
                contact.user.firstName = this.resultSet.getString("f_name");
                contact.user.lastName = this.resultSet.getString("l_name");

                try {
                    contact.user.picPath = (this.resultSet.getObject("pic_path") == null) ? new Pictures() : this.gson.fromJson(this.resultSet.getString("pic_path"), Pictures.class);
                } catch (Exception ex) {
                    System.out.println("Parse error on picture appCid " + contact.id);
                    contact.user.picPath.original.path = (this.resultSet.getObject("pic_path") == null) ? "" : this.resultSet.getString("pic_path");
                    ex.printStackTrace();
                }
                contact.user.createdDate = this.resultSet.getString("app_login_credential_c_date");

                contact.user.address.id = (this.resultSet.getObject("location_id") == null) ? 0 : this.resultSet.getInt("location_id");
                contact.user.address.lat = (this.resultSet.getObject("lat") == null) ? 0 : this.resultSet.getDouble("lat");
                contact.user.address.lng = (this.resultSet.getObject("lng") == null) ? 0 : this.resultSet.getDouble("lng");
                contact.user.address.formattedAddress = (this.resultSet.getObject("formatted_address") == null) ? "" : this.resultSet.getString("formatted_address");
                contact.user.address.countryName = (this.resultSet.getObject("country") == null) ? "" : this.resultSet.getString("country");
                contact.user.address.createdDate = (this.resultSet.getObject("location_c_date") == null) ? "" : this.resultSet.getString("location_c_date");

                //job details
                contact.job.id =(this.resultSet.getObject("job.id")==null)?0:this.resultSet.getInt("job.id");
                contact.job.appCredentialId = (this.resultSet.getObject("job.app_login_credential_id")==null)?0:this.resultSet.getInt("job.app_login_credential_id");
                contact.job.title = (this.resultSet.getObject("job.title") == null) ? "" : this.resultSet.getString("job.title");
                contact.job.description = (this.resultSet.getObject("job.description") == null)? "" : this.resultSet.getString("job.description");
                try{
                    contact.job.icons = (this.resultSet.getObject("job.icon")==null || !this.resultSet.getString("job.icon").trim().startsWith("{"))?new Pictures():this.gson.fromJson(this.resultSet.getString("job.icon"),Pictures.class);

                }catch (Exception ex){
                    ex.printStackTrace();
                }
                contact.job.price = (this.resultSet.getObject("job.price") == null)?0:this.resultSet.getFloat("job.price");
                contact.job.paymentType = (this.resultSet.getObject("job.payment_type") == null)?0:this.resultSet.getInt("job.payment_type");
                try {
                    contact.job.createdDate = (this.resultSet.getObject("job.created_date") == null)?"":this.getPrcessedTimeStamp(this.resultSet.getTimestamp("job.created_date"));
                }catch(Exception e) {
                    System.out.println(e.getMessage());
                    contact.job.createdDate = "";
                }
                //end job details

                contactList.add(contact);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeConnection();
        }

        return contactList;
    }

    public ArrayList<Contact> getWhoHasMyContactByOwnerId() {
        ArrayList<Contact> contacts = new ArrayList<Contact>();
        String query = "select " + super.tableName + ".*, " +
                "user_inf.id as user_inf_id," +
                " user_inf.created_date as user_inf_c_date,user_inf.f_name,user_inf.l_name,user_inf.pic_path," +
                " location.id as location_id,location.lat,location.lng,location.formatted_address,location.country,location.created_date as location_c_date," +
                " app_login_credential.id as app_login_credential_id,app_login_credential.text_status,app_login_credential.access_token,app_login_credential.phone_number," +
                " app_login_credential.created_date as app_login_credential_c_date,job.*" +
                "  from " + super.tableName + " " +
                " join app_login_credential on  app_login_credential.id  = " + super.tableName + ".owner_id  " +
                " join user_inf on user_inf.id = app_login_credential.u_id  " +
                " left join location on location.id = user_inf.address_id " +
                " left join job on job.app_login_credential_id = app_login_credential.id " +
                " where  " + super.tableName + ".contact_id="+ this.owner_id+" AND "+super.tableName+".owner_id NOT IN " +
                "( SELECT "+super.tableName+".contact_id " +
                "FROM "+super.tableName+" WHERE "+super.tableName+".owner_id="+this.owner_id+")";

        if (this.keyword != null && this.keyword != "") {
            query += " and ( user_inf.f_name like '%" + this.keyword + "%' or user_inf.l_name like '%" + this.keyword + "%' )";
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
                Byte tempFavorites = this.resultSet.getByte(super.tableName + ".favorites");
                Byte tempIsBlock =this.resultSet.getByte(super.tableName + ".is_block");

                Contact contact = new Contact();

                contact.id = this.resultSet.getInt(super.tableName + ".id");
                contact.nickName = this.resultSet.getString(super.tableName + ".nickname");
                contact.favorites = (tempFavorites.intValue()>0);
                contact.isBlock = (tempIsBlock.intValue()>0);
                contact.rating = this.resultSet.getInt(super.tableName + ".rating");

                contact.id = this.resultSet.getInt("app_login_credential_id");
                contact.textStatus = (this.resultSet.getString("text_status") == null) ? "" : this.resultSet.getString("text_status");
                contact.phoneNumber = this.resultSet.getString("phone_number");
                contact.user.id = this.resultSet.getInt("user_inf_id");
                contact.user.firstName = this.resultSet.getString("f_name");
                contact.user.lastName = this.resultSet.getString("l_name");
                try {
                    contact.user.picPath = (this.resultSet.getObject("pic_path") == null) ? new Pictures() : this.gson.fromJson(this.resultSet.getString("pic_path"), Pictures.class);
                } catch (Exception ex) {
                    System.out.println("Parse error on picture appCid " + contact.id);
                    contact.user.picPath.original.path = (this.resultSet.getObject("pic_path") == null) ? "" : this.resultSet.getString("pic_path");
                    ex.printStackTrace();
                }
                contact.user.createdDate = this.resultSet.getString("app_login_credential_c_date");

                contact.user.address.id = (this.resultSet.getObject("location_id") == null) ? 0 : this.resultSet.getInt("location_id");
                contact.user.address.lat = (this.resultSet.getObject("lat") == null) ? 0 : this.resultSet.getDouble("lat");
                contact.user.address.lng = (this.resultSet.getObject("lng") == null) ? 0 : this.resultSet.getDouble("lng");
                contact.user.address.formattedAddress = (this.resultSet.getObject("formatted_address") == null) ? "" : this.resultSet.getString("formatted_address");
                contact.user.address.countryName = (this.resultSet.getObject("country") == null) ? "" : this.resultSet.getString("country");
                contact.user.address.createdDate = (this.resultSet.getObject("location_c_date") == null) ? "" : this.resultSet.getString("location_c_date");

                //job details
                contact.job.id =(this.resultSet.getObject("job.id")==null)?0:this.resultSet.getInt("job.id");
                contact.job.appCredentialId = (this.resultSet.getObject("job.app_login_credential_id")==null)?0:this.resultSet.getInt("job.app_login_credential_id");
                contact.job.title = (this.resultSet.getObject("job.title") == null) ? "" : this.resultSet.getString("job.title");
                contact.job.description = (this.resultSet.getObject("job.description") == null)? "" : this.resultSet.getString("job.description");
                try{
                    contact.job.icons = (this.resultSet.getObject("job.icon")==null || !this.resultSet.getString("job.icon").trim().startsWith("{"))?new Pictures():this.gson.fromJson(this.resultSet.getString("job.icon"),Pictures.class);

                }catch (Exception ex){
                    ex.printStackTrace();
                }
                contact.job.price = (this.resultSet.getObject("job.price") == null)?0:this.resultSet.getFloat("job.price");
                contact.job.paymentType = (this.resultSet.getObject("job.payment_type") == null)?0:this.resultSet.getInt("job.payment_type");
                try {
                    contact.job.createdDate = (this.resultSet.getObject("job.created_date") == null)?"":this.getPrcessedTimeStamp(this.resultSet.getTimestamp("job.created_date"));
                }catch(Exception e) {
                    System.out.println(e.getMessage());
                    contact.job.createdDate = "";
                }
                //end job details


                contacts.add(contact);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeConnection();
        }

        return contacts;
    }
    public ArrayList<Contact> getWhoDoesNotHasMyContactByOwnerId() {
        ArrayList<Contact> contacts = new ArrayList<Contact>();
        String contactIdStr = "";

        ArrayList<Integer> contactIds = this.getIdArrayOfWhoHasMyContactByOwnerId();
        int i =0;


        for(Integer contact : contactIds){
            i++;
            contactIdStr += contact.toString();

            if(i<contactIds.size()){
                contactIdStr += ",";
            }
        }

        String query ="select " + super.tableName + ".*, " +
                " user_inf.id as user_inf_id," +
                " user_inf.created_date as user_inf_c_date,user_inf.f_name,user_inf.l_name,user_inf.pic_path," +
                " location.id as location_id,location.lat,location.lng,location.formatted_address,location.country,location.created_date as location_c_date," +
                " app_login_credential.id as app_login_credential_id,app_login_credential.text_status,app_login_credential.access_token,app_login_credential.phone_number," +
                " app_login_credential.created_date as app_login_credential_c_date,job.*" +
                "  from " + super.tableName + " " +
                " join app_login_credential on  app_login_credential.id  = " + super.tableName + ".contact_id  "+
                " join user_inf on user_inf.id = app_login_credential.u_id  " +
                " left join location on location.id = user_inf.address_id " +
                " left join job on job.app_login_credential_id = app_login_credential.id " +
                " where  " + super.tableName + ".owner_id="+ this.owner_id+" ";

        if(contactIdStr!=""){
            query +=" and " + super.tableName + ".contact_id not in ("+contactIdStr+")";
        }

        if (this.keyword != null && this.keyword != "") {
            query += " and ( user_inf.f_name like '%" + this.keyword + "%' or user_inf.l_name like '%" + this.keyword + "%' )";
        }

        if (this.limit > 0) {
            this.offset = this.offset * this.limit;
            query += " LIMIT " + this.offset + " ," + this.limit + " ";
        }
        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
                Byte tempFavorites = this.resultSet.getByte(super.tableName + ".favorites");
                Byte tempIsBlock =this.resultSet.getByte(super.tableName + ".is_block");

                Contact contact = new Contact();

                contact.id = this.resultSet.getInt(super.tableName + ".id");
                contact.nickName = this.resultSet.getString(super.tableName + ".nickname");
                contact.favorites = (tempFavorites.intValue()>0);
                contact.isBlock = (tempIsBlock.intValue()>0);
                contact.rating = this.resultSet.getInt(super.tableName + ".rating");

                contact.id = this.resultSet.getInt("app_login_credential_id");
                contact.textStatus = (this.resultSet.getString("text_status") == null) ? "" : this.resultSet.getString("text_status");
                contact.phoneNumber = this.resultSet.getString("phone_number");
                contact.user.id = this.resultSet.getInt("user_inf_id");
                contact.user.firstName = this.resultSet.getString("f_name");
                contact.user.lastName = this.resultSet.getString("l_name");
                try {
                    contact.user.picPath = (this.resultSet.getObject("pic_path") == null) ? new Pictures() : this.gson.fromJson(this.resultSet.getString("pic_path"), Pictures.class);
                } catch (Exception ex) {
                    System.out.println("Parse error on picture appCid " + contact.id);
                    contact.user.picPath.original.path = (this.resultSet.getObject("pic_path") == null) ? "" : this.resultSet.getString("pic_path");
                    ex.printStackTrace();
                }
                contact.user.createdDate = this.resultSet.getString("app_login_credential_c_date");

                contact.user.address.id = (this.resultSet.getObject("location_id") == null) ? 0 : this.resultSet.getInt("location_id");
                contact.user.address.lat = (this.resultSet.getObject("lat") == null) ? 0 : this.resultSet.getDouble("lat");
                contact.user.address.lng = (this.resultSet.getObject("lng") == null) ? 0 : this.resultSet.getDouble("lng");
                contact.user.address.formattedAddress = (this.resultSet.getObject("formatted_address") == null) ? "" : this.resultSet.getString("formatted_address");
                contact.user.address.countryName = (this.resultSet.getObject("country") == null) ? "" : this.resultSet.getString("country");
                contact.user.address.createdDate = (this.resultSet.getObject("location_c_date") == null) ? "" : this.resultSet.getString("location_c_date");

                //job details
                contact.job.id =(this.resultSet.getObject("job.id")==null)?0:this.resultSet.getInt("job.id");
                contact.job.appCredentialId = (this.resultSet.getObject("job.app_login_credential_id")==null)?0:this.resultSet.getInt("job.app_login_credential_id");
                contact.job.title = (this.resultSet.getObject("job.title") == null) ? "" : this.resultSet.getString("job.title");
                contact.job.description = (this.resultSet.getObject("job.description") == null)? "" : this.resultSet.getString("job.description");
                try{
                    contact.job.icons = (this.resultSet.getObject("job.icon")==null || !this.resultSet.getString("job.icon").trim().startsWith("{"))?new Pictures():this.gson.fromJson(this.resultSet.getString("job.icon"),Pictures.class);

                }catch (Exception ex){
                    ex.printStackTrace();
                }
                contact.job.price = (this.resultSet.getObject("job.price") == null)?0:this.resultSet.getFloat("job.price");
                contact.job.paymentType = (this.resultSet.getObject("job.payment_type") == null)?0:this.resultSet.getInt("job.payment_type");
                try {
                    contact.job.createdDate = (this.resultSet.getObject("job.created_date") == null)?"":this.getPrcessedTimeStamp(this.resultSet.getTimestamp("job.created_date"));
                }catch(Exception e) {
                    System.out.println(e.getMessage());
                    contact.job.createdDate = "";
                }
                //end job details


                contacts.add(contact);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeConnection();
        }

        return contacts;
    }

    public ArrayList<Integer> getIdArrayOfWhoHasMyContactByOwnerId() {
        ArrayList<Integer> contactIds = new ArrayList<Integer>();
        String query = "select " + super.tableName + ".owner_id"+
                "  from " + super.tableName + " " +
                " where  " + super.tableName + ".contact_id="+ this.owner_id;


        System.out.println(query);
        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
                AppCredential appCredential = new AppCredential();
                contactIds.add(this.resultSet.getInt("owner_id"));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeConnection();
        }

        return contactIds;
    }
    public ArrayList<Contact> getWhomIBlockedByOwnerId() {
        ArrayList<Contact> contacts = new ArrayList<Contact>();

        String query ="select " + super.tableName + ".*, " +
                " user_inf.id as user_inf_id," +
                " user_inf.created_date as user_inf_c_date,user_inf.f_name,user_inf.l_name,user_inf.pic_path," +
                " location.id as location_id,location.lat,location.lng,location.formatted_address,location.country,location.created_date as location_c_date," +
                " app_login_credential.id as app_login_credential_id,app_login_credential.text_status,app_login_credential.access_token,app_login_credential.phone_number," +
                " app_login_credential.created_date as app_login_credential_c_date,job.*" +
                "  from " + super.tableName + " " +
                " join app_login_credential on  app_login_credential.id  = " + super.tableName + ".contact_id  "+
                " join user_inf on user_inf.id = app_login_credential.u_id  " +
                " left join location on location.id = user_inf.address_id " +
                " left join job on job.app_login_credential_id = app_login_credential.id " +
                " where  " + super.tableName + ".owner_id="+ this.owner_id+" and " + super.tableName + ".is_block = 1";


        if (this.keyword != null && this.keyword != "") {
            query += " and ( user_inf.f_name like '%" + this.keyword + "%' or user_inf.l_name like '%" + this.keyword + "%' )";
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
                Byte tempFavorites = this.resultSet.getByte(super.tableName + ".favorites");
                Byte tempIsBlock =this.resultSet.getByte(super.tableName + ".is_block");

                Contact contact = new Contact();

                contact.id = this.resultSet.getInt(super.tableName + ".id");
                contact.nickName = this.resultSet.getString(super.tableName + ".nickname");
                contact.favorites = (tempFavorites.intValue()>0);
                contact.isBlock = (tempIsBlock.intValue()>0);
                contact.rating = this.resultSet.getInt(super.tableName + ".rating");

                contact.id = this.resultSet.getInt("app_login_credential_id");
                contact.textStatus = (this.resultSet.getString("text_status") == null) ? "" : this.resultSet.getString("text_status");
                contact.phoneNumber = this.resultSet.getString("phone_number");
                contact.user.id = this.resultSet.getInt("user_inf_id");
                contact.user.firstName = this.resultSet.getString("f_name");
                contact.user.lastName = this.resultSet.getString("l_name");
                try {
                    contact.user.picPath = (this.resultSet.getObject("pic_path") == null) ? new Pictures() : this.gson.fromJson(this.resultSet.getString("pic_path"), Pictures.class);
                } catch (Exception ex) {
                    System.out.println("Parse error on picture appCid " + contact.id);
                    contact.user.picPath.original.path = (this.resultSet.getObject("pic_path") == null) ? "" : this.resultSet.getString("pic_path");
                    ex.printStackTrace();
                }
                contact.user.createdDate = this.resultSet.getString("app_login_credential_c_date");

                contact.user.address.id = (this.resultSet.getObject("location_id") == null) ? 0 : this.resultSet.getInt("location_id");
                contact.user.address.lat = (this.resultSet.getObject("lat") == null) ? 0 : this.resultSet.getDouble("lat");
                contact.user.address.lng = (this.resultSet.getObject("lng") == null) ? 0 : this.resultSet.getDouble("lng");
                contact.user.address.formattedAddress = (this.resultSet.getObject("formatted_address") == null) ? "" : this.resultSet.getString("formatted_address");
                contact.user.address.countryName = (this.resultSet.getObject("country") == null) ? "" : this.resultSet.getString("country");
                contact.user.address.createdDate = (this.resultSet.getObject("location_c_date") == null) ? "" : this.resultSet.getString("location_c_date");


                //job details
                contact.job.id =(this.resultSet.getObject("job.id")==null)?0:this.resultSet.getInt("job.id");
                contact.job.appCredentialId = (this.resultSet.getObject("job.app_login_credential_id")==null)?0:this.resultSet.getInt("job.app_login_credential_id");
                contact.job.title = (this.resultSet.getObject("job.title") == null) ? "" : this.resultSet.getString("job.title");
                contact.job.description = (this.resultSet.getObject("job.description") == null)? "" : this.resultSet.getString("job.description");
                try{
                    contact.job.icons = (this.resultSet.getObject("job.icon")==null || !this.resultSet.getString("job.icon").trim().startsWith("{"))?new Pictures():this.gson.fromJson(this.resultSet.getString("job.icon"),Pictures.class);

                }catch (Exception ex){
                    ex.printStackTrace();
                }
                contact.job.price = (this.resultSet.getObject("job.price") == null)?0:this.resultSet.getFloat("job.price");
                contact.job.paymentType = (this.resultSet.getObject("job.payment_type") == null)?0:this.resultSet.getInt("job.payment_type");
                try {
                    contact.job.createdDate = (this.resultSet.getObject("job.created_date") == null)?"":this.getPrcessedTimeStamp(this.resultSet.getTimestamp("job.created_date"));
                }catch(Exception e) {
                    System.out.println(e.getMessage());
                    contact.job.createdDate = "";
                }
                //end job details

                contacts.add(contact);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeConnection();
        }

        return contacts;
    }
    public ArrayList<Contact> getWhoBlockedMeByOwnerId() {
        ArrayList<Contact> contacts = new ArrayList<Contact>();


        String query ="select " + super.tableName + ".*, " +
                " user_inf.id as user_inf_id," +
                " user_inf.created_date as user_inf_c_date,user_inf.f_name,user_inf.l_name,user_inf.pic_path," +
                " location.id as location_id,location.lat,location.lng,location.formatted_address,location.country,location.created_date as location_c_date," +
                " app_login_credential.id as app_login_credential_id,app_login_credential.text_status,app_login_credential.access_token,app_login_credential.phone_number," +
                " app_login_credential.created_date as app_login_credential_c_date,job.*" +
                "  from " + super.tableName + " " +
                " join app_login_credential on  app_login_credential.id  = " + super.tableName + ".owner_id  "+
                " join user_inf on user_inf.id = app_login_credential.u_id  " +
                " left join location on location.id = user_inf.address_id " +
                " left join job on job.app_login_credential_id = app_login_credential.id " +
                " where  " + super.tableName + ".contact_id="+ this.owner_id+" and " + super.tableName + ".is_block = 1";

        if (this.keyword != null && this.keyword != "") {
            query += " and ( user_inf.f_name like '%" + this.keyword + "%' or user_inf.l_name like '%" + this.keyword + "%' )";
        }

        if (this.limit > 0) {
            this.offset = this.offset * this.limit;
            query += " LIMIT " + this.offset + " ," + this.limit + " ";
        }
        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
                Byte tempFavorites = this.resultSet.getByte(super.tableName + ".favorites");
                Byte tempIsBlock =this.resultSet.getByte(super.tableName + ".is_block");

                Contact contact = new Contact();

                contact.id = this.resultSet.getInt(super.tableName + ".id");
                contact.nickName = this.resultSet.getString(super.tableName + ".nickname");
                contact.favorites = (tempFavorites.intValue()>0);
                contact.isBlock = (tempIsBlock.intValue()>0);
                contact.rating = this.resultSet.getInt(super.tableName + ".rating");

                contact.id = this.resultSet.getInt("app_login_credential_id");
                contact.textStatus = (this.resultSet.getString("text_status") == null) ? "" : this.resultSet.getString("text_status");
                contact.phoneNumber = this.resultSet.getString("phone_number");
                contact.user.id = this.resultSet.getInt("user_inf_id");
                contact.user.firstName = this.resultSet.getString("f_name");
                contact.user.lastName = this.resultSet.getString("l_name");
                try {
                    contact.user.picPath = (this.resultSet.getObject("pic_path") == null) ? new Pictures() : this.gson.fromJson(this.resultSet.getString("pic_path"), Pictures.class);
                } catch (Exception ex) {
                    System.out.println("Parse error on picture appCid " + contact.id);
                    contact.user.picPath.original.path = (this.resultSet.getObject("pic_path") == null) ? "" : this.resultSet.getString("pic_path");
                    ex.printStackTrace();
                }
                contact.user.createdDate = this.resultSet.getString("app_login_credential_c_date");

                contact.user.address.id = (this.resultSet.getObject("location_id") == null) ? 0 : this.resultSet.getInt("location_id");
                contact.user.address.lat = (this.resultSet.getObject("lat") == null) ? 0 : this.resultSet.getDouble("lat");
                contact.user.address.lng = (this.resultSet.getObject("lng") == null) ? 0 : this.resultSet.getDouble("lng");
                contact.user.address.formattedAddress = (this.resultSet.getObject("formatted_address") == null) ? "" : this.resultSet.getString("formatted_address");
                contact.user.address.countryName = (this.resultSet.getObject("country") == null) ? "" : this.resultSet.getString("country");
                contact.user.address.createdDate = (this.resultSet.getObject("location_c_date") == null) ? "" : this.resultSet.getString("location_c_date");


                //job details
                contact.job.id =(this.resultSet.getObject("job.id")==null)?0:this.resultSet.getInt("job.id");
                contact.job.appCredentialId = (this.resultSet.getObject("job.app_login_credential_id")==null)?0:this.resultSet.getInt("job.app_login_credential_id");
                contact.job.title = (this.resultSet.getObject("job.title") == null) ? "" : this.resultSet.getString("job.title");
                contact.job.description = (this.resultSet.getObject("job.description") == null)? "" : this.resultSet.getString("job.description");
                try{
                    contact.job.icons = (this.resultSet.getObject("job.icon")==null || !this.resultSet.getString("job.icon").trim().startsWith("{"))?new Pictures():this.gson.fromJson(this.resultSet.getString("job.icon"),Pictures.class);

                }catch (Exception ex){
                    ex.printStackTrace();
                }
                contact.job.price = (this.resultSet.getObject("job.price") == null)?0:this.resultSet.getFloat("job.price");
                contact.job.paymentType = (this.resultSet.getObject("job.payment_type") == null)?0:this.resultSet.getInt("job.payment_type");
                try {
                    contact.job.createdDate = (this.resultSet.getObject("job.created_date") == null)?"":this.getPrcessedTimeStamp(this.resultSet.getTimestamp("job.created_date"));
                }catch(Exception e) {
                    System.out.println(e.getMessage());
                    contact.job.createdDate = "";
                }
                //end job details

                contacts.add(contact);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeConnection();
        }

        return contacts;
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
    public boolean removeContact(){
        this.startTransaction();
        AppLoginCredentialModel appLoginCredentialModel = new AppLoginCredentialModel();

        for(int contactId : this.contactIdList ){
            this.contact_id = contactId;
            appLoginCredentialModel.setId(this.contact_id);

            if(this.delete()==0){
                this.rollBack();
                this.errorObj.errStatus = false;
                this.errorObj.msg = "Internal server error: no record found to delete ";
                return false;
            }
        }
        this.commitTransaction();
        return true;
    }
    public boolean blockContact(){
        this.startTransaction();
        this.is_block = true;

        AppLoginCredentialModel appLoginCredentialModel = new AppLoginCredentialModel();

        for(int contactId : this.contactIdList ){
            this.contact_id = contactId;
            appLoginCredentialModel.setId(this.contact_id);

            if(this.updateIsBlock()==0){
                this.rollBack();
                this.errorObj.errStatus = false;
                this.errorObj.msg = "Internal server error: no record found to delete ";
                return false;
            }
        }
        this.commitTransaction();
        return true;
    }
    public boolean favoriteContact(){
        this.startTransaction();
        this.favorites = true;

        AppLoginCredentialModel appLoginCredentialModel = new AppLoginCredentialModel();

        for(int contactId : this.contactIdList ){
            this.contact_id = contactId;
            appLoginCredentialModel.setId(this.contact_id);

            if(this.updateFavorites()==0){
                this.rollBack();
                this.errorObj.errStatus = false;
                this.errorObj.msg = "Internal server error: no record found to delete ";
                return false;
            }
        }
        this.commitTransaction();
        return true;
    }
    public boolean unBlockContact(){
        this.startTransaction();
        this.is_block = false;

        AppLoginCredentialModel appLoginCredentialModel = new AppLoginCredentialModel();

        for(int contactId : this.contactIdList ){
            this.contact_id = contactId;
            appLoginCredentialModel.setId(this.contact_id);

            if(this.updateIsBlock()==0){
                this.rollBack();
                this.errorObj.errStatus = false;
                this.errorObj.msg = "Internal server error: no record found to delete ";
                return false;
            }
        }
        this.commitTransaction();
        return true;
    }
    public boolean unFavoriteContact(){
        this.startTransaction();
        this.favorites = false;

        AppLoginCredentialModel appLoginCredentialModel = new AppLoginCredentialModel();

        for(int contactId : this.contactIdList ){
            this.contact_id = contactId;
            appLoginCredentialModel.setId(this.contact_id);

            if(this.updateFavorites()==0){
                this.rollBack();
                this.errorObj.errStatus = false;
                this.errorObj.msg = "Internal server error: no record found to delete ";
                return false;
            }
        }
        this.commitTransaction();
        return true;
    }
}
