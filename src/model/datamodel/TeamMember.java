package model.datamodel;

/**
 * Created by mi on 8/24/15.
 */
public class TeamMember extends User {
    public boolean is_lead;
    public String join_date;
    public TeamMember(){
        this.is_lead = false;
        this.join_date = null;
    }

}
