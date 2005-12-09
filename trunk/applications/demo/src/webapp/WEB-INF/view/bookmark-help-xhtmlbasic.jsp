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
<portlet:actionURL portletMode="view" var="myCancel"/>
<portlet:defineObjects/>
<%
ResourceBundle myText = portletConfig.getResourceBundle(request.getLocale());
%>
<h2><%=myText.getString("help.title")%></h2>
<p><%=myText.getString("help.text")%></p>
<form action="<%=myCancel%>" method="post">
<span>
<input name="cancel" type="submit" value="<%=myText.getString("cancel")%>">
</span>
</form>
