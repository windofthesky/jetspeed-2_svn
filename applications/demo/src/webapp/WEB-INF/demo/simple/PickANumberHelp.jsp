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
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri='/WEB-INF/portlet.tld' prefix='portlet'%>

<fmt:setBundle basename="org.apache.jetspeed.demo.simple.resources.PickANumberResources" />
<hr/>
<h2><fmt:message key="pickanumber.help.label.thisishelppage"/></h2>
<hr/>
<h3><fmt:message key="pickanumber.help.label.instructions"/></h3>
<p>
<fmt:message key="pickanumber.help.label.thisportletsrunsthepopularguessinggame"/>
</p>
<p>
<fmt:message key="pickanumber.help.label.whererangeis"/><br/>
<fmt:message key="pickanumber.help.label.where1is"/><br/>
</p>
<h4><fmt:message key="pickanumber.help.label.winning"/></h4>
<p>
<fmt:message key="pickanumber.help.label.whoeverguesses"/>
</p>
<p> 
<fmt:message key="pickanumber.help.label.taketurnwithyourfriends"/>
</p>
<p>
<hr/>
<h3>
<fmt:message key="pickanumber.help.label.preferences"/>
</h3>
<p>
<fmt:message key="pickanumber.help.label.thisportlethasonepreference"/>
</p>

<portlet:renderURL var="viewMe" portletMode='View'/>
<a href='<%=viewMe%>'>View</a>
