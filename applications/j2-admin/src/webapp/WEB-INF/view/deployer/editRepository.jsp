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
