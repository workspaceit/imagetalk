package socket.chat;

import com.google.gson.Gson;
import model.AppLoginCredentialModel;
import model.datamodel.app.Contact;
import model.datamodel.app.socket.SocketResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by mi on 10/26/15.
 */
public class ClientSocket {
    Gson gson;
    public void getConnected(){

        String hostName = "192.168.1.27";
        int portNumber = 9091;

        Socket clientSocket = null;
        this.gson = new Gson();
        try {
            clientSocket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            int count = 0;

            SocketResponse socketResponse = new SocketResponse();
            AppLoginCredentialModel appLoginCredentialModel = new AppLoginCredentialModel();



            socketResponse.responseStat.tag = "authentication";
            appLoginCredentialModel.setId(32);
            socketResponse.responseData =  appLoginCredentialModel.getAppCredentialById();

            out.println(this.gson.toJson(socketResponse));
            Thread.sleep(3000);
            Contact contact =  new Contact();
            contact.id = 1;
            socketResponse.responseStat.tag="textchat";
            socketResponse.responseData = contact;
            out.println(this.gson.toJson(socketResponse));
            while(!clientSocket.isClosed()){
                Thread.sleep(3000);
                //out.println("1");
                String recvStr = in.readLine();
                if(recvStr!=null){
                    System.out.println("recvStr : "+recvStr);
                    System.out.println(this.gson.fromJson(recvStr, SocketResponse.class));

                }else {
                    in.close();
                }
                count++;
               // out.println(this.gson.toJson(socketResponse));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void main(String args[]){
        ClientSocket clientSocket = new ClientSocket();
        clientSocket.getConnected();
    }
}
