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
<%@ taglib uri='/WEB-INF/portlet.tld' prefix='portlet'%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<portlet:defineObjects/>

<h3>User roles</h3>

<c:choose>

  <c:when test="${empty renderRequest.userPrincipal}">
	  Login to see the roles you've been assigned
  </c:when>

  <c:otherwise>
    User: <c:out value='${renderRequest.userPrincipal.name}'/><br>
    <br>
    isUserInRole:<br>
    <table border="1">
      <tr>
      	<th>Role Ref Name</th>
      	<th>Role Name</th>
      	<th>PortletRequest</th>
      	<th>ServletRequest</th>
      </tr>
      <tr>
      	<td>Administrator</td>
      	<td>admin</td>
      	<td><%= renderRequest.isUserInRole("Administrator") %></td>
      	<td><%= request.isUserInRole("Administrator") %></td>
      </tr>
      <tr>
      	<td>Manager</td>
      	<td>manager</td>
      	<td><%= renderRequest.isUserInRole("Manager") %></td>
      	<td><%= request.isUserInRole("Manager") %></td>
      </tr>
      <tr>
      	<td>User</td>
      	<td>user</td>
      	<td><%= renderRequest.isUserInRole("User") %></td>
      	<td><%= request.isUserInRole("User") %></td>
    	</tr>
    </table>
  </c:otherwise>

</c:choose>
<br>
For help on role configuration select the help icon.
