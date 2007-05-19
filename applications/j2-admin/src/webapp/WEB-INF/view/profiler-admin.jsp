<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>

<!--
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

<f:loadBundle basename="org.apache.jetspeed.portlets.profiler.resources.ProfilerResources" var="MESSAGE" />

<f:view>
<h:form id="extentForm">
<h:panelGrid id='pg00' columns='2'>
<h:panelGroup id='pgg00'>

<h:dataTable
	id='dt00'
    value="#{rules.extent}"
    var="mRule"
    styleClass="portlet-menu"
    headerClass="portlet-menu-caption"
>
    <h:column>
       <f:facet name="header">
          <h:outputText  styleClass='portlet-menu-item' value="#{MESSAGE['profiler.title.rules']}" />          
       </f:facet>
       <x:commandLink actionListener="#{rule.listen}" immediate="true" >
            <h:outputText value="#{mRule.id}" />
            <f:param name='selectedRule' value="#{mRule.id}"/>
       </x:commandLink>
    </h:column>
</h:dataTable>
</h:panelGroup>
<h:panelGroup id='pgg01' rendered="#{rule != null && rule.id != '{empty}'}">
	
<h:panelGrid id='pg01' columns="3">
<h:outputLabel id='lxid' for="xid" value="#{MESSAGE['profiler.rule.id']}: "/>
<h:inputText readonly='#{rule.updating}' id="xid" styleClass='portlet-form-label' value="#{rule.id}" size='80'/>
<h:message id='mxid' for="xid" styleClass="portlet-msg-error" showDetail="true" showSummary="false" />
<h:outputLabel id='ltitle' for="title" value="#{MESSAGE['profiler.rule.title']}: "/>
<h:inputText id="title" styleClass='portlet-form-label' value="#{rule.title}"  size='100'/>
<h:message id='mtitle' for="title" styleClass="portlet-msg-error" showDetail="true" showSummary="false" />

<h:outputLabel id='lclassname' for="classname" value="#{MESSAGE['profiler.rule.class']}: "/>
<h:selectOneMenu id="classname" styleClass='portlet-form-label' value="#{rule.classname}">
	<f:selectItems value="#{rule.classnames}"/>
</h:selectOneMenu>
<h:message id='mclassname' for="classname" styleClass="portlet-msg-error" showDetail="true" showSummary="false" />

</h:panelGrid>

<!-- criteria -->
	<h:dataTable
		id='dt01'
	    value="#{rule.criteria}"
	    var="mCriterion"
	    styleClass="portlet-menu"
	    headerClass="portlet-menu-caption"	    
	>
	    <h:column>
	       <f:facet name="header">
	          <h:outputText styleClass="portlet-font" value="#{MESSAGE['criterion.name']}" />          
	       </f:facet>
	       <x:commandLink action='gotoCriterionForm' actionListener="#{criterion.listen}" immediate="true" >
	            <h:outputText value="#{mCriterion.name}" />
	            <f:param name='selectedCriterion' value="#{mCriterion.name}"/>
	            <f:param name='selectedRule' value="#{rule.id}"/>	            
	       </x:commandLink>
	    </h:column>
	     <h:column>      
	       <f:facet name="header">
	          <h:outputText styleClass="portlet-font" value="#{MESSAGE['criterion.value']}" />          
	       </f:facet>
	        <h:outputText value="#{mCriterion.value}" />       
	    </h:column>
	     <h:column>      
	       <f:facet name="header">
	          <h:outputText styleClass="portlet-font" value="#{MESSAGE['criterion.resolver']}" />          
	       </f:facet>
	        <h:outputText value="#{mCriterion.type}" />       
	    </h:column>
	     <h:column>      
	       <f:facet name="header">
	          <h:outputText styleClass="portlet-font" value="#{MESSAGE['criterion.fallback.order']}" />          
	       </f:facet>
	        <h:outputText value="#{mCriterion.fallbackOrder}" />       
	    </h:column>
	    
	</h:dataTable>
	    <h:commandLink id="addCriterion" value="#{MESSAGE['criteria.new']}"  immediate='true'
	                     action="gotoCriterionForm" actionListener="#{criterion.listen}">
	    	<f:param name='selectedRule' value="#{rule.id}" />	            	    
	    </h:commandLink>
<h:panelGrid id='cmdButtons' columns='3'>
		<h:commandButton id="saveProfile" value="#{MESSAGE['profiler.rule.save']}" action="#{rule.saveProfile}"/>
		<h:commandButton id="newProfile" value="#{MESSAGE['profiler.rule.new']}" action="#{rule.createNewProfile}"/>
		<h:commandButton id="removeProfile" value="#{MESSAGE['profiler.rule.remove']}" action="#{rule.removeProfile}"/>
</h:panelGrid>

</h:panelGroup>
</h:panelGrid>
</h:form>
</f:view>
