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
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<fmt:setBundle basename="org.apache.jetspeed.demo.userinfo.resources.UserInfoResources" />

<portlet:defineObjects/>

<h3><fmt:message key="userinfo.label.UserAttributes"/></h3>

<c:choose>

  <c:when test="${empty renderRequest.userPrincipal}">
	  <fmt:message key="userinfo.label.LoginToSeeTheUserAttributesAvailable"/>
  </c:when>

  <c:otherwise>
    <b><fmt:message key="userinfo.label.UserAttributeForUser"/></b> <c:out value='${renderRequest.userPrincipal.name}'/><br>
    <br>

	<table border="1">
      <tr>
      	<th colspan="2" align="center"><fmt:message key="userinfo.label.PortletRequest"/></th>
	  </tr>
      <tr>
      	<th><fmt:message key="userinfo.label.UserAttribute"/></th>
      	<th><fmt:message key="userinfo.label.UserAttributeValue"/></th>
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
      	<th colspan="2" align="center"><fmt:message key="userinfo.label.ServletRequest"/></th>
	  </tr>
      <tr>
      	<th><fmt:message key="userinfo.label.UserAttribute"/></th>
      	<th><fmt:message key="userinfo.label.UserAttributeValue"/></th>
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
<fmt:message key="userinfo.label.ForHelpOnUserAttributesConfigurationSelectTheHelpIcon"/>
