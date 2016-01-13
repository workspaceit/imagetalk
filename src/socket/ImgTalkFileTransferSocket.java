package socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by mi on 10/26/15.
 */
public class ImgTalkFileTransferSocket {

    BaseSocketController baseSocketController;
    public void startServer(){


        int postNumber = 9027;
        boolean runServer =true;
        int count = 1;

        try {

            ServerSocket fileTransferSocket = new ServerSocket(postNumber);

            System.out.println("Waiting For request");
            Socket serviceSocket = fileTransferSocket.accept();

            System.out.println("Request arrived");
            FileTransferThread fileTransferThread = new FileTransferThread(serviceSocket);

            System.out.println("count :"+count);
            count++;
            fileTransferThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }finally {

        }
    }

    public static void main(String args[]){

        ImgTalkFileTransferSocket imgTalkServerSocket = new ImgTalkFileTransferSocket();
        imgTalkServerSocket.startServer();
    }
}
