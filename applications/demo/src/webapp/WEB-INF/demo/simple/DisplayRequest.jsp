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
<%@ taglib uri="http://jakarta.apache.org/taglibs/request-1.0" prefix="req" %>

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
   <center><h1>HTTP Request Header</h1></center>
    <table>
      <tr>
        <th>Name</th>
        <th>Returned Value</th>
      </tr>
      <tr>
        <td>Auth Type</td>
        <td><c:out value="${pageContext.request.authType}"/></td>
      </tr>
      <tr>
        <td>Character Encoding</td>
        <td><c:out value="${pageContext.request.characterEncoding}"/></td>
      </tr>
      <tr>
        <td>Content Length</td>
        <td><c:out value="${pageContext.request.contentLength}"/></td>
      </tr>
      <tr>
        <td>Content Type</td>
        <td><c:out value="${pageContext.request.contentType}"/></td>
      </tr>
      <tr>
        <td>Context Path</td>
        <td><c:out value="${pageContext.request.contextPath}"/></td>
      </tr>
      <tr>
        <td>Method</td>
        <td><c:out value="${pageContext.request.method}"/></td>
      </tr>
      <tr>
        <td>Path Info</td>
        <td><c:out value="${pageContext.request.pathInfo}"/></td>
      </tr>
      <tr>
        <td>Path Translated</td>
        <td><c:out value="${pageContext.request.pathTranslated}"/></td>
      </tr>
      <tr>
        <td>Protocol</td>
        <td><c:out value="${pageContext.request.protocol}"/></td>
      </tr>
      <tr>
        <td>Query String</td>
        <td><c:out value="${pageContext.request.queryString}"/></td>
      </tr>
      <tr>
        <td>Remote Address</td>
        <td><c:out value="${pageContext.request.remoteAddr}"/></td>
      </tr>
      <tr>
        <td>Remote Host</td>
        <td><c:out value="${pageContext.request.remoteHost}"/></td>
      </tr>
      <tr>
        <td>Remote User</td>
        <td><c:out value="${pageContext.request.remoteUser}"/></td>
      </tr>
      <tr>
        <td>Requested Session Id</td>
        <td><c:out value="${pageContext.request.requestedSessionId}"/></td>
      </tr>
      <tr>
        <td>Requested Session Id from Cookie</td>
        <td><c:out value="${pageContext.request.requestedSessionIdFromCookie}"/></td>
      </tr>
      <tr>
        <td>Requested Session Id from URL</td>
        <td><c:out value="${pageContext.request.requestedSessionIdFromURL}"/></td>
      </tr>
      <tr>
        <td>Request URI</td>
        <td><c:out value="${pageContext.request.requestURI}"/></td>
      </tr>
      <tr>
        <td>Request URL</td>
        <td><c:out value="${pageContext.request.requestURL}"/></td>
      </tr>
      <tr>
        <td>Scheme</td>
        <td><c:out value="${pageContext.request.scheme}"/></td>
      </tr>
      <tr>
        <td>Secure</td>
        <td><c:out value="${pageContext.request.secure}"/></td>
      </tr>
      <tr>
        <td>Server Name</td>
        <td><c:out value="${pageContext.request.serverName}"/></td>
      </tr>
      <tr>
        <td>Server Port</td>
        <td><c:out value="${pageContext.request.serverPort}"/></td>
      </tr>
      <tr>
        <td>Servlet Path</td>
        <td><c:out value="${pageContext.request.servletPath}"/></td>
      </tr>
    </table>

    <center><h2>HTTP Headers (via JSTL)</h2></center>
    <table>
      <tr>
        <th>Name</th>
        <th>Value</th>
      </tr>
      <c:forEach items="${headerValues}" var="headerValue">
        <tr>
          <td><c:out value="${headerValue.key}" /></td>
          <td><c:forEach items="${headerValue.value}" var="value">
          <c:out value="${value}"/></c:forEach></td>
        </tr>
      </c:forEach>
    </table>

    <center><h2>Attributes</h2></center>
    <table>
      <tr>
        <th>Name</th>
      </tr>
      <req:attributes id="loop">
        <tr>
          <td><jsp:getProperty name="loop" property="name"/></td>
       </tr>
      </req:attributes>
    </table>

    <center><h2>Parameters</h2></center>
    <table>
      <tr>
        <th>Name</th>
        <th>Value</th>
      </tr>
      <req:parameters id="loop">
        <tr>
          <td><jsp:getProperty name="loop" property="name"/></td>
          <td><jsp:getProperty name="loop" property="value"/></td>
        </tr>
      </req:parameters>
    </table>
    <center><h2>Cookies</h2></center>
    <table>
      <tr>
        <th>Name</th>
        <th>Value</th>
      </tr>
      <req:cookies id="loop">
        <tr>
          <td><jsp:getProperty name="loop" property="name"/></td>
          <td><jsp:getProperty name="loop" property="value"/></td>
        </tr>
      </req:cookies>
     </table>
  </div>

