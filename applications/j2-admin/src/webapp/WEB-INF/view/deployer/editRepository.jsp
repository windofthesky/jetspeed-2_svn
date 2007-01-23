<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
  <h:form>

    <t:div>
      <h:commandButton value="Back to Repository List"
        action="#{deployer_editRepository.jumpDisplayRepositories}"/>
    </t:div>

    <h:messages styleClass="portlet-msg-success" 
      errorClass="portlet-msg-error" fatalClass="portlet-msg-error"
      warnClass="portlet-msg-alert" infoClass="portlet-msg-info" />

    <t:div rendered="false">
      <h:selectOneMenu value="#{deployer_editRepository.repositoryClassName}">
        <f:selectItems value="#{deployer_editRepository.repositoryClassNames}"/>
      </h:selectOneMenu>
    </t:div>

    <t:div>
      <h:panelGrid columns="2">
      
        <h:outputLabel for="name">
          <h:outputText value="Name:"/>
        </h:outputLabel>
        <h:inputText id="name" value="#{deployer_editRepository.name}"/>
      
        <h:outputLabel for="path">
          <h:outputText value="Configuration Path:"/>
        </h:outputLabel>
        <h:inputText id="path" value="#{deployer_editRepository.path}"/>

      </h:panelGrid>
    </t:div>

    <t:div>
      <h:commandButton action="#{deployer_editRepository.doCreateRepository}" value="Create"
        rendered="#{deployer_editRepository.newRepository}"/>
      <h:commandButton action="#{deployer_editRepository.doUpdateRepository}" value="Update"
        rendered="#{!deployer_editRepository.newRepository}"/>
    </t:div>
        
  </h:form>
</f:view>
