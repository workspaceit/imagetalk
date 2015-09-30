package model.datamodel;

/**
 * Created by mi on 8/26/15.
 */
public class JoinTeamUser {
    public Team team;
    public User user;
    public JoinTeamUser(){
        this.team =  new Team();
        this.user = new User();
    }
}
