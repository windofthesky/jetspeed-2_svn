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
	<c:forEach var="field" items="${md.fields}">
			
		<input type="checkbox" name="metadata_id" value="<c:out value="${field.id}"/>"/>
				
		<c:out value="${field.name}"/> | <c:out value="${field.value}"/> | <c:out value="${field.locale}"/> 
		<%--TODO:  value needs to escaped, or use textarea--%>
				
		<input type="text" name="<c:out value="${field.id}"/>:value" value="<c:out value="${field.value}"/>"/>
					
		<br />	
	</c:forEach>
			
	<input type="submit" value="Edit" onClick="this.form.portlet_action.value = '<c:out value="${action_prefix}"/>edit_metadata'"/>
	<input type="submit" value="Remove Selected" onClick="this.form.portlet_action.value = '<c:out value="${action_prefix}"/>remove_metadata'"/>
</form>
		
<form action="<c:out value="${edit_metadata_link}"/>">
	<input type="hidden" name="portlet_action" value="<c:out value="${action_prefix}"/>add_metadata"/>
			
	Name: <input type="text" name="name" value=""/> <br />
	Value: <input type="text" name="value" value=""/> <br />
	Locale: <input type="text" name="locale" value=""/> <br />
	<input type="submit" value="Add Metadata"/>
</form>