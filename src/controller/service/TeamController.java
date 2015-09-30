package controller.service;

import com.google.gson.Gson;
import model.JoinTeamReqModel;
import model.TeamMemberModel;
import model.TeamModel;
import model.UserInfModel;
import model.datamodel.Login;
import model.datamodel.TeamDetails;
import model.datamodel.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by mi on 8/24/15.
 */
public class TeamController extends HttpServlet {
    Login login;
    ImageTalkBaseController baseController;
    PrintWriter pw;
    @Override
    public void init() throws ServletException {
        super.init();
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        String url = req.getRequestURI().toString();
        this.baseController = new ImageTalkBaseController();
        this.login = new Login();
        this.pw = resp.getWriter();

        if(!this.baseController.isSessionValid(req)) {
            this.pw.print(this.baseController.getResponse());
            return;
        }

        login = this.baseController.getUserLoginFromSession(req);

        if(url.endsWith("/")){
            url = url.substring(0, url.length()-1);
        }

        switch (url) {
            case "/team/add":
                this.add(req);
                break;
            case "/team/update/name":
                this.updateName(req);
                break;
            case "/team/delete":
                this.delete(req);
                break;
            case "/team/is_available":
                this.isAvailable(req);
                break;
            case "/team/my/own/get":
                this.getMyOwnTeamLlist();
                break;
            case "/team/my/get":
                this.getMyTeamLlist();
                break;
            case "/team/get":
                this.getTeamList();
                break;
            case "/team/details":
                this.details(req);
                break;
            case "/team/member":
                this.getTeamMembers(req);
                break;
            case "/team/member/add":
                this.addTeamMember(req);
                break;
            case "/team/member/remove":
                this.removeTeamMember(req);
                break;
            case "/team/member/leave":
                this.leaveTeamMember(req);
                break;
            default:
                break;
        }
        this.pw.close();
    }
    private void add(HttpServletRequest req){
        TeamModel teamModel = new TeamModel();
        Gson gson            = new Gson();
        ArrayList<Integer> membersIdList = new ArrayList<Integer>();


        if (!this.baseController.checkParam("name",req,true)) {
            this.baseController.serviceResponse.responseStat.msg = "Team name required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }else{
            teamModel.name = req.getParameter("name").trim();
        }


        if (this.baseController.checkParam("members",req,true)) {
            try{
                int[] members = gson.fromJson(req.getParameter("members").toString(),int[].class);
                for(int member : members){
                    membersIdList.add(member);
                }
            }catch (Exception e){
                this.baseController.serviceResponse.responseStat.msg = "Members are not in perfect format";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }

        if(teamModel.isTeamExist()){
            this.baseController.serviceResponse.responseStat.msg = "Team name already exist";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }




        teamModel.created_by = login.u_id;
        int teamId = teamModel.insert();

        if(teamId==0){
            this.baseController.serviceResponse.responseStat.msg = "Internal server error";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        TeamMemberModel teamMemberModel = new TeamMemberModel();
        teamMemberModel.team_id = teamId;
        teamMemberModel.u_id =  this.login.u_id;
        teamMemberModel.is_lead = true;


        int teamMemberId  = teamMemberModel.insert();
        teamModel.id = teamId;

        // adding member id if it is sent
        for(int u_id:membersIdList){
            if(!teamModel.isTeamMemberOfTeam(u_id)){
                teamMemberModel.team_id = teamId;
                teamMemberModel.u_id =  u_id;
                teamMemberModel.is_lead = false;
                teamMemberModel.insert();
            }else{
                System.out.println("FROM TeamController Line 158: Same member id multiple occurrence , u_id = "+u_id);
            }

        }

        if(teamMemberId==0){
            this.baseController.serviceResponse.responseStat.msg = "Internal server error";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }


        this.baseController.serviceResponse.responseStat.msg = "Team have been successfully created";
        teamModel.id = teamId;
        teamMemberModel.team_id = teamId;
        TeamDetails teamDetails = teamModel.getAllById();

        this.baseController.serviceResponse.responseData =teamDetails;

        this.pw.print(this.baseController.getResponse());
        return;
    }
    private void updateName(HttpServletRequest req){
        TeamModel teamModel = new TeamModel();
        if (!this.baseController.checkParam("name",req,true)) {
            this.baseController.serviceResponse.responseStat.msg = "Team name required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }else{
            teamModel.name = req.getParameter("name").trim();
        }

        if (!this.baseController.checkParam("team_id",req,true)) {
            this.baseController.serviceResponse.responseStat.msg = "Team id required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }else{
            try{
                teamModel.id = Integer.parseInt(req.getParameter("team_id").trim());
            }catch (NumberFormatException e){
                this.baseController.serviceResponse.responseStat.msg = "Team id not valid format, int required";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }
        if(teamModel.isTeamExistByOthers()){
            this.baseController.serviceResponse.responseStat.msg = "Name occupied by other team,try different name";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        if(!teamModel.updateName()){
            this.baseController.serviceResponse.responseStat.msg = "Internal server error";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        this.baseController.serviceResponse.responseStat.msg = "Team successfully updated";
        this.pw.print(this.baseController.getResponse());
        return;


    }
    private void delete(HttpServletRequest req){
        TeamModel teamModel = new TeamModel();


        if (!this.baseController.checkParam("team_id",req,true)) {
            this.baseController.serviceResponse.responseStat.msg = "Team id required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }else{
            try{
                teamModel.id = Integer.parseInt(req.getParameter("team_id").trim());
            }catch (NumberFormatException e){
                this.baseController.serviceResponse.responseStat.msg = "Team id not in valid format, int required";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }

        }
//        if(!teamModel.isTeamLeadOfTeam(login.u_id)){
//            this.baseController.serviceResponse.responseStat.msg = "Unable to delete, need team lead privilege of this team";
//            this.baseController.serviceResponse.responseStat.status = false;
//            this.pw.print(this.baseController.getResponse());
//            return;
//        }
        if(!teamModel.isTeamOwner(login.u_id)){
            this.baseController.serviceResponse.responseStat.msg = "Unable to delete, need owner privilege of this team";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        if(!teamModel.delete()){
            this.baseController.serviceResponse.responseStat.msg = "Internal server error";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        this.baseController.serviceResponse.responseStat.msg = "Team successfully deleted";
        this.pw.print(this.baseController.getResponse());
        return;
    }
    private void isAvailable(HttpServletRequest req){

        TeamModel teamModel = new TeamModel();

        if (!this.baseController.checkParam("name",req,true)) {
            this.baseController.serviceResponse.responseStat.msg = "Team name required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }else{
            teamModel.name = req.getParameter("name").trim();
        }

        if(teamModel.isTeamExist()){
            this.baseController.serviceResponse.responseStat.msg = "Team name already exist";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        this.baseController.serviceResponse.responseStat.msg = "Team name available";
        this.pw.print(this.baseController.getResponse());
        return;
    }
    private void getMyOwnTeamLlist(){

        TeamModel teamModel = new TeamModel();
        teamModel.created_by = this.login.u_id;

        this.baseController.serviceResponse.responseData =  teamModel.getAllByCreatedBy();
        this.pw.print(this.baseController.getResponse());
        return;
    }
    private void getMyTeamLlist(){

        TeamModel teamModel = new TeamModel();

        this.baseController.serviceResponse.responseData =  teamModel.getUnionOfIsTeamLeadAndCreatedBy(this.login.u_id);
        this.pw.print(this.baseController.getResponse());
        return;
    }
    private void details(HttpServletRequest req){
        TeamModel teamModel = new TeamModel();
        if(this.baseController.checkParam("team_id",req,true)){
            try{
                teamModel.id =Integer.parseInt(req.getParameter("team_id").trim());

                this.baseController.serviceResponse.responseData = teamModel.getAllById();
                this.pw.print(this.baseController.getResponse());
                return;
            }catch (NumberFormatException e){
                this.baseController.serviceResponse.responseStat.msg = "Team id not valid format, int required";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }else{
            this.baseController.serviceResponse.responseStat.msg = "Team id required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

    }
    private void getTeamList(){
        TeamModel teamModel = new TeamModel();


        this.baseController.serviceResponse.responseData =  teamModel.getAllByTeamMemberId(login.u_id);
        this.pw.print(this.baseController.getResponse());
        return;
    }
    private void getTeamMembers(HttpServletRequest req){
        TeamModel teamModel = new TeamModel();

        if (!this.baseController.checkParam("team_id",req,true)) {
            this.baseController.serviceResponse.responseStat.msg = "Team id required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }else{
            try{
                teamModel.id = Integer.parseInt(req.getParameter("team_id").trim());
            }catch (NumberFormatException e){
                this.baseController.serviceResponse.responseStat.msg = "Team id not valid format, int required";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }
        TeamDetails teamDetails  = teamModel.getAllById();

        this.baseController.serviceResponse.responseData = teamDetails.members;
        this.pw.print(this.baseController.getResponse());

        return;
    }
    private void addTeamMember(HttpServletRequest req){
        Gson gson = new Gson();
        TeamModel teamModel = new TeamModel();

        ArrayList<Integer> membersIdList = new ArrayList<Integer> ();

        if (this.baseController.checkParam("members",req,true)) {
            try{
                int[] members = gson.fromJson(req.getParameter("members").toString(),int[].class);
                for(int member : members){
                    membersIdList.add(member);
                }
            }catch (Exception e){
                this.baseController.serviceResponse.responseStat.msg = "Members are not in perfect format";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }
        if (this.baseController.checkParam("team_id",req,true)) {
            try{
                teamModel.id = Integer.parseInt(req.getParameter("team_id"));
            }catch (Exception e){
                this.baseController.serviceResponse.responseStat.msg = "Team id is not in perfect format, int required";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }else{
            this.baseController.serviceResponse.responseStat.msg = "Team id required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        if(!teamModel.isTeamLeadOfTeam(login.u_id)){
            this.baseController.serviceResponse.responseStat.msg = "Unable to add member, need team lead privilege of this team";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        for(int memberId : membersIdList){
            if(teamModel.isTeamMemberOfTeam(memberId)){
                UserInfModel userInfModel = new UserInfModel();
                User user = userInfModel.getById(memberId);
                this.baseController.serviceResponse.responseStat.status = false;
                this.baseController.serviceResponse.responseStat.msg = user.f_name+" "+user.l_name+" already a team member of this team";
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }
        TeamMemberModel teamMemberModel = new TeamMemberModel();

        for(int memberId : membersIdList){

            teamMemberModel.team_id = teamModel.id;
            teamMemberModel.u_id = memberId;

            int id = teamMemberModel.insert();
            if(id<=0){
                this.baseController.serviceResponse.responseStat.status = false;
                this.baseController.serviceResponse.responseStat.msg = "Internal server error";
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }

        this.baseController.serviceResponse.responseStat.msg = "Member successfully added";
        this.pw.print(this.baseController.getResponse());
        return;

    }

    private void removeTeamMember(HttpServletRequest req){
        Gson gson = new Gson();
        TeamModel teamModel = new TeamModel();

        ArrayList<Integer> membersIdList = new ArrayList<Integer> ();

        if (this.baseController.checkParam("members",req,true)) {
            try{
                int[] members = gson.fromJson(req.getParameter("members").toString(),int[].class);
                for(int member : members){
                    membersIdList.add(member);
                }
            }catch (Exception e){
                this.baseController.serviceResponse.responseStat.msg = "Members are not in perfect format";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }
        if (this.baseController.checkParam("team_id",req,true)) {
            try{
                teamModel.id = Integer.parseInt(req.getParameter("team_id"));
            }catch (Exception e){
                this.baseController.serviceResponse.responseStat.msg = "Team id is not in perfect format, int required";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }else{
            this.baseController.serviceResponse.responseStat.msg = "Team id required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        if(!teamModel.isTeamLeadOfTeam(login.u_id)){
            this.baseController.serviceResponse.responseStat.msg = "Unable to remove member, need team lead privilege of this team";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        TeamMemberModel teamMemberModel = new TeamMemberModel();
        JoinTeamReqModel joinTeamReqModel = new JoinTeamReqModel();
        for(int memberId : membersIdList){

            teamMemberModel.team_id = teamModel.id;
            teamMemberModel.u_id = memberId;
            joinTeamReqModel.team_id = teamModel.id;
            joinTeamReqModel.u_id = memberId;

            joinTeamReqModel.deleteByTeamIdAndUid();

            if(!teamMemberModel.deleteByTeamIdAndUid()){
                this.baseController.serviceResponse.responseStat.status = false;
                this.baseController.serviceResponse.responseStat.msg = "Internal server error";
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }

        this.baseController.serviceResponse.responseStat.msg = "Member successfully removed";
        this.pw.print(this.baseController.getResponse());
        return;

    }
    private void leaveTeamMember(HttpServletRequest req){

        TeamModel teamModel = new TeamModel();


        if (this.baseController.checkParam("team_id",req,true)) {
            try{
                teamModel.id = Integer.parseInt(req.getParameter("team_id"));
            }catch (Exception e){
                this.baseController.serviceResponse.responseStat.msg = "Team id is not in perfect format, int required";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }else{
            this.baseController.serviceResponse.responseStat.msg = "Team id required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        if(!teamModel.isTeamMemberOfTeam(login.u_id)){
            this.baseController.serviceResponse.responseStat.msg = "Not a member of this team";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        if(teamModel.isTeamLeadOfTeam(login.u_id)){
            this.baseController.serviceResponse.responseStat.msg = "Not a allowed to leave you own team";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        TeamMemberModel teamMemberModel = new TeamMemberModel();
        JoinTeamReqModel joinTeamReqModel = new JoinTeamReqModel();

        teamMemberModel.team_id = teamModel.id;
        teamMemberModel.u_id = login.u_id;

        if(!teamMemberModel.leaveTeam()){
            this.baseController.serviceResponse.responseStat.status = false;
            this.baseController.serviceResponse.responseStat.msg = "Internal server error";
            this.pw.print(this.baseController.getResponse());
            return;
        }

        this.baseController.serviceResponse.responseStat.msg = "You have left the team";
        this.pw.print(this.baseController.getResponse());
        return;

    }
}
