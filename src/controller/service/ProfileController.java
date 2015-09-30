package controller.service;

import model.LoginModel;
import model.UserInfModel;
import model.datamodel.Login;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by mi on 8/21/15.
 */

public class ProfileController extends HttpServlet {
    ImageTalkBaseController baseController;
    PrintWriter pw;
    Login login;
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

        String url = req.getRequestURI().toString();
        if(url.endsWith("/")){
            url = url.substring(0, url.length()-1);
        }
        switch (url) {
            case "/profile/details":
                this.getDetails(req,res);
                break;
            case "/profile/change/inf":
                this.changInf(req,res);
                System.out.println("Came here Switch");
                break;
            case "/profile/change/password":
                this.changePassword(req,res);
                break;
            default:
                break;
        }
        this.pw.close();


    }
    public void getDetails(HttpServletRequest req,HttpServletResponse res)
            throws ServletException,IOException
    {
        int u_id = Integer.parseInt(req.getParameter("u_id"));
        UserInfModel userInfModel = new UserInfModel();
        login = userInfModel.getProfileInformation(u_id);

        this.baseController.serviceResponse.responseData = login;
        this.pw.print(this.baseController.getResponse());

        return;

    }
    public void changInf(HttpServletRequest req,HttpServletResponse res)
            throws ServletException,IOException
    {
        System.out.println("Came here 01");

        UserInfModel userInfModel = new UserInfModel();

        userInfModel.id = this.login.user.id;

        if (!this.baseController.checkParam("f_name",req,true)) {
            this.baseController.serviceResponse.responseStat.msg = "First name required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }else{
            userInfModel.f_name = req.getParameter("f_name").trim();
        }
        if (!this.baseController.checkParam("l_name",req,true)) {
            this.baseController.serviceResponse.responseStat.msg = "Last name required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        else{
            userInfModel.l_name = req.getParameter("l_name").trim();
        }

        if (!this.baseController.checkParam("address",req,true)) {
            this.baseController.serviceResponse.responseStat.msg = "Address name required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }else{
            userInfModel.address = req.getParameter("address").trim();
        }

        if(!userInfModel.update()){
            this.baseController.serviceResponse.responseStat.msg = "Internal server error";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        this.login.user = userInfModel.getById(this.login.user.id);
        this.baseController.setSession(req,login);

        this.baseController.serviceResponse.responseStat.msg = "Successfully updated";
        this.baseController.serviceResponse.responseData = this.login.user;
        this.pw.print(this.baseController.getResponse());
    }
    public void changePassword(HttpServletRequest req,HttpServletResponse res)
            throws ServletException,IOException
    {
        LoginModel loginModel =  new LoginModel();

        if (!this.baseController.checkParam("new_password",req,true)) {
            this.baseController.serviceResponse.responseStat.msg = "Password empty";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }else{
            loginModel.password = req.getParameter("new_password").trim();
        }
        String confirmPassword  = null;
        if (!this.baseController.checkParam("confirm_password",req,true)) {
            this.baseController.serviceResponse.responseStat.msg = "Confirm password empty";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }else{
            confirmPassword = req.getParameter("confirm_password").trim();
        }
        if(!loginModel.password.equals(confirmPassword)){
            this.baseController.serviceResponse.responseStat.msg = "Password miss matched";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        loginModel.login = this.login;

        if(!loginModel.updatePassword()){
            this.baseController.serviceResponse.responseStat.msg = "Internal server error";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        Login tmpLogin = loginModel.getAllById(this.login.id);
        this.login.access_token = tmpLogin.access_token;
        this.login.type = tmpLogin.type;

        this.baseController.serviceResponse.responseStat.msg = "Password update";
        this.baseController.serviceResponse.responseData = this.login;
        this.pw.print(this.baseController.getResponse());
        return;
    }
}