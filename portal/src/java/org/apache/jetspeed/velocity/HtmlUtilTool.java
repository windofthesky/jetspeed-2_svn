/*
 * Created on Aug 19, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.velocity;

import org.apache.velocity.tools.view.tools.ViewTool;

/**
 * <p>
 * HtmlUtilTool
 * </p>
 * <p>
 *
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class HtmlUtilTool implements ViewTool
{

    /**
     * <p>
     * init
     * </p>
     *
     * @see org.apache.velocity.tools.view.tools.ViewTool#init(java.lang.Object)
     * @param arg0
     */
    public void init( Object arg0 )
    {        
        
    }
    
    public String getSafeElementId(Object obj)
    {
        if(obj == null)
        {
            return "null";
        }
        else
        {
            // Convert "/" to "-"
            String initValue = obj.toString();
            return initValue.replaceAll("[\\/,\\.]","-");
        }
    }

}
