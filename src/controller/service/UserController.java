package controller.service;

import model.AdminLoginModel;
import model.datamodel.app.Login;

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
/*    ImageTalkBaseController baseController;
    PrintWriter pw;*/
    @Override
    public void init() throws ServletException {
        super.init();
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        ImageTalkBaseController baseController = new ImageTalkBaseController();
        String url = req.getRequestURI().toString();
        baseController = new ImageTalkBaseController();
        this.login = new Login();
        PrintWriter pw = resp.getWriter();

        if(!baseController.isSessionValid(req)) {
            pw.print(baseController.getResponse());
            return;
        }

        login = baseController.getUserLoginFromSession(req);

        if(url.endsWith("/")){
            url = url.substring(0, url.length()-1);
        }

        switch (url) {
            case "/admin/user/update/type/teamlead":
                pw.print(this.makeTeamLead(req));
                break;
            case "/admin/user/update/type/user":
                pw.print(this.makeUser(req));
                break;

            default:
                break;
        }
        baseController.closeDbConnection();
        pw.close();
    }
    public String makeTeamLead(HttpServletRequest req){
        ImageTalkBaseController baseController = new ImageTalkBaseController();
        AdminLoginModel adminLoginModel = new AdminLoginModel();
        if (!baseController.checkParam("login_id", req, true)) {
            baseController.serviceResponse.responseStat.msg = "Id required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }else{
            try{
                adminLoginModel.login.id = Integer.parseInt(req.getParameter("login_id").trim());
            }catch (NumberFormatException e){
                baseController.serviceResponse.responseStat.msg = "Login id not valid format, int required";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }
        if(!adminLoginModel.updateTypeToTeamLead()){
            baseController.serviceResponse.responseStat.msg = "Internal server error";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }
        baseController.serviceResponse.responseStat.msg = "Successfully updated";
        return baseController.getResponse();

    }
    public String makeUser(HttpServletRequest req){
        ImageTalkBaseController baseController = new ImageTalkBaseController();
        AdminLoginModel adminLoginModel = new AdminLoginModel();
        if (!baseController.checkParam("login_id",req,true)) {
            baseController.serviceResponse.responseStat.msg = "Id required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }else{
            try{
                adminLoginModel.login.id = Integer.parseInt(req.getParameter("login_id").trim());
            }catch (NumberFormatException e){
                baseController.serviceResponse.responseStat.msg = "Login id not valid format, int required";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }
        if(!adminLoginModel.updateTypeToUser()){
            baseController.serviceResponse.responseStat.msg = "Internal server error";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }
        baseController.serviceResponse.responseStat.msg = "Successfully updated";
        return baseController.getResponse();

    }
}
