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
<br>
<logic:present name="accountBean" scope="session">
  <logic:equal name="accountBean" property="authenticated" value="true">
    <logic:equal name="accountBean" property="account.bannerOption"
                 value="true">
      <table align="center" background="<html:rewrite href="../images/bkg-topbar.gif"/>"
             cellpadding="5" width="100%">
        <tr>
          <td>
            <center>
              <html-el:img src="${accountBean.account.bannerImagePath}"/>
              &nbsp;
            </center>
          </td>
        </tr>
      </table>
    </logic:equal>
  </logic:equal>
</logic:present>
