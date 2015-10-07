package model;

/**
 * Created by mi on 10/7/15.
 */
public class LocationModel extends ImageTalkBaseModel {
    private int id;
    private double lat;
    private double lng;
    private String  formatted_address;
    private String  country;
    private String created_date;

    public LocationModel() {
        super();
        super.tableName = "location";

        this.id = 0;
        this.lat = 0;
        this.lng = 0;
        this.formatted_address = "";
        this.country ="";
        this.created_date = "";
    }

    public double getLat() {
        return lat;
    }

    public boolean setLat(double lat) {
        this.lat = lat;
        return true;
    }

    public double getLng() {
        return lng;
    }

    public boolean setLng(double lng) {
        this.lng = lng;
        return true;
    }

    public String getFormatted_address() {
        return formatted_address;
    }

    public boolean setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
        return true;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCreated_date() {
        return created_date;
    }

    public boolean setCreated_date(String created_date) {
        this.created_date = created_date;
        return true;
    }

    public int getId() {

        return id;
    }

    public boolean setId(int id) {
        this.id = id;
        return true;
    }

    public int insert(){
        String query = "INSERT INTO "+this.tableName+" (lat, lng, formatted_address, country)";
        query += "VALUES ("+this.lat+","+this.lng+",'"+this.formatted_address+"','"+this.country+"')";
        this.id = this.insertData(query);
        return  this.id;
    }
}
