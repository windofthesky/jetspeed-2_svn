<%@ page language="java" import="javax.portlet.*, java.util.List" session="true" %>
<%@ taglib uri='/WEB-INF/portlet.tld' prefix='portlet'%>
<%@ taglib uri='/WEB-INF/veltag.tld' prefix='vel'%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<portlet:defineObjects/>

<h3>Preference List</h3>

<p>

<portlet:renderURL windowState="normal" portletMode="view"  var="viewLink">
	 <portlet:param name="invokeMessage" value="No action just render" />
</portlet:renderURL>

<a href="<c:out value="${viewLink}" />">View Me!!!</a>
</p>

<p>
<a href="<portlet:actionURL windowState="normal" portletMode="view" />">Invoke My Action!!!</a>
</p>
<vel:velocity>
$renderRequest.getAttribute("viewMessage")
<br>

#foreach($paramName in $renderRequest.ParameterNames)
Parameter: $paramName
<br />
#end

<br>
$!renderRequest.getParameter("invokeMessage")

#set($preferences = $renderRequest.Preferences)


<br>
#foreach( $prefName in $preferences.Names)
  
  ${prefName} <br/>
  #foreach($prefValue in $preferences.getValues($prefName, null))
    &nbsp;&nbsp; value $velocityCount = ${prefValue} <br/>
  #end
 
#end
</vel:velocity>