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
