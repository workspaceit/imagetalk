package controller.service;

import model.ReportAppIssueModel;
import model.UserReviewModel;
import model.WallPostModel;
import model.WalletModel;

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
public class WalletController extends HttpServlet {


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
            case "/wallet/get/byuser":
                pw.print(this.getWalletInfo(req));
                break;

            default:
                break;
        }
        pw.close();
    }

    public String getWalletInfo(HttpServletRequest req){

        ImageTalkBaseController baseController = new ImageTalkBaseController(req);

        int userID = baseController.appCredential.id;

        WalletModel walletModel = new WalletModel();

        walletModel.setUserId(userID);

        double total_credit = walletModel.getTotalCreditByAppCredential();

        if(total_credit>0)
        {
            baseController.serviceResponse.responseStat.msg = "Total credit found !";
            baseController.serviceResponse.responseData =total_credit;
            return baseController.getResponse();
        }

        baseController.serviceResponse.responseStat.status =false;
        baseController.serviceResponse.responseStat.msg = "Total credit is 0!";
        baseController.serviceResponse.responseData = total_credit;
        return baseController.getResponse();

    }

}
