/*
 * Copyright 2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.portlets.tags;

import javax.portlet.PortletRequest;
import javax.servlet.jsp.JspException;

import org.apache.webapp.admin.TreeControl;
import org.apache.webapp.admin.TreeControlTag;

/**
 * @author <a href="mailto:jford@apache.org">Jeremy Ford</a>
 */
public class PortletTreeControlTag extends TreeControlTag
{
    private static final String PORTLET_REQUEST = "portlet_request";
    private static final String PORTLET_SESSION = "portlet_session";
    
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
}
