package model.datamodel;

/**
 * Created by mi on 9/20/15.
 */
public class Country {
    public int id;
    public String name;
    public String isoCode2;
    public String isoCode3;
    public Boolean status;
    public Country(){
        this.id = 0;
        this.name = "";
        this.isoCode2 = "";
        this.isoCode3 = "";
        this.status = false;
    }
}
