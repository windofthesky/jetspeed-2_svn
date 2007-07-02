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

def bundle = ResourceBundle.getBundle("org.apache.jetspeed.demo.customerInfo.resources.CustomerListResources")
def df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, request.locale);
%>

<div>
  <%=bundle.getString("page1.label.HelloFromPage1WithTaglib")%><br>
  <table width="100%">
    <tr bgcolor="lightgray">
      <th><%=bundle.getString("page1.label.CustomerName")%></th>
      <th><%=bundle.getString("page1.label.DateOfLastOrder")%></th>
    </tr>
    <%
    def customerList = renderRequest.getAttribute("CustomerList")
    for (customer in customerList)
    {
    %>
    <tr>
      <td>${customer.name}</td>
      <td><%=df.format(customer.lastOrderedAsDate)%></td>
    </tr>
    <%
    }
    %>
    <tr>
      <td colspan="2" align="center"><%=bundle.getString("page1.label.EndOfListInRequest")%></td>
    </tr>
    
    <%
    customerList = request.getAttribute("CustomerList");
    for (customer in customerList)
    {
    %>
    <tr>
      <td>${customer.name}</td>
      <td><%=df.format(customer.lastOrderedAsDate)%></td>
    </tr>
    <%
    }
    %>    
    
    <tr>
      <td colspan="2" align="center"><%=bundle.getString("page1.label.EndOfListInRenderRequest")%></td>
    </tr>
  </table>
  <p>
    <%=bundle.getString("page1.label.NoteTheAboveListShouldMatch")%>
  </p>
</div>
