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
<%@page import="org.apache.jetspeed.portlets.security.ChangePasswordPortlet"%>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<%@taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt"%>

<portlet:defineObjects/>
<fmt:setBundle basename="org.apache.jetspeed.portlets.security.resources.ChgPwdResources" />

<c:choose>
  <c:when test="${pageContext.request.userPrincipal != null}">

    <c:set var="errorMessagesKey"><%=ChangePasswordPortlet.ERROR_MESSAGES%></c:set>
    <c:set var="errorMessages" value="${requestScope[errorMessagesKey]}"/>
    <c:if test="${errorMessages != null}">
    <ul>
      <c:forEach items="${errorMessages}" var="error">
        <li style="color:red"><c:out value="${error}"/></li>
      </c:forEach>
    </ul>
    </c:if>

    <c:set var="passwordChangedKey"><%=ChangePasswordPortlet.PASSWORD_CHANGED%></c:set>
    <c:set var="p" value="${requestScope[passwordChangedKey]}"/>
    <c:if test="${requestScope[passwordChangedKey] != null}">
      <br>
      <i><fmt:message key="chgpwd.message.passwordChanged"/></i>
      <br><br>
    </c:if>

    <form method="POST" action='<portlet:actionURL/>'>
      <table border="0">
      <tr>
        <td><fmt:message key="chgpwd.label.currentPassword"/></td>
        <td><input type="text" size="30" name="<%=ChangePasswordPortlet.CURRENT_PASSWORD%>"></td>
      </tr>
      <tr>
        <td><fmt:message key="chgpwd.label.newPassword"/></td>
        <td><input type="text" size="30" name="<%=ChangePasswordPortlet.NEW_PASSWORD%>"></td>
      </tr>
      <tr>
        <td><fmt:message key="chgpwd.label.newPasswordAgain"/></td>
        <td><input type="text" size="30" name="<%=ChangePasswordPortlet.NEW_PASSWORD_AGAIN%>"></td>
      </tr>
      <tr>
        <td colspan="2"><input type="submit" value="<fmt:message key="chgpwd.label.save"/>"></td>
      </tr>
      </table>
    </form>
  </c:when>
  <c:otherwise>
    <fmt:message key="chgpwd.error.notLoggedOn"/><br>
  </c:otherwise>
</c:choose>