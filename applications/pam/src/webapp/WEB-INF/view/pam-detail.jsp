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
<%@ page language="java" import="javax.portlet.*, org.apache.jetspeed.portlets.pam.PortletApplicationBean" session="true" %>
<%@ taglib uri='/WEB-INF/portlet.tld' prefix='portlet'%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<portlet:defineObjects/>
<h2>Portlet Application Detail</h2>

<%
	String name = "N/A";
	String version = "0.0";
	PortletApplicationBean pa = (PortletApplicationBean)renderRequest.getAttribute("portletApplication");
	if (null != pa)
	{
		name = pa.getName();
		version = pa.getVersion();
	}
		
%>


app.name = <%= name %><br/>
app.version = <%= version %>

<p>TODO: Details</p>
