package controller.service;

import helper.ImageHelper;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;

/**
 * Created by mi on 10/13/15.
 */
public class StickerTransferController extends HttpServlet {
/*    ImageTalkBaseController baseController;
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

        res.setContentType("application/json");

        String url = req.getRequestURI();

        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        switch (url) {
            default:
                this.readPicture(req,res);
                break;
        }


    }
    private void readPicture(HttpServletRequest req,HttpServletResponse res){
        ServletOutputStream out = null;
        InputStream in = null;
        ImageTalkBaseController baseController = new ImageTalkBaseController();
// do the following in a finally block:
        try {

            if(!baseController.checkParam("p", req, true)){
                return;
            }
            out = res.getOutputStream();
            System.out.println("Value of p : " + req.getParameter("p"));
            String picRelativePath = URLDecoder.decode(req.getParameter("p").trim(), "UTF-8");
            System.out.println(picRelativePath);
            in = new FileInputStream(ImageHelper.getStickerGlobalPath()+picRelativePath);
            String mimeType = "image/jpeg";
            byte[] bytes = new byte[1024];
            int bytesRead;

            res.setContentType(mimeType);

            while ((bytesRead = in.read(bytes)) != -1) {
                out.write(bytes, 0, bytesRead);
            }

            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
