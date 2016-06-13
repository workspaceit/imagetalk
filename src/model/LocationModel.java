package model;

import model.datamodel.app.Location;
import model.datamodel.app.WallPost;
import model.datamodel.photo.Pictures;
import org.apache.commons.lang3.StringEscapeUtils;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by mi on 10/7/15.
 */
public class LocationModel extends ImageTalkBaseModel {
    private int id;
    private String place_id;
    private String icon;
    private String name;
    private String google_place_id;
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

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = StringEscapeUtils.escapeEcmaScript(name);
    }

    public String getGoogle_place_id() {
        return google_place_id;
    }

    public void setGoogle_place_id(String google_place_id) {
        this.google_place_id = StringEscapeUtils.escapeEcmaScript(google_place_id);
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
        String query = "INSERT INTO "+this.tableName+" (place_id,icon,name,google_place_id,lat, lng, formatted_address, country)";
        query += "VALUES ('"+this.place_id+"','"+this.icon+"','"+this.name+"','"+this.place_id+"',"+this.lat+","+this.lng+",'"+this.formatted_address+"','"+this.country+"')";
        System.out.println(query);
        this.id = this.insertData(query);
        return  this.id;

//        String query = "INSERT INTO "+this.tableName+" (lat, lng, formatted_address, country)";
//        query += "VALUES ("+this.lat+","+this.lng+",'"+this.formatted_address+"','"+this.country+"')";
//        this.id = this.insertData(query);
//        return  this.id;
    }
    public int insertPlaces(){
        String query = "INSERT INTO "+this.tableName+" (place_id,icon,name,google_place_id,lat, lng, formatted_address, country)";
        query += "VALUES ('"+this.place_id+"','"+this.icon+"','"+this.name+"','"+this.place_id+"',,"+this.lat+","+this.lng+",'"+this.formatted_address+"','"+this.country+"')";
        this.id = this.insertData(query);
        return  this.id;
    }

    public ArrayList<Integer> getLocationIdOfNearByWallpost(){

        ArrayList<Integer> locationIdList = new ArrayList<Integer>();


        String query = "SELECT id, SQRT(POW(69.1 * (lat - "+ this.lat+"), 2) +" +
                       "POW(69.1 * ("+this.lng+" - lng) * COS(lat / 57.3), 2)) AS distance " +
                       "FROM location HAVING distance < 0.3 ORDER BY distance DESC";

        this.setQuery(query);
        this.getData();

        try {
            while (this.resultSet.next()) {
                Location location = new Location();
                location.id = this.resultSet.getInt("location.id");

                locationIdList.add(location.id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return locationIdList;



    }
}
