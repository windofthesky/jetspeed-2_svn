/*
 * Created on May 28, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.container.url;
import java.util.Iterator;
import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.pluto.om.window.PortletWindow;

/**
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver</a>
 *
 */
public interface PortalControlParameter
{
    void init();

    void clearRenderParameters( PortletWindow portletWindow );

    PortletMode getMode( PortletWindow window );

    PortletWindow getPortletWindowOfAction();

    PortletMode getPrevMode( PortletWindow window );

    WindowState getPrevState( PortletWindow window );

    Iterator getRenderParamNames( PortletWindow window );

    String[] getRenderParamValues( PortletWindow window, String paramName );

    WindowState getState( PortletWindow window );

    Map getStateFullControlParameter();

    Map getStateLessControlParameter();

    boolean isOnePortletWindowMaximized();

    void setAction( PortletWindow window );

    void setMode( PortletWindow window, PortletMode mode );

    void setRenderParam( PortletWindow window, String name, String[] values );

    void setState( PortletWindow window, WindowState state );

    String getPIDValue();

    String decodeParameterName( String paramName );

    String encodeParameter( String param );

    String encodeRenderParamName( PortletWindow window, String paramName );

    String encodeRenderParamValues( String[] paramValues );

    String decodeParameterValue( String paramName, String paramValue );
}