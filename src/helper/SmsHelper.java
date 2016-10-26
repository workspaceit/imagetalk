package helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by mi on 10/26/16.
 */
public class SmsHelper {
    private static  final  String userName="wsit_developer";
    private static  final  String password="GKeGPVedBecTgb";
    private static  final  String apiId = "3630432";
    private static  final  String url = "http://api.clickatell.com/http/sendmsg";

    public static void sendRegistrationCode(String to, String code ) throws IOException {
    //api.clickatell.com/http/sendmsg?user=wsit_developer&password=GKeGPVedBecTgb&api_id=3630432&to=8801723810272&text=Message

        String msg = "Your ImageTalk confirmation token : "+code;
        URL obj = new URL(url+"?user="+userName+"&password="+password+"&api_id="+apiId+"&to="+to+"&text="+msg);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
       // con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());

    }
    public static void main(String args[]){
        try {
            SmsHelper.sendRegistrationCode("8801670396449","4567");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
