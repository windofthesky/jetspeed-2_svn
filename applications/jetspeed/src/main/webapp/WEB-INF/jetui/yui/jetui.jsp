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
<%@ page import="org.apache.jetspeed.om.page.Page" %>
<%@ page import="org.apache.jetspeed.om.page.ContentFragment" %>
<%@ page import="org.apache.jetspeed.om.page.Fragment" %>
<%@ page import="org.apache.jetspeed.portlets.layout.ColumnLayout" %>
<%@ page import="org.apache.jetspeed.om.page.ContentFragment" %>
<%@ page import="org.apache.jetspeed.decoration.DecoratorAction" %>
<%@ page import="org.apache.jetspeed.PortalReservedParameters" %>

<%
  Jetui jetui = (Jetui)request.getAttribute("jetui");
  RequestContext rc = (RequestContext)request.getAttribute(RequestContext.REQUEST_PORTALENV);
  Page portalPage = rc.getPage();
  ColumnLayout columnLayout = (ColumnLayout)request.getAttribute("columnLayout");
  String navContent = jetui.renderPortletWindow("_JetspeedNavigator", "j2-admin::JetspeedNavigator", rc);
  String tbContent = jetui.renderPortletWindow("_JetspeedToolbox", "j2-admin::JetspeedToolbox", rc);
  String encoding = "text/html"; 
  if (response.getCharacterEncoding() != null)
  {
      encoding += "; charset=" + response.getCharacterEncoding();
  }
  String baseUrl = jetui.getBaseURL(rc);
  String pageDec = jetui.getTheme(rc).getPageLayoutDecoration().getName();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta http-equiv="content-type" content="<%=encoding%>"/>
<title><%=jetui.getTitle(rc)%></title>
<link rel="shortcut icon" href="<%=baseUrl%>images/jetspeed.jpg" type="image/x-icon" />
<script type="text/javascript" src="<%=request.getContextPath()%>/javascript/yui/build/yui/yui-min.js"></script>
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

<body>
<div id='jetspeedZone'>
<div id="layout-<%=pageDec%>" class="layout-<%=pageDec%>" >
<div class="header">
<h1 class="logo">Jetspeed 2</h1>
<div class="menu">
&nbsp;<span style='position: absolute; left: 0px' id='jstbLeftToggle' class='jstbToggle1'></span><span id='jstbRightToggle' class='jstbToggle2' style='position: absolute; right: 0px'></span>
</div>
<%if (request.getUserPrincipal() != null) {%>
<span class="layout-statusarea">David Sean Taylor | Profile | Tasks (5) | Notifications (2) | <a href="<%=request.getContextPath()%>/login/logout">Log out</a></span>
<% } %>
<!-- <span class="layout-search"><input type='text' size='14'/></span><span class="layout-search2"><img height='18' src="<%=request.getContextPath()%>/images/search.png"/></span>  -->
</div> <!-- end header -->

<!-- main area -->
<table cellpadding="0" cellspacing="0" border="0" width="100%" id="main">
<tr>         
<td>
<div id='jstbLeft' class='jsLeftToolbar'> 
<div id="jsNavigator" class="portlet <%=pageDec%>">
    <div class="PTitle" >
      <div class="PTitleContent">Navigator</div>
    </div>
    <div class="PContentBorder">
      <div class="PContent"><span style="line-height:0.005px;">&nbsp;</span><%=navContent %></div>
    </div>
</div>
</div>
</td>
<td id='jsMainarea' class='jsMainarea'>
<div id="jsFragments" class="portal-nested-layout portal-nested-layout-TwoColumns">
<%
	ContentFragment maximized = (ContentFragment)request.getAttribute(PortalReservedParameters.MAXIMIZED_FRAGMENT_ATTRIBUTE);
	if (maximized != null)
	{
		String content = jetui.getRenderedContent(maximized, rc);
		request.setAttribute("content", content);
		request.setAttribute("pageDec", pageDec);
		request.setAttribute("fragment", maximized);
%>
<div id="column_id_0>" 
     class="portal-layout-column" 
     style="float:left; width:100%; background-color: #ffffff;">
<jsp:include page="jetui-portlet.jsp"/>
</div>	
<%	
	}
	else
	{
		int index = 0;
		for (Collection<Fragment> collections : columnLayout.getColumns())
		{
		    String columnFloat = columnLayout.getColumnFloat(index);
		    String columnWidth = columnLayout.getColumnWidth(index);
		// class="portal-layout-column portal-layout-column-${layoutType}-${columnIndex}"	        
%>
<div id="column_id_<%=index%>" 
     class="portal-layout-column" column='<%=index%>'
     style="float:<%=columnFloat%>; min-height: 100px; width:<%=columnWidth%>; background-color: #ffffff;">

<%	    
			int subindex = 0;
		    for (Fragment fragment : collections)
		    {
		        if (!(fragment.getName().equals("j2-admin::JetspeedToolbox") || fragment.getName().equals("j2-admin::JetspeedNavigator")))
		        {
		    		//String content = jetui.renderPortletWindow(fragment.getId(), fragment.getName(), rc);
		    		String content = jetui.getRenderedContent((ContentFragment)fragment, rc);
		    		request.setAttribute("content", content);
		    		request.setAttribute("pageDec", pageDec);
		    		request.setAttribute("fragment", fragment);		 
		    		request.setAttribute("coordinate", columnLayout.getCoordinate(fragment));
%>
<jsp:include page="jetui-portlet.jsp"/>
<%	    	
					subindex++;
		        }
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
<div id="jsToolbox" class="portlet <%=pageDec%>">
    <div class="PTitle" >
      <div class="PTitleContent">Toolbox</div>
    </div>
    <div class="PContentBorder">
      <div class="PContent"><span style="line-height:0.005px;">&nbsp;</span><%=tbContent %></div>
    </div>
</div>
</div> 
</td>
</tr>
</table>

</div> <!-- end layout -->
</div>
</body>
<script language="javascript">
var assetsDir = "assets/";
var buildDir = "<%=request.getContextPath()%>/javascript/yui/build/" ;
var yuiConfig = {base:"<%=request.getContextPath()%>/javascript/yui/build/", timeout: 10000, debug: true, useBrowserConsole: true}; // ({classNamePrefix: 'jet'})
</script>
<script src="<%=request.getContextPath()%>/jetui/engine/portal.js"></script>
 
</html>
