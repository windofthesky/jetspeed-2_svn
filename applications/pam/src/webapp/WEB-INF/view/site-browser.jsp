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
<%@ page import="net.sourceforge.myfaces.custom.tree.DefaultMutableTreeNode,
                 net.sourceforge.myfaces.custom.tree.model.DefaultTreeModel"%>
<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.sourceforge.net/tld/myfaces_ext_0_9.tld" prefix="x"%>

<!--
/*
 * Copyright 2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
//-->

<%
   if (pageContext.getAttribute("treeModel", PageContext.SESSION_SCOPE) == null) {
      DefaultMutableTreeNode root = new DefaultMutableTreeNode("XY");
      DefaultMutableTreeNode a = new DefaultMutableTreeNode("A");
      root.insert(a);
      DefaultMutableTreeNode b = new DefaultMutableTreeNode("B");
      root.insert(b);
      DefaultMutableTreeNode c = new DefaultMutableTreeNode("C");
      root.insert(c);

      DefaultMutableTreeNode node = new DefaultMutableTreeNode("a1");
      a.insert(node);
      node = new DefaultMutableTreeNode("a2 ");
      a.insert(node);
      node = new DefaultMutableTreeNode("b ");
      b.insert(node);

      a = node;
      node = new DefaultMutableTreeNode("x1");
      a.insert(node);
      node = new DefaultMutableTreeNode("x2");
      a.insert(node);

      pageContext.setAttribute("treeModel", new DefaultTreeModel(root), PageContext.SESSION_SCOPE);
   }
%>

<f:view>
	<x:panelLayout id="page">
        <f:facet name="body">
            <f:verbatim><h2>Here is a tree?</h2></f:verbatim>
            
            <h:panelGroup id="body">

                <x:tree id="tree" value="#{treeModel}"
                		iconLine="/images/tree/line.gif"
        				iconNoline="/images/tree/noline.gif"
				        iconChild="/images/tree/folder.gif"
				        iconChildFirst="/images/tree/line_first.gif"
				        iconChildMiddle="/images/tree/line_middle.gif"
				        iconChildLast="/images/tree/line_last.gif"
				        iconNodeOpen="/images/tree/node_open.gif"
				        iconNodeOpenFirst="/images/tree/node_open_first.gif"
				        iconNodeOpenMiddle="/images/tree/node_open_middle.gif"
				        iconNodeOpenLast="/images/tree/node_open_last.gif"
				        iconNodeClose="/images/tree/node_close.gif"
				        iconNodeCloseFirst="/images/tree/node_close_first.gif"
				        iconNodeCloseMiddle="/images/tree/node_close_middle.gif"
				        iconNodeCloseLast="/images/tree/node_close_last.gif"
                        styleClass="tree"
                        nodeClass="treenode"
                        selectedNodeClass="treenodeSelected"
                        expandRoot="true">
                </x:tree>
                <f:verbatim><br></f:verbatim>

            </h:panelGroup>
        </f:facet>
	</x:panelLayout>

</f:view>
