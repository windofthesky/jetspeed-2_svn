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

<html:form action="/shop/newOrder.shtml" styleId="orderBean" method="post" >

  <TABLE bgcolor="#008800" border=0 cellpadding=3 cellspacing=1 bgcolor=#FFFF88>
    <TR bgcolor=#FFFF88>
      <TD colspan=2><FONT color=GREEN size=4><B>Shipping Address</B></FONT></TD>
    </TR>
    <TR bgcolor=#FFFF88>
      <TD>First name:</TD>
      <TD><html:text name="orderBean" property="order.shipToFirstName" /></TD>
    </TR>
    <TR bgcolor=#FFFF88>
      <TD>Last name:</TD>
      <TD><html:text name="orderBean" property="order.shipToLastName" /></TD>
    </TR>
    <TR bgcolor=#FFFF88>
      <TD>Address 1:</TD>
      <TD>
        <html:text size="40" name="orderBean" property="order.shipAddress1" />
      </TD>
    </TR>
    <TR bgcolor=#FFFF88>
      <TD>Address 2:</TD>
      <TD>
        <html:text size="40" name="orderBean" property="order.shipAddress2" />
      </TD>
    </TR>
    <TR bgcolor=#FFFF88>
      <TD>City: </TD>
      <TD><html:text name="orderBean" property="order.shipCity" /></TD>
    </TR>
    <TR bgcolor=#FFFF88>
      <TD>State:</TD>
      <TD>
        <html:text size="4" name="orderBean" property="order.shipState" />
      </TD>
    </TR>
    <TR bgcolor=#FFFF88>
      <TD>Zip:</TD>
      <TD><html:text size="10" name="orderBean" property="order.shipZip" /></TD>
    </TR>
    <TR bgcolor=#FFFF88>
      <TD>Country: </TD>
      <TD>
        <html:text size="15" name="orderBean" property="order.shipCountry" />
      </TD>
    </TR>
  </TABLE>

  <P>
  <html:image src="../images/button_submit.gif"/>

</html:form>

<%@include file="../common/IncludeBottom.jsp"%>
