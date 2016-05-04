package controller.service;

import com.google.gson.Gson;
import model.ChatHistoryModel;
import model.datamodel.app.Chat;
import model.datamodel.app.ChatHistory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: rajib
 * Email: rajibcse2k10@gmail.com
 * Date: 11/3/15
 * Project Name:ImageTalk
 */
public class ChatController extends HttpServlet {
   /* ImageTalkBaseController baseController;
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


       /* this.req = req;
        this.res = resp;*/
        res.setContentType("application/json");
        res.setCharacterEncoding("utf8");
        ImageTalkBaseController baseController = new ImageTalkBaseController(req);
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
            case "/app/user/chat/add":
                pw.print(this.addchat(req));
                break;
            case "/app/user/chat/update":
                pw.print(this.updateReadStatus(req));
                break;
            case "/app/user/chat/show":
                pw.print(this.showChat(req));
                break;
            case "/app/user/chat/showPrevious":
                pw.print(this.showPreviousChat(req));
                break;
            case "/app/user/chat/showLatest":
                pw.print(this.showLatest(req));
                break;

            case "/app/user/chat/delete":
                pw.print(this.deleteChatMessage(req));
                break;
            case "/app/user/chat/private/photo/snapshot/confirm":
                pw.print(this.confirmTakeSnapshot(req));
                break;
            default:
                break;
        }
        pw.close();
    }

    private String showLatest(HttpServletRequest req) {

        ImageTalkBaseController baseController = new ImageTalkBaseController(req);

        ChatHistoryModel chatHistoryModel = new ChatHistoryModel();
        chatHistoryModel.setFrom(baseController.appCredential.id);
        ArrayList<ChatHistory> chatWithContactArrayList = chatHistoryModel.getChatsWithContact(baseController.appCredential.id);

        if (chatWithContactArrayList.size()==0)
        {
            baseController.serviceResponse.responseStat.msg = "NO Data received";
            baseController.serviceResponse.responseStat.status = true;
            //this.pw.print(this.baseController.getResponse());
            return baseController.getResponse();
        }

        baseController.serviceResponse.responseStat.msg = "Data received";
        baseController.serviceResponse.responseStat.status = true;
        baseController.serviceResponse.responseData = chatWithContactArrayList;
        //this.pw.print(this.baseController.getResponse());

        return baseController.getResponse();

    }


    private String showPreviousChat(HttpServletRequest req) {
        ImageTalkBaseController baseController = new ImageTalkBaseController(req);

        int to;
        int duration;

        ChatHistoryModel chatHistoryModel = new ChatHistoryModel();

        if(!baseController.checkParam("to",req,true))
        {
            baseController.serviceResponse.responseStat.msg = "receiver id is required";
            baseController.serviceResponse.responseStat.status = false;
            //this.pw.print(this.baseController.getResponse());
            return baseController.getResponse();
        }
        else
        {
            try{
                to = Integer.parseInt(req.getParameter("to"));
            }
            catch (Exception e)
            {
                baseController.serviceResponse.responseStat.msg = "receiver id must be int";
                baseController.serviceResponse.responseStat.status = false;
                //this.pw.print(this.baseController.getResponse());
                e.printStackTrace();
                return baseController.getResponse();
            }
        }

        if(!baseController.checkParam("duration",req,true))
        {
            baseController.serviceResponse.responseStat.msg = "duration for last chats is required";
            baseController.serviceResponse.responseStat.status = false;
            //this.pw.print(this.baseController.getResponse());
            return baseController.getResponse();
        }
        else
        {
            try{
                duration = Integer.parseInt(req.getParameter("duration"));
            }
            catch (Exception e)
            {
                baseController.serviceResponse.responseStat.msg = "duration must be int type";
                baseController.serviceResponse.responseStat.status = false;
                //this.pw.print(this.baseController.getResponse());
                e.printStackTrace();
                return baseController.getResponse();
            }
        }
        if(duration>5000 || duration<1)
        {
            baseController.serviceResponse.responseStat.msg = "duration can't be negative or greater than 13Years";
            baseController.serviceResponse.responseStat.status = false;
            //this.pw.print(this.baseController.getResponse());
            return baseController.getResponse();
        }

        if(baseController.checkParam("limit", req, true)) {
            try{
                chatHistoryModel.limit = Integer.parseInt(req.getParameter("limit").trim());
            }catch (Exception ex){
                System.out.println(ex);
                baseController.serviceResponse.responseStat.msg = "limit is not in valid format";
                baseController.serviceResponse.responseStat.status = false;
                //this.pw.print(this.baseController.getResponse());
                return baseController.getResponse();
            }
        }else{
            chatHistoryModel.limit = 5;
        }

        if(!baseController.checkParam("offset", req, true)){
            baseController.serviceResponse.responseStat.msg = "offset required";
            baseController.serviceResponse.responseStat.status = false;
           // this.pw.print(this.baseController.getResponse());
            return baseController.getResponse();
        }else {
            try{
                chatHistoryModel.offset = Integer.parseInt(req.getParameter("offset").trim());
            }catch (Exception ex){
                System.out.println(ex);
                baseController.serviceResponse.responseStat.msg = "offset is not in valid format";
                baseController.serviceResponse.responseStat.status = false;
                //this.pw.print(this.baseController.getResponse());
                return baseController.getResponse();
            }
        }

        chatHistoryModel.setFrom(baseController.appCredential.id);
        chatHistoryModel.setTo(to);
        ArrayList<Chat> previousChatArrayList = chatHistoryModel.getPreviousChatHistory(duration);
        if(previousChatArrayList.size()==0)
        {
            baseController.serviceResponse.responseStat.msg = "No record found!";
            baseController.serviceResponse.responseStat.status = false;
           // this.pw.print(this.baseController.getResponse());
            return baseController.getResponse();
        }

        baseController.serviceResponse.responseStat.msg = "Records Found";
        baseController.serviceResponse.responseStat.status = true;
        baseController.serviceResponse.responseData = previousChatArrayList;
        //this.pw.print(this.baseController.getResponse());
        return baseController.getResponse();

    }

    private String showChat(HttpServletRequest req) {
        ImageTalkBaseController baseController = new ImageTalkBaseController(req);

        ChatHistoryModel chatHistoryModel = new ChatHistoryModel();
        int to;

        if(!baseController.checkParam("to",req,true))
        {
            baseController.serviceResponse.responseStat.msg = "receiver id is required";
            baseController.serviceResponse.responseStat.status = false;
            //this.pw.print(this.baseController.getResponse());
            return baseController.getResponse();
        }
        else
        {
            try{
                to = Integer.parseInt(req.getParameter("to"));
            }
            catch (Exception e)
            {
                baseController.serviceResponse.responseStat.msg = "receiver id must be int";
                baseController.serviceResponse.responseStat.status = false;
               // this.pw.print(this.baseController.getResponse());
                e.printStackTrace();
                return baseController.getResponse();
            }
        }

        if(baseController.checkParam("limit", req, true)) {
            try{
                chatHistoryModel.limit = Integer.parseInt(req.getParameter("limit").trim());
            }catch (Exception ex){
                System.out.println(ex);
                baseController.serviceResponse.responseStat.msg = "limit is not in valid format";
                baseController.serviceResponse.responseStat.status = false;
                //this.pw.print(this.baseController.getResponse());
                return baseController.getResponse();
            }
        }else{
            chatHistoryModel.limit = 15;
        }

        if(!baseController.checkParam("offset", req, true)){
            baseController.serviceResponse.responseStat.msg = "offset required";
            baseController.serviceResponse.responseStat.status = false;
           // this.pw.print(this.baseController.getResponse());
            return baseController.getResponse();
        }else {
            try{
                chatHistoryModel.offset = Integer.parseInt(req.getParameter("offset").trim());
            }catch (Exception ex){
                System.out.println(ex);
                baseController.serviceResponse.responseStat.msg = "offset is not in valid format";
                baseController.serviceResponse.responseStat.status = false;
                //this.pw.print(this.baseController.getResponse());
                return baseController.getResponse();
            }
        }


        chatHistoryModel.setFrom(baseController.appCredential.id);
        chatHistoryModel.setTo(to);
        chatHistoryModel.setCurrentUserId(baseController.appCredential.id);

        ArrayList<Chat> chatArrayList = chatHistoryModel.getChatHistory();
        if(chatArrayList.size()==0)
        {
            baseController.serviceResponse.responseStat.msg = "No record found!";
            baseController.serviceResponse.responseStat.status = false;
            //this.pw.print(this.baseController.getResponse());

            return baseController.getResponse();
        }

        baseController.serviceResponse.responseStat.msg = "Records are in arraylist";
        baseController.serviceResponse.responseStat.status = true;
        baseController.serviceResponse.responseData = chatArrayList;
        System.out.println(baseController.getResponse());
        //this.pw.print(this.baseController.getResponse());

        return baseController.getResponse();

    }

    private void searchChat() {

    }

    private String updateReadStatus(HttpServletRequest req) {
        ImageTalkBaseController baseController = new ImageTalkBaseController(req);

        int read_status=0;
        int c_id;
        if(!baseController.checkParam("id",req,true))
        {
            baseController.serviceResponse.responseStat.msg = "Chat id is required to update";
            baseController.serviceResponse.responseStat.status = false;
            //this.pw.print(this.baseController.getResponse());
            return baseController.getResponse();
        }
        else
        {
            try{
                c_id = Integer.parseInt(req.getParameter("id"));
            }
            catch (Exception e)
            {
                baseController.serviceResponse.responseStat.msg = "chat id must be int";
                baseController.serviceResponse.responseStat.status = false;
                //this.pw.print(this.baseController.getResponse());
                e.printStackTrace();
                return baseController.getResponse();
            }
        }
        if(baseController.checkParam("read_status",req,true))
        {
            try{
                read_status = Integer.parseInt(req.getParameter("read_status"));
            }
            catch (Exception e){
                baseController.serviceResponse.responseStat.msg = "read_status must be int";
                baseController.serviceResponse.responseStat.status = false;
                //this.pw.print(this.baseController.getResponse());
                e.printStackTrace();
                return baseController.getResponse();
            }
        }

        ChatHistoryModel chatHistoryModel = new ChatHistoryModel();
        chatHistoryModel.setId(c_id);
        chatHistoryModel.setRead_status(read_status);
        if(!chatHistoryModel.updateReadStatus())
        {
            baseController.serviceResponse.responseStat.msg = "read_status not update ";
            baseController.serviceResponse.responseStat.status = false;
            //this.pw.print(this.baseController.getResponse());
            return baseController.getResponse();
        }
        baseController.serviceResponse.responseStat.msg = "read_status updated successfully ";
        baseController.serviceResponse.responseStat.status = false;
        return baseController.getResponse();
    }


    private String addchat(HttpServletRequest req) {
        ImageTalkBaseController baseController = new ImageTalkBaseController(req);

        String chat_id;
        int to;
        int from;
        String chat_text;
        String extra;
        String media_path;
        int type;
        String created_date;

        if(!baseController.checkParam("chat_id",req,true))
        {
            baseController.serviceResponse.responseStat.msg = "chat id is required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }
        else {
            try {
                chat_id = req.getParameter("chat_id");
            }
            catch (Exception e)
            {
                baseController.serviceResponse.responseStat.msg = "chat sender id must be long";
                baseController.serviceResponse.responseStat.status = false;
                e.printStackTrace();
                return baseController.getResponse();
            }
        }


        if(!baseController.checkParam("to",req,true))
        {
            baseController.serviceResponse.responseStat.msg = "chat sender id is required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }
        else {
            try {
                to = Integer.parseInt(req.getParameter("to"));
            }
            catch (Exception e)
            {
                baseController.serviceResponse.responseStat.msg = "chat sender id must be int";
                baseController.serviceResponse.responseStat.status = false;
                e.printStackTrace();
                return baseController.getResponse();
            }
        }

        if(!baseController.checkParam("from",req,true))
        {
            baseController.serviceResponse.responseStat.msg = "chat receiver id is required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }
        else {
            try {
                from = Integer.parseInt(req.getParameter("from"));
            }
            catch (Exception e)
            {
                baseController.serviceResponse.responseStat.msg = "chat receiver id must be int";
                baseController.serviceResponse.responseStat.status = false;
                e.printStackTrace();
                return baseController.getResponse();
            }
        }

        if(baseController.checkParam("chat_text",req,true))
        {
            chat_text = req.getParameter("chat_text");
        }
        else
            chat_text = "";

        if(baseController.checkParam("extra",req,true))
        {
            extra = req.getParameter("extra");
        }
        else
            extra = "";

        if(baseController.checkParam("media_path",req,true))
        {
            media_path = req.getParameter("media_path");
        }
        else
            media_path = "";

        if(baseController.checkParam("type",req,true))
        {
            try {
                type = Integer.parseInt(req.getParameter("type"));
            }
            catch (Exception e)
            {
                baseController.serviceResponse.responseStat.msg = "Type should be int";
                baseController.serviceResponse.responseStat.status = false;
                e.printStackTrace();
                return baseController.getResponse();
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
            baseController.serviceResponse.responseStat.status = false;
            baseController.serviceResponse.responseStat.msg = "Internal server error";
            return baseController.getResponse();
        }
        baseController.serviceResponse.responseStat.msg = "Chat added successfully";
        baseController.serviceResponse.responseStat.status = true;
        return baseController.getResponse();
    }
    private String confirmTakeSnapshot(HttpServletRequest req) {
        ImageTalkBaseController baseController = new ImageTalkBaseController(req);


        int id;
        if(!baseController.checkParam("id",req,true))
        {
            baseController.serviceResponse.responseStat.msg = "id is required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }else{
            try{

                id =  Integer.parseInt(req.getParameter("id"));
            }catch (Exception ex){
                ex.printStackTrace();
                baseController.serviceResponse.responseStat.msg = "id Int required";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }
        ChatHistoryModel chatHistory = new  ChatHistoryModel();
        chatHistory.setId(id);
        chatHistory.updateIsTakeSnapShotStatusById();
        return baseController.getResponse();
    }

    private String deleteChatMessage(HttpServletRequest req) {

        ImageTalkBaseController baseController = new ImageTalkBaseController(req);

        ChatHistoryModel chatHistoryModel = new ChatHistoryModel();

        chatHistoryModel.setCurrentUserId(baseController.appCredential.id);

        if(!baseController.checkParam("chat_id",req,true))
        {
            baseController.serviceResponse.responseStat.msg = "chat_id is required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }

        chatHistoryModel.setChat_id(req.getParameter("chat_id"));

        if(chatHistoryModel.updateDelete()){
            baseController.serviceResponse.responseStat.msg = "Successfully deleted";
            return baseController.getResponse();
        }

        baseController.serviceResponse.responseStat.status=false;
        baseController.serviceResponse.responseStat.msg = "Problem with app credential";
        return baseController.getResponse();

    }
}
