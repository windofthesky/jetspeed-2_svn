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
<%@page import="javax.portlet.PortletRequest" %>
<%@ taglib uri='/WEB-INF/portlet.tld' prefix='portlet'%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<fmt:setBundle basename="org.apache.jetspeed.demo.security.resources.RoleResources" />

<portlet:defineObjects/>

<h3><fmt:message key="roles.label.UserRoles"/></h3>

<c:choose>

  <c:when test="${empty renderRequest.userPrincipal}">
	  <fmt:message key="roles.label.LoginToSeeTheRoles"/>
  </c:when>

  <c:otherwise>
    <fmt:message key="roles.label.UserColon"/> <c:out value='${renderRequest.userPrincipal.name}'/><br>
    <br>
    <fmt:message key="roles.label.isUserInRole"/><br>
    <table border="1">
      <tr>
      	<th><fmt:message key="roles.label.RoleRefName"/></th>
      	<th><fmt:message key="roles.label.RoleName"/></th>
      	<th><fmt:message key="roles.label.PortletRequest"/></th>
      	<th><fmt:message key="roles.label.ServletRequest"/></th>
      </tr>
      <tr>
      	<td><fmt:message key="roles.label.Administrator"/></td>
      	<td><fmt:message key="roles.label.admin"/></td>
      	<td><%= renderRequest.isUserInRole("Administrator") %></td>
      	<td><%= request.isUserInRole("admin") %></td>
      </tr>
      <tr>
      	<td><fmt:message key="roles.label.Manager"/></td>
      	<td><fmt:message key="roles.label.manager"/></td>
      	<td><%= renderRequest.isUserInRole("Manager") %></td>
      	<td><%= request.isUserInRole("manager") %></td>
      </tr>
      <tr>
      	<td><fmt:message key="roles.label.User"/></td>
      	<td><fmt:message key="roles.label.user"/></td>
      	<td><%= renderRequest.isUserInRole("User") %></td>
      	<td><%= request.isUserInRole("user") %></td>
    	</tr>
    </table>
  </c:otherwise>

</c:choose>
<br>
<fmt:message key="roles.label.ForHelpOnRoleConfig"/>
