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