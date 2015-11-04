package socket;

import com.google.gson.Gson;
import helper.DateHelper;
import helper.ImageHelper;
import model.AppLoginCredentialModel;
import model.ChatHistoryModel;
import model.datamodel.app.AppCredential;
import model.datamodel.app.AuthCredential;
import model.datamodel.app.socket.SocketResponse;
import model.datamodel.app.socket.chat.ChatPhoto;
import model.datamodel.app.socket.chat.ChatTransferStatus;
import model.datamodel.app.socket.chat.TextChat;
import model.datamodel.photo.Pictures;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServiceThread extends Thread {
    final static String tag_authentication = "authentication";
    final static String tag_textChat = "textchat";
    final static String tag_ChatAcknowledgement = "chat_acknowledgement";
    final static String tag_chatPhoto = "chatphoto_transfer";
    final static String tag_chatVideo = "chatvideo_transfer";

    private Socket serviceSocket;
    private boolean authintic;
    private Gson gson;
    private AppCredential appCredential;
    private String id;
    BufferedReader input;
    PrintStream output;
    SocketResponse socketResponse;

    public ServiceThread(Socket serviceSocket ) {
        super();

        this.appCredential = new AppCredential();
        this.serviceSocket = serviceSocket;
        this.authintic = false;
        this.gson = new Gson();

        this.input = null;
        this.output = null;

        this.id = "";
        this.socketResponse = new SocketResponse();

    }
    public String getMsgId(){

        return new SimpleDateFormat("yyyyMMddHHmmssSSSSSS").format(new Date());
    }
    @Override
    public void run() {
        try {
            this.input = new BufferedReader(new InputStreamReader(this.serviceSocket.getInputStream()));
            this.output =  new PrintStream(this.serviceSocket.getOutputStream());
            int count = 1;



            while (!this.serviceSocket.isClosed()) {

                try{
                    String recvStr = input.readLine();
                    if(recvStr==null || recvStr==""){
                        this.closeConnection();

                        System.out.println("Connection closed");

                        continue;
                    }
                    // Initializing with new obj
                    this.socketResponse = this.gson.fromJson(recvStr, SocketResponse.class);

                    System.out.println("================================");
                    System.out.println("From : "+this.appCredential.user.firstName+" "+this.appCredential.user.lastName);
                    System.out.println("TAG : "+this.socketResponse.responseStat.tag);
                    // System.out.println("ObjStr : "+recvStr);
                    System.out.println("================================");


                    switch ( this.socketResponse.responseStat.tag){
                        case tag_authentication:
                            this.processAuthentication(this.socketResponse.responseData);
                            break;
                        case tag_textChat:
                            this.processTextChat(this.socketResponse.responseData);
                            break;
                        case tag_chatPhoto:
                            this.processChatPhotoTransfer(this.socketResponse.responseData);
                            break;
                        case tag_chatVideo:
                            this.processChatVideoTransfer(this.socketResponse.responseData);
                            break;
                        case tag_ChatAcknowledgement:
                            this.processChatAcknowledgement(this.socketResponse.responseData);
                            break;
                        default:
                            break;
                    }


                } catch (IOException e) {
                    output.close();
                    input.close();
                    this.serviceSocket.close();
                    e.printStackTrace();
                    continue;
                }

               // this.sendData(this.gson.toJson(this.socketResponse));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("");
        System.out.println("");
        System.out.println("");
    }
    private void processAuthentication(Object dataObject){

        this.socketResponse = new SocketResponse();
        try{
            String jObjStr = this.gson.toJson(dataObject);

            AuthCredential authCredential = this.gson.fromJson(jObjStr, AuthCredential.class);
            this.authintic = authenticate(authCredential.accessToken);

            if(this.authintic){
                this.appCredential = castAuthToAppCredential(authCredential);
                this.socketResponse.responseStat.tag = "authentication_status";
                this.socketResponse.responseStat.msg = "authentication success";
                System.out.println("Putting obj in :" + authCredential.id);
                BaseSocketController.putServiceThread(authCredential.id, this);
                ChatHistoryModel chatHistoryModel = new ChatHistoryModel();
                chatHistoryModel.setFrom(this.appCredential.id);

            }else{
                this.socketResponse.responseStat.tag = "authentication_status";
                this.socketResponse.responseStat.status = false;
                this.socketResponse.responseStat.msg = "Access token is wrong";
            }
        }catch (ClassCastException ex){
            sendError("0","Can not cast AuthCredential");
            this.closeConnection();

            ex.printStackTrace();
        }
        this.sendData(this.gson.toJson(this.socketResponse));

        if(!this.authintic){
            this.closeConnection();
        }
    }
    private void processTextChat(Object dataObject){
        if(!this.checkForAuthentication()){
            return;
        }
        this.socketResponse = new SocketResponse();
        try{
            String jObjStr = this.gson.toJson(dataObject);

            TextChat textChat = this.gson.fromJson(jObjStr, TextChat.class);
            ServiceThread contactServiceThread =  BaseSocketController.getServiceThread(textChat.appCredential.id);

            this.socketResponse.responseStat.tag = tag_textChat;

            String chatId = this.getMsgId();
            int senderId = this.appCredential.id;
            int receiverId = textChat.appCredential.id;
            textChat.text = validateChatTextMsg( textChat.text);
            String textMsg = textChat.text;


            if(contactServiceThread!=null){

                if(contactServiceThread.isOnline()) {
                    // Send text msg
                    textChat.chatId = chatId;
                    textChat.appCredential = this.appCredential;
                    this.socketResponse.responseData = textChat;

                    contactServiceThread.sendData(this.gson.toJson(this.socketResponse));


                    System.out.println("================================");
                    System.out.println("Send text msg : to " + contactServiceThread.appCredential.user.firstName+" " + contactServiceThread.appCredential.user.lastName);
                    System.out.println("Send text Object "+this.gson.toJson(this.socketResponse));
                    System.out.println("================================");


                }else{
                    saveOfflineMsg(textChat);
                }
            }else{
                // Offline text msg
                saveOfflineMsg(textChat);
            }
            // Saving to database
            saveInChatHistory(chatId,senderId,receiverId,textMsg);

        }catch (ClassCastException ex){
            this.socketResponse.responseStat.tag = "error";
            this.socketResponse.responseStat.status = false;
            this.socketResponse.responseStat.msg = "Can not cast AuthCredential";
            this.closeConnection();

            ex.printStackTrace();
        }

    }
    private void processChatPhotoTransfer(Object dataObject){
        if(!this.checkForAuthentication()){
            return;
        }
        this.socketResponse = new SocketResponse();
        String chatId = this.getMsgId();

        try{
            String jObjStr = this.gson.toJson(dataObject);

            ChatPhoto chatPhoto = this.gson.fromJson(jObjStr, ChatPhoto.class);
            ServiceThread contactServiceThread =  BaseSocketController.getServiceThread(chatPhoto.appCredential.id);

            this.socketResponse.responseStat.tag = tag_chatPhoto;


            int senderId = this.appCredential.id;
            int receiverId = chatPhoto.appCredential.id;
            chatPhoto.caption = validateChatTextMsg( chatPhoto.caption);
            String textMsg = chatPhoto.caption;


            if(contactServiceThread!=null){

                if(contactServiceThread.isOnline()) {
                    // Send text msg
                    chatPhoto.chatId = chatId;
                    chatPhoto.appCredential = this.appCredential;
                    this.socketResponse.responseData = chatPhoto;

                    contactServiceThread.sendData(this.gson.toJson(this.socketResponse));


                    System.out.println("================================");
                    System.out.println("Send photo msg : to " + contactServiceThread.appCredential.user.firstName+" " + contactServiceThread.appCredential.user.lastName);
                    //System.out.println("Send text Object "+this.gson.toJson(this.socketResponse));
                    System.out.println("================================");


                }else{
                  //  saveOfflineMsg(textChat);
                    sendChatAcknowledgement(chatId);
                }
            }else{
                // Offline text msg
             //   saveOfflineMsg(textChat);
                sendChatAcknowledgement(chatId);
            }
            // Saving to database
            savePhotoInChatHistory(chatId, senderId, receiverId, textMsg, chatPhoto.base64Img);
        }catch (ClassCastException ex){
            sendError(chatId, "Unable to cast the object");
            ex.printStackTrace();
        }
    }
    private void processChatVideoTransfer(Object dataObject){
        if(!this.checkForAuthentication()){
            return;
        }
        this.socketResponse = new SocketResponse();
        String chatId = this.getMsgId();

        try{
            String jObjStr = this.gson.toJson(dataObject);

            ChatPhoto chatPhoto = this.gson.fromJson(jObjStr, ChatPhoto.class);
            ServiceThread contactServiceThread =  BaseSocketController.getServiceThread(chatPhoto.appCredential.id);

            this.socketResponse.responseStat.tag = tag_chatPhoto;


            int senderId = this.appCredential.id;
            int receiverId = chatPhoto.appCredential.id;
            chatPhoto.caption = validateChatTextMsg( chatPhoto.caption);
            String textMsg = chatPhoto.caption;


            if(contactServiceThread!=null){

                if(contactServiceThread.isOnline()) {
                    // Send text msg
                    chatPhoto.chatId = chatId;
                    chatPhoto.appCredential = this.appCredential;
                    this.socketResponse.responseData = chatPhoto;

                    contactServiceThread.sendData(this.gson.toJson(this.socketResponse));


                    System.out.println("================================");
                    System.out.println("Send video : to " + contactServiceThread.appCredential.user.firstName+" " + contactServiceThread.appCredential.user.lastName);
                    //System.out.println("Send text Object "+this.gson.toJson(this.socketResponse));
                    System.out.println("================================");


                }else{
                    //  saveOfflineMsg(textChat);
                    sendChatAcknowledgement(chatId);
                }
            }else{
                // Offline text msg
                //   saveOfflineMsg(textChat);
                sendChatAcknowledgement(chatId);
            }
            // Saving to database
            saveVideoInChatHistory(chatId, senderId, receiverId, textMsg, chatPhoto.base64Img);
        }catch (ClassCastException ex){
            sendError(chatId,"Unable to cast the object");
            ex.printStackTrace();
        }
    }
    private void processChatAcknowledgement(Object dataObject) {

        ChatTransferStatus chatTransferStatus = new ChatTransferStatus();
        try {
            String jObjStr = this.gson.toJson(dataObject);

            chatTransferStatus = this.gson.fromJson(jObjStr, ChatTransferStatus.class);
            ServiceThread contactServiceThread =  BaseSocketController.getServiceThread(chatTransferStatus.appCredential.id);



            if(contactServiceThread!=null){

                if(contactServiceThread.isOnline()) {
                    this.socketResponse = new SocketResponse();
                    this.socketResponse.responseStat.tag = tag_ChatAcknowledgement;

                    chatTransferStatus.appCredential = this.appCredential;
                    this.socketResponse.responseData = chatTransferStatus;
                    contactServiceThread.sendData(this.gson.toJson(this.socketResponse));


                    System.out.println("********************************************");
                    System.out.println("Send text msg  to : " + contactServiceThread.appCredential.user.firstName);
                    System.out.println("Send text Object "+this.gson.toJson(this.socketResponse));
                    System.out.println("********************************************");
                }else{
                    // When it goes offline
                }
            }else{
                // Offline text msg status
                System.out.println("Obj is null");
            }
        }catch (ClassCastException ex){
            this.sendError(chatTransferStatus.chatId,"Internal error");

            ex.printStackTrace();
        }
        // Updating local database
        System.out.println("chatTransferStatus.chatId : "+ chatTransferStatus.chatId);
        if(chatTransferStatus.chatId !=""){
            markAsReadInChatHistory(chatTransferStatus.chatId);
        }

    }

    private void savePhotoInChatHistory(String chatId,int senderId,int receiverId,String textMsg,String imgBase64Str){

        int uId = this.appCredential.id;
        class DbOperationThread extends Thread{
            @Override
            public void run() {
                Pictures pictures = ImageHelper.saveChatPicture(imgBase64Str, uId);

                ChatHistoryModel chatHistory = new ChatHistoryModel();
                chatHistory.setChat_id(chatId);
                chatHistory.setTo(senderId);
                chatHistory.setType(ChatHistoryModel.type_chatPic);
                chatHistory.setMedia_path(gson.toJson(pictures));
                chatHistory.setFrom(receiverId);
                chatHistory.setChat_text(textMsg);
                chatHistory.insert();
            }
        };
        new DbOperationThread().start();
    }
    private void saveVideoInChatHistory(String chatId,int senderId,int receiverId,String textMsg,String byteStr){

        int uId = this.appCredential.id;
        class DbOperationThread extends Thread{
            @Override
            public void run() {
                byte[] b = gson.fromJson(byteStr,byte[].class);

                try {
                    Pictures videos = ImageHelper.saveByteToChatVideo(b, uId);
                    ChatHistoryModel chatHistoryModel = new ChatHistoryModel();
                    chatHistoryModel.setChat_id(chatId);
                    chatHistoryModel.setTo(senderId);
                    chatHistoryModel.setType(ChatHistoryModel.type_chatVideo);
                    chatHistoryModel.setMedia_path(gson.toJson(videos));
                    chatHistoryModel.setFrom(receiverId);
                    chatHistoryModel.setChat_text(textMsg);
                    chatHistoryModel.insert();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        };
        new DbOperationThread().start();
    }
    private void saveOfflineMsg(TextChat textChat){
        ChatTransferStatus chatTransferStatus = new ChatTransferStatus();


        this.socketResponse = new SocketResponse();
        this.socketResponse.responseStat.tag = tag_ChatAcknowledgement;

        chatTransferStatus.chatId  = this.getMsgId();
        chatTransferStatus.isRead =false;
        chatTransferStatus.isOnline =false;
        chatTransferStatus.appCredential = this.appCredential;

        this.socketResponse.responseData = chatTransferStatus;

        this.sendData(this.gson.toJson(this.socketResponse));


        System.out.println("********************************************");
        System.out.println("Send text msg : to " + chatTransferStatus.appCredential.user.firstName);
        System.out.println("Send text Object " + this.gson.toJson(this.socketResponse));
        System.out.println(" Offline text msg and obj null");
        System.out.println("********************************************");
    }
    private void saveInChatHistory(String chatId,int senderId,int receiverId,String textMsg){

        class DbOperationThread extends Thread{
            @Override
            public void run() {
                ChatHistoryModel chatHistory = new ChatHistoryModel();
                chatHistory.setChat_id(chatId);
                chatHistory.setTo(senderId);
                chatHistory.setType(ChatHistoryModel.type_txtChat);
                chatHistory.setFrom(receiverId);
                chatHistory.setChat_text(textMsg);
                chatHistory.insert();
            }
        };
        new DbOperationThread().start();
    }

    private void markAsReadInChatHistory(String chatId){

        class DbOperationThread extends Thread{
            @Override
            public void run() {
                ChatHistoryModel chatHistory = new ChatHistoryModel();
                chatHistory.setChat_id(chatId);
                chatHistory.updateReadStatusBychatId();
            }
        };
        new DbOperationThread().start();
    }
    private String validateChatTextMsg(String msg){
        String tempMsg = msg.trim();

        return tempMsg;
    }

    private void sendChatAcknowledgement(String chatId){
        ChatTransferStatus chatTransferStatus = new ChatTransferStatus();


        this.socketResponse = new SocketResponse();
        this.socketResponse.responseStat.tag = tag_ChatAcknowledgement;

        chatTransferStatus.chatId  = chatId;
        chatTransferStatus.isRead =false;
        chatTransferStatus.isOnline =false;
        chatTransferStatus.appCredential = this.appCredential;

        this.socketResponse.responseData = chatTransferStatus;

        this.sendData(this.gson.toJson(this.socketResponse));


        System.out.println("********************************************");
        System.out.println("Send text msg : to " + chatTransferStatus.appCredential.user.firstName);
        System.out.println("Send text Object " + this.gson.toJson(this.socketResponse));
        System.out.println(" Offline text msg and obj null");
        System.out.println("********************************************");
    }
    private void sendError(String chatId,String msg){
        this.socketResponse.responseStat.tag = "error";
        this.socketResponse.responseStat.status = false;
        this.socketResponse.responseStat.chatId = chatId;
        this.socketResponse.responseStat.msg = "Can not cast AuthCredential";

        this.sendData(this.gson.toJson(this.socketResponse));

    }
    public synchronized boolean sendAcknowledgement(){
        SocketResponse socketReponse = new SocketResponse();

        socketReponse.responseStat.tag = "acknowledgement";
        TextChat textChat = new TextChat();

        textChat.id = this.appCredential.id;
        textChat.text ="online";
        textChat.createdDate = DateHelper.getUtcDateProcessedTimeStamp();

        socketReponse.responseData = textChat;

        this.output.println(this.gson.toJson(socketReponse));
        return false;
    }
    public synchronized boolean sendTextMessage(String text){
        SocketResponse socketResponse = new SocketResponse();

        socketResponse.responseStat.tag = "text_chat";
        TextChat textChat = new TextChat();

        textChat.id = this.appCredential.id;
        textChat.text =text;
        textChat.createdDate = DateHelper.getUtcDateProcessedTimeStamp();

        socketResponse.responseData = textChat;

        this.output.println(this.gson.toJson(socketResponse));

        return false;
    }
    private synchronized void sendData(String jsonStr){
        this.output.println(jsonStr);
    }

    private AppCredential castAuthToAppCredential(AuthCredential authCredential){
        AppCredential appCredential = new AppCredential();

        appCredential.id = authCredential.id;
        appCredential.phoneNumber = authCredential.phoneNumber;
        appCredential.textStatus = authCredential.textStatus;
        appCredential.createdDate = authCredential.createdDate;
        appCredential.job = authCredential.job;
        appCredential.user = authCredential.user;


        return appCredential;

    }
    public boolean authenticate(String accessToken){

        AppLoginCredentialModel appLoginCredentialModel = new AppLoginCredentialModel();
        appLoginCredentialModel.setAccess_token(accessToken);
        AuthCredential authCredential = appLoginCredentialModel.getAuthincatedByAccessToken();

        if(authCredential.id>0) {
            if(!appLoginCredentialModel.isActive()){
                return false;
            }
            if(appLoginCredentialModel.isBanned()){
                return false;
            }
        }else{
            return false;
        }

        return true;
    }
    public boolean isOnline(){
       return !this.serviceSocket.isClosed();
    }
    private boolean checkForAuthentication(){
        if(!this.authintic){
            this.socketResponse.responseStat.tag = "error";
            this.socketResponse.responseStat.status = false;
            this.socketResponse.responseStat.msg = "Can not cast AuthCredential";
            this.sendData(this.gson.toJson(this.socketResponse));
            this.closeConnection();
            return false;
        }
        return true;
    }
    private synchronized void closeConnection(){
        try {
            if(this.appCredential.id>0){
                BaseSocketController.removeServiceThread(this.appCredential.id);
                System.out.println("--------------------------------------------");
                System.out.println("Closed connection of : " + this.appCredential.user.firstName+" "+this.appCredential.user.lastName);
                System.out.println("--------------------------------------------");
            }
            this.output.close();
            this.input.close();
            this.serviceSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}