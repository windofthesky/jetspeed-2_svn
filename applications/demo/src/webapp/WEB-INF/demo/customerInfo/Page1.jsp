<%@ page language="java"
         session="false"
%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri='/WEB-INF/portlet.tld' prefix='portlet'%>
<portlet:defineObjects/>

<div>
  Hello from Page 1 with Taglib<br>
  <table>
    <c:forEach items="${CustomerList}" var="Customer">
    <tr>
      <td><c:out value="${Customer.name}" /></td>
      
      <td><fmt:formatDate value="${Customer.lastOrderedAsDate}" /></td>
    </tr>
    </c:forEach>
 </div>
