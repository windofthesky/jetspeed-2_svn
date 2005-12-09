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
<%@ page language="java" import="org.apache.jetspeed.om.page.*,org.apache.pluto.om.entity.*" session="false" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri='/WEB-INF/portlet.tld' prefix='portlet'%>
<portlet:defineObjects/>
<% 
Page myPage = (Page)renderRequest.getAttribute("page");
Fragment myF = (Fragment)renderRequest.getAttribute("fragment");
PortletEntity myPE = (PortletEntity)renderRequest.getAttribute("entity");
%>
<!-- Portlet Std Decorator top -->
<table width="100%" cellspacing"0" cellpadding="0">
<tr>
  <td bgcolor="<%=myPage.getDefaultSkin()%>">
  <table width="100%">
  <tr>
    <td><% if (myPE!=null) out.write(myPE.getPortletDefinition().getName());%></td>
    <td>
    </td>
  </tr>
  </table>
  </td>
</tr>
<tr><td width="100%" valign="top">