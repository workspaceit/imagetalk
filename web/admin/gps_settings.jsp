<%@ page import="controller.service.ImageTalkBaseController" %>
<%@ page import="model.datamodel.Login" %>
<%@ page import="model.LoginModel" %>
<%@ page import="model.TeamModel" %>
<%@ page import="gps_socket.CentralSocketController" %>
<%
  ImageTalkBaseController imageTalkBaseController = new ImageTalkBaseController();
  Login login = new Login();
  if(imageTalkBaseController.isSessionValid(request)){
    login = imageTalkBaseController.getUserLoginFromSession(request);
  }else{
    response.sendRedirect("/admin/login");
  }
  String gpsServerStatusStr = (CentralSocketController.serverSocket!=null)?"Server is live":"Server is close";
  String divClass =  (CentralSocketController.serverSocket!=null)?"alert alert-success alert-dismissable":"alert alert-warning alert-dismissable";

%>
<!DOCTYPE html>
<html>

<%@include file="head.jsp"%>

<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">

  <%@include file="top_nav_bar.jsp"%>
  <%@include file="menu.jsp"%>

  <!-- Content Wrapper. Contains page content -->
  <div class="content-wrapper">
    <!-- Content Header (Page header) -->
    <section class="content-header">
      <h1>
        GPS
        <small>Pre-Alpha 1.0.0</small>
      </h1>
      <ol class="breadcrumb">
        <li><a href="<%=imageTalkBaseController.getBaseUrl(request)+"admin"%>" ><i class="fa fa-dashboard active"></i> Dashboard</a></li>
        <li>GPS</li>
        <li class="active">Settings</li>
      </ol>
    </section>

    <!-- Main content -->
    <section class="content">
      <!-- Info boxes -->
      <div class="row">

      Settings will implemented
      </div><!-- /.row -->



      <!-- Main row -->

    </section><!-- /.content -->
  </div><!-- /.content-wrapper -->

  <%@include file="footer.jsp"%>

  <!-- Control Sidebar -->
  <!-- Add the sidebar's background. This div must be placed
       immediately after the control sidebar -->
  <div class="control-sidebar-bg"></div>

</div><!-- ./wrapper -->

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
</body>
</html>
