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
<%@ page language="java" import="org.apache.jetspeed.om.page.*,org.apache.pluto.om.entity.*, org.apache.jetspeed.velocity.JetspeedPowerTool" session="false" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri='/WEB-INF/portlet.tld' prefix='portlet'%>
<portlet:defineObjects/>
<% 
JetspeedPowerTool jetspeed = new JetspeedPowerTool(pageContext);
Page myPage = jetspeed.getPage();
Fragment myF = jetspeed.getCurrentFragment();
PortletEntity myPE = jetspeed.getCurrentPortletEntity();

%>
<!-- BEGIN: portletstd_jsp -->
<table width="100%" cellspacing"0" cellpadding="0">
<tr>
  <td bgcolor="<%=myPage.getDefaultSkin()%>">
  <table width="100%">
  <tr>
    <td><% jetspeed.getTitle(myPE, myF);%></td>
    <td>
    </td>
  </tr>
  </table>
  </td>
</tr>
<tr><td width="100%" valign="top">
     <%
       if (!jetspeed.isHidden(myF))
       {
          jetspeed.include(myF);
       }
     %>
</td>
</tr>
</table>
<!-- END: portletstd_jsp -->