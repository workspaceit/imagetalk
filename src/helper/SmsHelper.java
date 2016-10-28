package helper;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by mi on 10/26/16.
 */
public class SmsHelper {
    private static  final  String userName="AC19bb7644b44dec9d088a8dd07d124f6e";
    private static  final  String password="2756f69b55b7f3a6d044afbcb5aa8e6f";
    private static  final  String url = "https://api.twilio.com/2010-04-01/Accounts/AC19bb7644b44dec9d088a8dd07d124f6e/Messages";

    public static void sendRegistrationCode(String to, String code ) throws IOException {

        if(!to.contains("+")){
            to = "+"+to;
        }
        String msg = "Your ImageTalk confirmation token : "+code;
        URL obj = new URL(url);
        String encoded = Base64.getEncoder().encodeToString((userName + ":" + password).getBytes(StandardCharsets.UTF_8));
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestProperty("Authorization", "Basic "+encoded);
        StringBuilder postData = new StringBuilder();
        Map<String,Object> params = new LinkedHashMap<>();

        params.put("To",to);
        params.put("From","+18559798993");
        params.put("Body",msg);

        for (Map.Entry<String,Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        // optional default is GET
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.getOutputStream().write(postDataBytes);
        //add request header
       // con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("Sending 'GET' request to URL : " + url);
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
            SmsHelper.sendRegistrationCode("toNUmber","Code");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
