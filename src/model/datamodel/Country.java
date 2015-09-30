package model.datamodel;

/**
 * Created by mi on 9/20/15.
 */
public class Country {


    public int id;
    public String name;
    public String niceName;
    public String iso3;
    public String numcode;
    public String phoneCode;
    public boolean status;
    public Country(){
        this.id = 0;
        this.name = "";
        this.niceName = "";
        this.iso3 = "";
        this.numcode = "";
        this.phoneCode = "";
        this.status = true;
    }
}
