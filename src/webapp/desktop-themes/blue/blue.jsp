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

<% 
    JetspeedDesktopContext desktop = (JetspeedDesktopContext)request.getAttribute(JetspeedDesktopContext.DESKTOP_ATTRIBUTE);
%>

<html>   <!-- NOTE: do not use strict doctype - see dojo svn log for FloatingPane.js -->
<head>
<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
<meta http-equiv="Content-style-type" content="text/css" />

<!-- 
  DOJO Config Script ( djConfig )
  -->
<script type="text/javascript">
    var djConfig = {isDebug: false};
    // djConfig.debugAtAllCosts = true;     
    // needed for js debuggers (both venkman and visual studio)
    djConfig.baseScriptUri = '<%= desktop.getPortalResourceUrl("/javascript/dojo/") %>' ;
    {
        var tEnds = djConfig.baseScriptUri.indexOf(";jsessionid=");
        if (tEnds > 0) djConfig.baseScriptUri = djConfig.baseScriptUri.substring(0, tEnds);
    }
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
    dojo.require("dojo.widget.Menu2");
    dojo.require("dojo.fx.html");

    dojo.hostenv.setModulePrefix('jetspeed.ui.widget', '../desktop/widget');
    dojo.hostenv.setModulePrefix('jetspeed.desktop', '../desktop/core');

    dojo.require("jetspeed.desktop.core");

    dojo.require("jetspeed.ui.widget.PortalTaskBar");
    dojo.require("jetspeed.ui.widget.PortletWindow");

    dojo.hostenv.writeIncludes();    
</script>
<script language="JavaScript" type="text/javascript">
    dojo.widget.manager.registerWidgetPackage('jetspeed.ui.widget');
</script>

<!-- <base> tag must appear after dojo load in IE6 ( see http://trac.dojotoolkit.org/ticket/557 ) -->
<base id="basetag" href="<%= desktop.getPortalResourceUrl("/") %>"> <!-- http://localhost:8080/jetspeed/ -->
<link rel="stylesheet" type="text/css" media="screen, projection" href="desktop-themes/blue/css/styles.css"/>
<script language="JavaScript" type="text/javascript">
    function init()
    {
        jetspeed.initializeDesktop();
    }
    function doRender(url,portletEntityId)
    {
        jetspeed.doRender(url,portletEntityId);
    }
    function doAction(url, portletEntityId, currentForm)
    {
        jetspeed.doAction(url,portletEntityId, currentForm);
    }
    dojo.event.connect(dojo, "loaded", "init");
</script>
<style>

html, body
{	
    width: 100%;	/* make the body expand to fill the visible window */
    height: 100%;
    /*overflow: hidden;*/  /* erase window level scrollbars */
    margin: 0 0 0 0;
}
/* body { padding: 10px 10px 100px 10px; } */

</style>

</head>

<!-- Start Jetspeed Desktop -->
<body id="jetspeedDesktop" class="layout-blue">
<div class="layout-blue">


<!-- Start Taskbar -->
<div dojoType="PortalTaskBar" id="jetspeedTaskbar" style="background-color: #666; width: 100%; bottom: 5px; height: 100px">
</div>
<!-- End Taskbar -->
</div>
</body>
<!-- End Jetspeed Desktop -->

</html>
