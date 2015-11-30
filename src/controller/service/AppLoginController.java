package controller.service;

/**
 * Created by mi on 8/20/15.
 */

import model.AdminLoginModel;
import model.AppLoginCredentialModel;
import model.ContactModel;
import model.WallPostModel;
import model.datamodel.app.AppCredential;
import model.datamodel.app.AuthCredential;
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
    ImageTalkBaseController baseController;

    LocalResponseClass localResponseObj;
    class LocalResponseClass{
        AuthCredential authCredential;
        ArrayList<AppCredential> contacts;
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
        this.baseController = new ImageTalkBaseController();
        this.localResponseObj = new LocalResponseClass();
        PrintWriter pw=res.getWriter();

        String url = req.getRequestURI().toString();

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
            default:
                break;
        }
        pw.close();


    }

    private String testSession(HttpServletRequest req,HttpServletResponse res)
        throws ServletException,IOException{
        Login login = new Login();
        ImageTalkBaseController baseController = new ImageTalkBaseController();
        if(baseController.isAppSessionValid(req)){
            this.baseController.serviceResponse.responseStat.status=false;
            this.baseController.serviceResponse.responseStat.msg = "Session Expired";
            this.baseController.serviceResponse.responseStat.isLogin=false;
            return this.baseController.getResponse();
        }

        this.baseController.serviceResponse.responseStat.status=true;
        this.baseController.serviceResponse.responseData = this.baseController.appCredential;
        return this.baseController.getResponse();

    }


    private String loginByAccessToken(HttpServletRequest req,HttpServletResponse res)
            throws ServletException,IOException
    {

        String accessToken = req.getParameter("access_token");


        AppLoginCredentialModel appLoginCredentialModel = new AppLoginCredentialModel();
        appLoginCredentialModel.setAccess_token(accessToken);
        AuthCredential authCredential = appLoginCredentialModel.getAuthincatedByAccessToken();
        if(authCredential.id>0){

            if(!appLoginCredentialModel.isActive()){
                this.baseController.serviceResponse.responseStat.status = false;
                this.baseController.serviceResponse.responseStat.isLogin = false;
                this.baseController.serviceResponse.responseStat.msg = "Account is not activated";
                return this.baseController.getResponse();
            }
            if(appLoginCredentialModel.isBanned()){
                this.baseController.serviceResponse.responseStat.status = false;
                this.baseController.serviceResponse.responseStat.isLogin = false;
                this.baseController.serviceResponse.responseStat.msg = "Account is Banned";
                return this.baseController.getResponse();
            }

            ContactModel contactModel = new ContactModel();
            contactModel.setOwner_id(authCredential.id);
            this.localResponseObj.authCredential =authCredential;
            this.localResponseObj.contacts = contactModel.getContactByOwnerId();

            HashMap<String,Integer> countResponse =  new HashMap();

            WallPostModel wallPostModel = new WallPostModel();
            wallPostModel.setOwner_id(authCredential.id);

            countResponse.put("present",0);
            countResponse.put("wallPost",wallPostModel.getCountByOwnerId());

            this.localResponseObj.extra = countResponse;
            this.baseController.serviceResponse.responseData =  this.localResponseObj;
            this.baseController.setAppSession(req, authCredential);
        }else{
            this.baseController.serviceResponse.responseStat.status = false;
            this.baseController.serviceResponse.responseStat.msg = "Access token is wrong";
        }

        return this.baseController.getResponse();
    }
    private String forgetPasword(HttpServletRequest req,HttpServletResponse res){

        AdminLoginModel adminLoginModel = new AdminLoginModel();
        String email = req.getParameter("email");
        String password  = adminLoginModel.getPasswordByEmail(email);

        if (password==null){
            this.baseController.serviceResponse.responseStat.status=false;
            this.baseController.serviceResponse.responseStat.msg = "Email is not found in the system";
            return this.baseController.getResponse();

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

        this.baseController.serviceResponse.responseStat.status=true;
        this.baseController.serviceResponse.responseStat.msg = "Email is sent to "+email;
        return this.baseController.getResponse();
    }
    public String adminLogout(HttpServletRequest req) {
        this.baseController.removeSession(req);
        this.baseController.serviceResponse.responseStat.status=true;
        this.baseController.serviceResponse.responseStat.msg = "Logout success";
        return this.baseController.getResponse();
    }
}