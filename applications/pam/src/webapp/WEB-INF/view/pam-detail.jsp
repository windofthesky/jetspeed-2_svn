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
<%@ page language="java" import="javax.portlet.*, java.util.*, org.apache.jetspeed.portlets.pam.PortletApplicationBean, org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite" session="true" %>
<%@ taglib uri='/WEB-INF/portlet.tld' prefix='portlet'%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>


<portlet:defineObjects/>
<h2>Portlet Application Detail</h2>

<c:set var="pa" value="${requestScope.portletApplication}" />
<c:set var="name" value="${pa.name}" />
<c:set var="version" value="${pa.version}" />

app.name = <c:out value="${name}"/><br />
app.version = <c:out value="${version}"/>

<p>TODO: Details</p>

<div id="portlets">

	<c:forEach var="portletDef" items="${pa.portletDefinitions}">
		<c:set var="pdefName" value="${portletDef.name}"/>
		
		<%--We must do this since portlet taglib doesn't support expressions--%>
		<% String pdefName = (String) pageContext.getAttribute("pdefName"); %>
		<portlet:actionURL var="select_portlet_link" >
			<portlet:param name="select_portlet" value="<%= pdefName %>" />
		</portlet:actionURL>
		
	
		<a href="<c:out value="${select_portlet_link}"/>">
			<c:out value="${portletDef.name}" /><br />
		</a>
	</c:forEach>
</div>

<br />
<br />

<div id="selectedPortlet" class="">
	<span class="portlet-section-header">Selected Portlet</span>
	<c:set var="pdef" value="${requestScope.portletDefinition}"/>
	<c:out value="${pdef.name}"/>
</div>
