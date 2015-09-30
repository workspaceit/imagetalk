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
			<div class="row innerLR">
			
				<!-- Column -->
				<div class="col-md-4">
				
					<!-- Group -->
					<div class="form-group">
						<label class="col-md-4 control-label" for="teamName">First name</label>
						<div class="col-md-8"><input class="form-control" id="f_name"  type="text" /></div>
					</div>
					<div class="form-group">
						<label class="col-md-4 control-label" for="teamName">Last name</label>
						<div class="col-md-8"><input class="form-control" id="l_name"  type="text" /></div>
					</div>
					<div class="form-group">
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
					<div class="form-group">
						<label class="col-md-4 control-label" for="teamName">Confirm Password</label>
						<div class="col-md-8"><input class="form-control" id="con_password"  type="text" /></div>
					</div>

					<div class="form-group">
						<label class="col-md-4 control-label" for="teamLeadSelect">Team lead</label>
						<div class="col-md-8"><select style="width: 100%;" id="teamLeadSelect">
								<input id="isTeamLeadSelected" type="checkbox" value="TEAM_LEAD" />
							</select>
						</div>
					</div>
					<div class="form-group">
						<label class="col-md-4 control-label"></label>
						<div class="col-md-8">
							<button type="submit" class="btn btn-primary"><i class="fa fa-check-circle"></i> Save</button>
							<button type="button" class="btn btn-default"><i class="fa fa-times"></i> Cancel</button>
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
		function addUser(){
			var f_name = ("#f_name").val();
			var l_name = ("#l_name").val();
			var address = ("#address").val();
			var email = ("#email").val();
			var password = ("#password").val();
			var con_password = ("#con_password").val();


			var teamName = $("#teamName").val();
			var teamLeadId = $("#teamLeadSelect").val()	;
			var url = $("#base_url").val()+"admin/operation/team/add";
			$.ajax({
				url: url,
				method: "POST",
				data: {
					"f_name": teamName,
					"l_name": teamLeadId,
					"":
				},
				success: function (data) {
					if(data.responseStat.status){

					}else{
						$("#createTeamErrorMsg").html(data.responseStat.msg);
					}
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