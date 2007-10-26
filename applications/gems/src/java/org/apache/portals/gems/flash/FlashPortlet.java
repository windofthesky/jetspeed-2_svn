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
package org.apache.portals.gems.flash;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Collections;
import java.util.Properties;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.portlet.PortletConfig;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;

import org.apache.velocity.context.Context;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.Template;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.desktop.JetspeedDesktop;
import org.apache.jetspeed.headerresource.HeaderResourceLib;
import org.apache.jetspeed.request.RequestContext;

public class FlashPortlet extends GenericVelocityPortlet
{
    public static final String HEIGHT_PREF = "HEIGHT";
    public static final String WIDTH_PREF = "WIDTH";
    public static final String SRC_PREF = "SRC";
    public static final String MAX_SRC_PREF = "MAX-SRC";
    public static final String MAX_HEIGHT_PREF = "MAX-HEIGHT";
    public static final String MAX_WIDTH_PREF = "MAX-WIDTH";
 
    public static final String OBJECT_PARAMS_INITPARAM = "object-params";
    public static final String OBJECT_ATTRIBUTES_INITPARAM = "object-attributes";
    public static final String FLASHVARS_INITPARAM = "flashvars";
    public static final String VIEW_PAGE_INITPARAM = "ViewPage";

    public static final String PARAM_VIEW_PAGE = "ViewPage";
    public static final String PARAM_EDIT_PAGE = "EditPage";
    public static final String PARAM_VIEW_PAGE_DEFAULT = "org/apache/portals/gems/flash/templates/flash-demo.vm";
    public static final String PARAM_EDIT_PAGE_DEFAULT = "org/apache/portals/gems/flash/templates/edit-prefs.vm";
    
    public static final String CODEBASE = "codebase";
    public static final String CLASSID = "classid";
    public static final String NODEID = "id";
    
    public static final String SRC = "SRC";
    public static final String WIDTH = "WIDTH";
    public static final String HEIGHT = "HEIGHT";

    public static final String WIDTH_ACTUAL = "widthActual";
    public static final String HEIGHT_ACTUAL = "heightActual";
    public static final String HEIGHT_PERCENT = "heightPercent";
    
    public static final String OBJECT_PARAMS = "PARAMS";
    public static final String OBJECT_ATTRIBUTES = "ATTRIBUTES";
    public static final String FLASHVARS = "FLASHVARS";
    public static final String EXTRA_SIZE_INFO = "EXTRA_SIZE_INFO";
    
    public static final String WINDOW_STATE = "windowState";
    public static final String NAMESPACE = "NAMESPACE";
    public static final String REPLACECONTENT_NODEID = "REPLACECONTENT_NODEID";
    public static final String SWF_VERSION = "SWF_VERSION";
    public static final String SWF_VERSION_DEFAULT = "9.0.0";
    
    public static final String SWFOBJECTS_LIB_URL = "SWFOBJECTS_URL";
    public static final String EXPRESS_INSTALL_URL = "EXPRESS_INSTALL_URL";
    
    public static final String IS_DESKTOP = "IS_DESKTOP";

    protected Log log = LogFactory.getLog( FlashPortlet.class );
    
    protected String viewPage = null;
    protected String editPage = null;

    private Map object_parameters = null;
    private Map object_attributes = null;    
    private Map flash_vars = null;

    private VelocityEngine engine;


	public void init( PortletConfig config ) throws PortletException
    { 
        super.init(config);

        String viewPage = config.getInitParameter( PARAM_VIEW_PAGE );
        if ( viewPage == null || viewPage.length() == 0 )
            viewPage = null;
        this.viewPage = viewPage;

        String editPage = config.getInitParameter( PARAM_EDIT_PAGE );
        if ( editPage == null || editPage.length() == 0 )
            editPage = null;
        this.editPage = editPage;
                
        Map objParams = parseSemicolonEqualsDelimitedProps( config.getInitParameter( OBJECT_PARAMS_INITPARAM ) );
        Map objAttrs = parseSemicolonEqualsDelimitedProps( config.getInitParameter( OBJECT_ATTRIBUTES_INITPARAM ) );
        Map flashVars = parseSemicolonEqualsDelimitedProps( config.getInitParameter( FLASHVARS_INITPARAM ) );
        
        if ( objAttrs != null )
        {
        	objAttrs.remove( CODEBASE );
        	objAttrs.remove( CLASSID );
        	objAttrs.remove( NODEID );
        	this.object_attributes = Collections.unmodifiableMap( objAttrs );
        }
        
        if ( objParams != null )
        {
        	objParams.remove( CODEBASE );
        	objParams.remove( CLASSID );
        	objParams.remove( NODEID );
        	this.object_parameters = Collections.unmodifiableMap( new HashMap( objParams ) );
        }
        
        if ( flashVars != null )
        	this.flash_vars = Collections.unmodifiableMap( flashVars );
    }
	
	protected final Map getDefaultObjectParameters()
	{
		return this.object_parameters;
	}
	protected final Map getDefaultObjectAttributes()
	{
		return this.object_attributes;
	}
	protected final Map getDefaultFlashVars()
	{
		return this.flash_vars;
	}
	protected String getDefaultSwfVersion()
	{
		return SWF_VERSION_DEFAULT;
	}
	
	protected Map getObjectParameters( RenderRequest request, RenderResponse response, SWFContext swfContext )
	{
		return this.object_parameters;
	}
	protected Map getObjectAttributes( RenderRequest request, RenderResponse response, SWFContext swfContext )
	{
		return this.object_attributes;
	}
	protected Map getFlashVars( RenderRequest request, RenderResponse response, SWFContext swfContext )
	{
		return this.flash_vars;
	}
	
	protected void setContextVars( RenderRequest request, RenderResponse response, Context context, SWFContext swfContext )
	{
		setParameterContextVars( request, response, context, swfContext );
		readSwfFileInfo( request, response, context, swfContext );
		setSizeContextVars( request, response, context, swfContext );
		setFinalContextVars( request, response, context, swfContext );	
	}
	
	protected void setFinalContextVars( RenderRequest request, RenderResponse response, Context context, SWFContext swfContext )
	{
		String namespace = response.getNamespace();
		context.put( NAMESPACE, namespace );
		context.put( REPLACECONTENT_NODEID, namespace + "_flash_replace" );
		
		RequestContext requestContext = (RequestContext)request.getAttribute( PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE );

        String portalBaseUrl = HeaderResourceLib.getPortalBaseUrl( requestContext );
		context.put( SWFOBJECTS_LIB_URL, portalBaseUrl + "/javascript/swfobject/swfobject.js" );
		context.put( EXPRESS_INSTALL_URL, portalBaseUrl + "/javascript/swfobject/expressInstall.swf" );
		
        Boolean desktopEnabled = (Boolean)requestContext.getAttribute( JetspeedDesktop.DESKTOP_ENABLED_REQUEST_ATTRIBUTE );
		context.put( IS_DESKTOP, ( (desktopEnabled == null) ? Boolean.FALSE : desktopEnabled ) );
		
		int swfVersion = ( swfContext.getHeader() != null ? swfContext.getHeader().getVersion() : -1 );
		if ( swfVersion > 0 )
		{
			context.put( SWF_VERSION, new Integer( swfVersion ).toString() + ".0.0" );
		}
		else
		{
			context.put( SWF_VERSION, getDefaultSwfVersion() );
		}
		context.put( SRC, swfContext.getSrc() );
	}
	
	protected void setParameterContextVars( RenderRequest request, RenderResponse response, Context context, SWFContext swfContext )
	{
		context.put( OBJECT_PARAMS, HeaderResourceLib.makeJSONObject( getObjectParameters( request, response, swfContext ), true ).toString() );
		
		Map objNodeIdMap = new HashMap();
		objNodeIdMap.put( "id", response.getNamespace() + "_objnode" );
		Map[] attrMaps = new Map[] { getObjectAttributes( request, response, swfContext ), objNodeIdMap };
		context.put( OBJECT_ATTRIBUTES, HeaderResourceLib.makeJSONObject( attrMaps, true ).toString() );
		
		context.put( FLASHVARS, HeaderResourceLib.makeJSONObject( getFlashVars( request, response, swfContext ), true ).toString() );
	}
	
	protected void readSwfFileInfo( RenderRequest request, RenderResponse response, Context context, SWFContext swfContext )
	{
        String swfSrc = swfContext.getSrc();
        int swfSrcLen = ( swfSrc != null ? swfSrc.length() : 0 );
        if ( swfSrcLen > 0 )
        {
        	SWFHeader swfH = new SWFHeader();
            String contextPath = request.getContextPath();
            int contextPathLen = ( contextPath != null ? contextPath.length() : 0 );
            if ( contextPathLen > 0 && swfSrcLen > contextPathLen && swfSrc.startsWith( contextPath ) )
            {
            	swfSrc = swfSrc.substring( contextPathLen );
            }
            if ( swfH.parseHeader( this.getPortletContext().getResourceAsStream( swfSrc ) ) )
            {
            	swfContext.setHeader( swfH );
            }
        }
	}
	
	protected void setSizeContextVars( RenderRequest request, RenderResponse response, Context context, SWFContext swfContext )
	{
		String swfHeight = swfContext.getHeight();
		String swfWidth = swfContext.getWidth();
		String swfHeightActual = null;
		String swfWidthActual = null;
		SWFHeader header = swfContext.getHeader();
		if ( header != null )
		{
			if ( header.getHeight() > 0 )
				swfHeightActual = new Integer( header.getHeight() ).toString();
        	if ( header.getWidth() > 0 )
        		swfWidthActual = new Integer( header.getWidth() ).toString();
		}

		boolean isMaximized = swfContext.isMaximized();
		if ( swfHeight == null )
		{
			if ( swfHeightActual != null )
				swfHeight = swfHeightActual;
			else
				swfHeight = ( isMaximized ? "800" : "250" );
			swfContext.setHeight( swfHeight );
		}
		if ( swfWidth == null )
		{
			swfWidth = "100%";    // ( isMaximized ? "600" : "250" );
			swfContext.setWidth( swfWidth );
		}
		context.put( HEIGHT, swfHeight );
        context.put( WIDTH, swfWidth );
        
        Map extraSizeVars = new HashMap();
        if ( swfHeightActual != null )
        	extraSizeVars.put( HEIGHT_ACTUAL, swfHeightActual );
        if ( swfWidthActual != null )
        	extraSizeVars.put( WIDTH_ACTUAL, swfWidthActual );
        
        String heightPercent = swfContext.getHeightPercentage();
        if ( heightPercent != null )
        	extraSizeVars.put( HEIGHT_PERCENT, heightPercent );
        
        context.put( EXTRA_SIZE_INFO, HeaderResourceLib.makeJSONObject( extraSizeVars, true ).toString() );
	}
       
    public void doView(RenderRequest request, RenderResponse response)
        throws PortletException, IOException
    {
        Context context = super.getContext(request);
        PortletPreferences prefs = request.getPreferences();
        
        String swfSrc = null;
        String swfHeight = null;
        String swfWidth = null;
        boolean isMaximized = false;
        
        if ( request.getWindowState().toString().equals( WindowState.MAXIMIZED.toString() ) )
        {
        	isMaximized = true;
        	swfHeight = prefs.getValue( MAX_HEIGHT_PREF, null );
            swfWidth = prefs.getValue( MAX_WIDTH_PREF, null );
            swfSrc = prefs.getValue( MAX_SRC_PREF, null );
        }
        
        if ( swfHeight == null || swfHeight.length() == 0 )
        	swfHeight = prefs.getValue( HEIGHT_PREF, null );
        
        if ( swfWidth == null || swfWidth.length() == 0 )
        	swfWidth = prefs.getValue( WIDTH_PREF, null );
        
        if ( swfSrc == null || swfSrc.length() == 0 )
        	swfSrc = prefs.getValue( SRC_PREF, null );

        context.put( WINDOW_STATE, ( (! isMaximized) ? "normal" : "max" ) );
        
        SWFContext swfContext = new SWFContext( swfSrc, swfHeight, swfWidth, isMaximized );
        setContextVars( request, response, context, swfContext );
        
        if ( this.viewPage != null )
        {
            super.doView( request, response );
        }
        else
        {
            processClasspathTemplate( PARAM_VIEW_PAGE_DEFAULT, context, response );
        }
    }

    public void doEdit( RenderRequest request, RenderResponse response ) throws PortletException, IOException
    {
        if ( this.editPage != null )
        {
            response.setContentType("text/html");
            doPreferencesEdit(request, response);
        }
        else
        {
            setupPreferencesEdit( request, response );
            processClasspathTemplate( PARAM_EDIT_PAGE_DEFAULT, getContext( request ), response );
        }
    }

    protected void processClasspathTemplate( String classpathTemplate, Context context, RenderResponse response ) throws PortletException
    {
        response.setContentType("text/html");
        VelocityEngine vEngine = null;
        synchronized ( this )
        {
            vEngine = this.engine;
            if ( vEngine == null )
            {
                vEngine = new VelocityEngine();
                configureClasspathVelocityEngine( vEngine );
                this.engine = vEngine;
            }
        }

        try
        {
            Template template = vEngine.getTemplate( classpathTemplate );
        
            StringWriter writer = new StringWriter();
            template.merge( context, writer );
            writer.close();
            
            response.getPortletOutputStream().write( writer.getBuffer().toString().getBytes() );
            response.getPortletOutputStream().flush();
        }
        catch ( Exception ex )
        {
            String errMsg = "Failed to generate content with classpath based VelocityEngine for " + this.getClass().getName() + " due to " + ex.getClass().getName() + " " + ex.getMessage();
            log.error( errMsg );
            throw new PortletException( errMsg );
        }
    }

    protected void configureClasspathVelocityEngine( VelocityEngine vEngine ) throws PortletException
    {
        try
        {
            Properties props = new Properties();
            props.setProperty( VelocityEngine.RESOURCE_LOADER, "classpath" );
            props.setProperty( "classpath." + VelocityEngine.RESOURCE_LOADER + ".class", ClasspathResourceLoader.class.getName() );
            vEngine.init( props );
        }
        catch ( Exception ex )
        {
            String errMsg = "Failed to configure classpath based VelocityEngine for " + this.getClass().getName() + " due to " + ex.getClass().getName() + " " + ex.getMessage();
            log.error( errMsg );
            throw new PortletException( errMsg );
        }
    }


    /* (non-Javadoc)
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#processAction(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException
    {
        String source = request.getParameter(SRC_PREF);
        String height = request.getParameter(HEIGHT_PREF);
        String width = request.getParameter(WIDTH_PREF);
        String maxSource = request.getParameter(MAX_SRC_PREF);
        String maxHeight = request.getParameter(MAX_HEIGHT_PREF);
        String maxWidth = request.getParameter(MAX_WIDTH_PREF);
        
        PortletPreferences prefs = request.getPreferences();
        prefs.setValue(SRC_PREF, source);
        prefs.setValue(HEIGHT_PREF, height);
        prefs.setValue(WIDTH_PREF, width);
        prefs.setValue(MAX_SRC_PREF, maxSource);
        prefs.setValue(MAX_HEIGHT_PREF, maxHeight);
        prefs.setValue(MAX_WIDTH_PREF, maxWidth);        
        prefs.store();
        super.processAction(request, response);
    }
    
    public Map parseSemicolonEqualsDelimitedProps( String propsStr )
    {
        if ( propsStr == null || propsStr.length() == 0 )
            return null;
        Map props = new HashMap();
        StringTokenizer parser = new StringTokenizer( propsStr, ";" );
        String token, propNm, propVal;
        int eqPos;
        while ( parser.hasMoreTokens() )
        {
            token = parser.nextToken();
            eqPos = token.indexOf( '=' );
            if ( eqPos > 0 )
            {
            	propNm = token.substring( 0, eqPos );
            	if ( eqPos < (token.length() -1) )
            	{
            		propVal = token.substring( eqPos + 1 );
            		props.put( propNm.toLowerCase(), propVal );
            	}
            }
        }
        return props;
    }
    
    protected class SWFContext
    {
    	private String src;
    	private SWFHeader header;
    	private String height;
    	private String height_percentage;
    	private String width;
    	private boolean is_maximized;
    	public SWFContext( String swfSrc, String swfHeight, String swfWidth, boolean isMaximized )
    	{
    		setSrc( swfSrc );
    		setHeight( swfHeight );
    		setWidth( swfWidth );
    		this.is_maximized = isMaximized;
    	}
		public String getSrc()
		{
        	return src;
        }
		public void setSrc( String src )
		{
			if ( src == null || src.length() == 0 )
				src = null;
        	this.src = src;
        }
		public SWFHeader getHeader()
		{
        	return header;
        }
		public void setHeader( SWFHeader swfHeader )
		{
        	this.header = swfHeader;
        }
		public String getHeight()
		{
        	return height;
        }
		public void setHeight( String height )
		{
			if ( height == null || height.length() == 0 )
				height = null;
			else
			{
				height = height.trim();
				if ( height.endsWith("%") )
				{
					if ( height.length() > 1 )
					{
						this.height_percentage = height;
					}
					height = null;
				}
			}
        	this.height = height;
        }
		public String getHeightPercentage()
		{
        	return height_percentage;
        }
		public String getWidth()
		{
        	return width;
        }
		public void setWidth( String width )
		{
			if ( width == null || width.length() == 0 )
				width = null;
        	this.width = width;
        }
		public boolean isMaximized()
		{
        	return is_maximized;
        }
    }
}
