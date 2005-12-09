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
<%@ page session="false"%>
<%@ taglib uri='/WEB-INF/portlet.tld' prefix='portlet'%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<fmt:setBundle basename="org.apache.jetspeed.portlet.helloworld.resources.HelloWorldResources" />
<portlet:defineObjects/>
<p><fmt:message key="hello.label.ThisIsATestLine"/></P>
<b><portlet:namespace/><fmt:message key="hello.label.TestAtPlutoOrg"/></b>

<h2><fmt:message key="hello.label.WeNeedToDoSomething"/></h2>
<p>
<fmt:message key="hello.label.PortletModeIs">
   <fmt:param><%= renderRequest.getPortletMode().toString() %></fmt:param>
</fmt:message>
<fmt:message key="hello.label.WindowStateIs">
   <fmt:param><%= renderRequest.getWindowState().toString() %></fmt:param>
</fmt:message> <br/>
</p>
