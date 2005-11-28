package org.apache.jetspeed.decoration;

import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.request.RequestContext;

public interface DecorationFactory
{
    Theme getTheme(Page page, RequestContext requestContext);
    
    PortletDecoration getPortletDecoration(String name, RequestContext requestContext);
    
    LayoutDecoration getLayoutDecoration(String name, RequestContext requestContext);
    
    Decoration getDecoration(Page page, Fragment fragment, RequestContext requestContext);    
}
