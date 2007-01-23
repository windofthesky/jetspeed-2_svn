<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
  <h:form>

    <t:div>
      <h:outputText value="Repository:"/>
      <h:selectOneMenu value="#{deployer_displayPortlets.repositoryName}">
        <f:selectItems value="#{deployer_displayPortlets.repositoryNames}"/>
      </h:selectOneMenu>
      <h:commandButton value="Select"
        action="#{deployer_displayPortlets.doSearch}"/>
      <h:commandButton value="Update Repository"
        action="#{deployer_displayPortlets.jumpUpdateRepository}" 
        rendered="false" immediate="true"/>
      <h:commandButton value="Refresh"
        action="#{deployer_displayPortlets.doRefresh}" 
        immediate="true"/>
    </t:div>

    <t:div rendered="false">
      <h:outputText value="Search:"/>
      <h:inputText value="#{deployer_displayPortlets.search}" />
      <h:commandButton value="Search"
        action="#{deployer_displayPortlets.doSearch}"/>
    </t:div>

    <h:messages styleClass="portlet-msg-success" 
      errorClass="portlet-msg-error" fatalClass="portlet-msg-error"
      warnClass="portlet-msg-alert" infoClass="portlet-msg-info" />


    <t:dataTable id="portletTable" var="portlet"
      value="#{deployer_displayPortlets.portlets}"
      headerClass="portlet-section-header"
      rowClasses="portlet-section-body,portlet-section-alternate"
      style="width:100%" rows="#{deployer_displayPortlets.pageSize}">
      <h:column>
        <f:facet name="header">
          <h:outputText value="Group ID" />
        </f:facet>
        <h:outputText value="#{portlet.groupId}" />
      </h:column>
      <h:column>
        <f:facet name="header">
          <h:outputText value="Artifact ID" />
        </f:facet>
        <h:outputText value="#{portlet.artifactId}" />
      </h:column>
      <h:column>
        <f:facet name="header">
          <h:outputText value="Name" />
        </f:facet>
        <h:outputText value="#{portlet.name}" />
      </h:column>
      <h:column>
        <f:facet name="header">
          <h:outputText value="Version" />
        </f:facet>
        <h:outputText value="#{portlet.version}" />
      </h:column>
      <h:column>
        <f:facet name="header">
          <h:outputText value="Type" />
        </f:facet>
        <h:outputText value="#{portlet.packaging}" />
      </h:column>
      <h:column>
        <f:facet name="header">
          <h:outputText value="Action" />
        </f:facet>
        <h:commandLink action="#{deployer_displayPortlets.doDeploy}"
          rendered="#{deployer_displayPortlets.deployable}">
          <h:outputText value="Deploy" />
        </h:commandLink>
        <h:outputText value="Deploy" rendered="#{!deployer_displayPortlets.deployable}"/>
      </h:column>
    </t:dataTable>
    <h:panelGrid columns="1" styleClass="portlet-section-footer"
      style="width:100%">
      <t:dataScroller for="portletTable" fastStep="10" pageCountVar="pageCount"
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
      <t:dataScroller for="portletTable" rowsCountVar="rowsCount"
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
