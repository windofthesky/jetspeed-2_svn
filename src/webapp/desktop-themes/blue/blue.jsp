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
<%@ page import="org.apache.jetspeed.request.RequestContext"%>
<%@ page import="org.apache.jetspeed.Jetspeed" %>
<%@ page import="org.apache.jetspeed.headerresource.HeaderResourceFactory" %>
<% 
    JetspeedDesktopContext desktop = (JetspeedDesktopContext)request.getAttribute(JetspeedDesktopContext.DESKTOP_ATTRIBUTE);
    //RequestContext requestContext = (RequestContext)request.getAttribute(RequestContext.REQUEST_PORTALENV);
    //HeaderResourceFactory resourceHeaderFactory = (HeaderResourceFactory)Jetspeed.getComponentManager().getComponent("org.apache.jetspeed.headerresource.HeaderResourceFactory");
    //String resourceHeader = resourceHeaderFactory.getHeaderResouce(requestContext).toString();
%>

<html>   <!-- NOTE: do not use strict doctype - see dojo svn log for FloatingPane.js -->
<head>
<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
<meta http-equiv="Content-style-type" content="text/css" />

<!-- 
  DOJO Config Script ( djConfig )
  -->
<script type="text/javascript">
    var djConfig = {isDebug: true, debugAtAllCosts: true};
    //var djConfig = {isDebug: false, debugAtAllCosts: false};
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
    //dojo.require("dojo.layout");
    dojo.require("dojo.widget.Manager");
    dojo.require("dojo.widget.TaskBar");
    dojo.require("dojo.widget.FloatingPane");
    dojo.require("dojo.widget.TabContainer");
    dojo.require("dojo.widget.Menu2");
    dojo.require("dojo.fx.html");

    dojo.hostenv.setModulePrefix('jetspeed.ui.widget', '../desktop/widget');
    dojo.hostenv.setModulePrefix('jetspeed.desktop', '../desktop/core');

    dojo.require("jetspeed.desktop.core");

    dojo.require("jetspeed.ui.widget.PortalTaskBar");
    dojo.require("jetspeed.ui.widget.PortletWindow");
    dojo.require("jetspeed.ui.widget.PortalTabContainer");
</script>
<script language="JavaScript" type="text/javascript">
    dojo.hostenv.writeIncludes();
</script>
<script language="JavaScript" type="text/javascript">
    dojo.widget.manager.registerWidgetPackage('jetspeed.ui.widget');
</script>
<!-- <base> tag must appear after dojo load in IE6 ( see http://trac.dojotoolkit.org/ticket/557 ) -->
<base id="basetag" href="<%= desktop.getPortalResourceUrl("/") %>">  <!-- http://localhost:8080/jetspeed/ --> 
<link rel="stylesheet" type="text/css" media="screen, projection" href="desktop-themes/blue/css/styles.css"/>
<script language="JavaScript" type="text/javascript">
    function init()
    {
        jetspeed.initializeDesktop();
    }
    function doRender( url, portletEntityId )
    {
        jetspeed.doRender( url, portletEntityId );
    }
    function doAction( url, portletEntityId, currentForm )
    {
        jetspeed.doAction( url,portletEntityId, currentForm );
    }
    dojo.event.connect( dojo, "loaded", "init" );
</script>
<script language="JavaScript" type="text/javascript">
/*
javascript: var tab = document.createElement( "div" ); tab.setAttribute( "label", "Blee" ); dojo.widget.byId( 'mainTabContainer' ).addChild( { domNode: tab } ); 
javascript: var tab = document.createElement( "div" ); var tabText = document.createTextNode("Blee"); tab.appendChild( tabText ); dojo.widget.byId( 'mainTabContainer' ).addChild( { domNode: tab } );
javascript: var tab = document.createElement( "div" ); var tabText = document.createTextNode("Blee"); tab.appendChild( tabText ); dojo.widget.byId( 'mainTabContainer' ).addChild( { domNode: tab, label: "Blee" } );
javascript: dojo.widget.byId( 'mainTabContainer' ).addChild( { domNode: tab, label: "Blee" } );
*/
    function notifyRetrieveAllMenusFinished()
    {
        dojo.debug( "window.notifyRetrieveAllMenusFinished" );
        jetspeed.pageNavigateSuppress = true;
        var menuObj = jetspeed.page.getMenu( "pages" );
        if ( ! menuObj ) return;
        var menuName = menuObj.getName();
        if ( menuName == "pages" )
        {
            var portalTabWidget = dojo.widget.byId( "mainTabContainer" );
            if ( ! portalTabWidget )
                dojo.raise( "window.notifyRetrieveMenuFinished could not find widget for mainTabContainer" );
            portalTabWidget.createTabsFromMenu( menuObj );
        }
        jetspeed.pageNavigateSuppress = false;
    }
    dojo.event.connect( jetspeed, "notifyRetrieveAllMenusFinished", "notifyRetrieveAllMenusFinished" );  // jetspeed.notifyRetrieveMenuFinished
</script>
<style>

html, body, .jetspeedDesktop
{	
    width: 100%;	/* make the body expand to fill the visible window */
    height: 100%;
    margin: 0 0 0 0;
}

</style>

</head>

<!-- Start Jetspeed Desktop -->
<body class="layout-blue" id="jetspeedPage">
<div widgetId="mainTabContainer" dojoType="PortalTabContainer" style="width: 100%; height: 25px;"></div>
<div class="layout-blue" id="jetspeedDesktop">
<!-- Start Taskbar -->
<!-- the presence of the PortalTaskBar here is hiding a style load problem (at least in ff - sure to be worse in IE) -->
<!-- for now we need to keep this here until this can be fixed -->
<!-- (when we don't want a taskbar - set windowState to "minimized", otherwise omit windowState) -->
<div dojoType="PortalTaskBar" id="jetspeedTaskbar" style="background-color: #666; width: 98%; bottom: 5px; height: 110px" windowState="minimized" resizable="false">
</div>
<!-- End Taskbar -->
</div>
</body>
<!-- End Jetspeed Desktop -->
</html>
