package model.datamodel;

import java.util.ArrayList;

/**
 * Created by mi on 8/25/15.
 */
public class TeamDetails extends Team {
    public User createdBy;
    public ArrayList<TeamMember> members;

    public TeamDetails(){
        this.createdBy = new User();
        this.members = new ArrayList<TeamMember>();
    }
}
