<%@ page import="java.util.Date" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.sql.Timestamp" %>
<header class="main-header">

  <!-- Logo -->
  <a href="index2.html" class="logo">
    <!-- mini logo for sidebar mini 50x50 pixels -->
    <span class="logo-mini"><b>I</b>TALK</span>
    <!-- logo for regular state and mobile devices -->
    <span class="logo-lg">Talk</span>
  </a>

  <!-- Header Navbar: style can be found in header.less -->
  <nav class="navbar navbar-static-top" role="navigation">
    <!-- Sidebar toggle button-->
    <a href="#" class="sidebar-toggle" data-toggle="offcanvas" role="button">
      <span class="sr-only">Toggle navigation</span>
    </a>
    <!-- Navbar Right Menu -->
    <div class="navbar-custom-menu">
      <ul class="nav navbar-nav">

        <li class="dropdown user user-menu">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown">
            <span class="hidden-xs"><%=login.user.firstName+" "+login.user.lastName%></span>
          </a>
          <ul class="dropdown-menu">
            <!-- User image -->
            <li class="user-header" style="height: 84px;">

              <p>
               Admin of ImageTalk
                  <%
                      String date="";
                      if(login.created_date!=null){
                          date = login.created_date.split(" ")[0];


                          try{
                              SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
                              DateFormat showDateFormat = new SimpleDateFormat("MMMM, dd yyyy");
                              Date parsedDate = dateFormat.parse(login.created_date);
                              Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
                          %>
                            <small>Member since  <%=showDateFormat.format(parsedDate)%>
                          <%
                          }catch(Exception e){//this generic but you can control another types of exception

                          }
                      }

                %>

                </small>
              </p>
            </li>
            <!-- Menu Body -->
            <%--<li class="user-body">--%>
              <%--<div class="col-xs-4 text-center">--%>
                <%--<a href="#">Followers</a>--%>
              <%--</div>--%>
              <%--<div class="col-xs-4 text-center">--%>
                <%--<a href="#">Sales</a>--%>
              <%--</div>--%>
              <%--<div class="col-xs-4 text-center">--%>
                <%--<a href="#">Friends</a>--%>
              <%--</div>--%>
            <%--</li>--%>
            <!-- Menu Footer-->
            <li class="user-footer">
              <%--<div class="pull-left">--%>
                <%--<a href="#" class="btn btn-default btn-flat">Profile</a>--%>
              <%--</div>--%>
              <div class="pull-right">
                <a href="javascript:void(0) " onclick="doLogout()" class="btn btn-default btn-flat">Sign out</a>
              </div>
            </li>
          </ul>
        </li>

      </ul>
    </div>

  </nav>
</header>
<script>
  function doLogout(){

    var url = $("#base_url").val()+"admin/logout";
    $.ajax({
      url: url,
      method: "POST",
      success: function (data) {

        if(data.responseStat.status){
          window.location = $("#base_url").val()+"admin/login";
        }
      }
    });


    console.log("Trigger");
    return false;
  }
</script>