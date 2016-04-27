package controller.service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Application Name : ImageTalk
 * Package Name     : controller.service
 * Author           : Abu Bakar Siddique
 * Email            : absiddique.live@gmail.com
 * Created Date     : 4/27/16
 */
public class UserReviewController extends HttpServlet {


    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        res.setContentType("application/json");
        res.setCharacterEncoding("utf8");

        ImageTalkBaseController baseController = new ImageTalkBaseController(req);

        PrintWriter pw = res.getWriter();

        if(!baseController.isAppSessionValid(req)){
            pw.print(baseController.getResponse());
            pw.close();
            return;
        }

        String url = req.getRequestURI().toString();

        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        switch (url) {
            case "/user/review/add":
                pw.print(this.addReview(req));
                break;

            default:
                break;
        }
        pw.close();
    }

    public String addReview(HttpServletRequest req){

        ImageTalkBaseController baseController = new ImageTalkBaseController();
        baseController.serviceResponse.responseStat.msg = "Successfully updated";
        return baseController.getResponse();

    }

}
