package socket;

import model.datamodel.app.AppCredential;
import model.datamodel.app.AuthCredential;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mi on 10/29/15.
 */
public class BaseSocketController {
    private static HashMap<Integer,ServiceThread> serviceThreads = new HashMap<Integer,ServiceThread>();
    public synchronized static ServiceThread getServiceThread(int appCredentialId){
        return serviceThreads.get(appCredentialId);
    }
    public synchronized static void putServiceThread(int appCredentialId,ServiceThread serviceThread){
        serviceThreads.put(appCredentialId, serviceThread);
    }
}