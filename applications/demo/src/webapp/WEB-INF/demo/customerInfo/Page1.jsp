<%@ page language="java"
         session="false"
%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<div>
  Hello from Page 1<br>
  <table>
    <c:forEach items="${CustomerList}" var="Customer">
    <tr>
      <td><c:out value="${Customer.name}" /></td>
      
      <td><fmt:formatDate value="${Customer.lastOrderedAsDate}" /></td>
    </tr>
    </c:forEach>
 </div>
