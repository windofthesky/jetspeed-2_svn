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
<%@ page language="java" import="javax.portlet.*, java.util.List, java.util.Iterator, org.apache.jetspeed.om.common.portlet.MutablePortletApplication" session="true" %>
<%@ taglib uri='/WEB-INF/portlet.tld' prefix='portlet'%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<portlet:defineObjects/>
<p>This page was invoked from a LOCAL portlet app</P>
<a href='/snipet.html'>somelink</a>

<p>renderURL</p>

<p>
<portlet:renderURL windowState="normal" portletMode="view"  var="myView">
	 <portlet:param name="invokeMsg" value="No action just render" />
</portlet:renderURL>
<a href="<c:out value="${myView}" />">View!</a>

</p>

<p>"<c:out value="${myView}" />"</p>

<p>
<a href="<portlet:actionURL windowState="normal" portletMode="view" />">My Action!!!</a>
<a href="<portlet:actionURL windowState="normal" portletMode="view" />">Invoke My Action!!!</a>

</p>


<br>

<table>
<%
 List apps = (List) renderRequest.getAttribute("apps");
 for (Iterator i = apps.iterator(); i.hasNext();)
 {
   MutablePortletApplication pa = (MutablePortletApplication) i.next();
   out.println("<tr><td>" + pa.getName() + "</td>");
   out.println("<td>" + pa.getDescription() + "</td></tr>"); 
 }
%>
</table>


