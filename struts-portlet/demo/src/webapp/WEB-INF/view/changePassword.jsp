<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-portlet" prefix="sp" %>
<h2><bean:message key="change.title"/></h2>
<hr/>
<bean:message key="change.message"/>
<sp:link action="/Logon">
  <bean:message key="change.try"/>
</sp:link>
