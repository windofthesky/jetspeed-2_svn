<%@ page language="java" session="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<c:if test="${empty targetValue}">
  <c:set var="targetValue" scope="session" value="5"/>
  <c:set var="guessCount" scope="session" value="0"/>
</c:if>

<c:set var="guessCount" scope="session" value="${guessCount + 1}"/>
<div>
  You guessed <c:out value="${param.Guess}"/>
  <br/>
  Guess Count <c:out value="${guessCount}"/>
  <br/>
  <c:choose>
    <c:when test="${targetValue == param.Guess}">
      <center><strong>You have guessed the number!!!!</strong></center>
      <c:remove var="targetValue" scope="session"/> 
    </c:when>
    <c:when test="${targetValue < (param.Guess + 0) }">
      You have guessed to high.  Try a lower Number
    </c:when>
    <c:when test="${targetValue > (param.Guess + 0)}">
      You have guessed to low.  Try a higher Number
    </c:when>
  </c:choose>
  <br/>
  <form action="PickANumber.jsp" method="POST">
    <input type="text" name="Guess">
    <input type="submit">
  </form>
</div>
