<%@ page import="controller.service.ImageTalkBaseController" %>
<%@ page import="model.datamodel.Login" %>
<%@ page import="model.LoginModel" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="model.UserInfModel" %>
<%@ page import="model.datamodel.User" %>
<%@ page import="model.TeamModel" %>
<%@ page import="model.datamodel.TeamDetails" %>
<%@ page import="model.datamodel.TeamMember" %>
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
	String url = request.getRequestURI().toString();
	if(url.endsWith("/")){
		url = url.substring(0, url.length()-1);
	}
	int id = 0;
	try{
		String[] urlSplit = url.split("/");
		id = Integer.parseInt(urlSplit[urlSplit.length - 1]);
	}catch (Exception ex) {
		response.sendRedirect("/admin/team/management");
	}
	TeamModel teamModel = new TeamModel();
	teamModel.id = id;
	TeamDetails teamDetails = teamModel.getAllById();
	if(teamDetails.id == 0){
		response.sendRedirect("/admin/team/management");
	}
	TeamMember teamLead = teamModel.getTeamLeadOfTeam();


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
				<li >Team</li>
				<li class="active">add new</li>
			</ol>
		</section>

		<section class="content">

			<!-- SELECT2 EXAMPLE -->
			<div class="box box-default">
				<div class="box-header with-border">
					<h3 class="box-title">Team information</h3>

				</div><!-- /.box-header -->
				<div class="box-body">
					<div class="row">
						<div class="col-md-6">
							<div class="form-group">
								<label class="col-md-4 control-label" for="teamName">Team name</label>
								<div class="col-md-8">
									<input class="form-control" id="teamName" name="firstname" type="text" value="<%=teamDetails.name%>"/>
									<input id="teamId" type="hidden" value="<%=teamDetails.id%>"/>

								</div>
							</div>
							<div class="form-group">
							</div>
							<div class="form-group">
								<label class="col-md-4 control-label" for="teamName">Team lead</label>
								<div class="col-md-8">
									<select style="width: 100%;" id="teamLeadSelect">
										<option value="0">Select team lead</option>
										<%
											String selected = "";
											for(Login tmpLogin :loginList){
												if(teamLead.id == tmpLogin.u_id){
													selected = "selected";
												}else{
													selected = "";
												}

										%>

										<option <%=selected%> value="<%=tmpLogin.id%>"><%=tmpLogin.user.f_name+" "+tmpLogin.user.l_name%></option>
										<% } %>
									</select>
								</div>
							</div><!-- /.form-group -->
							<div class="form-group">
								<label class="col-md-4 control-label"></label>
								<div class="col-md-8">
									<button type="button" onclick="updateTeam()" class="btn btn-primary"><i class="fa fa-check-circle"></i> Update</button>
								</div>
							</div>
							<div class="form-group">
								<label  class="col-md-4 control-label"></label>
								<div class="col-md-8">
									<span id="createTeamErrorMsg" ></span>
								</div>
							</div>

						</div><!-- /.box -->

					</div>
				</div>
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
		$(function () {
			//Initialize Select2 Elements
			$(".select2").select2();
		});
		function updateTeam(){
			$("#createTeamErrorMsg").html("");
			var teamName = $("#teamName").val();
			var teamLeadId = $("#teamLeadSelect").val()	;
			var teamID = $("#teamId").val()	;
			var url = $("#base_url").val()+"admin/operation/team/update/basic";
			$.ajax({
				url: url,
				method: "POST",
				data: {"team_id":teamID,"name": teamName, "team_lead_id": teamLeadId},
				success: function (data) {
					if(data.responseStat.status){
						$("#goToT_management").show();
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
