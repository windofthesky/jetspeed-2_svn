<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.sourceforge.net/tld/myfaces_ext_0_9.tld" prefix="x"%>


<f:loadBundle basename="org.apache.portals.applications.desktop.resources.Calendar" var="MESSAGE" />

<f:view>

<h:form id="calendarForm">
<x:inputCalendar 
    value="#{calendar.date}"
    monthYearRowClass="portlet-section-header" 
    weekRowClass="portlet-section-subheader"
    dayCellClass="portlet-menu-item"
    currentDayCellClass="portlet-menu-item-selected" 
     />     
<h:outputText value="#{calendar.date}" />
<h:commandButton id="edit" value="#{MESSAGE['calendar.notes']}" action="#{calendar.selectDate}"/>

</h:form>     
     
</f:view>
