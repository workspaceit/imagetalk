package model.datamodel.app;

/**
 * Created by mi on 10/19/15.
 */
public class Places extends Location {
    public String placeId;
    public String icon;
    public String name;
    public String googlePlaceId;
    float rating;

    public Places() {
        this.placeId = "";
        this.icon = "";
        this.name = "";
        this.rating = 0;
        this.googlePlaceId = "";
    }
}
