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
<%@ page session="false" %>
<%@ page import="javax.portlet.*"%>
<%@ page import="java.util.*"%>
<%@ taglib uri='/WEB-INF/portlet.tld' prefix='portlet'%>
<portlet:actionURL portletMode="edit" var="myAction">
<portlet:param name="add" value="add"/>
</portlet:actionURL>
<portlet:actionURL portletMode="view" var="myCancel"/>
<jsp:useBean id="addUrl" scope="request" class="java.lang.String" />
<jsp:useBean id="cancelUrl" scope="request" class="java.lang.String" />
<portlet:defineObjects/>
<%
ResourceBundle myText = portletConfig.getResourceBundle(request.getLocale());
%>
<%=myText.getString("available_bookmarks")%><br/>
<form action="<%=myAction%>" method="post">
<table>
  <tr>
    <th abbr="<%=myText.getString("name")%>"><%=myText.getString("name")%></th>
    <th abbr="<%=myText.getString("url")%>"><%=myText.getString("url")%></th>
    <th abbr="<%=myText.getString("delete")%>"></th>
  </tr>
<%
PortletPreferences prefs = renderRequest.getPreferences();
Enumeration e = prefs.getNames();
while (e.hasMoreElements())
  {
    String name = (String)e.nextElement();
    String value = prefs.getValue(name,
                       "<" +
                       myText.getString("undefined")
                       +">");
%>
    <tr>
      <td><%=name%></td>
      <td><%=value%></td>
<portlet:actionURL var="removeUrl"><portlet:param name="remove" value="<%=name%>"/></portlet:actionURL>
      <td><a href="<%=removeUrl.toString()%>">[<%=myText.getString("delete")%>]</a></td>
    </tr>
<%
  }
%>
  <tr>
    <td><input name="name" type="text"/></td>
    <td><input name="value" size='50' type="text"/></td>
    <td><input name="add" type="submit" value="<%=myText.getString("add")%>"/></td>
  </tr>
</table>
</form>
<form action="<%=myCancel%>" method="post">
<span>
<input name="cancel" type="submit" value="<%=myText.getString("cancel")%>"/>
</span>
</form>
