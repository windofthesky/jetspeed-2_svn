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
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<fmt:setBundle basename="org.apache.jetspeed.demo.preferences.resources.PreferenceResources" />

<portlet:defineObjects/>

<h3><fmt:message key="prefview.label.PreferenceList"/></h3>

<p>
<fmt:message key="prefview.label.noactionjustrender" var="viewMeParam"/>
<jsp:useBean id="viewMeParam" type="String"/>

<portlet:renderURL windowState="normal" portletMode="view" var="viewLink">
  <portlet:param name="invokeMessage" value="<%=viewMeParam%>"/>
</portlet:renderURL>
<a href="<c:out value="${viewLink}" />"><fmt:message key="prefview.label.ViewMe"/></a>
</p>

<p>
<a href="<portlet:actionURL windowState="normal" portletMode="view" />"><fmt:message key="prefview.label.InvokeMyAction"/></a>
</p>
<vel:velocity>
$renderRequest.getAttribute("viewMessage")
<br>

#foreach($paramName in $renderRequest.ParameterNames)
<fmt:message key="prefview.label.Parameter"/> $paramName
<br />
#end

<br>
$!renderRequest.getParameter("invokeMessage")

#set($preferences = $renderRequest.Preferences)


<br>
#foreach( $prefName in $preferences.Names)
  
  ${prefName} <br/>
  #foreach($prefValue in $preferences.getValues($prefName, null))
    &nbsp;&nbsp; <fmt:message key="prefview.label.value"/> $velocityCount = ${prefValue} <br/>
  #end
 
#end
</vel:velocity>