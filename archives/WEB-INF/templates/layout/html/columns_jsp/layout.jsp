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
<%@ page language="java" 
         import="javax.portlet.*, java.util.*, org.apache.jetspeed.aggregator.*, org.apache.jetspeed.om.page.*,org.apache.jetspeed.velocity.JetspeedPowerTool"
         session="false" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri='/WEB-INF/portlet.tld' prefix='portlet'%>
<portlet:defineObjects/>

<% 
JetspeedPowerTool jetspeed = new JetspeedPowerTool(pageContext);
List[] table = jetpseed.getColumns();
Page myPage = jetspeed.getPage();
Fragment myFragment = jetspeed.getCurrentFragment();
org.apache.pluto.om.entity.PortletEntity portletEntity =jetspeed.getCurrentPortletEntity();
%>

<%
  String decorator = myPage.getDefaultDecorator(myFragment.getType());
  String decoTop = null;
  String decoBottom = null;
  
  if (myFragment.getDecorator()!=null)
  {
        decorator = myFragment.getDecorator();
  }

  if (decorator != null)
  {
        decoTop = decorator+"-top.jsp";
        decoBottom = decorator+"-bottom.jsp";
  }
%>
<!-- Decorator <%= decorator %>  <%=decoTop%>-<%=decoBottom%>-->
<%
  if ((decoTop != null) && (myFragment == myPage.getRootFragment()))
  {
%>
<jsp:include page="<%=decoTop%>" />
<%
  }
%>
<table width="100%" cellspacing="0" cellpadding="0">
  <tr>
  <%
     for(int i=0; i < table.length; i++)
     {
  %>
    <td valign="top"><table width="100%">
    <%
        for(Iterator it=table[i].iterator(); it.hasNext();)
        {
           Fragment f = (Fragment)it.next();
     %>
     <tr><td width="100%">
     <%
         jetspeed.decorateAndInclude(f);
      %>

     </td></tr>
     <%
        }
     %>
     </table></td>
  <%
     }
  %>
  </tr>
</table>
<%

  if ((decoBottom != null) && (myFragment == myPage.getRootFragment()))
  {
%>
<jsp:include page="<%=decoBottom%>" />
<%
  }
%>
