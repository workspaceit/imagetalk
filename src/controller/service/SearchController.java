package controller.service;

import model.JoinTeamReqModel;
import model.TeamMemberModel;
import model.TeamModel;
import model.UserInfModel;
import model.datamodel.Login;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by mi on 8/24/15.
 */
public class SearchController extends HttpServlet {
    ImageTalkBaseController baseController;
    PrintWriter pw;
    Login login;

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json");

        String url = req.getRequestURI().toString();
        this.baseController = new ImageTalkBaseController();
        this.login = new Login();
        this.pw = resp.getWriter();

        if (!this.baseController.isSessionValid(req)) {
            this.pw.print(this.baseController.getResponse());
            return;
        }

        login = this.baseController.getUserLoginFromSession(req);

        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        switch (url) {
            case "/search/user":
                this.searchUser(req);
                break;
            case "/search/user/foradd":
                this.searchUserForAdd(req);
                break;
            case "/search/team/formanagement":
                this.searchTeamForTeamManagement(req);
                break;
            case "/search/user/formanagement":
                this.searchTeamMemberForTeamManagement(req);
                break;
            case "/search/team/forjoinrequest":
                this.searchTeamMemberForTeamRequest(req);
                break;
            case "/search/user/request/my/jointeam":
                this.searchMyUserSendJoinTeamRequest(req);
                break;
            case "/search/user/request/my/jointeam/team_id":
                this.searchUserSendJoinRequestToMyTeamByTeamId(req);
                break;
            default:
                break;
        }
        this.pw.close();
    }

    public void searchUser(HttpServletRequest req) {
        UserInfModel userInfModel = new UserInfModel();
        String keyword = null;
        if (this.baseController.checkParam("keyword", req, true)) {
            keyword = req.getParameter("keyword").trim();
        }

        this.baseController.serviceResponse.responseData = userInfModel.getAllByKeyword(keyword);
        this.pw.print(this.baseController.getResponse());
        return;
    }

    public void searchUserForAdd(HttpServletRequest req) {
        UserInfModel userInfModel = new UserInfModel();
        String keyword = null;
        if (this.baseController.checkParam("keyword", req, true)) {
            keyword = req.getParameter("keyword").trim();
        }
        userInfModel.id = login.u_id;
        //this.baseController.serviceResponse.responseData = userInfModel.getAllByKeyword(keyword);

        this.baseController.serviceResponse.responseData = userInfModel.getAllUserLoginByKeyword(keyword);
        this.pw.print(this.baseController.getResponse());
        return;

    }

    public void searchTeamForTeamManagement(HttpServletRequest req) {
        TeamModel teamModel = new TeamModel();
        String keyword = null;
        if (this.baseController.checkParam("keyword", req, true)) {
            keyword = req.getParameter("keyword").trim();
        }

        this.baseController.serviceResponse.responseData = teamModel.getUnionOfIsTeamLeadAndCreatedByAndKeyword(this.login.u_id, keyword);
        this.pw.print(this.baseController.getResponse());
        return;
    }

    public void searchTeamMemberForTeamManagement(HttpServletRequest req) {

        TeamMemberModel teamMemberModel = new TeamMemberModel();

        if (!this.baseController.checkParam("team_id", req, true)) {
            this.baseController.serviceResponse.responseStat.msg = "Team id required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        } else {
            try {
                teamMemberModel.team_id = Integer.parseInt(req.getParameter("team_id").trim());
            } catch (NumberFormatException e) {
                this.baseController.serviceResponse.responseStat.msg = "Team id not valid format, int required";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }

        String keyword = null;
        if (this.baseController.checkParam("keyword", req, true)) {
            keyword = req.getParameter("keyword").trim();
        }


        this.baseController.serviceResponse.responseData = teamMemberModel.getAllTeamMemberByTeamIdAndKeyWord(keyword);
        this.pw.print(this.baseController.getResponse());

        return;
    }
    private void searchTeamMemberForTeamRequest(HttpServletRequest req){
        JoinTeamReqModel joinTeamReqModel=new JoinTeamReqModel();

       /* if(!this.baseController.checkParam("team_id",req,true)){
            this.baseController.serviceResponse.responseStat.msg = "Team id required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }else{
            try {
                joinTeamReqModel.team_id = Integer.parseInt(req.getParameter("team_id").trim());
            } catch (NumberFormatException e) {
                this.baseController.serviceResponse.responseStat.msg = "Team id not valid format, int required";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }*/

        String keyword = null;
        if (this.baseController.checkParam("keyword", req, true)) {
            keyword = req.getParameter("keyword").trim();
        }
        joinTeamReqModel.u_id=login.u_id;
        this.baseController.serviceResponse.responseData = joinTeamReqModel.getTeamListForRequestByUidAndKeyword(keyword);
        this.pw.print(this.baseController.getResponse());
        return;
    }
    private void searchMyUserSendJoinTeamRequest(HttpServletRequest req){
        JoinTeamReqModel joinTeamReqModel = new JoinTeamReqModel();
        joinTeamReqModel.u_id= login.u_id;
        String keyword = null;
        if (this.baseController.checkParam("keyword", req, true)) {
            keyword = req.getParameter("keyword").trim();
        }
        this.baseController.serviceResponse.responseData = joinTeamReqModel.getTeamAreRequested(keyword);
        this.pw.print(this.baseController.getResponse());
        return;
    }
    private void searchUserSendJoinRequestToMyTeamByTeamId(HttpServletRequest req){
        TeamModel teamModel = new TeamModel();

        JoinTeamReqModel joinTeamReqModel = new JoinTeamReqModel();
        joinTeamReqModel.u_id= login.u_id;

        if (!this.baseController.checkParam("team_id", req, true)) {
            this.baseController.serviceResponse.responseStat.msg = "Team id required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        } else {
            try {
                joinTeamReqModel.team_id = Integer.parseInt(req.getParameter("team_id").trim());
                teamModel.id =joinTeamReqModel.team_id;

            } catch (NumberFormatException e) {
                this.baseController.serviceResponse.responseStat.msg = "Team id not valid format, int required";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }
        if(!teamModel.isTeamLeadOfTeam(login.u_id)){
            this.baseController.serviceResponse.responseStat.msg = "You are not team lead of this team";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }


        this.baseController.serviceResponse.responseData = joinTeamReqModel.getTeamAreRequestedByTeamId();
        this.pw.print(this.baseController.getResponse());
        return;
    }
}
