<%--
Copyright 2004 The Apache Software Foundation
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

<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri='/WEB-INF/portlet.tld' prefix='portlet'%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/pam.tld" prefix="pam" %>

<%@ page import="org.apache.jetspeed.portlets.pam.beans.TabBean" %>

<fmt:setBundle basename="org.apache.jetspeed.portlets.site.resources.SiteResources" />

<!--
/*
 * Copyright 2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
//-->

<c:set var="selectedTab" value="${requestScope.selected_tab}"/>

<div id="tabs">
	<c:set var="tab_items" value="${requestScope.tabs}"/>
	<c:set var="currentTab" value="${selectedTab}"/>
	<c:set var="url_param_name" value="selected_tab"/>
	<%@ include file="tabs.jsp"%>
</div>

<c:if test="${currentTab.id == 'site_details'}">
	<c:choose>
		<c:when test="${folder != null}">
			Name (Node): <c:out value="${folder.name}"/> <br />
			Path (Node): <c:out value="${folder.path}"/> <br />
			Title (BaseElement): <c:out value="${folder.title}"/> <br />
			Parent (Node): <c:out value="${folder.parent.title}"/> <br />
			ID (BaseElement): <c:out value="${folder.id}"/> <br />
			Type (Node): <c:out value="${folder.type}"/> <br />
			URL (Node): <c:out value="${folder.url}"/> <br />
			ACL (SecuredResource): <c:out value="${folder.acl}"/> <br />
			Hidden (Node): <c:out value="${folder.hidden}"/> <br />
			Default Page (Folder): <c:out value="${folder.defaultPage}"/> <br />
			Default Theme (Folder): <c:out value="${folder.defaultTheme}"/> <br />
			
			
			<c:forEach var="field" items="${folder.metadataFields}">
				<c:out value="${field.name}"/> | <c:out value="${field.value}"/> | <c:out value="${field.locale}"/> <br />
			</c:forEach>
			
			
		</c:when>

		<c:when test="${page != null}">
		
			Name: <c:out value="${page.name}"/> <br />
			Title: <c:out value="${page.title}"/> <br />
			Parent: <c:out value="${page.parent.title}"/> <br />
			
		</c:when>
		
		<c:otherwise>
			<fmt:message key="site.details.choose_folder_or_page"/>
			
			
		</c:otherwise>
	</c:choose>
</c:if>
