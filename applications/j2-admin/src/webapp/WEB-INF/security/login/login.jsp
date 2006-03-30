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
<%@page import="org.apache.jetspeed.request.RequestContext"%>
<%@taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/core_rt" prefix="c_rt"%>

<fmt:setBundle basename="org.apache.jetspeed.portlets.security.resources.LoginResources" />

<c_rt:set var="requestContext" value="<%=request.getAttribute(RequestContext.REQUEST_PORTALENV)%>"/>
<%
  RequestContext rc = (RequestContext)request.getAttribute(RequestContext.REQUEST_PORTALENV);
  String encoder = rc.getRequest().getParameter("encoder");
  System.out.println("encoder = " + encoder);  
  String dstLogin = "/login/proxy";
  String dstLogout = "/login/logout";
  String dstAccount = "/portal/my-account.psml";
  if (encoder != null && encoder.equals("desktop"))
  {
      dstLogin = dstLogin + "?" +  LoginConstants.DESTINATION + "=" + rc.getRequest().getContextPath() + "/desktop";
      dstLogout = dstLogout + "?" +  LoginConstants.DESTINATION + "=" + rc.getRequest().getContextPath() + "/desktop";
      dstAccount = "/desktop/my-account.psml" + "?" +  LoginConstants.DESTINATION + "=" + rc.getRequest().getContextPath() + "/desktop";
  }
%>
<c_rt:set var="destLogin" value="<%=dstLogin%>"/>
<c_rt:set var="destLogout" value="<%=dstLogout%>"/>
<c_rt:set var="destAccount" value="<%=dstAccount%>"/>

<c:choose>
  <c:when test="${pageContext.request.userPrincipal != null}">
    <fmt:message key="login.label.Welcome"><fmt:param><c:out value="${pageContext.request.userPrincipal.name}"/></fmt:param></fmt:message><br>
    <a href='<c:url context="${requestContext.request.contextPath}" value="${destLogout}"/>'><fmt:message key="login.label.Logout"/></a>
    <br>
    <a href='<c:url context="${requestContext.request.contextPath}" value="${destAccount}"/>'><fmt:message key="login.label.ChangePassword"/></a>
  </c:when>
  <c:otherwise>
    <%-- backdoor access to the portal session to get the login error count --%>
    <c_rt:set var="errorCode" value="<%=((RequestContext)request.getAttribute(RequestContext.REQUEST_PORTALENV)).getSessionAttribute(LoginConstants.ERRORCODE)%>"/>
    <c:choose>    
      <c:when test="${not empty errorCode}">
        <br>
        <i><fmt:message key="login.label.ErrorCode.${errorCode}"/></i>
        <br>
      </c:when>
      <c:otherwise>
        <c_rt:set var="retryCount" value="<%=((RequestContext)request.getAttribute(RequestContext.REQUEST_PORTALENV)).getSessionAttribute(LoginConstants.RETRYCOUNT)%>"/>
        <c:if test="${not empty retryCount}">
          <br>
          <i><fmt:message key="login.label.InvalidUsernameOrPassword"><fmt:param value="${retryCount}"/></fmt:message></i>
          <br>
        </c:if>
      </c:otherwise>
    </c:choose>   
    <form method="POST" action='<c:url context="${requestContext.request.contextPath}" value="${destLogin}"/>'>
      <table border="0">
      <tr>
        <td><fmt:message key="login.label.Username"/></td>
        <c_rt:set var="userName" value="<%=((RequestContext)request.getAttribute(RequestContext.REQUEST_PORTALENV)).getSessionAttribute(LoginConstants.USERNAME)%>"/>
        <td><input type="text" size="30" name="<%=LoginConstants.USERNAME%>" value="<c:out value="${userName}"/>"></td>
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
