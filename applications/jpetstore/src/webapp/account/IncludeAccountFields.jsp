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
<FONT color=darkgreen><H3>Account Information</H3></FONT>

<TABLE bgcolor="#008800" border=0 cellpadding=3 cellspacing=1 bgcolor=#FFFF88>
  <TR bgcolor=#FFFF88>
    <TD>First name:</TD>
    <TD><html:text name="accountBean" property="account.firstName" /></TD>
  </TR>
  <TR bgcolor=#FFFF88>
    <TD>Last name:</TD>
    <TD><html:text name="accountBean" property="account.lastName" /></TD>
  </TR>
  <TR bgcolor=#FFFF88>
    <TD>Email:</TD>
    <TD><html:text size="40" name="accountBean" property="account.email" /></TD>
  </TR>
  <TR bgcolor=#FFFF88>
    <TD>Phone:</TD>
    <TD><html:text name="accountBean" property="account.phone" /></TD>
  </TR>
  <TR bgcolor=#FFFF88>
    <TD>Address 1:</TD>
    <TD>
      <html:text size="40" name="accountBean" property="account.address1" />
    </TD>
  </TR>
  <TR bgcolor=#FFFF88>
    <TD>Address 2:</TD>
    <TD>
      <html:text size="40" name="accountBean" property="account.address2" />
    </TD>
  </TR>
  <TR bgcolor=#FFFF88>
    <TD>City: </TD>
    <TD><html:text name="accountBean" property="account.city" /></TD>
  </TR>
  <TR bgcolor=#FFFF88>
    <TD>State:</TD>
    <TD><html:text size="4" name="accountBean" property="account.state" /></TD>
  </TR>
  <TR bgcolor=#FFFF88>
    <TD>Zip:</TD>
    <TD><html:text size="10" name="accountBean" property="account.zip" /></TD>
  </TR>
  <TR bgcolor=#FFFF88>
    <TD>Country: </TD>
    <TD>
      <html:text size="15" name="accountBean" property="account.country" />
    </TD>
  </TR>
</TABLE>

<FONT color=darkgreen><H3>Profile Information</H3></FONT>

<TABLE bgcolor="#008800" border=0 cellpadding=3 cellspacing=1 >
  <TR bgcolor=#FFFF88>
    <TD>Language Preference:</TD>
    <TD>
      <html:select name="accountBean" property="account.languagePreference">
        <html:options name="accountBean" property="languages" />
      </html:select>
    </TD>
  </TR>
  <TR bgcolor=#FFFF88>
    <TD>Favourite Category:</TD>
    <TD>
      <html:select name="accountBean" property="account.favouriteCategoryId">
        <html:options name="accountBean" property="categories" />
      </html:select>
    </TD>
  </TR>
  <TR bgcolor=#FFFF88>
    <TD colspan=2>
      <html:checkbox name="accountBean"
                     property="accountListOption"/> Enable MyList
    </TD>
  </TR>
  <TR bgcolor=#FFFF88>
    <TD colspan=2>
      <html:checkbox name="accountBean"
                     property="accountBannerOption"/> Enable MyBanner
    </TD>
  </TR>
</TABLE>