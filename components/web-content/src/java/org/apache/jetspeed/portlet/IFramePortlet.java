/*
 * Copyright 2000-2004 The Apache Software Foundation.
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
package org.apache.jetspeed.portlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

/**
 * IFramePortlet
 *
 * TODO:
 * - add capabilities test for IFRAME
 * - add locale specific "no iframes" message
 *
 * @author <a href="mailto:rwatler@finali.com">Randy Watler</a>
 * @version $Id$
 */
public class IFramePortlet extends GenericPortlet
{
    /**
     * Configuration constants.
     */
    public static final String ENABLE_SOURCE_PREFERENCES_PARAM = "enableSourcePreferences";
    public static final String CUSTOM_SOURCE_PARAM = "customSource";
    public static final String MAXIMIZED_CUSTOM_SOURCE_PARAM = "maximizedCustomSource";
    public static final String EDIT_SOURCE_PARAM = "editSource";
    public static final String MAXIMIZED_EDIT_SOURCE_PARAM = "maximizedEditSource";
    public static final String HELP_SOURCE_PARAM = "helpSource";
    public static final String MAXIMIZED_HELP_SOURCE_PARAM = "maximizedHelpSource";
    public static final String VIEW_SOURCE_PARAM = "viewSource";
    public static final String MAXIMIZED_VIEW_SOURCE_PARAM = "maximizedViewSource";
    public static final String ALIGN_ATTR_PARAM = "align";
    public static final String CLASS_ATTR_PARAM = "class";
    public static final String FRAME_BORDER_ATTR_PARAM = "frameBorder";
    public static final String HEIGHT_ATTR_PARAM = "height";
    public static final String ID_ATTR_PARAM = "id";
    public static final String MARGIN_HEIGHT_ATTR_PARAM = "marginHeight";
    public static final String MARGIN_WIDTH_ATTR_PARAM = "marginWidth";
    public static final String MAXIMIZED_HEIGHT_ATTR_PARAM = "maximizedHeight";
    public static final String MAXIMIZED_SCROLLING_ATTR_PARAM = "maximizedScrolling";
    public static final String MAXIMIZED_STYLE_ATTR_PARAM = "maximizedStyle";
    public static final String MAXIMIZED_WIDTH_ATTR_PARAM = "maximizedWidth";
    public static final String NAME_ATTR_PARAM = "name";
    public static final String SCROLLING_ATTR_PARAM = "scrolling";
    public static final String STYLE_ATTR_PARAM = "style";
    public static final String WIDTH_ATTR_PARAM = "width";

    /**
     * Configuration default constants.
     */
    public static final String ALIGN_ATTR_DEFAULT = "BOTTOM";
    public static final String FRAME_BORDER_ATTR_DEFAULT = "0";
    public static final String HEIGHT_ATTR_DEFAULT = "100%";
    public static final String MARGIN_HEIGHT_ATTR_DEFAULT = "0";
    public static final String MARGIN_WIDTH_ATTR_DEFAULT = "0";
    public static final String MAXIMIZED_HEIGHT_ATTR_DEFAULT = "100%";
    public static final String MAXIMIZED_SCROLLING_ATTR_DEFAULT = "AUTO";
    public static final String MAXIMIZED_WIDTH_ATTR_DEFAULT = "100%";
    public static final String SCROLLING_ATTR_DEFAULT = "NO";
    public static final String WIDTH_ATTR_DEFAULT = "100%";

    /**
     * Enable source parameter preferences overrides member.
     */
    private boolean enableSourcePreferences;

    /**
     * Default IFRAME source attribute members.
     */
    private String defaultCustomSource;
    private String defaultMaximizedCustomSource;
    private String defaultEditSource;
    private String defaultMaximizedEditSource;
    private String defaultHelpSource;
    private String defaultMaximizedHelpSource;
    private String defaultViewSource;
    private String defaultMaximizedViewSource;

    /**
     * IFRAME attribute members.
     */
    private String alignAttr;
    private String classAttr;
    private String frameBorderAttr;
    private String heightAttr;
    private String idAttr;
    private String marginHeightAttr;
    private String marginWidthAttr;
    private String maximizedHeightAttr;
    private String maximizedScrollingAttr;
    private String maximizedStyleAttr;
    private String maximizedWidthAttr;
    private String nameAttr;
    private String scrollingAttr;
    private String styleAttr;
    private String widthAttr;

    /**
     * Portlet constructor.
     */
    public IFramePortlet()
    {
    }

    /**
     * Initialize portlet configuration.
     */
    public void init(PortletConfig config)
        throws PortletException
    {
        super.init(config);

        String initParam = config.getInitParameter(ENABLE_SOURCE_PREFERENCES_PARAM);
        if (initParam != null)
            enableSourcePreferences = (new Boolean(initParam)).booleanValue();

        defaultCustomSource = config.getInitParameter(CUSTOM_SOURCE_PARAM);
        defaultMaximizedCustomSource = config.getInitParameter(MAXIMIZED_CUSTOM_SOURCE_PARAM);
        defaultEditSource = config.getInitParameter(EDIT_SOURCE_PARAM);
        defaultMaximizedEditSource = config.getInitParameter(MAXIMIZED_EDIT_SOURCE_PARAM);
        defaultHelpSource = config.getInitParameter(HELP_SOURCE_PARAM);
        defaultMaximizedHelpSource = config.getInitParameter(MAXIMIZED_HELP_SOURCE_PARAM);
        defaultViewSource = config.getInitParameter(VIEW_SOURCE_PARAM);
        defaultMaximizedViewSource = config.getInitParameter(MAXIMIZED_VIEW_SOURCE_PARAM);

        alignAttr = getAttributeParam(config, ALIGN_ATTR_PARAM, ALIGN_ATTR_DEFAULT);
        classAttr = getAttributeParam(config, CLASS_ATTR_PARAM, null);
        frameBorderAttr = getAttributeParam(config, FRAME_BORDER_ATTR_PARAM, FRAME_BORDER_ATTR_DEFAULT);
        heightAttr = getAttributeParam(config, HEIGHT_ATTR_PARAM, HEIGHT_ATTR_DEFAULT);
        idAttr = getAttributeParam(config, ID_ATTR_PARAM, null);
        marginHeightAttr = getAttributeParam(config, MARGIN_HEIGHT_ATTR_PARAM, MARGIN_HEIGHT_ATTR_DEFAULT);
        marginWidthAttr = getAttributeParam(config, MARGIN_WIDTH_ATTR_PARAM, MARGIN_WIDTH_ATTR_DEFAULT);
        maximizedHeightAttr = getAttributeParam(config, MAXIMIZED_HEIGHT_ATTR_PARAM, MAXIMIZED_HEIGHT_ATTR_DEFAULT);
        maximizedScrollingAttr = getAttributeParam(config, MAXIMIZED_SCROLLING_ATTR_PARAM, MAXIMIZED_SCROLLING_ATTR_DEFAULT);
        maximizedStyleAttr = getAttributeParam(config, MAXIMIZED_STYLE_ATTR_PARAM, null);
        maximizedWidthAttr = getAttributeParam(config, MAXIMIZED_WIDTH_ATTR_PARAM, MAXIMIZED_WIDTH_ATTR_DEFAULT);
        nameAttr = getAttributeParam(config, NAME_ATTR_PARAM, null);
        scrollingAttr = getAttributeParam(config, SCROLLING_ATTR_PARAM, SCROLLING_ATTR_DEFAULT);
        styleAttr = getAttributeParam(config, STYLE_ATTR_PARAM, null);
        widthAttr = getAttributeParam(config, WIDTH_ATTR_PARAM, WIDTH_ATTR_DEFAULT);
    }
    
    /**
     * Generate IFRAME with custom source.
     */
    public void doCustom(RenderRequest request, RenderResponse response)
        throws PortletException, IOException
    {
        // get IFRAME source
        String source = null;
        if (request.getWindowState().equals(WindowState.MAXIMIZED))
            source = getSourcePreference(request, MAXIMIZED_CUSTOM_SOURCE_PARAM, defaultMaximizedCustomSource);
        if (source == null)
            source = getSourcePreference(request, CUSTOM_SOURCE_PARAM, defaultCustomSource);
        if ((source == null) && request.getWindowState().equals(WindowState.MAXIMIZED))
            source = getSourcePreference(request, MAXIMIZED_VIEW_SOURCE_PARAM, defaultMaximizedViewSource);
        if (source == null)
            source = getSourcePreference(request, VIEW_SOURCE_PARAM, defaultViewSource);
        if (source == null)
            throw new PortletException("IFRAME source not specified for custom portlet mode.");

        // render IFRAME content
        doIFrame(request, source, response);
    }

    /**
     * Generate IFRAME with edit source.
     */
    public void doEdit(RenderRequest request, RenderResponse response)
        throws PortletException, IOException
    {
        // get IFRAME source
        String source = null;
        if (request.getWindowState().equals(WindowState.MAXIMIZED))
            source = getSourcePreference(request, MAXIMIZED_EDIT_SOURCE_PARAM, defaultMaximizedEditSource);
        if (source == null)
            source = getSourcePreference(request, EDIT_SOURCE_PARAM, defaultEditSource);
        if ((source == null) && request.getWindowState().equals(WindowState.MAXIMIZED))
            source = getSourcePreference(request, MAXIMIZED_VIEW_SOURCE_PARAM, defaultMaximizedViewSource);
        if (source == null)
            source = getSourcePreference(request, VIEW_SOURCE_PARAM, defaultViewSource);
        if (source == null)
            throw new PortletException("IFRAME source not specified for edit portlet mode.");

        // render IFRAME content
        doIFrame(request, source, response);
    }

    /**
     * Generate IFRAME with help source.
     */
    public void doHelp(RenderRequest request, RenderResponse response)
        throws PortletException, IOException
    {
        // get IFRAME source
        String source = null;
        if (request.getWindowState().equals(WindowState.MAXIMIZED))
            source = getSourcePreference(request, MAXIMIZED_HELP_SOURCE_PARAM, defaultMaximizedHelpSource);
        if (source == null)
            source = getSourcePreference(request, HELP_SOURCE_PARAM, defaultHelpSource);
        if ((source == null) && request.getWindowState().equals(WindowState.MAXIMIZED))
            source = getSourcePreference(request, MAXIMIZED_VIEW_SOURCE_PARAM, defaultMaximizedViewSource);
        if (source == null)
            source = getSourcePreference(request, VIEW_SOURCE_PARAM, defaultViewSource);
        if (source == null)
            throw new PortletException("IFRAME source not specified for help portlet mode.");

        // render IFRAME content
        doIFrame(request, source, response);
    }

    /**
     * Generate IFRAME with view source.
     */
    public void doView(RenderRequest request, RenderResponse response)
        throws PortletException, IOException
    {
        // get IFRAME source
        String source = null;
        if (request.getWindowState().equals(WindowState.MAXIMIZED))
            source = getSourcePreference(request, MAXIMIZED_VIEW_SOURCE_PARAM, defaultMaximizedViewSource);
        if (source == null)
            source = getSourcePreference(request, VIEW_SOURCE_PARAM, defaultViewSource);
        if (source == null)
            throw new PortletException("IFRAME source not specified for view portlet mode.");

        // render IFRAME content
        doIFrame(request, source, response);
    }

    /**
     * Render IFRAME content
     */
    protected void doIFrame(RenderRequest request, String sourceAttr, RenderResponse response)
        throws IOException
    {
        // generate HTML IFRAME content
        StringBuffer content = new StringBuffer(4096);
        content.append("<IFRAME");
        content.append(" SRC=\"").append(sourceAttr).append("\"");
        if (alignAttr != null)
            content.append(" ALIGN=\"").append(alignAttr).append("\"");
        if (classAttr != null)
            content.append(" CLASS=\"").append(classAttr).append("\"");
        if (frameBorderAttr != null)
            content.append(" FRAMEBORDER=\"").append(frameBorderAttr).append("\"");
        if (idAttr != null)
            content.append(" ID=\"").append(idAttr).append("\"");
        if (marginHeightAttr != null)
            content.append(" MARGINHEIGHT=\"").append(marginHeightAttr).append("\"");
        if (marginWidthAttr != null)
            content.append(" MARGINWIDTH=\"").append(marginWidthAttr).append("\"");
        if (nameAttr != null)
            content.append(" NAME=\"").append(nameAttr).append("\"");
        if (request.getWindowState().equals(WindowState.MAXIMIZED))
        {
            if (maximizedHeightAttr != null)
                content.append(" HEIGHT=\"").append(maximizedHeightAttr).append("\"");
            if (maximizedScrollingAttr != null)
                content.append(" SCROLLING=\"").append(maximizedScrollingAttr).append("\"");
            if (maximizedStyleAttr != null)
                content.append(" STYLE=\"").append(maximizedStyleAttr).append("\"");
            if (maximizedWidthAttr != null)
                content.append(" WIDTH=\"").append(maximizedWidthAttr).append("\"");
        }
        else
        {
            if (heightAttr != null)
                content.append(" HEIGHT=\"").append(heightAttr).append("\"");
            if (scrollingAttr != null)
                content.append(" SCROLLING=\"").append(scrollingAttr).append("\"");
            if (styleAttr != null)
                content.append(" STYLE=\"").append(styleAttr).append("\"");
            if (widthAttr != null)
                content.append(" WIDTH=\"").append(widthAttr).append("\"");
        }
        content.append(">");
        content.append("<P STYLE=\"textAlign:center\"><A HREF=\"").append(sourceAttr).append("\">").append(sourceAttr).append("</A></P>");
        content.append("</IFRAME>");

        // set required content type and write HTML IFRAME content
        response.setContentType("text/html");
        response.getWriter().print(content.toString());
    }

    /**
     * Get IFRAME attribute parameter.
     */
    private String getAttributeParam(PortletConfig config, String name, String defaultValue)
    {
        String value = config.getInitParameter(name);
        if (value == null)
            value = defaultValue;
        return (((value != null) && (value.length() > 0) && ! value.equalsIgnoreCase("none")) ? value : null);
    }

    /**
     * Get IFRAME source preference value if enabled.
     */
    private String getSourcePreference(RenderRequest request, String name, String defaultValue)
    {
        if (! enableSourcePreferences)
            return defaultValue;
        PortletPreferences prefs = request.getPreferences();
        return ((prefs != null) ? prefs.getValue(name, defaultValue) : defaultValue);
    }
}
