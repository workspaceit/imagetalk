package socket;

import com.google.gson.Gson;
import helper.DateHelper;
import helper.ImageHelper;
import model.AppLoginCredentialModel;
import model.ChatHistoryModel;
import model.ContactModel;
import model.datamodel.app.AppCredential;
import model.datamodel.app.AuthCredential;
import model.datamodel.app.Contact;
import model.datamodel.app.Places;
import model.datamodel.app.socket.Acknowledgement;
import model.datamodel.app.socket.SocketResponse;
import model.datamodel.app.socket.chat.*;
import model.datamodel.app.video.Videos;
import model.datamodel.photo.Pictures;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ServiceThread extends Thread {
    final static String tag_authentication = "authentication";
    final static String tag_textChat = "textchat";
    final static String tag_chatPhoto = "chatphoto_transfer";
    final static String tag_chatLocation = "chatlocation_share";
    final static String tag_ChatContactShare = "chatcontact_share";
    final static String tag_chatPrivatePhoto = "chatprivatephoto_transfer";
    final static String tag_chatPrivatePhotoTookSnapshot = "chat_private_photo_took_snapshot";
    final static String tag_chatPrivatePhotoTookSnapshotStartCountDown = "chat_private_photo_transfer_start_countdown";
    final static String tag_chatVideo = "chatvideo_transfer";
    final static String tag_ContactOnline = "contact_online";
    final static String tag_ChatAcknowledgement = "chat_acknowledgement";
    final static String tag_ChatPrivatePhotoAcknowledgement = "chat_private_photo_destroy_acknowledgement";
    final static String tag_ChatPrivatePhotoCountDownAcknowledgement = "chat_private_photo_countdown_acknowledgement";
    final static String tag_ChatReceived = "chat_received";
    final static String tag_UserOnline = "user_online";

    private Socket serviceSocket;
    private boolean authintic;
    private Gson gson;
    private AppCredential appCredential;
    private String id;
    private BufferedReader input;
    private PrintStream output;
    private SocketResponse socketResponse;
    private InputStream fileInput;

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
            this.fileInput = this.serviceSocket.getInputStream();
            this.output =  new PrintStream(this.serviceSocket.getOutputStream());
            int count = 1;



            while (!this.serviceSocket.isClosed()) {

                try{
                    String recvStr = input.readLine();
                    if(recvStr==null || recvStr==""){
                        this.closeConnection();
                        AppLoginCredentialModel appLoginCredentialModel = new AppLoginCredentialModel();
                        appLoginCredentialModel.setId(appCredential.id);
                        appLoginCredentialModel.updateLastLogout();
                        System.out.println("Connection closed");

                        continue;
                    }
                    // Initializing with new obj
                    this.socketResponse = this.gson.fromJson(recvStr, SocketResponse.class);

                    System.out.println("================================");
                    System.out.println("From : "+this.appCredential.user.firstName+" "+this.appCredential.user.lastName);
                    System.out.println("TAG : "+this.socketResponse.responseStat.tag);
                     System.out.println("ObjStr : "+recvStr);
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
                        case tag_chatLocation:
                            this.processLocationChat(this.socketResponse.responseData);
                            break;
                        case tag_ChatContactShare:
                            this.processChatContactSharing(this.socketResponse.responseData);
                            break;
                        case tag_chatPrivatePhoto:
                            this.processChatPrivatePhotoTransfer(this.socketResponse.responseData);
                            break;
                        case tag_chatPrivatePhotoTookSnapshot:
                            this.processChatPrivateTookSnapShot(this.socketResponse.responseData);
                            break;
                        case tag_chatPrivatePhotoTookSnapshotStartCountDown:
                            this.processChatPrivatePhotoTransferStartCountDown(this.socketResponse.responseData);
                            break;
                        case tag_ChatAcknowledgement:
                            this.processChatAcknowledgement(this.socketResponse.responseData);
                            break;
                        case tag_ContactOnline:
                            this.processUserOnline(this.socketResponse.responseData);
                            break;
                        case tag_UserOnline:
                            this.processUserOnline(this.socketResponse.responseData);
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
        AppLoginCredentialModel appLoginCredentialModel = new AppLoginCredentialModel();
        appLoginCredentialModel.setId(appCredential.id);
        appLoginCredentialModel.updateLastLogout();
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
        String chatId = this.getMsgId();
        try{

            String jObjStr = this.gson.toJson(dataObject);

            TextChat textChat = this.gson.fromJson(jObjStr, TextChat.class);
            // Sending the sender Received Acknowledgement
            sendChatReceivedAcknowledgement(0, textChat.tmpChatId,chatId, false,false);

            ServiceThread contactServiceThread =  BaseSocketController.getServiceThread(textChat.appCredential.id);

            this.socketResponse.responseStat.tag = tag_textChat;
            this.socketResponse.responseStat.chatId = chatId;

            int senderId = this.appCredential.id;
            int receiverId = textChat.appCredential.id;
            textChat.text = validateChatTextMsg( textChat.text);
            String textMsg = textChat.text;

            textChat.recevice = true;
            textChat.send = false;
            textChat.chatId = chatId;
            this.socketResponse.responseData = textChat;
            // Received acknowledgement

            if(contactServiceThread!=null){

                if(contactServiceThread.isOnline()) {
                    // Send text msg
                    textChat.appCredential = this.appCredential;

                    contactServiceThread.sendData(this.gson.toJson(this.socketResponse));


                    System.out.println("================================");
                    System.out.println("Send text msg : to " + contactServiceThread.appCredential.user.firstName+" " + contactServiceThread.appCredential.user.lastName);
                    System.out.println("Send text Object "+this.gson.toJson(this.socketResponse));
                    System.out.println("================================");
                }else{
                    sendChatAcknowledgement(0,textChat.tmpChatId,chatId,false,false);
                }
            }else{
                sendChatAcknowledgement(0,textChat.tmpChatId,chatId,false,false);
            }
            // Saving to database
            saveInChatHistory(chatId, senderId, receiverId, textMsg);

        }catch (ClassCastException ex){
            sendError(chatId, "Can not cast the object");
            ex.printStackTrace();
        }

    }
    private void processChatContactSharing(Object dataObject){
        if(!this.checkForAuthentication()){
            return;
        }
        this.socketResponse = new SocketResponse();
        String chatId = this.getMsgId();
        try{

            String jObjStr = this.gson.toJson(dataObject);

            ContactShare contactShare = this.gson.fromJson(jObjStr, ContactShare.class);

            // Sending the sender Received Acknowledgement
            sendChatReceivedAcknowledgement(0, contactShare.tmpChatId,chatId, false,false);

            ServiceThread contactServiceThread =  BaseSocketController.getServiceThread(contactShare.appCredential.id);

            this.socketResponse.responseStat.tag = tag_ChatContactShare;
            this.socketResponse.responseStat.chatId = chatId;

            int senderId = this.appCredential.id;
            int receiverId = contactShare.appCredential.id;

            contactShare.recevice = true;
            contactShare.send = false;
            contactShare.chatId = chatId;
            this.socketResponse.responseData = contactShare;
            // Received acknowledgement

            if(contactServiceThread!=null){
                if(contactServiceThread.isOnline()) {
                    // Send text msg
                    contactShare.appCredential = this.appCredential;
                    contactServiceThread.sendData(this.gson.toJson(this.socketResponse));

                    System.out.println("================================");
                    System.out.println("Send text msg : to " + contactServiceThread.appCredential.user.firstName+" " + contactServiceThread.appCredential.user.lastName);
                    System.out.println("Send text Object "+this.gson.toJson(this.socketResponse));
                    System.out.println("================================");
                }else{
                    sendChatAcknowledgement(0,contactShare.tmpChatId,chatId,false,false);
                }
            }else{
                sendChatAcknowledgement(0,contactShare.tmpChatId,chatId,false,false);
            }
            // Saving to database
            saveContactShare(chatId, senderId, receiverId, contactShare);

        }catch (ClassCastException ex){
            sendError(chatId, "Can not cast the object");
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
            sendChatReceivedAcknowledgement(0, chatPhoto.tmpChatId,chatId, false,false);

            System.out.println("Recepent :"+jObjStr);
            System.out.println("chatPhoto.appCredential.id :" + jObjStr);
            ServiceThread contactServiceThread =  BaseSocketController.getServiceThread(chatPhoto.appCredential.id);

            this.socketResponse.responseStat.tag = tag_chatPhoto;
            this.socketResponse.responseStat.chatId = chatId;

            int senderId = this.appCredential.id;
            int receiverId = chatPhoto.appCredential.id;
            chatPhoto.caption = validateChatTextMsg( chatPhoto.caption);
            String textMsg = chatPhoto.caption;

            chatPhoto.recevice = true;
            chatPhoto.send = false;
            Pictures pictures = ImageHelper.saveChatPicture(chatPhoto.base64Img, senderId);
            chatPhoto.base64Img = "";
            chatPhoto.pictures = pictures;

            chatPhoto.chatId = chatId;
            this.socketResponse.responseData = chatPhoto;

            // Received ackonwledgement

            if(contactServiceThread!=null){
                if(contactServiceThread.isOnline()) {

                    // Send text msg
                    chatPhoto.appCredential = this.appCredential;
                    contactServiceThread.sendData(this.gson.toJson(this.socketResponse));


                    System.out.println("================================");
                    System.out.println("Send photo msg : to " + contactServiceThread.appCredential.user.firstName+" " + contactServiceThread.appCredential.user.lastName);
                    System.out.println("Send text Object "+this.gson.toJson(this.socketResponse));
                    System.out.println("================================");

                    sendChatAcknowledgement(0, chatPhoto.tmpChatId, chatId, false, true);
                }else{
                    sendChatAcknowledgement(0,chatPhoto.tmpChatId,chatId,false,false);
                }
            }else{
                sendChatAcknowledgement(0,chatPhoto.tmpChatId,chatId,false,false);
            }
            // Saving to database
            savePhotoInChatHistory(chatId, senderId, receiverId, textMsg, pictures);
        }catch (ClassCastException ex){
            sendError(chatId,"Can not cast the object");
            ex.printStackTrace();
        }

    }
    private void processChatPrivatePhotoTransfer(Object dataObject){
        if(!this.checkForAuthentication()){
            return;
        }
        this.socketResponse = new SocketResponse();
        String chatId = this.getMsgId();



        try{
            String jObjStr = this.gson.toJson(dataObject);
            PrivateChatPhoto privateChatPhoto = this.gson.fromJson(jObjStr, PrivateChatPhoto.class);
            // Sending the sender Received Acknowledgement
            sendChatReceivedAcknowledgement(0, privateChatPhoto.tmpChatId,chatId, false,false);

            System.out.println("Recepent :"+jObjStr);
            System.out.println("chatPhoto.appCredential.id :" + jObjStr);
            ServiceThread contactServiceThread =  BaseSocketController.getServiceThread(privateChatPhoto.appCredential.id);

            this.socketResponse.responseStat.tag = tag_chatPrivatePhoto;
            this.socketResponse.responseStat.chatId = chatId;

            int senderId = this.appCredential.id;
            int receiverId = privateChatPhoto.appCredential.id;
            privateChatPhoto.caption = validateChatTextMsg( privateChatPhoto.caption);
            String textMsg = privateChatPhoto.caption;

            privateChatPhoto.recevice = true;
            privateChatPhoto.send = false;
            Pictures pictures = ImageHelper.saveChatPrivatePicture(privateChatPhoto.base64Img, senderId);
            privateChatPhoto.base64Img = "";
            privateChatPhoto.pictures = pictures;

            privateChatPhoto.chatId = chatId;
            this.socketResponse.responseData = privateChatPhoto;

            if(contactServiceThread!=null){
                if(contactServiceThread.isOnline()) {
                    // Send text msg
                    privateChatPhoto.appCredential = this.appCredential;
                    contactServiceThread.sendData(this.gson.toJson(this.socketResponse));


                    System.out.println("================================");
                    System.out.println("Send photo msg : to " + contactServiceThread.appCredential.user.firstName+" " + contactServiceThread.appCredential.user.lastName);
                    System.out.println("Send text Object "+this.gson.toJson(this.socketResponse));
                    System.out.println("================================");

                    sendChatAcknowledgement(0, privateChatPhoto.tmpChatId, chatId, false, true);
                }else{
                    sendChatAcknowledgement(0,privateChatPhoto.tmpChatId,chatId,false,false);
                }
            }else{
                sendChatAcknowledgement(0,privateChatPhoto.tmpChatId,chatId,false,false);
            }
            // Saving to database
            savePrivatePhotoInChatHistory(chatId, senderId, receiverId, textMsg, privateChatPhoto, contactServiceThread);
        }catch (ClassCastException ex){
            sendError(chatId,"Can not cast the object");
            ex.printStackTrace();
        }

    }
    private void processChatPrivatePhotoTransferStartCountDown(Object dataObject){
        if(!this.checkForAuthentication()){
            return;
        }
        this.socketResponse = new SocketResponse();
        String chatId = "";



        try{
            String jObjStr = this.gson.toJson(dataObject);
            PrivateChatPhoto privateChatPhoto = this.gson.fromJson(jObjStr, PrivateChatPhoto.class);
            chatId = privateChatPhoto.chatId;
            // Sending the sender Received Acknowledgement
           // sendChatReceivedAcknowledgement(0, privateChatPhoto.tmpChatId, chatId, false, false);

            System.out.println("Recepent :"+jObjStr);
            System.out.println("chatPhoto.appCredential.id :" + jObjStr);
            ServiceThread contactServiceThread =  BaseSocketController.getServiceThread(privateChatPhoto.appCredential.id);

            this.socketResponse.responseStat.tag = tag_chatPrivatePhotoTookSnapshotStartCountDown;
            this.socketResponse.responseStat.chatId = chatId;

            int senderId = this.appCredential.id;
            int receiverId = privateChatPhoto.appCredential.id;
            privateChatPhoto.caption = validateChatTextMsg( privateChatPhoto.caption);
            String textMsg = privateChatPhoto.caption;

            privateChatPhoto.recevice = true;
            privateChatPhoto.send = false;

            privateChatPhoto.base64Img = "";

            privateChatPhoto.chatId = chatId;
            this.socketResponse.responseData = privateChatPhoto;

            if(contactServiceThread!=null) {
                if (contactServiceThread.isOnline()) {

                    // Send text msg
                    privateChatPhoto.appCredential = this.appCredential;
                    contactServiceThread.sendData(this.gson.toJson(this.socketResponse));


                    System.out.println("================================");
                    System.out.println("Send photo msg : to " + contactServiceThread.appCredential.user.firstName + " " + contactServiceThread.appCredential.user.lastName);
                    System.out.println("Send text Object " + this.gson.toJson(this.socketResponse));
                    System.out.println("================================");
                }
            }

            destroyPrivatePhotoUsingTimer(privateChatPhoto, chatId, contactServiceThread);
        }catch (ClassCastException ex){
            sendError(chatId,"Can not cast the object");
            ex.printStackTrace();
        }


    }
    private void processChatPrivateTookSnapShot(Object dataObject){
//        chatHistory.setId(id);
//        chatHistory.updateIsTakeSnapShotStatusById();

        if(!this.checkForAuthentication()){
            return;
        }
        this.socketResponse = new SocketResponse();
        String chatId = this.getMsgId();



        try{
            String jObjStr = this.gson.toJson(dataObject);
            PrivateChatPhoto privateChatPhoto = this.gson.fromJson(jObjStr, PrivateChatPhoto.class);

            // Sending the sender Received Acknowledgement
            sendChatReceivedAcknowledgement(0, privateChatPhoto.tmpChatId, chatId, false, false);


            ServiceThread contactServiceThread =  BaseSocketController.getServiceThread(privateChatPhoto.appCredential.id);

            this.socketResponse.responseStat.tag = tag_chatPrivatePhotoTookSnapshot;
            this.socketResponse.responseStat.chatId = chatId;

            int senderId = this.appCredential.id;
            int receiverId = privateChatPhoto.appCredential.id;
            privateChatPhoto.caption = validateChatTextMsg( privateChatPhoto.caption);
            String textMsg = privateChatPhoto.caption;

            privateChatPhoto.recevice = true;
            privateChatPhoto.send = false;


            this.socketResponse.responseData = privateChatPhoto;

            if(contactServiceThread!=null){
                if(contactServiceThread.isOnline()) {
                    // Send text msg
                    privateChatPhoto.appCredential = this.appCredential;
                    this.socketResponse.responseStat.msg =  this.appCredential.user.firstName+" " +
                                                            "" + contactServiceThread.appCredential.user.lastName+
                                                            " made a snapshot of the picture that you sent";
                    contactServiceThread.sendData(this.gson.toJson(this.socketResponse));

                    System.out.println("================================");
                    System.out.println("Send photo msg : to " + contactServiceThread.appCredential.user.firstName+" " + contactServiceThread.appCredential.user.lastName);
                    System.out.println("Send text Object "+this.gson.toJson(this.socketResponse));
                    System.out.println("================================");

                   // sendChatAcknowledgement(0, privateChatPhoto.tmpChatId, chatId, false, true);
                }else{
                   // sendChatAcknowledgement(0,privateChatPhoto.tmpChatId,chatId,false,false);
                }
            }else{
               // sendChatAcknowledgement(0,privateChatPhoto.tmpChatId,chatId,false,false);
            }
            // Saving to database
            updatePrivatePhotoTakeSnapShotStatusInChatHistory(chatId);
        }catch (ClassCastException ex){
            sendError(chatId,"Can not cast the object");
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

            this.socketResponse.responseStat.tag = tag_chatVideo;


            int senderId = this.appCredential.id;
            int receiverId = chatPhoto.appCredential.id;
            chatPhoto.caption = validateChatTextMsg( chatPhoto.caption);
            String textMsg = chatPhoto.caption;

            chatPhoto.recevice = true;
            chatPhoto.send = false;

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
                    sendChatAcknowledgement(0, chatPhoto.tmpChatId, chatId, false, true);

                }else{
                    //  saveOfflineMsg(textChat);
                    sendChatAcknowledgement(0,chatPhoto.tmpChatId,chatId,false,false);
                }
            }else{
                // Offline text msg
                //   saveOfflineMsg(textChat);
                sendChatAcknowledgement(0,chatPhoto.tmpChatId,chatId,false,false);
            }
            // Saving to database
            saveVideoInChatHistory(chatId, senderId, receiverId, textMsg, chatPhoto.base64Img);
        }catch (ClassCastException ex){
            sendError(chatId,"Unable to cast the object");
            ex.printStackTrace();
        }
    }
    private void processLocationChat(Object dataObject){
        if(!this.checkForAuthentication()){
            return;
        }
        this.socketResponse = new SocketResponse();
        String chatId = this.getMsgId();



        try{
            String jObjStr = this.gson.toJson(dataObject);
            LocationShare locationShare = this.gson.fromJson(jObjStr, LocationShare.class);
            // Sending the sender Received Acknowledgement
            sendChatReceivedAcknowledgement(0, locationShare.tmpChatId,chatId, false,false);

            System.out.println("Recepent :"+jObjStr);
            System.out.println("chatPhoto.appCredential.id :" + jObjStr);
            ServiceThread contactServiceThread =  BaseSocketController.getServiceThread(locationShare.appCredential.id);

            this.socketResponse.responseStat.tag = tag_chatLocation;
            this.socketResponse.responseStat.chatId = chatId;

            int senderId = this.appCredential.id;
            int receiverId = locationShare.appCredential.id;

            locationShare.recevice = true;
            locationShare.send = false;

            locationShare.chatId = chatId;
            this.socketResponse.responseData = locationShare;

            // Received ackonwledgement

            if(contactServiceThread!=null){
                if(contactServiceThread.isOnline()) {
                    // Send text msg
                    locationShare.appCredential = this.appCredential;
                    contactServiceThread.sendData(this.gson.toJson(this.socketResponse));


                    System.out.println("================================");
                    System.out.println("Send photo msg : to " + contactServiceThread.appCredential.user.firstName+" " + contactServiceThread.appCredential.user.lastName);
                    System.out.println("Send text Object "+this.gson.toJson(this.socketResponse));
                    System.out.println("================================");

                    sendChatAcknowledgement(0, locationShare.tmpChatId, chatId, false, true);
                }else{
                    sendChatAcknowledgement(0,locationShare.tmpChatId,chatId,false,false);
                }
            }else{
                sendChatAcknowledgement(0,locationShare.tmpChatId,chatId,false,false);
            }
            // Saving to database
            saveChatLocationInChatHistory(chatId, senderId, receiverId, locationShare);
        }catch (ClassCastException ex){
            sendError(chatId,"Can not cast the object");
            ex.printStackTrace();
        }

    }
    private void processChatAcknowledgement(Object dataObject) {

        Acknowledgement acknowledgement = new Acknowledgement();
        try {
            String jObjStr = this.gson.toJson(dataObject);

            acknowledgement = this.gson.fromJson(jObjStr, Acknowledgement.class);
            ServiceThread contactServiceThread =  BaseSocketController.getServiceThread(acknowledgement.appCredential.id);



            if(contactServiceThread!=null){

                if(contactServiceThread.isOnline()) {
                    this.socketResponse = new SocketResponse();
                    this.socketResponse.responseStat.tag = tag_ChatAcknowledgement;

                    acknowledgement.appCredential = this.appCredential;
                    this.socketResponse.responseData = acknowledgement;
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
            this.sendError(acknowledgement.chatId,"Internal error");

            ex.printStackTrace();
        }
        // Updating local database
        System.out.println("chatTransferStatus.chatId : "+ acknowledgement.chatId);
        if(acknowledgement.chatId !=""){
            markAsReadInChatHistory(acknowledgement.chatId);
        }

    }
    private void processContactOnline(Object dataObject) {


        try {
            ContactModel contactModel = new ContactModel();
            contactModel.setOwner_id(this.appCredential.id);
            ArrayList<Integer> contactIdList = contactModel.getContactIdByOwnerId();

            for(int appCredentialId : contactIdList){

                ServiceThread contactServiceThread =  BaseSocketController.getServiceThread(appCredentialId);
                Acknowledgement acknowledgement = new Acknowledgement();
                if(contactServiceThread!=null){
                    if(contactServiceThread.isOnline()) {
                        acknowledgement.isOnline = true;
                    }else{
                        acknowledgement.isOnline = false;
                    }
                    acknowledgement.appCredential = contactServiceThread.appCredential;
                }else{
                    acknowledgement.isOnline = false;
                    System.out.println("Obj is null");
                    acknowledgement.appCredential.id = appCredentialId;
                }


                this.socketResponse = new SocketResponse();
                this.socketResponse.responseStat.tag = tag_ContactOnline;



                this.socketResponse.responseData = acknowledgement;
                this.sendData(this.gson.toJson(this.socketResponse));
                System.out.println("********************************************");
                System.out.println("Send text msg  to : " + this.appCredential.user.firstName);
                System.out.println("Send text Object "+this.gson.toJson(this.socketResponse));
                System.out.println("********************************************");
            }

        }catch (ClassCastException ex){
            this.sendError("0","Internal error");

            ex.printStackTrace();
        }

    }

    private void processUserOnline(Object dataObject) {


        try {
            ContactModel contactModel = new ContactModel();
            contactModel.setOwner_id(this.appCredential.id);
            System.out.println(dataObject);
            String jObjStr = this.gson.toJson(dataObject);
            System.out.println(jObjStr);
            Integer[] contactIdList = this.gson.fromJson(jObjStr, Integer[].class);

            for(int appCredentialId : contactIdList){

                ServiceThread contactServiceThread =  BaseSocketController.getServiceThread(appCredentialId);
                Acknowledgement acknowledgement = new Acknowledgement();
                if(contactServiceThread!=null){
                    if(contactServiceThread.isOnline()) {
                        acknowledgement.isOnline = true;
                    }else{
                        AppLoginCredentialModel appLoginCredentialModel = new AppLoginCredentialModel();
                        appLoginCredentialModel.setId(appCredentialId);
                        acknowledgement.isOnline = false;
                        acknowledgement.lastSeen = appLoginCredentialModel.getLastLogoutTime();

                    }
                    acknowledgement.appCredential = contactServiceThread.appCredential;
                }else{
                    acknowledgement.isOnline = false;
                    AppLoginCredentialModel appLoginCredentialModel = new AppLoginCredentialModel();
                    appLoginCredentialModel.setId(appCredentialId);
                    acknowledgement.lastSeen = appLoginCredentialModel.getLastLogoutTime();

                    acknowledgement.isOnline = false;
                    System.out.println("Contact is false");
                    acknowledgement.appCredential.id = appCredentialId;
                }


                this.socketResponse = new SocketResponse();
                this.socketResponse.responseStat.tag = tag_UserOnline;



                this.socketResponse.responseData = acknowledgement;
                this.sendData(this.gson.toJson(this.socketResponse));
                System.out.println("********************************************");
                System.out.println("Send text msg  to : " + this.appCredential.user.firstName);
                System.out.println("Send text Object "+this.gson.toJson(this.socketResponse));
                System.out.println("********************************************");
            }

        }catch (ClassCastException ex){
            this.sendError("0","Internal error");

            ex.printStackTrace();
        }

    }

    private void savePhotoInChatHistory(String chatId,int senderId,int receiverId,String textMsg,Pictures pictures){

        int uId = this.appCredential.id;
        class DbOperationThread extends Thread{
            @Override
            public void run() {


                ChatHistoryModel chatHistory = new ChatHistoryModel();
                chatHistory.setChat_id(chatId);
                chatHistory.setTo(receiverId);
                chatHistory.setType(ChatHistoryModel.type_chatPic);
                chatHistory.setMedia_path(gson.toJson(pictures));
                chatHistory.setFrom(senderId);
                chatHistory.setChat_text(textMsg);
                chatHistory.insert();
            }
        };
        new DbOperationThread().start();
    }
    private void savePrivatePhotoInChatHistory(String chatId,int senderId,int receiverId,String textMsg,PrivateChatPhoto privateChatPhoto, ServiceThread contactServiceThread){

        class DbOperationThread extends Thread{
            @Override
            public void run() {


                ChatHistoryModel chatHistory = new ChatHistoryModel();
                chatHistory.setChat_id(chatId);
                chatHistory.setTo(receiverId);
                chatHistory.setType(ChatHistoryModel.type_chatPrivatePhoto);
                chatHistory.setExtra(gson.toJson(privateChatPhoto));
                chatHistory.setMedia_path(gson.toJson(privateChatPhoto.pictures));
                chatHistory.setFrom(senderId);
                chatHistory.setChat_text(textMsg);
                chatHistory.insert();

            }
        };
        new DbOperationThread().start();
    }
    private void destroyPrivatePhotoUsingTimer(PrivateChatPhoto privateChatPhoto,String chatId,ServiceThread contactServiceThread)  {
        class DestroyPrivatePhotoThread extends Thread {
            @Override

            public void run() {
                System.out.println("Timer " + privateChatPhoto.timer);
                if (privateChatPhoto.timer != 0) {

                    try {
                        this.sleep(privateChatPhoto.timer * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Executed after " + privateChatPhoto.timer * 1000 + "Mili Sec Chat Id" + chatId);
                    ChatHistoryModel chatHistory = new ChatHistoryModel();
                    chatHistory.setChat_id(chatId);
                    chatHistory.updateIsDeleteTrueByChat_id();

                    File file = new File(ImageHelper.getGlobalPath()+privateChatPhoto.pictures.original.path);
                    if(file.exists()){
                        file.delete();
                    }
                    sendChatPrivatePhotoAcknowledgement(0, privateChatPhoto.tmpChatId, chatId, false, false);

                    if (contactServiceThread != null) {
                        if (contactServiceThread.isOnline()) {
                            contactServiceThread.sendChatPrivatePhotoAcknowledgement(0, chatId, privateChatPhoto.tmpChatId, false, false);
                        }
                    }
                }
            }
        }
        new DestroyPrivatePhotoThread().start();
    }
    private void updatePrivatePhotoTakeSnapShotStatusInChatHistory(String chatId){

        int uId = this.appCredential.id;
        class DbOperationThread extends Thread{
            @Override
            public void run() {
                ChatHistoryModel chatHistory = new ChatHistoryModel();
                chatHistory.setChat_id(chatId);
                chatHistory.updateIsTakeSnapShotStatusByChatId();
            }
        };
        new DbOperationThread().start();
    }
    private void saveChatLocationInChatHistory(String chatId,int senderId,int receiverId,LocationShare locationShare){

        int uId = this.appCredential.id;
        class DbOperationThread extends Thread{
            @Override
            public void run() {


                ChatHistoryModel chatHistory = new ChatHistoryModel();
                chatHistory.setChat_id(chatId);
                chatHistory.setTo(receiverId);
                chatHistory.setType(ChatHistoryModel.type_chatLocationShare);
                chatHistory.setFrom(senderId);
                chatHistory.setExtra(gson.toJson(locationShare.places));


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
                    Videos videos = ImageHelper.saveByteToChatVideo(b, uId);
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
                chatHistory.setTo(receiverId);
                chatHistory.setType(ChatHistoryModel.type_txtChat);
                chatHistory.setFrom(senderId);
                chatHistory.setChat_text(textMsg);
                if(chatHistory.insert()<=0){
                    sendError(chatId,"Unable to handle Smile from IOS device");
                }
            }
        };
        new DbOperationThread().start();
    }
    private void saveContactShare(String chatId,int senderId,int receiverId,ContactShare contactShare){

        class DbOperationThread extends Thread{
            @Override
            public void run() {
                ChatHistoryModel chatHistory = new ChatHistoryModel();
                chatHistory.setChat_id(chatId);
                chatHistory.setTo(receiverId);
                chatHistory.setType(ChatHistoryModel.type_chatContactShare);
                chatHistory.setFrom(senderId);
                chatHistory.setExtra(gson.toJson(contactShare.contact));
                chatHistory.insert();
            }
        };
        new DbOperationThread().start();
    }

    private synchronized void  markAsReadInChatHistory(String chatId){

        class DbOperationThread extends Thread{
            @Override
            public void run() {
                ChatHistoryModel chatHistory = new ChatHistoryModel();
                chatHistory.setChat_id(chatId);
                if(chatHistory.updateReadStatusByChatId()){
                    try {
                        this.sleep(10000);
                        chatHistory.updateReadStatusByChatId();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        new DbOperationThread().start();
    }
    private String validateChatTextMsg(String msg){
        String tempMsg = msg.trim();

        return tempMsg;
    }

    private void sendChatAcknowledgement(int id,String tmpChatId,String chatId,boolean isRead,boolean isOline){
        Acknowledgement acknowledgement = new Acknowledgement();


        this.socketResponse = new SocketResponse();
        this.socketResponse.responseStat.tag = tag_ChatAcknowledgement;

        this.socketResponse.responseStat.chatId = chatId;

        acknowledgement.id = id;
        acknowledgement.tmpChatId = tmpChatId;
        acknowledgement.chatId  = chatId;
        acknowledgement.isRead =isRead;
        acknowledgement.isOnline =isOline;
        acknowledgement.appCredential = this.appCredential;

        this.socketResponse.responseData = acknowledgement;

        this.sendData(this.gson.toJson(this.socketResponse));


        System.out.println("********************************************");
        System.out.println("Send text msg : to " + acknowledgement.appCredential.user.firstName);
        System.out.println("Send text Object " + this.gson.toJson(this.socketResponse));
        System.out.println(" Offline text msg and obj null");
        System.out.println("********************************************");
    }
    private void sendChatPrivatePhotoAcknowledgement(int id,String tmpChatId,String chatId,boolean isRead,boolean isOline){
        Acknowledgement acknowledgement = new Acknowledgement();


        this.socketResponse = new SocketResponse();
        this.socketResponse.responseStat.tag = tag_ChatPrivatePhotoAcknowledgement;

        this.socketResponse.responseStat.chatId = chatId;

        acknowledgement.id = id;
        acknowledgement.tmpChatId = tmpChatId;
        acknowledgement.chatId  = chatId;
        acknowledgement.isRead =isRead;
        acknowledgement.isOnline =isOline;
        acknowledgement.appCredential = this.appCredential;

        this.socketResponse.responseData = acknowledgement;

        this.sendData(this.gson.toJson(this.socketResponse));


        System.out.println("********************************************");
        System.out.println("Send text msg : to " + acknowledgement.appCredential.user.firstName);
        System.out.println("Send text Object " + this.gson.toJson(this.socketResponse));
        System.out.println(" Offline text msg and obj null");
        System.out.println("********************************************");
    }

    private void sendChatReceivedAcknowledgement(int id,String tmpChatId,String chatId,boolean isRead,boolean isOline){
        Acknowledgement acknowledgement = new Acknowledgement();


        SocketResponse socketResponse = new SocketResponse();
        socketResponse.responseStat.tag = tag_ChatReceived;

        socketResponse.responseStat.chatId = chatId;

        acknowledgement.id = id;
        acknowledgement.tmpChatId = tmpChatId;
        acknowledgement.chatId  = chatId;
        acknowledgement.isRead =isRead;
        acknowledgement.isOnline =isOline;
        acknowledgement.appCredential = this.appCredential;

        socketResponse.responseData = acknowledgement;

        this.sendData(this.gson.toJson(socketResponse));


        System.out.println("********************************************");
        System.out.println("Send text msg : to " + acknowledgement.appCredential.user.firstName);
        System.out.println("Send text Object " + this.gson.toJson(socketResponse));
        System.out.println(" Offline text msg and obj null");
        System.out.println("********************************************");
    }
    private void sendError(String chatId,String msg){
        this.socketResponse.responseStat.tag = "error";
        this.socketResponse.responseStat.status = false;
        this.socketResponse.responseStat.chatId = chatId;
        this.socketResponse.responseStat.msg = msg;
        System.out.println("********************************************");
        System.out.println("Send text msg : to " + this.appCredential.user.firstName);
        System.out.println("Send text Object " + this.gson.toJson(socketResponse));
        System.out.println("********************************************");
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
        if(this.isOnline())
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