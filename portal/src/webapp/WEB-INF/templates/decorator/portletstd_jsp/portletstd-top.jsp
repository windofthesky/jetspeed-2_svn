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