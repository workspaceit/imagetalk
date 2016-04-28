package model;

/**
 * Application Name : ImageTalk
 * Package Name     : model.stable
 * Author           : Abu Bakar Siddique
 * Email            : absiddique.live@gmail.com
 * Created Date     : 4/26/16
 */
public class WallPostStatusModel extends  ImageTalkBaseModel{

    private  int id;
    private  int owner_id;
    private  int wall_post_id;

    public WallPostStatusModel() {
        super.tableName = "wall_post_status";

        this.id = 0;
        this.owner_id =0;
        this.wall_post_id = 0;
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

    public int hide(){
        String query = "INSERT INTO " + super.tableName+" (`owner_id`, `wall_post_id`) " +
                       " VALUES ("+this.getCurrentUserId()+","+this.wall_post_id+")";
        return insertData(query);
    }


}
