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
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://portals.apache.org/bridges/struts/tags-portlet" prefix="sp" %>
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

