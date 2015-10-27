package model;

import model.datamodel.app.User;

import java.sql.SQLException;

/**
 * Created by mi on 10/27/15.
 */
public class WallPostFavoriteModel extends  ImageTalkBaseModel{

    private  int id;
    private  int owner_id;
    private  int wall_post_id;
    private  int created_date;

    public boolean isFavorite;

    public WallPostFavoriteModel() {
        super.tableName = "wall_post_favorite";

        this.id = 0;
        this.owner_id =0;
        this.wall_post_id = 0;
        this.created_date = 0;

        this.isFavorite = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(int owner_id) {
        this.owner_id = owner_id;
    }

    public int getWall_post_id() {
        return wall_post_id;
    }

    public void setWall_post_id(int wall_post_id) {
        this.wall_post_id = wall_post_id;
    }

    public int getCreated_date() {
        return created_date;
    }

    public void setCreated_date(int created_date) {
        this.created_date = created_date;
    }

    public int insert(){
        String query = "INSERT INTO " + super.tableName+" (`owner_id`, `wall_post_id`, `created_date`) " +
                        " VALUES ("+this.owner_id+","+this.wall_post_id+",'"+this.getUtcDateTime()+"')";
        return insertData(query);
    }
    public int delete(){
        String query = "DELETE FROM " + super.tableName+
                " where owner_id = "+this.owner_id+" and wall_post_id = "+this.wall_post_id+"  limit 1";
        return deleteData(query);
    }

    public boolean changeFavoriteState(){

        WallPostModel wallPostModel = new WallPostModel();

        wallPostModel.setId(this.wall_post_id);

        if(!wallPostModel.isIdExist()){
            this.errorObj.msg = "Wall post id : "+this.wall_post_id+" does not exist";
            this.errorObj.errStatus = true;
            return false;
        }
        if(this.isFavoriteWallPost()) {
            this.delete();
            this.operationStatus.msg = "Wall post removed from favorite list";

            this.isFavorite = false;
        }else{
            if(this.insert()<=0){
                this.errorObj.errStatus = true;
                this.errorObj.msg = "Internal Server error";
                return false;
            }else{
                this.operationStatus.msg = "Wall post added to favorite list";
                this.isFavorite = true;
            }
        }
        return true;
    }

    public boolean isFavoriteWallPost(){
        String query = "SELECT id FROM  " + super.tableName+
                       " where owner_id = "+this.owner_id+" and wall_post_id = "+this.wall_post_id+"  limit 1";

        this.setQuery(query);
        this.getData();

        try {
            while (this.resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return false;
    }
}
