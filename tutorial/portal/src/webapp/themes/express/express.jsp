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
<%@ page language="java" import="org.apache.jetspeed.desktop.JetspeedDesktopContext" session="true" %>
<%@ page import="java.util.Enumeration"%>
<%@ page import="org.apache.jetspeed.request.RequestContext"%>
<%@ page import="org.apache.jetspeed.Jetspeed" %>
<%@ page import="org.apache.jetspeed.PortalReservedParameters" %>
<%@ page import="org.apache.jetspeed.om.page.Fragment" %>
<%@ page import="org.apache.jetspeed.decoration.Theme" %>
<%@ page import="org.apache.jetspeed.decoration.Decoration" %>
<% 
    JetspeedDesktopContext desktop = (JetspeedDesktopContext)request.getAttribute(JetspeedDesktopContext.DESKTOP_ATTRIBUTE);
    RequestContext requestContext = (RequestContext)request.getAttribute(RequestContext.REQUEST_PORTALENV);

    String desktopThemeStyleClass = "layout-" + desktop.getDesktopTheme();
%>
<html> <!-- .jsp --> <!-- NOTE: do not use strict doctype - see dojo svn log for FloatingPane.js -->
<head>
<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
<meta http-equiv="Content-style-type" content="text/css" />

<%= desktop.getHeaderResource().getNamedContentForPrefix( "header.dojo" )%>

<%= desktop.getHeaderResource().getContent()%>

<script language="JavaScript" type="text/javascript">
    function notifyRetrieveAllMenusFinished()
    {
        dojo.debug( "window.notifyRetrieveAllMenusFinished" );
    }
    dojo.event.connect( jetspeed, "notifyRetrieveAllMenusFinished", "notifyRetrieveAllMenusFinished" );
</script>
</head>

<body class="<%= desktopThemeStyleClass %>">
<!-- Start Jetspeed Page -->
<div class="<%= desktopThemeStyleClass %>" id="jetspeedPage">
<div id="banner" style="position: static">    <!-- BOZO: set to absolute in stylesheet - don't know why - no apparent reason -->
  <table>
    <tr>
      <td>
        <div class="logo">
        <img src='<%= desktop.getDesktopThemeResourceUrl("images/logo.gif") %>' alt="Logo" border="0"/>
        </div>
      </td>
      <td>
        <div align="right" id="login">
          &nbsp;
        </div>
      </td>
    </tr>
  </table>
</div>
<div widgetId="jetspeed-menu-pages" dojoType="jetspeed:PortalTabContainer" style="width: 100%; margin-top: 2px; margin-left: -1px"></div>
<div widgetId="jetspeed-menu-breadcrumbs" dojoType="jetspeed:PortalBreadcrumbContainer" style="width: 100%; margin-top: 2px; margin-left: -1px"></div>
<table cellpadding="0" cellspacing="0" border="0" width="100%" id="main">
<tr>
<td id="leftcol">
<div widgetId="jetspeed-menu-navigations" dojoType="jetspeed:PortalAccordionContainer" style=""></div>
</td>
<td id="jetspeedDesktopCell">
<!-- Start Jetspeed Desktop -->
<div class="<%= desktopThemeStyleClass %>" id="jetspeedDesktop"></div>
<!-- End Jetspeed Desktop -->
</td>
</tr>
</table>
<!-- Start Taskbar -->
<!-- (when we don't want a taskbar - set windowState to "minimized", otherwise omit windowState) -->
<!-- <div dojoType="jetspeed:PortalTaskBar" id="jetspeedTaskbar" style="background-color: #666; width: 98%; bottom: 5px; height: 110px" windowState="minimized" resizable="false"></div> -->
<!-- End Taskbar -->
</div>
<!-- End Jetspeed Page -->
</body>
</html>
