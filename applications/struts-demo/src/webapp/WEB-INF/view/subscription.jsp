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
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://portals.apache.org/bridges/struts/tags-portlet-html" prefix="html" %>
<h2>
<logic:equal name="SubscriptionForm" property="action"
            scope="request" value="Create">
  <bean:message key="subscription.title.create"/>
</logic:equal>
<logic:equal name="SubscriptionForm" property="action"
            scope="request" value="Delete">
  <bean:message key="subscription.title.delete"/>
</logic:equal>
<logic:equal name="SubscriptionForm" property="action"
            scope="request" value="Edit">
  <bean:message key="subscription.title.edit"/>
</logic:equal>
</h2>
<hr/>
<html:errors/>

<html:form action="/SaveSubscription" focus="host">
<html:hidden property="action"/>
<table border="0" width="100%">

  <tr>
    <th align="right">
      <bean:message key="prompt.username"/>:
    </th>
    <td align="left">
        <bean:write name="user" property="username" filter="true"/>
    </td>
  </tr>

  <tr>
    <th align="right">
      <bean:message key="prompt.mailHostname"/>:
    </th>
    <td align="left">
      <logic:equal name="SubscriptionForm" property="action"
                  scope="request" value="Create">
        <html:text property="host" size="50"/>
      </logic:equal>
      <logic:notEqual name="SubscriptionForm" property="action"
                     scope="request" value="Create">
        <html:hidden property="host" write="true"/>
      </logic:notEqual>
    </td>
  </tr>

  <tr>
    <th align="right">
      <bean:message key="prompt.mailUsername"/>:
    </th>
    <td align="left">
      <html:text property="username" size="50"/>
    </td>
  </tr>

  <tr>
    <th align="right">
      <bean:message key="prompt.mailPassword"/>:
    </th>
    <td align="left">
      <html:password property="password" size="50"/>
    </td>
  </tr>

  <tr>
    <th align="right">
      <bean:message key="prompt.mailServerType"/>:
    </th>
    <td align="left">
      <html:select property="type">
        <html:options collection="serverTypes" property="value"
                   labelProperty="label"/>
      </html:select>
    </td>
  </tr>

  <tr>
    <th align="right">
      <bean:message key="prompt.autoConnect"/>:
    </th>
    <td align="left">
      <html:checkbox property="autoConnect"/>
    </td>
  </tr>

  <tr>
    <td align="right">
      <logic:equal name="SubscriptionForm" property="action"
                  scope="request" value="Create">
        <html:submit>
          <bean:message key="button.save"/>
        </html:submit>
      </logic:equal>
      <logic:equal name="SubscriptionForm" property="action"
                  scope="request" value="Delete">
        <html:submit>
          <bean:message key="button.confirm"/>
        </html:submit>
      </logic:equal>
      <logic:equal name="SubscriptionForm" property="action"
                  scope="request" value="Edit">
        <html:submit>
          <bean:message key="button.save"/>
        </html:submit>
      </logic:equal>
    </td>
    <td align="left">
      <logic:notEqual name="SubscriptionForm" property="action"
                     scope="request" value="Delete">
        <html:reset>
          <bean:message key="button.reset"/>
        </html:reset>
      </logic:notEqual>
      &nbsp;
      <html:cancel>
        <bean:message key="button.cancel"/>
      </html:cancel>
    </td>
  </tr>

</table>

</html:form>

<jsp:include page="footer.jsp" />
