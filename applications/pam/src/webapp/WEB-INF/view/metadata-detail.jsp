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
<form name="Edit_Metatdata_Form" action="<c:out value="${edit_metadata_link}"/>">
	<input type="hidden" name="portlet_action" value=""/>
	<table>
		<tr>
			<th>&nbsp;</th>
			<th><fmt:message key="pam.details.name"/></th>
			<th><fmt:message key="pam.details.locale"/></th>
			<th><fmt:message key="pam.details.value"/></th>
		</tr>
	<c:forEach var="field" items="${md.fields}">
		<tr>
			<td>
				<input type="checkbox" name="metadata_id" value="<c:out value="${field.id}"/>"/>
			</td>
			<td>	
				<c:out value="${field.name}"/>
			</td>
			<td align="center">
				<c:out value="${field.locale}"/> 
				
			</td>
			<td>
				<%--TODO:  value needs to escaped, or use textarea--%>
				<input type="text" name="<c:out value="${field.id}"/>:value" value="<c:out value="${field.value}"/>" size="50"/>
			</td>
		</tr>
	</c:forEach>
	</table>
			
	<input type="submit" value="<fmt:message key="pam.details.edit"/>" onClick="this.form.portlet_action.value = '<c:out value="${action_prefix}"/>edit_metadata'"/>
	<input type="submit" value="<fmt:message key="pam.details.remove"/>" onClick="this.form.portlet_action.value = '<c:out value="${action_prefix}"/>remove_metadata'"/>
</form>
		
<form action="<c:out value="${edit_metadata_link}"/>">
	<input type="hidden" name="portlet_action" value="<c:out value="${action_prefix}"/>add_metadata"/>
	<div>
		<table>
			<tr>
				<td>
					<span class="portlet-form-label"><fmt:message key="pam.details.name"/></span>
				</td>
				<td>
					<input type="text" name="name" value=""/>
				</td>
			</tr>
			<tr>
				<td>
					<span class="portlet-form-label"><fmt:message key="pam.details.value"/></span>
				</td>
				<td>
					<input type="text" name="value" value=""/>
				</td>
			</tr>
			<tr>
				<td>
					<span class="portlet-form-label"><fmt:message key="pam.details.locale"/></span>
				</td>
				<td>
					<input type="text" name="locale" value=""/>
				</td>
			</tr>
		</table>
	</div>
	<input type="submit" value="<fmt:message key="pam.details.add_metadata"/>"/>
</form>