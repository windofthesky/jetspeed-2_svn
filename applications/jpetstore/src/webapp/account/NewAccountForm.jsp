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

<html:form action="/shop/newAccount.shtml" method="post" >

  <html:hidden name="accountBean" property="validation" value="new" />

  <TABLE cellpadding=10 cellspacing=0 align=center border=1 bgcolor=#dddddd>
    <TR>
      <TD>
        <FONT color=darkgreen><H3>User Information</H3></FONT>
        <TABLE bgcolor="#008800" border=0 cellpadding=3 cellspacing=1
               bgcolor=#FFFF88>
          <TR bgcolor=#FFFF88>
            <TD>User ID:</TD>
            <TD><html:text name="accountBean" property="username" /></TD>
          </TR>
          <TR bgcolor=#FFFF88>
            <TD>New password:</TD>
            <TD><html:password name="accountBean" property="password"/></TD>
          </TR>
          <TR bgcolor=#FFFF88>
            <TD>Repeat password:</TD>
            <TD>
              <html:password name="accountBean" property="repeatedPassword"/>
            </TD>
          </TR>
        </TABLE>

<%@include file="IncludeAccountFields.jsp"%>

  </TABLE>

  <BR>
  <CENTER>
    <html:image border="0" src="../images/button_submit.gif" />
  </CENTER>

</html:form>

<%@include file="../common/IncludeBottom.jsp"%>
