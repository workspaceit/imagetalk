package controller.service;

import com.google.gson.Gson;
import model.ChatHistoryModel;

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
 * Date: 11/3/15
 * Project Name:ImageTalk
 */
public class ChatController extends HttpServlet {
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
            case "/app/user/chat/add":
                this.addchat();
                break;
            case "/app/user/chat/remove":
                this.remvoeChat();
                break;
            case "/app/user/chat/update":
                this.updateReadStatus();
                break;
            case "/app/user/chat/search":
                this.searchChat();
                break;
            case "/app/user/chat/show":
                this.showChat();
                break;
            default:
                break;
        }
    }

    private void showChat() {

        int to;

        if(!this.baseController.checkParam("to",this.req,true))
        {
            this.baseController.serviceResponse.responseStat.msg = "receiver id is required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        else
        {
            try{
                to = Integer.parseInt(req.getParameter("to"));
            }
            catch (Exception e)
            {
                this.baseController.serviceResponse.responseStat.msg = "receiver id must be int";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                e.printStackTrace();
                return;
            }
        }

        ChatHistoryModel chatHistoryModel = new ChatHistoryModel();
        chatHistoryModel.setFrom(this.baseController.appCredential.id);
        chatHistoryModel.setTo(to);
        if(chatHistoryModel.getChatHistory())
        {
            this.baseController.serviceResponse.responseStat.msg = "success";
            this.baseController.serviceResponse.responseStat.status = true;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        else
        {
            this.baseController.serviceResponse.responseStat.msg = "Fail";
            this.baseController.serviceResponse.responseStat.status = true;
            this.pw.print(this.baseController.getResponse());
            return;
        }
    }

    private void searchChat() {
    }

    private void updateReadStatus() {

        int read_status=0;
        int c_id;
        if(!this.baseController.checkParam("id",this.req,true))
        {
            this.baseController.serviceResponse.responseStat.msg = "Chat id is required to update";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        else
        {
            try{
                c_id = Integer.parseInt(req.getParameter("id"));
            }
            catch (Exception e)
            {
                this.baseController.serviceResponse.responseStat.msg = "chat id must be int";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                e.printStackTrace();
                return;
            }
        }
        if(this.baseController.checkParam("read_status",this.req,true))
        {
            try{
                read_status = Integer.parseInt(req.getParameter("read_status"));
            }
            catch (Exception e){
                this.baseController.serviceResponse.responseStat.msg = "read_status must be int";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                e.printStackTrace();
                return;
            }
        }

        ChatHistoryModel chatHistoryModel = new ChatHistoryModel();
        chatHistoryModel.setId(c_id);
        chatHistoryModel.setRead_status(read_status);
        if(!chatHistoryModel.updateReadStatus())
        {
            this.baseController.serviceResponse.responseStat.msg = "read_status not update ";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        this.baseController.serviceResponse.responseStat.msg = "read_status updated successfully ";
        this.baseController.serviceResponse.responseStat.status = false;
        this.pw.print(this.baseController.getResponse());
        return;
    }

    private void remvoeChat() {
    }

    private void addchat() {
        long chat_id;
        int to;
        int from;
        String chat_text;
        String extra;
        String media_path;
        int type;
        String created_date;

        if(!this.baseController.checkParam("chat_id",this.req,true))
        {
            this.baseController.serviceResponse.responseStat.msg = "chat id is required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        else {
            try {
                chat_id = Integer.parseInt(req.getParameter("chat_id"));
            }
            catch (Exception e)
            {
                this.baseController.serviceResponse.responseStat.msg = "chat sender id must be long";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                e.printStackTrace();
                return;
            }
        }


        if(!this.baseController.checkParam("to",this.req,true))
        {
            this.baseController.serviceResponse.responseStat.msg = "chat sender id is required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        else {
            try {
                to = Integer.parseInt(req.getParameter("to"));
            }
            catch (Exception e)
            {
                this.baseController.serviceResponse.responseStat.msg = "chat sender id must be int";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                e.printStackTrace();
                return;
            }
        }

        if(!this.baseController.checkParam("from",this.req,true))
        {
            this.baseController.serviceResponse.responseStat.msg = "chat receiver id is required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        else {
            try {
                from = Integer.parseInt(req.getParameter("from"));
            }
            catch (Exception e)
            {
                this.baseController.serviceResponse.responseStat.msg = "chat receiver id must be int";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                e.printStackTrace();
                return;
            }
        }

        if(this.baseController.checkParam("chat_text",this.req,true))
        {
            chat_text = req.getParameter("chat_text");
        }
        else
            chat_text = "";

        if(this.baseController.checkParam("extra",this.req,true))
        {
            extra = req.getParameter("extra");
        }
        else
            extra = "";

        if(this.baseController.checkParam("media_path",this.req,true))
        {
            media_path = req.getParameter("media_path");
        }
        else
            media_path = "";

        if(this.baseController.checkParam("type",this.req,true))
        {
            try {
                type = Integer.parseInt(req.getParameter("type"));
            }
            catch (Exception e)
            {
                this.baseController.serviceResponse.responseStat.msg = "Type should be int";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                e.printStackTrace();
                return;
            }

        }
        else
            type = 0;


        ChatHistoryModel chatHistoryModel = new ChatHistoryModel();
        chatHistoryModel.setChat_id(chat_id);
        chatHistoryModel.setTo(to);
        chatHistoryModel.setFrom(from);
        chatHistoryModel.setChat_text(chat_text);
        chatHistoryModel.setExtra(extra);
        chatHistoryModel.setMedia_path(media_path);
        chatHistoryModel.setRead_status(0);

        if(chatHistoryModel.insert()==0)
        {
            this.baseController.serviceResponse.responseStat.status = false;
            this.baseController.serviceResponse.responseStat.msg = "Internal server error";
            this.pw.print(this.baseController.getResponse());
            return;
        }
        this.baseController.serviceResponse.responseStat.msg = "Chat added successfully";
        this.baseController.serviceResponse.responseStat.status = true;
        this.pw.print(this.baseController.getResponse());
        return;


    }
}
