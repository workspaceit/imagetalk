package controller.admin;

import com.google.gson.Gson;
import controller.service.ImageTalkBaseController;
import gps_socket.CabGuardServerSocket;
import gps_socket.CentralSocketController;
import model.*;
import model.datamodel.Login;
import model.datamodel.Team;
import model.datamodel.TeamDetails;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mi on 9/2/15.
 */
public class AdminController extends HttpServlet {
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
        if(!this.baseController.isAdmin(req)){
            this.pw.print(this.baseController.getResponse());
            return;
        }
        login = this.baseController.getUserLoginFromSession(req);

        if(url.endsWith("/")){
            url = url.substring(0, url.length()-1);
        }

        switch (url) {
            case "/admin/operation/team/add":
                this.addTeam(req);
                break;
            case "/admin/operation/team/update/basic":
                this.updateTeam(req);
                break;
            case "/admin/operation/team/delete":
                this.deleteTeam(req);
                break;
            case "/admin/operation/user/add":
                this.addUser(req, resp);
                break;
            case "/admin/operation/admin/add":
                this.addAdminUser(req, resp);
                break;
            case "/admin/operation/user/delete":
                this.deleteUser(req);
                break;
            case "/admin/operation/admin_user/delete":
                this.deleteAdminUser(req);
                break;
            case "/admin/operation/socket_server/start":
                this.startSocketServer();
            default:
                break;
        }
        this.pw.close();
    }
    public void addTeam(HttpServletRequest req){
        TeamModel teamModel = new TeamModel();
        Gson gson            = new Gson();
        ArrayList<Integer> membersIdList = new ArrayList<Integer>();
        int teamLeadId = 0;

        if (!this.baseController.checkParam("name",req,true)) {
            this.baseController.serviceResponse.responseStat.msg = "Team name required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }else{
            teamModel.name = req.getParameter("name").trim();
        }

        if (this.baseController.checkParam("members",req,true)) {
            this.baseController.serviceResponse.responseStat.msg = "Team lead id required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }else{
            try{
                teamLeadId = Integer.parseInt(req.getParameter("team_lead_id").trim());
                if(teamLeadId<=0){
                    this.baseController.serviceResponse.responseStat.msg = "Select team lead";
                    this.baseController.serviceResponse.responseStat.status = false;
                    this.pw.print(this.baseController.getResponse());
                    return;
                }
            }catch (Exception e){
                this.baseController.serviceResponse.responseStat.msg = "Team lead id is not in perfect format,Int required";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
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
        teamMemberModel.u_id =  teamLeadId;
        teamMemberModel.is_lead = true;


        int teamMemberId  = teamMemberModel.insert();


        // adding member id if it is sent
        for(int u_id:membersIdList){
            teamMemberModel.team_id = teamId;
            teamMemberModel.u_id =  u_id;
            teamMemberModel.is_lead = false;
            teamMemberModel.insert();
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
    public void addUser(HttpServletRequest req, HttpServletResponse resp){
        resp.setContentType("application/json");

        this.baseController = new ImageTalkBaseController();
        LoginModel loginModel = new LoginModel();
        UserInfModel userInfModel = new UserInfModel();

        if (this.baseController.checkParam("f_name",req,true)) {
            userInfModel.f_name = req.getParameter("f_name");
        } else {
            this.baseController.serviceResponse.responseStat.msg = "First name empty";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        if (this.baseController.checkParam("l_name",req,true)) {
            userInfModel.l_name = req.getParameter("l_name");
        } else {
            this.baseController.serviceResponse.responseStat.msg = "Last name empty";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        if (this.baseController.checkParam("address",req,true)) {
            userInfModel.address = req.getParameter("address");
        } else {
            this.baseController.serviceResponse.responseStat.msg = "Address empty";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        if (this.baseController.checkParam( "email",req, true)) {
            // String email_regex = "[A-Z]+[a-zA-Z_]+@\b([a-zA-Z]+.){2}\b?.[a-zA-Z]+";
            String testString = req.getParameter("email").trim();

            String EMAIL_PATTERN ="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
            Pattern pattern = Pattern.compile(EMAIL_PATTERN);
            Matcher matcher = pattern.matcher(testString);

            if(matcher.matches()){
                loginModel.email = req.getParameter("email");
            }else{
                this.baseController.serviceResponse.responseStat.msg = "Email is not valid";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }


        } else {
            this.baseController.serviceResponse.responseStat.msg = "Email empty";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        if (this.baseController.checkParam("password",req,true)) {
            loginModel.password = req.getParameter("password");
            if(loginModel.password.length() < 6){
                this.baseController.serviceResponse.responseStat.msg = "Password at least 6 digit required";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        } else {
            this.baseController.serviceResponse.responseStat.msg = "Password empty";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }




        if(loginModel.isEmailExist(loginModel.email)){
            this.baseController.serviceResponse.responseStat.status = false;
            this.baseController.serviceResponse.responseStat.msg = "Email already used";
            this.pw.println(this.baseController.getResponse());
            return;
        }

        if (this.baseController.checkParam("type",req,true)) {
            String type = req.getParameter("type").trim();
            System.out.println("type = "+type);
            if(type.equals("ADMIN")){
                System.out.println("type = 1 block");
                loginModel.type = 1;
            }else if(type.equals("TEAM_LEAD")){
                System.out.println("type = 2 block");
                loginModel.type = 2;
            }else{
                System.out.println("type = 3 block");
                loginModel.type = 3;
            }
        }else{
            loginModel.type = 3;
        }


        int userId = userInfModel.insertData();
        if (userId<=0) {
            userInfModel.deleteData(userId);
            this.pw.println(this.baseController.getResponse());
            return;
        }

        loginModel.u_id = userId;
        int loginId = loginModel.insertData(userId);

        if (loginId<=0) {
            userInfModel.deleteData(userId);
            this.pw.println(this.baseController.getResponse());
            return;
        }
        UserStatusModel userStatusModel = new UserStatusModel();
        userStatusModel.status_id = 1;
        userStatusModel.login_id = loginId;

        int userStatusId =userStatusModel.insert();

        if (userStatusId<0) {
            userInfModel.deleteData(userId);
            this.pw.println(this.baseController.getResponse());
            return;
        }

        this.baseController.serviceResponse.responseStat.status = true;
        this.baseController.serviceResponse.responseStat.msg = "Registration Successfully";
        this.pw.println(this.baseController.getResponse());
        return;
    }
    public void addAdminUser(HttpServletRequest req, HttpServletResponse resp){
        resp.setContentType("application/json");

        this.baseController = new ImageTalkBaseController();
        LoginModel loginModel = new LoginModel();
        UserInfModel userInfModel = new UserInfModel();

        if (this.baseController.checkParam("f_name",req,true)) {
            userInfModel.f_name = req.getParameter("f_name");
        } else {
            this.baseController.serviceResponse.responseStat.msg = "First name empty";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        if (this.baseController.checkParam("l_name",req,true)) {
            userInfModel.l_name = req.getParameter("l_name");
        } else {
            this.baseController.serviceResponse.responseStat.msg = "Last name empty";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

//        if (this.baseController.checkParam("address",req,true)) {
//            userInfModel.address = req.getParameter("address");
//        } else {
//            this.baseController.serviceResponse.responseStat.msg = "Address empty";
//            this.baseController.serviceResponse.responseStat.status = false;
//            this.pw.print(this.baseController.getResponse());
//            return;
//        }

        if (this.baseController.checkParam( "email",req, true)) {
            // String email_regex = "[A-Z]+[a-zA-Z_]+@\b([a-zA-Z]+.){2}\b?.[a-zA-Z]+";
            String testString = req.getParameter("email").trim();

            String EMAIL_PATTERN ="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
            Pattern pattern = Pattern.compile(EMAIL_PATTERN);
            Matcher matcher = pattern.matcher(testString);

            if(matcher.matches()){
                loginModel.email = req.getParameter("email");
            }else{
                this.baseController.serviceResponse.responseStat.msg = "Email is not valid";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }


        } else {
            this.baseController.serviceResponse.responseStat.msg = "Email empty";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        if (this.baseController.checkParam("password",req,true)) {
            loginModel.password = req.getParameter("password");
            if(loginModel.password.length() < 6){
                this.baseController.serviceResponse.responseStat.msg = "Password at least 6 digit required";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        } else {
            this.baseController.serviceResponse.responseStat.msg = "Password empty";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }




        if(loginModel.isEmailExist(loginModel.email)){
            this.baseController.serviceResponse.responseStat.status = false;
            this.baseController.serviceResponse.responseStat.msg = "Email already used";
            this.pw.println(this.baseController.getResponse());
            return;
        }
        // Setting type is Admin
        loginModel.type = 1;


        int userId = userInfModel.insertData();
        if (userId<=0) {
            userInfModel.deleteData(userId);
            this.pw.println(this.baseController.getResponse());
            return;
        }

        loginModel.u_id = userId;
        int loginId = loginModel.insertData(userId);

        if (loginId<=0) {
            userInfModel.deleteData(userId);
            this.pw.println(this.baseController.getResponse());
            return;
        }
        UserStatusModel userStatusModel = new UserStatusModel();
        userStatusModel.status_id = 1;
        userStatusModel.login_id = loginId;

        int userStatusId =userStatusModel.insert();

        if (userStatusId<0) {
            userInfModel.deleteData(userId);
            this.pw.println(this.baseController.getResponse());
            return;
        }

        this.baseController.serviceResponse.responseStat.status = true;
        this.baseController.serviceResponse.responseStat.msg = "Registration Successfully";
        this.pw.println(this.baseController.getResponse());
        return;
    }
    private void updateTeam(HttpServletRequest req){
        TeamModel teamModel = new TeamModel();
        int team_lead_id = 0;
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

        if (!this.baseController.checkParam("team_lead_id",req,true)) {
            this.baseController.serviceResponse.responseStat.msg = "Team lead id required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }else{
            try{
                team_lead_id = Integer.parseInt(req.getParameter("team_lead_id").trim());
            }catch (NumberFormatException e){
                this.baseController.serviceResponse.responseStat.msg = "Team lead id not valid format, int required";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }

        LoginModel loginModel = new LoginModel();
        loginModel.id = team_lead_id;

        if(!loginModel.isTeamLead()){
            this.baseController.serviceResponse.responseStat.msg = "The person selected is not team lead";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
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

        TeamMemberModel teamMemberModel = new TeamMemberModel();
        Login teamLead =  loginModel.getAllById(team_lead_id);

        teamLead = loginModel.getAllById(team_lead_id);
        teamMemberModel.team_id = teamModel.id;
        teamMemberModel.u_id = teamLead.u_id;

        if(!teamMemberModel.updateTeamLead()){
            this.baseController.serviceResponse.responseStat.msg = "Team name update but unable to update team lead";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        this.baseController.serviceResponse.responseStat.msg = "Team successfully updated";
        this.pw.print(this.baseController.getResponse());
        return;


    }
    private void deleteTeam(HttpServletRequest req){
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
    private void deleteUser(HttpServletRequest req){
        int u_id=0;
        int login_id = 0;
        if (!this.baseController.checkParam("u_id",req,true)) {
            this.baseController.serviceResponse.responseStat.msg = "User id required";
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

        if (!this.baseController.checkParam("login_id",req,true)) {
            this.baseController.serviceResponse.responseStat.msg = "User id required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }else{
            try{
                login_id = Integer.parseInt(req.getParameter("login_id").trim());
            }catch (NumberFormatException e){
                this.baseController.serviceResponse.responseStat.msg = "login id not valid format, int required";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }

        LoginModel loginModel = new LoginModel();
        UserInfModel userInfModel = new UserInfModel();
        TeamModel teamModel = new TeamModel();
        TeamMemberModel teamMemberModel = new TeamMemberModel();

        loginModel.login.id = login_id;
        userInfModel.id = u_id;
        teamModel.created_by = u_id;
        teamMemberModel.u_id = u_id;

        ArrayList<Team> teamList = teamModel.getAllByCreatedBy();
        if(teamList.size()>0){
            this.baseController.serviceResponse.responseStat.msg +="Below teams are created by this user <br>";
            for(Team t :teamList) {
                this.baseController.serviceResponse.responseStat.msg += " " + t.name + "  <br>";
            }
            this.baseController.serviceResponse.responseStat.msg += "In order to delete the user those team have to be deleted";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        ArrayList<Team> leadTeamList = teamModel.getIsTeamLeadTeams(u_id);
        if(leadTeamList.size()>0){
            this.baseController.serviceResponse.responseStat.msg +="Below teams are leaded by this user <br>";
            for(Team t :leadTeamList) {
                this.baseController.serviceResponse.responseStat.msg += " " + t.name + "  <br>";
            }
            this.baseController.serviceResponse.responseStat.msg += "In order to delete the user new team lead have to be assigned to those team ";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        if(loginModel.deleteById()<=0){
            this.baseController.serviceResponse.responseStat.msg = "Internal server error at login model";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        if(userInfModel.deleteById()<=0){
            this.baseController.serviceResponse.responseStat.msg = "Internal server error at userinf model";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
//        if(teamModel.deleteByCreateBy()<=0){
//
//        }
        if(teamMemberModel.deleteByUid()<=0){

        }
        this.baseController.serviceResponse.responseStat.msg = "User successfully deleted";
        this.pw.print(this.baseController.getResponse());
        return;

    }
    private void deleteAdminUser(HttpServletRequest req){
        int u_id=0;
        int login_id = 0;
        if (!this.baseController.checkParam("u_id",req,true)) {
            this.baseController.serviceResponse.responseStat.msg = "User id required";
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

        if (!this.baseController.checkParam("login_id",req,true)) {
            this.baseController.serviceResponse.responseStat.msg = "User id required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }else{
            try{
                login_id = Integer.parseInt(req.getParameter("login_id").trim());
            }catch (NumberFormatException e){
                this.baseController.serviceResponse.responseStat.msg = "login id not valid format, int required";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }

        LoginModel loginModel = new LoginModel();
        UserInfModel userInfModel = new UserInfModel();
        TeamModel teamModel = new TeamModel();
        TeamMemberModel teamMemberModel = new TeamMemberModel();

        loginModel.login.id = login_id;
        userInfModel.id = u_id;
        teamModel.created_by = u_id;
        teamMemberModel.u_id = u_id;





        if(loginModel.deleteById()<=0){
            this.baseController.serviceResponse.responseStat.msg = "Internal server error at login model";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        if(userInfModel.deleteById()<=0){
            this.baseController.serviceResponse.responseStat.msg = "Internal server error at userinf model";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        this.baseController.serviceResponse.responseStat.msg = "User successfully deleted";
        this.pw.print(this.baseController.getResponse());
        return;

    }
    private void startSocketServer(){
        int portNumber = 9091;

        try {
            if(CentralSocketController.serverSocket!=null){
                this.baseController.serviceResponse.responseStat.status = false;
                this.baseController.serviceResponse.responseStat.msg ="Socket started already started";
                this.pw.println(this.baseController.getResponse());
                return;
            }
            CentralSocketController.serverSocket = new ServerSocket(portNumber);
            final ServerSocket serverSocket =  CentralSocketController.serverSocket;
            class ServerAuthThread extends Thread{
                public CabGuardServerSocket echoSocket;
                public Socket socket;

                @Override
                public void run(){
                    CabGuardServerSocket echoSocket = new CabGuardServerSocket(this.socket);
                    if(echoSocket.isAuthentic()){
                        System.out.println("SOCKET Auth COMPLETED :");
                        CentralSocketController.clientSocketList.put(echoSocket.sLogin.u_id,echoSocket);
                        echoSocket.setLive();
                    }else{
                        System.out.println("SOCKET Auth Failed :");
                        try {
                            this.socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Set<Integer> keys = CentralSocketController.clientSocketList.keySet();  //get all keys
                    for(Integer i: keys)
                    {
                        System.out.println("KEYS :"+i);
                    }
                }
            }
            class ServerThread extends Thread{
                ServerAuthThread serverAuthThread;
                @Override
                public void run(){
                    int u_d=0;

                    while(true){
                        try {

                            Socket socket = serverSocket.accept();
                            this.serverAuthThread = new ServerAuthThread();
                            this.serverAuthThread.socket = socket;
                            this.serverAuthThread.start();

                            u_d++;
                        } catch (IOException ex) {
                            Logger.getLogger(CentralSocketController.class.getName()).log(Level.SEVERE, null, ex);
                            try {
                                serverSocket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            new ServerThread().start();
            System.out.println("Socket Server Started");
        }catch(Exception e) {

        }
        this.baseController.serviceResponse.responseStat.msg ="Socket started";
        this.pw.println(this.baseController.getResponse());
    }
}
