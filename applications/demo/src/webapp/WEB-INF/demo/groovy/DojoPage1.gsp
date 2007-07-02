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

<div dojoType="LayoutContainer" layoutChildPriority="left-right" style="width: 100%">

  <%=bundle.getString("page1.label.HelloFromPage1WithTaglib")%><br>

  <div id="mainTabContainer" dojoType="TabContainer" selectedChild="customers-renderRequest" doLayout="false">

    <div id="customers-renderRequest" dojoType="ContentPane" label="List 1">

      <div class="tableContainer">
        <table dojoType="SortableTable" widgetId="customersTable1" headClass="fixedHeader" tbodyClass="scrollContent" enableMultipleSelect="true" enableAlternateRows="true" rowAlternateClass="alternateRow" templateCssPath="/demo/css/demo.css" cellpadding="0" cellspacing="0" border="0">
          <col width="40%"></col>
          <col width="60%"></col>
          <thead>
            <tr>
              <th field="custName" dataType="String"><%=bundle.getString("page1.label.CustomerName")%></th>
              <th field="orderDate" dataType="String"><%=bundle.getString("page1.label.DateOfLastOrder")%></th>
            </tr>
          </thead>
          <tbody>
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
          </tbody>
        </table>
        
      </div>
      
    </div>
    
    <div id="customers-request" dojoType="ContentPane" label="List 2">

      <div class="tableContainer">

        <table dojoType="SortableTable" widgetId="customersTable2" headClass="fixedHeader" tbodyClass="scrollContent" enableMultipleSelect="true" enableAlternateRows="true" rowAlternateClass="alternateRow" templateCssPath="/demo/css/demo.css" cellpadding="0" cellspacing="0" border="0">
          <col width="40%"></col>
          <col width="60%"></col>
          <thead>
            <tr>
              <th field="custName" dataType="String"><%=bundle.getString("page1.label.CustomerName")%></th>
              <th field="orderDate" dataType="String"><%=bundle.getString("page1.label.DateOfLastOrder")%></th>
            </tr>
          </thead>
          <tbody>
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
          </tbody>
        </table>
        
      </div>
      
    </div>
    
  </div>
  
  <p>
    <%=bundle.getString("page1.label.NoteTheAboveListShouldMatch")%>
  </p>
  
</div>
