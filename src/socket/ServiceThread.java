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
import java.lang.reflect.Array;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ServiceThread extends Thread {
    public static HashMap<Integer,String> currentUsers = new HashMap<>();
    public final static String tag_authentication = "authentication";
    public final static String tag_textChat = "textchat";
    public final static String tag_chatPhoto = "chatphoto_transfer";
    public final static String tag_chatLocation = "chatlocation_share";
    public final static String tag_ChatContactShare = "chatcontact_share";
    public final static String tag_chatPrivatePhoto = "chatprivatephoto_transfer";
    public final static String tag_chatPrivatePhotoTookSnapshot = "chat_private_photo_took_snapshot";
    public final static String tag_chatPrivatePhotoUpdateCountDown = "chat_private_photo_update_count_down";
    public final static String tag_chatPrivatePhotoTookSnapshotStartCountDown = "chat_private_photo_destroy";
    public final static String tag_chatVideo = "chat_video_transfer";
    public final static String tag_ContactOnlineOffline = "broad_cast_contact_online_offline";
    public final static String tag_ChatAcknowledgement = "chat_acknowledgement";
    public final static String tag_ChatPrivatePhotoAcknowledgement = "chat_private_photo_destroy_acknowledgement";
    public final static String tag_ChatPrivatePhotoCountDownAcknowledgement = "chat_private_photo_countdown_acknowledgement";
    public final static String tag_SyncContact = "sync_contact";
    public final static String tag_ChatReceived = "chat_received";
    public final static String tag_UserOnline = "user_online";

    private Socket serviceSocket;
    private boolean authintic;
    private Gson gson;
    private AppCredential appCredential;
    private ArrayList<Contact> contacts;
    private String id;
    private BufferedReader input;
    private PrintStream output;
    //private SocketResponse socketResponse;
    private InputStream fileInput;

    private ArrayList<AppCredential> currentUnknownChatPersons;

    public ServiceThread(Socket serviceSocket ) {
        super();

        this.appCredential = new AppCredential();
        this.currentUnknownChatPersons = new ArrayList<>();
        this.contacts = new ArrayList<Contact>();
        this.serviceSocket = serviceSocket;
        this.authintic = false;
        this.gson = new Gson();

        this.input = null;
        this.output = null;

        this.id = "";
       // this.socketResponse = new SocketResponse();

    }
    public String getMsgId(){

        return new SimpleDateFormat("yyyyMMddHHmmssSSSSSS").format(new Date());
    }

    public void processPushBack(String objStr){
        if (!this.serviceSocket.isClosed()) {
            SocketResponse socketResponse = this.gson.fromJson(objStr, SocketResponse.class);

            System.out.println("================================");
            System.out.println("From : "+this.appCredential.user.firstName+" "+this.appCredential.user.lastName);
            System.out.println("TAG : "+socketResponse.responseStat.tag);
            //System.out.println("ObjStr : "+recvStr);
            System.out.println("================================");


            switch ( socketResponse.responseStat.tag) {
                case tag_chatVideo:
                    this.processChatVideoTransfer(socketResponse.responseData);
                    break;
                case tag_chatPhoto:
                    this.processChatPhotoPushBackTransfer(socketResponse.responseData);
                    break;
                default:
                    break;
            }
        }
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

//                        AppLoginCredentialModel appLoginCredentialModel = new AppLoginCredentialModel();
//                        appLoginCredentialModel.setId(appCredential.id);
//                        appLoginCredentialModel.updateLastLogout();

                        System.out.println("Connection closed");
                        continue;
                    }
                    // Initializing with new obj
                    SocketResponse socketResponse = new SocketResponse();
                    socketResponse = this.gson.fromJson(recvStr, SocketResponse.class);

                    System.out.println("================================");
                    System.out.println("From : "+this.appCredential.user.firstName+" "+this.appCredential.user.lastName);
                    System.out.println("TAG : "+socketResponse.responseStat.tag);
                     //System.out.println("ObjStr : "+recvStr);
                    System.out.println("================================");


                    switch ( socketResponse.responseStat.tag){
                        case tag_authentication:
                            this.processAuthentication(socketResponse.responseData);
                            break;
                        case tag_textChat:
                            this.processTextChat(socketResponse.responseData);
                            break;
                        case tag_chatPhoto:
                            this.processChatPhotoTransfer(socketResponse.responseData);
                            break;
                        case tag_chatVideo:
                            this.processChatVideoTransfer(socketResponse.responseData);
                        case tag_chatLocation:
                            this.processLocationChat(socketResponse.responseData);
                            break;
                        case tag_ChatContactShare:
                            this.processChatContactSharing(socketResponse.responseData);
                            break;
                        case tag_chatPrivatePhoto:
                            this.processChatPrivatePhotoTransfer(socketResponse.responseData);
                            break;
                        case tag_chatPrivatePhotoTookSnapshot:
                            this.processChatPrivateTookSnapShot(socketResponse.responseData);
                            break;
                        case tag_chatPrivatePhotoTookSnapshotStartCountDown:
                            this.processChatPrivatePhotoTransferStartCountDown(socketResponse.responseData);
                            break;
                        case tag_ChatAcknowledgement:
                            this.processChatAcknowledgement(socketResponse.responseData);
                            break;
                        case tag_UserOnline:
                            this.processUserOnline(socketResponse.responseData);
                            break;
                        case tag_chatPrivatePhotoUpdateCountDown:
                            this.processChatPhotoCountDownUpdate(socketResponse.responseData);
                        default:
                            break;
                    }
                } catch (IOException e) {
                    this.closeConnection();
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

        this.broadCastOnlineOfflineToContact(false);
        this.broadCastOnlineOfflineToUnknownChatPerson(false);

    }
    private void flushDataToOthers(ServiceThread contactServiceThread,Object obj){

        if(!this.isInContactList( contactServiceThread.appCredential)){
            if(!this.currentUnknownChatPersons.contains(contactServiceThread.appCredential)){
                this.currentUnknownChatPersons.add(contactServiceThread.appCredential);
            }
        }
        contactServiceThread.sendData(this.appCredential, (String) obj);
    }
    private void processAuthentication(Object dataObject){

        SocketResponse socketResponse = new SocketResponse();
        try{
            String jObjStr = this.gson.toJson(dataObject);

            AuthCredential authCredential = this.gson.fromJson(jObjStr, AuthCredential.class);
            this.authintic = authenticate(authCredential.accessToken);

            if(this.authintic){
                this.appCredential = castAuthToAppCredential(authCredential);
                socketResponse.responseStat.tag = "authentication_status";
                socketResponse.responseStat.msg = "authentication success";
                System.out.println("Putting obj in :" + authCredential.id);
                BaseSocketController.putServiceThread(authCredential.id, this);
                ChatHistoryModel chatHistoryModel = new ChatHistoryModel();
                chatHistoryModel.setFrom(this.appCredential.id);


                ServiceThread.currentUsers.put(authCredential.id, "HOGA");
                // Fetching contacts

                this.syncContactsWithDb();
                this.broadCastOnlineOfflineToContact(true);
            }else{
                socketResponse.responseStat.tag = "authentication_status";
                socketResponse.responseStat.status = false;
                socketResponse.responseStat.msg = "Access token is wrong";
            }
        }catch (ClassCastException ex){
            sendError("0","Can not cast AuthCredential");
            this.closeConnection();

            ex.printStackTrace();
        }
        this.sendData(null, this.gson.toJson(socketResponse));

        if(!this.authintic){
            this.closeConnection();
        }
    }
    private void processTextChat(Object dataObject){
        if(!this.checkForAuthentication()){
            return;
        }
        SocketResponse socketResponse = new SocketResponse();
        String chatId = this.getMsgId();
        try{

            String jObjStr = this.gson.toJson(dataObject);

            TextChat textChat = this.gson.fromJson(jObjStr, TextChat.class);
            // Sending the sender Received Acknowledgement
            sendChatReceivedAcknowledgement(0, textChat.tmpChatId,chatId, false,false);

            ServiceThread contactServiceThread =  BaseSocketController.getServiceThread(textChat.appCredential.id);

            socketResponse.responseStat.tag = tag_textChat;
            socketResponse.responseStat.chatId = chatId;

            int senderId = this.appCredential.id;
            int receiverId = textChat.appCredential.id;
            textChat.text = validateChatTextMsg( textChat.text);
            String textMsg = textChat.text;

            textChat.recevice = true;
            textChat.send = false;
            textChat.chatId = chatId;
            socketResponse.responseData = textChat;
            // Received acknowledgement

            if(contactServiceThread!=null){

                if(contactServiceThread.isOnline()) {
                    // Send text msg
                    textChat.appCredential = this.appCredential;
                    this.flushDataToOthers(contactServiceThread, this.gson.toJson(socketResponse));


                    System.out.println("================================");
                    System.out.println("Send text msg : to " + contactServiceThread.appCredential.user.firstName+" " + contactServiceThread.appCredential.user.lastName);
                    System.out.println("Send text Object "+this.gson.toJson(socketResponse));
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
        SocketResponse socketResponse = new SocketResponse();
        String chatId = this.getMsgId();
        try{

            String jObjStr = this.gson.toJson(dataObject);

            ContactShare contactShare = this.gson.fromJson(jObjStr, ContactShare.class);

            // Sending the sender Received Acknowledgement
            sendChatReceivedAcknowledgement(0, contactShare.tmpChatId,chatId, false,false);

            ServiceThread contactServiceThread =  BaseSocketController.getServiceThread(contactShare.appCredential.id);

            socketResponse.responseStat.tag = tag_ChatContactShare;
            socketResponse.responseStat.chatId = chatId;

            int senderId = this.appCredential.id;
            int receiverId = contactShare.appCredential.id;

            contactShare.recevice = true;
            contactShare.send = false;
            contactShare.chatId = chatId;
            socketResponse.responseData = contactShare;
            // Received acknowledgement

            if(contactServiceThread!=null){
                if(contactServiceThread.isOnline()) {
                    // Send text msg
                    contactShare.appCredential = this.appCredential;
                    this.flushDataToOthers(contactServiceThread, this.gson.toJson(socketResponse));

                    System.out.println("================================");
                    System.out.println("Send text msg : to " + contactServiceThread.appCredential.user.firstName+" " + contactServiceThread.appCredential.user.lastName);
                    System.out.println("Send text Object "+this.gson.toJson(socketResponse));
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
        SocketResponse socketResponse = new SocketResponse();
        String chatId = this.getMsgId();



        try{
            String jObjStr = this.gson.toJson(dataObject);
            ChatPhoto chatPhoto = this.gson.fromJson(jObjStr, ChatPhoto.class);
            sendChatReceivedAcknowledgement(0, chatPhoto.tmpChatId,chatId, false,false);

            System.out.println("Recepent :"+jObjStr);
            System.out.println("chatPhoto.appCredential.id :" + jObjStr);
            ServiceThread contactServiceThread =  BaseSocketController.getServiceThread(chatPhoto.appCredential.id);

            socketResponse.responseStat.tag = tag_chatPhoto;
            socketResponse.responseStat.chatId = chatId;

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
            socketResponse.responseData = chatPhoto;

            // Received ackonwledgement

            if(contactServiceThread!=null){
                if(contactServiceThread.isOnline()) {

                    // Send text msg
                    chatPhoto.appCredential = this.appCredential;

                    this.flushDataToOthers(contactServiceThread, this.gson.toJson(socketResponse));

                    System.out.println("================================");
                    System.out.println("Send photo msg : to " + contactServiceThread.appCredential.user.firstName+" " + contactServiceThread.appCredential.user.lastName);
                    System.out.println("Send text Object "+this.gson.toJson(socketResponse));
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
    private void processChatPhotoPushBackTransfer(Object dataObject){
        if(!this.checkForAuthentication()){
            return;
        }
        SocketResponse socketResponse = new SocketResponse();
        String chatId = this.getMsgId();



        try{
            String jObjStr = this.gson.toJson(dataObject);
            ChatPhoto chatPhoto = this.gson.fromJson(jObjStr, ChatPhoto.class);
            sendChatReceivedAcknowledgement(0, chatPhoto.tmpChatId,chatId, false,false);

            System.out.println("Recepent :"+jObjStr);
            System.out.println("chatPhoto.appCredential.id :" + jObjStr);
            ServiceThread contactServiceThread =  BaseSocketController.getServiceThread(chatPhoto.appCredential.id);

            socketResponse.responseStat.tag = tag_chatPhoto;
            socketResponse.responseStat.chatId = chatId;

            int senderId = this.appCredential.id;
            int receiverId = chatPhoto.appCredential.id;
            chatPhoto.caption = validateChatTextMsg( chatPhoto.caption);
            String textMsg = chatPhoto.caption;

            chatPhoto.recevice = true;
            chatPhoto.send = false;

            chatPhoto.base64Img = "";

            chatPhoto.chatId = chatId;
            socketResponse.responseData = chatPhoto;

            // Received ackonwledgement

            if(contactServiceThread!=null){
                if(contactServiceThread.isOnline()) {

                    // Send text msg
                    chatPhoto.appCredential = this.appCredential;

                    this.flushDataToOthers(contactServiceThread, this.gson.toJson(socketResponse));

                    System.out.println("================================");
                    System.out.println("Send photo msg : to " + contactServiceThread.appCredential.user.firstName+" " + contactServiceThread.appCredential.user.lastName);
                    System.out.println("Send text Object "+this.gson.toJson(socketResponse));
                    System.out.println("================================");

                    sendChatAcknowledgement(0, chatPhoto.tmpChatId, chatId, false, true);
                }else{
                    sendChatAcknowledgement(0,chatPhoto.tmpChatId,chatId,false,false);
                }
            }else{
                sendChatAcknowledgement(0,chatPhoto.tmpChatId,chatId,false,false);
            }

        }catch (ClassCastException ex){
            sendError(chatId,"Can not cast the object");
            ex.printStackTrace();
        }

    }
    private void processChatPrivatePhotoTransfer(Object dataObject){
        if(!this.checkForAuthentication()){
            return;
        }
        SocketResponse socketResponse = new SocketResponse();
        String chatId = this.getMsgId();



        try{
            String jObjStr = this.gson.toJson(dataObject);
            PrivateChatPhoto orignalPrivateChatPhoto = this.gson.fromJson(jObjStr, PrivateChatPhoto.class);
            PrivateChatPhoto privateChatPhoto = orignalPrivateChatPhoto;
            // Sending the sender Received Acknowledgement
            privateChatPhoto.from = this.appCredential.id;
            privateChatPhoto.to = privateChatPhoto.appCredential.id;

            sendChatReceivedAcknowledgement(0, privateChatPhoto.tmpChatId, chatId, false, false);


            ServiceThread contactServiceThread =  BaseSocketController.getServiceThread(privateChatPhoto.appCredential.id);

            socketResponse.responseStat.tag = tag_chatPrivatePhoto;
            socketResponse.responseStat.chatId = chatId;

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

            socketResponse.responseData = privateChatPhoto;
            // Assign Sender
            String privateChatPhotoStrObj = this.gson.toJson(privateChatPhoto);
            privateChatPhoto.appCredential = this.appCredential;
            if(contactServiceThread!=null){
                if(contactServiceThread.isOnline()) {
                    // Send text msg

                    this.flushDataToOthers(contactServiceThread, this.gson.toJson(socketResponse));

                    System.out.println("================================");
                    System.out.println("Send private photo msg : to " + contactServiceThread.appCredential.user.firstName+" " + contactServiceThread.appCredential.user.lastName);
                    System.out.println("Send text Object "+this.gson.toJson(socketResponse));
                    System.out.println("================================");

                    sendChatAcknowledgement(0, privateChatPhoto.tmpChatId, chatId, false, true);
                }else{
                    sendChatAcknowledgement(0,privateChatPhoto.tmpChatId,chatId,false,false);
                }
            }else{
                sendChatAcknowledgement(0,privateChatPhoto.tmpChatId,chatId,false,false);
            }
            // Saving to database
            savePrivatePhotoInChatHistory(chatId, senderId, receiverId, textMsg, this.gson.fromJson(privateChatPhotoStrObj, PrivateChatPhoto.class), contactServiceThread);
        }catch (ClassCastException ex){
            sendError(chatId,"Can not cast the object");
            ex.printStackTrace();
        }

    }
    private void processChatPrivatePhotoTransferStartCountDown(Object dataObject){
        if(!this.checkForAuthentication()){
            return;
        }
        if(dataObject==null || dataObject==""){
            sendError("0","Private Photo Object Empty");
        }
        SocketResponse socketResponse = new SocketResponse();
        String chatId = "";



        try{
            String jObjStr = this.gson.toJson(dataObject);
            System.out.println("================================");
            System.out.println("From Private Chat photo :" + jObjStr);
            System.out.println("================================");
            PrivateChatPhoto privateChatPhoto = this.gson.fromJson(jObjStr, PrivateChatPhoto.class);

            // Sending the sender Received Acknowledgement
           // sendChatReceivedAcknowledgement(0, privateChatPhoto.tmpChatId, chatId, false, false);


            ServiceThread contactServiceThread =  BaseSocketController.getServiceThread(privateChatPhoto.appCredential.id);

            socketResponse.responseStat.tag = tag_chatPrivatePhotoTookSnapshotStartCountDown;
            socketResponse.responseStat.chatId = chatId;

            int senderId = this.appCredential.id;
            int receiverId = privateChatPhoto.appCredential.id;
            privateChatPhoto.caption = validateChatTextMsg( privateChatPhoto.caption);
            String textMsg = privateChatPhoto.caption;

            privateChatPhoto.recevice = true;
            privateChatPhoto.send = false;

            privateChatPhoto.base64Img = "";


            socketResponse.responseData = privateChatPhoto;

            if(contactServiceThread!=null) {
                if (contactServiceThread.isOnline()) {

                    // Send text msg
                    privateChatPhoto.appCredential = this.appCredential;

                    this.flushDataToOthers(contactServiceThread, this.gson.toJson(socketResponse));

                    System.out.println("================================");
                    System.out.println("Send photo msg : to " + contactServiceThread.appCredential.user.firstName + " " + contactServiceThread.appCredential.user.lastName);
                    System.out.println("Send text Object " + this.gson.toJson(socketResponse));
                    System.out.println("================================");
                }
            }

            destroyPrivatePhotoUsingTimer(privateChatPhoto, contactServiceThread);
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
        SocketResponse  socketResponse = new SocketResponse();
        String chatId = this.getMsgId();



        try{
            String jObjStr = this.gson.toJson(dataObject);
            PrivateChatPhoto privateChatPhoto = this.gson.fromJson(jObjStr, PrivateChatPhoto.class);

            ServiceThread contactServiceThread =  BaseSocketController.getServiceThread(privateChatPhoto.from);

            socketResponse.responseStat.tag = tag_chatPrivatePhotoTookSnapshot;
            socketResponse.responseStat.chatId = chatId;

            privateChatPhoto.caption = validateChatTextMsg( privateChatPhoto.caption);

            privateChatPhoto.recevice = true;
            privateChatPhoto.send = false;


            socketResponse.responseData = privateChatPhoto;
            privateChatPhoto.appCredential = this.appCredential;
            privateChatPhoto.extra =  this.appCredential.user.firstName+" " +
                    "" + this.appCredential.user.lastName+
                    " made a snapshot of the picture that you sent";

            if(contactServiceThread!=null){
                if(contactServiceThread.isOnline()) {
                    // Send text msg

                    this.flushDataToOthers(contactServiceThread, this.gson.toJson(socketResponse));
                    System.out.println("================================");
                    System.out.println("Send photo msg : to " + contactServiceThread.appCredential.user.firstName+" " + contactServiceThread.appCredential.user.lastName);
                    System.out.println("Send text Object "+this.gson.toJson(socketResponse));
                    System.out.println("================================");

                   // sendChatAcknowledgement(0, privateChatPhoto.tmpChatId, chatId, false, true);
                }else{
                   // sendChatAcknowledgement(0,privateChatPhoto.tmpChatId,chatId,false,false);
                }
            }else{
               // sendChatAcknowledgement(0,privateChatPhoto.tmpChatId,chatId,false,false);
            }
            // Saving to database
            updatePrivatePhotoTakeSnapShotStatusInChatHistory(chatId, privateChatPhoto);
        }catch (ClassCastException ex){
            sendError(chatId,"Can not cast the object");
            ex.printStackTrace();
        }
    }
    private void processChatPhotoCountDownUpdate(Object dataObject){
        if(!this.checkForAuthentication()){
            return;
        }
        SocketResponse socketResponse = new SocketResponse();
        String chatId = this.getMsgId();



        try{
            String jObjStr = this.gson.toJson(dataObject);
            PrivateChatPhoto privateChatPhoto = this.gson.fromJson(jObjStr, PrivateChatPhoto.class);


            // Update to database
            updatePrivatePhotoCountDown(privateChatPhoto.chatId, privateChatPhoto.timer);
        }catch (ClassCastException ex){
            sendError(chatId,"Can not cast the object");
            ex.printStackTrace();
        }

    }
    private void processChatVideoTransfer(Object dataObject){
        if(!this.checkForAuthentication()){
            return;
        }
        SocketResponse socketResponse = new SocketResponse();
        String chatId = this.getMsgId();

        try{
            String jObjStr = this.gson.toJson(dataObject);

            ChatVideo chatVideo = this.gson.fromJson(jObjStr, ChatVideo.class);
            ServiceThread contactServiceThread =  BaseSocketController.getServiceThread(chatVideo.appCredential.id);

            socketResponse.responseStat.tag = tag_chatVideo;


            int senderId = this.appCredential.id;
            int receiverId = chatVideo.appCredential.id;
            chatVideo.caption = validateChatTextMsg( chatVideo.caption);
            String textMsg = chatVideo.caption;

            chatVideo.recevice = true;
            chatVideo.send = false;
            ChatVideo originalChatVideo = this.gson.fromJson(this.gson.toJson(chatVideo), ChatVideo.class);

            if(contactServiceThread!=null){

                if(contactServiceThread.isOnline()) {
                    // Send text msg
                    chatVideo.chatId = chatId;
                    chatVideo.appCredential = this.appCredential;
                    socketResponse.responseData = chatVideo;

                    this.flushDataToOthers(contactServiceThread, this.gson.toJson(socketResponse));

                    System.out.println("================================");
                    System.out.println("Send video : to " + contactServiceThread.appCredential.user.firstName+" " + contactServiceThread.appCredential.user.lastName);
                    //System.out.println("Send text Object "+this.gson.toJson(this.socketResponse));
                    System.out.println("================================");
                    sendChatAcknowledgement(0, originalChatVideo.tmpChatId, chatId, false, true);

                }else{
                    //  saveOfflineMsg(textChat);
                    sendChatAcknowledgement(0,originalChatVideo.tmpChatId,chatId,false,false);
                }
            }else{
                // Offline text msg
                //   saveOfflineMsg(textChat);
                sendChatAcknowledgement(0,originalChatVideo.tmpChatId,chatId,false,false);
            }

        }catch (ClassCastException ex){
            sendError(chatId,"Unable to cast the object");
            ex.printStackTrace();
        }
    }
    private void processLocationChat(Object dataObject){
        if(!this.checkForAuthentication()){
            return;
        }
        SocketResponse socketResponse = new SocketResponse();
        String chatId = this.getMsgId();



        try{
            String jObjStr = this.gson.toJson(dataObject);
            LocationShare locationShare = this.gson.fromJson(jObjStr, LocationShare.class);
            // Sending the sender Received Acknowledgement
            sendChatReceivedAcknowledgement(0, locationShare.tmpChatId,chatId, false,false);

            System.out.println("Recepent :"+jObjStr);
            System.out.println("chatPhoto.appCredential.id :" + jObjStr);
            ServiceThread contactServiceThread =  BaseSocketController.getServiceThread(locationShare.appCredential.id);

            socketResponse.responseStat.tag = tag_chatLocation;
            socketResponse.responseStat.chatId = chatId;

            int senderId = this.appCredential.id;
            int receiverId = locationShare.appCredential.id;

            locationShare.recevice = true;
            locationShare.send = false;

            locationShare.chatId = chatId;
            socketResponse.responseData = locationShare;

            // Received ackonwledgement

            if(contactServiceThread!=null){
                if(contactServiceThread.isOnline()) {
                    // Send text msg
                    locationShare.appCredential = this.appCredential;
                    this.flushDataToOthers(contactServiceThread, this.gson.toJson(socketResponse));

                    System.out.println("================================");
                    System.out.println("Send photo msg : to " + contactServiceThread.appCredential.user.firstName+" " + contactServiceThread.appCredential.user.lastName);
                    System.out.println("Send text Object "+this.gson.toJson(socketResponse));
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
                    SocketResponse socketResponse = new SocketResponse();
                    socketResponse.responseStat.tag = tag_ChatAcknowledgement;

                    acknowledgement.appCredential = this.appCredential;
                    socketResponse.responseData = acknowledgement;
                    this.flushDataToOthers(contactServiceThread, this.gson.toJson(socketResponse));

                    System.out.println("********************************************");
                    System.out.println("Send text msg  to : " + contactServiceThread.appCredential.user.firstName);
                    System.out.println("Send text Object "+this.gson.toJson(socketResponse));
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


                SocketResponse socketResponse = new SocketResponse();
                socketResponse.responseStat.tag = tag_UserOnline;



                socketResponse.responseData = acknowledgement;
                this.sendData(null,this.gson.toJson(socketResponse));
                System.out.println("********************************************");
                System.out.println("Send text msg  to : " + this.appCredential.user.firstName);
                System.out.println("Send text Object "+this.gson.toJson(socketResponse));
                System.out.println("********************************************");
            }

        }catch (ClassCastException ex){
            this.sendError("0","Internal error");

            ex.printStackTrace();
        }

    }
    private void broadCastOnlineOfflineToContact(boolean online) {


        try {
            for(Contact contact : this.contacts){

                ServiceThread contactServiceThread =  BaseSocketController.getServiceThread(contact.id);

                Acknowledgement acknowledgement = new Acknowledgement();
                acknowledgement.appCredential = this.appCredential;
                acknowledgement.isOnline = online;

                SocketResponse socketResponse = new SocketResponse();
                socketResponse.responseStat.tag = tag_ContactOnlineOffline;
                socketResponse.responseData = acknowledgement;

                if(contactServiceThread!=null){
                    if(contactServiceThread.isOnline()) {
                        this.flushDataToOthers(contactServiceThread, this.gson.toJson(socketResponse));

                        System.out.println("********************************************");
                        System.out.println("Online/Offline : " + contactServiceThread.appCredential.user.firstName);
                        System.out.println("Send text Object "+this.gson.toJson(socketResponse));
                        System.out.println("********************************************");

                    }
                }
            }




        }catch (ClassCastException ex){
            this.sendError("0","Internal error");

            ex.printStackTrace();
        }

    }
    private void broadCastOnlineOfflineToUnknownChatPerson(boolean online){
        try {
            for(AppCredential appCredential : this.currentUnknownChatPersons){

                ServiceThread contactServiceThread =  BaseSocketController.getServiceThread(appCredential.id);

                Acknowledgement acknowledgement = new Acknowledgement();
                acknowledgement.appCredential = this.appCredential;
                acknowledgement.isOnline = online;

                SocketResponse socketResponse = new SocketResponse();
                socketResponse.responseStat.tag = tag_ContactOnlineOffline;
                socketResponse.responseData = acknowledgement;

                if(contactServiceThread!=null){
                    if(contactServiceThread.isOnline()) {
                        this.flushDataToOthers(contactServiceThread, this.gson.toJson(socketResponse));

                        System.out.println("********************************************");
                        System.out.println("Online/Offline : " + contactServiceThread.appCredential.user.firstName);
                        System.out.println("Send text Object "+this.gson.toJson(socketResponse));
                        System.out.println("********************************************");

                    }
                }
            }




        }catch (ClassCastException ex){
            this.sendError("0","Internal error");

            ex.printStackTrace();
        }
    }
    private void syncContactsWithDb(){
        ContactModel contactModel = new ContactModel();
        contactModel.setOwner_id(this.appCredential.id);
        this.contacts = contactModel.getContactByOwnerId();

        Acknowledgement acknowledgement = new Acknowledgement();
        acknowledgement.appCredential = this.appCredential;
        acknowledgement.isOnline = true;

        SocketResponse socketResponse = new SocketResponse();
        socketResponse.responseStat.tag = tag_SyncContact;
        socketResponse.responseStat.msg="Contacts syncs";
        socketResponse.responseData = acknowledgement;


        this.sendData(null, this.gson.toJson(socketResponse));
    }
    private void savePhotoInChatHistory(String chatId,int senderId,int receiverId,String textMsg,Pictures pictures){

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
    private void destroyPrivatePhotoUsingTimer(PrivateChatPhoto privateChatPhoto,ServiceThread contactServiceThread)  {
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
                    System.out.println("Executed after " + privateChatPhoto.timer * 1000 + "Mili Sec Chat Id" + privateChatPhoto.chatId);
                    ChatHistoryModel chatHistory = new ChatHistoryModel();
                    chatHistory.setChat_id(privateChatPhoto.chatId);
                    chatHistory.updateIsDeleteTrueByChat_id();

                    File file = new File(ImageHelper.getGlobalPath()+privateChatPhoto.pictures.original.path);
                    if(file.exists()){
                        file.delete();
                    }
                    sendChatPrivatePhotoAcknowledgement(0, privateChatPhoto, false, false);

                    if (contactServiceThread != null) {
                        if (contactServiceThread.isOnline()) {
                            contactServiceThread.sendChatPrivatePhotoAcknowledgement(0, privateChatPhoto, false, false);
                        }
                    }
                }
            }
        }
        new DestroyPrivatePhotoThread().start();
    }
    private void updatePrivatePhotoTakeSnapShotStatusInChatHistory(String newChatId,PrivateChatPhoto privateChatPhoto){


        class DbOperationThread extends Thread{
            @Override
            public void run() {
                ChatHistoryModel chatHistory = new ChatHistoryModel();
                chatHistory.setChat_id(privateChatPhoto.chatId);
                TextChat textChat = new TextChat();
                textChat.chatId = newChatId;
                textChat.text = (String)privateChatPhoto.extra;
                textChat.appCredential = privateChatPhoto.appCredential;
                textChat.to = privateChatPhoto.to;
                textChat.from = privateChatPhoto.from;

                chatHistory.updateIsTakeSnapShotStatusByChatId((String) privateChatPhoto.extra, textChat);
            }
        };
        new DbOperationThread().start();
    }
    private void updatePrivatePhotoCountDown(String chatId,int timer){

        class DbOperationThread extends Thread{
            @Override
            public void run() {
                ChatHistoryModel chatHistory = new ChatHistoryModel();
                chatHistory.setChat_id(chatId);
                chatHistory.updateCountDownByChatId(timer);
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
    private long saveVideoInChatHistory(ChatVideo chatVideo){

        ChatHistoryModel chatHistoryModel = new ChatHistoryModel();
        chatHistoryModel.setChat_id(chatVideo.chatId);
        chatHistoryModel.setTo(chatVideo.to);
        chatHistoryModel.setType(ChatHistoryModel.type_chatVideo);
        chatHistoryModel.setMedia_path(gson.toJson(chatVideo.video));
        chatHistoryModel.setFrom(chatVideo.from);
        chatHistoryModel.setChat_text("");
        return chatHistoryModel.insert();
    }
    private void saveOfflineMsg(TextChat textChat){
        ChatTransferStatus chatTransferStatus = new ChatTransferStatus();


        SocketResponse socketResponse = new SocketResponse();
        socketResponse.responseStat.tag = tag_ChatAcknowledgement;

        chatTransferStatus.chatId  = this.getMsgId();
        chatTransferStatus.isRead =false;
        chatTransferStatus.isOnline =false;
        chatTransferStatus.appCredential = this.appCredential;

        socketResponse.responseData = chatTransferStatus;

        this.sendData(null,this.gson.toJson(socketResponse));


        System.out.println("********************************************");
        System.out.println("Send text msg : to " + chatTransferStatus.appCredential.user.firstName);
        System.out.println("Send text Object " + this.gson.toJson(socketResponse));
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

    public void sendChatAcknowledgement(int id,String tmpChatId,String chatId,boolean isRead,boolean isOline){
        Acknowledgement acknowledgement = new Acknowledgement();


        SocketResponse socketResponse = new SocketResponse();
        socketResponse.responseStat.tag = tag_ChatAcknowledgement;

        socketResponse.responseStat.chatId = chatId;

        acknowledgement.id = id;
        acknowledgement.tmpChatId = tmpChatId;
        acknowledgement.chatId  = chatId;
        acknowledgement.isRead =isRead;
        acknowledgement.isOnline =isOline;
        acknowledgement.appCredential = this.appCredential;

        socketResponse.responseData = acknowledgement;

        this.sendData(null,this.gson.toJson(socketResponse));


        System.out.println("********************************************");
        System.out.println("Send text msg : to " + acknowledgement.appCredential.user.firstName);
        System.out.println("Send text Object " + this.gson.toJson(socketResponse));
        System.out.println(" Offline text msg and obj null");
        System.out.println("********************************************");
    }
    private void sendChatPrivatePhotoAcknowledgement(int id,PrivateChatPhoto privateChatPhoto,boolean isRead,boolean isOnline){
        Acknowledgement acknowledgement = new Acknowledgement();


        SocketResponse socketResponse = new SocketResponse();
        socketResponse.responseStat.tag = tag_ChatPrivatePhotoAcknowledgement;

        socketResponse.responseStat.chatId = privateChatPhoto.chatId;

        acknowledgement.id = id;
        acknowledgement.tmpChatId =  privateChatPhoto.tmpChatId;
        acknowledgement.chatId  =  privateChatPhoto.chatId;
        acknowledgement.isRead =isRead;
        acknowledgement.isOnline =isOnline;
        acknowledgement.appCredential = this.appCredential;

        socketResponse.responseData = acknowledgement;

        this.sendData(null,this.gson.toJson(socketResponse));


        System.out.println("********************************************");
        System.out.println("Send text msg : to " + acknowledgement.appCredential.user.firstName);
        System.out.println("Send text Object " + this.gson.toJson(socketResponse));
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

        this.sendData(null,this.gson.toJson(socketResponse));


        System.out.println("********************************************");
        System.out.println("Send text msg : to " + acknowledgement.appCredential.user.firstName);
        System.out.println("Send text Object " + this.gson.toJson(socketResponse));
        System.out.println(" Offline text msg and obj null");
        System.out.println("********************************************");
    }
    private void sendError(String chatId,String msg){
        SocketResponse socketResponse = new SocketResponse();
        socketResponse.responseStat.tag = "error";
        socketResponse.responseStat.status = false;
        socketResponse.responseStat.chatId = chatId;
        socketResponse.responseStat.msg = msg;

        System.out.println("********************************************");
        System.out.println("Error Send text msg : to " + this.appCredential.user.firstName);
        System.out.println("Error Send text Object " + this.gson.toJson(socketResponse));
        System.out.println("********************************************");
        this.sendData(null,this.gson.toJson(socketResponse));

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
    private synchronized void sendData(AppCredential sender,String jsonStr){

        if(sender!=null){
            if(!this.isInContactList(sender)){
                if(!this.currentUnknownChatPersons.contains(sender)){
                    this.currentUnknownChatPersons.add(sender);
                }
            }
        }

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
    private boolean isInContactList(AppCredential appCredential){
        for(Contact contact : this.contacts){
            if(appCredential.id == contact.id){
                return true;
            }
        }
        return false;
    }
    private boolean checkForAuthentication(){
        if(!this.authintic){
            SocketResponse socketResponse = new SocketResponse();
            socketResponse.responseStat.tag = "error";
            socketResponse.responseStat.status = false;
            socketResponse.responseStat.msg = "Can not cast AuthCredential";
            this.sendData(null,this.gson.toJson(socketResponse));
            System.out.println("----------------------------------------------------------------------------------------------");
            System.out.println("checkForAuthentication : False : this.authintic : False : For " + this.appCredential.user.firstName+" "+this.appCredential.user.lastName);
            System.out.println("----------------------------------------------------------------------------------------------");
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
            if(this.output!=null)
                 this.output.close();
            if(this.input!=null)
                this.input.close();
            if(this.serviceSocket!=null)
                this.serviceSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}