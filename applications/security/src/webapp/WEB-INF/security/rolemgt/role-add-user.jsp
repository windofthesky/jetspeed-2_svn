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

<h:outputText value="#{roleMgtMessages['currentRolePath']}" styleClass="PTitleContent" />
<f:verbatim>&nbsp;</f:verbatim>
<h:outputText value="#{roleActionForm.rolePath}" />
<f:verbatim><br></f:verbatim>
<h:outputText value="#{roleMgtMessages['currentRoleName']}" styleClass="PTitleContent" />
<f:verbatim>&nbsp;</f:verbatim>
<h:outputText value="#{roleActionForm.roleName}" />
<f:verbatim><br><br></f:verbatim>

<h:panelGrid columns="3" styleClass="portlet-scroller-grid" rowClasses="portlet-scroller-grid">
	<h:panelGroup>
		
		<h:outputText value="#{roleMgtMessages['availableUsersHeader']}" styleClass="PTitleContent" />
		<f:verbatim><br></f:verbatim>
		
		<x:dataTable id="availableUsersTable"
                     headerClass="portlet-table-header"
                     footerClass="portlet-table-footer"
                     rowClasses="portlet-table-row1, portlet-table-row2"
                     columnClasses="portlet-table-col1, portlet-table-col2"
                     var="availableUser"
                     value="#{availableUsersList.availableUsers}"
                     preserveDataModel="true"
                     rows="10"
                   >
                   
            <h:column>
            	<f:facet name="header">
                	<h:outputText value="#{roleMgtMessages['selectHeader']}" />
                </f:facet>
                <h:outputText value="X" />
            </h:column>
            
        	<h:column>
            	<f:facet name="header">
                	<h:outputText value="#{roleMgtMessages['username']}" />
                </f:facet>
                <h:outputText value="#{availableUser.username}" />
            </h:column>

        </x:dataTable>
		<x:dataScroller id="availableUsersScroller"
						for="availableUsersTable"
                        fastStep="5"
                        pageCountVar="availableUserPageCount"
                        pageIndexVar="availableUserPageIndex"
                        styleClass="portlet-scroller"
                        paginator="true"
                        paginatorMaxPages="5"
                        paginatorTableClass="portlet-paginator"
                        paginatorActiveColumnStyle="font-weight:bold;"
                  	>
        	<% /* f:facet name="previous">
            	<h:graphicImage url="images/icon_previous.gif" border="0" />
            </f:facet>
            <f:facet name="next">
            	<h:graphicImage url="images/icon_next.gif" border="0" />
            </f:facet */ %>
     	</x:dataScroller>
        <x:dataScroller id="availableUsersScrollerPages"
                        for="availableUsersTable"
                        pageCountVar="availableUserPageCount"
                        pageIndexVar="availableUserPageIndex"
                 	>
        	<h:outputFormat value="#{roleMgtMessages['scrollerPages']}">
            	<f:param value="#{availableUserPageIndex}" />
                <f:param value="#{availableUserPageCount}" />
            </h:outputFormat>
 		</x:dataScroller>
 		
 	</h:panelGroup>
 	
 	<h:panelGroup>
 		<f:verbatim>Rigth<br></f:verbatim>
 		<f:verbatim>Left<br></f:verbatim>
 	</h:panelGroup>
 	
 	<h:panelGroup>

		<h:outputText value="#{roleMgtMessages['selectedUsersHeader']}" styleClass="PTitleContent" />
		<f:verbatim><br></f:verbatim>

		<x:dataTable id="selectedUsersTable"
                     headerClass="portlet-table-header"
                     footerClass="portlet-table-footer"
                     rowClasses="portlet-table-row1, portlet-table-row2"
                     columnClasses="portlet-table-col1, portlet-table-col2"
                     var="selectedUser"
                     value="#{selectedUsersList.selectedUsers}"
                     preserveDataModel="true"
                     rows="10"
                   >
                   
        	<h:column>
            	<f:facet name="header">
                	<h:outputText value="#{roleMgtMessages['selectHeader']}" />
                </f:facet>
                <h:outputText value="X" />
            </h:column>
        	
        	<h:column>
            	<f:facet name="header">
                	<h:outputText value="#{roleMgtMessages['username']}" />
                </f:facet>
                <h:outputText value="#{selectedUser.username}" />
            </h:column>

        </x:dataTable>
		<x:dataScroller id="selectedUsersScroller"
						for="selectedUsersTable"
                        fastStep="5"
                        pageCountVar="selectedUserPageCount"
                        pageIndexVar="selectedUserPageIndex"
                        styleClass="portlet-scroller"
                        paginator="true"
                        paginatorMaxPages="5"
                        paginatorTableClass="portlet-paginator"
                        paginatorActiveColumnStyle="font-weight:bold;"
                  	>
        	<% /* f:facet name="previous">
            	<h:graphicImage url="images/icon_previous.gif" border="0" />
            </f:facet>
            <f:facet name="next">
            	<h:graphicImage url="images/icon_next.gif" border="0" />
            </f:facet */ %>
     	</x:dataScroller>
        <x:dataScroller id="selectedUsersScrollerPages"
                        for="selectedUsersTable"
                        pageCountVar="selectedUserPageCount"
                        pageIndexVar="selectedUserPageIndex"
                 	>
        	<h:outputFormat value="#{roleMgtMessages['scrollerPages']}">
            	<f:param value="#{selectedUserPageIndex}" />
                <f:param value="#{selectedUserPageCount}" />
            </h:outputFormat>
 		</x:dataScroller>
 	

 	</h:panelGroup>
 
 </h:panelGrid>