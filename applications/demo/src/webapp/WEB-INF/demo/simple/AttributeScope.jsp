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
<%@ taglib uri='/WEB-INF/portlet.tld' prefix='portletAPI'%>

<portletAPI:init/>
<div>
  Portlet Name = <c:out value="${portletConfig.portletName}"/><br/>
<%
{
  PortletSession portletSession = portletRequest.getPortletSession();
  out.println( "Session ID = " + portletSession.getId() + "<br/>");
}
%>

  <h2>Application Scope Attributes</h2>
<%
{
  Object attribute = null;
  PortletSession portletSession = portletRequest.getPortletSession();
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
  <h2>Portlet Scope Attributes</h2>
<%
{
  Object attribute = null;
  PortletSession portletSession = portletRequest.getPortletSession();
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
  <h2>Request Scope Attributes</h2>
<%
{
  Object attribute = null;
  Enumeration attributes = portletRequest.getAttributeNames();
  while (attributes.hasMoreElements())
  {
    String name = (String)attributes.nextElement();
    attribute = portletRequest.getAttribute(name);
    if (attribute!=null)
    {
      out.print(name + " = " + attribute + "<br/>");
    }
  }
}
%>
  <br/>
  <form action="<%= portletResponse.createActionURL() %>" method="POST">
    <input type="submit" value="Increment Counters">
  </form>
</div>
