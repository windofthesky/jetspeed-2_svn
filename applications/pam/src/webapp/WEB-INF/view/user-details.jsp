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
<%@ page import="javax.portlet.*" %>
<%@ page import="java.util.*" %>
<%@ page import="org.apache.jetspeed.portlets.pam.beans.TabBean" %>
<%@ page import="org.apache.jetspeed.portlets.security.users.JetspeedUserBean" %>
<%@ page import="org.apache.jetspeed.om.common.preference.*" %>
<%@ page import="org.apache.jetspeed.om.common.*" %>

<%@ taglib uri='/WEB-INF/portlet.tld' prefix='portlet'%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.apache.jetspeed.portlets.security.resources.SecurityResources" />


<portlet:defineObjects/>

<c:set var="user" value="${requestScope.user}" />

user = <c:out value="${user}"/>

<%--Beginning of User check -%>
<c:if test="${user != null}">

userxxx = <c:out value="${user}"/>

<c:set var="tabs" value="${requestScope.tabs}"/>
<c:set var="selectedTab" value="${requestScope.selected_tab}"/>

<fmt:message key="user.principal.name"/> = <c:out value="${user.Principal}"/><br />

<div id="tabs">
	<c:set var="tab_items" value="${tabs}"/>
	<c:set var="currentTab" value="${selectedTab}"/>
	<c:set var="url_param_name" value="selected_tab"/>
	<%@ include file="tabs.jsp"%>
</div>

<%--Beginning of User Attributes tab data--%>
<%--TODO:  switch to c:choose --%>
<c:if test="${selectedTab.id == 'user_attributes'}">
  <div id="attributes">	
  </div>
  <h3>USER ATTRIBUTES</h3>
      
</c:if>
<%--End of User Attributes tab data--%>

<%--Beginning Security tab data--%>
<%--TODO:  switch to c:choose --%>
<c:if test="${selectedTab.id == 'user_security'}">
  <div id="Security">
  </div>
  <h3>SECURITY ATTRIBUTES</h3>
  
</c:if>
<%--End of UserAttr tab data--%>

</c:if>
<%--End of User check --%>

<br />
<br />


