<%@ page import="controller.service.ImageTalkBaseController" %>
<%@ page import="model.datamodel.Login" %>
<%@ page import="model.LoginModel" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="model.UserInfModel" %>
<%@ page import="model.datamodel.User" %>
<%@ page import="model.datamodel.Country" %>
<%@ page import="model.CountryModel" %>
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
	CountryModel countryModel = new CountryModel();
	ArrayList<Login> loginList = loginModel.getAllTeamLead();
	ArrayList<Country> countries = countryModel.getAll();

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
				App User
				<small>add new</small>
			</h1>
			<ol class="breadcrumb">
				<li><a href="<%=imageTalkBaseController.getBaseUrl(request)+"admin"%>"><i class="fa fa-dashboard"></i> Dash board</a></li>
				<li ><a href="<%=imageTalkBaseController.getBaseUrl(request)+"admin/admin_user/management"%>" >All Admin User</a></li>
				<li class="active">add new</li>
			</ol>
		</section>

		<section class="content">

			<!-- SELECT2 EXAMPLE -->
			<div class="box box-default">
				<div class="box-header with-border">
					<h3 class="box-title">App User information</h3>

				</div><!-- /.box-header -->
				<form class="form-horizontal margin-none" method="post" autocomplete="off" onsubmit="return addUser();">

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

									<!-- Group -->
									<div class="form-group">
										<label class="col-md-4 control-label" for="teamName">First name</label>
										<div class="col-md-8"><input class="form-control" id="f_name"  type="text" /></div>
									</div>
									<div class="form-group">
										<label class="col-md-4 control-label" for="teamName">Last name</label>
										<div class="col-md-8"><input class="form-control" id="l_name"  type="text" /></div>
									</div>
									<div class="form-group" style="display: none;">
										<label class="col-md-4 control-label" for="teamName">Address</label>
										<div class="col-md-8"><input class="form-control" id="address"  type="text" /></div>
									</div>

									<div class="form-group">
										<label class="col-md-4 control-label" for="teamName">Email</label>
										<div class="col-md-8"><input class="form-control" id="email"  type="text" /></div>
									</div>
									<div class="form-group">
										<label class="col-md-4 control-label" for="teamName">Password</label>
										<div class="col-md-8"><input class="form-control" id="password"  type="text" /></div>
									</div>


									<%--<div class="form-group">--%>
										<%--<label class="col-md-4 control-label" for="teamName">Confirm Password</label>--%>
										<%--<div class="col-md-8"><input class="form-control" id="con_password"  type="text" /></div>--%>
									<%--</div>--%>


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

        function addUser(){
            $("#createTeamErrorMsg").html("");
            var f_name = $("#f_name").val();
            var l_name = $("#l_name").val();
            var address = $("#address").val();
            var email = $("#email").val();
            var password = $("#password").val();
            var con_password = $("#con_password").val();


            var userType = "USER";
            if(typeof $('#isTeamLeadSelected:checked').val()!="undefined"){
                userType = "TEAM_LEAD";
            }
            var url = $("#base_url").val()+"admin/operation/admin/add";
            $.ajax({
                url: url,
                method: "POST",
                data: {
                    "f_name": f_name,
                    "l_name": l_name,
                    "address":address,
                     "email":email,
                    "password":password,
                     "type":userType

                },
                success: function (data) {
                    if(data.responseStat.status){
                       $("#f_name").val("");
                         $("#l_name").val("");
                        $("#address").val("");
                        $("#email").val("");
                        $("#password").val("");
                       $("#con_password").val("");
                        if(typeof $('#isTeamLeadSelected:checked').val()!="undefined"){
                            $('#isTeamLeadSelected').removeAttr("checked");
                        }

                    }else{

                    }
                    $("#createTeamErrorMsg").html(data.responseStat.msg);
                    console.log(data);
                }
            });
            console.log("Trigger");
            return false;
        }

	</script>
</body>
</html>
