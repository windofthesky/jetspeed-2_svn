<%--
Copyright 2004 The Apache Software Foundation
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
--%>
<%@ page language="java" import="javax.portlet.*, java.util.*, org.apache.jetspeed.portlets.pam.PortletApplicationBean, org.apache.jetspeed.portlets.pam.beans.TabBean, org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite" session="true" %>
<%@ taglib uri='/WEB-INF/portlet.tld' prefix='portlet'%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>


<portlet:defineObjects/>
<h2>Portlet Application Detail</h2>

<c:set var="pa" value="${requestScope.portletApplication}" />
<c:set var="name" value="${pa.name}" />
<c:set var="version" value="${pa.version}" />

<c:set var="tabs" value="${requestScope.tabs}"/>
<c:set var="selectedTab" value="${requestScope.selected_tab}"/>

app.name = <c:out value="${name}"/><br />
app.version = <c:out value="${version}"/>

<p>TODO: Details</p>

<div id="portlets">

	<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<tr>
		<c:forEach var="tab" items="${tabs}">
			<td <c:if test="${tab == selectedTab}"> class="LTabLeft" </c:if>
			    <c:if test="${tab != selectedTab}"> class="LTabLeftLow" </c:if>
			     style="font-size:1pt;" nowrap="true">
			     &nbsp;
			</td>
	        	<td <c:if test="${tab == selectedTab}"> class="LTab" </c:if>
	                <c:if test="${tab != selectedTab}"> class="LTabLow" </c:if>
                     align="center" valign="middle" nowrap="true">
                     
                     <% String tabid = ((TabBean)pageContext.getAttribute("tab")).getId(); %>
                     <portlet:actionURL var="select_tab_link" >
                         <portlet:param name="selected_tab" value="<%= tabid %>" />
                     </portlet:actionURL>
                     
                    <a href="<c:out value="${select_tab_link}"/>">
                        <c:out value="${tab.description}"/>
                    </a>
                </td>
                <td <c:if test="${tab == selectedTab}"> class="LTabRight" </c:if>
                    <c:if test="${tab != selectedTab}"> class="LTabRightLow" </c:if> 
                    style="font-size:1pt;" nowrap="true">
                    &nbsp;
                </td>
            </c:forEach>
        </tr>
        </table>


    <c:forEach var="portletDef" items="${pa.portletDefinitions}">
        <c:set var="pdefName" value="${portletDef.name}"/>
        
        <%--We must do this since portlet taglib doesn't support expressions--%>
        <% String pdefName = (String) pageContext.getAttribute("pdefName"); %>
        <portlet:actionURL var="select_portlet_link" >
            <portlet:param name="select_portlet" value="<%= pdefName %>" />
        </portlet:actionURL>
        
    
        <a href="<c:out value="${select_portlet_link}"/>">
            <c:out value="${portletDef.name}" /><br />
        </a>
    </c:forEach>
</div>

<br />
<br />

<div id="selectedPortlet" class="">
    <span class="portlet-section-header">Selected Portlet</span>
    <c:set var="pdef" value="${requestScope.portletDefinition}"/>
    <c:out value="${pdef.name}"/>
</div>
