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
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.List" %>
<%@ page import="org.apache.jetspeed.demo.customerInfo.CustomerInfo" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri='/WEB-INF/portlet.tld' prefix='portlet'%>
<portlet:defineObjects/>

<div>
  Hello from Page 1 with Taglib<br>
  <table width="100%">
    <tr bgcolor="lightgray">
      <th>Customer Name</th>
      <th>Date of Last Order</th>
    </tr>
    <c:forEach items="${CustomerList}" var="Customer">
    <tr>
      <td><c:out value="${Customer.name}" /></td>
      
      <td><fmt:formatDate value="${Customer.lastOrderedAsDate}" /></td>
    </tr>
    </c:forEach>
    <tr>
      <td colspan="2" align="center">** End of List in request **</td>
    </tr>
<%
 List customerList = (List) renderRequest.getAttribute("CustomerList");
 for (Iterator i = customerList.iterator(); i.hasNext();)
 {
   CustomerInfo customer = (CustomerInfo) i.next();
   out.println("<tr>");
   out.println("  <td>");
   out.println("    " + customer.getName()); 
   out.println("  </td>");
   out.println("  <td>");
   out.println("    " + customer.getLastOrderedAsDate()); 
   out.println("  </td>");
   out.println("</tr>");
 }
%>
    <tr>
      <td colspan="2" align="center">** End of List in renderRequest **</td>
    </tr>
  </table>
  <p>
    <strong>NOTE:</strong> The above list should match!
  </p>
</div>
