<%--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
--%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
  <h:form>

    <t:div>
      <h:commandButton value="Add Repository"
        action="#{deployer_displayRepositories.doAddRepository}"/>
      <h:commandButton value="Reload Repositories"
        action="#{deployer_displayRepositories.doReloadRepositories}"/>
      <h:commandButton value="Deploy Portlet"
        action="#{deployer_displayRepositories.jumpDisplayPortlets}" 
        rendered="false" immediate="true"/>
    </t:div>

    <h:messages styleClass="portlet-msg-success" 
      errorClass="portlet-msg-error" fatalClass="portlet-msg-error"
      warnClass="portlet-msg-alert" infoClass="portlet-msg-info" />

    <t:dataTable id="repositoryTable" var="repository"
      value="#{deployer_displayRepositories.repositories}"
      headerClass="portlet-section-header"
      rowClasses="portlet-section-body,portlet-section-alternate"
      style="width:100%" rows="#{deployer_displayRepositories.pageSize}">
      <h:column>
        <f:facet name="header">
          <h:outputText value="Name" />
        </f:facet>
        <h:outputText value="#{repository.name}" />
      </h:column>
      <h:column>
        <f:facet name="header">
          <h:outputText value="State" />
        </f:facet>
        <h:outputText value="Active" rendered="#{repository.available}"/>
        <h:outputText value="Inactive" rendered="#{!repository.available}"/>
      </h:column>
      <h:column>
        <f:facet name="header">
          <h:outputText value="Action" />
        </f:facet>
        <h:panelGrid columns="2">
          <h:commandLink action="#{deployer_displayRepositories.doEditRepository}">
            <h:outputText value="Edit" />
          </h:commandLink>
          <h:commandLink action="#{deployer_displayRepositories.doDeleteRepository}">
            <h:outputText value="Delete" />
          </h:commandLink>
        </h:panelGrid>
      </h:column>
    </t:dataTable>
    <h:panelGrid columns="1" styleClass="portlet-section-footer"
      style="width:100%">
      <t:dataScroller for="repositoryTable" fastStep="10" pageCountVar="pageCount"
        pageIndexVar="pageIndex" paginator="true" paginatorMaxPages="9">
        <f:facet name="first">
          <t:outputText value="First" rendered="#{pageIndex>1}"/>
        </f:facet>
        <f:facet name="last">
          <t:outputText value="Last" rendered="#{pageIndex<pageCount}"/>
        </f:facet>
        <f:facet name="previous">
          <t:outputText value="Previous" rendered="#{pageIndex>1}"/>
        </f:facet>
        <f:facet name="next">
          <t:outputText value="Next" rendered="#{pageIndex<pageCount}"/>
        </f:facet>
      </t:dataScroller>
      <t:dataScroller for="repositoryTable" rowsCountVar="rowsCount"
        displayedRowsCountVar="displayedRowsCountVar"
        firstRowIndexVar="firstRowIndex" lastRowIndexVar="lastRowIndex"
        pageCountVar="pageCount" immediate="true" pageIndexVar="pageIndex">
        <h:outputFormat value="{0} found ({4} / {5})" rendered="#{rowsCount!=0}">
          <f:param value="#{rowsCount}" />
          <f:param value="#{displayedRowsCountVar}" />
          <f:param value="#{firstRowIndex}" />
          <f:param value="#{lastRowIndex}" />
          <f:param value="#{pageIndex}" />
          <f:param value="#{pageCount}" />
        </h:outputFormat>
        <h:outputText value="No portlet."
          rendered="#{rowsCount==0}" />
      </t:dataScroller>
    </h:panelGrid>
        
  </h:form>
</f:view>
