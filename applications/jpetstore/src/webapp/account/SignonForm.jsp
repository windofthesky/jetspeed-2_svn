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

<html:form action="/shop/signon" method="POST">

  <table align="center" border="0">
    <tr>
      <td colspan="2">Please enter your username and password.<br/>&nbsp;</td>
    </tr>
    <tr>
      <td>Username:</td>
      <td><input type="text" name="username" value="j2ee" /></td>
    </tr>
    <tr>
      <td>Password:</td>
      <td><input type="password" name="password" value="j2ee" /></td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>
        <html:image border="0" src="../images/button_submit.gif" />
      </td>
    </tr>
  </table>
</html:form>

<center>
  <html:link page="/shop/newAccountForm.shtml">
    <html:img border="0" src="../images/button_register_now.gif" />
  </html:link>
</center>

<%@include file="../common/IncludeBottom.jsp"%>
