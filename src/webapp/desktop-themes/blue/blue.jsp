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
<script type="text/javascript">

    var djConfig = {isDebug: true};
    djConfig.debugAtAllCosts = true;     <!-- needed for js debuggers (both venkman and visual studio) -->
    djConfig.baseScriptUri = '<%= desktop.getPortalResourceUrl("/javascript/dojo/") %>' ;
    {
        var tEnds = djConfig.baseScriptUri.indexOf(";jsessionid=");
        if (tEnds > 0) djConfig.baseScriptUri = djConfig.baseScriptUri.substring(0, tEnds);
    }

	function doRender(url,portletEntityId)
	{
        jetspeed.doRender(url,portletEntityId);
	}
    function doAction(url, portletEntityId, currentForm)
    {
        jetspeed.doAction(url,portletEntityId, currentForm);
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
    dojo.require("dojo.widget.Manager");
    dojo.require("dojo.widget.TaskBar");
    dojo.require("dojo.widget.FloatingPane");
    dojo.require("dojo.fx.*");

    dojo.hostenv.setModulePrefix('jetspeed.ui.widget', '../desktop/widget');

    dojo.require("jetspeed.ui.widget.PortalTaskBar");

    dojo.hostenv.writeIncludes();    
</script>
<script language="JavaScript" type="text/javascript">
    dojo.widget.manager.registerWidgetPackage('jetspeed.ui.widget');
</script>
<!-- 
  Jetspeed Script
  -->
<script type="text/javascript" src="<%= desktop.getPortalResourceUrl("/javascript/jetspeed/jetspeed-ajax-api.js") %>"></script>

<script language="JavaScript" type="text/javascript">
    function init()
    {
        jetspeed.initializeDesktop();
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
    font-family: Arial, Helvetica, sans-serif;
}
body { padding: 10px 10px 100px 10px; }

<!-- below styles are for jetspeed.ui.PortletDivWindow; jetspeed.ui.PortletDivWindow is for testing purposes only at this point -->
.portletBody{
	border: 3px solid #EEE;
	background: #FFF;
	margin: 5px;
}

.portletFrame{
	border : 1px solid #79A7E2;
	overflow: hidden;
}

.portletHeader{
	cursor: move;
	background: #EFF5FF;
	height: 1.8em;
	overflow: hidden;
}
.portletHeader .showHide{
	width: 16px;
	height: 16px;
	cursor: pointer;
	float: left;
	padding: 2px 0 0 2px;
}
.portletHeader .title{
	font-weight: bold;
	padding-top: 2px;
	line-height: 1.4em;
	color: #00368F;
}
.portletHeader .title a{
	color: #00368F;
}
.portletHeader .title a:hover{
	color: #F60;
}
.portletHeader .close{
	float: right;
	padding: 2px 2px 2px 0;
	cursor: pointer;
}

</style>

</head>

<!-- Start Jetspeed Desktop -->
<body id="jetspeedDesktop">

<!--
<div dojoType="FloatingPane"
	title="Layout window w/shadow"
	constrainToContainer="true"
	hasShadow="true"
	resizable="true"
	displayMinimizeAction="true"
	displayMaximizeAction="true"
	contentWrapper="layout"
	style="width: 300px; height: 200px; top: 600px; left: 400px;"
>
	<div dojoType="ContentPane" layoutAlign="top" style="border: solid white;">TOP</div>
	<div dojoType="ContentPane" layoutAlign="bottom" style="border: solid white;">BOTTOM</div>
	<div dojoType="ContentPane" layoutAlign="left" style="border: solid white;">LEFT</div>
	<div dojoType="ContentPane" layoutAlign="right" style="border: solid white;">RIGHT</div>
	<div dojoType="ContentPane" layoutAlign="client" style="border: solid white;">CENTER</div>
</div>
-->

<!-- Start Taskbar -->
<!-- <div dojoType="TaskBar" id="jetspeedTaskbar" style="background-color: #666; width: 100%; bottom: 5px; height: 100px"> -->
    <!-- <button onclick="jetspeed.testLoadPageCreateDivPortlets()">Portal (DIV)</button>
    <button onclick="jetspeed.testLoadPageCreateWidgetPortlets()">Portal (WIDGET)</button> -->
<!-- </div> -->
<!-- End Taskbar -->

</body>
<!-- End Jetspeed Desktop -->

</html>
