<%@ page import="controller.service.ImageTalkBaseController" %>
<%@ page import="model.datamodel.app.Login" %>
<%@ page import="model.AdminLoginModel" %>
<%@ page import="java.util.ArrayList" %>
<%
  ImageTalkBaseController imageTalkBaseController = new ImageTalkBaseController();
  Login login = new Login();
  if(imageTalkBaseController.isSessionValid(request)){
    login = imageTalkBaseController.getUserLoginFromSession(request);
  }else{
    response.sendRedirect("/admin/login");
  }
  AdminLoginModel adminLoginModel = new AdminLoginModel();
  ArrayList<Login> loginsList = adminLoginModel.getAllExceptMe(login.u_id);

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
        App user
        <small>all admin user list</small>
      </h1>
      <ol class="breadcrumb">
        <li><a href="<%=imageTalkBaseController.getBaseUrl(request)+"admin"%>"><i class="fa fa-dashboard"></i> Dashboard</a></li>
        <li>Admin user management</li>
      </ol>
    </section>

    <!-- Main content -->
    <section class="content">
      <div class="row">
        <div class="col-xs-12">
          <div class="box">
            <div class="box-header">
              <h3 class="box-title">
                <button class="btn btn-block btn-primary btn-lg" onclick="javascript:location.href ='<%=imageTalkBaseController.getBaseUrl(request)+"admin/admin_user/add"%>';">Add Admin user</button>
              </h3>
            </div><!-- /.box-header -->
            <div class="box-body">
              <table class="table table-bordered table-striped" id="example1">
                <thead>
                <tr>
                  <th>Name</th>
                  <th>Email</th>
                  <th>Type</th>
                  <th>Action</th>
                </tr>
                </thead>
                <tbody>
                <%

                  for(Login temLogin:loginsList){
                %>

                <tr>
                  <td><%=temLogin.user.firstName+" "+temLogin.user.lastName%></td>
                  <td><%=temLogin.email%></td>
                  <%
                    String userType="";
                    String onclickMethod = "";
                    String onclickActionStr = "";
                    switch (temLogin.type){
                      case 1:
                        userType ="Admin";
                        onclickMethod = "";
                        break;
                      case 2:
                        userType ="Team lead";
                        onclickMethod = "makeUser(this,"+temLogin.id+")";
                        onclickActionStr = "Change to user";
                        break;
                      case 3:
                        userType ="Member";
                        onclickMethod = "makeTeamLead(this,"+temLogin.id+")";
                        onclickActionStr = "Make team lead";
                        break;
                      default:
                        break;

                    }


                  %>
                  <td class="UserType"><%=userType%></td>
                  <td>
                    <div class="btn-group">
                      <button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown">
                        Action
                      </button>
                      <ul class="dropdown-menu pull-left">
                        <li><a href="javascript:void(0)" onclick="<%=onclickMethod%>"><%=onclickActionStr%></a></li>
                        <li><a href="javascript:void(0)" onclick="deleteAppUer(this,<%=temLogin.user.id%>,<%=temLogin.id%>)">Delete</a></li>
                      </ul>
                    </div>
                    <div class="btn-group" ><img class="loadingImg" src="/assets/img/loding.gif" style="display: none;"/></div>
                  </td>
                </tr>


                <!-- // Table body END -->
                <%	} %>
                </tbody>
              </table>

            </div><!-- /.box-body -->
          </div><!-- /.box -->
        </div><!-- /.col -->
      </div><!-- /.row -->
      <div class="row">
        <div class="small-box bg-red" id="errorMsgDiv">

        </div>
      </div>
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
<!-- DataTables -->
<script src="/assets/plugins/datatables/jquery.dataTables.min.js"></script>
<script src="/assets/plugins/datatables/dataTables.bootstrap.min.js"></script>
<!-- SlimScroll -->
<script src="/assets/plugins/slimScroll/jquery.slimscroll.min.js"></script>
<!-- FastClick -->
<script src="/assets/plugins/fastclick/fastclick.min.js"></script>
<!-- AdminLTE App -->
<script src="/assets/dist/js/app.min.js"></script>
<!-- AdminLTE for demo purposes -->
<script src="/assets/dist/js/demo.js"></script>
<!-- page script -->

<script>
  $(function () {
    $("#example1").DataTable();
//    $('#example2').DataTable({
//      "paging": true,
//      "lengthChange": false,
//      "searching": false,
//      "ordering": true,
//      "info": true,
//      "autoWidth": false
//    });
  });
  function makeTeamLead(elem,login_id){
    var loadingImg = $(elem).parents("td").first().find(".loadingImg").first();

    var url  = $("#base_url").val()+"admin/user/update/type/teamlead";
    $(loadingImg).show();
    $.ajax({
      url: url,
      method: "POST",
      data: {"login_id": login_id},
      success: function (data) {
        if(data.responseStat.status){
          $(loadingImg).hide();
          $(elem).parents("tr").first().find(".UserType").first().html("Team lead");
          $(elem).attr("onclick","makeUser(this,"+login_id+")");
          $(elem).html("Change to user");
        }else{
        }
      }
    });


    console.log("Trigger");
    return false;
  }
  function makeUser(elem,login_id){
    var loadingImg = $(elem).parents("td").first().find(".loadingImg").first();

    var url  = $("#base_url").val()+"admin/user/update/type/user";
    $(loadingImg).show();
    $.ajax({
      url: url,
      method: "POST",
      data: {"login_id": login_id},
      success: function (data) {
        if(data.responseStat.status){
          $(loadingImg).hide();
          $(elem).parents("tr").first().find(".UserType").first().html("Member");
          $(elem).attr("onclick","makeTeamLead(this,"+login_id+")");
          $(elem).html("Make Team Lead");
        }else{

        }
      }
    });


    console.log("Trigger");
    return false;
  }
  function deleteAppUer(elem,user_id,login_id){
    $("#errorMsgDiv").html("");
    var r = confirm("Are you sure ?");
    if (r) {
      var loadingImg = $(elem).parents("td").first().find(".loadingImg").first();
      var url = $("#base_url").val()+"admin/operation/admin_user/delete";
      $(loadingImg).show();
      $.ajax({
        url: url,
        method: "POST",
        data: {
          "u_id": user_id,
          "login_id": login_id
        },
        success: function (data) {
          $(loadingImg).hide();
          if(data.responseStat.status){
            var row = $(elem).parents("tr").first();
            if ( !$(row).hasClass('selected') ) {
              $(row).addClass('selected');

            }
            var userTable = $("#example1").DataTable();
            userTable.row('.selected').remove().draw( false );
          }else{

            $("#errorMsgDiv").html(data.responseStat.msg);
            document.getElementById( 'errorMsgDiv' ).scrollIntoView();
          }

          console.log(data);
        }
      });
    } else {

    }
  }

</script>
</body>
</html>
