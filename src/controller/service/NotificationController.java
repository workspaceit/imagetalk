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
    ImageTalkBaseController baseController;
    PrintWriter pw;
    HttpServletRequest req;
    HttpServletResponse res;
    Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.req = req;
        this.res = resp;
        res.setContentType("application/json");
        this.baseController = new ImageTalkBaseController();
        this.pw = res.getWriter();

        this.gson = new Gson();

        String url = req.getRequestURI().toString();

        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        if(!this.baseController.isAppSessionValid(this.req)){
            this.pw.print(this.baseController.getResponse());
            this.pw.close();
            return;
        }
        
        switch (url)
        {
            case "/app/user/notification/add":
                this.addNotifications();
                break;
            default:break;
        }

    }

    private void addNotifications() {
        String msg;
        String tag;
        int is_read;
        String data_object;

        if(!this.baseController.checkParam("msg",this.req,true))
        {
            this.baseController.serviceResponse.responseStat.msg = "Notification msg is required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        else
        {
            msg = req.getParameter("msg");
        }

        if(!this.baseController.checkParam("tag",this.req,true))
        {
            this.baseController.serviceResponse.responseStat.msg = "Notification tag is required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        else
        {
            tag = req.getParameter("tag");
        }

        if(this.baseController.checkParam("is_read",this.req,true))
        {
            try {
                is_read = Integer.parseInt(req.getParameter("is_read"));
            }
            catch (Exception e) {
                this.baseController.serviceResponse.responseStat.msg = "is_read is int";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }
        else
        {
           is_read = 0;
        }

        if(this.baseController.checkParam("data_object",this.req,true))
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
            this.baseController.serviceResponse.responseStat.status = false;
            this.baseController.serviceResponse.responseStat.msg = "Internal server error";
            this.pw.print(this.baseController.getResponse());
            return;
        }
        this.baseController.serviceResponse.responseStat.msg = "Notification added successfully";
        this.baseController.serviceResponse.responseStat.status = true;
        this.pw.print(this.baseController.getResponse());
        return;



    }

}
