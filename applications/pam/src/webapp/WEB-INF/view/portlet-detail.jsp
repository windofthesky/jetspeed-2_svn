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
<c:set var="portlet_tabs" value="${requestScope.portlet_tabs}"/>

<c:set var="selectedPortletTab" value="${requestScope.selected_portlet_tab}"/>

<span class="portlet-section-header">Selected Portlet</span>
<c:out value="${selectedPDef.name}"/>
<div id="portlet_tabs">

	<c:set var="tab_items" value="${portlet_tabs}"/>
	<c:set var="currentTab" value="${selectedPortletTab}"/>
	<c:set var="url_param_name" value="selected_portlet_tab"/>
	<%@ include file="tabs.jsp"%>
</div>

<br />

<c:if test="${selectedPortletTab.id == 'Details'}">
	Expiration Cache: <c:out value="${selectedPDef.expirationCache}"/> <br />
	Portlet ID: <c:out value="${selectedPDef.portletIdentifier}"/> <br />
	Unique Name: <c:out value="${selectedPDef.uniqueName}"/> <br />
	PreferenceValidator: <c:out value="${selectedPDef.preferenceValidatorClassname}"/> <br />
	ClassName: <c:out value="${selectedPDef.className}"/> <br />
	
</c:if>

<c:if test="${selectedPortletTab.id == 'Metadata'}">
	<div id="portlet_metadata">
			
		<c:set var="md" value="${selectedPDef.metadata}"/>
		<c:set var="action_prefix" value="portlet."/>
		<portlet:actionURL var="edit_metadata_link" >			
		</portlet:actionURL>
		
		<%@ include file="metadata-detail.jsp" %>
	</div>
</c:if>

<c:if test="${selectedPortletTab.id == 'Preferences'}">
	<c:set var="prefSet" value="${selectedPDef.preferenceSet}"/>
	<%
		PreferenceSetComposite comp = (PreferenceSetComposite)pageContext.findAttribute("prefSet");
		Iterator prefIter = comp.iterator();
		pageContext.setAttribute("prefIter", prefIter);
	%>
	<c:forEach var="pref" items="${prefIter}">
		<c:out value="${pref.name}"/> | 
		<c:forEach var="value" items="${pref.values}">
			<c:out value="${value}"/>, 
		</c:forEach>
		<br />
	</c:forEach>
</c:if>

<c:if test="${selectedPortletTab.id == 'Languages'}">
	<c:set var="langSet" value="${selectedPDef.languageSet}"/>
	<c:forEach var="lang" items="${langSet.innerCollection}">
		<c:out value="${lang.title}"/> | <c:out value="${lang.shortTitle}"/> | 
		<c:forEach var="keyword" items="${lang.keywords}">
			<c:out value="${keyword}"/> , 
		</c:forEach>
		| <c:out value="${lang.locale}"/> <br />
	</c:forEach>
</c:if>

<c:if test="${selectedPortletTab.id == 'Parameters'}">
	<c:set var="paramSet" value="${selectedPDef.initParameterSet}"/>
	
	<%--
		TODO:  if needed, place iterator into page context
		see prefs section
	--%>
	<c:forEach var="theparam" items="${paramSet.innerCollection}">
		<c:out value="${theparam.name}"/> | <c:out value="${theparam.value}"/> <br />
	</c:forEach>
</c:if>
<c:if test="${selectedPortletTab.id == 'Security'}">
	<c:set var="roleSet" value="${selectedPDef.initSecurityRoleRefSet}"/>
	<c:forEach var="therole" items="${roleSet.innerCollection}">
		<c:out value="${therole.roleName}"/>  <br />
	</c:forEach>
</c:if>

<c:if test="${selectedPortletTab.id == 'ContentType'}">
	<c:set var="contentTypeSet" value="${selectedPDef.contentTypeSet}"/>
	<c:forEach var="contentType" items="${contentTypeSet.innerCollection}">
		<c:out value="${contentType.contentType}"/> | 
		
		<c:forEach var="mode" items="${contentType.portletModes}">
			<c:out value="${mode}"/>, 
		</c:forEach>
		
		<br />
	</c:forEach>
</c:if>
