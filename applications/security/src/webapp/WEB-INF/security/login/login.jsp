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
<%@page import="org.apache.jetspeed.login.LoginConstants" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="org.apache.jetspeed.portlets.security.resources.LoginResources" />

<html>
  <title><fmt:message key="login.label.Login"/></title>
  <body>
  <% if ( request.getUserPrincipal() != null )
  		{ %>
  	<fmt:message key="login.label.Welcome"><fmt:param><%= request.getUserPrincipal().getName() %></fmt:param></fmt:message><br>
  	<a href='<%= response.encodeURL(request.getContextPath()+"/login/logout") %>'><fmt:message key="login.label.Logout"/></a>
  <% 	}
    	else
			{
					Integer retryCount = (Integer)request.getSession().getAttribute(LoginConstants.RETRYCOUNT);
					if ( retryCount != null )
					{ %>
		<br><i><fmt:message key="login.label.InvalidUsernameOrPassword"><fmt:param><%=retryCount%></fmt:param></fmt:message></i><br>
		   <% } %>
		<form method="POST" action='<%= response.encodeURL(request.getContextPath()+"/login/proxy")%>'>
      <fmt:message key="login.label.Username"/> <input type="text" size="15" name="<%=LoginConstants.USERNAME%>">
      <br>
      <fmt:message key="login.label.Password"/> <input type="password" size="15" name="<%=LoginConstants.PASSWORD%>">
      <input type="submit" value="<fmt:message key="login.label.Login"/>">
    </form>
  <% } %>
  </body>
</html>

