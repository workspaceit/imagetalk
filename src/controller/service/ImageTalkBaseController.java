package controller.service;

import com.google.gson.Gson;
import model.ImageTalkBaseModel;
import model.ImageTalkDbCon;
import model.datamodel.app.AppCredential;
import model.datamodel.app.Login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * Created by mi on 8/20/15.
 */
public class ImageTalkBaseController {
    public ServiceResponse serviceResponse = null;
    public AppCredential appCredential;
    private Gson gson            = null;



    public ImageTalkBaseController() {
        this.gson = new Gson();
        this.serviceResponse = new ServiceResponse();
        this.appCredential = new AppCredential();
    }
    public ImageTalkBaseController(HttpServletRequest req) {
        this.gson = new Gson();
        this.serviceResponse = new ServiceResponse();
        this.appCredential = new AppCredential();

        // Initilize App Credential Id
        isAppSessionValid(req);
    }
    public void closeDbConnection(){
        ImageTalkDbCon.closeConnection();
    }
    public String getResponse() {
        return this.gson.toJson(this.serviceResponse);
    }
    public void refresh() {
        this.gson = new Gson();
        this.serviceResponse = new ServiceResponse();
    }
    public Login getUserLoginFromSession(HttpServletRequest req){
        Login login = new Login();
        HttpSession session = req.getSession();
        login = (Login)session.getAttribute("userSession");
        return login;
    }
    public Login getAppCredentialFromSession(HttpServletRequest req){
        Login login = new Login();
        HttpSession session = req.getSession();
        login = (Login)session.getAttribute("userSession");
        return login;
    }
    public boolean isSessionValid(HttpServletRequest req){
        Login login = new Login();
        HttpSession session = req.getSession();
        System.out.println(session.getAttribute("userSession"));
        login = (Login)session.getAttribute("userSession");

        if(login==null){
            this.serviceResponse.responseStat.msg ="Your session expired";
            this.serviceResponse.responseStat.status =false;
            return false;
        }
        return true;
    }
    public void printRequestDetails(HttpServletRequest req){
        String url = req.getRequestURI().toString();

        System.out.println("*************************************************************");
        System.out.println("Requested Url : "+url);
        System.out.println("Client IP     : " +req.getRemoteAddr());
        Date d = new Date();
        System.out.println("Date          : " +d );
        System.out.println("*************************************************************");
        System.out.println();
        System.out.println();
    }
    public void printResponsetDetails(HttpServletRequest req,HttpServletResponse res,String jsonStr){
        String url = req.getRequestURI().toString();

        System.out.println("*************************************************************");
        System.out.println("JSON Response : "+jsonStr);
        System.out.println("Client IP     : " +req.getRemoteAddr());
        Date d = new Date();
        System.out.println("Date          : " +d );
        System.out.println("*************************************************************");
        System.out.println();
        System.out.println();
    }
    public boolean isAppSessionValid(HttpServletRequest req){

        this.printRequestDetails(req);
        HttpSession session = req.getSession();
        this.appCredential = (AppCredential)session.getAttribute("userSession");


        if(appCredential==null ||  this.appCredential.id==0){
            System.out.println("Your session expired          : "  );
            System.out.println("*************************************************************");
            System.out.println();
            System.out.println();


            this.serviceResponse.responseStat.msg ="Your session expired";
            this.serviceResponse.responseStat.status =false;
            this.serviceResponse.responseStat.isLogin = false;
            return false;
        }
        return true;
    }
    public boolean checkParam(String param,HttpServletRequest req,boolean emptyVal){
        if(emptyVal && req.getParameter(param) == null || req.getParameter(param).trim() == ""){
            return false;
        }
        if(req.getParameter(param) == null){
            return false;
        }
        return true;
    }
    public void setAdminSession(HttpServletRequest req,Login login){
        HttpSession session = req.getSession();
        session.setAttribute("userSession",login);
    }
    public void setAppSession(HttpServletRequest req,AppCredential appCredential){
        HttpSession session = req.getSession();
        session.setAttribute("userSession",appCredential);
    }
    public void removeSession(HttpServletRequest req){
        HttpSession session = req.getSession();
        session.invalidate();
    }
    public String getBaseUrl(HttpServletRequest request){
        String url = request.getRequestURL().toString();
        String baseURL = url.substring(0, url.length() - request.getRequestURI().length()) + request.getContextPath() + "/";
        return baseURL;
    }
    public boolean isAdmin(HttpServletRequest req){
        if(this.isSessionValid(req)){
            Login login = this.getUserLoginFromSession(req);
            if(login.type!=1){
                this.serviceResponse.responseStat.msg ="Your need admin privilege expired";
                this.serviceResponse.responseStat.status =false;
            }

            return (login.type == 1)?true:false;
        }
        return false;
    }
    public void setModelError(ImageTalkBaseModel.ModelError obj){
        if(obj!=null){
            this.serviceResponse.responseStat.msg  = obj.getMsg();
            this.serviceResponse.responseStat.status = false;
        }

    }
}