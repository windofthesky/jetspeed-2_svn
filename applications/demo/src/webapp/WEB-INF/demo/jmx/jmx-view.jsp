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