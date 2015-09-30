<%@ page import="controller.service.ImageTalkBaseController" %>
<%@ page import="model.datamodel.Login" %>
<%@ page import="model.LoginModel" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="model.UserInfModel" %>
<%@ page import="model.datamodel.User" %>
<!DOCTYPE html>
<%
    ImageTalkBaseController imageTalkBaseController = new ImageTalkBaseController();
    Login login = new Login();
    if(imageTalkBaseController.isSessionValid(request)){
        login = imageTalkBaseController.getUserLoginFromSession(request);
    }else{
        response.sendRedirect("/admin/login");
    }
    LoginModel loginModel = new LoginModel();
    ArrayList<Login> loginList = loginModel.getAllTeamLead();

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
                Team
                <small>add new</small>
            </h1>
            <ol class="breadcrumb">
                <li><a href="<%=imageTalkBaseController.getBaseUrl(request)+"admin"%>"><i class="fa fa-dashboard"></i> Dash board</a></li>
                <li ><a href="<%=imageTalkBaseController.getBaseUrl(request)+"admin/team/management"%>">All Team</a></li>
                <li class="active">Add new</li>
            </ol>
        </section>

        <section class="content">
            <div class="box box-default">
                <div class="box-header with-border">
                    <h3 class="box-title">Team information</h3>

                </div><!-- /.box-header -->
                <form class="form-horizontal margin-none" method="post" autocomplete="off" onsubmit="return createTeam();">

                    <!-- Widget -->
                    <div class="widget">

                        <!-- Widget heading -->
                        <%--<div class="widget-head">--%>
                        <%--<h4 class="heading">Validate a form with jQuery</h4>--%>
                        <%--</div>--%>
                        <!-- // Widget heading END -->

                        <div class="widget-body innerAll inner-2x">

                            <!-- Row -->
                            <div class="row">

                                <!-- Column -->
                                <div class="col-md-10">

                                    <div class="form-group">
                                        <label class="col-md-4 control-label" for="teamName">Team name</label>
                                        <div class="col-md-8"><input class="form-control" id="teamName" type="text" /></div>
                                    </div>
                                    <div class="form-group">
                                    </div>
                                    <div class="form-group">
                                        <label class="col-md-4 control-label" for="teamName">Team lead</label>
                                        <div class="col-md-8">
                                            <select class="form-control select2" style="width: 100%;"  id="teamLeadSelect" >
                                                <option value="0">Select a team lead</option>
                                                <% for(Login tmpLogin :loginList){ %>
                                                <option value="<%=tmpLogin.u_id%>"><%=tmpLogin.user.f_name+" "+tmpLogin.user.l_name%></option>
                                                <% } %>
                                            </select>
                                        </div>
                                    </div><!-- /.form-group -->
                                    <div class="form-group">
                                        <label class="col-md-4 control-label"></label>
                                        <div class="col-md-8">
                                            <button type="submit" class="btn btn-primary"><i class="fa fa-check-circle"></i> Save</button>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label  class="col-md-4 control-label"></label>
                                        <div class="col-md-8">
                                            <span id="createTeamErrorMsg" ></span>
                                        </div>
                                    </div>


                                </div>
                                <!-- // Row END -->

                                <!-- Row -->




                            </div>
                        </div>
                        <!-- // Widget END -->

                </form>

            </div>
        </section><!-- /.content -->
    </div>
    </div>
    <%@include file="footer.jsp"%>

      <!-- Control Sidebar -->



    <!-- jQuery 2.1.4 -->
    <script src="/assets/plugins/jQuery/jQuery-2.1.4.min.js"></script>
    <!-- Bootstrap 3.3.5 -->
    <script src="/assets/bootstrap/js/bootstrap.min.js"></script>
    <!-- Select2 -->
    <script src="/assets/plugins/select2/select2.full.min.js"></script>
    <!-- InputMask -->
    <script src="/assets/plugins/input-mask/jquery.inputmask.js"></script>
    <script src="/assets/plugins/input-mask/jquery.inputmask.date.extensions.js"></script>
    <script src="/assets/plugins/input-mask/jquery.inputmask.extensions.js"></script>
    <!-- date-range-picker -->
    <script src="/assets/js/moment.min.js"></script>
    <script src="/assets/plugins/daterangepicker/daterangepicker.js"></script>
    <!-- bootstrap color picker -->
    <script src="/assets/plugins/colorpicker/bootstrap-colorpicker.min.js"></script>
    <!-- bootstrap time picker -->
    <script src="/assets/plugins/timepicker/bootstrap-timepicker.min.js"></script>
    <!-- SlimScroll 1.3.0 -->
    <script src="/assets/plugins/slimScroll/jquery.slimscroll.min.js"></script>
    <!-- iCheck 1.0.1 -->
    <script src="/assets/plugins/iCheck/icheck.min.js"></script>
    <!-- FastClick -->
    <script src="/assets/plugins/fastclick/fastclick.min.js"></script>
    <!-- AdminLTE App -->
    <script src="/assets/dist/js/app.min.js"></script>
    <!-- AdminLTE for demo purposes -->
    <script src="/assets/dist/js/demo.js"></script>
    <!-- Page script -->
    <script>
      $(function () {
        //Initialize Select2 Elements
        $(".select2").select2();
      });
      function createTeam(){
          $("#createTeamErrorMsg").html("");
          var teamName = $("#teamName").val();
          var teamLeadId = $("#teamLeadSelect").val();
          var url = $("#base_url").val()+"admin/operation/team/add";
          $.ajax({
              url: url,
              method: "POST",
              data: {"name": teamName, "team_lead_id": teamLeadId},
              success: function (data) {
                  if(data.responseStat.status){
                      $("#teamName").val("");
                      $("#teamLeadSelect").val(0);
                      $('#teamLeadSelect').select2(0);
                  }else{

                  }
                  $("#createTeamErrorMsg").html(data.responseStat.msg);

              }
          });


          console.log("Trigger");
          return false;
      }
    </script>
  </body>
</html>
