<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.sourceforge.net/tld/myfaces_ext_0_9.tld" prefix="x"%>

<f:loadBundle basename="org.apache.portals.applications.desktop.resources.Calendar" var="MESSAGE" />

<f:view>
<h:outputText  styleClass='portlet-header' value="#{MESSAGE['calendar.notes']}" />
 <h:outputText value="#{calendar.date}" />

<h:form id="calendarForm">
	<h:inputTextarea value="#{calendar.notes}" />
	<h:commandButton id="save" value="#{MESSAGE['add']}" action="#{calendar.save}"/>     
 	<h:commandButton id="cancel" value="#{MESSAGE['cancel']}" 
	                     action="returnFromNotes" immediate='true'>
	</h:commandButton>   
</h:form>     
     
</f:view>
