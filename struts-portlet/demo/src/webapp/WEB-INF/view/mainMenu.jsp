<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/tags/app" prefix="app" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-portlet" prefix="sp" %>
<h2><bean:message key="mainMenu.title"/></h2>
<hr/>
<h3><bean:message key="mainMenu.heading"/> <bean:write name="user" property="fullName" /></h3>
<ul>
<li><sp:link action="/EditRegistration?action=Edit"><bean:message key="mainMenu.registration"/></sp:link></li>
<li><sp:link forward="logoff"><bean:message key="mainMenu.logoff"/></sp:link></li>
</ul>
