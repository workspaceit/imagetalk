package model;

import model.datamodel.JoinTeamReq;
import model.datamodel.JoinTeamUser;
import model.datamodel.Team;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by mi on 8/26/15.
 */
public class JoinTeamReqModel extends  ImageTalkBaseModel {
    public int   id;

    public int team_id;
    public int u_id;
    public int requestStatus;
    public JoinTeamReqModel() {
        super();
        this.id = 0;
        this.u_id = 0;
        this.team_id = 0;
        this.requestStatus = 100;
        this.tableName = "join_team_req";
    }
    public int insert(){
        this.query = "INSERT INTO "+this.tableName+" (team_id,u_id,status) VALUES ("+this.team_id+","+this.u_id+",0)";
        return this.insertData(this.query);
    }
    public boolean isAlreadySendRequest(){
        this.query = "select id,status from "+this.tableName+" where team_id = "+this.team_id+" and u_id = "+this.u_id;
        System.out.println(this.query );
        this.getData(this.query);
        try {
            while(this.resultSet.next()){
                this.requestStatus = this.resultSet.getInt("status");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return false;
    }
    public ArrayList<JoinTeamReq> getRequestOnMyTeam(ArrayList<Integer> teamIdList){

        ArrayList<JoinTeamReq> joinTeamReqsList = new ArrayList<JoinTeamReq>();

        String teamIdListStr = "(";

        teamIdListStr += ")";
        this.query = "SELECT team.id,team.name,team.created_by,team.created_date,join_team_req.status FROM team "+
                        "join join_team_req on team.id = join_team_req.team_id where team.id in "+teamIdListStr;
        this.getData(this.query);

        try {
            while(this.resultSet.next()){
                JoinTeamReq joinTeamReq = new JoinTeamReq();

                joinTeamReq.id = this.resultSet.getInt("id");
                joinTeamReq.name = this.resultSet.getString("name");
                joinTeamReq.created_date = this.resultSet.getString("created_date");
                joinTeamReq.requestStatus = this.resultSet.getInt("status");

                joinTeamReqsList.add(joinTeamReq);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return joinTeamReqsList;
    }
    public ArrayList<JoinTeamUser> getTeamAreRequested(String keyword){

        String teamIdInStr = "";

        ArrayList<JoinTeamUser> joinTeamUserList = new ArrayList<JoinTeamUser>();
        TeamModel teamModel = new TeamModel();

        ArrayList<Team> teamMembrList = teamModel.getUnionOfIsTeamLeadAndCreatedBy(this.u_id);

        int i=0;
        if(teamMembrList.size()==0){
            return joinTeamUserList;
        }
        for(Team team : teamMembrList){
            i++;
            teamIdInStr+=String.valueOf(team.id);
            if(i<teamMembrList.size()){
                teamIdInStr+=",";
            }

        }

        this.query ="SELECT team.id team_id,team.name,team.created_date as team_c_date, " +
                " user_inf.id as user_inf_id,user_inf.f_name, user_inf.l_name,user_inf.address,user_inf.created_date as user_c_date FROM join_team_req  " +
                " join  team on team.id = join_team_req.team_id and join_team_req.status = 0 " +
                " join user_inf on join_team_req.u_id = user_inf.id ";

        this.query +=" where team_id in ("+teamIdInStr+") ";

        if(keyword!=null && keyword!=""){
            this.query +=" and user_inf.f_name like '%"+keyword+"%'";
        }


       this.getData(this.query);

        try {
            while(this.resultSet.next()){
                JoinTeamUser joinTeamUser = new JoinTeamUser();

                joinTeamUser.team.id = this.resultSet.getInt("team_id");
                joinTeamUser.team.name = this.resultSet.getString("name");
                joinTeamUser.team.created_date = this.resultSet.getString("team_c_date");

                joinTeamUser.user.id =this.resultSet.getInt("user_inf_id");
                joinTeamUser.user.f_name = this.resultSet.getString("f_name");
                joinTeamUser.user.l_name =this.resultSet.getString("l_name");
                joinTeamUser.user.address =this.resultSet.getString("address");
                joinTeamUser.user.created_date = this.resultSet.getString("user_c_date");

                joinTeamUserList.add(joinTeamUser);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return joinTeamUserList;
    }
    public JoinTeamUser getTeamAreRequestedByTeamId(){


        this.query ="SELECT team.id team_id,team.name,team.created_date as team_c_date, " +
                " user_inf.id as user_inf_id,user_inf.f_name, user_inf.l_name,user_inf.address,user_inf.created_date as user_c_date FROM join_team_req  " +
                " join  team on team.id = join_team_req.team_id and join_team_req.status = 0 " +
                " join user_inf on join_team_req.u_id = user_inf.id " +
                " where team_id = "+this.team_id+" limit 1";


        this.getData(this.query);
        JoinTeamUser joinTeamUser = new JoinTeamUser();
        try {
            while(this.resultSet.next()){


                joinTeamUser.team.id = this.resultSet.getInt("team_id");
                joinTeamUser.team.name = this.resultSet.getString("name");
                joinTeamUser.team.created_date = this.resultSet.getString("team_c_date");

                joinTeamUser.user.id =this.resultSet.getInt("user_inf_id");
                joinTeamUser.user.f_name = this.resultSet.getString("f_name");
                joinTeamUser.user.l_name =this.resultSet.getString("l_name");
                joinTeamUser.user.address =this.resultSet.getString("address");
                joinTeamUser.user.created_date = this.resultSet.getString("user_c_date");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return joinTeamUser;
    }
    public ArrayList<JoinTeamReq> getTeamListForRequestByUidAndKeyword(String keyword){
        ArrayList<JoinTeamUser> joinTeamUserList = new ArrayList<JoinTeamUser>();
        TeamModel teamModel = new TeamModel();

        ArrayList<Team> teamMembrList = teamModel.getAllByTeamMemberId(this.u_id);

        int i=0;
        String teamIdInStr = "(";
        for(Team team : teamMembrList){
            i++;
            teamIdInStr+=String.valueOf(team.id);
            if(i<teamMembrList.size()){
                teamIdInStr+=",";
            }

        }

        teamIdInStr +=")";
        ArrayList<JoinTeamReq> joinTeamReqsList = new ArrayList<JoinTeamReq>();

        this.query = "SELECT team.id,team.name," +
                " team.created_by," +
                " team.created_date," +
                " ifnull(join_team_req.status,100) as requestStatus " +
                " FROM join_team_req  right join  team on team.id = join_team_req.team_id and join_team_req.u_id ="+this.u_id;
        if(teamMembrList.size()>0){
            this.query +=" where team.id not in "+teamIdInStr+" ";

            if(keyword!=null && keyword!=""){
                this.query +=" and team.name like '%"+keyword+"%' ";
            }
        }else{
            if(keyword!=null && keyword!=""){
                this.query +=" where team.name like '%"+keyword+"%' ";
            }
        }


        this.getData(this.query);

        try {
            while(this.resultSet.next()){
                JoinTeamReq joinTeamReq = new JoinTeamReq();

                joinTeamReq.id = this.resultSet.getInt("id");
                joinTeamReq.name = this.resultSet.getString("name");
                joinTeamReq.created_date = this.resultSet.getString("created_date");
                joinTeamReq.requestStatus = this.resultSet.getInt("requestStatus");

                joinTeamReqsList.add(joinTeamReq);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return joinTeamReqsList;
    }
    public boolean acceptRequest(int u_id){
        TeamMemberModel teamMemberModel = new TeamMemberModel();
        teamMemberModel.u_id = u_id;
        teamMemberModel.team_id = this.team_id;
        if(teamMemberModel.addTeamMember()<=0){
            this.errorMsg = ( this.errorMsg ==null)?"Internal server error": this.errorMsg;
            return false;
        }
        this.query = "UPDATE join_team_req SET status=1 WHERE team_id = "+this.team_id+" and u_id = "+u_id;
        return this.updateData(this.query);
    }
    public boolean rejectRequest(int u_id){
        this.query = "UPDATE join_team_req SET status=-1 WHERE team_id = "+this.team_id+" and u_id = "+u_id;
        return this.updateData(this.query);
    }
    public boolean sendRequestAgain(){
        this.query = "UPDATE join_team_req SET status=0 WHERE team_id = "+this.team_id+" and u_id = "+this.u_id;
        System.out.println(this.query);
        return this.updateData(this.query);
    }
    public boolean deleteByTeamIdAndUid(){
        this.query = "DELETE FROM "+this.tableName+" WHERE team_id = "+this.team_id+" and u_id="+this.u_id;
        return this.updateData(this.query);
    }
}
