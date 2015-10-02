package controller.admin;

/**
 * Created by mi on 8/20/15.
 */

import controller.service.ImageTalkBaseController;
import model.AdminLoginModel;
import model.datamodel.app.Login;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

public class AdminLoginController extends HttpServlet {
    ImageTalkBaseController baseController;
    PrintWriter pw;
    @Override
    public void init() throws ServletException {
        super.init();
    }
    public class LoginRespose{
        Login login;
        public LoginRespose(){
            this.login =new Login();
        }
    }
    @Override
    public void doPost(HttpServletRequest req,HttpServletResponse res)
            throws ServletException,IOException
    {
        res.setContentType("application/json");
        this.baseController = new ImageTalkBaseController();
        this.pw=res.getWriter();

        String url = req.getRequestURI().toString();

        if(url.endsWith("/")){
            url = url.substring(0, url.length()-1);
        }

        switch (url) {
            case "/admin/authenticate":
                this.adminLoginByEmailAndPassword(req, res);
                break;
            case "/admin/logout":
                this.adminLogout(req);
                break;
            default:
                break;
        }
        this.pw.close();


    }

    private void adminLoginByEmailAndPassword(HttpServletRequest req,HttpServletResponse res)
            throws ServletException,IOException
    {



        String email = req.getParameter("email");
        String password = req.getParameter("password");

        AdminLoginModel adminLoginModel = new AdminLoginModel();
        if(adminLoginModel.isValidAdminLogin(email, password)){
            LoginRespose loginResponse = new LoginRespose();

            loginResponse.login = adminLoginModel.login;
            this.baseController.serviceResponse.responseData = loginResponse;
            this.baseController.setAdminSession(req, adminLoginModel.login);
            this.baseController.serviceResponse.responseStat.msg = "Successfull login";
        }else{
            this.baseController.serviceResponse.responseStat.status = false;
            this.baseController.serviceResponse.responseStat.msg = "Email or password is wrong";
        }

        this.pw.print(this.baseController.getResponse());
    }
    public void adminLogout(HttpServletRequest req) {
        this.baseController.removeSession(req);
        this.baseController.serviceResponse.responseStat.status=true;
        this.baseController.serviceResponse.responseStat.msg = "Logout success";
        this.pw.print(this.baseController.getResponse());
        return;
    }
}