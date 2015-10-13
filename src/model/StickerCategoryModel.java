package model;

import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Created by mi on 10/9/15.
 */
public class StickerCategoryModel extends ImageTalkBaseModel {

    private int    id;
    private String name;
    private int    is_paid;
    private int    created_by;
    private String created_date;

    public int getId() {
        return id;
    }

    public boolean setId(int id) {
        this.id = id;
        return true;
    }

    public String getName() {
        return name;
    }

    public boolean setName(String name) {
        this.name = name;
        return true;
    }

    public int getIs_paid() {
        return is_paid;
    }

    public boolean setIs_paid(int is_paid) {
        this.is_paid = (is_paid < 0 || is_paid > 1) ? 0 : is_paid;
        return true;
    }

    public int getCreated_by() {
        return created_by;
    }

    public boolean setCreated_by(int created_by) {
        this.created_by = created_by;
        return true;
    }

    public String getCreated_date() {
        return created_date;
    }

    public boolean setCreated_date(String created_date) {
        this.created_date = created_date;
        return true;
    }

    public int insert() {
        String query = "INSERT INTO `sticker_category`( `name`,`is_paid`, `created_by`) " +
                       " VALUES ('" + this.name + "'," + this.is_paid + "," + this.created_by + ")";
        this.id = this.insertData(query);
        return this.id;
    }

    public ArrayList<StickerCategoryModel> getStickerCategoryList() {
        ArrayList<StickerCategoryModel> categoryList = new ArrayList<>();
        String                          sql          = "SELECT * FROM sticker_category";

        this.setQuery(sql);
        this.getData();

        try {
            while (this.resultSet.next()) {
                StickerCategoryModel stickerCategoryModel = new StickerCategoryModel();
                stickerCategoryModel.setId(resultSet.getInt("id"));
                stickerCategoryModel.setName(resultSet.getString("name"));
                stickerCategoryModel.setIs_paid(resultSet.getInt("is_paid"));
                stickerCategoryModel.setCreated_by(resultSet.getInt("created_by"));
                stickerCategoryModel.setCreated_date(resultSet.getString("created_date"));

                categoryList.add(stickerCategoryModel);
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }

        return categoryList;
    }

}
