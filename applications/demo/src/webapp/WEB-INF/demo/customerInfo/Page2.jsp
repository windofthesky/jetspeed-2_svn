<%@ page language="java"
         session="false"
%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<div>
  Hello from Page 2<br>
  <c:if test="${empty CurrentCustomer}">
    <c:set var="CurrentCustomer" value="${CustomerList[0]}"/>
  </c:if>
  <table>
    <tr>
      <td>CustomerName</td>
      <td>:</td>
      <td><c:out value="${CurrentCustomer.name}" /></td>
    </tr>
    <tr>
      <td>Last Ordered</td>
      <td>:</td>
      <td><fmt:formatDate value="${CurrentCustomer.lastOrderedAsDate}" /></td>
    </tr>
  </table>
  <ul>
    <c:if test="${CurrentCustomer.billingAddress != null}">
    <li>Show Billing Address in CustomerDetail window</li>
    </c:if>
    <c:if test="${CurrentCustomer.shippingAddress != null}">
    <li>Show Shipping Address in CustomerDetail window</li>
    </c:if>
  </ul> 
 </div>
