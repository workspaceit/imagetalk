package controller.service;

import com.google.gson.Gson;
import model.NotificationModel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: rajib
 * Email: rajibcse2k10@gmail.com
 * Date: 11/4/15
 * Project Name:ImageTalk
 */
public class NotificationController extends HttpServlet {
/*    ImageTalkBaseController baseController;
    PrintWriter pw;
    HttpServletRequest req;
    HttpServletResponse res;*/
    Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        ImageTalkBaseController baseController = new ImageTalkBaseController();
        res.setContentType("application/json");
        baseController = new ImageTalkBaseController();
        PrintWriter pw = res.getWriter();

        this.gson = new Gson();

        String url = req.getRequestURI().toString();

        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        if(!baseController.isAppSessionValid(req)){
            pw.print(baseController.getResponse());
            pw.close();
            return;
        }
        
        switch (url)
        {
            case "/app/user/notification/add":
                pw.print(this.addNotifications(req));
                break;
            default:break;
        }

    }

    private String addNotifications(HttpServletRequest req) {
        ImageTalkBaseController baseController = new ImageTalkBaseController(req);
        String msg;
        String tag;
        int is_read;
        String data_object;

        if(!baseController.checkParam("msg",req,true))
        {
            baseController.serviceResponse.responseStat.msg = "Notification msg is required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }
        else
        {
            msg = req.getParameter("msg");
        }

        if(!baseController.checkParam("tag",req,true))
        {
            baseController.serviceResponse.responseStat.msg = "Notification tag is required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }
        else
        {
            tag = req.getParameter("tag");
        }

        if(baseController.checkParam("is_read",req,true))
        {
            try {
                is_read = Integer.parseInt(req.getParameter("is_read"));
            }
            catch (Exception e) {
                baseController.serviceResponse.responseStat.msg = "is_read is int";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }
        else
        {
           is_read = 0;
        }

        if(baseController.checkParam("data_object",req,true))
        {
            data_object = req.getParameter("data_object");
        }
        else
        {
            data_object = "";
        }

        NotificationModel notificationModel = new NotificationModel();
        notificationModel.setMsg(msg);
        notificationModel.setTag(tag);
        notificationModel.setIs_read(is_read);
        notificationModel.setData_object(data_object);

        if(notificationModel.insert()==0)
        {
            baseController.serviceResponse.responseStat.status = false;
            baseController.serviceResponse.responseStat.msg = "Internal server error";
            return baseController.getResponse();
        }
        baseController.serviceResponse.responseStat.msg = "Notification added successfully";
        baseController.serviceResponse.responseStat.status = true;
        return baseController.getResponse();

    }

}
