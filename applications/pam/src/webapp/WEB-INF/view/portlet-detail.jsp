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

<c:if test="${selectedPortletTab.id == 'pd_details'}">
	<table>
		<tr>
			<td>
				<fmt:message key="pam.details.expiration_cache"/>
			</td>
			<td>
				<c:out value="${selectedPDef.expirationCache}"/>
			</td>
		</tr>
		</tr>
			<td>
				<fmt:message key="pam.details.id"/>
			</td>
			<td>
				<c:out value="${selectedPDef.portletIdentifier}"/>
			</td>
		</tr>
		</tr>
			<td>
				<fmt:message key="pam.details.unique_name"/>
			</td>
			<td>
				<c:out value="${selectedPDef.uniqueName}"/>
			</td>
		</tr>
		</tr>
			<td>
				<fmt:message key="pam.details.preference_validator"/>
			</td>
			<td>
				<c:out value="${selectedPDef.preferenceValidatorClassname}"/>
			</td>
		</tr>
		</tr>
			<td>
				<fmt:message key="pam.details.class_name"/>
			</td>
			<td>
				<c:out value="${selectedPDef.className}"/>
			</td>
		</tr>
	</table>
</c:if>

<c:if test="${selectedPortletTab.id == 'pd_metadata'}">
	<div id="portlet_metadata">
			
		<c:set var="md" value="${selectedPDef.metadata}"/>
		<c:set var="action_prefix" value="portlet."/>
		<portlet:actionURL var="edit_metadata_link" >			
		</portlet:actionURL>
		
		<%@ include file="metadata-detail.jsp" %>
	</div>
</c:if>

<c:if test="${selectedPortletTab.id == 'pd_preferences'}">
	<c:set var="prefSet" value="${selectedPDef.preferenceSet}"/>
	<%
		PreferenceSetComposite comp = (PreferenceSetComposite)pageContext.findAttribute("prefSet");
		Iterator prefIter = comp.iterator();
		pageContext.setAttribute("prefIter", prefIter);
	%>
	
	<portlet:actionURL var="edit_preferenece_link" >
    </portlet:actionURL>
	
	<form action="<c:out value="${edit_preferenece_link}"/>">
		<input type="hidden" name="portlet_action" value=""/>
		<table border="1">
			<tr>
				<th>&nbsp;</th>
				<th><fmt:message key="pam.details.name"/></th>
				<th><fmt:message key="pam.details.value"/></th>
			</tr>
			<tr>
				<td colspan="3">&nbsp;</td>
			</td>
		<c:forEach var="pref" items="${prefIter}">
			<tr>
				<td>
					<input type="checkbox" name="pref_remove_id" value="<c:out value="${pref.name}"/>"/>
				</td>
				<td>
					<c:out value="${pref.name}"/>
					<input type="hidden" name="pref_edit_id" value="<c:out value="${pref.name}"/>"/>
				</td>
				<td>
					<table>
					<c:forEach var="value" items="${pref.values}" varStatus="status">
						<tr>
							<td>
								<input type="text" name="<c:out value="${pref.name}"/>:<c:out value="${status.index}"/>" value="<c:out value="${value}"/>"/>
							</td>
						</tr>
					</c:forEach>
					</table>
				</tr>
			</tr>
		</c:forEach>
		</table>
		
		<input type="submit" value="<fmt:message key="pam.details.edit"/>" onClick="this.form.portlet_action.value = 'portlet.edit_preference'"/>
		<input type="submit" value="<fmt:message key="pam.details.remove"/>" onClick="this.form.portlet_action.value = 'portlet.remove_preference'"/>
	</form>
	
	<hr />
	
	
	<form action="<c:out value="${edit_preferenece_link}"/>">
		<input type="hidden" name="portlet_action" value="portlet.add_preference"/>
		<table>
			<tr>
				<td>
					<fmt:message key="pam.details.name"/>
				</td>
				<td>
					<input type="text" name="name"/>
				</td>
				<%--TODO add combo box of existing keys--%>
			</tr>
			<tr>
				<td>
					<fmt:message key="pam.details.value"/>
				</td>
				<td>
					<input type="text" name="value"/>
				</td>
			</tr>
			<%--
			<tr>
				<td>
					<fmt:message key="pam.details.type"/>
				</td>
				<td>
					<select>
						<option value="string">String</option>
						<option value="int">Int</option>
					</select>
				</td>
			</tr>
			--%>
		</table>
		<input type="submit" value="<fmt:message key="pam.details.add_preference"/>"/>
	</form>
</c:if>

<c:if test="${selectedPortletTab.id == 'pd_languages'}">
	<c:set var="langSet" value="${selectedPDef.languageSet}"/>
	
	<portlet:actionURL var="edit_language_link" >
    </portlet:actionURL>
	
	<form action="<c:out value="${edit_language_link}"/>">
		<input type="hidden" name="portlet_action" value=""/>
	<table border="1">
		<tr>
			<th>&nbsp;</th>
			<th><fmt:message key="pam.details.title"/></th>
			<th><fmt:message key="pam.details.short_title"/></th>
			<th><fmt:message key="pam.details.keyword"/></th>
			<th><fmt:message key="pam.details.locale"/></th>
		</tr>
	<c:forEach var="lang" items="${langSet.innerCollection}" varStatus="status">
		<tr>
			<td>
				<input type="checkbox" name="language_remove_id" value="<c:out value="${status.index}"/>"/>
				<input type="hidden" name="language_edit_id" value="<c:out value="${status.index}"/>"/>
			</td>
			<td>
				<input type="text" name="title:<c:out value="${status.index}"/>" value="<c:out value="${lang.title}"/>"/>
			</td>
			<td>
				<input type="text" name="short_title:<c:out value="${status.index}"/>" value="<c:out value="${lang.shortTitle}"/>"/>
			</td>
			<td>
				<table>
				<c:forEach var="keyword" items="${lang.keywords}" varStatus="keywordStatus">
					<tr>
						<td>
							<input type="text" name="keyword:<c:out value="${status.index}"/>:<c:out value="${keywordStatus.index}"/>" value="<c:out value="${keyword}"/>"/>
						</td>
					</tr>
				</c:forEach>
				</table>
				<%--
				<input type="text" name="keyword:<c:out value="${status.index}"/>" value="<c:forEach var="keyword" items="${lang.keywords}" varStatus="keywordStatus"><c:out value="${keyword}"/>,</c:forEach>"/>
				--%>
			</td>
			<td>
				<c:out value="${lang.locale}"/>
			</td>
		</tr>
	</c:forEach>
	</table>
	
		<input type="submit" value="<fmt:message key="pam.details.edit"/>" onClick="this.form.portlet_action.value = 'portlet.edit_language'"/>
		<input type="submit" value="<fmt:message key="pam.details.remove"/>" onClick="this.form.portlet_action.value = 'portlet.remove_language'"/>
	</form>
	
	<form action="<c:out value="${edit_language_link}"/>">
		<input type="hidden" name="portlet_action" value="portlet.add_language"/>
		
		<table>
			<tr>
				<td>
					<fmt:message key="pam.details.title"/>
				</td>
				<td>
					<input type="text" name="title" value=""/>
				</td>
			</tr>
			<tr>
				<td>
					<fmt:message key="pam.details.short_title"/>
				</td>
				<td>
					<input type="text" name="short_title" value=""/>
				</td>
			</tr>
			<tr>
				<td>
					<fmt:message key="pam.details.keyword"/>
				</td>
				<td>
					<input type="text" name="keyword" value=""/>
				</td>
			</tr>
			<tr>
				<td>
					<fmt:message key="pam.details.locale"/>
				</td>
				<td>
					<input type="text" name="locale" value=""/>
				</td>
			</tr>
		</table>
		
		<input type="submit" value="<fmt:message key="pam.details.add_language"/>"/>
	</form>
</c:if>

<c:if test="${selectedPortletTab.id == 'pd_parameters'}">
	<c:set var="paramSet" value="${selectedPDef.initParameterSet}"/>
	
	<%--
		TODO:  if needed, place iterator into page context
		see prefs section
	--%>
	<c:forEach var="theparam" items="${paramSet.innerCollection}">
		<c:out value="${theparam.name}"/> | <c:out value="${theparam.value}"/> <br />
	</c:forEach>
</c:if>
<c:if test="${selectedPortletTab.id == 'pd_security'}">
	<c:set var="roleSet" value="${selectedPDef.initSecurityRoleRefSet}"/>
	<c:forEach var="therole" items="${roleSet.innerCollection}">
		<c:out value="${therole.roleName}"/>  <br />
	</c:forEach>
</c:if>

<c:if test="${selectedPortletTab.id == 'pd_content_type'}">
	<c:set var="contentTypeSet" value="${selectedPDef.contentTypeSet}"/>
	<c:forEach var="contentType" items="${contentTypeSet.innerCollection}">
		<c:out value="${contentType.contentType}"/> | 
		
		<c:forEach var="mode" items="${contentType.portletModes}">
			<c:out value="${mode}"/>, 
		</c:forEach>
		
		<br />
	</c:forEach>
</c:if>
