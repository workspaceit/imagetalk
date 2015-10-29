package model;

/**
 * Created by rajib on 10/29/15.
 */
public class JobModel extends ImageTalkBaseModel {
    private int id;
    private String description;
    private String icon;
    private int price;
    private int payment_type;
    private int app_login_credential_id;
    private String created_date;

    public JobModel() {
        this.tableName = "job";

        this.id = 0;
        this.description = "";
        this.icon="";
        this.price = 0;
        this.payment_type = 0;
        this.app_login_credential_id = 0;
        this.created_date = "";
    }

    public int getId() {
        return id;
    }

    public boolean setId(int id) {
        this.id = id;
        return true;
    }

    public String getDescription() {
        return description;
    }

    public boolean setDescription(String description) {
        this.description = description;
        return true;
    }

    public String getIcon() {
        return icon;
    }

    public boolean setIcon(String icon) {
        this.icon = icon;
        return true;
    }

    public int getPrice() {
        return price;
    }

    public boolean setPrice(int price) {
        this.price = price;
        return  true;
    }

    public int getPayment_type() {
        return payment_type;
    }

    public boolean setPayment_type(int payment_type) {
        this.payment_type = payment_type;
        return true;
    }

    public int getApp_login_credential_id() {
        return app_login_credential_id;
    }

    public boolean setApp_login_credential_id(int app_login_credential_id) {
        this.app_login_credential_id = app_login_credential_id;
        return true;
    }

    public String getCreated_date() {
        return created_date;
    }

    public boolean setCreated_date(String created_date) {
        this.created_date = created_date;
        return true;
    }

    public int insert(){
        String query = "INSERT INTO " + this.tableName + " (`description`, `icon`, `price`, `app_login_credential_id`) " +
                "VALUES ('"+this.description+"','"+this.icon+"',"+this.price+","+this.app_login_credential_id+")";
        this.id = this.insertData(query);
        return this.id;
    }


}
