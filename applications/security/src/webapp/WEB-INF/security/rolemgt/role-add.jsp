<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>

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

<h:outputText value="#{roleMgtMessages['currentParentRolePath']}" styleClass="PTitleContent" />
<f:verbatim>&nbsp;</f:verbatim>
<h:outputText value="#{roleActionForm.parentRole.fullPath}" />
<f:verbatim><br></f:verbatim>
<h:outputText value="#{roleMgtMessages['currentParentRoleName']}" styleClass="PTitleContent" />
<f:verbatim>&nbsp;</f:verbatim>
<h:outputText value="#{roleActionForm.parentRole.roleName}" />
<f:verbatim><br><br></f:verbatim>

<h:form id="addRoleForm" name="addRoleForm">

	<h:panelGrid columns="2" columnClasses="portlet-form-label, portlet-form-input-field">
                         
		<h:outputLabel for="roleName" value="#{roleMgtMessages['roleNameLabel']}"/>
		<h:panelGroup>
			<h:inputText id="roleName" value="#{roleActionForm.roleName}" required="true" />
		    <f:verbatim><br></f:verbatim>
		    <h:message for="roleName" styleClass="portlet-msg-error" showDetail="true" showSummary="false" />
		</h:panelGroup>

		<h:panelGroup>
		    <h:commandButton id="addRoleAction" action="none" value="#{roleMgtMessages['addRole']}" styleClass="portlet-form-button">
				<f:actionListener type="org.apache.jetspeed.portlets.security.rolemgt.RoleActionListener" />
		    </h:commandButton>
		</h:panelGroup>
		<h:panelGroup/>

	</h:panelGrid>
</h:form>


<f:verbatim><br></f:verbatim>