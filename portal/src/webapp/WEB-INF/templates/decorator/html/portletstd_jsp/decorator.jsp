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
    <td><% if (myPE!=null) out.write(myPE.getPortletDefinition().getName());%></td>
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