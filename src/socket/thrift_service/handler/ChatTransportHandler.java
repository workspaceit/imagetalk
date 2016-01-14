package socket.thrift_service.handler;

import com.google.gson.Gson;
import helper.ImageHelper;
import model.AppLoginCredentialModel;
import model.ChatHistoryModel;
import model.datamodel.app.AuthCredential;
import model.datamodel.app.socket.SocketResponse;
import model.datamodel.app.socket.chat.ChatPhoto;
import model.datamodel.app.socket.chat.ChatVideo;
import model.datamodel.app.socket.chat.PrivateChatPhoto;
import model.datamodel.app.video.Videos;
import org.apache.thrift.TException;
import socket.BaseSocketController;
import socket.ServiceThread;
import socket.thrift_service.ChatTransport;
import socket.thrift_service.ResponseObj;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mi on 1/13/16.
 */
public class ChatTransportHandler implements ChatTransport.Iface {
    public Gson gson  = new Gson();
    @Override
    public String getToken(String accessToken) throws TException {
        System.out.println("At getToken");

        String token = null;
        try{
            AppLoginCredentialModel appLoginCredentialModel = new AppLoginCredentialModel();
            appLoginCredentialModel.setAccess_token(accessToken);
            AuthCredential authCredential = appLoginCredentialModel.getAuthincatedByAccessToken();
            if(authCredential.id>0 && authCredential.accessToken!=null && authCredential.accessToken!=""){
                token = BaseSocketController.getThriftToken(authCredential.id);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        System.out.println("Token :"+token);
        return token;
    }

    @Override
    public ResponseObj sendVideo(int appCredentialId, String token, String socketResponse, ByteBuffer bufferedByte, String fileName) throws TException {
        System.out.println("At send Video");
        ResponseObj responseObj = new ResponseObj();
        try{
            if(BaseSocketController.isThriftToken(appCredentialId,token)){
                byte[] b = new byte[bufferedByte.remaining()];
                bufferedByte.get(b);

                SocketResponse socketResp= this.gson.fromJson(socketResponse, SocketResponse.class);

                String dataObjStr = this.gson.toJson(socketResp.responseData);

                ChatVideo chatVideo =  this.gson.fromJson(dataObjStr, ChatVideo.class);
                chatVideo.video = ImageHelper.saveByteToChatVideo(b, chatVideo.appCredential.user.id, fileName);
                chatVideo.chatId = getMsgId();
                chatVideo.id = saveVideoInChatHistory(appCredentialId,chatVideo);
                socketResp.responseData = chatVideo;

                ServiceThread st = BaseSocketController.getServiceThread(appCredentialId);


                if(st!=null){
                    socketResp.responseStat.tag = ServiceThread.tag_chatVideo;
                    st.processPushBack(this.gson.toJson(socketResp));
                }
            }else{
                responseObj.status = false;
                responseObj.msg = "Token miss matched";
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return responseObj;
    }

    @Override
    public ResponseObj sendPicture(int appCredentialId, String token, String socketResponse, ByteBuffer bufferedByte, String fileName) throws TException {
        System.out.println("At send Picture");
        System.out.println("App Id : "+appCredentialId);
        System.out.println("token : "+token);
        System.out.println("FileName : "+fileName);

        ResponseObj responseObj = new ResponseObj();
        try{
            if(BaseSocketController.isThriftToken(appCredentialId,token)){
                byte[] b = new byte[bufferedByte.remaining()];
                bufferedByte.get(b);
                System.out.println("socketResponse : "+socketResponse);
                SocketResponse socketResp= this.gson.fromJson(socketResponse, SocketResponse.class);
                String dataObjStr = this.gson.toJson(socketResp.responseData);

                ChatPhoto chatPhoto =  this.gson.fromJson(dataObjStr,ChatPhoto.class);
                chatPhoto.pictures = ImageHelper.saveByteToChatPicture(b, chatPhoto.appCredential.user.id, fileName);
                chatPhoto.chatId = getMsgId();
                // Saving to database
                chatPhoto.id = savePhotoInChatHistory(appCredentialId,chatPhoto);

                socketResp.responseData = chatPhoto;

                ServiceThread st = BaseSocketController.getServiceThread(appCredentialId);



                if(st!=null){
                    System.out.println("Inside Socket");
                    socketResp.responseStat.tag = ServiceThread.tag_chatPhoto;
                    st.processPushBack(this.gson.toJson(socketResp));
                }else{
                    System.out.println("Socket Null miss matched");
                }
                getServiceObj();


            }else{
                responseObj.status = false;
                responseObj.msg = "Token miss matched";
                System.out.println("Token miss matched");
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return responseObj;
    }

    @Override
    public ResponseObj sendPrivatePhoto(int appCredentialId, String token, String socketResponse, ByteBuffer bufferedByte, String fileName) throws TException {
        System.out.println("At send PrivatePhoto");
        System.out.println("At send Picture");
        System.out.println("App Id : "+appCredentialId);
        System.out.println("token : "+token);
        System.out.println("FileName : "+fileName);

        ResponseObj responseObj = new ResponseObj();
        try{
            if(BaseSocketController.isThriftToken(appCredentialId,token)){
                byte[] b = new byte[bufferedByte.remaining()];
                bufferedByte.get(b);
                System.out.println("socketResponse : "+socketResponse);
                SocketResponse socketResp= this.gson.fromJson(socketResponse, SocketResponse.class);
                String dataObjStr = this.gson.toJson(socketResp.responseData);

                PrivateChatPhoto privateChatPhoto =  this.gson.fromJson(dataObjStr,PrivateChatPhoto.class);
                privateChatPhoto.pictures = ImageHelper.saveByteToChatPrivatePicture(b, privateChatPhoto.appCredential.user.id, fileName);
                privateChatPhoto.chatId = getMsgId();
                // Saving to database
                privateChatPhoto.id = savePrivatePhotoInChatHistory(appCredentialId, privateChatPhoto);

                socketResp.responseData = privateChatPhoto;

                ServiceThread st = BaseSocketController.getServiceThread(appCredentialId);

                if(st!=null){
                    socketResp.responseStat.tag = ServiceThread.tag_chatPhoto;
                    st.processPushBack(this.gson.toJson(socketResp));
                }



            }else{
                responseObj.status = false;
                responseObj.msg = "Token miss matched";
                System.out.println("Token miss matched");
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return responseObj;
    }

    @Override
    public ResponseObj sendVoice(int appCredentialId, String token, String socketResponse, ByteBuffer bufferedByte, String fileName) throws TException {
        System.out.println("At send Voice");

        return null;
    }

    @Override
    public ByteBuffer getVideo(int appCredentialId, String token, int id) throws TException {
        System.out.println("At get Video");

        return null;
    }

    @Override
    public ResponseObj expireMyToken(int appCredentialId, String token) throws TException {
        System.out.println("At expire My Token");
        ResponseObj responseObj = new ResponseObj();
        try{
            BaseSocketController.removeThriftToken(appCredentialId,token);
        }catch (Exception ex){
            ex.printStackTrace();
            responseObj.status = false;
        }
        return responseObj;
    }

    public long savePhotoInChatHistory(int fromId,ChatPhoto chatPhoto){
        ChatHistoryModel chatHistory = new ChatHistoryModel();
        chatHistory.setChat_id(chatPhoto.chatId);
        chatHistory.setTo(chatPhoto.appCredential.id);
        chatHistory.setType(ChatHistoryModel.type_chatPic);
        chatHistory.setMedia_path(gson.toJson(chatPhoto.pictures));
        chatHistory.setFrom(fromId);
        chatHistory.setChat_text("");
        return chatHistory.insert();
    }
    private long savePrivatePhotoInChatHistory(int fromId,PrivateChatPhoto privateChatPhoto){

        ChatHistoryModel chatHistory = new ChatHistoryModel();
        chatHistory.setChat_id(privateChatPhoto.chatId);
        chatHistory.setTo(privateChatPhoto.appCredential.id);
        chatHistory.setType(ChatHistoryModel.type_chatPrivatePhoto);
        chatHistory.setExtra(gson.toJson(privateChatPhoto));
        chatHistory.setMedia_path(gson.toJson(privateChatPhoto.pictures));
        chatHistory.setFrom(fromId);
        chatHistory.setChat_text("");
        return chatHistory.insert();
    }
    private long saveVideoInChatHistory(int fromId,ChatVideo chatVideo){

        ChatHistoryModel chatHistoryModel = new ChatHistoryModel();
        chatHistoryModel.setChat_id(chatVideo.chatId);
        chatHistoryModel.setTo(chatVideo.appCredential.id);
        chatHistoryModel.setType(ChatHistoryModel.type_chatVideo);
        chatHistoryModel.setMedia_path(gson.toJson(chatVideo.video));
        chatHistoryModel.setFrom(fromId);
        chatHistoryModel.setChat_text("");
        return chatHistoryModel.insert();
    }

    public String getMsgId(){

        return new SimpleDateFormat("yyyyMMddHHmmssSSSSSS").format(new Date());
    }
    public void getServiceObj(){
        BaseSocketController.printAllKey();
    }
}
