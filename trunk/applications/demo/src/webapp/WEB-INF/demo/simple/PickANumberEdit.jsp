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
<%@ page language="java" session="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri='/WEB-INF/portlet.tld' prefix='portlet'%>
<%@ page import="javax.portlet.PortletSession"%>
<portlet:defineObjects/>
<fmt:setBundle basename="org.apache.jetspeed.demo.simple.resources.PickANumberResources" />

<portlet:actionURL var="editAction">
</portlet:actionURL>

<%
	String topRange = renderRequest.getPreferences().getValue("TopRange", "101");
%>

<div>
  <br/>
  <form action="<%=editAction%>" method="POST">  
    <fmt:message key="pickanumber.edit.label.highendofguessrange"/>
    <input type="text" name="TopRange" value="<%=topRange%>">
    <input type="submit" value='<fmt:message key="pickanumber.edit.label.save"/>'> 
  </form>  
</div>

<portlet:renderURL var="normalMe" portletMode='View'/>
<a href='<%=normalMe%>'>View</a>
