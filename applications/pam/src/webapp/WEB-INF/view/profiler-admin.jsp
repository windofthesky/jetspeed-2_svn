<%@ page import="net.sourceforge.myfaces.custom.tree.DefaultMutableTreeNode,
                 net.sourceforge.myfaces.custom.tree.model.DefaultTreeModel"%>
<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.sourceforge.net/tld/myfaces_ext_0_9.tld" prefix="x"%>

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

<link href='css/security-admin.css' type='text/css'/>

<f:view>
<h:panelGrid columns='2'>
<h:panelGroup>
<h:dataTable
    value="#{rules.extent}"
    var="erule"
    styleClass="portlet-section-body"
    headerClass="portlet-form-button"
    rowClasses="portlet-menu-item-selected,portlet-MintyBlue"
>
    <h:column>
       <f:facet name="header">
          <h:outputText value="Rule Id" />
       </f:facet>
       <x:commandLink actionListener="#{rule.listen}" immediate="true" >
            <h:outputText value="#{erule.id}" />
            <f:param name='selectedRule' value="#{erule.id}"/>
       </x:commandLink>
    </h:column>
    
    <h:column>
       <f:facet name="header">
         <h:outputText value="Description"
            style="font-weight: bold"/>
       </f:facet>
        <h:outputText value='#{erule.title}'/>
    </h:column>
    <h:column>
        <h:selectBooleanCheckbox value="false"/>
    </h:column>
</h:dataTable>
</h:panelGroup>
<h:panelGroup>
    <h:form id="ruleForm" name="ruleForm">
        <h:panelGrid columns="2" >
            <f:facet name="header">
                <h:outputText id="cfH" value="Edit Rule"/>
            </f:facet>
            <f:facet name="footer">
                <h:outputText value="End"/>
            </f:facet>

            <h:outputLabel for="title" value="Title"/>
            <h:panelGroup>
                <h:inputText id="title" value="#{rule.title}" required="true" />
                <h:message for="title" styleClass="error" showDetail="true" showSummary="false" />
            </h:panelGroup>

        </h:panelGrid>
    </h:form>

</h:panelGroup>
</h:panelGrid>
</f:view>