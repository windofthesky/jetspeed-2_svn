<%--
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

--%>
<%@ page contentType="text/html" %>
<%@ page import="java.util.Collection" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="org.apache.jetspeed.ui.Jetui" %>
<%@ page import="org.apache.jetspeed.request.RequestContext" %>
<%@ page import="org.apache.jetspeed.om.page.ContentFragment" %>
<%@ page import="org.apache.jetspeed.om.page.ContentPage" %>
<%@ page import="org.apache.jetspeed.portlets.layout.ColumnLayout" %>
<%@ page import="org.apache.jetspeed.decoration.DecoratorAction" %>
<%@ page import="org.apache.jetspeed.PortalReservedParameters" %>
<%@ page import="org.apache.jetspeed.administration.PortalConfiguration" %>
<%@ page import="org.apache.jetspeed.administration.PortalConfigurationConstants" %>

<%
  Jetui jetui = (Jetui)request.getAttribute("jetui");
  PortalConfiguration pc = jetui.getPortalConfiguration();
  RequestContext rc = (RequestContext)request.getAttribute(RequestContext.REQUEST_PORTALENV);
  Map userInfo = jetui.getUserAttributes(rc);
  ContentPage portalPage = rc.getPage();
  ContentFragment maximized = (ContentFragment)request.getAttribute(PortalReservedParameters.MAXIMIZED_FRAGMENT_ATTRIBUTE);
  ColumnLayout columnLayout = (ColumnLayout)request.getAttribute("columnLayout");
  
  String navContent = null;
  String tbContent = null;
  
  ContentFragment pageNav = jetui.getContentFragment("jsPageNavigator",  rc);
  ContentFragment toolbox = jetui.getContentFragment("jsToolbox",  rc);
  
  if (maximized != null)
  {
      navContent = jetui.renderPortletWindow(pageNav.getId(), pageNav.getName(), rc);
      tbContent = jetui.renderPortletWindow(toolbox.getId(), pageNav.getName(), rc);
  }
  else
  {
      navContent = jetui.getRenderedContent(pageNav, rc);
      tbContent = jetui.getRenderedContent(toolbox, rc);
  }
  
  String breadcrumbs = jetui.renderPortletWindow("jsBreadcrumbMenu", "j2-admin::BreadcrumbMenu", rc);
  
  String encoding = "text/html"; 
  if (response.getCharacterEncoding() != null)
  {
      encoding += "; charset=" + response.getCharacterEncoding();
  }
  String baseUrl = jetui.getBaseURL(rc);
  String pageDec = jetui.getTheme(rc).getPageLayoutDecoration().getName();
  
  String portalContextPath = request.getContextPath();
  String portalServletPath = request.getServletPath();
  String portalPagePath = rc.getPortalURL().getPath();
  if (portalPagePath == null || "".equals(portalPagePath)) {
      portalPagePath = "/";
  }
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta http-equiv="content-type" content="<%=encoding%>"/>
<title><%=jetui.getTitle(rc)%></title>
<link rel="shortcut icon" href="<%=baseUrl%>images/jetspeed.jpg" type="image/x-icon" />
<script type="text/javascript" src="<%=request.getContextPath()%>/javascript/yui/build/yui/yui-min.js"></script>
<script language="javascript">
var JetuiConfiguration = {
	engine: "<%=pc.getString(PortalConfigurationConstants.JETUI_RENDER_ENGINE)%>",
	ajaxTransport: "<%=pc.getString(PortalConfigurationConstants.JETUI_AJAX_TRANSPORT)%>",
	dragMode: "<%=pc.getString(PortalConfigurationConstants.JETUI_DRAG_MODE)%>",
	portletStyle: "<%=pc.getString(PortalConfigurationConstants.JETUI_STYLE_PORTLET)%>",
	layoutStyle: "<%=pc.getString(PortalConfigurationConstants.JETUI_STYLE_LAYOUT)%>",
	dragHandleStyle: "<%=pc.getString(PortalConfigurationConstants.JETUI_STYLE_DRAG_HANDLE)%>",
	portalContextPath: "<%=portalContextPath%>",
	portalServletPath: "<%=portalServletPath%>",
	portalPagePath: "<%=portalPagePath%>"
};
var JETUI_YUI = {
  base: "<%=request.getContextPath()%>/javascript/yui/build/",
  timeout: 10000, 
  debug: true, 
  useBrowserConsole: true,
  config: JetuiConfiguration,
  modules: {
    'jetui-portal': { fullpath: "<%=request.getContextPath()%>/javascript/jetspeed/jetui/jetui-portal.js" }
  }
}; // ({classNamePrefix: 'jet'})
</script>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/jetui/css/portal.css"/>

<%=jetui.includeHeaderResources(rc)%>

<%
// define layout objects (preferedLocale, rootFragment, site, theme, layoutDecorator)
// decorator macros
// defineNavigationConstants()

for (String style : jetui.getStyleSheets(rc))
{%>
<link rel="stylesheet" type="text/css" media="screen, projection" href="<%=request.getContextPath()%>/<%=style%>"/>
<%}
%>

<body class="yui-skin-sam">
<div id='jetspeedZone'>
<div id="layout-<%=pageDec%>" class="layout-<%=pageDec%>" >
<div class="header">
<h1 class="logo">Jetspeed 2</h1>
<div class="menu">
&nbsp;<span style='position: absolute; left: 0px; top: 50px;' id='jstbLeftToggle' class='jstbToggle1'></span><span id='jstbRightToggle' class='jstbToggle2' style='position: absolute; right: 0px; top: 50px;'></span>
</div>
<%if (request.getUserPrincipal() != null) {%>
<span class="layout-statusarea"><b><%=userInfo.get("user.name.given")%> <%=userInfo.get("user.name.family")%></b> | Profile | Tasks (5) | Notifications (2) | <a href="<%=request.getContextPath()%>/login/logout?org.apache.jetspeed.login.destination=<%=request.getContextPath()%>/ui">Log out</a></span>
<% } %>
<!-- <span class="layout-search"><input type='text' size='14'/></span><span class="layout-search2"><img height='18' src="<%=request.getContextPath()%>/images/search.png"/></span>  -->
</div> <!-- end header -->

<!-- main area -->
<table cellpadding="0" cellspacing="0" border="0" width="100%" id="main">
<tr>         
<td>
<div id='jstbLeft' class='jsLeftToolbar'>
<div id='template-top2.jsPageNavigator' class='xportal-layout-cell'>
<div id="jsNavigator2" class="portlet <%=pageDec%>">
    <div class="PTitle" >
      <div class="PTitleContent">Navigator</div>
    </div>
    <div class="PContentBorder">
      <div class="PContent"><div id="nav-main"><%=navContent %></div></div>
    </div>
<a class="addthis_button" href="http://www.addthis.com/bookmark.php?v=250&amp;pub=xa-4b0265f81058c137"><img src="http://s7.addthis.com/static/btn/sm-share-en.gif" width="83" height="16" alt="Bookmark and Share" style="border:0"/></a><script type="text/javascript" src="http://s7.addthis.com/js/250/addthis_widget.js#pub=xa-4b0265f81058c137"></script>
</div>
</div>
</div>
</td>
<td id='jsMainarea' class='jsMainarea'>
<div class="PContent"><span style="line-height:0.005px;">&nbsp;</span><%=breadcrumbs%></div>
<div id="jsFragments" class="portal-nested-layout portal-nested-layout-TwoColumns">
<%
    if (maximized != null)
    {
        String content = jetui.getRenderedContent(maximized, rc);
        request.setAttribute("content", content);
        String decorator = maximized.getDecorator();
		if (decorator == null)
		    decorator = pageDec;
        request.setAttribute("decorator", decorator);
        request.setAttribute("fragment", maximized);
		request.setAttribute("coordinate", columnLayout.getCoordinate(maximized));
%>
<div id="column_id_0"
     class="portal-layout-column"
	 locked="<%=maximized.isLocked()%>" 
     style="float:left; width:100%; background-color: #ffffff;">
<jsp:include page="jetui-portlet.jsp"/>
</div>  
<%  
    }
    else
    {
        int index = 0;
        for (Collection<ContentFragment> collections : columnLayout.getColumns())
        {
            String columnFloat = columnLayout.getColumnFloat(index);
            String columnWidth = columnLayout.getColumnWidth(index);
        // class="portal-layout-column portal-layout-column-${layoutType}-${columnIndex}"           
%>
<div id="column_id_<%=index%>" 
     class="portal-layout-column" column='<%=index%>'
	 locked='false' 
     style="float:<%=columnFloat%>; min-height: 100px; width:<%=columnWidth%>;">

<%      
            int subindex = 0;
            for (ContentFragment fragment : collections)
            {
                   //String content = jetui.renderPortletWindow(fragment.getId(), fragment.getName(), rc);
                   String content = jetui.getRenderedContent((ContentFragment)fragment, rc);
                   request.setAttribute("content", content);                    
                   String decorator = fragment.getDecorator(); 
                   if (decorator == null)
    	       		    decorator = pageDec;
                   request.setAttribute("decorator", decorator);                    
                   request.setAttribute("fragment", fragment);      
                   request.setAttribute("coordinate", columnLayout.getCoordinate(fragment));
%>
<jsp:include page="jetui-portlet.jsp"/>
<%          
                    subindex++;
            }
            index++;
%>
</div>
<%
        }
    }
%>
</div>
</td>
<td>
<div id='jstbRight' class='jsRightToolbar'>
<div id='jsToolbox' class='xportal-layout-cell'>
<div id="jsToolbox2" class="portlet <%=pageDec%>">
    <div class="PTitle" >
      <div class="PTitleContent">Toolbox</div>
    </div>
    <div class="PContentBorder">
      <div class="PContent"><%=tbContent %></div>
    </div>
</div>
</div> 
</div>
</td>
</tr>
</table>

</div> <!-- end layout -->
</div>

<%
for (ContentFragment fragment : columnLayout.getDetachedPortlets())
{
	String x = fragment.getProperty(ContentFragment.X_PROPERTY_NAME);
	String y = fragment.getProperty(ContentFragment.Y_PROPERTY_NAME);
    String content = jetui.getRenderedContent((ContentFragment)fragment, rc);
%>
   <div id='<%=fragment.getId()%>' detached='true' locked='<%=fragment.isLocked()%>' name='<%=fragment.getName()%>' column='0' row='0' x='<%=x%>' y='<%=y%>' style='position: absolute; top: <%=x%>px; left: <%=y%>px;'>
<%=content%>
   </div>			    
<% } %>

<script src="<%=request.getContextPath()%>/jetui/engine/portal.js"></script>

<div id="jsPortletTemplate" class="portal-layout-cell yui-dd-draggable yui-dd-drop" style="display: none">
  <div class="portlet <%=pageDec%>">
    <div class="PTitle">
      <div class="PTitleContent">Loading...</div>
      <div class="PActionBar">
        <span style='cursor: pointer; z-index: 1000;' id='jetspeed-close-XXX' title="close" class="portlet-action-close"><img src="<%=request.getContextPath()%>/decorations/portlet/jetspeed/images/close.gif" alt="Close" border="0" /></span>
      </div>
    </div>
    <div class="PContentBorder">
      <div class="PContent"></div>
    </div>
  </div>
</div>

</body>
</html>
