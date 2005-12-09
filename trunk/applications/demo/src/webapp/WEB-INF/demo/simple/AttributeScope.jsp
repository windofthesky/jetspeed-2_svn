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
<%@ page language="java" session="true" %>
<%@ page import="java.util.*" %>
<%@ page import="javax.portlet.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri='/WEB-INF/portlet.tld' prefix='portletAPI'%>
<fmt:setBundle basename="org.apache.jetspeed.demo.simple.resources.AttributeScopeResources" />

<portletAPI:defineObjects/>
<div>
<fmt:message key="attributescope.label.PortletName"/> = <c:out value="${portletConfig.portletName}"/><br/>
<fmt:message key="attributescope.label.SessionID"/>
<%
{
  PortletSession portletSession = renderRequest.getPortletSession(true);
  out.println( " = " + portletSession.getId() + "<br/>");
}
%>

  <h2><fmt:message key="attributescope.label.ApplicationScopeAttributes"/></h2>
<%
{
  Object attribute = null;
  PortletSession portletSession = renderRequest.getPortletSession(true);
  Enumeration attributes = portletSession.getAttributeNames(PortletSession.APPLICATION_SCOPE);
  while (attributes.hasMoreElements())
  {
    String name = (String)attributes.nextElement();
    attribute = portletSession.getAttribute(name, PortletSession.APPLICATION_SCOPE);
    if (attribute!=null)
    {
      out.print(name + " = " + attribute + "<br/>");
    }
  }
}
%>
  <h2><fmt:message key="attributescope.label.PortletScopeAttributes"/></h2>
<%
{
  Object attribute = null;
  PortletSession portletSession = renderRequest.getPortletSession(true);
  Enumeration attributes = portletSession.getAttributeNames(PortletSession.PORTLET_SCOPE);
  while (attributes.hasMoreElements())
  {
    String name = (String)attributes.nextElement();
    attribute = portletSession.getAttribute(name, PortletSession.PORTLET_SCOPE);
    if (attribute!=null)
    {
      out.print(name + " = " + attribute + "<br/>");
    }
  }
}
%>
  <h2><fmt:message key="attributescope.label.RequestScopeAttributes"/></h2>
<%
{
  Object attribute = null;
  Enumeration attributes = renderRequest.getAttributeNames();
  while (attributes.hasMoreElements())
  {
    String name = (String)attributes.nextElement();
    attribute = renderRequest.getAttribute(name);
    if (attribute!=null)
    {
      out.print(name + " = " + attribute + "<br/>");
    }
  }
}
%>
  <br/>
  <form action="<%= renderResponse.createActionURL() %>" method="POST">
    <input type="submit" value="<fmt:message key="attributescope.label.IncrementCounters"/>">
  </form>
</div>
