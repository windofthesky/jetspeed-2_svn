<%--
 Copyright 2000-2004 Apache Software Foundation

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
<%@ page import="java.io.PrintWriter"%>
<%@include file="../common/IncludeTop.jsp"%>

<logic:notPresent name="BeanActionException">
  <logic:notPresent name="message">
    <H3>Something happened...</H3>
    <B>But no further information was provided.</B>
  </logic:notPresent>
</logic:notPresent>
<P/>

<logic:present name="BeanActionException">
  <H3>Error!</H3>
  <B><font color="red">
    <bean:write name="BeanActionException" property="class.name"/></font></B>

  <P/>
  <bean:write name="BeanActionException" property="message"/>
</logic:present>

<P/>

<logic:present name="BeanActionException">
  <h4>Stack</h4>
  <i><pre>
<%
  Exception e = (Exception)request.getAttribute("BeanActionException");
  e.printStackTrace(new PrintWriter(out));
%>
  </pre></i>
</logic:present>

<%@include file="../common/IncludeBottom.jsp"%>
