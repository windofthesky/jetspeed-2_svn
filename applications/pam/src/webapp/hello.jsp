<%@ page language="java" import="javax.portlet.*, java.util.List" session="true" %>
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





