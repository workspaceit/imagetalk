package model;

/**
 * Created by IntelliJ IDEA.
 * User: rajib
 * Email: rajibcse2k10@gmail.com
 * Date: 11/9/15
 * Project Name:ImageTalk
 */
public class StickerUsageEligibleModel extends ImageTalkBaseModel {
    private int id;
    private int sticker_category_id;
    private int app_login_credential_id;
    private int usage;
    private String created_date;

    public StickerUsageEligibleModel() {
        this.tableName = "user_sticker_eligible";

        this.id = 0;
        this.sticker_category_id = 0;
        this.app_login_credential_id = 0;
        this.usage = 0;
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

    public int getApp_login_credential_id() {
        return app_login_credential_id;
    }

    public boolean setApp_login_credential_id(int app_login_credential_id) {
        this.app_login_credential_id = app_login_credential_id;
        return true;
    }

    public int getUsage() {
        return usage;
    }

    public boolean setUsage(int usage) {
        this.usage = usage;
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
        String query = "INSERT INTO `user_sticker_eligible`( `sticker_category_id`, `app_login_credential_id`,`usage`, `created_date`) " +
                " VALUES (" + this.sticker_category_id + "," + this.app_login_credential_id + "," + this.usage + ",'" + this.getUtcDateTime() + "')";

        System.out.println(query);
        this.id = this.insertData(query);
        System.out.println(this.id);
        return this.id;
    }
    public int isExist()
    {
        String query1 = "SELECT * FROM "+this.tableName+" WHERE "+this.tableName+".sticker_category_id="+this.sticker_category_id+
                " AND "+this.tableName+".app_login_credential_id="+this.app_login_credential_id;

        System.out.println(query1);
        this.setQuery(query1);
        this.getData();
        try {
            while (this.resultSet.next())
            {
                this.id = this.resultSet.getInt("id");
                int us;
                us = this.resultSet.getInt("usage");
                if (us>0)
                {
                    return 1;
                }
                return id;

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            this.closeConnection();
        }
        return 0;
    }

    public boolean updateStickerUsage(){

        String query = "UPDATE " + this.tableName + " SET `usage`=" + this.usage + " WHERE `id`="+this.id;

        System.out.println(query);
        return this.updateData(query);
    }
}
