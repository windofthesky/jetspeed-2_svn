<%@ page session="false" %>                            
<%@ page import="javax.portlet.*"%>
<%@ page import="java.util.Enumeration"%>
<%@ page import="java.util.ResourceBundle"%>
<%@ taglib uri='/WEB-INF/portlet.tld' prefix='portlet'%>
<portlet:defineObjects/>                            

<%
ResourceBundle myText = portletConfig.getResourceBundle(renderRequest.getLocale());         
%>
<br><B><%=myText.getString("available_bookmarks")%></B><br><br>       
<%
  PortletPreferences prefs = renderRequest.getPreferences();
  Enumeration e = prefs.getNames();                        
  if (!e.hasMoreElements()) // no bookmarks
    {
%>
      <%=myText.getString("no_bookmarks")%><BR>
<%
    }
  while (e.hasMoreElements())
    {
      String name = (String)e.nextElement();
      String value = prefs.getValue
                     (name,"<"+
                     myText.getString("undefined")+">");
%>
      <A target="_blank" HREF=<%=value%>><%=name%></A><BR>                
<%
    }
%>
<br>