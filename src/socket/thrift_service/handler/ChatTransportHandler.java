package socket.thrift_service.handler;

import com.google.gson.Gson;
import helper.ImageHelper;
import model.AppLoginCredentialModel;
import model.ChatHistoryModel;
import model.datamodel.app.AuthCredential;
import model.datamodel.app.socket.SocketResponse;
import model.datamodel.app.socket.chat.ChatVideo;
import model.datamodel.app.video.Videos;
import org.apache.thrift.TException;
import socket.BaseSocketController;
import socket.ServiceThread;
import socket.thrift_service.ChatTransport;
import socket.thrift_service.ResponseObj;

import java.nio.ByteBuffer;

/**
 * Created by mi on 1/13/16.
 */
public class ChatTransportHandler implements ChatTransport.Iface {
    public Gson gson  = new Gson();
    @Override
    public String getToken(String accessToken) throws TException {
        String token = null;
        AppLoginCredentialModel appLoginCredentialModel = new AppLoginCredentialModel();
        appLoginCredentialModel.setAccess_token(accessToken);

        AuthCredential authCredential = appLoginCredentialModel.getAuthincatedByAccessToken();

        if(authCredential.id>0 && authCredential.accessToken!=null && authCredential.accessToken!=""){
            token = BaseSocketController.getThriftToken(authCredential.id);
        }

        return token;
    }

    @Override
    public ResponseObj sendVideo(int appCredentialId, String token, String socketResponse, ByteBuffer bufferedByte, String fileName) throws TException {
        ResponseObj responseObj = new ResponseObj();

        if(BaseSocketController.isThriftToken(appCredentialId,token)){
            byte[] b = new byte[bufferedByte.remaining()];
            bufferedByte.get(b);

            SocketResponse socketResp= this.gson.fromJson(socketResponse, SocketResponse.class);
            String dataObjStr = this.gson.toJson(socketResp.responseData);

            ChatVideo chatVideo =  this.gson.fromJson(dataObjStr,ChatVideo.class);
            chatVideo.video = ImageHelper.saveChatVideo(b,chatVideo.appCredential.id,fileName);

            socketResp.responseData = chatVideo;

            ServiceThread st = BaseSocketController.getServiceThread(appCredentialId);
            st.processPushBack(this.gson.toJson(socketResp));
        }else{
            responseObj.status = false;
            responseObj.msg = "Token miss matched";
        }
        return responseObj;
    }

    @Override
    public ResponseObj sendPicture(int appCredentialId, String token, String socketResponse, ByteBuffer bufferedByte, String fileName) throws TException {
        return null;
    }

    @Override
    public ResponseObj sendVoice(int appCredentialId, String token, String socketResponse, ByteBuffer bufferedByte, String fileName) throws TException {
        return null;
    }

    @Override
    public ResponseObj expireMyToken(int appCredentialId, String token) throws TException {

        BaseSocketController.removeThriftToken(appCredentialId,token);
        return null;
    }
}
