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

<bean:define id="product" name="catalogBean" property="product" />
<bean:define id="item" name="catalogBean" property="item" />


<table align="left" bgcolor="#008800" border="0" cellspacing="2"
       cellpadding="2">
  <tr>
    <td bgcolor="#FFFF88">
      <html:link paramId="productId" paramName="product"
                 paramProperty="productId" page="/shop/viewProduct.shtml">
        <b>
          <font color="BLACK" size="2">
            &lt;&lt; <bean:write name="product" property="name" /></font>
        </b>
      </html:link>
    </td>
  </tr>
</table>

<p>

<table align="center" bgcolor="#008800" cellspacing="2" cellpadding="3"
       border="0" width="60%">
  <tr bgcolor="#FFFF88">
    <td bgcolor="#FFFFFF">
      <html-el:img src="${product.imagePath}"/>
      <bean:write filter="false" name="product" property="description"/>
    </td>
  </tr>
  <tr bgcolor="#FFFF88">
    <td width="100%" bgcolor="#cccccc">
      <b><bean:write name="item" property="itemId" /></b>
    </td>
  </tr>
  <tr bgcolor="#FFFF88">
    <td>
      <b>
        <font size="4">
          <bean:write name="item" property="attribute1" />
          <bean:write name="item" property="attribute2" />
          <bean:write name="item" property="attribute3" />
          <bean:write name="item" property="attribute4" />
          <bean:write name="item" property="attribute5" />
          <bean:write name="item" property="product.name" />
        </font>
      </b>
    </td>
  </tr>
  <tr bgcolor="#FFFF88">
    <td>
      <font size="3"><i><bean:write name="product" property="name" /></i></font>
    </td>
  </tr>
  <tr bgcolor="#FFFF88">
    <td>

<logic:lessEqual name="item" property="quantity" value="0">
      <font color="RED" size="2"><i>Back ordered.</i></font>
</logic:lessEqual>

<logic:greaterEqual name="item" property="quantity" value="1">
      <font size="2">
        <bean:write name="item" property="quantity" /> in stock.</font>
</logic:greaterEqual>

    </td>
  </tr>
  <tr bgcolor="#FFFF88">
    <td>
      <bean:write name="item" property="listPrice" format="$#,##0.00" />
    </td>
  </tr>

  <tr bgcolor="#FFFF88">
    <td>
      <html:link paramId="workingItemId" paramName="item" paramProperty="itemId"
                 page="/shop/addItemToCart.shtml">
        <html:img border="0" src="../images/button_add_to_cart.gif" />
      </html:link>
    </td>
  </tr>
</table>

<%@include file="../common/IncludeBottom.jsp"%>
