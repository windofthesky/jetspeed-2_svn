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
<%@ page language="java" session="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri='/WEB-INF/portlet.tld' prefix='portlet'%>
<%@ page import="javax.portlet.PortletSession"%>
<portlet:defineObjects/>
<fmt:setBundle basename="org.apache.jetspeed.demo.simple.resources.PickANumberResources" />

<portlet:actionURL var="myAction">
	<portlet:param name="myParam" value="testParam"/>
</portlet:actionURL>

<%
    PortletSession portletSession = renderRequest.getPortletSession(true);
    Long value = (Long)portletSession.getAttribute("LastGuess", PortletSession.APPLICATION_SCOPE);    
    long lastGuess = 0;
    if (value != null)
    {
    	lastGuess = value.longValue();
    }
        	
%>

<c:set var="guessCount" scope="session" value="${GuessCount}"/>
<c:set var="targetValue" scope="session" value="${TargetValue}"/>
<c:set var="lastGuess" scope="session" value="${LastGuess}"/>
<c:set var="topRange" scope="session" value="${TopRange}"/>

<h2>
<fmt:message key="pickanumber.label.pickanumberguess"/>
</h2>

<c:choose>
<c:when test="${empty guessCount}">
</c:when>
<c:when test="${targetValue == lastGuess}">
</c:when>
<c:otherwise>
<fmt:message key="pickanumber.label.guessthusfar">
	<fmt:param><c:out value="${guessCount}"/></fmt:param>
</fmt:message>
</c:otherwise>
</c:choose>

<c:choose>
<c:when test="${targetValue == lastGuess}">
<p>
<fmt:message key="pickanumber.label.startnewgame"/><br/><fmt:message key="pickanumber.label.enternumber"><fmt:param><c:out value="${TopRange}"/></fmt:param></fmt:message>
</p>
</c:when>
<c:otherwise>
<p>
<fmt:message key="pickanumber.label.enternumber"><fmt:param><c:out value="${TopRange}"/></fmt:param></fmt:message>
</p>
</c:otherwise>
</c:choose>

<p>
  <c:choose>
    <c:when test="${empty targetValue}">
       <fmt:message key="pickanumber.label.readytostartanewgame"/>
    </c:when>  
    <c:when test="${empty lastGuess}">
       <fmt:message key="pickanumber.label.readytostartanewgame"/>
    </c:when>      
    <c:when test="${targetValue == lastGuess}">
      <center><strong><fmt:message key="pickanumber.label.guessiscorrect"><fmt:param><%=lastGuess%></fmt:param><fmt:param><c:out value="${guessCount}"/></fmt:param></fmt:message></strong></center>
      <c:remove var="targetValue" scope="session"/> 
    </c:when>
    <c:when test="${targetValue < lastGuess}">
      <fmt:message key="pickanumber.label.guessedtohigh"/>
    </c:when>
    <c:when test="${targetValue > lastGuess}">
      <fmt:message key="pickanumber.label.guessedtolow"/>
    </c:when>
    <c:otherwise>
       <fmt:message key="pickanumber.label.readytostartanewgame"/>
    </c:otherwise>
  </c:choose>
</p>
<p>
  <form action="<%=myAction%>" method="POST">
    <input type="text" name="Guess" value="<%=lastGuess%>"/>
    <input type="submit" value='<fmt:message key="pickanumber.label.guess"/>'/>
  </form>
</p>