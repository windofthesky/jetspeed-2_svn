<%@ page language="java"
         session="false"
%>
<!--
  Copyright (c) 2003 The Apache Software Foundation.  All rights 
  reserved.

  @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
-->
 <div>
<% 
   if (request.getRemoteUser() != null)
   {
%>
   Welcome <%= request.getRemoteUser() %>
  <a href='<%= request.getContextPath() + "/logout.jsp" %>'>Logout</a>
<%
   }
   else
   {
%>
   <a href='<%= request.getContextPath() + "/LogInRedirector.jsp" %>'>Login</a>
<%
   }
%>

  </div>

