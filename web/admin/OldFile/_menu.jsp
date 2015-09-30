<div id="menu" class="hidden-print hidden-xs">

  <div id="sidebar-discover-wrapper">
    <ul class="list-unstyled">

      <li>

        <a href="<%=cabGuardBaseController.getBaseUrl(request)+"admin"%>" ><span class="badge pull-right badge-primary hidden-md"></span><i></i><span>Dashboard</span></a>

      </li>

      <li>
        <a href="#sidebar-discover-media" class="glyphicons picture" data-toggle="sidebar-discover"><span class="badge pull-right badge-primary hidden-md">2</span><i></i><span>User</span></a>
        <div id="sidebar-discover-media" class="sidebar-discover-menu">
          <div class="innerAll text-center border-bottom text-muted-dark">
            <strong>User List</strong>
            <button class="btn btn-xs btn-default close-discover"><i class="fa fa-fw fa-times"></i></button>
          </div>
          <ul class="animated fadeIn">
            <li><a href="<%=cabGuardBaseController.getBaseUrl(request)+"admin/user/add"%>"><i class="fa fa-video-camera"></i>Add new</a></li>
            <li><a href="<%=cabGuardBaseController.getBaseUrl(request)+"admin"%>"><i class="fa fa-video-camera"></i> All admin user</a></li>
            <li><a href="<%=cabGuardBaseController.getBaseUrl(request)+"admin/user/list"%>"><i class="fa fa-camera"></i> All member user</a></li>
          </ul>
        </div>
      </li>

      <li>
        <a href="#teamManagement" class="glyphicons picture" data-toggle="sidebar-discover"><span class="badge pull-right badge-primary hidden-md">2</span><i></i><span>Team</span></a>
        <div id="teamManagement" class="sidebar-discover-menu">
          <div class="innerAll text-center border-bottom text-muted-dark">
            <strong>Team Management</strong>
            <button class="btn btn-xs btn-default close-discover"><i class="fa fa-fw fa-times"></i></button>
          </div>
          <ul class="animated fadeIn">
            <li><a href="<%=cabGuardBaseController.getBaseUrl(request)+"admin/team/add"%>"><i class="fa fa-video-camera"></i> Add new</a></li>
            <li><a href="<%=cabGuardBaseController.getBaseUrl(request)+"admin/team/management"%>"><i class="fa fa-camera"></i>Management</a></li>
          </ul>
        </div>
      </li>
    </ul>
  </div>
</div>
