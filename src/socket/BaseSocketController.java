package socket;


import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.util.HashMap;

/**
 * Created by mi on 10/29/15.
 */
import java.security.SecureRandom;
import java.util.Map;
import java.util.Set;


public class BaseSocketController {
    public static HashMap<Integer,ServiceThread> serviceThreads = new HashMap<Integer,ServiceThread>();
    public static HashMap<Integer,String> thriftToken = new HashMap<Integer,String>();


    public synchronized static String getThriftToken(int appCredential){
        String token = generateToken();
        token = token.trim();
        System.out.println("AppCId From getThriftToken : " + appCredential);
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
        token = token.trim();
        System.out.println("AppCId From isThriftToken : " + appCredential);
        System.out.println("Token : " + token);
        if(thriftToken.containsKey(appCredential)){
            String tempToken = thriftToken.get(appCredential);
            System.out.println("Token tempToken: " + tempToken);
            if(tempToken!=null && tempToken!=""){
                System.out.println("01");
                if(tempToken.equals(token)){
                    System.out.println("02");
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
    public static ServiceThread getServiceThread(int appCredentialId){
        printAllKey();
        System.out.println("Getting AppCID :" + appCredentialId);
        return BaseSocketController.serviceThreads.get(appCredentialId);

    }
    public static void putServiceThread(int appCredentialId,ServiceThread serviceThread){
        System.out.println("Putting AppCID :"+appCredentialId);
        BaseSocketController.serviceThreads.put(appCredentialId, serviceThread);
    }
    public synchronized static void removeServiceThread(int appCredentialId){
        serviceThreads.remove(appCredentialId);
    }
    public static void printAllKey(){
        System.out.println("Key :Printer");

        for ( int key : serviceThreads.keySet() ) {
            System.out.println( key );
        }
    }
}