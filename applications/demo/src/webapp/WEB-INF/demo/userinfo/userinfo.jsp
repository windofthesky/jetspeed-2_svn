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
<%@ taglib uri="portlet.tld" prefix='portlet'%>
<%@ taglib uri="c.tld" prefix="c" %>
<%@ taglib uri="c-rt.tld" prefix="c-rt" %>

<portlet:defineObjects/>

<h3>User attributes</h3>

<c:choose>

  <c:when test="${empty renderRequest.userPrincipal}">
	  Login to see the user attributes available.
  </c:when>

  <c:otherwise>
    <b>User Attribute for user:</b> <c:out value='${renderRequest.userPrincipal.name}'/><br>
    <br>

	<table border="1">
      <tr>
      	<th colspan="2" align="center">Portlet Request</th>
	  </tr>
      <tr>
      	<th>User Attribute</th>
      	<th>User Attribute Value</th>
      </tr>
      
      <c-rt:forEach var="userAttr" items="<%= renderRequest.getAttribute(PortletRequest.USER_INFO)%>">    
      <tr>
      	<td><c:out value="${userAttr.key}"/></td>
      	<td><c:out value="${userAttr.value}"/></td>
      </tr>
	  </c-rt:forEach>

    </table>    

	<br>
	
	<table border="1">
      <tr>
      	<th colspan="2" align="center">Servlet Request</th>
	  </tr>
      <tr>
      	<th>User Attribute</th>
      	<th>User Attribute Value</th>
      </tr>
      
      <c-rt:forEach var="userAttr" items="<%= request.getAttribute(PortletRequest.USER_INFO)%>">    
      <tr>
      	<td><c:out value="${userAttr.key}"/></td>
      	<td><c:out value="${userAttr.value}"/></td>
      </tr>
	  </c-rt:forEach>

    </table>
	 
  </c:otherwise>

</c:choose>
<br>
For help on user attributes configuration select the help icon.
