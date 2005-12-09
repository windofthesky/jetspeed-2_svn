<%@ page session="false" %>
<%@ page import="javax.portlet.*"%>
<%@ page import="java.util.*"%>
<%@ taglib uri='/WEB-INF/portlet.tld' prefix='portlet'%>

<portlet:actionURL portletMode="view" var="myCancel"/>

<portlet:defineObjects/>
<%
ResourceBundle myText = portletConfig.getResourceBundle(request.getLocale());
%>

<B><%=myText.getString("help.title")%></B>
<p><%=myText.getString("help.text")%></B></p>
<FORM ACTION="<%=myCancel%>" METHOD="POST">            
<INPUT NAME="cancel"  TYPE="submit"                 
 VALUE="<%=myText.getString("cancel")%>">            
</FORM>                                    