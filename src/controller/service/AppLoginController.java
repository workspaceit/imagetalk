package controller.service;

/**
 * Created by mi on 8/20/15.
 */

import model.*;
import model.datamodel.app.AppCredential;
import model.datamodel.app.AuthCredential;
import model.datamodel.app.Contact;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class AppLoginController extends HttpServlet {

    
    class LocalResponseClass{
        AuthCredential authCredential;
        ArrayList<Contact> contacts;
        Object extra;

        public LocalResponseClass() {
            this.authCredential = new AuthCredential();
            this.contacts = new ArrayList();
            this.extra = new Object();
        }
    }
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
        ImageTalkBaseController baseController = new ImageTalkBaseController();
        LocalResponseClass localResponseObj  = new LocalResponseClass();
        PrintWriter pw=res.getWriter();

        String url = req.getRequestURI();

        if(url.endsWith("/")){
            url = url.substring(0, url.length()-1);
        }

        switch (url) {

            case "/app/login/authenticate/accesstoken":
                pw.print(this.loginByAccessToken(req, res));
                break;
            case "/app/login/sendpassword":
                pw.print(this.forgetPasword(req, res));
                break;
            case "/app/login/test_session":
                pw.print(this.testSession(req, res));
                break;
            case "/app/login/phone/update":
                pw.print(this.updatePhone(req));
                break;
            default:
                break;
        }
        baseController.closeDbConnection();
        pw.close();
    }

    private String testSession(HttpServletRequest req,HttpServletResponse res)
        throws ServletException,IOException{
        Login login = new Login();
        ImageTalkBaseController baseController = new ImageTalkBaseController();
        if(!baseController.isAppSessionValid(req)){
            baseController.serviceResponse.responseStat.status=false;
            baseController.serviceResponse.responseStat.msg = "Session Expired";
            baseController.serviceResponse.responseStat.isLogin=false;
            return baseController.getResponse();
        }

        baseController.serviceResponse.responseStat.status=true;
        baseController.serviceResponse.responseData = baseController.appCredential;
        return baseController.getResponse();

    }


    private String loginByAccessToken(HttpServletRequest req,HttpServletResponse res)
            throws ServletException,IOException
    {

        String accessToken = req.getParameter("access_token");
        ImageTalkBaseController baseController = new ImageTalkBaseController();
        LocalResponseClass localResponseObj  = new LocalResponseClass();

        AppLoginCredentialModel appLoginCredentialModel = new AppLoginCredentialModel();
        appLoginCredentialModel.setAccess_token(accessToken);
        AuthCredential authCredential = appLoginCredentialModel.getAuthincatedByAccessToken();
        if(authCredential.id>0 && authCredential.accessToken!=null && authCredential.accessToken!=""){

            if(!appLoginCredentialModel.isActive()){
                baseController.serviceResponse.responseStat.status = false;
                baseController.serviceResponse.responseStat.isLogin = false;
                baseController.serviceResponse.responseStat.msg = "Account is not activated";
                return baseController.getResponse();
            }
            if(appLoginCredentialModel.isBanned()){
                baseController.serviceResponse.responseStat.status = false;
                baseController.serviceResponse.responseStat.isLogin = false;
                baseController.serviceResponse.responseStat.msg = "Account is Banned";
                return baseController.getResponse();
            }

            ContactModel contactModel = new ContactModel();
            contactModel.setOwner_id(authCredential.id);
            localResponseObj.authCredential =authCredential;
            localResponseObj.contacts = contactModel.getContactByOwnerId();

            HashMap<String,Integer> countResponse =  new HashMap();

            WallPostModel wallPostModel = new WallPostModel();
            wallPostModel.setOwner_id(authCredential.id);

            System.out.println("Auth cred id :"+authCredential.id);

            countResponse.put("present",0);
            countResponse.put("wallPost",wallPostModel.getCountByOwnerId());

            // Updating Last Login Time
            appLoginCredentialModel.updateLastLogin();

            localResponseObj.extra = countResponse;
            baseController.serviceResponse.responseData =  localResponseObj;
            baseController.setAppSession(req, authCredential);
        }else{
            baseController.serviceResponse.responseStat.status = false;
            baseController.serviceResponse.responseStat.msg = "Access token is wrong";
        }

        return baseController.getResponse();
    }
    private String forgetPasword(HttpServletRequest req,HttpServletResponse res){

        ImageTalkBaseController baseController = new ImageTalkBaseController();
        AdminLoginModel adminLoginModel = new AdminLoginModel();
        String email = req.getParameter("email");
        String password  = adminLoginModel.getPasswordByEmail(email);

        if (password==null){
            baseController.serviceResponse.responseStat.status=false;
            baseController.serviceResponse.responseStat.msg = "Email is not found in the system";
            return baseController.getResponse();

        }

        String to = email;
        String from = "cabguardpro@workspaceit.com";
        String host = "localhost";
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);
        Session session = Session.getDefaultInstance(properties);


        try{

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(to));
            message.setSubject("Forget password....??");
            message.setText("Your password : "+password);
            Transport.send(message);
            String title = "";
            String body = "";


        }catch (MessagingException mex) {
            mex.printStackTrace();

        }

        baseController.serviceResponse.responseStat.status=true;
        baseController.serviceResponse.responseStat.msg = "Email is sent to "+email;
        return baseController.getResponse();
    }
    public String adminLogout(HttpServletRequest req) {
        ImageTalkBaseController baseController = new ImageTalkBaseController();
        baseController.removeSession(req);
        baseController.serviceResponse.responseStat.status=true;
        baseController.serviceResponse.responseStat.msg = "Logout success";
        return baseController.getResponse();
    }

    public String updatePhone(HttpServletRequest req)
    {
        ImageTalkBaseController baseController  = new ImageTalkBaseController(req);

        WallPostModel wallPostModel = new WallPostModel();
        wallPostModel.setOwner_id(baseController.appCredential.id);

        baseController.serviceResponse.responseStat.status=true;
        baseController.serviceResponse.responseStat.msg = "Test success";
        return baseController.getResponse();
    }
}