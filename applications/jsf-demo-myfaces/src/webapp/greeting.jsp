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

<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<f:view>
    <h:form id="helloForm" >
    <h2>Hi. My name is Duke.  I'm thinking of a number from
    <h:outputText value="#{UserNumberBean.minimum}"/> to
    <h:outputText value="#{UserNumberBean.maximum}"/>.  Can you guess
    it?</h2>

    <h:graphicImage id="waveImg" url="/wave.med.gif" />
      <h:inputText id="userNo" value="#{UserNumberBean.userNumber}"
                      validator="#{UserNumberBean.validate}"/>          
     <h:commandButton id="submit" action="success" value="Submit" />
         <p>
     <h:message style="color: red; font-family: 'New Century Schoolbook', serif; font-style: oblique; text-decoration: overline" id="errors1" for="userNo"/>

    </h:form>
    
</f:view>  
