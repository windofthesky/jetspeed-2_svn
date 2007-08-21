<%--
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
--%>
<%@ page session="false"%>
<%@ taglib uri='/WEB-INF/portlet.tld' prefix='portlet'%>
<%@ page import="org.apache.jetspeed.request.RequestContext"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c_rt"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<fmt:setBundle basename="org.apache.jetspeed.portlet.helloworld.resources.HelloWorldResources" />
<portlet:defineObjects/>
<c_rt:set var="requestContext" value="<%=request.getAttribute(RequestContext.REQUEST_PORTALENV)%>"/>
<c:set var="portalContextPath" value="${requestContext.request.contextPath}"/>
<p><fmt:message key="hello.label.ThisIsATestLine"/></p>
<p><b><portlet:namespace/><fmt:message key="hello.label.TestAtPlutoOrg"/></b></p>
<p><fmt:message key="hello.label.WeNeedToDoSomething"/></p>
<p>
<fmt:message key="hello.label.PortletModeIs">
   <fmt:param><%= renderRequest.getPortletMode().toString() %></fmt:param>
</fmt:message>
<fmt:message key="hello.label.WindowStateIs">
   <fmt:param><%= renderRequest.getWindowState().toString() %></fmt:param>
</fmt:message> 
</p>
<do type="accept" label="logout">
<go href="<c:url context="${portalContextPath}" value="/login/logout"/>"/>
</do>
