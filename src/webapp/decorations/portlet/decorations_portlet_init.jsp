<%@page language="java" pageEncoding="UTF-8"%>

<%@taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://java.sun.com/portlet" prefix="portlet" %>

<%@page import="org.apache.jetspeed.layout.JetspeedPowerTool"%>
<%@page import="org.apache.jetspeed.om.page.ContentFragment"%>

<portlet:defineObjects/>

<%

  int DEFAULT_SCOPE = PageContext.PAGE_SCOPE;

  //extract the jpt, just in case we need it.
  JetspeedPowerTool _jpt = (JetspeedPowerTool) renderRequest.getAttribute("jpt");
  pageContext.setAttribute("jetspeed", _jpt, DEFAULT_SCOPE);

  // set the fragment
  ContentFragment _frag = _jpt.getCurrentFragment();
  pageContext.setAttribute("frag", _frag, DEFAULT_SCOPE);
  
  //hidden
  pageContext.setAttribute("isHidden", _jpt.isHidden(_frag), DEFAULT_SCOPE);
  
%>

  <%-- BEGIN Request Scoped Variables --%>
  <c:set var="decoration" value="${frag.decoration}" scope="page"/>
  <c:set var="actions" value="${decoration.actions}" scope="page" />

  <%-- PortletActionBar --%>
  <c:set var="PortletActionBar" scope="page">
  
                        <div class="PActionBar"> <!-- B: div portal-page-actions --> 
                          <c:forEach var="_action" items="${actions}">
                            <a href="<c:out value='${_action.action}'/>" 
                               title="<c:out value='${_action.name}'/>" 
                               class="action portlet-action"
                               <c:if test="${_action.target}"> target="<c:out value='${_action.target}'/>"</c:if>>
                              <img src="<c:out value='${requestScope.cPath}/${_action.link}'/>" alt="<c:out value='${_action.alt}'/>" border="0" />
                            </a>
                          </c:forEach>
                        </div> <!-- E: div portal-page-actions --> 
  
  </c:set>

  <%-- END Request Scoped Variables --%>
  