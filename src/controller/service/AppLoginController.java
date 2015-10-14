package controller.service;

/**
 * Created by mi on 8/20/15.
 */

import model.AdminLoginModel;
import model.AppLoginCredentialModel;
import model.ContactModel;
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
import java.util.Properties;

public class AppLoginController extends HttpServlet {
    ImageTalkBaseController baseController;
    PrintWriter pw;
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
        this.pw=res.getWriter();

        String url = req.getRequestURI().toString();

        if(url.endsWith("/")){
            url = url.substring(0, url.length()-1);
        }

        switch (url) {

            case "/app/login/authenticate/accesstoken":
                this.loginByAccessToken(req, res);
                break;
            case "/app/login/sendpassword":
                this.forgetPasword(req, res);
                break;
            case "/app/login/test_session":
                this.testSession(req, res);
                break;
            default:
                break;
        }
        this.pw.close();


    }

    private void testSession(HttpServletRequest req,HttpServletResponse res)
        throws ServletException,IOException{
        Login login = new Login();
        HttpSession session = req.getSession();
        login = (Login)session.getAttribute("userSession");
        this.baseController.serviceResponse.responseStat.status=true;
        this.baseController.serviceResponse.responseData = login;
        this.pw.print(this.baseController.getResponse());

    }


    private void loginByAccessToken(HttpServletRequest req,HttpServletResponse res)
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
                this.pw.print(this.baseController.getResponse());
                return;
            }
            if(appLoginCredentialModel.isBanned()){
                this.baseController.serviceResponse.responseStat.status = false;
                this.baseController.serviceResponse.responseStat.isLogin = false;
                this.baseController.serviceResponse.responseStat.msg = "Account is Banned";
                this.pw.print(this.baseController.getResponse());
                return;
            }

            ContactModel contactModel = new ContactModel();
            contactModel.setOwner_id(authCredential.id);
            this.localResponseObj.authCredential =authCredential;
            this.localResponseObj.contacts = contactModel.getContactByOwnerId();

            this.baseController.serviceResponse.responseData =  this.localResponseObj;
            this.baseController.setAppSession(req, authCredential);
        }else{
            this.baseController.serviceResponse.responseStat.status = false;
            this.baseController.serviceResponse.responseStat.msg = "Access token is wrong";
        }

        this.pw.print(this.baseController.getResponse());
    }
    private void forgetPasword(HttpServletRequest req,HttpServletResponse res){

        AdminLoginModel adminLoginModel = new AdminLoginModel();
        String email = req.getParameter("email");
        String password  = adminLoginModel.getPasswordByEmail(email);

        if (password==null){
            this.baseController.serviceResponse.responseStat.status=false;
            this.baseController.serviceResponse.responseStat.msg = "Email is not found in the system";
            this.pw.print(this.baseController.getResponse());
            return;
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
        this.pw.print(this.baseController.getResponse());
        return;
    }
    public void adminLogout(HttpServletRequest req) {
        this.baseController.removeSession(req);
        this.baseController.serviceResponse.responseStat.status=true;
        this.baseController.serviceResponse.responseStat.msg = "Logout success";
        this.pw.print(this.baseController.getResponse());
        return;
    }
}