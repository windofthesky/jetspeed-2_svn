<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="x"%>

<%
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
%>

<h:outputText value="#{roleMgtMessages['viewRoleInfo']}" styleClass="portlet-msg-info" />
<f:verbatim><br></f:verbatim>
<x:messages id="roleSelectManyCheckboxMsgList" layout="table" styleClass="portlet-msg-error" showDetail="true" summaryFormat="{0} " />
<f:verbatim><br></f:verbatim>

<h:form name="roleSelectManyCheckboxForm">
<x:selectManyCheckbox id="roleSelectManyCheckbox" value="#{roleActionForm.selectedRoles}" layout="spread" />

<x:tree id="tree" value="#{roleTreeTable.treeModel}"
   		var="roleTreeItem"
   		styleClass="tree"
   	    nodeClass="tree-node"
   	    headerClass="portlet-table-header"
   	    footerClass="portlet-table-footer"
        rowClasses="portlet-table-body, portlet-table-alternate"
        columnClasses="portlet-table-col1, portlet-table-col2"
        selectedNodeClass="tree-node-selected"
        expandRoot="true">
   
	<h:column>
		<f:facet name="header">
			<h:outputText value="#{roleMgtMessages['selectHeader']}" />
        </f:facet>
        <x:treeCheckbox for="roleSelectManyCheckbox" itemValue="#{roleTreeItem.fullPath}" />
	</h:column>
	<x:treeColumn>
		<f:facet name="header">
			<h:outputText value="#{roleMgtMessages['roleHierarchy']}" />
		</f:facet>
		<h:commandLink action="none">
        	<h:outputText value="#{roleTreeItem.roleName}" />
        	<f:param name="addRoleToParam" value="#{roleTreeItem.fullPath}"/>
        	<f:actionListener type="org.apache.jetspeed.portlets.security.rolemgt.AddRoleCommandLinkActionListener" />
        </h:commandLink>
	</x:treeColumn>
	<f:facet name="footer">
		<h:panelGroup>
			<h:form name="roleMgt">
				<h:commandButton id="editRoleAction" action="none" value="#{roleMgtMessages['editRole']}">
					<f:actionListener type="org.apache.jetspeed.portlets.security.rolemgt.RoleActionListener" />
				</h:commandButton>
				<f:verbatim>&nbsp;</f:verbatim>
				<h:commandButton id="removeRoleCmd" value="#{roleMgtMessages['removeRole']}"/>
			</h:form>
		</h:panelGroup>
	</f:facet>
</x:tree>

</h:form>
<f:verbatim><br></f:verbatim>
        