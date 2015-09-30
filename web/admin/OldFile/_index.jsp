<%@ page import="controller.service.ImageTalkBaseController" %>
<%@ page import="model.datamodel.Login" %>
<%@ page import="model.LoginModel" %>
<%@ page import="model.TeamModel" %>
<%
	ImageTalkBaseController imageTalkBaseController = new ImageTalkBaseController();
	Login login = new Login();
	if(imageTalkBaseController.isSessionValid(request)){
		login = imageTalkBaseController.getUserLoginFromSession(request);
	}else{
		response.sendRedirect("/admin/login");
	}
	LoginModel loginModel = new LoginModel();
	TeamModel teamModel = new TeamModel();

	int totalMember  =  loginModel.getCount();
	int totalTeam  =  teamModel.getCount();

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



<div class="innerLR">

	<h2 class="margin-none">Analytics &nbsp;<i class="fa fa-fw fa-pencil text-muted"></i></h2>

	<div class="separator-h"></div>
				
	<div class="row">
		<div class="col-md-8">

			<div class="row">
				<div class="col-md-6">
					<div class="widget innerAll text-center">
						<h3 class="innerT">Total member</h3>
						<p class="innerB margin-none text-xlarge text-condensed strong text-primary"><%=totalMember%></p>

					</div>
				</div>
				<div class="col-md-6">
					<div class="widget innerAll text-center">
						<h3 class="innerT">Total team</h3>
						<p class="innerB margin-none text-xlarge text-condensed strong text-primary"><%=totalTeam%></p>

					</div>
				</div>

				<!-- //Col -->
			</div>
			<!-- //Row -->


		</div>
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

	</script>
	
	<script src="/assets/components/library/bootstrap/js/bootstrap.min.js?v=v1.0.2&sv=v0.0.1"></script>
	<script src="/assets/components/plugins/nicescroll/jquery.nicescroll.min.js?v=v1.0.2&sv=v0.0.1"></script>
	<script src="/assets/components/plugins/breakpoints/breakpoints.js?v=v1.0.2&sv=v0.0.1"></script>
	<script src="/assets/components/plugins/preload/pace/pace.min.js?v=v1.0.2&sv=v0.0.1"></script>
	<script src="/assets/components/plugins/preload/pace/preload.pace.init.js?v=v1.0.2&sv=v0.0.1"></script>
	<script src="/assets/components/core/js/animations.init.js?v=v1.0.2"></script>
	<script src="/assets/components/modules/admin/charts/flot/assets/lib/jquery.flot.js?v=v1.0.2&sv=v0.0.1"></script>
	<script src="/assets/components/modules/admin/charts/flot/assets/lib/jquery.flot.resize.js?v=v1.0.2&sv=v0.0.1"></script>
	<script src="/assets/components/modules/admin/charts/flot/assets/lib/plugins/jquery.flot.tooltip.min.js?v=v1.0.2&sv=v0.0.1"></script>
	<script src="/assets/components/modules/admin/charts/flot/assets/custom/js/flotcharts.common.js?v=v1.0.2&sv=v0.0.1"></script>
	<script src="/assets/components/modules/admin/charts/flot/assets/custom/js/flotchart-line-2.init.js?v=v1.0.2&sv=v0.0.1"></script>
	<script src="/assets/components/modules/admin/charts/flot/assets/custom/js/flotchart-mixed-1.init.js?v=v1.0.2&sv=v0.0.1"></script>
	<script src="/assets/components/modules/admin/charts/flot/assets/custom/js/flotchart-bars-horizontal.init.js?v=v1.0.2&sv=v0.0.1"></script>
	<script src="/assets/components/modules/admin/charts/easy-pie/assets/lib/js/jquery.easy-pie-chart.js?v=v1.0.2&sv=v0.0.1"></script>
	<script src="/assets/components/modules/admin/charts/easy-pie/assets/custom/easy-pie.init.js?v=v1.0.2&sv=v0.0.1"></script>
	<script src="/assets/components/modules/admin/charts/sparkline/jquery.sparkline.min.js?v=v1.0.2&sv=v0.0.1"></script>
	<script src="/assets/components/modules/admin/charts/sparkline/sparkline.init.js?v=v1.0.2&sv=v0.0.1"></script>
	<script src="/assets/components/modules/admin/maps/vector/assets/lib/jquery-jvectormap-1.2.2.min.js?v=v1.0.2&sv=v0.0.1"></script>
	<script src="/assets/components/modules/admin/maps/vector/assets/lib/maps/jquery-jvectormap-world-mill-en.js?v=v1.0.2&sv=v0.0.1"></script>
	<script src="/assets/components/modules/admin/maps/vector/assets/custom/maps-vector.world-map-markers.init.js?v=v1.0.2&sv=v0.0.1"></script>
	<script src="/assets/components/core/js/sidebar.main.init.js?v=v1.0.2"></script>
	<script src="/assets/components/core/js/sidebar.discover.init.js?v=v1.0.2"></script>
	<script src="/assets/components/core/js/core.init.js?v=v1.0.2"></script>
</body>
</html>