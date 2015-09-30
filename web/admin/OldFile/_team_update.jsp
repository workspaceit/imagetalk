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
		id = Integer.parseInt(url.substring(url.length() - 1, url.length()));
	}catch (Exception ex) {
		response.sendRedirect("/admin/team/management");
	}
	TeamModel teamModel = new TeamModel();
	teamModel.id = id;
	TeamDetails teamDetails = teamModel.getAllById();
	TeamMember teamLead = teamModel.getTeamLeadOfTeam();


%>
<!DOCTYPE html>
<%@include file="head.jsp" %>
<body class="">
	
	<!-- Main Container Fluid -->
	<div class="container-fluid menu-hidden">
		
				<!-- Sidebar Menu -->
		<%@include file="menu.jsp" %>

		<!-- // Sidebar Menu END -->

		<!-- Content -->
		<div id="content">

			<%@include file="top_nav_bar.jsp" %>
<!-- // END navbar -->



<h3>Create team</h3>
<div class="innerLR">
	<!-- Form -->
<form class="form-horizontal margin-none" method="post" autocomplete="off" onsubmit="return updateTeam();">
	
	<!-- Widget -->
	<div class="widget">
	
		<!-- Widget heading -->
		<%--<div class="widget-head">--%>
			<%--<h4 class="heading">Validate a form with jQuery</h4>--%>
		<%--</div>--%>
		<!-- // Widget heading END -->
		
		<div class="widget-body innerAll inner-2x">
		
			<!-- Row -->
			<div class="row innerLR">
			
				<!-- Column -->
				<div class="col-md-4">
				
					<!-- Group -->
					<div class="form-group">
						<label class="col-md-4 control-label" >
							<button id="goToT_management" type="button" class="btn btn-primary" style="display:none" onclick="goToInternalSite('admin/team/managemenet')"
									>
								<i class="fa fa-check-circle" ></i> Go to team management
							</button>
						</label>

					</div>

					<div class="form-group">
						<label class="col-md-4 control-label" for="teamName">Team name</label>
						<div class="col-md-8"><input class="form-control" id="teamName" name="firstname" type="text" value="<%=teamDetails.name%>"/></div>
					</div>
					<div class="form-group">
						<label class="col-md-4 control-label" for="teamLeadSelect">Team lead</label>
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

										<option <%=selected%> value="<%=tmpLogin.u_id%>"><%=tmpLogin.user.f_name+" "+tmpLogin.user.l_name%></option>
									<% } %>
							</select>
						</div>
						<input id="teamId" type="hidden" value="<%=teamDetails.id%>" />
					</div>
					<div class="form-group">
						<label class="col-md-4 control-label"></label>
						<div class="col-md-8">
							<button type="submit" class="btn btn-primary"><i class="fa fa-check-circle"></i> Update</button>
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
<!-- // Form END -->




</div>	
	
		
		</div>
		<!-- // Content END -->
		
		<div class="clearfix"></div>
		<!-- // Sidebar menu & content wrapper END -->

		<%@include file="footer.jsp" %>
		
		<!-- // Footer END -->
		
	</div>
	<!-- // Main Container Fluid END -->
	

	<!-- Global -->
	<script data-id="App.Config">
		var App = {};	var basePath = '',
			commonPath = '../assets/',
			rootPath = '../',
			DEV = false,
			componentsPath = '../assets/components/';

		var primaryColor = '#3695d5',
			dangerColor = '#b55151',
			successColor = '#609450',
			infoColor = '#4a8bc2',
			warningColor = '#ab7a4b',
			inverseColor = '#45484d';

		var themerPrimaryColor = primaryColor;

	</script>
	<script>
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

	<script src="/assets/components/library/bootstrap/js/bootstrap.min.js?v=v1.0.2&sv=v0.0.1"></script>
	<script src="/assets/components/plugins/nicescroll/jquery.nicescroll.min.js?v=v1.0.2&sv=v0.0.1"></script>
	<script src="/assets/components/plugins/breakpoints/breakpoints.js?v=v1.0.2&sv=v0.0.1"></script>
	<script src="/assets/components/plugins/preload/pace/pace.min.js?v=v1.0.2&sv=v0.0.1"></script>
	<script src="/assets/components/plugins/preload/pace/preload.pace.init.js?v=v1.0.2&sv=v0.0.1"></script>
	<script src="/assets/components/core/js/animations.init.js?v=v1.0.2"></script>
	<script src="/assets/components/common/forms/validator/assets/lib/jquery-validation/dist/jquery.validate.min.js?v=v1.0.2&sv=v0.0.1"></script>
	<script src="/assets/components/common/forms/validator/assets/custom/form-validator.init.js?v=v1.0.2&sv=v0.0.1"></script>
	<script src="/assets/components/common/forms/elements/fuelux-checkbox/fuelux-checkbox.js?v=v1.0.2&sv=v0.0.1"></script>
	<script src="/assets/components/core/js/sidebar.main.init.js?v=v1.0.2"></script>
	<script src="/assets/components/core/js/sidebar.discover.init.js?v=v1.0.2"></script>
	<script src="/assets/components/core/js/core.init.js?v=v1.0.2"></script>


	<script src="/assets/components/common/forms/elements/bootstrap-select/assets/lib/js/bootstrap-select.js?v=v1.0.2&sv=v0.0.1"></script>
	<script src="/assets/components/common/forms/elements/bootstrap-select/assets/custom/js/bootstrap-select.init.js?v=v1.0.2&sv=v0.0.1"></script>
	<script src="/assets/components/common/forms/elements/select2/assets/lib/js/select2.js?v=v1.0.2&sv=v0.0.1"></script>
	<script src="/assets/components/common/forms/elements/select2/assets/custom/js/select2.init.js?v=v1.0.2&sv=v0.0.1"></script>
</body>
</html>