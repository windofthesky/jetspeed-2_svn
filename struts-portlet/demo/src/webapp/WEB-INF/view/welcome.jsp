<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-portlet" prefix="sp" %>
<h2><bean:message key="index.title"/></h2>
<hr/>
<h3><bean:message key="index.heading"/></h3>
<ul>
<li><sp:link action="/EditRegistration?action=Create"><bean:message key="index.registration"/></sp:link></li>
<li><sp:link action="/Logon"><bean:message key="index.logon"/></sp:link></li>
</ul>

<h3>Language Options</h3>
<ul>
<li><sp:link action="/Locale?language=en">English</sp:link></li>
<li><sp:link action="/Locale?language=ja" useLocalEncoding="true">Japanese</sp:link></li>
<li><sp:link action="/Locale?language=ru" useLocalEncoding="true">Russian</sp:link></li>
</ul>

<hr />

<p><html:img bundle="alternate" pageKey="struts.logo.path" altKey="struts.logo.alt"/></p>

<p><sp:link action="/Tour"><bean:message key="index.tour"/></sp:link></p>

