<%--
  Created by IntelliJ IDEA.
  User: Abu Bakar Siddique
  Email: absiddique.live@gmail.com
  Date: 10/8/15
  Time: 12:28 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="controller.service.ImageTalkBaseController" %>
<%@ page import="model.datamodel.app.Login" %>
<%
    ImageTalkBaseController imageTalkBaseController = new ImageTalkBaseController();
    Login login = new Login();
    if (imageTalkBaseController.isSessionValid(request)) {
        login = imageTalkBaseController.getUserLoginFromSession(request);
    } else {
        response.sendRedirect("/admin/login");
    }
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
            <h1>Sticker Category</h1>
        </section>

        <!-- Main content -->
        <section class="content">
            <div class="row">
                <div class="row shuffle clearfix">
                    <div class="col-lg-12">
                        <form class="form-horizontal" method="post" action="" onsubmit="return false;">
                            <div class="box-body">
                                <div class="form-group">
                                    <label for="name" class="col-sm-2 control-label">Name</label>

                                    <div class="col-sm-10">
                                        <input name="name" type="text" class="form-control" id="name"
                                               placeholder="Category Name">
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label for="is_paid" class="col-sm-2 control-label">Type</label>

                                    <div class="col-sm-10">
                                        <select name="is_paid" class="form-control" id="is_paid">
                                            <option value="">Select One</option>
                                            <option value="0" selected>Free</option>
                                            <option value="1">Paid</option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                            <div class="feedback">
                                <div id="error">

                                </div>
                                <div id="success"></div>

                            </div>
                            <!-- /.box-body -->
                            <div class="box-footer">
                                <button type="submit" class="btn btn-default">Cancel</button>
                                <button id="submit" class="btn btn-info pull-right">Submit</button>
                            </div>
                            <!-- /.box-footer -->
                        </form>
                    </div>
                </div>
            </div>
            <!-- /.row -->
        </section>
        <!-- /.content -->
    </div>
    <!-- /.content-wrapper -->
    <%@include file="footer.jsp" %>
    <div class="control-sidebar-bg"></div>
</div>
<!-- ./wrapper -->

<!-- jQuery 2.1.4 -->
<script src="/assets/plugins/jQuery/jQuery-2.1.4.min.js"></script>
<!-- Bootstrap 3.3.5 -->
<script src="/assets/bootstrap/js/bootstrap.min.js"></script>
<!-- FastClick -->
<script src="/assets/plugins/fastclick/fastclick.min.js"></script>
<!-- AdminLTE App -->
<script src="/assets/dist/js/app.min.js"></script>
<!-- Sparkline -->
<script src="/assets/plugins/sparkline/jquery.sparkline.min.js"></script>
<!-- jvectormap -->
<script src="/assets/plugins/jvectormap/jquery-jvectormap-1.2.2.min.js"></script>
<script src="/assets/plugins/jvectormap/jquery-jvectormap-world-mill-en.js"></script>
<!-- SlimScroll 1.3.0 -->
<script src="/assets/plugins/slimScroll/jquery.slimscroll.min.js"></script>
<!-- ChartJS 1.0.1 -->
<script src="/assets/plugins/chartjs/Chart.min.js"></script>
<!-- AdminLTE dashboard demo (This is only for demo purposes) -->
<script src="/assets/dist/js/pages/dashboard2.js"></script>
<!-- AdminLTE for demo purposes -->
<script src="/assets/dist/js/demo.js"></script>
<script>
    $("#submit").click(function () {
        $.ajax({
            method: "POST",
            url: "<%=baseUrl%>admin/sticker/operation/category/new",
            data: {
                name: $("#name").val(),
                is_paid: $("#is_paid").val()
            },
            error: function (data) {

            },
            success: function (data) {

                var parseData = jQuery.parseJSON(data);

                $("#success").html(parseData.responseStat.msg);
            }
        });
    });
</script>

</body>
</html>