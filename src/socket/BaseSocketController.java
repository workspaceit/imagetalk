package socket;


import java.math.BigInteger;
import java.util.HashMap;

/**
 * Created by mi on 10/29/15.
 */
import java.security.SecureRandom;


public class BaseSocketController {
    private static HashMap<Integer,ServiceThread> serviceThreads = new HashMap<Integer,ServiceThread>();
    private static HashMap<Integer,String> thriftToken = new HashMap<Integer,String>();
    public synchronized static String getThriftToken(int appCredential){
        String token = generateToken();
        thriftToken.put(appCredential, token);
        return token;
    }
    public synchronized static boolean removeThriftToken(int appCredential, String token ){

        if(isThriftToken(appCredential,token)){
            thriftToken.remove(appCredential);
            return true;
        }
        return false;
    }
    public synchronized static boolean isThriftToken(int appCredential,String token){
        boolean status = false;

        if(thriftToken.containsKey(appCredential)){
            String tempToken = thriftToken.get(appCredential);
            if(tempToken!=null && tempToken!=""){
                if(tempToken == token){
                    status = true;
                }
            }
        }

        return status;
    }
    private static String generateToken(){
        SecureRandom random = new SecureRandom();
        String token = new BigInteger(130, random).toString(32);

        return token;
    }
    public synchronized static ServiceThread getServiceThread(int appCredentialId){
        return serviceThreads.get(appCredentialId);
    }
    public synchronized static void putServiceThread(int appCredentialId,ServiceThread serviceThread){
        serviceThreads.put(appCredentialId, serviceThread);
    }
    public synchronized static void removeServiceThread(int appCredentialId){
        serviceThreads.remove(appCredentialId);
    }
}