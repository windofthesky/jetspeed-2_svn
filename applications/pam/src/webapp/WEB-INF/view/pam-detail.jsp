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
<c:set var="selectedPDef" value="${requestScope.portletDefinition}"/>

app.name = <c:out value="${name}"/><br />
app.version = <c:out value="${version}"/> <br />

<%--
app.description = <c:out value="${pa.description}"/> <br />

<c:choose>
	<c:when test="${pa.applicationType == '0'}">
		app.type = WEBAPP <br />
	</c:when>
	<c:when test="${pa.applicationType == '1'}">
		app.type = LOCAL <br />
	</c:when>
</c:choose>

app.id = <c:out value="${pa.applicationIdentifier}"/>
--%>

<div id="tabs">
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
</div>

<%--Beginning of Portlets tab data--%>
<%--TODO:  switch to c:choose --%>
<c:if test="${selectedTab.id == 'Portlets'}">
  <div id="portlets">
	
	<portlet:actionURL var="select_portlet_link" >
        <%--<portlet:param name="select_portlet" value="<%= pdefName %>" />--%>
    </portlet:actionURL>
	<form action="<c:out value="${select_portlet_link}"/>">
		<select name="select_portlet">
		
			<option value="" <c:if test="! ${selectedPDef}"> selected="true"</c:if>
			>Please Choose Portlet</option>
		
		<c:forEach var="portletDef" items="${pa.portletDefinitions}">
		    <c:set var="pdefName" value="${portletDef.name}"/>
		    
		    <%--We must do this since portlet taglib doesn't support expressions--%>
		    <% String pdefName = (String) pageContext.getAttribute("pdefName"); %>
		    
		    <option value="<c:out value="${portletDef.name}"/>" <c:if test="${selectedPDef.name == portletDef.name}">selected="true"</c:if>>
			  <c:out value="${portletDef.name}"/>
		    </option>
			<%--
		    <a href="<c:out value="${select_portlet_link}"/>">
		        <c:out value="${portletDef.name}" /><br />
		    </a>
		    --%>
		</c:forEach>
		</select>
		
		<input type="submit" value="Select"/>
    </form>
  </div>
    
  <div id="selectedPortlet" class="">
    
    <span class="portlet-section-header">Selected Portlet</span>
	<c:out value="${selectedPDef.name}"/>
   
	<%--
    <%@ include file="portlet-detail.jsp" %>
    --%>
    
  </div>
</c:if>
<%--End of Portlets tab data--%>

<%--Beginning of UserAttr tab data--%>
<%--TODO:  switch to c:choose --%>
<c:if test="${selectedTab.id == 'UserAttr'}">
  <div id="Details">
	<portlet:actionURL var="edit_user_attr_link" >
	</portlet:actionURL>
		
	<form name="Edit_UserAttr_Form" action="<c:out value="${edit_user_attr_link}"/>">
		<input type="hidden" name="portlet_action" value="edit_user_attribute"/>
		<c:forEach var="userAttr" items="${pa.userAttributes}">
			<%--<input type="hidden" name="user_attr_name" value="<c:out value="${userAttr.name}"/>"/>--%>
			
			<input type="checkbox" name="user_attr_id" value="<c:out value="${userAttr.name}"/>"/>
			<c:out value="${userAttr.name}"/> | 
			<input type="text" name="<c:out value="${userAttr.name}"/>:description" value="<c:out value="${userAttr.description}"/>"/> <br />
		</c:forEach>
		
		<input type="submit" value="Edit" onClick="this.form.portlet_action.value = 'edit_user_attribute'"/>
		<input type="submit" value="Remove Selected" onClick="this.form.portlet_action.value = 'remove_user_attribute'"/>
	</form>
	
	<form action="<c:out value="${edit_user_attr_link}"/>">
			<input type="hidden" name="portlet_action" value="add_user_attribute"/>
			
			Name: <input type="text" name="user_attr_name" value=""/> <br />
			Description: <input type="text" name="user_attr_desc" value=""/> <br />
			<input type="submit" value="Add User Attr"/>
		</form>
  </div>
</c:if>
<%--End of UserAttr tab data--%>


<%--Beginning of Metadata tab data--%>
<%--TODO:  switch to c:choose --%>
<c:if test="${selectedTab.id == 'Metadata'}">
	<div id="metadata">
		<script type="text/javascript">
			
		</script>
	
	
		<c:set var="md" value="${pa.metadata}"/>
		
		<%
			//pageContext.getAttribute("md").getClass().getName()
		%>
		
		<portlet:actionURL var="edit_metadata_link" >
			
		</portlet:actionURL>
		
		<form name="Edit_Metatdata_Form" action="<c:out value="${edit_metadata_link}"/>">
		<input type="hidden" name="portlet_action" value="edit_metadata"/>
		<c:forEach var="field" items="${md.fields}">
		
			<input type="checkbox" name="metadata_id" value="<c:out value="${field.id}"/>"/>
			
			<c:out value="${field.name}"/> | <c:out value="${field.value}"/> | <c:out value="${field.locale}"/> 
			<%--TODO:  value needs to escaped, or use textarea--%>
			
			<input type="text" name="<c:out value="${field.id}"/>:value" value="<c:out value="${field.value}"/>"/>
				
			<br />	
		</c:forEach>
		
		<input type="submit" value="Edit" onClick="this.form.portlet_action.value = 'edit_metadata'"/>
		<input type="submit" value="Remove Selected" onClick="this.form.portlet_action.value = 'remove_metadata'"/>
		</form>
		
		<form action="<c:out value="${edit_metadata_link}"/>">
			<input type="hidden" name="portlet_action" value="add_metadata"/>
			
			Name: <input type="text" name="name" value=""/> <br />
			Value: <input type="text" name="value" value=""/> <br />
			Locale: <input type="text" name="locale" value=""/> <br />
			<input type="submit" value="Add Metadata"/>
		</form>
	</div>
</c:if>
<%--End of Metadata tab data--%>

<%--Beginning of Details tab data--%>
<%--TODO:  switch to c:choose --%>
<c:if test="${selectedTab.id == 'Details'}">
	<div id="details">
		app.name = <c:out value="${name}"/><br />
		app.version = <c:out value="${version}"/> <br />
		app.description = <c:out value="${pa.description}"/> <br />

		<c:choose>
			<c:when test="${pa.applicationType == '0'}">
				app.type = WEBAPP <br />
			</c:when>
			<c:when test="${pa.applicationType == '1'}">
				app.type = LOCAL <br />
			</c:when>
		</c:choose>

		app.id = <c:out value="${pa.applicationIdentifier}"/>
	
	
		<%--Name | AppId | Id <br />--%>
		<hr />
		Jetspeed Services
		<hr />
		<c:forEach var="service" items="${pa.jetspeedServices}">
			<c:out value="${service.name}"/> <br /> <%--| <c:out value="${service.appId}"/> | <c:out value="${service.id}"/><br />--%>
		</c:forEach>
	</div>
</c:if>
<%--End of Details tab data--%>

<br />
<br />


