/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.datamodel.app;

/**
 *
 * @author mi
 */
public class Location {

    public int id;
    public double lat;
    public double lon;
    public String formattedAddress;
    public String countryName;
    public String createdDate;
    public Location(){
        this.id = 0;
        this.lat = 0;
        this.lon = 0;
        this.formattedAddress = "";
        this.countryName = "";
        this.createdDate = "";
    }
}