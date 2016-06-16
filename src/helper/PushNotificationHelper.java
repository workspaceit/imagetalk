package helper;

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import model.UserInfModel;
import model.WallPostModel;
import model.WalletModel;
import model.datamodel.app.User;
import model.datamodel.app.WallPost;

import javax.jws.soap.SOAPBinding;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

/**
 * Application Name : ImageTalk
 * Package Name     : helper
 * Author           : Abu Bakar Siddique
 * Email            : absiddique.live@gmail.com
 * Created Date     : 6/7/16
 */
public class PushNotificationHelper {

    public static String certificatePath = "/home/touch/Projects/j2ee/ImageTalk/src/controller/service/src/imagetalk.p12";

    public static String alertBody;
    public static String deviceToken;
    public static int badgeNo;

    public void  likeNotification(int wallpostId,String likerName){
        WallPostModel wallPostModel = new WallPostModel();
        wallPostModel.setId(wallpostId);

        WallPost wallPost = new WallPost();
        wallPost = wallPostModel.getById();

        UserInfModel userInfModel = new UserInfModel();
        User user= new User();

        userInfModel.setId(wallPost.owner.user.id);

        user = userInfModel.getById();

        String deviceId = user.deviceId;

        if(deviceId == "" || deviceId ==null)
        {
            return;
        }

        PushNotificationHelper.alertBody = "Your post is liked by "+likerName;
        PushNotificationHelper.badgeNo = 1;

        try{
            ApnsService service =
                    APNS.newService()
                            .withCert(PushNotificationHelper.certificatePath, "wsit97480")
                            .withSandboxDestination()
                            .build();

            System.setProperty("https.protocols", "TLSv1");
            System.out.println("push test");
            String payload = APNS.newPayload().alertBody(PushNotificationHelper.alertBody).sound("default").badge(PushNotificationHelper.badgeNo).build();
            //{"aps":{"alert":"This is test.. (9)","badge":1,"sound":"default"}}
            String token = deviceId;
            service.push(token, payload);

            Map<String, Date> inactiveDevices = service.getInactiveDevices();
            for (String deviceToken : inactiveDevices.keySet()) {
                Date inactiveAsOf = inactiveDevices.get(deviceToken);
            }
        }catch (Exception ex){
            ex.printStackTrace();
            return;
        }


    }
}
