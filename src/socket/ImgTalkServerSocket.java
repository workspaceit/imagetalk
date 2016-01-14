package socket;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by mi on 10/26/15.
 */
public class ImgTalkServerSocket {

    public void startServer(){
        System.out.println("OK");

        int postNumber = 9025;
        boolean runServer =true;
        int count = 1;

        try {
            ServerSocket chatImgTalkServerSocket = new ServerSocket(postNumber);

            while(runServer) {
                System.out.println("Waiting for request");
                Socket serviceSocket = chatImgTalkServerSocket.accept();
                System.out.println("Request arrived");

                ServiceThread serviceThread = new ServiceThread(serviceSocket);

                System.out.println("count :"+count);
                count++;
                serviceThread.start();
            }
            chatImgTalkServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();

        }finally {

        }
    }
    public static void main(String args[]){

        ImgTalkServerSocket imgTalkServerSocket = new ImgTalkServerSocket();
        imgTalkServerSocket.startServer();
    }
}
