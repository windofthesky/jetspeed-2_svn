<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/tags/app"         prefix="app" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-portlet" prefix="sp" %>
<!--
<title><bean:message key="logon.title"/></title>
-->

<html:errors/>

<sp:form action="/SubmitLogon" focus="username"
         onsubmit="return validateLogonForm(this);">
<table border="0" width="100%">

  <tr>
    <th align="right">
      <bean:message key="prompt.username"/>:
    </th>
    <td align="left">
      <html:text property="username" size="16" maxlength="18"/>
    </td>
  </tr>

  <tr>
    <th align="right">
      <bean:message key="prompt.password" bundle="alternate"/>:
    </th>
    <td align="left">
      <html:password property="password" size="16" maxlength="18"
                    redisplay="false"/>
    </td>
  </tr>

  <tr>
    <td align="right">
      <html:submit property="Submit" value="Submit"/>
    </td>
    <td align="left">
      <html:reset/>
    </td>
  </tr>

</table>

</sp:form>

<html:javascript formName="LogonForm"
        dynamicJavascript="true"
         staticJavascript="false" cdata="false"/>
<sp:script src="/staticJavascript.jsp"/>

<jsp:include page="footer.jsp" />
