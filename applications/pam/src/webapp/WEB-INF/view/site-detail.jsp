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

<c:choose>
	<c:when test="${folder != null}">
		Name: <c:out value="${folder.name}"/> <br />
		Title: <c:out value="${folder.title}"/> <br />
		Parent: <c:out value="${folder.parent.title}"/> <br />
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