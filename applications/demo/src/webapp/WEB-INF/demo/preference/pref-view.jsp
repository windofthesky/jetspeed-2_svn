<%@ page language="java" import="javax.portlet.*, java.util.List" session="false" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<h2>Preference List</h2>


<% PortletRequest pr = (PortletRequest) request.getAttribute("javax.portlet.request"); 
   request.setAttribute("pr",pr);
%>

<h3>Preference List</h3>
<c:set var="preferences" value="${pr.preferences}" scope="request"/>
<c:forEach var="prefName"  items="${preferences.names}" >
  <c:set var="prefName" value="${prefName}" scope="request"/>
  <%
    PortletPreferences prefs = (PortletPreferences) request.getAttribute("preferences");
	String prefName = (String) request.getAttribute("prefName");
	String prefValue = prefs.getValue(prefName, "undefined");
	request.setAttribute("prefValue", prefValue);
  %>
 <c:out value="${prefName}" /> =  <c:out value="${prefValue}" />
 
</c:forEach>