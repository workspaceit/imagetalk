package controller.service;

/**
 * Created by mi on 8/26/15.
 */

import model.JoinTeamReqModel;
import model.TeamModel;
import model.datamodel.Login;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


public class JoinTeamController extends HttpServlet {
    ImageTalkBaseController baseController;
    PrintWriter pw;
    Login login;
    JoinTeamReqModel joinTeamReqModel;
    @Override
    public void init() throws ServletException {
        super.init();
    }
    @Override
    public void doPost(HttpServletRequest req,HttpServletResponse res)
            throws ServletException,IOException
    {
        res.setContentType("application/json");
        this.baseController = new ImageTalkBaseController();
        this.pw=res.getWriter();

        login = new Login();
        if(!this.baseController.isSessionValid(req)) {
            this.pw.print(this.baseController.getResponse());
            return;
        }

        login = this.baseController.getUserLoginFromSession(req);

        joinTeamReqModel = new JoinTeamReqModel();

        if (!this.baseController.checkParam("team_id", req, true)) {
            this.baseController.serviceResponse.responseStat.msg = "Team id required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }else{
            try{
                joinTeamReqModel.team_id = Integer.parseInt(req.getParameter("team_id").trim());
            }catch (NumberFormatException e){
                this.baseController.serviceResponse.responseStat.msg = "Team id not valid format, int required";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }
        joinTeamReqModel.u_id = login.u_id;



        String url = req.getRequestURI().toString();
        if(url.endsWith("/")){
            url = url.substring(0, url.length()-1);
        }

        switch (url) {
            case "/join_team/request/onmyteams":
                this.onMyTeamRequest(req);
                break;
            case "/join_team/request/send":
                this.sendRequest(req);
                break;
            case "/join_team/request/accept":
                this.acceptRequest(req);
                break;
            case "/join_team/request/reject":
                this.rejectRequest(req);
                break;
            default:
                break;
        }
        this.pw.close();
    }
    private void sendRequest(HttpServletRequest req){

        TeamModel teamModel=  new TeamModel();
        if (!this.baseController.checkParam("team_id",req,true)) {
            this.baseController.serviceResponse.responseStat.msg = "Team id required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }else{
            try{
                this.joinTeamReqModel.team_id = Integer.parseInt(req.getParameter("team_id").trim());
                teamModel.id =  this.joinTeamReqModel.team_id;
            }catch (NumberFormatException e){
                this.baseController.serviceResponse.responseStat.msg = "Team id not valid format, int required";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }

        this.joinTeamReqModel.u_id = login.u_id;

        if(teamModel.isTeamMemberOfTeam(login.u_id)){
            this.baseController.serviceResponse.responseStat.msg = "Already a team member of this team";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        if(this.joinTeamReqModel.isAlreadySendRequest()){

            this.baseController.serviceResponse.responseStat.msg = "Team join request is sent again";
            if(!this.joinTeamReqModel.sendRequestAgain()){
                this.baseController.serviceResponse.responseStat.msg = "Internal server error";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }else{

            this.baseController.serviceResponse.responseStat.msg = "Team join request is sent";
            if(this.joinTeamReqModel.insert() <= 0){
                this.baseController.serviceResponse.responseStat.msg = "Internal server error";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }
        this.pw.print(this.baseController.getResponse());
        return;
    }
    private void acceptRequest(HttpServletRequest req){
        TeamModel teamModel = new TeamModel();
        teamModel.id = this.joinTeamReqModel.team_id;
        int u_id;
        if (!teamModel.isTeamLeadOfTeam(this.joinTeamReqModel.u_id )){
            this.baseController.serviceResponse.responseStat.msg = "Need team lead permission to do the operation";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        if (!this.baseController.checkParam("u_id", req, true)) {
            this.baseController.serviceResponse.responseStat.msg = "Team id required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }else{
            try{
                u_id = Integer.parseInt(req.getParameter("u_id").trim());
            }catch (NumberFormatException e){
                this.baseController.serviceResponse.responseStat.msg = "User id not valid format, int required";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }

        if(!this.joinTeamReqModel.acceptRequest(u_id)){
            this.baseController.serviceResponse.responseStat.msg = this.joinTeamReqModel.errorMsg;
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        this.baseController.serviceResponse.responseStat.msg = "Member has joined to your team";
        this.pw.print(this.baseController.getResponse());
        return;

    }
    private void rejectRequest(HttpServletRequest req){
        TeamModel teamModel = new TeamModel();
        teamModel.id = this.joinTeamReqModel.team_id;
        int u_id;
        if (!teamModel.isTeamLeadOfTeam(this.joinTeamReqModel.u_id )){
            this.baseController.serviceResponse.responseStat.msg = "Need team lead permission to do the operation";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        if (!this.baseController.checkParam("u_id", req, true)) {
            this.baseController.serviceResponse.responseStat.msg = "Team id required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }else{
            try{
                u_id = Integer.parseInt(req.getParameter("u_id").trim());
            }catch (NumberFormatException e){
                this.baseController.serviceResponse.responseStat.msg = "User id not valid format, int required";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }

        if(!this.joinTeamReqModel.rejectRequest(u_id)){
            this.baseController.serviceResponse.responseStat.msg = this.joinTeamReqModel.errorMsg;
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        this.baseController.serviceResponse.responseStat.msg = "Request has been rejected";
        this.pw.print(this.baseController.getResponse());
        return;

    }
    private void onMyTeamRequest(HttpServletRequest req){
        JoinTeamReqModel joinTeamReqModel = new JoinTeamReqModel();
        joinTeamReqModel.u_id= login.u_id;
        this.baseController.serviceResponse.responseData = joinTeamReqModel.getTeamAreRequested(null);
        this.pw.print(this.baseController.getResponse());
        return;
    }
}
