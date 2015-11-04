package model;

import model.datamodel.app.Liker;
import model.datamodel.app.Stickers;
import model.datamodel.photo.Pictures;
import org.apache.commons.lang3.StringEscapeUtils;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by mi on 10/9/15.
 */
public class StickersModel extends ImageTalkBaseModel {

    private int    id;
    private int    sticker_category_id;
    private String path;
    private int    is_paid;
    private int    created_by;
    private String created_date;

    public StickersModel() {
        super();
        super.tableName = "stickers";


        this.id = 0;
        this.sticker_category_id = 0;
        this.path = "";
        this.is_paid = -1;
        this.created_by = 0;
        this.created_date = "";
    }

    public int getId() {
        return id;
    }

    public boolean setId(int id) {
        this.id = id;
        return true;
    }

    public int getSticker_category_id() {
        return sticker_category_id;
    }

    public boolean setSticker_category_id(int sticker_category_id) {
        this.sticker_category_id = sticker_category_id;
        return true;
    }

    public String getPath() {
        return path;
    }

    public boolean setPath(String path) {
        this.path = StringEscapeUtils.escapeEcmaScript(path);
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
    public ArrayList<Stickers> getAll(){

        ArrayList<Stickers> stickerList = new ArrayList<Stickers>();
        String query =  " select *" +
                " from " + super.tableName+
                " join sticker_category on sticker_category.id ="+ super.tableName+".sticker_category_id ";
        if(this.is_paid!=-1){
            query +="where  "+ super.tableName+".is_paid = "+this.is_paid;
        }
        if(this.limit >0){
            this.offset = this.offset * this.limit;
            query += " LIMIT "+this.offset+" ,"+this.limit+" ";
        }

        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
                Stickers stickers = new Stickers();
                stickers.id = this.resultSet.getInt("id");
                stickers.stickerCategoryId = this.resultSet.getInt("sticker_category_id");
                stickers.categoryName = this.resultSet.getString("sticker_category.name");
                stickers.path = this.resultSet.getString("path");


                stickerList.add(stickers);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return stickerList;
    }
    public ArrayList<Stickers> getAllByCategoryId(){

        ArrayList<Stickers> stickerList = new ArrayList<Stickers>();
        String query =  " select *" +
                " from " + super.tableName+
                " join sticker_category on sticker_category.id ="+ super.tableName+".sticker_category_id "+
                " where sticker_category_id = "+this.sticker_category_id;
        if(this.is_paid!=-1){
            query +=" and "+ super.tableName+".is_paid = "+this.is_paid;
        }
        if(this.limit >0){
            this.offset = this.offset * this.limit;
            query += " LIMIT "+this.offset+" ,"+this.limit+" ";
        }

        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
                Stickers stickers = new Stickers();
                stickers.id = this.resultSet.getInt("id");
                stickers.stickerCategoryId = this.resultSet.getInt("sticker_category_id");
                stickers.categoryName = this.resultSet.getString("sticker_category.name");
                stickers.path = this.resultSet.getString("path");


                stickerList.add(stickers);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return stickerList;
    }
    public ArrayList<Stickers> getAllByCategoryId(int category_id){

        ArrayList<Stickers> stickerList = new ArrayList<Stickers>();
        String query =  " select *" +
                " from " + super.tableName+
                " join sticker_category on sticker_category.id ="+ super.tableName+".sticker_category_id "+
                " where sticker_category_id = "+category_id;

        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
                Stickers stickers = new Stickers();
                stickers.id = this.resultSet.getInt("id");
                stickers.stickerCategoryId = this.resultSet.getInt("sticker_category_id");
                stickers.categoryName = this.resultSet.getString("sticker_category.name");
                stickers.path = this.resultSet.getString("path");

                stickerList.add(stickers);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return stickerList;
    }
    public int insert() {
        String query = "INSERT INTO `stickers`( `sticker_category_id`, `path`,`is_paid`, `created_by`) " +
                       " VALUES (" + this.sticker_category_id + ",'" + this.path + "'," + this.is_paid + "," + this.created_by + ")";
        this.insertData(query);
        return this.id;
    }

    public boolean deleteStickers(int id) {
        String sql = "DELETE FROM " + this.tableName + " WHERE sticker_category_id = '" + id + "'";
        System.out.print(sql);
        if (this.deleteData(sql) == 1) {
            return true;
        }

        return false;
    }

}
