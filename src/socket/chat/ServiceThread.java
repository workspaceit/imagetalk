package socket.chat;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

class ServiceThread extends Thread {
    private Socket serviceSocket;
    private Gson gson;
    public ServiceThread(Socket serviceSocket ) {
        super();
        this.serviceSocket = serviceSocket;
        this.gson = new Gson();
    }

    @Override
    public void run() {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(this.serviceSocket.getInputStream()));
            PrintStream output = new PrintStream(this.serviceSocket.getOutputStream());
            int count = 0;
            while (!this.serviceSocket.isClosed()) {
                try{

                    String recvStr = input.readLine();
                    if (recvStr != null) {
                        System.out.println(recvStr);
                        output.println("Hi "+count);
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
}