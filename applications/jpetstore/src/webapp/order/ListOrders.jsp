<%--
 Copyright 2000-2004 Apache Software Foundation

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
<%@include file="../common/IncludeTop.jsp"%>

<center>
  <font size="4"><b>My Orders</b></font>
</center>
<BR>
<table align="center" bgcolor="#008800" border="0" cellspacing="2"
       cellpadding="3">
  <tr bgcolor="#CCCCCC">
    <td><b>Order ID</b></td>
    <td><b>Date</b></td>
    <td><b>Total Price</b></td>
  </tr>

<logic:iterate id="order" name="orderBean" property="orderList">
  <tr bgcolor="#FFFF88">
    <td>
      <b>
        <html:link paramId="orderId" paramName="order" paramProperty="orderId"
                   page="/shop/viewOrder.shtml">
          <font color="BLACK">
            <bean:write name="order" property="orderId" />
          </font>
        </html:link>
      </b>
    </td>
    <td>
      <bean:write name="order" property="orderDate"
                  format="yyyy/MM/dd hh:mm:ss" />
    </td>
    <td>
      <bean:write name="order" property="totalPrice" format="$#,##0.00" />
    </td>
  </tr>
</logic:iterate>

</table>

<BR>
<center>

<logic:notEqual name="orderBean" property="orderList.firstPage" value="true" >
  <html:link href="switchOrderPage.shtml?pageDirection=previous">
    <html:img src="../images/button_prev.gif" border="0"/>
  </html:link>
</logic:notEqual>

<logic:notEqual name="orderBean" property="orderList.lastPage" value="true" >
  <html:link href="switchOrderPage.shtml?pageDirection=next">
    <html:img src="../images/button_next.gif" border="0"/>
  </html:link>
</logic:notEqual>

</center>

<%@include file="../common/IncludeBottom.jsp"%>
