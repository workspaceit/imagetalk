package socket;

import model.datamodel.app.AppCredential;
import model.datamodel.app.AuthCredential;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mi on 10/29/15.
 */
public class BaseSocketController {
    private static HashMap<Integer,ServiceThread> serviceThreads = new HashMap<Integer,ServiceThread>();
    private static ServerSocket fileTransferSocket;
    public synchronized static ServiceThread getServiceThread(int appCredentialId){
        return serviceThreads.get(appCredentialId);
    }
    public synchronized static void putServiceThread(int appCredentialId,ServiceThread serviceThread){
        serviceThreads.put(appCredentialId, serviceThread);
    }
    public synchronized static void removeServiceThread(int appCredentialId){
        serviceThreads.remove(appCredentialId);
    }
    public synchronized static void setFileTransferSocket(ServerSocket fts ){
        fileTransferSocket = fts;
    }
    public synchronized static ServerSocket getFileTransferSocket(){
        return fileTransferSocket;
    }

}