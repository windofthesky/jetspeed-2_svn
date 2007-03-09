/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.portlets.tags;

import java.io.IOException;
import java.net.URLEncoder;

import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.apache.webapp.admin.TreeControl;
import org.apache.webapp.admin.TreeControlNode;
import org.apache.webapp.admin.TreeControlTag;

/**
 * @author <a href="mailto:jford@apache.org">Jeremy Ford</a>
 */
public class PortletTreeControlTag extends TreeControlTag
{
    private static final String PORTLET_REQUEST = "portlet_request";
    private static final String PORTLET_SESSION = "portlet_session";
    
    /**
     * The names of tree state images that we need.
     * 
     * Copied from parent
     */
    static final String IMAGE_HANDLE_DOWN_LAST =    "handledownlast.gif";
    static final String IMAGE_HANDLE_DOWN_MIDDLE =  "handledownmiddle.gif";
    static final String IMAGE_HANDLE_RIGHT_LAST =   "handlerightlast.gif";
    static final String IMAGE_HANDLE_RIGHT_MIDDLE = "handlerightmiddle.gif";
    static final String IMAGE_LINE_LAST =           "linelastnode.gif";
    static final String IMAGE_LINE_MIDDLE =         "linemiddlenode.gif";
    static final String IMAGE_LINE_VERTICAL =       "linevertical.gif";
    
    public void setScope(String scope) {
        if (!PORTLET_REQUEST.equals(scope) &&
            !PORTLET_SESSION.equals(scope)
            )
        {
            super.setScope(scope);
        }

        this.scope = scope;
    }
    
    /**
     * Return the <code>TreeControl</code> instance for the tree control that
     * we are rendering.
     *
     * @exception JspException if no TreeControl instance can be found
     */
    protected TreeControl getTreeControl() throws JspException {

        Object treeControl = null;
        
        PortletRequest renderRequest = (PortletRequest) pageContext.findAttribute("javax.portlet.request");
        if(PORTLET_REQUEST.equals(scope))
        {
            
            treeControl = renderRequest.getAttribute(tree);
        }
        else if(PORTLET_SESSION.equals(scope))
        {
            treeControl = renderRequest.getPortletSession().getAttribute(tree);
        }
                
        if (treeControl == null)
        {
            treeControl = super.getTreeControl();
        }
       
        return (TreeControl)treeControl;
    }
    
    /**
     * Render the specified node, as controlled by the specified parameters.
     *
     * @param out The <code>JspWriter</code> to which we are writing
     * @param node The <code>TreeControlNode</code> we are currently
     *  rendering
     * @param level The indentation level of this node in the tree
     * @param width Total displayable width of the tree
     * @param last Is this the last node in a list?
     *
     * @exception IOException if an input/output error occurs
     */
    protected void render(JspWriter out, TreeControlNode node,
                          int level, int width, boolean last)
        throws IOException {

        try
        {
        HttpServletResponse response =
            (HttpServletResponse) pageContext.getResponse();
        
        PortletRequest renderRequest = (PortletRequest)pageContext.getRequest().getAttribute("javax.portlet.request");
        RenderResponse renderResponse = (RenderResponse)pageContext.getRequest().getAttribute("javax.portlet.response");
        PortletConfig portletConfig = (PortletConfig)pageContext.getRequest().getAttribute("javax.portlet.config");
        
   
        // if the node is root node and the label value is
        // null, then do not render root node in the tree.
        
        if ("ROOT-NODE".equalsIgnoreCase(node.getName()) &&
        (node.getLabel() == null)) {
            // Render the children of this node
            TreeControlNode children[] = node.findChildren();
            int lastIndex = children.length - 1;
            int newLevel = level + 1;
            for (int i = 0; i < children.length; i++) {
                render(out, children[i], newLevel, width, i == lastIndex);
            }
            return;
        }
        
        // Render the beginning of this node
        out.println("  <tr valign=\"middle\">");
        out.print("<td><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tr valign=\"middle\">");

        // Create the appropriate number of indents
        for (int i = 0; i < level; i++) {
            int levels = level - i;
            TreeControlNode parent = node;
            for (int j = 1; j <= levels; j++)
                parent = parent.getParent();
            if (parent.isLast())
                out.print("    <td>&nbsp;</td>");
            else {
                out.print("    <td><img src=\"");
                out.print(images);
                out.print("/");
                out.print(IMAGE_LINE_VERTICAL);
                out.print("\" alt=\"\" border=\"0\"></td>");
            }
            out.println();
        }

        // Render the tree state image for this node

        // HACK to take into account special characters like = and &
        // in the node name, could remove this code if encode URL
        // and later request.getParameter() could deal with = and &
        // character in parameter values. 
        String encodedNodeName = URLEncoder.encode(node.getName());

        PortletURL treeActionUrl = renderResponse.createActionURL();
        treeActionUrl.setParameter("node", node.getName());
        String treeAction = treeActionUrl.toString();
//        String action = replace(getAction(), "${name}", encodedNodeName);

        
//        String updateTreeAction =
//            replace(getAction(), "tree=${name}", "select=" + encodedNodeName);
//        updateTreeAction =
//            ((HttpServletResponse) pageContext.getResponse()).
//            encodeURL(updateTreeAction);

        
        out.print("    <td>");
        
        
        //add an anchor so that we can return to this node
        out.print("<a name=\"");
        out.print(node.getName());
        out.print("\">");
        
        if ((action != null) && !node.isLeaf()) {
            out.print("<a href=\"");
            out.print(response.encodeURL(treeAction));
            out.print("\">");
        }
        out.print("<img src=\"");
        out.print(images);
        out.print("/");
        if (node.isLeaf()) {
            if (node.isLast())
                out.print(IMAGE_LINE_LAST);
            else
                out.print(IMAGE_LINE_MIDDLE);
            out.print("\" alt=\"");
        } else if (node.isExpanded()) {
            if (node.isLast())
                out.print(IMAGE_HANDLE_DOWN_LAST);
            else
                out.print(IMAGE_HANDLE_DOWN_MIDDLE);
            out.print("\" alt=\"close node");
        } else {
            if (node.isLast())
                out.print(IMAGE_HANDLE_RIGHT_LAST);
            else
                out.print(IMAGE_HANDLE_RIGHT_MIDDLE);
            out.print("\" alt=\"expand node");
        }
        out.print("\" border=\"0\">");
        if ((action != null) && !node.isLeaf())
            out.print("</a>");
        out.println("</td>");
        
        

        // Calculate the hyperlink for this node (if any)
        String hyperlink = null;
        String nodeAction = node.getAction();
        if(nodeAction == null && node.isExpandWhenClicked())
        {
            hyperlink = treeAction;
        }
        if (nodeAction != null)
        {
            if(node.getAction().equals("portlet_url"))
            {
                PortletURL actionUrl = renderResponse.createActionURL();
                actionUrl.setParameter("select_node", node.getName());
                hyperlink = ((HttpServletResponse) pageContext.getResponse()).encodeURL(actionUrl.toString());
            }
            else
            {
                hyperlink = ((HttpServletResponse) pageContext.getResponse()).
                    encodeURL(node.getAction());
            }
        }
        

        // Render the icon for this node (if any)
        out.print("    <td ");
        
        if(node.getLabel() != null)
        {
            //make sure text does not wrap
            out.print(" style=\"");
            out.print("white-space:nowrap; vertical-align:middle;");
            out.print("\"");
        }
        
        out.print(">");
        if (node.getIcon() != null) {
            if (hyperlink != null) {
                out.print("<a href=\"");
                out.print(hyperlink);
                out.print("\"");
                String target = node.getTarget();
                if(target != null) {
                    out.print(" target=\"");
                    out.print(target);
                    out.print("\"");
                }
                /* Invalid, not used, and not usefull
                // to refresh the tree in the same 'self' frame
                out.print(" onclick=\"");
                out.print("self.location.href='" + updateTreeAction + "'");
                out.print("\"");
                */
                out.print(">");
            }
            out.print("<img src=\"");
            out.print(images);
            out.print("/");
            out.print(node.getIcon());
            out.print("\" alt=\"");
            out.print("\" border=\"0\">");
            if (hyperlink != null)
                out.print("</a>");
        }

        // Render the label for this node (if any)

        if (node.getLabel() != null) {
            String labelStyle = node.getCSSClass();
            if (node.isSelected() && (styleSelected != null))
                labelStyle = styleSelected;
            else if (!node.isSelected() && (styleUnselected != null))
                labelStyle = styleUnselected;
            if (hyperlink != null) {
                // Note the leading space so that the text has some space
                // between it and any preceding images
                out.print(" <a href=\"");
                out.print(hyperlink);
                out.print("\"");
                String target = node.getTarget();
                if(target != null) {
                    out.print(" target=\"");
                    out.print(target);
                    out.print("\"");
                }
                if (labelStyle != null) {
                    out.print(" class=\"");
                    out.print(labelStyle);
                    out.print("\"");
                }
                
                if(node.getTitle() != null)
                {
                    out.print(" title=\"");
                    out.print(node.getTitle());
                    out.print("\"");
                }
                
                /* Invalid, not used, and not usefull
                // to refresh the tree in the same 'self' frame
                out.print(" onclick=\"");
                out.print("self.location.href='" + updateTreeAction + "'");
                out.print("\"");
                */
                out.print(">");
            } else if (labelStyle != null) {
                out.print("<span class=\"");
                out.print(labelStyle);
                out.print("\">");
            }
            out.print(node.getLabel());
            if (hyperlink != null)
                out.print("</a>");
            else if (labelStyle != null)
                out.print("</span>");
        }
        out.println("</td>");
        
        

        // Render the end of this node
        out.println("  </tr>");
        out.println("</table></td></tr>");
        
        // Render the children of this node
        if (node.isExpanded()) {
            TreeControlNode children[] = node.findChildren();
            int lastIndex = children.length - 1;
            int newLevel = level + 1;
            for (int i = 0; i < children.length; i++) {
                render(out, children[i], newLevel, width, i == lastIndex);
            }
        }
      }
      catch(Exception e)
      {
          out.print(e.getLocalizedMessage());
      }
    }
}
