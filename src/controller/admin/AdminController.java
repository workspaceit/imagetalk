package controller.admin;

import controller.service.ImageTalkBaseController;

import model.*;
import model.datamodel.app.Login;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mi on 9/2/15.
 */
public class AdminController extends HttpServlet {
    Login                   login;
    ImageTalkBaseController baseController;
    PrintWriter             pw;

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        String url = req.getRequestURI().toString();
        this.baseController = new ImageTalkBaseController();
        this.login = new Login();
        this.pw = resp.getWriter();

        if (!this.baseController.isSessionValid(req)) {
            this.pw.print(this.baseController.getResponse());
            return;
        }
        if (!this.baseController.isAdmin(req)) {
            this.pw.print(this.baseController.getResponse());
            return;
        }
        login = this.baseController.getUserLoginFromSession(req);

        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        switch (url) {

            case "/admin/operation/user/add":
                this.addUser(req, resp);
                break;
            case "/admin/operation/admin/add":
                this.addAdminUser(req, resp);
                break;

            case "/admin/operation/admin_user/delete":
                this.deleteAdminUser(req);
                break;

            case "/admin/operation/change/user/status":
                this.changeUserStatus(req);
                break;

            case "/admin/operation/upload/image/ajax":
                this.uploadImageByAjax(req);
                break;

            default:
                break;
        }
        this.pw.close();
    }

    public void addUser(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/json");

        this.baseController = new ImageTalkBaseController();
        AdminLoginModel adminLoginModel = new AdminLoginModel();
        UserInfModel    userInfModel    = new UserInfModel();

        if (this.baseController.checkParam("f_name", req, true)) {
            userInfModel.setF_name(req.getParameter("f_name"));
        } else {
            this.baseController.serviceResponse.responseStat.msg = "First name empty";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        if (this.baseController.checkParam("l_name", req, true)) {
            userInfModel.setL_name(req.getParameter("l_name"));
        } else {
            this.baseController.serviceResponse.responseStat.msg = "Last name empty";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        if (this.baseController.checkParam("address", req, true)) {
            userInfModel.setAddress(req.getParameter("address"));
        } else {
            this.baseController.serviceResponse.responseStat.msg = "Address empty";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        if (this.baseController.checkParam("email", req, true)) {
            // String email_regex = "[A-Z]+[a-zA-Z_]+@\b([a-zA-Z]+.){2}\b?.[a-zA-Z]+";
            String testString = req.getParameter("email").trim();

            String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
            Pattern pattern = Pattern.compile(EMAIL_PATTERN);
            Matcher matcher = pattern.matcher(testString);

            if (matcher.matches()) {
                adminLoginModel.email = req.getParameter("email");
            } else {
                this.baseController.serviceResponse.responseStat.msg = "Email is not valid";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }


        } else {
            this.baseController.serviceResponse.responseStat.msg = "Email empty";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        if (this.baseController.checkParam("password", req, true)) {
            adminLoginModel.password = req.getParameter("password");
            if (adminLoginModel.password.length() < 6) {
                this.baseController.serviceResponse.responseStat.msg = "Password at least 6 digit required";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        } else {
            this.baseController.serviceResponse.responseStat.msg = "Password empty";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }


        if (adminLoginModel.isEmailExist(adminLoginModel.email)) {
            this.baseController.serviceResponse.responseStat.status = false;
            this.baseController.serviceResponse.responseStat.msg = "Email already used";
            this.pw.println(this.baseController.getResponse());
            return;
        }

        if (this.baseController.checkParam("type", req, true)) {
            String type = req.getParameter("type").trim();
            System.out.println("type = " + type);
            if (type.equals("ADMIN")) {
                System.out.println("type = 1 block");
                adminLoginModel.type = 1;
            } else if (type.equals("TEAM_LEAD")) {
                System.out.println("type = 2 block");
                adminLoginModel.type = 2;
            } else {
                System.out.println("type = 3 block");
                adminLoginModel.type = 3;
            }
        } else {
            adminLoginModel.type = 3;
        }


        int userId = userInfModel.insertData();
        if (userId <= 0) {
            userInfModel.deleteData(userId);
            this.pw.println(this.baseController.getResponse());
            return;
        }

        adminLoginModel.u_id = userId;
        int loginId = adminLoginModel.insertData(userId);

        if (loginId <= 0) {
            userInfModel.deleteData(userId);
            this.pw.println(this.baseController.getResponse());
            return;
        }


        this.baseController.serviceResponse.responseStat.status = true;
        this.baseController.serviceResponse.responseStat.msg = "Registration Successfully";
        this.pw.println(this.baseController.getResponse());
        return;
    }

    public void addAdminUser(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/json");

        this.baseController = new ImageTalkBaseController();
        AdminLoginModel adminLoginModel = new AdminLoginModel();
        UserInfModel    userInfModel    = new UserInfModel();

        if (this.baseController.checkParam("f_name", req, true)) {
            userInfModel.setF_name(req.getParameter("f_name"));
        } else {
            this.baseController.serviceResponse.responseStat.msg = "First name empty";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        if (this.baseController.checkParam("l_name", req, true)) {
            userInfModel.setL_name(req.getParameter("l_name"));
        } else {
            this.baseController.serviceResponse.responseStat.msg = "Last name empty";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        //        if (this.baseController.checkParam("address",req,true)) {
        //            userInfModel.address = req.getParameter("address");
        //        } else {
        //            this.baseController.serviceResponse.responseStat.msg = "Address empty";
        //            this.baseController.serviceResponse.responseStat.status = false;
        //            this.pw.print(this.baseController.getResponse());
        //            return;
        //        }

        if (this.baseController.checkParam("email", req, true)) {
            // String email_regex = "[A-Z]+[a-zA-Z_]+@\b([a-zA-Z]+.){2}\b?.[a-zA-Z]+";
            String testString = req.getParameter("email").trim();

            String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
            Pattern pattern = Pattern.compile(EMAIL_PATTERN);
            Matcher matcher = pattern.matcher(testString);

            if (matcher.matches()) {
                adminLoginModel.email = req.getParameter("email");
            } else {
                this.baseController.serviceResponse.responseStat.msg = "Email is not valid";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }


        } else {
            this.baseController.serviceResponse.responseStat.msg = "Email empty";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        if (this.baseController.checkParam("password", req, true)) {
            adminLoginModel.password = req.getParameter("password");
            if (adminLoginModel.password.length() < 6) {
                this.baseController.serviceResponse.responseStat.msg = "Password at least 6 digit required";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        } else {
            this.baseController.serviceResponse.responseStat.msg = "Password empty";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }


        if (adminLoginModel.isEmailExist(adminLoginModel.email)) {
            this.baseController.serviceResponse.responseStat.status = false;
            this.baseController.serviceResponse.responseStat.msg = "Email already used";
            this.pw.println(this.baseController.getResponse());
            return;
        }
        // Setting type is Admin
        adminLoginModel.type = 1;


        int userId = userInfModel.insertData();
        if (userId <= 0) {
            userInfModel.deleteData(userId);
            this.pw.println(this.baseController.getResponse());
            return;
        }

        adminLoginModel.u_id = userId;
        int loginId = adminLoginModel.insertData(userId);

        if (loginId <= 0) {
            userInfModel.deleteData(userId);
            this.pw.println(this.baseController.getResponse());
            return;
        }


        this.baseController.serviceResponse.responseStat.status = true;
        this.baseController.serviceResponse.responseStat.msg = "Registration Successfully";
        this.pw.println(this.baseController.getResponse());
        return;
    }

    private void deleteAdminUser(HttpServletRequest req) {
        int u_id     = 0;
        int login_id = 0;
        if (!this.baseController.checkParam("u_id", req, true)) {
            this.baseController.serviceResponse.responseStat.msg = "User id required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        } else {
            try {
                u_id = Integer.parseInt(req.getParameter("u_id").trim());
            } catch (NumberFormatException e) {
                this.baseController.serviceResponse.responseStat.msg = "User id not valid format, int required";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }

        if (!this.baseController.checkParam("login_id", req, true)) {
            this.baseController.serviceResponse.responseStat.msg = "User id required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        } else {
            try {
                login_id = Integer.parseInt(req.getParameter("login_id").trim());
            } catch (NumberFormatException e) {
                this.baseController.serviceResponse.responseStat.msg = "login id not valid format, int required";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }

        AdminLoginModel adminLoginModel = new AdminLoginModel();
        UserInfModel    userInfModel    = new UserInfModel();


        adminLoginModel.login.id = login_id;
        userInfModel.setId(u_id);


        if (adminLoginModel.deleteById() <= 0) {
            this.baseController.serviceResponse.responseStat.msg = "Internal server error at login model";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        if (userInfModel.deleteById() <= 0) {
            this.baseController.serviceResponse.responseStat.msg = "Internal server error at userinf model";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        this.baseController.serviceResponse.responseStat.msg = "User successfully deleted";
        this.pw.print(this.baseController.getResponse());
        return;

    }

    private void changeUserStatus(HttpServletRequest req) {
        AppLoginCredentialModel appModel = new AppLoginCredentialModel();

        int userId        = Integer.parseInt(req.getParameter("user_id"));
        int status        = Integer.parseInt(req.getParameter("user_status"));
        int currentStatus = appModel.getUserStatusById(userId);

        if (status == currentStatus) {
            String statusName = "Banned";

            if (status == 1) {
                statusName = "Permitted";
            }

            this.baseController.serviceResponse.responseStat.msg = "This user already " + statusName;
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        boolean changeStatus = appModel.changeUserStatus(userId, status);

        if (changeStatus) {
            this.baseController.serviceResponse.responseStat.msg = "User Status Changed Successfully";
            this.baseController.serviceResponse.responseStat.status = true;
        } else {
            this.baseController.serviceResponse.responseStat.msg = "User Status Changed Failed";
            this.baseController.serviceResponse.responseStat.status = false;
        }

        this.pw.print(this.baseController.getResponse());
        return;
    }

    private void uploadImageByAjax(HttpServletRequest req) {
        boolean isMultipart;
        String  saveDir     = "ImageTalk_sticker/";
//        String  filePath    = "/home/touch/Projects/j2ee/" + saveDir;
        String  filePath    = "/home/mi/Projects/j2ee/" + saveDir;


        int     maxFileSize = 250 * 1024 * 1024;
        int     maxMemSize  = 10 * 1024 * 1024;
        File    file;

        isMultipart = ServletFileUpload.isMultipartContent(req);

        if (!isMultipart) {
            this.pw.print("{\"isComplete\":false, \"isSuccess\":false}");
            return;
        }

        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(maxMemSize);
        factory.setRepository(new File(filePath));

        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setSizeMax(maxFileSize);

        try {
            File fileSaveDir = new File(filePath);
            if (!fileSaveDir.exists()) {
                fileSaveDir.mkdir();
            }

            List fileItems = upload.parseRequest(req);
            Iterator i = fileItems.iterator();
            /*List<FileItem> fl = upload.parseRequest(req);

            for (FileItem item : fl) {
                if (item.isFormField()) {
                    // Process regular form field (input type="text|radio|checkbox|etc", select, etc).
                    String fieldname = item.getFieldName();
                    String fieldvalue = item.getString();
                } else {

                }
            }*/
            while (i.hasNext()) {
                String tempName = saveDir;
                FileItem fi = (FileItem) i.next();
                String fieldName1 = fi.getFieldName();
                String fieldValue1 = fi.getString();
                if (!fi.isFormField()) {
                    // Get the uploaded file parameters
                    String fieldName = fi.getFieldName();
                    String fileName = fi.getName();
                    String contentType = fi.getContentType();
                    boolean isInMemory = fi.isInMemory();
                    long sizeInBytes = fi.getSize();

                    if (sizeInBytes > maxFileSize || contentType == "image/gif") {
                        this.pw.print("{\"isComplete\":false, \"isSuccess\":false}");
                        fi.delete();
                        return;
                    }

                    if (fileName.lastIndexOf("\\") >= 0) {
                        tempName = Instant.now().getEpochSecond() + "_" + fileName.substring(fileName.lastIndexOf("\\"));
                        file = new File(filePath + tempName);
                    } else {
                        tempName = Instant.now().getEpochSecond() + "_" + fileName.substring(fileName.lastIndexOf("\\") + 1);
                        file = new File(filePath + tempName);
                    }

                    fi.write(file);
                    this.pw.print("{\"path\":\"" + saveDir + tempName + "\",\"isComplete\":true, \"isSuccess\":true, \"hasErrors\":false, \"hasWarnings\":false}");
                    System.out.println("{\"path\":\"" + saveDir + tempName + "\",\"isComplete\":true, \"isSuccess\":true, \"hasErrors\":false, \"hasWarnings\":false}");
                    return;
                } else {
                    String fieldName = fi.getFieldName();
                    String fieldValue = fi.getString();
                }
            }
        } catch (Exception ex) {
            System.out.println(ex);
            this.pw.print("{\"isComplete\":false, \"isSuccess\":false}");
        }
    }
}
