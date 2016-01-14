<%@ page import="controller.service.ImageTalkBaseController" %>
<%@ page import="model.datamodel.app.Login" %>
<%@ page import="model.AdminLoginModel" %>
<%
  ImageTalkBaseController imageTalkBaseController = new ImageTalkBaseController();
  Login login = new Login();
  if(imageTalkBaseController.isSessionValid(request)){
    login = imageTalkBaseController.getUserLoginFromSession(request);
  }else{
    response.sendRedirect("/admin/login");
  }
  AdminLoginModel adminLoginModel = new AdminLoginModel();

  int totalAdminUser  =  adminLoginModel.getCountOfAdminUser();

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
        Developer Arena
        <small>Pre-Alpha 1.0.0</small>
      </h1>
      <ol class="breadcrumb">
        <li><a href="#"><i class="fa fa-dashboard active"></i> Dashboard</a></li>
      </ol>
    </section>

    <!-- Main content -->
    <section class="content">
      <!-- Info boxes -->
      <div class="row">


        <div class="col-md-3 col-sm-6 col-xs-12">
          <div class="info-box">
            <span class="info-box-icon bg-yellow">CS</span>
            <div class="info-box-content">
              <span class="info-box-text" id="cs_msg"></span>
              <span class="info-box-number"><button class="btn btn-block btn-danger btn-lg" onclick="getCsStarted('cs_btn')" id="cs_btn">Start</button></span>

            </div><!-- /.info-box-content -->
          </div><!-- /.info-box -->
        </div><!-- /.col -->
      </div><!-- /.row -->
      <div class="col-md-3 col-sm-6 col-xs-12">
        <div class="info-box">
          <span class="info-box-icon bg-blue">TS</span>
          <div class="info-box-content">
            <span class="info-box-text" id="ts_msg"></span>
            <span class="info-box-number"><button class="btn btn-block btn-danger btn-lg" onclick="getTsStarted('ts_btn')" id="ts_btn" >Start</button></span>
          </div><!-- /.info-box-content -->
        </div><!-- /.info-box -->
      </div><!-- /.col -->
  </div><!-- /.row -->
      <%--<div class="col-md-3 col-sm-6 col-xs-12">--%>
        <%--<div class="info-box">--%>
          <%--<span class="info-box-icon bg-yellow"><i class="ion ion-ios-people-outline"></i></span>--%>
          <%--<div class="info-box-content">--%>
            <%--<span class="info-box-text">Team</span>--%>
            <%--<span class="info-box-number">0</span>--%>
          <%--</div><!-- /.info-box-content -->--%>
        <%--</div><!-- /.info-box -->--%>
      <%--</div><!-- /.col -->--%>


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
<script>
  function getCsStarted(id){
    $.ajax({
      url: $("#base_url").val() + "developer/server/access/start/chatserver",
      method: "POST",
      success: function (data) {
        if (data.responseStat.status) {
          $("#"+id).attr("onclick","stopCs('"+id+"')");
          $("#"+id).html("Stop");
        }else{

        }
        $("#cs_msg").html(data.responseStat.msg);
      }
    });
  }
  function getTsStarted(id){
    $.ajax({
      url: $("#base_url").val() + "developer/server/access/start/thriftserver",
      method: "POST",
      success: function (data) {
        if (data.responseStat.status) {
          $("#"+id).attr("onclick","stopTs('"+id+"')");
          $("#"+id).html("Stop");
        }else{

        }
        $("#ts_msg").html(data.responseStat.msg);
      }
    });
  }

  function getCsStatus(id){
    $.ajax({
      url: $("#base_url").val() + "developer/server/access/chatserver/running",
      method: "POST",
      success: function (data) {
        if (data.responseStat.status) {
          $("#"+id).attr("onclick","stopCs('"+id+"')");
          $("#"+id).html("Stop");
        }else{
          $("#"+id).attr("onclick","getCsStarted('"+id+"')");
          $("#"+id).html("Start");
        }
        $("#cs_msg").html(data.responseStat.msg);
      }
    });
  }
  function getTsStatus(id){
    $.ajax({
      url: $("#base_url").val() + "developer/server/access/thriftserver/running",
      method: "POST",
      success: function (data) {
        if (data.responseStat.status) {
          $("#"+id).attr("onclick","stopTs('"+id+"')");
          $("#"+id).html("Stop");
        }else{
          $("#"+id).attr("onclick","getTsStarted('"+id+"')");
          $("#"+id).html("Start");
        }
        $("#ts_msg").html(data.responseStat.msg);
      }
    });
  }
  function stopCs(id){
    $.ajax({
      url: $("#base_url").val() + "developer/server/access/chatserver/stop",
      method: "POST",
      success: function (data) {
        if (data.responseStat.status) {
          $("#"+id).attr("onclick","getCsStarted('"+id+"')");
          $("#"+id).html("Start");
        }else{

        }
        $("#cs_msg").html(data.responseStat.msg);
      }
    });
  }
  function stopTs(id){
    $.ajax({
      url: $("#base_url").val() + "developer/server/access/thriftserver/stop",
      method: "POST",
      success: function (data) {
        if (data.responseStat.status) {
          $("#"+id).attr("onclick","getTsStarted('"+id+"')");
          $("#"+id).html("Start");
        }else{

        }
        $("#ts_msg").html(data.responseStat.msg);
      }
    });
  }
  getCsStatus('cs_btn');
  getTsStatus('ts_btn')
</script>


</body>
</html>
