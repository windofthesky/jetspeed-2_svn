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

<bean:define id="productList" name="catalogBean" property="productList" />

<table align="left" bgcolor="#008800" border="0" cellspacing="2"
       cellpadding="2">
  <tr>
    <td bgcolor="#FFFF88">
      <html:link page="/shop/index.shtml">
        <b><font color="BLACK" size="2">&lt;&lt; Main Menu</font></b>
      </html:link>
    </td>
  </tr>
</table>

<table align="center" bgcolor="#008800" border="0" cellspacing="2"
       cellpadding="3">
  <tr bgcolor="#CCCCCC">
    <td>&nbsp;</td>
    <td><b>Product ID</b></td>
    <td><b>Name</b></td>
  </tr>

<logic:iterate id="product" name="productList" >
  <tr bgcolor="#FFFF88">
    <td>
      <html:link paramId="productId" paramName="product"
                 paramProperty="productId" page="/shop/viewProduct.shtml">
        <bean:write filter="false" name="product" property="description"/>
      </html:link>
    </td>
    <td>
      <b>
        <html:link paramId="productId" paramName="product"
                   paramProperty="productId" page="/shop/viewProduct.shtml">
          <font color="BLACK">
            <bean:write name="product" property="productId" /></font>
        </html:link>
      </b>
    </td>
    <td><bean:write name="product" property="name" /></td>
  </tr>
</logic:iterate>

  <tr>
    <td bgcolor="#FFFFFF" colspan="3">

<logic:notEqual name="productList" property="firstPage" value="true" >
      <html:link href="switchSearchListPage.shtml?pageDirection=previous">
        <html:img src="../images/button_prev.gif" border="0"/>
      </html:link>
</logic:notEqual>

<logic:notEqual name="productList" property="lastPage" value="true" >
      <html:link href="switchSearchListPage.shtml?pageDirection=next">
        <html:img src="../images/button_next.gif" border="0"/>
      </html:link>
</logic:notEqual>

    </td>
  </tr>

</table>

<%@include file="../common/IncludeBottom.jsp"%>
