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
<%@ page language="java" import="javax.portlet.*, java.util.List, java.util.Iterator, org.apache.jetspeed.om.common.portlet.MutablePortletApplication" session="true" %>
<%@ taglib uri='/WEB-INF/portlet.tld' prefix='portlet'%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/pam.tld" prefix="pam" %>
<portlet:defineObjects/>
<h2>Portlet Application Manager</h2>
<h3>Application Tree View</h3>

<hr />

<portlet:actionURL var="searchLink" />

<form action="<c:out value="${searchLink}"/>" method="post">

	<input type="text" name="query" value="" /> <input type="submit" value="Search"/>

</form>

<c:set var="results" value="${requestScope.search_results}" />

<c:if test="${results != null}">
	<c:forEach var="result" items="${search_results}">
		<c:out value="${result.title}"/> | <c:out value="${result.description}"/> <br />
	</c:forEach>
</c:if>

<hr />



<portlet:actionURL var="nodeLink" >
	<portlet:param name="node" value="${name}" />
</portlet:actionURL>

<pam:tree tree="j2_tree" images="/pam/images" scope="portlet_request"
          action="<%= nodeLink %>"
  />
  
  <%--
  style="tree-control"
        styleSelected="tree-control-selected"
      styleUnselected="tree-control-unselected"
      --%>

