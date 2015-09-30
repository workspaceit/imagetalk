/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gps_socket;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import model.LoginModel;
import model.TeamMemberModel;
import model.UserInfModel;
import model.datamodel.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mi
 */
public class CabGuardServerSocket {
    public Socket serverSocket;
    public PrintWriter out; 
    public BufferedReader in;
    public boolean readCommand;
    public String location;
    Gson gson;
    public SocketLogin sLogin;
    public SocketResponse socketResponse;
    private Object socketLock;
    public CabGuardServerSocket(Socket socket){
        socketLock = new Object();
        this.readCommand = true;
        this.location = "";
        this.socketResponse = new SocketResponse();
        this.gson = new Gson();
        this.sLogin = new SocketLogin();
        try {
            this.serverSocket = socket;
            this.out = new PrintWriter(socket.getOutputStream(), true);
            //BufferedReader authIn = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
        } catch (UnknownHostException ex) {
            System.out.println(ex);
        } catch (IOException ex) {
            System.out.println(ex);
        }
       
    }
    public void SendJsonString(String msg){
        //synchronized(socketLock) {
            if (this.isConnected()) {
                System.out.println("AT SEND JSON STR"+msg);
                this.out.println(msg);
            }
        //}
    }
    public boolean isConnected() {
        synchronized(socketLock) {
            return (serverSocket != null && !serverSocket.isClosed());
        }
    }
    public boolean isAuthentic(){
        try {
            BufferedReader authIn = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            String inComingMsg = authIn.readLine();
          //  String inComingMsg="";
            String line;
            System.out.println("BEFORE REDING TEXT :"+inComingMsg);
//            boolean checkForString=true;
//            while (checkForString){
//                if((line = authIn.readLine()) != null){
//                    System.out.println("LINE:"+line);
//                    inComingMsg +=line;
//                    if(inComingMsg.substring(inComingMsg.length()-1)=="\n"){
//                        checkForString = false;
//                    }
//                }else {
//                    checkForString = false;
//                }
//
//            }

           // inComingMsg = authIn.readLine();
            System.out.println("AFTER READING TEXT :"+inComingMsg);
            SocketResponse socketResponse = gson.fromJson(inComingMsg, SocketResponse.class);
            if(socketResponse.responseStat.command.equals("AUTHENTICATION")){
                try{
                    this.sLogin = this.gson.fromJson(this.gson.toJson(socketResponse.responseData),SocketLogin.class);
                    LoginModel loginModel = new LoginModel();

                    if(loginModel.isValidLoginByAccessToken(this.sLogin.access_token)){
                        return true;
                    }else{
                        return false;
                    }
                }catch (Exception e){
                    return false;
                }

            }
        }catch (IOException ioe){
            System.out.println(ioe);
            return false;
        }

        return false;
    }
    public void setLive(){
        new ReadCommandThread().start();
    }
    public void saySomeThing(String msg){
        this.out.println(msg);
    }
    class ReadCommandThread extends Thread{
        @Override
        public void run(){
            try {
                //out.println("HI There!!!\n");
                //System.out.println("Saying Hi there");
                int nullIncounter = 0;
                while(readCommand){
                    in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
                    //saySomeThing("HEy!! Whats going on\n");

                    String inComingMsg="";
                    String line;
                    System.out.println("BEFORE REDING TEXT :"+inComingMsg);
                    inComingMsg = in.readLine();
                   /* while ((line = in.readLine()) != null){
                        System.out.println("LINE:"+line);
                        inComingMsg +=line;
                    }
*/
                    System.out.println("AFTER READING TEXT :"+inComingMsg);

                    if(inComingMsg==null || inComingMsg==""){
                        nullIncounter++;
                        if(nullIncounter>10){
                            readCommand=false;
                        }
                        continue;
                    }else{
                        nullIncounter = 0;
                    }

                    SocketResponse socketResponse = gson.fromJson(inComingMsg,SocketResponse.class);
                    System.out.println("COMMAND :"+socketResponse.responseStat.command);

                    if(socketResponse.responseStat.command.equals("GET_TEAM_MEMBER_LOCATION")){
                        try {
                            System.out.println(gson.toJson(socketResponse.responseData));
                            int i = 0;
                            String locationStr = "";
                            ArrayList<LocationSocket> locationSocketList = new ArrayList<LocationSocket>();
                            JsonObject jObj = gson.toJsonTree(socketResponse.responseData).getAsJsonObject();
                            //Login sender = gson.fromJson(jObj.get("sender").getAsJsonObject(), Login.class);
                            Team team = gson.fromJson(jObj.get("team").getAsJsonObject(), Team.class);



                            TeamMemberModel teamMemberModel = new TeamMemberModel();
                            teamMemberModel.team_id = team.id;
                            ArrayList<TeamMember> teamMemberList =  teamMemberModel.getAllByTeamId();
                            System.out.println("TEAM  ID" + team.id);
                            System.out.println("TEAM MEMBER SIZE " + teamMemberList.size());
                            UserInfModel userInfModel = new UserInfModel();
                            if(!socketResponse.responseStat.onlyReceiverMember){
                                for(TeamMember tm : teamMemberList){
                                    LocationSocket locationSocket = new LocationSocket();
                                    locationSocket.receiver.u_id = tm.id;
                                    locationSocket.sender =sLogin;
                                    locationSocket.receiver.user = userInfModel.getById(tm.id);
                                    locationSocketList.add(locationSocket);
                                    System.out.println("TEAM MEMBER " + tm.id);
                                }
                            }else{
                                LocationSocket locationSocket = new LocationSocket();
                                locationSocket.sender = sLogin;
                                SocketLogin receiver = gson.fromJson(jObj.get("receiver").getAsJsonObject(), SocketLogin.class);
                                locationSocket.receiver = receiver;
                                locationSocketList.add(locationSocket);
                            }




                            for (LocationSocket ls : locationSocketList) {
                                SocketResponse sr = new SocketResponse();

                                System.out.println("ls.receiver.u_id " + ls.receiver.u_id);
                                CabGuardServerSocket cs = CentralSocketController.clientSocketList.get(ls.receiver.u_id);

                                if(cs==null){
                                    System.out.println("CS IS NULL");
                                    CentralSocketController.clientSocketList.remove(ls.receiver.u_id);
                                    continue;
                                }

                                if(ls.receiver.u_id == sLogin.u_id){
                                    continue;
                                }
                                ls.team = team;
                                System.out.println("GIVE_LOCATION TO : " + ls.receiver.u_id);
                                sr.responseStat.command = "GIVE_LOCATION";
                                sr.responseStat.onlyReceiverMember = socketResponse.responseStat.onlyReceiverMember;
                                sr.responseData = ls;
                                System.out.println("isConnected : " + ls.receiver.u_id);
                                if(cs.isConnected()){
                                    System.out.println("Sendind GIVE_LOCATION TO : " + ls.receiver.u_id);
                                    cs.SendJsonString(gson.toJson(sr));
                                }

                            }
                        }catch(Exception  ex){
                            System.out.println(ex);
                            continue;
                        }
                    }else if(socketResponse.responseStat.command.equals("BROADCAST_LOCATION")){
                        try {
                            System.out.println(gson.toJson(socketResponse.responseData));
                            LocationSocket locationSocket = new LocationSocket();
                            JsonObject jObj = gson.toJsonTree(socketResponse.responseData).getAsJsonObject();
                            SocketLogin receiver = gson.fromJson(jObj.get("receiver").getAsJsonObject(), SocketLogin.class);
                            SocketLogin sender = gson.fromJson(jObj.get("sender").getAsJsonObject(), SocketLogin.class);

                            CabGuardServerSocket cs = CentralSocketController.clientSocketList.get(receiver.u_id);
                            SocketResponse sr = new SocketResponse();
                            sr.responseStat.command = "INCOMING_LOCATION";
                            locationSocket.sender = sender;
                            sr.responseData = locationSocket;
                            if(cs.isConnected()) {
                                cs.SendJsonString(gson.toJson(sr));
                                System.out.println("INCOMING_LOCATION");
                                System.out.println("Sending GPS :" + receiver.email);
                                System.out.print("JSON DATA :");
                                System.out.println(jObj.get("sender").getAsJsonObject());
                                System.out.print("JSON DATA :");
                                System.out.println(gson.toJson(sr));
                            }
                        }catch (Exception ex){
                            System.out.println(ex);
                            continue;
                        }
                    }
                }
            }
            catch (IOException ex) {
                Logger.getLogger(CabGuardServerSocket.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}