package org.apache.jetspeed.services.title;

import javax.servlet.http.HttpServletRequest;

import org.apache.pluto.om.window.PortletWindow;


public interface DynamicTitleService extends org.apache.pluto.services.title.DynamicTitleService
{
    String getDynamicTitle(PortletWindow window, HttpServletRequest request);
}
