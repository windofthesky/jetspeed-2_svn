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

<f:view>

	<f:loadBundle basename="org.apache.jetspeed.portlets.security.resources.RoleMgtResources" var="roleMgtMessages"/>

	<h:panelGroup>

	<x:panelTabbedPane styleClass="tabs"
			activeTabStyleClass="activeTabs"
			inactiveTabStyleClass="inactiveTabs"
			width="400">
			
		<x:tabChangeListener type="org.apache.jetspeed.portlets.security.rolemgt.PanelTabStateListener" />	
    	
    	<x:panelTab id="viewRolesTab" label="#{roleMgtMessages['viewRoles']}" rendered="#{roleMgtPanelTabState.renderViewRoles}">
  			<jsp:include page="roles-view.jsp" />      
	    </x:panelTab>
	    
		<x:panelTab id="editRoleTab" label="#{roleMgtMessages['viewRoles']}" rendered="#{roleMgtPanelTabState.renderEditRole}">		
			<h:form id="selection">
    			<h:selectOneMenu id="seloneMenuPanel" value="#{stackState.selected}" styleClass="selectOneMenu" onchange="document.forms['selection'].submit();">
		        	<f:selectItem itemValue="addUserToRole" itemLabel="#{roleMgtMessages['addUserToRole']}" />
        		    <f:selectItem itemValue="addGroupToRole" itemLabel="#{roleMgtMessages['addGroupToRole']}" />
		        </h:selectOneMenu>
		    </h:form>

		    <x:panelStack id="stack" selectedPanel="#{stackState.selected}">
    			<h:panelGroup id="addUserToRole">
		        	<jsp:include page="role-add-user.jsp" />   
        		</h:panelGroup>
				<h:panelGroup id="addGroupToRole">
					<jsp:include page="role-add-group.jsp" />  
		        </h:panelGroup>
			</x:panelStack>
	    </x:panelTab>
	    
    	<x:panelTab id="addRoleTab" label="#{roleMgtMessages['addRole']}" rendered="true">
	    	<jsp:include page="role-add.jsp" />
   		</x:panelTab>
	</x:panelTabbedPane>

	</h:panelGroup>

</f:view>