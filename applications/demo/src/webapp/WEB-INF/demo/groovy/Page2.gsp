<%/*
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/%>
<%
import java.text.DateFormat

def renderRequest = request.getAttribute("javax.portlet.request")
def renderResponse = request.getAttribute("javax.portlet.response")

def bundle = ResourceBundle.getBundle("org.apache.jetspeed.demo.customerInfo.resources.CustomerInfoResources")
def df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, request.locale);
%>

<div>
  <%=bundle.getString("page2.label.HelloFromPage2")%><br>
  <%
  def customerList = renderRequest.getAttribute("CustomerList")
  def currentCustomer = renderRequest.getAttribute("CurrentCustomer")
  
  if (currentCustomer == null) {
    currentCustomer = customerList.get(0)
  }
  %>
  <table>
    <tr>
      <td><%=bundle.getString("page2.label.CustomerName")%></td>
      <td><%=bundle.getString("page2.label.sepa")%></td>
      <td>${currentCustomer.name}</td>
    </tr>
    <tr>
      <td><%=bundle.getString("page2.label.LastOrdered")%></td>
      <td><%=bundle.getString("page2.label.sepa")%></td>
      <td><%=df.format(currentCustomer.lastOrderedAsDate)%></td>
    </tr>
  </table>
  <ul>
    <% if (currentCustomer.billingAddress != null) { %>
    <li><%=bundle.getString("page2.label.ShowBillingAddressInCustomerDetailWindow")%></li>
    <% } %>
    <% if (currentCustomer.shippingAddress != null) { %>
    <li><%=bundle.getString("page2.label.ShowShippingAddressInCustomerDetailWindow")%></li>
    <% } %>
  </ul> 
 </div>
