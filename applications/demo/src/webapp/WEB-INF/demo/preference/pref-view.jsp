<%@ page language="java" import="javax.portlet.*, java.util.List" session="true" %>
<%@ taglib uri='/WEB-INF/portlet.tld' prefix='portlet'%>
<%@ taglib uri='/WEB-INF/veltag.tld' prefix='vel'%>

<portlet:defineObjects/>

<h3>Preference List</h3>

<portlet:renderURL>
	  <portlet:param name="foo" value="bar" />
</portlet:renderURL>
<vel:velocity>

#set($preferences = $renderRequest.Preferences)
#foreach( $prefName in $preferences.Names)
  
  ${prefName} <br/>
  #foreach($prefValue in $preferences.getValues($prefName, null))
    &nbsp;&nbsp; value $velocityCount = ${prefValue} <br/>
  #end
 
#end
</vel:velocity>