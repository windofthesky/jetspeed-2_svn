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
<%@ page language="java" import="javax.portlet.*, java.util.List" session="false" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<c:set var="portletRequest" value="${request}"/>

<h1>JMX Portlet Example</h1>
<% PortletRequest pr = (PortletRequest) request.getAttribute("javax.portlet.request"); %>
<% 
  List portletNames = (List)pr.getAttribute("portlets");
  request.setAttribute("portletNames", portletNames); 
%>

<div style="margin-left: 20px">
<h2 >
Number of registered portlets:
 <span style="color:red"><%=portletNames.size() %></span>
 
</h2>

<h2 >
Registered Portlets:
</h2>
<h3 style="margin-left: 20px">
 <ul>
  <c:forEach var="portletName"  items="${portletNames}" >
    <li><c:out value="${portletName}" /></li>
  </c:forEach>
 </ul>
</h3>


</div>