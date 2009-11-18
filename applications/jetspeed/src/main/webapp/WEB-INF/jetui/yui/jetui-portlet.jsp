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
<%@ page import="org.apache.jetspeed.portlets.layout.LayoutCoordinate" %>
<%@ page import="org.apache.jetspeed.om.page.ContentFragment" %>
<%@ page import="org.apache.jetspeed.decoration.Decoration" %>
<%@ page import="org.apache.jetspeed.decoration.DecoratorAction" %>
<%@ page import="org.apache.jetspeed.PortalReservedParameters" %>
<%
	String content = (String)request.getAttribute("content");
	String decorator = (String)request.getAttribute("decorator");
	ContentFragment fragment = (ContentFragment)request.getAttribute("fragment");
	LayoutCoordinate coordinate = (LayoutCoordinate)request.getAttribute("coordinate");
	String title = "";
	boolean showTitle = fragment.getDecoration().getTitleOption() == Decoration.TitleOption.SHOW; 	
	if (showTitle && fragment.getPortletContent() != null)
	    title = fragment.getPortletContent().getTitle();
%>
	<div class="portal-layout-cell" id="<%=fragment.getId()%>" name="<%=fragment.getName()%>" column="<%=coordinate.getX()%>" row="<%=coordinate.getY()%>">
		<div class="portlet <%=decorator%>">
		    <div class="PTitle" >
	          <div class="PTitleContent"><%=title%></div>
		  	    <div class="PActionBar">
<%
					Decoration.ActionsOption option = fragment.getDecoration().getActionsOption(); 	
					if (option != Decoration.ActionsOption.HIDE) // TODO: HOVER, DROP DOWN not yet implemented
					{
						for(DecoratorAction action : (List<DecoratorAction>)fragment.getDecoration().getActions())
					    {			        
					        String target = "target='"+ action.getTarget() + "'";
					        if (action.getTarget() == null)
					            target = "";
 %>			    
			     <a href="<%=action.getAction()%>" title="<%=action.getName()%>" class="action portlet-action" <%=target%>><img src="<%=request.getContextPath()%>/<%=action.getLink()%>" alt="<%=action.getAlt()%>" border="0" /></a>
<%                  } // for loop				
// FIXME: integrate close into standard actions, use security constraints on close action
if (request.getUserPrincipal() != null && fragment.getDecoration().getActions().size() > 0)
{
%>
				 <span style='cursor: pointer; z-index: 1000;' id='jetspeed-close-<%=fragment.getId()%>' title="close" class="portlet-action-close"><img src="<%=request.getContextPath()%>/decorations/portlet/jetspeed/images/close.gif" alt="Close" border="0" /></span>				 
<% }  } %>
			    </div>
		      </div>
		       <div class="PContentBorder">
		         <div class="PContent"><%=content%></div>
		       </div>
		    </div>
          </div>
     
