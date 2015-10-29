package socket;

import com.google.gson.Gson;
import helper.DateHelper;
import model.datamodel.app.AppCredential;
import model.datamodel.app.socket.TextChat;
import socket.SocketReponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class ServiceThread extends Thread {
    private Socket serviceSocket;
    private Gson gson;
    private AppCredential appCredential;
    BufferedReader input;
    PrintStream output;


    public ServiceThread(Socket serviceSocket ) {
        super();

        this.appCredential = new AppCredential();
        this.serviceSocket = serviceSocket;
        this.gson = new Gson();

        this.input = null;
        this.output = null;

    }

    @Override
    public void run() {
        try {
            this.input = new BufferedReader(new InputStreamReader(this.serviceSocket.getInputStream()));
            this.output =  new PrintStream(this.serviceSocket.getOutputStream());
            int count = 1;



            while (!this.serviceSocket.isClosed()) {
                try{

                    String recvStr = input.readLine();
                    if (recvStr != null) {

                        ServiceThread st = BaseSocketController.serviceThreads.get(Integer.parseInt(recvStr));

                        st.sendTextMessage("Hi there Online " + this.isOnline());
                        this.sendAcknoledgement();
                        System.out.println(recvStr);

                    } else {
                        output.close();
                        input.close();
                        this.serviceSocket.close();
                    }
                    count++;
                } catch (IOException e) {
                    output.close();
                    input.close();
                    this.serviceSocket.close();

                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public boolean isOnline(){
       return !this.serviceSocket.isClosed();
    }
    public synchronized boolean sendTextMessage(String text){
        SocketReponse socketReponse = new SocketReponse();

        socketReponse.responseStat.incommingTag = "text_chat";
        TextChat textChat = new TextChat();

        textChat.id = this.appCredential.id;
        textChat.text =text;
        textChat.createdDate = DateHelper.getUtcDateProcessedTimeStamp();

        socketReponse.responseData = textChat;

        this.output.println(this.gson.toJson(socketReponse));

        return false;
    }
    public synchronized boolean sendAcknoledgement(){
        SocketReponse socketReponse = new SocketReponse();

        socketReponse.responseStat.incommingTag = "acknoledgement";
        TextChat textChat = new TextChat();

        textChat.id = this.appCredential.id;
        textChat.text ="online";
        textChat.createdDate = DateHelper.getUtcDateProcessedTimeStamp();

        socketReponse.responseData = textChat;

        this.output.println(this.gson.toJson(socketReponse));
        return false;
    }
}