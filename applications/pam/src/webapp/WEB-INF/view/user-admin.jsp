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
<%@ taglib uri='/WEB-INF/portlet.tld' prefix='portlet'%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<link href='css/security-admin.css' type='text/css'/>

<f:view>
<h:dataTable 
	value="#{users.users}" 
	var="user"
	styleClass="securityList"
	headerClass="securityHeader"
	rowClasses="evenRow,oddRow"
>
    <h:column>
       <f:facet name="header">
         <h:outputText value="Last Name"
            style="font-weight: bold"/>
       </f:facet>    
        <h:outputText value='#{user.last}'/>
        <f:verbatim>,</f:verbatim>
    </h:column>
    <h:column>
       <f:facet name="header">
         <h:outputText value="First Name"
            style="font-weight: bold"/>
       </f:facet>        
        <h:outputText value='#{user.first}'/>
    </h:column>

</h:dataTable>
</f:view>
<p>end</p>
