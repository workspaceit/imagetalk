package socket.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by mi on 10/26/15.
 */
public class ClientSocket {
    public void getConnected(){

        String hostName = "192.168.1.27";
        int portNumber = 9091;

        Socket clientSocket = null;
        try {
            clientSocket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out.println("HELLO");
            out.println("HELLO");
            out.println("HELLO");
            out.println("HELLO");
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String args[]){
        ClientSocket clientSocket = new ClientSocket();
        clientSocket.getConnected();
    }
}
