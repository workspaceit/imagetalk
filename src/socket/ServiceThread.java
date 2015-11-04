package socket;

import com.google.gson.Gson;
import helper.DateHelper;
import model.AppLoginCredentialModel;
import model.ChatHistoryModel;
import model.datamodel.app.AppCredential;
import model.datamodel.app.AuthCredential;
import model.datamodel.app.socket.SocketResponse;
import model.datamodel.app.socket.chat.TextChat;
import model.datamodel.app.socket.chat.TextChatStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServiceThread extends Thread {
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



                    System.out.println("================================");
                    System.out.println("From : "+this.appCredential.user.firstName+" "+this.appCredential.user.lastName);
                    System.out.println("TAG : "+this.socketResponse.responseStat.tag);
                    System.out.println("ObjStr : "+recvStr);
                    System.out.println("================================");


                    this.socketResponse = this.gson.fromJson(recvStr, SocketResponse.class);


                    switch ( this.socketResponse.responseStat.tag){
                        case "authentication":
                            this.processAuthentication(this.socketResponse.responseData);
                            break;
                        case "textchat":
                            this.processTextChat(this.socketResponse.responseData);
                            break;
                        case "textchat_status":
                            this.processTextChatStatus(this.socketResponse.responseData);
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
        System.out.println("");
        System.out.println("");
        System.out.println("");
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

            this.socketResponse.responseStat.tag = "textchat";

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
    private void saveInChatHistory(String chatId,int senderId,int receiverId,String textMsg){

        class DbOperationThread extends Thread{
            @Override
            public void run() {
                ChatHistoryModel chatHistory = new ChatHistoryModel();
                chatHistory.setChat_id(chatId);
                chatHistory.setTo(senderId);
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
    private void saveOfflineMsg(TextChat textChat){
        TextChatStatus textChatStatus = new TextChatStatus();


        this.socketResponse = new SocketResponse();
        this.socketResponse.responseStat.tag = "textchat_status";

        textChatStatus.chatId  = this.getMsgId();
        textChatStatus.isRead =false;
        textChatStatus.isOnline =false;
        textChatStatus.appCredential = this.appCredential;

        this.socketResponse.responseData = textChatStatus;

        this.sendData(this.gson.toJson(this.socketResponse));


        System.out.println("********************************************");
        System.out.println("Send text msg : to " + textChatStatus.appCredential.user.firstName);
        System.out.println("Send text Object "+this.gson.toJson(this.socketResponse));
        System.out.println(" Offline text msg and obj null");
        System.out.println("********************************************");
    }

    private void processTextChatStatus(Object dataObject){
        System.out.println("AT processTextChat");
        TextChatStatus textChatStatus = new TextChatStatus();
        try{
            String jObjStr = this.gson.toJson(dataObject);

            textChatStatus = this.gson.fromJson(jObjStr, TextChatStatus.class);
            ServiceThread contactServiceThread =  BaseSocketController.getServiceThread(textChatStatus.appCredential.id);



            if(contactServiceThread!=null){

                if(contactServiceThread.isOnline()) {
                    this.socketResponse = new SocketResponse();
                    this.socketResponse.responseStat.tag = "textchat_status";

                    textChatStatus.appCredential = this.appCredential;

                    this.socketResponse.responseData = textChatStatus;

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
            this.socketResponse.responseStat.tag = "error";
            this.socketResponse.responseStat.status = false;
            this.socketResponse.responseStat.msg = "Can not cast AuthCredential";
            this.closeConnection();

            ex.printStackTrace();
        }
        // Updating local database
        System.out.println("textChatStatus.chatId : "+textChatStatus.chatId);
        if(textChatStatus.chatId !=""){
            markAsReadInChatHistory(textChatStatus.chatId);
        }

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
            this.socketResponse.responseStat.tag = "error";
            this.socketResponse.responseStat.status = false;
            this.socketResponse.responseStat.msg = "Can not cast AuthCredential";
            this.closeConnection();

            ex.printStackTrace();
        }
        this.sendData(this.gson.toJson(this.socketResponse));

        if(!this.authintic){
            this.closeConnection();
        }
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
    private synchronized void sendData(String jsonStr){
        this.output.println(jsonStr);
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