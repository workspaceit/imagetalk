package model;

import model.datamodel.Team;
import model.datamodel.TeamDetails;
import model.datamodel.TeamMember;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by mi on 8/24/15.
 */
public class TeamModel extends ImageTalkBaseModel {

    public int id;
    public String name;
    public int created_by;
    public String created_date;

    public TeamModel(){
        this.tableName = "team";
        this.query = null;

    }
    public int insert(){
        this.query = "INSERT INTO "+this.tableName+" (`name`, `created_by`) VALUES ('"+this.name+"',"+this.created_by+")";
        return this.insertData(this.query);
    }
    public int getCount() {
        String sql = "select count(id) as count from "+this.tableName+" ";
        int count = 0;
        this.resultSet = this.getData(sql);
        try {
            while (this.resultSet.next()) {
                count = this.resultSet.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return count;
    }
    public boolean updateName(){
        this.query = "UPDATE "+this.tableName+" SET name ='"+this.name+"' WHERE id ="+this.id;
        return this.updateData(this.query);

    }
    public TeamDetails getAllById() {
        this.query = "SELECT \n" +
                " team.`id` as teamId,\n" +
                " team.`name`, \n" +
                " team.`created_by`, \n" +
                " team.`created_date` as `team_created_date`,\n" +
                " user_inf.`id`as u_id, \n" +
                " user_inf.`f_name`, \n" +
                " user_inf.`l_name`, \n" +
                " user_inf.`address`, \n" +
                " user_inf.`created_date` as `user_inf_created_date`\n" +
                " FROM  "+this.tableName+" \n" +
                " join user_inf on team.`created_by`= user_inf.id\n"+
                " where team.id = "+this.id;

        TeamDetails teamDetails = new TeamDetails();
        this.resultSet = this.getData(this.query);
        try {
            while (this.resultSet.next()) {
                teamDetails.id = this.resultSet.getInt("teamId");
                teamDetails.name = this.resultSet.getString("name");
                teamDetails.createdBy.id = this.resultSet.getInt("u_id");
                teamDetails.createdBy.f_name = this.resultSet.getString("f_name");
                teamDetails.createdBy.l_name = this.resultSet.getString("l_name");
                teamDetails.createdBy.address = this.resultSet.getString("address");
                teamDetails.createdBy.created_date = this.resultSet.getString("user_inf_created_date");
                teamDetails.created_date = this.resultSet.getString("team_created_date");
            }
            TeamMemberModel teamMemberModel = new TeamMemberModel();
            teamMemberModel.team_id = this.id;
            teamDetails.members = teamMemberModel.getAllByTeamId();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }

        return teamDetails;
    }
    public ArrayList<TeamDetails> getAllTeamDetails() {
        this.query = "SELECT " +
                " team.`id` as teamId," +
                " team.`name`, " +
                " team.`created_by`, " +
                " team.`created_date` as `team_created_date`," +
                " user_inf.`id`as u_id, " +
                " user_inf.`f_name`, " +
                " user_inf.`l_name`, " +
                " user_inf.`address`, " +
                " user_inf.`created_date` as `user_inf_created_date` " +
                " FROM  "+this.tableName+" " +
                " join user_inf on team.`created_by`= user_inf.id ";

        ArrayList<TeamDetails> teamDetailsList= new ArrayList<TeamDetails>();

        this.resultSet = this.getData(this.query);
        try {
            while (this.resultSet.next()) {
                TeamDetails teamDetails = new TeamDetails();
                teamDetails.id = this.resultSet.getInt("teamId");
                teamDetails.name = this.resultSet.getString("name");
                teamDetails.createdBy.id = this.resultSet.getInt("u_id");
                teamDetails.createdBy.f_name = this.resultSet.getString("f_name");
                teamDetails.createdBy.l_name = this.resultSet.getString("l_name");
                teamDetails.createdBy.address = this.resultSet.getString("address");
                teamDetails.createdBy.created_date = this.resultSet.getString("user_inf_created_date");
                teamDetails.created_date = this.resultSet.getString("team_created_date");

                TeamMemberModel teamMemberModel = new TeamMemberModel();
                teamMemberModel.team_id = teamDetails.id;

                teamDetails.members = teamMemberModel.getAllByTeamId();

                teamDetailsList.add(teamDetails);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }

        return teamDetailsList;
    }
    public ArrayList<Team> getAllByCreatedBy(){
        ArrayList<Team> teamList = new ArrayList<Team>();
        this.query = "select * from "+this.tableName+" where created_by = "+this.created_by;
        this.getData(this.query);
        try {
            while(this.resultSet.next()){
                Team team = new Team();
                team.id = this.resultSet.getInt("id");
                team.name = this.resultSet.getString("name");
                team.created_date = this.resultSet.getString("created_date");

                teamList.add(team);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return teamList;
    }
    public ArrayList<Team> getUnionOfIsTeamLeadAndCreatedBy(int u_Id){
        ArrayList<Team> teamList = new ArrayList<Team>();

        this.query = "Select team.id, team.name, team.created_by, team.created_date\n" +
                "from "+this.tableName+"  \n" +
                "join team_member on team.id = team_member.team_id \n" +
                "where team_member.u_id = "+u_Id+" and team_member.is_lead = 1";
        this.query +=" union ";
        this.query += " select id,name,created_by,created_date from "+this.tableName+" where created_by = "+u_Id;


        this.getData(this.query);
        try {
            while(this.resultSet.next()){
                Team team = new Team();
                team.id = this.resultSet.getInt("id");
                team.name = this.resultSet.getString("name");
                team.created_date = this.resultSet.getString("created_date");

                teamList.add(team);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return teamList;
    }
    public ArrayList<Team> getIsTeamLeadTeams(int u_Id){
        ArrayList<Team> teamList = new ArrayList<Team>();

        this.query = "Select team.id, team.name, team.created_by, team.created_date\n" +
                "from "+this.tableName+"  \n" +
                "join team_member on team.id = team_member.team_id \n" +
                "where team_member.u_id = "+u_Id+" and team_member.is_lead = 1 ";


        this.getData(this.query);
        try {
            while(this.resultSet.next()){
                Team team = new Team();
                team.id = this.resultSet.getInt("id");
                team.name = this.resultSet.getString("name");
                team.created_date = this.resultSet.getString("created_date");

                teamList.add(team);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return teamList;
    }
    public ArrayList<Team> getUnionOfIsTeamLeadAndCreatedByAndKeyword(int u_Id,String keyword){
        ArrayList<Team> teamList = new ArrayList<Team>();

        this.query = "Select team.id, team.name, team.created_by, team.created_date\n" +
                "from "+this.tableName+"  \n" +
                "join team_member on team.id = team_member.team_id \n" +
                "where team_member.u_id = "+u_Id+" and team_member.is_lead = 1 ";
        this.query += " and team.name like '%"+keyword+"%'";

        this.query +=" union ";
        this.query += " select id,name,created_by,created_date from "+this.tableName+" where created_by = "+u_Id;
        this.query += " and team.name like '%"+keyword+"%'";

        this.getData(this.query);
        try {
            while(this.resultSet.next()){
                Team team = new Team();
                team.id = this.resultSet.getInt("id");
                team.name = this.resultSet.getString("name");
                team.created_date = this.resultSet.getString("created_date");

                teamList.add(team);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return teamList;
    }
    public ArrayList<Team> getAllByTeamMemberId(int u_Id){
        ArrayList<Team> teamList = new ArrayList<Team>();
        this.query = "Select team.id, team.name, team.created_by, team.created_date\n" +
                "from "+this.tableName+"  \n" +
                "join team_member on team.id = team_member.team_id \n" +
                "where team_member.u_id = "+u_Id;
        this.getData(this.query);
        try {
            while(this.resultSet.next()){
                Team team = new Team();
                team.id = this.resultSet.getInt("id");
                team.name = this.resultSet.getString("name");
                team.created_date = this.resultSet.getString("created_date");

                teamList.add(team);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return teamList;
    }
    public ArrayList<Team> getAllByTeamMemberIdAndTeamLead(int u_Id){
        ArrayList<Team> teamList = new ArrayList<Team>();
        this.query = "Select team.id, team.name, team.created_by, team.created_date\n" +
                "from "+this.tableName+"  \n" +
                "join team_member on team.id = team_member.team_id \n" +
                "where team_member.u_id = "+u_Id+" and team_member.is_lead = 1";
        this.getData(this.query);
        try {
            while(this.resultSet.next()){
                Team team = new Team();
                team.id = this.resultSet.getInt("id");
                team.name = this.resultSet.getString("name");
                team.created_date = this.resultSet.getString("created_date");

                teamList.add(team);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return teamList;
    }
    public boolean isTeamExist(){
        String sql = "select id from "+this.tableName+" where name = '" + this.name + "'  limit 1";
        this.getData(sql);

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
    public boolean isTeamExistByOthers(){
        String sql = "select id from "+this.tableName+" where name = '" + this.name + "' and id!= "+this.id+" limit 1";
        this.getData(sql);

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
    public boolean delete(){
        this.query = "DELETE FROM "+this.tableName+" WHERE id = "+this.id;
        boolean status = this.updateData(this.query);
        if(status){
            TeamMemberModel teamMemberModel = new TeamMemberModel();
            teamMemberModel.team_id = this.id;
            status = teamMemberModel.deleteByTeamId();
        }
        return status;
    }
    public boolean isTeamOwner(int u_id){

        this.query = "select id from "+this.tableName+" where created_by = "+u_id+" and id = "+this.id;
        this.getData(this.query);
        try {
            while(this.resultSet.next()){
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return false;
    }
    public boolean isTeamLeadOfTeam(int u_id){
        this.query = "Select team.id, team.name, team.created_by, team.created_date\n" +
                "from "+this.tableName+"  \n" +
                "join team_member on team.id = team_member.team_id \n" +
                "where team_member.u_id = "+u_id+" and team_member.is_lead = 1 and team.id = "+this.id;


        this.getData(this.query);
        try {
            while(this.resultSet.next()){
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return false;
    }
    public TeamMember getTeamLeadOfTeam(){
        this.query = "Select team_member.id," +
                     " team_member.u_id," +
                     " team_member.team_id," +
                     " team_member.is_lead," +
                     " team_member.created_date as join_date, " +
                     " user_inf.f_name, " +
                     " user_inf.l_name, " +
                     " user_inf.address, " +
                     " user_inf.created_date as user_inf_created_date " +
                     " from "+this.tableName+" " +
                     " join team_member on team.id = team_member.team_id " +
                     " join user_inf ON user_inf.id = team_member.u_id "+
                     " where team_member.is_lead = 1 and team.id = "+this.id;

        TeamMember teamMember = new TeamMember();
        this.getData(this.query);
        try {
            while(this.resultSet.next()){
                teamMember.id = this.resultSet.getInt("u_id");
                teamMember.f_name = this.resultSet.getString("f_name");
                teamMember.l_name = this.resultSet.getString("l_name");
                teamMember.address = this.resultSet.getString("address");
                teamMember.join_date = this.resultSet.getString("join_date");

                Byte isLeadTmp = this.resultSet.getByte("is_lead");
                teamMember.is_lead =(isLeadTmp.intValue()==1)?true:false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return teamMember;
    }
    public boolean isTeamMemberOfTeam(int u_id){
        this.query = "Select team.id, team.name, team.created_by, team.created_date\n" +
                "from "+this.tableName+"  \n" +
                "join team_member on team.id = team_member.team_id \n" +
                "where team_member.u_id = "+u_id+"  and team.id = "+this.id;

        this.getData(this.query);
        try {
            while(this.resultSet.next()){
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return false;
    }
    public int deleteByCreateBy(){
        String sql = "DELETE FROM " + this.tableName + " WHERE created_by = '"+ this.created_by +"'";
        return  this.deleteData(sql);
    }
}