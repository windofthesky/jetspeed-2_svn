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
<%@ taglib uri="/tags/app" prefix="app" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://portals.apache.org/bridges/struts/tags-portlet" prefix="sp" %>
<h2><bean:message key="mainMenu.title"/></h2>
<hr/>
<h3><bean:message key="mainMenu.heading"/> <bean:write name="user" property="fullName" /></h3>
<ul>
<li><sp:link action="/EditRegistration?action=Edit"><bean:message key="mainMenu.registration"/></sp:link></li>
<li><sp:link forward="logoff"><bean:message key="mainMenu.logoff"/></sp:link></li>
</ul>
