package model;

/**
 * Created by mi on 10/12/15.
 */
public class ContactModel extends ImageTalkBaseModel {

    private int id;
    private String nickname;
    private int owner_id;
    private int contact_id;
    private boolean favorites;
    private boolean is_block;
    private String created_date;
    private int  rating;

    public ContactModel() {
        this.id = 0;
        this.nickname = "";
        this.owner_id = 0;
        this.contact_id = 0;
        this.favorites = false;
        this.is_block = false;
        this.created_date = "";
        this.rating = 0;
    }

    public int getId() {
        return id;
    }

    public boolean setId(int id) {
        this.id = id;
        return true;
    }

    public String getNickname() {
        return nickname;
    }

    public boolean setNickname(String nickname) {
        this.nickname = nickname;
        return true;
    }

    public int getOwner_id() {
        return owner_id;
    }

    public boolean setOwner_id(int owner_id) {
        this.owner_id = owner_id;
        return true;
    }

    public int getContact_id() {
        return contact_id;
    }

    public boolean setContact_id(int contact_id) {
        this.contact_id = contact_id;
        return true;
    }

    public boolean isFavorites() {
        return favorites;
    }

    public void setFavorites(boolean favorites) {
        this.favorites = favorites;
    }

    public boolean is_block() {
        return is_block;
    }

    public boolean setIs_block(boolean is_block) {
        this.is_block = is_block;
        return true;
    }

    public String getCreated_date() {
        return created_date;
    }

    public boolean setCreated_date(String created_date) {
        this.created_date = created_date;
        return true;
    }

    public int getRating() {
        return rating;
    }

    public boolean setRating(int rating) {
        this.rating = rating;
        return true;
    }
    public int insert(){
        String query = "INSERT INTO `contact`(`nickname`, `owner_id`, `contact_id`, `favorites`, `is_block`, `rating`) " +
                "VALUES ('"+this.nickname+"',"+this.owner_id+","+this.contact_id+","+this.favorites+","+this.is_block+","+this.rating+")";
        this.id = this.insertData(query);
        return this.id;
    }
}
