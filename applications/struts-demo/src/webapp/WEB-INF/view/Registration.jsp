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

  $Id$
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/tags/app"    prefix="app" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://portals.apache.org/bridges/struts/tags-portlet" prefix="sp" %>
<h2>
<logic:equal name="RegistrationForm" property="action"
            scope="request" value="Create">
  <bean:message key="registration.title.create"/>
</logic:equal>
<logic:equal name="RegistrationForm" property="action"
            scope="request" value="Edit">
  <bean:message key="registration.title.edit"/>
</logic:equal>
</h2>
<hr/>
<html:errors/>

<sp:form action="/SaveRegistration" focus="username"
         onsubmit="return validateRegistrationForm(this);">
<html:hidden property="action"/>
<table border="0" width="100%">

  <tr>
    <th align="right">
      <bean:message key="prompt.username"/>:
    </th>
    <td align="left">
      <logic:equal name="RegistrationForm" property="action"
                  scope="request" value="Create">
        <html:text property="username" size="16" maxlength="16"/>
      </logic:equal>
      <logic:equal name="RegistrationForm" property="action"
                  scope="request" value="Edit">
<%--
        <bean:write name="RegistrationForm" property="username"
                   scope="request" filter="true"/>
--%>
	<html:hidden property="username" write="true"/>
      </logic:equal>
    </td>
  </tr>

  <tr>
    <th align="right">
      <bean:message key="prompt.password"/>:
    </th>
    <td align="left">
      <html:password property="password" size="16" maxlength="16"/>
    </td>
  </tr>

  <tr>
    <th align="right">
      <bean:message key="prompt.password2"/>:
    </th>
    <td align="left">
      <html:password property="password2" size="16" maxlength="16"/>
    </td>
  </tr>

  <tr>
    <th align="right">
      <bean:message key="prompt.fullName"/>:
    </th>
    <td align="left">
      <html:text property="fullName" size="50"/>
    </td>
  </tr>

  <tr>
    <th align="right">
      <bean:message key="prompt.fromAddress"/>:
    </th>
    <td align="left">
      <html:text property="fromAddress" size="50"/>
    </td>
  </tr>

  <tr>
    <th align="right">
      <bean:message key="prompt.replyToAddress"/>:
    </th>
    <td align="left">
      <html:text property="replyToAddress" size="50"/>
    </td>
  </tr>

  <tr>
    <td align="right">
      <html:submit>
        <bean:message key="button.save"/>
      </html:submit>
    </td>
    <td align="left">
      <html:reset>
        <bean:message key="button.reset"/>
      </html:reset>
      &nbsp;
      <html:cancel>
        <bean:message key="button.cancel"/>
      </html:cancel>
    </td>
  </tr>

</table>
</sp:form>

<logic:equal name="RegistrationForm" property="action"
            scope="request" value="Edit">

<div align="center">
<h3><bean:message key="heading.subscriptions"/></h3>
</div>

<table border="1" width="100%">

  <tr>
    <th align="center" width="30%">
      <bean:message key="heading.host"/>
    </th>
    <th align="center" width="25%">
      <bean:message key="heading.user"/>
    </th>
    <th align="center" width="10%">
      <bean:message key="heading.type"/>
    </th>
    <th align="center" width="10%">
      <bean:message key="heading.autoConnect"/>
    </th>
    <th align="center" width="15%">
      <bean:message key="heading.action"/>
    </th>
  </tr>

<logic:iterate id="subscription" name="user" property="subscriptions">
  <tr>
    <td align="left">
      <bean:write name="subscription" property="host" filter="true"/>
    </td>
    <td align="left">
      <bean:write name="subscription" property="username" filter="true"/>
    </td>
    <td align="center">
      <bean:write name="subscription" property="type" filter="true"/>
    </td>
    <td align="center">
      <bean:write name="subscription" property="autoConnect"/>
    </td>
    <td align="center">
      <app:linkSubscription page="/EditSubscription.do?action=Delete">
        <bean:message key="registration.deleteSubscription"/>
      </app:linkSubscription>
      <app:linkSubscription page="/EditSubscription.do?action=Edit">
        <bean:message key="registration.editSubscription"/>
      </app:linkSubscription>
    </td>
  </tr>
</logic:iterate>

</table>

<sp:link action="/EditSubscription?action=Create" paramId="username"
 paramName="RegistrationForm" paramProperty="username">
  <bean:message key="registration.addSubscription"/>
</sp:link>


</logic:equal>

<html:javascript formName="RegistrationForm"
        dynamicJavascript="true"
         staticJavascript="false" cdata="false"/>

<sp:script src="/staticJavascript.jsp"/>

<jsp:include page="footer.jsp" />
