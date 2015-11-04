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
%>
<!DOCTYPE html>
<html>

<%@include file="head.jsp" %>
<link href="/assets/filer/css/jquery.filer.css" type="text/css" rel="stylesheet"/>
<link href="/assets/filer/css/themes/jquery.filer-dragdropbox-theme.css" type="text/css" rel="stylesheet"/>

<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">

    <%@include file="top_nav_bar.jsp" %>
    <%@include file="menu.jsp" %>

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1>
                All Stickers by
                <small>Category</small>
            </h1>
            <ol class="breadcrumb">
                <li><a href="<%=imageTalkBaseController.getBaseUrl(request)+"admin"%>"><i class="fa fa-dashboard"></i>
                    Dashboard</a></li>
                <li>Sticker Category</li>
            </ol>
        </section>

        <!-- Main content -->
        <section class="content">
            <div class="row">
                <div class="col-xs-12">
                    <div class="box">
                        <div class="box-header">
                            <h3 class="box-title">
                                <h2>Sticker Category</h2>
                            </h3>
                        </div>
                        <!-- /.box-header -->
                        <div class="box-body">
                            <% if(stickerbycategory.size()==0)
                            {%>
                               <h1>No Stickers of this category</h1>
                            <%
                                }
                                else
                                {
                            %>
                            <table class="table table-bordered table-striped" id="example1">
                                <thead>
                                <tr>
                                    <th>SL</th>
                                    <th>Sticker ID</th>
                                    <th>Category Name</th>
                                    <th>Sticker Icon</th>
                                </tr>
                                </thead>
                                <tbody>
                                <% int i = 1; %>
                                <% for (Stickers stickersModel1 : stickerbycategory) { %>
                                <tr>
                                    <td>
                                        <%=i%>
                                    </td>
                                    <td>
                                        <%=stickersModel1.id%>
                                    </td>
                                    <td>
                                        <%=stickersModel1.categoryName%>
                                    </td>
                                    <td>
                                        <img src="/app/media/access/sticker?p=<%=stickersModel1.path%>" alter="image icon">
                                    </td>
                                    <% i++; %>
                                </tr>
                                <% } %>
                                </tbody>
                            </table>
                                <%}%>
                        </div>
                        <!-- /.box-body -->
                    </div>
                    <!-- /.box -->
                </div>
                <!-- /.col -->
            </div>
            <!-- /.row -->
            <div class="row">
                <div class="small-box bg-red" id="errorMsgDiv">

                </div>
            </div>
        </section>
        <!-- /.content -->
    </div>

    <%@include file="footer.jsp" %>

    <!-- Control Sidebar -->

    <!-- Add the sidebar's background. This div must be placed
         immediately after the control sidebar -->
    <div class="control-sidebar-bg"></div>
</div>
<!-- ./wrapper -->

<!-- jQuery 2.1.4 -->
<script src="/assets/plugins/jQuery/jQuery-2.1.4.min.js"></script>
<!-- Bootstrap 3.3.5 -->
<script src="/assets/bootstrap/js/bootstrap.min.js"></script>
<!-- DataTables -->
<script src="/assets/plugins/datatables/jquery.dataTables.min.js"></script>
<script src="/assets/plugins/datatables/dataTables.bootstrap.min.js"></script>
<!-- SlimScroll -->
<script src="/assets/plugins/slimScroll/jquery.slimscroll.min.js"></script>
<!-- FastClick -->
<script src="/assets/plugins/fastclick/fastclick.min.js"></script>
<!-- AdminLTE App -->
<script src="/assets/dist/js/app.min.js"></script>
<!-- AdminLTE for demo purposes -->
<script src="/assets/dist/js/demo.js"></script>

</body>
</html>
