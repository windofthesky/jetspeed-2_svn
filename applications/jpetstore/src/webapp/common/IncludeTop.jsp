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
<%@page contentType="text/html"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://portals.apache.org/bridges/struts/tags-portlet-html" prefix="html" %>
<%@ taglib uri="http://portals.apache.org/bridges/struts/tags-portlet-html-el" prefix="html-el" %>

    <table background="<html:rewrite href="../images/bkg-topbar.gif"/>" border="0" cellspacing="0"
           cellpadding="5" width="100%">
      <tr>
        <td>
          <html:link page="/shop/index.shtml">
            <html:img border="0" src="../images/logo-topbar.gif" />
          </html:link>
        </td>
        <td align="right">
          <html:link page="/shop/viewCart.shtml">
            <html:img border="0" imageName="img_cart" src="../images/cart.gif" />
          </html:link>
          <html:img border="0" src="../images/separator.gif" />

<logic:notPresent name="accountBean" scope="session">
          <html:link page="/shop/signonForm.shtml">
            <html:img border="0" imageName="img_signin" src="../images/sign-in.gif" />
          </html:link>
</logic:notPresent>

<logic:present name="accountBean" scope="session">
  <logic:notEqual name="accountBean" property="authenticated" value="true"
                  scope="session">
          <html:link page="/shop/signonForm.shtml">
            <html:img border="0" imageName="img_signin" src="../images/sign-in.gif" />
          </html:link>
  </logic:notEqual>
</logic:present>

<logic:present name="accountBean" scope="session">
  <logic:equal name="accountBean" property="authenticated" value="true"
               scope="session">
          <html:link page="/shop/signoff.shtml">
            <html:img border="0" imageName="img_signout" src="../images/sign-out.gif" />
          </html:link>
          <html:img border="0" src="../images/separator.gif" />
          <html:link page="/shop/editAccountForm.shtml">
            <html:img border="0" imageName="img_myaccount"
                 src="../images/my_account.gif" />
          </html:link>
  </logic:equal>
</logic:present>

          <html:img border="0" src="../images/separator.gif" />
          <html:link href="../help.shtml">
            <html:img border="0" imageName="img_help" src="../images/help.gif" />
          </html:link>
        </td>
        <td align="left" valign="bottom">
          <html:form method="post" action="/shop/searchProducts.shtml">
            <input name="keyword" size="14" />
            <html:image border="0" src="../images/search.gif"/>
          </html:form>
        </td>
      </tr>
    </table>

<%@include file="../common/IncludeQuickHeader.jsp"%>

<!-- Support for non-traditional but simple message -->
<logic:present name="message">
    <b><font color="BLUE"><bean:write name="message" /></font></b>
</logic:present>

<!-- Support for non-traditional but simpler use of errors... -->
<logic:present name="errors">
  <logic:iterate id="error" name="errors">
    <B><FONT color=RED>
      <BR>
      <bean:write name="error" /></FONT></B>
  </logic:iterate>
</logic:present>
