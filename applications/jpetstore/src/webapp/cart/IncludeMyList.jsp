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
<bean:define id="myList" name="accountBean" property="myList" />

<logic:present name="myList" >
  <p>&nbsp;</p>
  <table align="right" bgcolor="#008800" border="0" cellspacing="2"
         cellpadding="3">
    <tr bgcolor="#CCCCCC">
      <td>
        <font size="4"><b>Pet Favorites</b></font>
        <font size="2"><i>
            <br/>Shop for more of your
            <br/>favorite pets here.</i></font>
      </td>
    </tr>
    <tr bgcolor="#FFFF88">
      <td>

  <logic:iterate id="product" name="myList" >
        <html:link paramId="productId" paramName="product"
                   paramProperty="productId" page="/shop/viewProduct.shtml">
          <bean:write name="product" property="name" />
        </html:link>
        <br />
        <font size="2">
          (<bean:write name="product" property="productId" />)</font>
        <br />
  </logic:iterate>

      </td>
    </tr>
    <tr>
      <td bgcolor="#FFFFFF">

  <logic:notEqual name="myList" property="firstPage" value="true" >
        <html:link href="switchMyListPage.shtml?pageDirection=previous">
          <html:img src="../images/button_prev.gif" border="0"/>
        </html:link>
  </logic:notEqual>

  <logic:notEqual name="myList" property="lastPage" value="true" >
        <html:link href="switchMyListPage.shtml?pageDirection=next">
          <html:img src="../images/button_next.gif" border="0"/>
        </html:link>
  </logic:notEqual>

      </td>
    </tr>
  </table>
</logic:present>
