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
<%@ page contentType="text/html" %>
<%@ taglib uri="http://portals.apache.org/bridges/struts/tags-portlet-html" prefix="html" %>
    <TABLE background="<html:rewrite href="images/bkg-topbar.gif"/>" border=0 cellSpacing=0
           cellPadding=5 width="100%">
        <TR>
          <TD>
            <html:link href="shop/index.shtml">
              <html:img border="0" src="images/logo-topbar.gif"/>
            </html:link>
          </TD>
          <TD align=right>
            <html:link href="shop/index.shtml">
              <html:img border="0" imageName="img_cart" src="images/cart.gif"/>
            </html:link>
            <html:img border="0" src="images/separator.gif"/>
            <html:link href="shop/signonForm.shtml" >
              <html:img border="0" imageName="img_signin" src="images/sign-in.gif"/>
            </html:link>
            <html:img border="0" src="images/separator.gif"/>
            <html:link href="help.shtml">
              <html:img border="0" imageName="img_help" src="images/help.gif"/>
            </html:link>
          </TD>
          <TD align=left valign="bottom">
            <html:form method="post" action="/shop/searchProducts.shtml">
              <INPUT name=keyword size=14>
              <html:image border="0" src="images/search.gif"/>
            </html:form>
          </TD>
        </TR>
    </TABLE>
    <TABLE border=0 cellSpacing=0 width="100%">
        <TR>
          <TD vAlign=top width=100%>
            <p>&nbsp;</p>
            <p align="center"><b>Welcome to JPetStore 4</b></p>
            <p align="center">
              <html:link href="shop/index.shtml">Enter the Store</html:link>
            </p>
          </TD>
        </TR>
    </TABLE>

    <P>&nbsp;</P>

    <P align="center">
      <a href="http://www.ibatis.com">
        <html:img border="0" align="center" src="images/poweredby.gif"/>
      </a>
    </P>
