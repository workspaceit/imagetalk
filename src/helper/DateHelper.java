package helper;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by mi on 10/29/15.
 */
public class DateHelper {

    public static String getProcessedTimeStamp(Timestamp timeStamp) {
        String processedTime = "";
        if(timeStamp!=null){

            Long longTime = timeStamp.getTime() / 1000;
            processedTime = Long.toString(longTime);
        }

        return processedTime;
    }
    public static String getUtcDateProcessedTimeStamp() {
        String processedTime = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date date = dateFormat.parse(dateFormat.format(new java.util.Date()));
            java.sql.Timestamp timeStampDate = new Timestamp(date.getTime());
            Long longTime = timeStampDate.getTime() / 1000;
            return Long.toString(longTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return "";
    }
    public static String getUtcDateTime(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        return dateFormat.format(new java.util.Date());
    }
}
