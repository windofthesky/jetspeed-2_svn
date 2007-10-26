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
package org.apache.jetspeed.desktop.impl;

import java.io.IOException;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.cache.CacheElement;
import org.apache.jetspeed.cache.JetspeedCache;
import org.apache.jetspeed.container.url.BasePortalURL;
import org.apache.jetspeed.decoration.DecorationFactory;
import org.apache.jetspeed.decoration.LayoutDecoration;
import org.apache.jetspeed.decoration.PortletDecoration;
import org.apache.jetspeed.decoration.Theme;
import org.apache.jetspeed.desktop.JetspeedDesktop;
import org.apache.jetspeed.desktop.JetspeedDesktopContext;
import org.apache.jetspeed.headerresource.HeaderResource;
import org.apache.jetspeed.headerresource.HeaderResourceFactory;
import org.apache.jetspeed.headerresource.HeaderResourceLib;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.request.RequestContext;
import org.springframework.web.context.ServletContextAware;

/**
 * Desktop Valve
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:smilek@apache.org">Steve Milek</a>
 * @version $Id: JetspeedDesktopImpl.java $
 */
public class JetspeedDesktopImpl implements JetspeedDesktop, ServletContextAware
{
    private final static String EOL = "\r\n";   // html eol
    private final static String DOJO_CONFIG_LAYOUT_DECORATION_PATH_VAR_NAME = HeaderResource.HEADER_INTERNAL_DOJO_CONFIG_JETSPEED_VAR_NAME + ".layoutDecorationPath";
    private final static String DOJO_CONFIG_LAYOUT_VAR_NAME = HeaderResource.HEADER_INTERNAL_DOJO_CONFIG_JETSPEED_VAR_NAME + ".layoutName";
    private final static String DOJO_CONFIG_PORTLET_DECORATIONS_PATH_VAR_NAME = HeaderResource.HEADER_INTERNAL_DOJO_CONFIG_JETSPEED_VAR_NAME + ".portletDecorationsPath";
    private final static String DOJO_CONFIG_PORTLET_DECORATIONS_ALLOWED_VAR_NAME = HeaderResource.HEADER_INTERNAL_DOJO_CONFIG_JETSPEED_VAR_NAME + ".portletDecorationsAllowed";
    private final static String DOJO_CONFIG_PORTLET_DECORATIONS_CONFIG_VAR_NAME = HeaderResource.HEADER_INTERNAL_DOJO_CONFIG_JETSPEED_VAR_NAME + ".portletDecorationsProperties";    
    private final static String DOJO_CONFIG_ACTION_LABELS_NAME = HeaderResource.HEADER_INTERNAL_DOJO_CONFIG_JETSPEED_VAR_NAME + ".desktopActionLabels";
    private final static String DOJO_CONFIG_LOADING_IMGPROPS_NAME = HeaderResource.HEADER_INTERNAL_DOJO_CONFIG_JETSPEED_VAR_NAME + ".loadingImgProps";
    private final static String DOJO_CONFIG_PAGEEDITOR_LABELS_NAME = HeaderResource.HEADER_INTERNAL_DOJO_CONFIG_JETSPEED_VAR_NAME + ".pageEditorLabels";
    private final static String DOJO_CONFIG_PAGEEDITOR_DIALOG_LABELS_NAME = HeaderResource.HEADER_INTERNAL_DOJO_CONFIG_JETSPEED_VAR_NAME + ".pageEditorDialogLabels";
    
    private final static String[] DESKTOP_LOADING_PROPERTY_NAMES = new String[] 
                                                                 { "imgdir", "imganimated", "imgstepprefix", "imgstepextension", "imgsteps"
                                                                 };
    private final static String[] DESKTOP_ACTION_RESOURCE_NAMES = new String[] 
                                                                 { "menu", "tile", "untile", "heightexpand", "heightnormal",
    															   "restore", "removeportlet", "minimized", "maximized", "normal",
    															   "help", "edit", "view", "print", "addportlet", "editpage", 
    															   "movetiled", "moveuntiled", "loadpage", "loadpageeditor",
    															   "loadportletrender", "loadportletaction", "loadportletupdate"
    															 };
    private final static String[] DESKTOP_PAGEEDITOR_RESOURCE_NAMES = new String[]
                                                                 { "title", "changelayout", "changepagelayouttheme", "changepageportlettheme",
    	                                                           "newpage", "deletepage", "addlayout", "addportlet", "columnsizes",
    	                                                           "deletelayout", "movemode", "movemode_exit", "changeportlettheme"
    	                                                         };
    private final static String[] DESKTOP_PAGEEDITOR_DIALOG_RESOURCE_NAMES = new String[]
                                                                 { "columnsizes", "columnsizes_column1", "columnsizes_column2", "columnsizes_column3",
    	                                                           "columnsizes_column4", "columnsizes_column5", "newpage", "newpage_name",
    	                                                           "newpage_title", "newpage_titleshort", "deletepage", "deletelayout",
    	                                                           "removeportlet", "ok", "cancel", "yes", "no"
                                                                 };
    private final static String DESKTOP_LOADING_PROPERTY_NAME_PREFIX = "desktop.loading.";
    private final static String DESKTOP_ACTION_RESOURCE_NAME_PREFIX = "desktop.action.";
    private final static String DESKTOP_PAGEEDITOR_RESOURCE_NAME_PREFIX = "desktop.pageeditor.";
    private final static String DESKTOP_PAGEEDITOR_DIALOG_RESOURCE_NAME_PREFIX = "desktop.pageeditor.dialog.";
    
    private static final Log log = LogFactory.getLog( JetspeedDesktopImpl.class );

    private DecorationFactory decorationFactory;
        
    /** desktop pipeline servlet path */
    private String desktopServletPath;
    
    /** default extension for layout templates */
    private String defaultLayoutTemplateExtension;
    
    /** spring-fed servlet context property */
    private ServletContext servletContext;
    
    /** tool for directing output to html &lt;head&gt; */
    private HeaderResourceFactory headerResourceFactory;
    
    private JetspeedCache desktopContentCache;
    
    /** base portal URL to override default URL server info from servlet */
    private BasePortalURL baseUrlAccess = null;
    
    public JetspeedDesktopImpl( DecorationFactory decorationFactory, HeaderResourceFactory headerResourceFactory, JetspeedCache desktopContentCache, String desktopServletPath, String defaultLayoutTemplateExtension )
    {
        this( decorationFactory, headerResourceFactory, desktopContentCache, desktopServletPath, defaultLayoutTemplateExtension, null, null, null );
    }
    public JetspeedDesktopImpl( DecorationFactory decorationFactory, HeaderResourceFactory headerResourceFactory, JetspeedCache desktopContentCache, String desktopServletPath, String defaultLayoutTemplateExtension, String defaultDesktopLayoutDecoration, String defaultDesktopPortletDecoration )
    {
        this( decorationFactory, headerResourceFactory, desktopContentCache, desktopServletPath, defaultLayoutTemplateExtension, defaultDesktopLayoutDecoration, defaultDesktopPortletDecoration, null );
    }
    public JetspeedDesktopImpl( DecorationFactory decorationFactory, HeaderResourceFactory headerResourceFactory, JetspeedCache desktopContentCache, String desktopServletPath, String defaultLayoutTemplateExtension, String defaultDesktopLayoutDecoration, String defaultDesktopPortletDecoration, BasePortalURL baseUrlAccess )
    {
        this.decorationFactory = decorationFactory;
        this.headerResourceFactory = headerResourceFactory;
        this.desktopContentCache = desktopContentCache;
        
        if ( desktopServletPath != null && desktopServletPath.length() > 0 )
        {
            if ( desktopServletPath.charAt( 0 ) != '/' )
                desktopServletPath = "/" + desktopServletPath;
        }
        this.desktopServletPath = desktopServletPath;
        if ( this.desktopServletPath == null || this.desktopServletPath.length() == 0 )
        {
            log.warn( "JetspeedDesktopImpl initialization is incomplete due to undefined desktop servlet path." );
            this.desktopServletPath = null;
        }
        
        this.defaultLayoutTemplateExtension = defaultLayoutTemplateExtension;
        
        // set default layout and portlet decorations only if they are not currently undefined
        if ( defaultDesktopLayoutDecoration != null && defaultDesktopLayoutDecoration.length() > 0 )
        {
            String existingDefaultDesktopLayoutDecoration = decorationFactory.getDefaultDesktopLayoutDecoration();
            if ( existingDefaultDesktopLayoutDecoration == null || existingDefaultDesktopLayoutDecoration.length() == 0 )
            {
                decorationFactory.setDefaultDesktopLayoutDecoration( defaultDesktopLayoutDecoration );
            }
        }
        if ( defaultDesktopPortletDecoration != null && defaultDesktopPortletDecoration.length() > 0 )
        {
            String existingDefaultDesktopPortletDecoration = decorationFactory.getDefaultDesktopPortletDecoration();
            if ( existingDefaultDesktopPortletDecoration == null || existingDefaultDesktopPortletDecoration.length() == 0 )
            {
                decorationFactory.setDefaultDesktopPortletDecoration( defaultDesktopPortletDecoration );
            }
        }
        
        this.baseUrlAccess = baseUrlAccess;
    }

    public void render( RequestContext request )    
    {
        String layoutDecorationTemplatePath = null;
        boolean layoutDecorationTemplatePathWasAssigned = false;
        try
        {
            Page page = request.getPage();
            
            // enable desktop
            request.setAttribute( JetspeedDesktop.DESKTOP_ENABLED_REQUEST_ATTRIBUTE, Boolean.TRUE );
            
            // get decorations
            Theme theme = decorationFactory.getTheme( page, request );
            
            HeaderResource hr = getHeaderResourceFactory().getHeaderResouce( request );
            JetspeedDesktopContext desktopContext = new JetspeedDesktopContextImpl( request, this.baseUrlAccess, theme, hr, defaultLayoutTemplateExtension );
            
            String layoutTemplateIdPropertyName = null;
            if ( "true".equals( request.getRequest().getParameter( "jsprintmode" ) ) )
                layoutTemplateIdPropertyName = JetspeedDesktopContext.LAYOUT_PRINT_TEMPLATE_ID_PROP;
            
            layoutDecorationTemplatePath = desktopContext.getLayoutTemplatePath( layoutTemplateIdPropertyName );
            layoutDecorationTemplatePathWasAssigned = true;
            
            RequestDispatcher dispatcher = request.getRequest().getRequestDispatcher( layoutDecorationTemplatePath );
            
            hr.dojoEnable();
            
            request.getRequest().setAttribute( JetspeedDesktopContext.DESKTOP_CONTEXT_ATTRIBUTE, desktopContext );
            request.getRequest().setAttribute( JetspeedDesktopContext.DESKTOP_REQUEST_CONTEXT_ATTRIBUTE, request );
            request.getRequest().setAttribute( JetspeedDesktopContext.DESKTOP_COMPONENT_MANAGER_ATTRIBUTE, Jetspeed.getComponentManager() );
            
            String layoutDecorationName = desktopContext.getLayoutDecorationName();
            boolean inclStyleLayout = hr.isHeaderSectionIncluded( HeaderResource.HEADER_SECTION_DESKTOP_STYLE_LAYOUT );
            String dojoConfigContentCacheKey = DOJO_CONFIG_LAYOUT_VAR_NAME + "." + layoutDecorationName;
            String dojoConfigContent = getCachedContent( dojoConfigContentCacheKey );
            
            if ( dojoConfigContent == null )
            {
	            String portletDecorationsBasePath = decorationFactory.getPortletDecorationsBasePath();
	            StringBuffer dojoConfigAddOn = new StringBuffer();
	            dojoConfigAddOn.append( "    " ).append( DOJO_CONFIG_LAYOUT_DECORATION_PATH_VAR_NAME ).append( " = \"" ).append( desktopContext.getLayoutBasePath() ).append( "\";" ).append( EOL );
	            dojoConfigAddOn.append( "    " ).append( DOJO_CONFIG_LAYOUT_VAR_NAME ).append( " = \"" ).append( layoutDecorationName ).append( "\";" ).append( EOL );
	            dojoConfigAddOn.append( "    " ).append( DOJO_CONFIG_PORTLET_DECORATIONS_PATH_VAR_NAME ).append( " = \"" ).append( portletDecorationsBasePath ).append( "\";" ).append( EOL );
	            
	            LayoutDecoration desktopLayoutDecoration = decorationFactory.getLayoutDecoration( layoutDecorationName, request );
	            if ( desktopLayoutDecoration != null )
	            {
	            	boolean atLeastOneFound = false;
	            	StringBuffer loadingPropsBuffer = new StringBuffer();
	                loadingPropsBuffer.append( "    " ).append( DOJO_CONFIG_LOADING_IMGPROPS_NAME ).append( " = { " );
	                for ( int i = 0 ; i < DESKTOP_LOADING_PROPERTY_NAMES.length ; i++ )
	                {
	                    String propValue = desktopLayoutDecoration.getProperty( DESKTOP_LOADING_PROPERTY_NAME_PREFIX + DESKTOP_LOADING_PROPERTY_NAMES[ i ] );
	                    if ( propValue != null )
	                    {
	                        if ( atLeastOneFound )
	                        {
	                            loadingPropsBuffer.append( ", " );
	                        }
	                        else
	                        {
	                        	atLeastOneFound = true;
	                        }
	                        loadingPropsBuffer.append( DESKTOP_LOADING_PROPERTY_NAMES[ i ] ).append( ": " ).append( propValue );
	                    }
	                }
	                loadingPropsBuffer.append( " };" );
	                if ( atLeastOneFound )
	                	dojoConfigAddOn.append( loadingPropsBuffer.toString() ).append( EOL );
	            }
	            else
	            {
	            	log.error( "Failed to find desktop layout decoration " + layoutDecorationName + " - layout decoration properties cannot be added to content." );
	            }
	            
	            Set desktopPortletDecorationsNames = decorationFactory.getDesktopPortletDecorations( request );
	            String portletDecorationNamesContent = HeaderResourceLib.makeJSONStringArray( desktopPortletDecorationsNames );
	            dojoConfigAddOn.append( "    " ).append( DOJO_CONFIG_PORTLET_DECORATIONS_ALLOWED_VAR_NAME ).append( " = " ).append( portletDecorationNamesContent ).append( ";" );

	            StringBuffer pDecsOut = new StringBuffer();
	            Iterator desktopPortletDecorationsNamesIter = desktopPortletDecorationsNames.iterator();
	            while ( desktopPortletDecorationsNamesIter.hasNext() )
	            {
	                String desktopPortletDecorationName = (String)desktopPortletDecorationsNamesIter.next();
	            
	                PortletDecoration desktopPortletDecoration = decorationFactory.getPortletDecoration( desktopPortletDecorationName, request );
	                
	                StringBuffer pOut = new StringBuffer();
	                
	                String actionButtonOrderContent = desktopPortletDecoration.getProperty( HeaderResource.HEADER_CONFIG_DESKTOP_WINDOW_ACTION_BUTTON_ORDER );
	                if ( actionButtonOrderContent != null && actionButtonOrderContent.length() > 0 )
	                {
	                    pOut.append( ( pOut.length() > 0 ) ? ", " : "" ).append( HeaderResource.DESKTOP_JSON_WINDOW_ACTION_BUTTON_ORDER ).append( ": " ).append( actionButtonOrderContent );
	                }
	                
	                String actionNoImageContent = desktopPortletDecoration.getProperty( HeaderResource.HEADER_CONFIG_DESKTOP_WINDOW_ACTION_NOIMAGE );
	                if ( actionNoImageContent != null && actionNoImageContent.length() > 0 )
	                {
	                	pOut.append( ( pOut.length() > 0 ) ? ", " : "" ).append( HeaderResource.DESKTOP_JSON_WINDOW_ACTION_NOIMAGE ).append( ": " ).append( actionNoImageContent );
	                }
	                
	                String actionMenuOrderContent = desktopPortletDecoration.getProperty( HeaderResource.HEADER_CONFIG_DESKTOP_WINDOW_ACTION_MENU_ORDER );
	                if ( actionMenuOrderContent != null && actionMenuOrderContent.length() > 0 )
	                {
	                	pOut.append( ( pOut.length() > 0 ) ? ", " : "" ).append( HeaderResource.DESKTOP_JSON_WINDOW_ACTION_MENU_ORDER ).append( ": " ).append( actionMenuOrderContent );
	                }
	                
	                String windowActionButtonTooltip = desktopPortletDecoration.getProperty( HeaderResource.HEADER_CONFIG_DESKTOP_WINDOW_ACTION_BUTTON_TOOLTIP );
	                if ( windowActionButtonTooltip != null && windowActionButtonTooltip.length() > 0 )
	                {
	                    pOut.append( ( pOut.length() > 0 ) ? ", " : "" ).append( HeaderResource.DESKTOP_JSON_WINDOW_ACTION_BUTTON_TOOLTIP ).append( ": " ).append( windowActionButtonTooltip );
	                }

	                String windowActionButtonMax = desktopPortletDecoration.getProperty( HeaderResource.HEADER_CONFIG_DESKTOP_WINDOW_ACTION_BUTTON_MAX );
	                if ( windowActionButtonMax != null && windowActionButtonMax.length() > 0 )
	                {
	                    pOut.append( ( pOut.length() > 0 ) ? ", " : "" ).append( HeaderResource.DESKTOP_JSON_WINDOW_ACTION_BUTTON_MAX ).append( ": " ).append( windowActionButtonMax );
	                }
	                
	                String iconEnabledContent = desktopPortletDecoration.getProperty( HeaderResource.HEADER_CONFIG_DESKTOP_WINDOW_ICON_ENABLED );
	                if ( iconEnabledContent != null && iconEnabledContent.length() > 0 )
	                {
	                	pOut.append( ( pOut.length() > 0 ) ? ", " : "" ).append( HeaderResource.DESKTOP_JSON_WINDOW_ICON_ENABLED ).append( ": " ).append( iconEnabledContent );
	                }
	                
	                String iconPathContent = desktopPortletDecoration.getProperty( HeaderResource.HEADER_CONFIG_DESKTOP_WINDOW_ICON_PATH );
	                if ( iconPathContent != null && iconPathContent.length() > 0 )
	                {
	                	pOut.append( ( pOut.length() > 0 ) ? ", " : "" ).append( HeaderResource.DESKTOP_JSON_WINDOW_ICON_PATH ).append( ": " ).append( iconPathContent ).append( ";" ).append( EOL );
	                }
	                
	                String titlebarEnabledContent = desktopPortletDecoration.getProperty( HeaderResource.HEADER_CONFIG_DESKTOP_WINDOW_TITLEBAR_ENABLED );
	                if ( titlebarEnabledContent != null && titlebarEnabledContent.length() > 0 )
	                {
	                	pOut.append( ( pOut.length() > 0 ) ? ", " : "" ).append( HeaderResource.DESKTOP_JSON_WINDOW_TITLEBAR_ENABLED ).append( ": " ).append( titlebarEnabledContent );
	                }
	                
	                String resizebarEnabledContent = desktopPortletDecoration.getProperty( HeaderResource.HEADER_CONFIG_DESKTOP_WINDOW_RESIZEBAR_ENABLED );
	                if ( resizebarEnabledContent != null && resizebarEnabledContent.length() > 0 )
	                {
	                	pOut.append( ( pOut.length() > 0 ) ? ", " : "" ).append( HeaderResource.DESKTOP_JSON_WINDOW_RESIZEBAR_ENABLED ).append( ": " ).append( resizebarEnabledContent );
	                }
	                
	                if ( pOut.length() > 0 )
	                {
	                	if ( pDecsOut.length() == 0 )
	                	{
	                		pDecsOut.append( DOJO_CONFIG_PORTLET_DECORATIONS_CONFIG_VAR_NAME ).append( " = { " );
	                	}
	                	else
	                	{
	                		pDecsOut.append( ", " );
	                	}
	                	pDecsOut.append( "\"" ).append( desktopPortletDecorationName ).append( "\": { " ).append( pOut.toString() ).append( " }" ).append( EOL );
	                }
	            }   // while ( desktopPortletDecorationsNamesIter.hasNext() )
	            if ( pDecsOut.length() > 0 )
	            {
	            	pDecsOut.append( " }" );
		            dojoConfigAddOn.append( EOL ).append( "    " ).append( pDecsOut.toString() ).append( ";" );
	            }
	            
	            dojoConfigContent = dojoConfigAddOn.toString();
	            setCachedContent( dojoConfigContentCacheKey, dojoConfigContent );		            
            }            
            
            if ( dojoConfigContent != null )
            {
            	hr.addHeaderSectionFragment( DOJO_CONFIG_LAYOUT_VAR_NAME, HeaderResource.HEADER_SECTION_DOJO_CONFIG, dojoConfigContent );
            }
            
            if ( inclStyleLayout )
            {
            	String contextPath = request.getRequest().getContextPath();
            	String styleLayoutContentCacheKey = (HeaderResource.HEADER_SECTION_DESKTOP_STYLE_LAYOUT + "." + layoutDecorationName + "." + contextPath);
            	String styleLayoutContent = getCachedContent( styleLayoutContentCacheKey );
            	if ( styleLayoutContent == null )
                {
            		String portletDecorationsBasePath = decorationFactory.getPortletDecorationsBasePath();
    	            String portletDecorationsBaseRelative = portletDecorationsBasePath;
    	            if ( portletDecorationsBaseRelative != null && portletDecorationsBaseRelative.length() > 1 && portletDecorationsBaseRelative.indexOf( '/' ) == 0 )
    	            {
    	                portletDecorationsBaseRelative = portletDecorationsBaseRelative.substring( 1 );
    	            }
	                StringBuffer desktopThemeStyleLink = new StringBuffer();
	                
	                int stylesheetCount = 0;
	                Iterator stylesheetIter = theme.getStyleSheets().iterator();
	                while ( stylesheetIter.hasNext() )
	                {
	                    String stylesheetHref = (String)stylesheetIter.next();
	                    if ( stylesheetHref != null && stylesheetHref.length() > 0 )
	                    {
	                        if ( ! stylesheetHref.startsWith( portletDecorationsBaseRelative ) )
	                        {   // exclude portlet decorations - in desktop these are loaded via javascript
	                            if ( stylesheetCount > 0 )
	                            {
	                                desktopThemeStyleLink.append( EOL );
	                            }
	                            
	                            desktopThemeStyleLink.append( "<link rel=\"stylesheet\" type=\"text/css\" media=\"screen, projection\" href=\"" );
	                            //desktopThemeStyleLink.append( desktopContext.getPortalResourceUrl( stylesheetHref ) ).append( "\"/>" );
	                            desktopThemeStyleLink.append( contextPath + "/" + stylesheetHref ).append( "\"/>" );
	                            
	                            stylesheetCount++;
	                        }
	                    }
	                }
	                styleLayoutContent = desktopThemeStyleLink.toString();
		            setCachedContent( styleLayoutContentCacheKey, styleLayoutContent );
                }
                if ( styleLayoutContent != null && styleLayoutContent.length() > 0 )
                {
                	hr.setHeaderSectionType( HeaderResource.HEADER_SECTION_DESKTOP_STYLE_LAYOUT, HeaderResource.HEADER_TYPE_LINK_TAG );
                	hr.addHeaderSectionFragment( "desktop.style.layout", HeaderResource.HEADER_SECTION_DESKTOP_STYLE_LAYOUT, styleLayoutContent );            	
                }
            }
            
            String layoutDecorationLocaleSuffix = "." + layoutDecorationName + "." + request.getLocale().toString();
            String desktopActionLabelsCacheKey = DOJO_CONFIG_ACTION_LABELS_NAME + layoutDecorationLocaleSuffix;
            String pageEditorLabelsCacheKey = DOJO_CONFIG_PAGEEDITOR_LABELS_NAME + layoutDecorationLocaleSuffix;
            String pageEditorDialogLabelsCacheKey = DOJO_CONFIG_PAGEEDITOR_DIALOG_LABELS_NAME + layoutDecorationLocaleSuffix;
            
            String desktopActionLabelsContent = getCachedContent( desktopActionLabelsCacheKey );
            String pageEditorLabelsContent = getCachedContent( pageEditorLabelsCacheKey );
            String pageEditorDialogLabelsContent = getCachedContent( pageEditorDialogLabelsCacheKey );
            if ( desktopActionLabelsContent == null || pageEditorLabelsContent == null || pageEditorDialogLabelsContent == null )
            {
            	ResourceBundle messages = desktopContext.getLayoutResourceBundle( request.getLocale() );            	
            	if ( desktopActionLabelsContent == null )
            	{
            		desktopActionLabelsContent = getResourcesAsJavascriptObject( DESKTOP_ACTION_RESOURCE_NAME_PREFIX, DESKTOP_ACTION_RESOURCE_NAMES, messages, DOJO_CONFIG_ACTION_LABELS_NAME, "    ", true );
            		setCachedContent( desktopActionLabelsCacheKey, desktopActionLabelsContent );
            	}
            	if ( pageEditorLabelsContent == null )
            	{
            		pageEditorLabelsContent = getResourcesAsJavascriptObject( DESKTOP_PAGEEDITOR_RESOURCE_NAME_PREFIX, DESKTOP_PAGEEDITOR_RESOURCE_NAMES, messages, DOJO_CONFIG_PAGEEDITOR_LABELS_NAME, "    ", true );
            		setCachedContent( pageEditorLabelsCacheKey, pageEditorLabelsContent );
            	}
            	if ( pageEditorDialogLabelsContent == null )
            	{
            		pageEditorDialogLabelsContent = getResourcesAsJavascriptObject( DESKTOP_PAGEEDITOR_DIALOG_RESOURCE_NAME_PREFIX, DESKTOP_PAGEEDITOR_DIALOG_RESOURCE_NAMES, messages, DOJO_CONFIG_PAGEEDITOR_DIALOG_LABELS_NAME, "    ", true );
            		setCachedContent( pageEditorDialogLabelsCacheKey, pageEditorDialogLabelsContent );
            	}
            }
            if ( desktopActionLabelsContent != null && desktopActionLabelsContent.length() > 0 )
            {
                hr.addHeaderSectionFragment( DOJO_CONFIG_ACTION_LABELS_NAME, HeaderResource.HEADER_SECTION_DOJO_CONFIG, desktopActionLabelsContent );
            }
            if ( pageEditorLabelsContent != null && pageEditorLabelsContent.length() > 0 )
            {
                hr.addHeaderSectionFragment( DOJO_CONFIG_PAGEEDITOR_LABELS_NAME, HeaderResource.HEADER_SECTION_DOJO_CONFIG, pageEditorLabelsContent );
            }
            if ( pageEditorDialogLabelsContent != null && pageEditorDialogLabelsContent.length() > 0 )
            {
                hr.addHeaderSectionFragment( DOJO_CONFIG_PAGEEDITOR_DIALOG_LABELS_NAME, HeaderResource.HEADER_SECTION_DOJO_CONFIG, pageEditorDialogLabelsContent );
            }
            
            dispatcher.include( request.getRequest(), request.getResponse() );
        }
        catch ( Exception e )
        {
            try
            {
                if ( layoutDecorationTemplatePathWasAssigned )
                {
                    layoutDecorationTemplatePath = ( layoutDecorationTemplatePath == null || layoutDecorationTemplatePath.length() == 0 ? "null" : layoutDecorationTemplatePath );
                    log.error( "Failed to include desktop layout decoration at path " + layoutDecorationTemplatePath, e );
                    request.getResponse().getWriter().println( "Desktop layout decoration " + layoutDecorationTemplatePath + " is not available" );
                }
                else
                {
                    log.error( "Failed to initialize for inclusion of desktop layout decoration", e );
                    request.getResponse().getWriter().println( "Failed to initialize for inclusion of desktop layout decoration" );
                }
            }
            catch ( IOException ioe )
            {
                log.error( "Failed to write desktop layout decoration exception information to servlet output writer", ioe );
            }
        }
    }

    private String getCachedContent( String cacheKey )
    {
    	CacheElement cachedElement = desktopContentCache.get(cacheKey);
        if (cachedElement != null)
         return (String)cachedElement.getContent();  
        return null;
    }
    private void setCachedContent( String cacheKey, String content )
    {
    	CacheElement cachedElement = desktopContentCache.createElement( cacheKey, content );
    	cachedElement.setTimeToIdleSeconds(desktopContentCache.getTimeToIdleSeconds());
    	cachedElement.setTimeToLiveSeconds(desktopContentCache.getTimeToLiveSeconds());
    	desktopContentCache.put( cachedElement );
    }

    private String getResourcesAsJavascriptObject( String resourceNamePrefix, String[] resourceNames, ResourceBundle messages, String varName, String indent, boolean ifEmptyReturnEmptyString )
    {
    	StringBuffer jsObjBuffer = new StringBuffer();
        boolean atLeastOneFound = false;
        if ( indent != null )
        	jsObjBuffer.append( indent );
        if ( varName != null )
        	jsObjBuffer.append( varName ).append( " = " );
        jsObjBuffer.append( "{ " );
        for ( int i = 0 ; i < resourceNames.length ; i++ )
        {
            String resourceValue = null;
        	try
        	{
        		resourceValue = messages.getString( resourceNamePrefix + resourceNames[ i ] );
        	}
        	catch ( java.util.MissingResourceException ex ) { }
            if ( resourceValue != null )
            {
                if ( atLeastOneFound )
                {
                    jsObjBuffer.append( ", " );
                }
                else
                {
                	atLeastOneFound = true;
                }
                jsObjBuffer.append( resourceNames[ i ] ).append( ": \"" ).append( resourceValue ).append( "\"" );
            }
        }
        jsObjBuffer.append( " };" );
        if ( ! atLeastOneFound && ifEmptyReturnEmptyString )
        	return "";
        return jsObjBuffer.toString();
    }
    
    public boolean isDesktopEnabled( RequestContext requestContext )
    {
        return this.decorationFactory.isDesktopEnabled( requestContext );
    }
    
    public ServletContext getServletContext()
    {
        return servletContext;
    }

    public void setServletContext(ServletContext servletContext)
    {
        this.servletContext = servletContext;
    }
    
    public HeaderResourceFactory getHeaderResourceFactory()
    {
        return this.headerResourceFactory;
    }
    
    // get portal urls - each of these methods is copied from HeaderResourceImpl.java
 
    /**
     * Desktop servlet path ( e.g. /desktop )
     * 
     * @return portal base url
     */
    public String getDesktopServletPath()
    {
        return this.desktopServletPath;
    }
    
    /**
     * Portal base url ( e.g. http://localhost:8080/jetspeed )
     * 
     * @return portal base url
     */
    public String getPortalBaseUrl( RequestContext context )
    {
        return HeaderResourceLib.getPortalBaseUrl( context, this.baseUrlAccess );
    }
    
    /**
     * Portal base url ( e.g. http://localhost:8080/jetspeed )
     * 
     * @return portal base url
     */
    public String getPortalBaseUrl( RequestContext context, boolean encode )
    {
        String baseurl = getPortalBaseUrl( context );
        if ( ! encode )
        {
            return baseurl;
        }
        else
        {
            return context.getResponse().encodeURL( baseurl );
        }
    }
    
    /**
     * Portal base url with relativePath argument appended ( e.g. http://localhost:8080/jetspeed/javascript/dojo/ )
     * 
     * @return portal base url with relativePath argument appended
     */
    public String getPortalResourceUrl( RequestContext context, String relativePath )
    {
        return getPortalResourceUrl( context, relativePath, false );
    }
    
    /**
     * Portal base url with relativePath argument appended ( e.g. http://localhost:8080/jetspeed/javascript/dojo/ )
     * 
     * @return portal base url with relativePath argument appended
     */
    public String getPortalResourceUrl( RequestContext context, String relativePath, boolean encode )
    {
        return HeaderResourceLib.getPortalResourceUrl( relativePath, getPortalBaseUrl( context ), encode, context );
    }
    
    /**
     * Portal base servlet url ( e.g. http://localhost:8080/jetspeed/desktop/ )
     * 
     * @return portal base servlet url
     */
    public String getPortalUrl( RequestContext context )
    {
        return HeaderResourceLib.getPortalUrl( getPortalBaseUrl( context ), context, getDesktopServletPath() );
    }
    
    /**
     * Portal base servlet url ( e.g. http://localhost:8080/jetspeed/desktop/ )
     * 
     * @return portal base servlet url
     */
    public String getPortalUrl( RequestContext context, boolean encode )
    {
        return getPortalUrl( context, null, encode );
    }
    
    /**
     * Portal base servlet url with relativePath argument appended ( e.g. http://localhost:8080/jetspeed/desktop/default-page.psml )
     * 
     * @return portal base servlet url with relativePath argument appended
     */
    public String getPortalUrl( RequestContext context, String relativePath )
    {
        return getPortalUrl( context, relativePath, false );
    }
    
    /**
     * Portal base servlet url with relativePath argument appended ( e.g. http://localhost:8080/jetspeed/desktop/default-page.psml )
     * 
     * @return portal base servlet url with relativePath argument appended
     */
    public String getPortalUrl( RequestContext context, String relativePath, boolean encode )
    {
        return HeaderResourceLib.getPortalResourceUrl( relativePath, getPortalUrl( context ), encode, context );
    }
}
    
