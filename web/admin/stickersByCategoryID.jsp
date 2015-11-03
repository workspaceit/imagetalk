<%--
  Created by IntelliJ IDEA.
  User: rajib
  Date: 11/2/15
  Time: 2:25 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="controller.service.ImageTalkBaseController" %>
<%@ page import="model.datamodel.app.Login" %>
<%@ page import="model.StickersModel" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="model.datamodel.app.Stickers" %>
<%@ page import="javax.servlet.*" %>
<%@ page import="javax.servlet.http.*" %>
<%
    ImageTalkBaseController imageTalkBaseController = new ImageTalkBaseController();
    Login login = new Login();
    if (imageTalkBaseController.isSessionValid(request)) {
        login = imageTalkBaseController.getUserLoginFromSession(request);
    } else {
        response.sendRedirect("/admin/login");
    }

    int categoryId = Integer.parseInt(request.getParameter("ID"));

    StickersModel stickersModel  = new StickersModel();
    ArrayList<Stickers> stickerbycategory = stickersModel.getAllByCategoryId(categoryId);
    for (Stickers stickersModel1:stickerbycategory)
    {%>
        <h3><%=stickersModel1.id%></h3>
        <h3><%=stickersModel1.categoryName%></h3>
             <br/>
   <% }%>


<html>
<head>
    <title></title>
</head>
<body>
</body>
</html>
