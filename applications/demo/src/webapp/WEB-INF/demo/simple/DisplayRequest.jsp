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
<%@ page language="java"
         session="false"
%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/request-1.0" prefix="req" %>
<fmt:setBundle basename="org.apache.jetspeed.demo.simple.resources.DisplayRequestResources" />

<!--
  Copyright (c) 2001 The Apache Software Foundation.  All rights 
  reserved.
-->

<!--
 A JSP portlet example that displays Jetspeed TagLib and servlet request
 data. 

 @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
-->
 <div>
   <center><h1><fmt:message key="displayrequest.label.HTTPRequestHeader"/></h1></center>
    <table>
      <tr>
        <th><fmt:message key="displayrequest.label.Name"/></th>
        <th><fmt:message key="displayrequest.label.ReturnedValue"/></th>
      </tr>
      <tr>
        <td><fmt:message key="displayrequest.label.AuthType"/></td>
        <td><c:out value="${pageContext.request.authType}"/></td>
      </tr>
      <tr>
        <td><fmt:message key="displayrequest.label.CharacterEncoding"/></td>
        <td><c:out value="${pageContext.request.characterEncoding}"/></td>
      </tr>
      <tr>
        <td><fmt:message key="displayrequest.label.ContentLength"/></td>
        <td><c:out value="${pageContext.request.contentLength}"/></td>
      </tr>
      <tr>
        <td><fmt:message key="displayrequest.label.ContentType"/></td>
        <td><c:out value="${pageContext.request.contentType}"/></td>
      </tr>
      <tr>
        <td><fmt:message key="displayrequest.label.ContextPath"/></td>
        <td><c:out value="${pageContext.request.contextPath}"/></td>
      </tr>
      <tr>
        <td><fmt:message key="displayrequest.label.Method"/></td>
        <td><c:out value="${pageContext.request.method}"/></td>
      </tr>
      <tr>
        <td><fmt:message key="displayrequest.label.PathInfo"/></td>
        <td><c:out value="${pageContext.request.pathInfo}"/></td>
      </tr>
      <tr>
        <td><fmt:message key="displayrequest.label.PathTranslated"/></td>
        <td><c:out value="${pageContext.request.pathTranslated}"/></td>
      </tr>
      <tr>
        <td><fmt:message key="displayrequest.label.Protocol"/></td>
        <td><c:out value="${pageContext.request.protocol}"/></td>
      </tr>
      <tr>
        <td><fmt:message key="displayrequest.label.QueryString"/></td>
        <td><c:out value="${pageContext.request.queryString}"/></td>
      </tr>
      <tr>
        <td><fmt:message key="displayrequest.label.RemoteAddress"/></td>
        <td><c:out value="${pageContext.request.remoteAddr}"/></td>
      </tr>
      <tr>
        <td><fmt:message key="displayrequest.label.RemoteHost"/></td>
        <td><c:out value="${pageContext.request.remoteHost}"/></td>
      </tr>
      <tr>
        <td><fmt:message key="displayrequest.label.RemoteUser"/></td>
        <td><c:out value="${pageContext.request.remoteUser}"/></td>
      </tr>
      <tr>
        <td><fmt:message key="displayrequest.label.RequestedSessionID"/></td>
        <td><c:out value="${pageContext.request.requestedSessionId}"/></td>
      </tr>
      <tr>
        <td><fmt:message key="displayrequest.label.RequestedSessionIDFromcCookie"/></td>
        <td><c:out value="${pageContext.request.requestedSessionIdFromCookie}"/></td>
      </tr>
      <tr>
        <td><fmt:message key="displayrequest.label.RequestedSessionIDFromURL"/></td>
        <td><c:out value="${pageContext.request.requestedSessionIdFromURL}"/></td>
      </tr>
      <tr>
        <td><fmt:message key="displayrequest.label.RequestURI"/></td>
        <td><c:out value="${pageContext.request.requestURI}"/></td>
      </tr>
      <tr>
        <td><fmt:message key="displayrequest.label.RequestURL"/></td>
        <td><c:out value="${pageContext.request.requestURL}"/></td>
      </tr>
      <tr>
        <td><fmt:message key="displayrequest.label.Scheme"/></td>
        <td><c:out value="${pageContext.request.scheme}"/></td>
      </tr>
      <tr>
        <td><fmt:message key="displayrequest.label.Secure"/></td>
        <td><c:out value="${pageContext.request.secure}"/></td>
      </tr>
      <tr>
        <td><fmt:message key="displayrequest.label.ServerName"/></td>
        <td><c:out value="${pageContext.request.serverName}"/></td>
      </tr>
      <tr>
        <td><fmt:message key="displayrequest.label.ServerPort"/></td>
        <td><c:out value="${pageContext.request.serverPort}"/></td>
      </tr>
      <tr>
        <td><fmt:message key="displayrequest.label.ServletPath"/></td>
        <td><c:out value="${pageContext.request.servletPath}"/></td>
      </tr>
    </table>

    <center><h2><fmt:message key="displayrequest.label.HTTPHeadersViaJSTL"/></h2></center>
    <table>
      <tr>
        <th><fmt:message key="displayrequest.label.Name"/></th>
        <th><fmt:message key="displayrequest.label.Value"/></th>
      </tr>
      <c:forEach items="${headerValues}" var="headerValue">
        <tr>
          <td><c:out value="${headerValue.key}" /></td>
          <td><c:forEach items="${headerValue.value}" var="value">
          <c:out value="${value}"/></c:forEach></td>
        </tr>
      </c:forEach>
    </table>

    <center><h2><fmt:message key="displayrequest.label.Attributes"/></h2></center>
    <table>
      <tr>
        <th><fmt:message key="displayrequest.label.Name"/></th>
      </tr>
      <req:attributes id="loop">
        <tr>
          <td><jsp:getProperty name="loop" property="name"/></td>
       </tr>
      </req:attributes>
    </table>

    <center><h2><fmt:message key="displayrequest.label.Parameters"/></h2></center>
    <table>
      <tr>
        <th><fmt:message key="displayrequest.label.Name"/></th>
        <th><fmt:message key="displayrequest.label.Value"/></th>
      </tr>
      <req:parameters id="loop">
        <tr>
          <td><jsp:getProperty name="loop" property="name"/></td>
          <td><jsp:getProperty name="loop" property="value"/></td>
        </tr>
      </req:parameters>
    </table>
    <center><h2><fmt:message key="displayrequest.label.Cookies"/></h2></center>
    <table>
      <tr>
        <th><fmt:message key="displayrequest.label.Name"/></th>
        <th><fmt:message key="displayrequest.label.Value"/></th>
      </tr>
      <req:cookies id="loop">
        <tr>
          <td><jsp:getProperty name="loop" property="name"/></td>
          <td><jsp:getProperty name="loop" property="value"/></td>
        </tr>
      </req:cookies>
     </table>
  </div>

