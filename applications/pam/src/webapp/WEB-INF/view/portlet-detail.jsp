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
				<td>
					<fmt:message key="pam.details.title.description"/>
				</td>
			</tr>
			<tr>
				<td>
					<fmt:message key="pam.details.short_title"/>
				</td>
				<td>
					<input type="text" name="short_title" value=""/>
				</td>
				<td>
					<fmt:message key="pam.details.short_title.description"/>
				</td>
			</tr>
			<tr>
				<td>
					<fmt:message key="pam.details.keyword"/>
				</td>
				<td>
					<input type="text" name="keyword" value=""/>
				</td>
				<td>
					<fmt:message key="pam.details.keyword.description"/>
				</td>
			</tr>
			<tr>
				<td>
					<fmt:message key="pam.details.locale"/>
				</td>
				<td>
					<input type="text" name="locale" value=""/>
				</td>
				<td>
					<fmt:message key="pam.details.locale.description"/>
				</td>
			</tr>
		</table>
		
		<input type="submit" value="<fmt:message key="pam.details.add_language"/>"/>
	</form>
</c:if>

<c:if test="${selectedPortletTab.id == 'pd_parameters'}">
	<c:set var="paramSet" value="${selectedPDef.initParameterSet}"/>
	
	<portlet:actionURL var="edit_parameter_link" >
    </portlet:actionURL>
	
	<%--
		TODO:  if needed, place iterator into page context
		see prefs section
	--%>
	
	<form action="<c:out value="${edit_parameter_link}"/>">
		<input type="hidden" name="portlet_action" value=""/>
	
	<table>
	<c:forEach var="theparam" items="${paramSet.innerCollection}">
		<tr>
			<td>
				<input type="checkbox" name="parameter_remove_id" value="<c:out value="${theparam.name}"/>" />
			</td>
			<td>
				<input type="hidden" name="parameter_edit_id" value="<c:out value="${theparam.name}"/>" />
				<c:out value="${theparam.name}"/>
			</td>
			<td>
				<input type="text" name="<c:out value="${theparam.name}"/>:value" value="<c:out value="${theparam.value}"/>"/>
			</td>
			<%--
				TODO handle descriptions
			--%>
		</tr>
	</c:forEach>
	</table>
	
		<input type="submit" value="<fmt:message key="pam.details.edit"/>" onClick="this.form.portlet_action.value = 'portlet.edit_parameter'"/>
		<input type="submit" value="<fmt:message key="pam.details.remove"/>" onClick="this.form.portlet_action.value = 'portlet.remove_parameter'"/>
	
	</form>
	
	<form action="<c:out value="${edit_parameter_link}"/>">
		<input type="hidden" name="portlet_action" value="portlet.add_parameter"/>
		<table>
			<tr>
				<td>
					<fmt:message key="pam.details.name"/>
				</td>
				<td>
					<input type="text" name="name" value=""/>
				</td>
				<td>
					<fmt:message key="pam.details.name.description"/>
				</td>
			</tr>
			<tr>
				<td>
					<fmt:message key="pam.details.value"/>
				</td>
				<td>
					<input type="text" name="value" value=""/>
				</td>
				<td>
					<fmt:message key="pam.details.value.description"/>
				</td>
			</tr>
			<tr>
				<td>
					<fmt:message key="pam.details.description"/>
				</td>
				<td>
					<input type="text" name="description" value=""/>
				</td>
				<td>
					<fmt:message key="pam.details.description.description"/>
				</td>
			</tr>
			<tr>
				<td>
					<fmt:message key="pam.details.locale"/>
				</td>
				<td>
					<input type="text" name="locale" value=""/>
				</td>
				<td>
					<fmt:message key="pam.details.locale.description"/>
				</td>
			</tr>
		</table>
		<input type="submit" value="<fmt:message key="pam.details.add_parameter"/>"/>
	</form>
	
</c:if>
<c:if test="${selectedPortletTab.id == 'pd_security'}">
	<c:set var="roleSet" value="${selectedPDef.initSecurityRoleRefSet}"/>
	
	<portlet:actionURL var="edit_security_link" >
    </portlet:actionURL>

	<c:if test="${! empty roleSet.innerCollection}">
	<form action="<c:out value="${edit_security_link}"/>">
		<input type="hidden" name="portlet_action" value=""/>	
	
		<table border="1">
			<tr>
				<th>&nbsp;</th>
				<th><fmt:message key="pam.details.role_name"/></th>
				<th><fmt:message key="pam.details.role_link"/></th>
			</tr>
		<c:forEach var="therole" items="${roleSet.innerCollection}">
			<tr>
				<td>
					<input type="checkbox" name="security_remove_id" value="<c:out value="${therole.roleName}"/>"/>
				</td>
				<td>
					<input type="hidden" name="security_edit_id" value="<c:out value="${therole.roleName}"/>"/>
					<input type="text" name="<c:out value="${therole.roleName}"/>:name" value="<c:out value="${therole.roleName}"/>"/>
				</td>
				<td>
					<input type="text" name="<c:out value="${therole.roleName}"/>:link" value="<c:out value="${therole.roleLink}"/>"/>
				</td>
			</tr>
		</c:forEach>
		</table>
	
		<input type="submit" value="<fmt:message key="pam.details.edit"/>" onClick="this.form.portlet_action.value = 'portlet.edit_security'"/>
		<input type="submit" value="<fmt:message key="pam.details.remove"/>" onClick="this.form.portlet_action.value = 'portlet.remove_security'"/>
	
	</form>
	</c:if>

	<form action="<c:out value="${edit_security_link}"/>">
		<input type="hidden" name="portlet_action" value="portlet.add_security"/>
		<table>
			<tr>
				<td>
					<fmt:message key="pam.details.role_name"/>
				</td>
				<td>
					<input type="text" name="name" value=""/>
				</td>
				<td>
					<fmt:message key="pam.details.role_name.description"/>
				</td>
			</tr>
			<tr>
				<td>
					<fmt:message key="pam.details.role_link"/>
				</td>
				<td>
					<input type="text" name="link" value=""/>
				</td>
				<td>
					<fmt:message key="pam.details.role_link.description"/>
				</td>
			</tr>
		</table>
		<input type="submit" value="<fmt:message key="pam.details.add_security"/>"/>
	</form>

</c:if>

<c:if test="${selectedPortletTab.id == 'pd_content_type'}">
	<c:set var="contentTypeSet" value="${selectedPDef.contentTypeSet}"/>
	
	<portlet:actionURL var="edit_content_type_link" >
    </portlet:actionURL>

	<c:if test="${! empty contentTypeSet.innerCollection}">
	<form action="<c:out value="${edit_content_type_link}"/>">
		<input type="hidden" name="portlet_action" value=""/>
		<table border="1">
			<tr>
				<th>&nbsp;</th>
				<th><fmt:message key="pam.details.content_type"/></th>
				<td><fmt:message key="pam.details.modes"/></th>
			</tr>
		<c:forEach var="contentType" items="${contentTypeSet.innerCollection}">
			<tr>
				<td>
					<input type="checkbox" name="content_type_remove_id" value="<c:out value="${contentType.contentType}"/>"/>
				</td>
				<td>
					
					<c:out value="${contentType.contentType}"/>
				</td>
				<td>			
					<c:forEach var="mode" items="${contentType.portletModes}">
						<c:out value="${mode}"/>, 
					</c:forEach>
				</td>			
			</tr>
		</c:forEach>
		</table>
		
		<input type="submit" value="<fmt:message key="pam.details.edit"/>" onClick="this.form.portlet_action.value = 'portlet.edit_content_type'"/>
		<input type="submit" value="<fmt:message key="pam.details.remove"/>" onClick="this.form.portlet_action.value = 'portlet.remove_content_type'"/>
	</form>
	</c:if>
	
	<form action="<c:out value="${edit_content_type_link}"/>">
		<input type="hidden" name="portlet_action" value="portlet.add_content_type"/>
		<table>
			<tr>
				<td>
					<fmt:message key="pam.details.content_type"/>
				</td>
				<td>
					<%--TODO:  this could be a select box--%>
					<input type="text" name="content_type" value=""/>
				</td>
				<td>
					<fmt:message key="pam.details.content_type.description"/>
				</td>
			</tr>
			<tr>
				<td>
					<fmt:message key="pam.details.portlet_mode"/>
				</td>
				<td>
					<select name="mode" multiple="true">
						<option value="view"><fmt:message key="pam.details.porltet_mode.view"/></option>
						<option value="edit"><fmt:message key="pam.details.porltet_mode.edit"/></option>
						<option value="help"><fmt:message key="pam.details.porltet_mode.help"/></option>
					</select>
				</td>
				<td>
					<fmt:message key="pam.details.portlet_mode.description"/>
				</td>
			</tr>
			<tr>
				<td>
					<fmt:message key="pam.details.custom_mode"/>
				</td>
				<td>
					<%--TODO:  this could be a select box--%>
					<input type="text" name="custom_modes" value=""/>
				</td>
				<td>
					<fmt:message key="pam.details.custom_modes.description"/>
				</td>
			</tr>
		</table>
		<input type="submit" value="<fmt:message key="pam.details.add_content_type"/>"/>
	</form>
</c:if>
