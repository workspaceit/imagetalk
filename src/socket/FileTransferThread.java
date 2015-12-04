package socket;

import com.google.gson.Gson;
import helper.DateHelper;
import helper.ImageHelper;
import model.AppLoginCredentialModel;
import model.ChatHistoryModel;
import model.datamodel.app.AppCredential;
import model.datamodel.app.AuthCredential;
import model.datamodel.app.socket.Acknowledgement;
import model.datamodel.app.socket.SocketResponse;
import model.datamodel.app.socket.chat.ChatPhoto;
import model.datamodel.app.socket.chat.ChatTransferStatus;
import model.datamodel.app.socket.chat.TextChat;
import model.datamodel.app.video.Videos;
import model.datamodel.photo.Pictures;
import org.apache.commons.lang3.ArrayUtils;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class FileTransferThread extends Thread {
    final static String tag_authentication = "authentication";
    final static String tag_textChat = "textchat";
    final static String tag_ChatAcknowledgement = "chat_acknowledgement";
    final static String tag_chatPhoto = "chatphoto_transfer";
    final static String tag_chatVideo = "chatvideo_transfer";
    final static String tag_chatVideoProto = "chatvideo_transfer_proto";

    private Socket serviceSocket;
    private boolean authintic;
    private Gson gson;
    private AppCredential appCredential;
    private String id;
    private DataInputStream input;
    private PrintStream output;
    private SocketResponse socketResponse;
    private InputStream fileInput;
    private  DataInputStream dIn;

    public FileTransferThread(Socket serviceSocket) {
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
           this.dIn = new DataInputStream(serviceSocket.getInputStream());
            while(!serviceSocket.isClosed()){
                System.out.println("********* Start Convertion *********");
                Object obj = this.getObject();
                System.out.println(obj.getClass());
                System.out.println(obj.toString());
//                HashMap<String,Object>  sr = (HashMap<String,Object>)obj;
                //Videos videos = ImageHelper.saveByteToChatVideo((byte[]) sr.get("file"), 420);
//                System.out.println("Done Convertion");

//                DataOutputStream dIn = new DataOutputStream(this.serviceSocket.getOutputStream());
//                byte[] resp = ImageHelper.serialize(this.gson.toJson(sr.get("auth")));
//                dIn.writeInt(resp.length);
//                dIn.write(resp );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("");
        System.out.println("");
        System.out.println("");
    }

    private Object getObject(){


        Object respObj = new Object();
        try {

//            Gson gson = new Gson();
//            Object s = gson.fromJson(dIn.readUTF(), Object.class);
//            System.out.println(s);

            System.out.println("available "+dIn.available());
            byte[] message = new byte[dIn.available()]; // dIn.available()


            while(dIn.available() > 0){
                System.out.println("at 01");
                dIn.readFully(message); // read the message , 0, message.length
                System.out.println("at 02");

                byte[] one = new byte[]{-84,-19,0,5,115,114,0};

                byte[] concatBytes = ArrayUtils.addAll(one, message);
                ByteArrayInputStream b = new ByteArrayInputStream(concatBytes);
                System.out.println("at 03");
                System.out.println("Binary Array Start");
//                for(int i=0;i<concatBytes.length;i++){
//                    System.out.println(concatBytes[i]);
//                }
                System.out.println("Before ObjectInputStream");


                ObjectInputStream o = new ObjectInputStream(b);

                System.out.println("After ObjectInputStream");
                try {
                    System.out.println("Before Obj");
                    respObj = o.readObject();
                    System.out.println("After Obj");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

     //   HashMap<String,AuthCredential> asc = (HashMap<String,AuthCredential>)respObj;

        System.out.println(respObj.getClass());
        System.out.println(respObj.toString());
        System.out.println("Before Return");
        return respObj;

//        Object respObj = new Object();
//        try {
//            byte[] tmpByte= new byte[16*12004];
//            int length= dIn.read(tmpByte);//= dIn.readByte();                    // read length of incoming message
//
//
//
//            System.out.println("length :"+ length);
//            System.out.println("length :" + dIn.available());
//            if(length>0) {
//                byte[] message = new byte[length];
//                dIn.readFully(message, 0, message.length); // read the message
//                ByteArrayInputStream b = new ByteArrayInputStream(message);
//                ObjectInputStream o = new ObjectInputStream(b);
//                try {
//                    respObj = o.readObject();
//
//                } catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//         System.out.println(respObj.getClass());
//         System.out.println(respObj.toString());
//        return respObj;
    }
    private void transferFile(){

    }
}