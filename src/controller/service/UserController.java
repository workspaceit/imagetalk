package controller.service;

import model.LoginModel;
import model.datamodel.Login;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by mi on 9/1/15.
 */
public class UserController extends HttpServlet {
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
            case "/admin/user/update/type/teamlead":
                this.makeTeamLead(req);
                break;
            case "/admin/user/update/type/user":
                this.makeUser(req);
                break;
            default:
                break;
        }
        this.pw.close();
    }
    public void makeTeamLead(HttpServletRequest req){
        LoginModel loginModel = new LoginModel();
        if (!this.baseController.checkParam("login_id", req, true)) {
            this.baseController.serviceResponse.responseStat.msg = "Id required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }else{
            try{
                loginModel.login.id = Integer.parseInt(req.getParameter("login_id").trim());
            }catch (NumberFormatException e){
                this.baseController.serviceResponse.responseStat.msg = "Login id not valid format, int required";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }
        if(!loginModel.updateTypeToTeamLead()){
            this.baseController.serviceResponse.responseStat.msg = "Internal server error";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        this.baseController.serviceResponse.responseStat.msg = "Successfully updated";
        this.pw.print(this.baseController.getResponse());
        return;

    }
    public void makeUser(HttpServletRequest req){
        LoginModel loginModel = new LoginModel();
        if (!this.baseController.checkParam("login_id",req,true)) {
            this.baseController.serviceResponse.responseStat.msg = "Id required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }else{
            try{
                loginModel.login.id = Integer.parseInt(req.getParameter("login_id").trim());
            }catch (NumberFormatException e){
                this.baseController.serviceResponse.responseStat.msg = "Login id not valid format, int required";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }
        if(!loginModel.updateTypeToUser()){
            this.baseController.serviceResponse.responseStat.msg = "Internal server error";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        this.baseController.serviceResponse.responseStat.msg = "Successfully updated";
        this.pw.print(this.baseController.getResponse());
        return;

    }
}
