package controller.service;

import helper.ImageHelper;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.*;
import java.net.URLConnection;
import java.net.URLDecoder;

/**
 * Created by mi on 10/7/15.
 */
public class PictureAndFileController extends HttpServlet {
   /* ImageTalkBaseController baseController;
    PrintWriter pw;
    HttpServletRequest req;
    HttpServletResponse res;*/

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String url = req.getRequestURI().toString();

        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }



        switch (url) {
            default:
                readPicture(req, res);
                break;
        }

    }
    private void readPicture(HttpServletRequest req, HttpServletResponse res){
        ImageTalkBaseController baseController = new ImageTalkBaseController();
        ServletOutputStream out = null;
        InputStream in = null;

        String picRelativePath = "";
        // do the following in a finally block:
        try {

            if(!baseController.checkParam("p", req, true)){
                return;
            }
            out = res.getOutputStream();
            System.out.println(req.getParameter("p"));
            picRelativePath = URLDecoder.decode(req.getParameter("p"), "UTF-8");
            System.out.println(picRelativePath);
            File file = new File(ImageHelper.getGlobalPath()+picRelativePath);
            if (!file.exists()) {
                return;
            }


            in = new FileInputStream(ImageHelper.getGlobalPath()+picRelativePath);
            System.out.println(URLConnection.guessContentTypeFromStream(in));
            String mimeType = "image/jpeg";
            byte[] bytes = new byte[1024];
            int bytesRead;

            res.setContentType(mimeType);

            while ((bytesRead = in.read(bytes)) != -1) {
                out.write(bytes, 0, bytesRead);
            }
            in.close();
            out.flush();
            out.close();

        } catch (IOException e) {
            System.out.println("Exception picRelativePath : '"+picRelativePath+"'");
            e.printStackTrace();
        }finally {

        }

    }
}
