<%@ page import="controller.service.ImageTalkBaseController" %>
<%
	ImageTalkBaseController imageTalkBaseController = new ImageTalkBaseController();

%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<title>ImageTalk</title>
	<!-- Tell the browser to be responsive to screen width -->
	<meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
	<!-- Bootstrap 3.3.5 -->
	<link rel="stylesheet" href="/assets/bootstrap/css/bootstrap.min.css" >
	<!-- Font Awesome -->
	<link rel="stylesheet" href="/assets/css/font-awesome.min.css" >
	<!-- Ionicons -->
	<link rel="stylesheet" href="/assets/css/ionicons.min.css" >
	<!-- Theme style -->
	<link rel="stylesheet" href="/assets/dist/css/AdminLTE.min.css" >
	<!-- iCheck -->
	<link rel="stylesheet" href="/assets/plugins/iCheck/square/blue.css" >

	<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
	<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
	<!--[if lt IE 9]>
	<script src="/assets/js/html5shiv.js"></script>
	<script src="/assets/js/respond.js/1.3.0/respond.min.js"></script>
	<![endif]-->
</head>
<body class="hold-transition login-page">
<div class="login-box">
	<div class="login-logo">
		<a href="../../index2.html"><b>Image</b>Talk</a>
	</div><!-- /.login-logo -->
	<div class="login-box-body">
		<p class="login-box-msg" id="errorMsg">Sign in to start your session</p>
		<form  method="post" onsubmit="return doLogin()">
			<div class="form-group has-feedback">
				<input type="email" class="form-control" placeholder="Email" id="exampleInputEmail" >
				<span class="glyphicon glyphicon-envelope form-control-feedback"></span>
			</div>


			<div class="form-group has-feedback">
				<input type="password" class="form-control" placeholder="Password" id="exampleInputPassword" >
				<span class="glyphicon glyphicon-lock form-control-feedback"></span>
			</div>
			<div class="row">
				<div class="col-xs-8">
					<div class="checkbox icheck">

					</div>
				</div><!-- /.col -->
				<div class="col-xs-4">
					<button type="submit" class="btn btn-primary btn-block btn-flat">Sign In</button>
				</div><!-- /.col -->
			</div>
		</form>





	</div><!-- /.login-box-body -->
</div><!-- /.login-box -->
<input type="hidden" id="base_url" value="<%=imageTalkBaseController.getBaseUrl(request)%>" />
<!-- jQuery 2.1.4 -->
<script src="/assets/plugins/jQuery/jQuery-2.1.4.min.js"></script>
<!-- Bootstrap 3.3.5 -->
<script src="/assets/bootstrap/js/bootstrap.min.js"></script>
<!-- iCheck -->
<script src="/assets/plugins/iCheck/icheck.min.js"></script>
<script>
	$(function () {
		$('input').iCheck({
			checkboxClass: 'icheckbox_square-blue',
			radioClass: 'iradio_square-blue',
			increaseArea: '20%' // optional
		});
	});
</script>
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

				}
				$("#errorMsg").html(data.responseStat.msg);
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
	$(document).ready(function(){
		$("#exampleInputEmail").focus();
	});
</script>
</body>
</html>
