<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-portlet" prefix="sp" %>
<h2><bean:message key="change.title"/></h2>
<hr/>
<bean:message key="change.message"/>
<sp:link action="/Logon">
  <bean:message key="change.try"/>
</sp:link>
