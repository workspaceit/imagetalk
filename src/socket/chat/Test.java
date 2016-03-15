package socket.chat;

import com.google.gson.Gson;
import com.googlecode.javacv.FFmpegFrameGrabber;
import com.googlecode.javacv.FrameGrabber;
import model.datamodel.app.AuthCredential;

import javax.imageio.ImageIO;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * Created by mi on 12/4/15.
 */
public class Test {
    public static void main(String args[]){
        String videoPath = "/home/mi/Projects/j2ee/imagetalk_picture/420/chat/media/video/367680686848995.mp4";
        String imgSavePath = "/home/mi/Projects/j2ee/imagetalk_picture/420/chat/media/367680686848995.png";
        FFmpegFrameGrabber g = new FFmpegFrameGrabber(videoPath);
        try {
            g.start();
            ImageIO.write(g.grab().getBufferedImage(), "png", new File(imgSavePath));
            g.stop();
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}
