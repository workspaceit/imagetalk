<aside class="main-sidebar">
  <section class="sidebar">
    <ul class="sidebar-menu">
      <li class="header">MAIN NAVIGATION</li>
      <li class="active">
        <a href="<%=imageTalkBaseController.getBaseUrl(request)+"admin"%>">
          <i class="fa fa-envelope"></i> <span>Dashboard</span>
        </a>
      </li>


      <li class="treeview">
        <a href="#">
          <i class="fa fa-user-secret"></i>
          <span>User</span>
          <i class="fa fa-angle-left pull-right"></i>
        </a>
        <ul class="treeview-menu">
          <li><a href="<%=imageTalkBaseController.getBaseUrl(request)+"admin/admin_user/management"%>"><i class="fa fa-circle-o"></i> All member</a></li>
          <li><a href="<%=imageTalkBaseController.getBaseUrl(request)+"admin/admin_user/add"%>"><i class="fa fa-circle-o"></i> Add new user</a></li>
        </ul>
      </li>

      <li class="treeview">
        <a href="#">
          <i class="fa fa-user"></i>
          <span>App User</span>
          <i class="fa fa-angle-left pull-right"></i>
        </a>
        <ul class="treeview-menu">
          <li><a href="<%=baseUrl%>admin/app/user"><i class="fa fa-circle-o"></i> All member</a></li>
          <li><a href="javascript:void(0)"><i class="fa fa-circle-o"></i> Add new user</a></li>

          <li><a href="javascript:void(0)"><i class="fa fa-circle-o"></i> Active user</a></li>
          <li><a href="javascript:void(0)"><i class="fa fa-circle-o"></i> Add new user</a></li>
          <li><a href="javascript:void(0)"><i class="fa fa-circle-o"></i> Inactive user</a></li>
          <li><a href="javascript:void(0)"><i class="fa fa-circle-o"></i> Banned user</a></li>
        </ul>
      </li>

      <li class="treeview">
        <a href="#">
          <i class="fa fa-gears"></i>
          <span>Setup</span>
          <i class="fa fa-angle-left pull-right"></i>
        </a>
        <ul class="treeview-menu">
          <li><a href="javascript:void(0)"><i class="fa fa-circle-o"></i>Upload logo</a></li>
        </ul>
      </li>

      <li class="treeview">
        <a href="#">
          <i class="fa fa-bullhorn"></i>
          <span>Complain list</span>
          <i class="fa fa-angle-left pull-right"></i>
        </a>
        <ul class="treeview-menu">
          <li><a href="javascript:void(0)"><i class="fa fa-circle-o"></i> All</a></li>
        </ul>
      </li>

      <li class="treeview">
        <a href="#">
          <i class="fa fa-dollar"></i>
          <span>Job details</span>
          <i class="fa fa-angle-left pull-right"></i>
        </a>
        <ul class="treeview-menu">
          <li><a href="javascript:void(0)"><i class="fa fa-circle-o"></i> Total service list</a></li>
        </ul>
      </li>
      <li class="treeview">
        <a href="#">
          <i class="fa fa-envelope-o"></i>
          <span>Send message to user</span>
          <i class="fa fa-angle-left pull-right"></i>
        </a>
      </li>

      <%--<li class="treeview">--%>
        <%--<a href="#">--%>
          <%--<i class="fa fa-laptop"></i>--%>
          <%--<span>Team</span>--%>
          <%--<i class="fa fa-angle-left pull-right"></i>--%>
        <%--</a>--%>
        <%--<ul class="treeview-menu">--%>
          <%--<li><a href="<%=imageTalkBaseController.getBaseUrl(request)+"admin/team/management"%>"><i class="fa fa-circle-o"></i> All team</a></li>--%>
          <%--<li><a href="<%=imageTalkBaseController.getBaseUrl(request)+"admin/team/add"%>"><i class="fa fa-circle-o"></i> Add new team</a></li>--%>
        <%--</ul>--%>
      <%--</li>--%>
      <%--<li class="treeview">--%>
        <%--<a href="#">--%>
          <%--<i class="fa fa-laptop"></i>--%>
          <%--<span>Gps Server</span>--%>
          <%--<i class="fa fa-angle-left pull-right"></i>--%>
        <%--</a>--%>
        <%--<ul class="treeview-menu">--%>
          <%--<li><a href="<%=imageTalkBaseController.getBaseUrl(request)+"admin/gps/status"%>"><i class="fa fa-circle-o"></i>Status</a></li>--%>
          <%--<li><a href="<%=imageTalkBaseController.getBaseUrl(request)+"admin/gps/settings"%>"><i class="fa fa-circle-o"></i>Settings</a></li>--%>
        <%--</ul>--%>
      <%--</li>--%>
    </ul>
</section>
<!-- /.sidebar -->
</aside>