package model.datamodel.app;

import model.datamodel.photo.Pictures;

/**
 * Created by mi on 10/29/15.
 */
public class Job {
    public int id;
    public int appCredentialId;
    public String title;
    public String description;
    public Pictures icons;
    public float price;
    public int paymentType;

    public String createdDate;

    public Job() {
        this.id = 0;
        this.appCredentialId = 0;
        this.title = "";
        this.description = "";
        this.icons = new Pictures();
        this.price = 0;
        this.paymentType = 0;
        this.createdDate = "";
    }
}
