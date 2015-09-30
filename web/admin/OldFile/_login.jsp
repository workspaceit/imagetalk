<%@ page import="controller.service.ImageTalkBaseController" %><%
	ImageTalkBaseController imageTalkBaseController = new ImageTalkBaseController();

%>

<!DOCTYPE html>

<%@include file="head.jsp" %>

<body class=" loginWrapper">
	
	<!-- Main Container Fluid -->
	<div class="container-fluid menu-hidden">
		
				
		<!-- Content -->
		<div id="content">





<!-- row-app -->
<div class="row row-app">

	<!-- col -->
		<!-- col-separator.box -->
		<div class="col-separator col-unscrollable box">
			
			<!-- col-table -->
			<div class="col-table">
				
				<h4 class="innerAll margin-none border-bottom text-center"><i class="fa fa-lock"></i> Login to your Account</h4>

				<!-- col-table-row -->
				<div class="col-table-row">

					<!-- col-app -->
					<div class="col-app col-unscrollable">

						<!-- col-app -->
						<div class="col-app">
							<div class="login">
								<div class="placeholder text-center"><i class="fa fa-lock"></i></div>
								<div class="panel panel-default col-md-4 col-sm-6 col-sm-offset-3 col-md-offset-4">

								  <div class="panel-body">
								  	<form id="loginForm" role="form" action="index.html?lang=en" onsubmit="return doLogin()">

								  	  <div class="form-group">
									    <label for="exampleInputEmail">Email address</label>
									    <input type="email" class="form-control" id="exampleInputEmail" placeholder="Enter email">
									  </div>
									  <div class="form-group">
									    <label for="exampleInputPassword">Password</label>
									    <input type="password" class="form-control" id="exampleInputPassword" placeholder="Password">
									  </div>

									  <button type="submit" class="btn btn-primary btn-block">Login</button>

										<span id="loginErrorMsg"></span>
									</form>
								   
								  </div>
								
								</div>

								<div class="clearfix"></div>
							
							</div>


						</div>
						<!-- // END col-app -->

					</div>
					<!-- // END col-app.col-unscrollable -->

				</div>
				<!-- // END col-table-row -->
			
			</div>
			<!-- // END col-table -->
			
		</div>
		<!-- // END col-separator.box -->


</div>
<!-- // END row-app -->

	<input type="hidden" id="base_url" value="<%=imageTalkBaseController.getBaseUrl(request)%>" />
	

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
	
	<script src="/assets/components/library/bootstrap/js/bootstrap.min.js?v=v1.0.2&sv=v0.0.1"></script>
	<script src="/assets/components/plugins/nicescroll/jquery.nicescroll.min.js?v=v1.0.2&sv=v0.0.1"></script>
	<script src="/assets/components/plugins/breakpoints/breakpoints.js?v=v1.0.2&sv=v0.0.1"></script>
	<script src="/assets/components/plugins/preload/pace/pace.min.js?v=v1.0.2&sv=v0.0.1"></script>
	<script src="/assets/components/plugins/preload/pace/preload.pace.init.js?v=v1.0.2&sv=v0.0.1"></script>
	<script src="/assets/components/core/js/animations.init.js?v=v1.0.2"></script>
	<script src="/assets/components/core/js/core.init.js?v=v1.0.2"></script>

	<script>
		function doLogin(){
			$("#loginErrorMsg").html("");
			var email =$("#exampleInputEmail").val();
			var password = $("#exampleInputPassword").val()	;
			var url = "";
			url = "/login/admin/authenticate";
			enablDisableAll(false);
			$.ajax({
				url: url,
				method: "POST",
				data: {"email": email, "password": password},
				success: function (data) {
					enablDisableAll(true);
					if(data.responseStat.status){
						window.location = $("#base_url").val()+"admin";
					}else{
						$("#loginErrorMsg").html(data.responseStat.msg)
					}
				}
			});


			console.log("Trigger");
			return false;
		}
		function enablDisableAll(enable){
			if(enable){
				$('#loginForm').find("input,button").removeAttr("disabled");
			}else{
				$('#loginForm').find("input,button").attr("disabled","disabled");
			}
		}
		</script>
</body>
</html>