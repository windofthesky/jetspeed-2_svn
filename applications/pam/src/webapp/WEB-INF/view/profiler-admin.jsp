<%@ taglib uri='/WEB-INF/portlet.tld' prefix='portlet'%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<f:view>
<h:dataTable value="#{tableData.names}" var="name">
    <h:column>
        <h:outputText value='#{name.last}'/>
        <f:verbatim>,</f:verbatim>
    </h:column>
    <h:column>
        <h:outputText value='#{name.first}'/>
    </h:column>

</h:dataTable>
</f:view>
<p>end</p>
