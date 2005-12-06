package org.apache.jetspeed.decoration;

import java.util.List;

import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.request.RequestContext;

public interface DecorationFactory
{
    Theme getTheme(Page page, RequestContext requestContext);
    
    PortletDecoration getPortletDecoration(String name, RequestContext requestContext);
    
    LayoutDecoration getLayoutDecoration(String name, RequestContext requestContext);
    
    Decoration getDecoration(Page page, Fragment fragment, RequestContext requestContext);
    
    /**
     * Get the portal-wide list of page decorations.
     * 
     * @return A list of page decorations of type <code>String</code>
     */
    List getPageDecorations(RequestContext request);

    /**
     * Get the portal-wide list of portlet decorations.
     * 
     * @return A list of portlet decorations of type <code>String</code>
     */    
    List getPortletDecorations(RequestContext request);
    
    /**
     * Get the portal-wide list of available layouts.
     * 
     * @return A list of layout portlets of type <code>LayoutInfo</code>
     */    
    List getLayouts(RequestContext request);
    
}
