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
Pick a Number Guess Count Game 
</h2>

<c:choose>
<c:when test="${empty guessCount}">
</c:when>
<c:when test="${targetValue == lastGuess}">
</c:when>
<c:otherwise>
You have made <c:out value="${guessCount}"/> guess thus far.
</c:otherwise>
</c:choose>

<c:choose>
<c:when test="${targetValue == lastGuess}">
<p>
Start a new Game now!<br/>Enter a number between 1 and <c:out value="${TopRange}"/>
</p>
</c:when>
<c:otherwise>
<p>
Enter a number between 1 and <c:out value="${TopRange}"/>
</p>
</c:otherwise>
</c:choose>

<p>
  <c:choose>
    <c:when test="${empty targetValue}">
       Ready to start a New game.
    </c:when>  
    <c:when test="${empty lastGuess}">
       Ready to start a New game.
    </c:when>      
    <c:when test="${targetValue == lastGuess}">
      <center><strong><%=lastGuess%> is correct! You have guessed the number in <c:out value="${guessCount}"/> guesses!!!!</strong></center>
      <c:remove var="targetValue" scope="session"/> 
    </c:when>
    <c:when test="${targetValue < lastGuess}">
      You have guessed to high.  Try a lower Number 
    </c:when>
    <c:when test="${targetValue > lastGuess}">
      You have guessed to low.  Try a higher Number
    </c:when>
    <c:otherwise>
       Ready to start a New game.    
    </c:otherwise>
  </c:choose>
</p>
<p>
  <form action="<%=myAction%>" method="POST">
    <input type="text" name="Guess" value="<%=lastGuess%>"/>
    <input type="submit" value='Guess'/>
  </form>
</p>