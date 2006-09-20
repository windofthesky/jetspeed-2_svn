<%--
Copyright 2004 The Apache Software Foundation

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

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

    //Enumeration iter = request.getAttributeNames();
    //String debugout = "";
    //while ( iter.hasMoreElements() )
    //{
    //    debugout += iter.nextElement().toString() + ", " ;
    //}
    //System.out.println( "request.getAttributeNames() : " + debugout );

    //Fragment rootFragment = (Fragment)requestContext.getPage().getRootContentFragment();

    //String testThemeResourceUrl = desktop.getDesktopThemeResourceUrl("images/logo.gif");
    ///System.out.println( "t h e m e : " + testThemeResourceUrl );
%>
<html> <!-- .jsp --> <!-- NOTE: do not use strict doctype - see dojo svn log for FloatingPane.js -->
<head>
<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
<meta http-equiv="Content-style-type" content="text/css" />
<script src="http://maps.google.com/maps?file=api&amp;v=2&amp;key=ABQIAAAAisHr-hr7f_yfo_m3teTC5RQXGaCFRGWXJQavRKQcb1Ew_fwkKRQ26QnpXVIkxSMwwTECWDV23ZDaLQ"
        type="text/javascript"></script> 
<!-- 
  DOJO Config Script ( djConfig )
  -->
<script type="text/javascript">
    var djConfig = {isDebug: true, debugAtAllCosts: true};
    //var djConfig = {isDebug: false, debugAtAllCosts: false};
    // needed for js debuggers (both venkman and visual studio)
    function de_jsessionid_url( url )
    {   // presence of ;jsessionid in dojo baseScriptUri is bad news
        var tEnds = url.indexOf(";jsessionid=");
        if (tEnds > 0) url = url.substring(0, tEnds);
        return url;
    }
    djConfig.baseScriptUri = de_jsessionid_url( '<%= desktop.getPortalResourceUrl("/javascript/dojo/") %>' );
    djConfig.desktopThemeRootUrl = de_jsessionid_url( '<%= desktop.getDesktopThemeRootUrl() %>' );
</script>
<!-- 
  DOJO Script
  -->
<script type="text/javascript" src="<%= desktop.getPortalResourceUrl("/javascript/dojo/dojo.js") %>"></script>
<script language="JavaScript" type="text/javascript">
    dojo.require("dojo.lang.*");
    dojo.require("dojo.dnd.HtmlDragMove");
    dojo.require("dojo.dnd.HtmlDragSource");
    dojo.require("dojo.event.*");    
    dojo.require("dojo.io");
    dojo.require("dojo.collections.ArrayList");
    dojo.require("dojo.collections.Set");
    dojo.require("dojo.widget.Manager");
    dojo.require("dojo.widget.TaskBar");
    dojo.require("dojo.widget.FloatingPane");
    dojo.require("dojo.widget.TabContainer");
    dojo.require("dojo.widget.AccordionPane");
    dojo.require("dojo.widget.Menu2");
    dojo.require('dojo.widget.Checkbox');
    dojo.require('dojo.widget.Dialog');
    dojo.require('dojo.widget.Button');
    dojo.require("dojo.lfx.html");

    dojo.require('dojo.widget.LayoutContainer');
    dojo.require('dojo.widget.ContentPane');
    dojo.require('dojo.widget.LinkPane');
    dojo.require('dojo.widget.SplitContainer');
    dojo.require('dojo.widget.TabContainer');
    dojo.require('dojo.widget.Tree');

    dojo.hostenv.setModulePrefix('jetspeed.ui.widget', '../desktop/widget');
    dojo.hostenv.setModulePrefix('jetspeed.desktop', '../desktop/core');

    dojo.require("jetspeed.desktop.core");

    dojo.require("jetspeed.ui.widget.PortalTaskBar");
    dojo.require("jetspeed.ui.widget.PortletWindow");
    dojo.require("jetspeed.ui.widget.PortalTabContainer");
    dojo.require("jetspeed.ui.widget.PortalAccordionContainer");
    dojo.require("jetspeed.ui.widget.PortletDefContainer");
    dojo.require("jetspeed.ui.widget.EditorTable");
</script>
<script language="JavaScript" type="text/javascript">
    dojo.hostenv.writeIncludes();
</script>
<script language="JavaScript" type="text/javascript">
    dojo.widget.manager.registerWidgetPackage('jetspeed.ui.widget');
</script>
<base id="basetag" href="<%= desktop.getPortalResourceUrl("/") %>">  <!-- http://localhost:8080/jetspeed/ --> 
     <!-- <base> tag must appear after dojo load in IE6 ( see http://trac.dojotoolkit.org/ticket/557 ) -->
<link rel="stylesheet" type="text/css" media="screen, projection" href='<%= desktop.getDesktopThemeResourceUrl("css/styles.css") %>'/>

<script language="JavaScript" type="text/javascript">
    function jsDesktopInit()
    {
        jetspeed.initializeDesktop( '<%= desktop.getDesktopTheme() %>', de_jsessionid_url( '<%= desktop.getDesktopThemeRootUrl() %>' ) );
    }
    function doRender( bindArgs, portletEntityId )
    {
        jetspeed.doRender( bindArgs, portletEntityId );
    }
    function doAction( bindArgs, portletEntityId )
    {
        jetspeed.doAction( bindArgs, portletEntityId );
    }
    dojo.addOnLoad( window.jsDesktopInit );
</script>


<script language="JavaScript" type="text/javascript">
    function notifyRetrieveAllMenusFinished()
    {
        dojo.debug( "window.notifyRetrieveAllMenusFinished" );
    }
    dojo.event.connect( jetspeed, "notifyRetrieveAllMenusFinished", "notifyRetrieveAllMenusFinished" );
</script>
<style>

html, body, #jetspeedDesktop
{	
    width: 100%;	/* make the body expand to fill the visible window */
    height: 100%;
    margin: 0 0 0 0;
}

</style>

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
<div widgetId="jetspeed-menu-pages" dojoType="PortalTabContainer" style="width: 100%; height: 30px; margin-top: 2px; margin-left: -1px"></div>
<table cellpadding="0" cellspacing="0" border="0" width="100%" id="main" style="position: static">  <!-- id="main"  has top: 170px and position: absolute -->
<tr>
<td valign="top" id="leftcol" >
<div widgetId="jetspeed-menu-navigations" dojoType="PortalAccordionContainer" style=""></div>
</td>
<td style="vertical-align: top">   <!-- hack of "vertical-align: top" needed by IE once the leftnav was added  -->
<!-- Start Jetspeed Desktop -->
<div class="<%= desktopThemeStyleClass %>" id="jetspeedDesktop"></div>
<!-- End Jetspeed Desktop -->
</td>
</tr>
</table>
<!-- Start Taskbar -->
<!-- the presence of the PortalTaskBar here is hiding a style load problem (at least in ff - sure to be worse in IE) -->
<!-- for now we need to keep this here until this can be fixed -->
<!-- (when we don't want a taskbar - set windowState to "minimized", otherwise omit windowState) -->
<div dojoType="PortalTaskBar" id="jetspeedTaskbar" style="background-color: #666; width: 98%; bottom: 5px; height: 110px" windowState="minimized" resizable="false"></div>
<!-- End Taskbar -->
</div>
<!-- End Jetspeed Page -->
</body>
</html>
