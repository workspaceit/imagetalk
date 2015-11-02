package socket;

import com.google.gson.Gson;
import helper.DateHelper;
import model.AppLoginCredentialModel;
import model.datamodel.app.AppCredential;
import model.datamodel.app.AuthCredential;
import model.datamodel.app.Contact;
import model.datamodel.app.socket.SocketResponse;
import model.datamodel.app.socket.chat.TextChat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class ServiceThread extends Thread {
    private Socket serviceSocket;
    private boolean authintic;
    private Gson gson;
    private AppCredential appCredential;
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

        this.socketResponse = new SocketResponse();

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
                        continue;
                    }

                    System.out.println("recvStr : "+recvStr);
                    this.socketResponse = this.gson.fromJson(recvStr, SocketResponse.class);


                    switch ( this.socketResponse.responseStat.tag){
                        case "authentication":
                            this.processAuthentication(this.socketResponse.responseData);
                            break;
                        case "textchat":
                            this.processTextChat(this.socketResponse.responseData);
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

    }
    private void processTextChat(Object dataObject){
        System.out.println("AT processTextChat");
        this.socketResponse = new SocketResponse();
        try{
            String jObjStr = this.gson.toJson(dataObject);

            TextChat textChat = this.gson.fromJson(jObjStr, TextChat.class);
            ServiceThread contactServiceThread =  BaseSocketController.getServiceThread(textChat.contact.id);

            this.socketResponse.responseStat.tag = "textchat";

            if(contactServiceThread!=null){

                if(contactServiceThread.isOnline()) {
                    // Send text msg
                   // Remove when user will send actual contact;
                    textChat.contact.user.firstName = contactServiceThread.appCredential.user.firstName;
                    textChat.contact.user.lastName = contactServiceThread.appCredential.user.lastName;

                    this.socketResponse.responseData = textChat;

                    contactServiceThread.sendData(this.gson.toJson(this.socketResponse));

                    System.out.println("Send text msg : to " + textChat.contact.user.firstName);
                    System.out.println("Send text Object "+textChat.text);
                }else{
                    // Offline text msg
                    System.out.println(" Offline text msg");
                }
            }else{
                // Offline text msg


                System.out.println("Obj is null");
            }
        }catch (ClassCastException ex){
            this.socketResponse.responseStat.tag = "error";
            this.socketResponse.responseStat.status = false;
            this.socketResponse.responseStat.msg = "Can not cast AuthCredential";
            this.closeConnection();

            ex.printStackTrace();
        }
    }
    private void processAuthentication(Object dataObject){

        this.socketResponse = new SocketResponse();
        try{
            String jObjStr = this.gson.toJson(dataObject);

            AuthCredential authCredential = this.gson.fromJson(jObjStr,AuthCredential.class);
            this.authintic = authenticate(authCredential.accessToken);

            if(this.authintic){
                this.socketResponse.responseStat.tag = "authentication_status";
                this.socketResponse.responseStat.msg = "authentication success";
                System.out.println("Putting obj in :"+authCredential.id);
                BaseSocketController.putServiceThread(authCredential.id,this);
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
        this.appCredential = (AppCredential)authCredential;
        System.out.println(this.appCredential.phoneNumber);
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
    private synchronized void closeConnection(){
        try {
            this.output.close();
            this.input.close();
            this.serviceSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}