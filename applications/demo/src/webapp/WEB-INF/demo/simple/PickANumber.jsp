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
<%@ taglib uri='/WEB-INF/portlet.tld' prefix='portlet'%>
<%@ page import="javax.portlet.PortletSession"%>
<portlet:defineObjects/>

<portlet:actionURL var="myAction">
	<portlet:param name="myParam" value="testParam"/>
</portlet:actionURL>

<c:if test="${empty targetValue}">
  <c:set var="targetValue" scope="session" value="5"/>
  <c:set var="guessCount" scope="session" value="0"/>
</c:if>

<%
    PortletSession portletSession = renderRequest.getPortletSession(true);
    Long lg = (Long)portletSession.getAttribute("LastGuess", PortletSession.APPLICATION_SCOPE);
    String lastGuess = "50";
    if (lg != null)
    {
    	lastGuess = lg.toString();
    }
    else
    {
    	lg = new Long(50);
    	lastGuess = "50";
    }    	
%>

<c:set var="guessCount" scope="session" value="${guessCount + 1}"/>

target =  <c:out value="${targetValue}"/>
<br/>

last guess =  <c:out value="${param.Guess}"/>

<br/>

<div>
  You guessed <%=lastGuess%>
  <br/>
  Guess Count <c:out value="${guessCount}"/>
  <br/>
  <c:choose>
    <c:when test="${targetValue == (param.Guess + 0)}">
      <center><strong>You have guessed the number!!!!</strong></center>
      <c:remove var="targetValue" scope="session"/> 
    </c:when>
    <c:when test="${targetValue < (param.Guess + 0)}">
      You have guessed to high.  Try a lower Number
    </c:when>
    <c:when test="${targetValue > (param.Guess + 0)}">
      You have guessed to low.  Try a higher Number
    </c:when>
  </c:choose>
  <br/>
  <form action="<%=myAction%>" method="POST">
    <input type="text" name="Guess" value="<%=lastGuess%>">
    <input type="submit">
  </form>
</div>
