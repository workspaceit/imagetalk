package model.datamodel;

/**
 * Created by mi on 9/20/15.
 */
public class Country {


    public int id;
    public String iso;
    public String name;
    public String niceName;
    public String iso3;
    public int numcode;
    public int phoneCode;
    public boolean status;
    public Country(){
        this.id = 0;
        this.iso = "";
        this.name = "";
        this.niceName = "";
        this.iso3 = "";
        this.numcode = 0;
        this.phoneCode = 0;
        this.status = true;
    }
}
