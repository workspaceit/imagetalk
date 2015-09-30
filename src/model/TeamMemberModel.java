package model;

import model.datamodel.TeamMember;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by mi on 8/24/15.
 */
public class TeamMemberModel extends ImageTalkBaseModel {

    public int team_id;
    public int u_id;
    public boolean is_lead;
    public String created_date;

    public TeamMemberModel(){
        this.tableName = "team_member";

        this.team_id = 0;
        this.u_id = 0;
        this.is_lead =false;
        this.created_date = null;
    }

    public int insert(){
        int tempIsLead = (this.is_lead)?1:0;
        this.query = "INSERT INTO "+this.tableName+" ( `team_id`, `u_id`, `is_lead`) VALUES ("+this.team_id+","+this.u_id+","+tempIsLead+")";
        return this.insertData(this.query);
    }

    public int addTeamMember(){
        TeamModel teamModel =new TeamModel();
        teamModel.id = this.team_id;
        if(!teamModel.isTeamMemberOfTeam(this.u_id)){
           return  this.insert();
        }else{
            this.errorMsg = "User already a team member of this team";
        }
        return 0;
    }
    public ArrayList<TeamMember> getAllByTeamId(){
        this.query = "select team_member.id,  \n" +
                "team_member.team_id,\n" +
                "team_member.u_id,  \n" +
                "team_member.is_lead,  \n" +
                "team_member.created_date as team_member_created_date,\n" +
                "user_inf.f_name, \n" +
                "user_inf.l_name, \n" +
                "user_inf.address, \n" +
                "user_inf.created_date as user_inf_created_date \n" +
                " FROM "+this.tableName+" \n" +
                " JOIN user_inf ON team_member.u_id = user_inf.id where team_member.team_id = "+this.team_id;


        ArrayList<TeamMember> teamMembersList = new ArrayList<TeamMember>();


        this.getData(this.query);
        try {
            while (this.resultSet.next()) {
                TeamMember teamMember = new TeamMember();
                teamMember.id = this.resultSet.getInt("u_id");
                teamMember.f_name = this.resultSet.getString("f_name");
                teamMember.l_name = this.resultSet.getString("l_name");
                teamMember.address = this.resultSet.getString("address");
                Byte isLeadTmp = this.resultSet.getByte("is_lead");
                teamMember.is_lead =(isLeadTmp.intValue()==1)?true:false;
                teamMember.created_date = this.resultSet.getString("user_inf_created_date");
                teamMember.join_date = this.resultSet.getString("team_member_created_date");
                teamMembersList.add(teamMember);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }

        return teamMembersList;
    }

    public ArrayList<TeamMember> getAllTeamMemberByTeamIdAndKeyWord(String keyWord){

        if(keyWord==null || keyWord==""){
            return getAllByTeamId();
        }
        this.query = "select team_member.id,  \n" +
                "team_member.team_id,\n" +
                "team_member.u_id,  \n" +
                "team_member.is_lead,  \n" +
                "team_member.created_date as team_member_created_date,\n" +
                "user_inf.f_name, \n" +
                "user_inf.l_name, \n" +
                "user_inf.address, \n" +
                "user_inf.created_date as user_inf_created_date \n" +
                " FROM "+this.tableName+" \n" +
                " JOIN user_inf ON team_member.u_id = user_inf.id where team_member.team_id = "+this.team_id+" and ( f_name like '%"+keyWord+"%' or l_name like '%"+keyWord+"%' ) order by f_name asc ";

        System.out.println(this.query );
        ArrayList<TeamMember> teamMembersList = new ArrayList<TeamMember>();


        this.resultSet = this.getData(this.query);
        try {
            while (this.resultSet.next()) {
                TeamMember teamMember = new TeamMember();
                teamMember.id = this.resultSet.getInt("u_id");
                teamMember.f_name = this.resultSet.getString("f_name");
                teamMember.l_name = this.resultSet.getString("l_name");
                teamMember.address = this.resultSet.getString("address");
                Byte isLeadTmp = this.resultSet.getByte("is_lead");
                teamMember.is_lead =(isLeadTmp.intValue()==1)?true:false;
                teamMember.join_date = this.resultSet.getString("team_member_created_date");
                teamMembersList.add(teamMember);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return teamMembersList;
    }

    public boolean deleteByTeamId(){
        this.query = "DELETE FROM "+this.tableName+" WHERE team_id = "+this.team_id;
        return this.updateData(this.query);
    }
    public boolean deleteByTeamIdAndUid(){
        this.query = "DELETE FROM "+this.tableName+" WHERE team_id = "+this.team_id+" and u_id="+this.u_id;
        return this.updateData(this.query);
    }
    public boolean leaveTeam(){
        JoinTeamReqModel joinTeamReqModel = new JoinTeamReqModel();
        joinTeamReqModel.u_id = this.u_id;
        joinTeamReqModel.team_id = this.team_id;

        joinTeamReqModel.deleteByTeamIdAndUid();
        return  this.deleteByTeamIdAndUid();
    }
    public boolean updateTeamLead(){
        this.query = "UPDATE "+this.tableName+" SET u_id = "+this.u_id+"    WHERE team_id ="+this.team_id+"  and is_lead = 1 ";
        return this.updateData(this.query);

    }
    public int deleteByUid(){
        String sql = "DELETE FROM " + this.tableName + " WHERE id = '"+ this.u_id +"'";
        return  this.deleteData(sql);
    }
}
