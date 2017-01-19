package controller.admin;

/**
 * Created by mi on 8/20/15.
 */

import controller.service.ImageTalkBaseController;
import helper.DateHelper;
import model.AdminLoginModel;
import model.ReportWallPostModel;
import model.WallPostModel;
import model.datamodel.admin.AdminCredential;
import model.datamodel.app.Login;
import model.datamodel.app.ReportWallPost;
import model.datamodel.app.WallPost;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class AdminWallPostReportController extends HttpServlet {

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

        PrintWriter pw = res.getWriter();

        if (!baseController.isSessionValid(req)) {
            pw.print(baseController.getResponse());
            pw.close();
            return;
        }
        if (!baseController.isAdmin(req)) {
            pw.print(baseController.getResponse());
            pw.close();
            return;
        }

        String url = req.getRequestURI().toString();

        if(url.endsWith("/")){
            url = url.substring(0, url.length()-1);
        }
        String respStr = "";
        switch (url) {
            case "/admin/report/wall-post/take-action":
                respStr = this.reportWallPostTakeAction(req, res);
                break;
            default:
                break;
        }
        pw.print(respStr);
        pw.close();


    }

    private String reportWallPostTakeAction(HttpServletRequest req,HttpServletResponse res)
            throws ServletException,IOException
    {


        ImageTalkBaseController baseController = new ImageTalkBaseController();
        Login login = baseController.getUserLoginFromSession(req);
        AdminCredential adminCredential = new AdminLoginModel().getAdminCredentialById(login.id);
        int id = 0;

        if(req.getParameter("id")==null || req.getParameter("id")==""){
           baseController.serviceResponse.responseStat.status = false;
           baseController.serviceResponse.responseStat.msg = "Report wall post id required";
            return baseController.getResponse();
        }
        try {
            id = Integer.parseInt(req.getParameter("id"));
        }catch (NumberFormatException ex){
           baseController.serviceResponse.responseStat.status = false;
           baseController.serviceResponse.responseStat.msg = "Report wall post id int required";
            return baseController.getResponse();
        }

        if(req.getParameter("action_type")==null || req.getParameter("action_type")==""){
            baseController.serviceResponse.responseStat.status = false;
            baseController.serviceResponse.responseStat.msg = "Action type required";
            return baseController.getResponse();
        }

        String actionType =  req.getParameter("action_type").trim();

        ReportWallPostModel reportWallPostModel = new ReportWallPostModel();
        ReportWallPost reportWallPost = reportWallPostModel.getById(id);
        WallPostModel wallPostModel = new WallPostModel();

        reportWallPost.actionTakenBy = adminCredential;

        if(reportWallPost.id==0){
           baseController.serviceResponse.responseStat.status = false;
           baseController.serviceResponse.responseStat.msg = "Report wall post not found";
           return baseController.getResponse();
        }
        if(actionType.equals("_block")){
            wallPostModel.updateIsBlockedTrue(reportWallPost.wallPost);
        }else if(actionType.equals("_allow")){
            wallPostModel.updateIsBlockedFalse(reportWallPost.wallPost);
        }

        reportWallPost.actionTakenAt = DateHelper.getCurrentUtcTimeStamp();
        reportWallPost.actionTaken = true;
        reportWallPostModel.updateByWalpostId(reportWallPost);

        baseController.serviceResponse.responseStat.status = true;
        return baseController.getResponse();

    }

}