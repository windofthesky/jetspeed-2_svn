/*
 * Created on Jun 29, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.apache.jetspeed.portlets.pam.tags;

import javax.portlet.PortletRequest;
import javax.servlet.jsp.JspException;

import org.apache.webapp.admin.TreeControl;
import org.apache.webapp.admin.TreeControlTag;

/**
 * @author Jeremy
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PAMTreeControlTag extends TreeControlTag {
	
	/**
     * Return the <code>TreeControl</code> instance for the tree control that
     * we are rendering.
     *
     * @exception JspException if no TreeControl instance can be found
     */
    protected TreeControl getTreeControl() throws JspException {

        Object renderRequest = pageContext.findAttribute("renderRequest");
        Object treeControl =
            	((PortletRequest)renderRequest).getAttribute("j2_tree");
        if (treeControl == null)
            throw new JspException("Cannot find tree control attribute '" +
                                   tree + "'");
        else if (!(treeControl instanceof TreeControl))
            throw new JspException("Invalid tree control attribute '" +
                                   tree + "'");
        else
            return ((TreeControl) treeControl);

    }
}
