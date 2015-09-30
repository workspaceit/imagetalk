package controller.service;

/**
 * Created by mi on 8/20/15.
 */

import gps_socket.CabGuardServerSocket;
import gps_socket.CentralSocketController;
import model.LoginModel;
import model.UserStatusModel;
import model.datamodel.Login;
import model.datamodel.Status;

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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController extends HttpServlet {
    ImageTalkBaseController baseController;
    PrintWriter pw;
    @Override
    public void init() throws ServletException {
        super.init();
    }
    public class LoginRespose{
        Login login;
        Status status;
        public LoginRespose(){
            this.login =new Login();
            this.status = new Status();
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
            case "/login/authenticate":
                this.loginByEmailAndPassword(req,res);
                break;
            case "/login/authenticate/accesstoken":
                this.loginByAccessToken(req,res);
                break;
            case "/login/sendpassword":
                this.forgetPasword(req,res);
                break;
            case "/login/test_session":
                this.testSession(req, res);
                break;
            case "/login/admin/authenticate":
                this.adminLoginByEmailAndPassword(req, res);
                break;
            case "/login/admin/logout":
                this.adminLogout(req);
                break;
            default:
                break;
        }
        this.pw.close();


    }
    public void doGet(HttpServletRequest req,HttpServletResponse res)
            throws ServletException,IOException
    {
        int portNumber = 9091;
        PrintWriter pw = res.getWriter();
        try {
            CentralSocketController.serverSocket = new ServerSocket(portNumber);

            class ServerAuthThread extends Thread{
                public CabGuardServerSocket echoSocket;
                public  Socket socket;

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

                            Socket socket =  CentralSocketController.serverSocket.accept();
                            this.serverAuthThread = new ServerAuthThread();
                            this.serverAuthThread.socket = socket;
                            this.serverAuthThread.start();

                            u_d++;
                        } catch (IOException ex) {
                            Logger.getLogger(CentralSocketController.class.getName()).log(Level.SEVERE, null, ex);
                            try {
                                CentralSocketController.serverSocket.close();
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
        pw.println("<div>Socket started</div>");
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
    private void loginByEmailAndPassword(HttpServletRequest req,HttpServletResponse res)
            throws ServletException,IOException
    {



        String email = req.getParameter("email");
        String password = req.getParameter("password");

        LoginModel loginModel = new LoginModel();
        if(loginModel.isValidLogin(email,password)){
            UserStatusModel userStatusModel = new UserStatusModel();
            LoginRespose loginResponse = new LoginRespose();
            if(!loginModel.isActive()){
                this.baseController.serviceResponse.responseStat.status = false;
                this.baseController.serviceResponse.responseStat.msg = "Account is not activated";
                this.pw.print(this.baseController.getResponse());
                return;
            }
            loginResponse.login = loginModel.login;
            loginResponse.status = userStatusModel.getByLoginId(loginModel.login.id);
            this.baseController.serviceResponse.responseData = loginResponse;
            this.baseController.setSession(req,loginModel.login);

            System.out.println("AUTH DONE");
        }else{
            this.baseController.serviceResponse.responseStat.status = false;
            this.baseController.serviceResponse.responseStat.msg = "Email or password is wrong";
        }

        this.pw.print(this.baseController.getResponse());
    }
    private void adminLoginByEmailAndPassword(HttpServletRequest req,HttpServletResponse res)
            throws ServletException,IOException
    {



        String email = req.getParameter("email");
        String password = req.getParameter("password");

        LoginModel loginModel = new LoginModel();
        if(loginModel.isValidAdminLogin(email, password)){
            UserStatusModel userStatusModel = new UserStatusModel();
            LoginRespose loginResponse = new LoginRespose();

            loginResponse.login = loginModel.login;
            loginResponse.status = userStatusModel.getByLoginId(loginModel.login.id);
            this.baseController.serviceResponse.responseData = loginResponse;
            this.baseController.setSession(req,loginModel.login);
            this.baseController.serviceResponse.responseStat.msg = "Successfull login";
        }else{
            this.baseController.serviceResponse.responseStat.status = false;
            this.baseController.serviceResponse.responseStat.msg = "Email or password is wrong";
        }

        this.pw.print(this.baseController.getResponse());
    }
    private void loginByAccessToken(HttpServletRequest req,HttpServletResponse res)
            throws ServletException,IOException
    {

        String accessToken = req.getParameter("access_token");
        LoginModel loginModel = new LoginModel();

        if(loginModel.isValidLoginByAccessToken(accessToken)){
            UserStatusModel userStatusModel = new UserStatusModel();
            LoginRespose loginResponse = new LoginRespose();
            loginResponse.login = loginModel.login;

            if(!loginModel.isActive()){
                this.baseController.serviceResponse.responseStat.status = false;
                this.baseController.serviceResponse.responseStat.msg = "Account is not activated";
                this.pw.print(this.baseController.getResponse());
                return;
            }

            loginResponse.status = userStatusModel.getByLoginId(loginModel.login.id);
            this.baseController.serviceResponse.responseData = loginResponse;
            this.baseController.setSession(req, loginModel.login);
        }else{
            this.baseController.serviceResponse.responseStat.status = false;
            this.baseController.serviceResponse.responseStat.msg = "Access token is wrong";
        }

        this.pw.print(this.baseController.getResponse());
    }
    private void forgetPasword(HttpServletRequest req,HttpServletResponse res){

        LoginModel loginModel = new LoginModel();
        String email = req.getParameter("email");
        String password  = loginModel.getPasswordByEmail(email);

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