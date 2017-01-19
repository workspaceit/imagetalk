package model.datamodel.app;

import model.datamodel.photo.Pictures;

/**
 * Created by mi on 8/20/15.
 */
public class User {
    public int    id;
    public String firstName;
    public String lastName;
    public String deviceId;
    public Pictures picPath;
    public Location address;
    public String createdDate;

    public User() {
        this.id = 0;
        this.firstName = null;
        this.lastName = null;
        this.deviceId = "";
        this.address = new Location();
        this.picPath = new Pictures();
        this.createdDate = "";
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", picPath=" + picPath +
                ", address=" + address +
                ", createdDate='" + createdDate + '\'' +
                '}';
    }
}
