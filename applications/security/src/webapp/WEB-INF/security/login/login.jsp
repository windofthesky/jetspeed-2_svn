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
<%@page import="org.apache.jetspeed.login.LoginConstants"%>
<%@taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://java.sun.com/jstl/core" prefix="c"%>

<fmt:setBundle basename="org.apache.jetspeed.portlets.security.resources.LoginResources" />

<c:choose>
  <c:when test="${pageContext.request.userPrincipal != null}">
    <fmt:message key="login.label.Welcome"><fmt:param><c:out value="${pageContext.request.userPrincipal.name}"/></fmt:param></fmt:message><br>
    <a href='<c:url value="/login/logout"/>'><fmt:message key="login.label.Logout"/></a>
    <br>
    <a href='<c:url value="/portal/Administrative/change-password.psml"/>'><fmt:message key="login.label.ChangePassword"/></a>
  </c:when>
  <c:otherwise>
    <c:set var="retryCountKey"><%=LoginConstants.RETRYCOUNT%></c:set>
    <c:set var="retryCount" value="${sessionScope[retryCountKey]}"/>
    <c:if test="${retryCount != null}">
      <br>
      <i><fmt:message key="login.label.InvalidUsernameOrPassword"><fmt:param value="${retryCount}"/></fmt:message></i>
      <br>
    </c:if>
    <form method="POST" action='<c:url value="/login/proxy"/>'>
      <table border="0">
      <tr>
        <td><fmt:message key="login.label.Username"/></td>
        <td><input type="text" size="30" name="<%=LoginConstants.USERNAME%>"></td>
      </tr>
      <tr>
        <td><fmt:message key="login.label.Password"/></td>
        <td><input type="password" size="30" name="<%=LoginConstants.PASSWORD%>"></td>
      </tr>
      <tr>
        <td colspan="2"><input type="submit" value="<fmt:message key="login.label.Login"/>"></td>
      </tr>
      </table>
    </form>
  </c:otherwise>
</c:choose>
