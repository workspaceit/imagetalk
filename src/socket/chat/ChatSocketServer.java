package socket.chat;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by mi on 10/26/15.
 */
public class ChatSocketServer {
    public void startServer(){
        System.out.println("OK");

        int postNumber = 9091;
        boolean runServer =true;
        try {
            ServerSocket chatServerSocket = new ServerSocket(postNumber);
            while(runServer) {
                System.out.println("Waiting for request");
                Socket serviceSocket = chatServerSocket.accept();
                System.out.println("Request arrived");
                class StartThread extends Thread {
                    @Override
                    public void run() {
                        try {
                            BufferedReader input = new BufferedReader(new InputStreamReader(serviceSocket.getInputStream()));
                            PrintStream output = new PrintStream(serviceSocket.getOutputStream());
                            while (true) {
                                String recvStr = input.readLine();
                                if (recvStr != null) {
                                    System.out.println(recvStr);
                                    output.println("Hi");
                                } else {
                                    input.close();
                                }

                            }

                            //
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
                new StartThread().run();
            }
            chatServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {

        }
    }
    public static void main(String args[]){

        ChatSocketServer chatSocketServer = new ChatSocketServer();
        chatSocketServer.startServer();
    }
}
