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
<%@ page language="java" session="true" %>
<%@ page import="javax.portlet.*" %>
<%@ page import="java.util.*" %>
<%@ page import="org.apache.jetspeed.portlets.pam.beans.TabBean" %>
<%@ page import="org.apache.jetspeed.portlets.security.users.JetspeedUserBean" %>
<%@ page import="org.apache.jetspeed.om.common.preference.*" %>
<%@ page import="org.apache.jetspeed.om.common.*" %>

<%@ taglib uri='/WEB-INF/portlet.tld' prefix='portlet'%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.apache.jetspeed.portlets.security.resources.SecurityResources" />


<portlet:defineObjects/>

<c:set var="user" value="${requestScope.user}" />

<%--Beginning of User check --%>
<c:if test="${user != null}">

<c:set var="tabs" value="${requestScope.tabs}"/>
<c:set var="selectedTab" value="${requestScope.selected_tab}"/>

<br/>
<div class='portlet-section-header'>
<fmt:message key="user.principal.name"/> :
<span style='font-size:11pt; text-transform:uppercase'>
<c:out value="${user.principal}"/>
</span>
</div>
<br/>

<div id="tabs">
	<c:set var="tab_items" value="${tabs}"/>
	<c:set var="currentTab" value="${selectedTab}"/>
	<c:set var="url_param_name" value="selected_tab"/>
	<%@ include file="tabs.jsp"%>
</div>


<%--Beginning of User Attributes tab data--%>
<%--TODO:  switch to c:choose --%>
<c:if test="${currentTab.id == 'user_attributes'}">
  <div id="attributes">	
  <portlet:actionURL var="edit_user_attr_link" />
  
	<form name="Edit_UserAttr_Form" action="<c:out value="${edit_user_attr_link}"/>" method="post">
		<input type="hidden" name="portlet_action" value="security_user.edit_user_attribute"/>
		
		<table>
			<tr>
				<th>&nbsp;</th>
				<th><fmt:message key="security.name"/></th>
				<th><fmt:message key="security.value"/></th>
			</tr>
		<c:forEach var="userAttr" items="${user.attributes}">
			<tr>
			<%--<input type="hidden" name="user_attr_name" value="<c:out value="${userAttr.name}"/>"/>--%>
			
				<td>
					<input type="checkbox" name="user_attr_id" value="<c:out value="${userAttr.name}"/>"/>
				</td>
				<td>
					<c:out value="${userAttr.name}"/>
				</td>
				<td>
					<input type="text" name="<c:out value="${userAttr.name}"/>:value" value="<c:out value="${userAttr.value}"/>"/>
				</td>
			</tr>
		</c:forEach>
		</table>
		<input type="submit" value="<fmt:message key="security.update"/>" onClick="this.form.portlet_action.value = 'security_user.update_user_attribute'"/>
		<input type="submit" value="<fmt:message key="security.remove"/>" onClick="this.form.portlet_action.value = 'security_user.remove_user_attribute'"/>
	</form>
	<form name="Add_UserAttr_Form" action="<c:out value="${edit_user_attr_link}"/>" method="post">
		<input type="hidden" name="portlet_action" value="security_user.add_user_attribute"/>
		
		<table>
			<tr>
				<td>
					<fmt:message key="security.name"/>
				</td>
				<td>
					<input type="text" name="user_attr_name" value=""/>
				</td>
			</tr>
			<tr>
				<td>
					<fmt:message key="security.value"/>
				</td>
				<td>
					<input type="text" name="user_attr_value" value=""/>
				</td>
			</tr>
		</table>
		<input type="submit" value="<fmt:message key="security.add"/>"/>
	</form>
  </div>	
</c:if>
<%--End of User Attributes tab data--%>

<%--Beginning Security Credential tab data--%>
<%--TODO:  switch to c:choose --%>
<c:if test="${selectedTab.id == 'user_credential'}">
  <div id="Credential">
  <portlet:actionURL var="edit_credential_link" />
  
  <form name="Edit_Credential_Form" action="<c:out value="${edit_credential_link}"/>" method="post">
    <input type="hidden" name="portlet_action" value="security_user.update_user_credential"/>    
    <table>
      <tr>
        <td>
          <fmt:message key="security.credential.value"/>
        </td>
        <td>
          <input type="password" name="user_cred_value" value=""/>
        </td>
        <td>
          &nbsp;
        </td>
        <td>
          <input type="hidden" name="user_cred_updreq" value="<c:out value="${credential.updateRequired}"/>"/>
          <input type="checkbox" 
                 <c:if test="${credential.updateRequired}">checked</c:if>
                 onclick="if(this.checked) user_cred_updreq.value='true';else user_cred_updreq.value='false';"
          />
        </td>
        <td>
          <fmt:message key="security.credential.update.required"/>
        </td>
      </tr>
      <tr>
        <td>
          <fmt:message key="security.credential.last.logon"/>
        </td>
        <td>
          <fmt:formatDate value="${credential.lastLogonDate}" type="both" dateStyle="short" timeStyle="long"/>
        </td>
        <td>
          &nbsp;
        </td>
        <td>
          <input type="hidden" name="user_cred_enabled" value="<c:out value="${credential.enabled}"/>"/>
          <input type="checkbox" 
                 <c:if test="${credential.enabled}">checked</c:if>
                 onclick="if(this.checked) user_cred_enabled.value='true';else user_cred_enabled.value='false';"
          />
        </td>
        <td>
          <fmt:message key="security.enabled"/>
        </td>
      </tr>
      <tr>
        <td>
          <fmt:message key="security.credential.expires"/>
        </td>
        <td>
          <fmt:formatDate value="${credential.expirationDate}" type="both" dateStyle="short" timeStyle="long"/>
        </td>
        <td>
          &nbsp;
        </td>
        <td>
          <input type="checkbox" disabled <c:if test="${credential.expired}">checked</c:if>/>
        </td>
        <td>
          <fmt:message key="security.expired"/>
        </td>
      </tr>
    </table>
    <input type="submit" value="<fmt:message key="security.update"/>"/>
  </form>
    
  </div>  
</c:if>
<%--End of Security Credential tab data--%>

<%--Beginning Security Role tab data--%>
<%--TODO:  switch to c:choose --%>
<c:if test="${selectedTab.id == 'user_role'}">
  <div id="Role">
  <portlet:actionURL var="edit_role_link" />
  
	<form name="Edit_Role_Form" action="<c:out value="${edit_role_link}"/>" method="post">
		<input type="hidden" name="portlet_action" value="security_user.edit_role"/>		
		<table>
			<tr>
				<th>&nbsp;</th>
				<th><fmt:message key="security.rolename"/></th>
			</tr>
		<c:forEach var="role" items="${roles}">
			<tr>			
				<td>
					<input type="checkbox" name="user_role_id" value="<c:out value="${role.principal.name}"/>"/>
				</td>
				<td>
					<c:out value="${role.principal.name}"/>
				</td>
			</tr>
		</c:forEach>
		</table>
		<input type="submit" value="<fmt:message key="security.remove"/>" onClick="this.form.portlet_action.value = 'security_user.remove_user_role'"/>
    </form>
	<form name="Add_Role_Form" action="<c:out value="${edit_role_link}"/>" method="post">
		<input type="hidden" name="portlet_action" value="security_user.add_user_role"/>
		
		<table>
			<tr>
				<td>
					<fmt:message key="security.rolename"/>
				</td>
				<td>
					<input type="text" name="role_name" value=""/>
				</td>
			</tr>
		</table>
		<input type="submit" value="<fmt:message key="security.add"/>"/>
	</form>
    
  </div>  
</c:if>
<%--End of Security Role tab data--%>

<%--Beginning Security Group tab data--%>
<%--TODO:  switch to c:choose --%>
<c:if test="${selectedTab.id == 'user_group'}">
  <div id="Group">
  <portlet:actionURL var="edit_group_link" />
  
	<form name="Edit_Group_Form" action="<c:out value="${edit_group_link}"/>" method="post">
		<input type="hidden" name="portlet_action" value="security_user.edit_group"/>		
		<table>
			<tr>
				<th>&nbsp;</th>
				<th><fmt:message key="security.groupname"/></th>
			</tr>
		<c:forEach var="group" items="${groups}">
			<tr>			
				<td>
					<input type="checkbox" name="user_group_id" value="<c:out value="${group.principal.name}"/>"/>
				</td>
				<td>
					<c:out value="${group.principal.name}"/>
				</td>
			</tr>
		</c:forEach>
		</table>
		<input type="submit" value="<fmt:message key="security.remove"/>" onClick="this.form.portlet_action.value = 'security_user.remove_user_group'"/>
    </form>
	<form name="Add_Group_Form" action="<c:out value="${edit_group_link}"/>" method="post">
		<input type="hidden" name="portlet_action" value="security_user.add_user_group"/>
		
		<table>
			<tr>
				<td>
					<fmt:message key="security.groupname"/>
				</td>
				<td>
					<input type="text" name="group_name" value=""/>
				</td>
			</tr>
		</table>
		<input type="submit" value="<fmt:message key="security.add"/>"/>
	</form>
    
  </div>  
</c:if>
<%--End of Security Group tab data--%>


<%--Beginning Profile tab data--%>
<%--TODO:  switch to c:choose --%>
<c:if test="${selectedTab.id == 'user_profile'}">
  <div id="Profile">	
  <portlet:actionURL var="edit_profile_link" />
  
	<form name="Edit_Profile_Form" action="<c:out value="${edit_profile_link}"/>" method="post">
		<input type="hidden" name="portlet_action" value="security_user.remove_user_rule"/>		
		<table>
			<tr>
				<th>&nbsp;</th>
				<th><fmt:message key="security.name"/></th>
				<th><fmt:message key="security.rule"/></th>
			</tr>
		<c:forEach var="rule" items="${rules}">
			<tr>			
				<td>
					<input type="checkbox" name="user_profile_id" value="<c:out value="${rule.locatorName}"/>"/>
				</td>
				<td>
					<c:out value="${rule.locatorName}"/>
				</td>
				<td>
					<c:out value="${rule.profilingRule}"/>
				</td>
			</tr>
		</c:forEach>
		</table>
		<input type="submit" value="<fmt:message key="security.remove"/>" onClick="this.form.portlet_action.value = 'security_user.remove_user_rule'"/>
	</form>
	<form name="Add_Profile_Form" action="<c:out value="${edit_profile_link}"/>" method="post">
		<input type="hidden" name="portlet_action" value="security_user.add_rule"/>
		
		<table>
			<tr>
				<td>
					<fmt:message key="security.name"/>
				</td>
				<td>
					<input type="text" name="locator_name" value=""/>
					Common Locator Names: [page,docset]
				</td>
			</tr>
			<tr>
				<td>
					<fmt:message key="security.rule"/>
				</td>
				<td>
					<select name="select_rule">								
						<c:forEach var="prule" items="${prules}">						    						    
						    <option value="<c:out value="${prule.id}"/>">
							  <c:out value="${prule.id}"/>
						    </option>
						</c:forEach>
					</select>
				</td>
			</tr>
		</table>
		<input type="submit" value="<fmt:message key="security.add"/>"/>
	</form>
  </div>	
</c:if>
<%--End of Profile tab data--%>


<%--End of User check --%>
</c:if>

<br />
<br />


