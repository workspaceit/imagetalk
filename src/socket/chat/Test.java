package socket.chat;

import com.google.gson.Gson;
import model.datamodel.app.AuthCredential;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * Created by mi on 12/4/15.
 */
public class Test {
    public static void main(String args[]){
        Object respObj = new Object();
        try {

            String test="123456789";

            HashMap<String,AuthCredential> a = new HashMap<String,AuthCredential>();
            Integer intget = new Integer("12");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream( baos );
            oos.writeObject(a);
   //         oos.flush();
//            byte[] bytes = baos.toByteArray();
         //   String str = new String(test.getBytes(), StandardCharsets.UTF_8);
            byte[] message =  baos.toByteArray();//str.getBytes();////test.getBytes("iso-8859-1"); // dIn.available()

//            Gson gson = new Gson();
//            Object s = gson.fromJson(test, String.class);
//            System.out.println(s);


            ByteArrayInputStream b = new ByteArrayInputStream(message);
            System.out.println("Start Of Array");
            for(int i=0;i<message.length;i++){
                System.out.println(message[i]);
            }
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

        } catch (IOException e) {
            e.printStackTrace();
        }



        System.out.println(respObj.getClass());
        System.out.println(respObj.toString());
        System.out.println("Before Return");


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
}
