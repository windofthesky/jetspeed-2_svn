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
package org.apache.portals.gems.dojo;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.headerresource.HeaderResource;
import org.apache.jetspeed.portlet.PortletHeaderRequest;
import org.apache.jetspeed.portlet.PortletHeaderResponse;
import org.apache.jetspeed.portlet.SupportsHeaderPhase;
import org.apache.pluto.core.impl.RenderRequestImpl;
import org.apache.pluto.core.impl.RenderResponseImpl;
import org.apache.portals.gems.util.HttpBufferedResponse;
import org.apache.portals.gems.util.PortletContentImpl;
import org.springframework.beans.BeansException;
import org.springframework.web.portlet.DispatcherPortlet;


/**
 * Abstract DOJO portlet for inserting in cross context dojo widget includes
 * 
 * @author <a href="mailto:smilek@apache.org">Steve Milek</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a> 
 * @version $Id: $
 */
public class DojoSpringMVCPortlet extends DispatcherPortlet implements SupportsHeaderPhase
{
	protected static final String CRLF = "\r\n";

    /*
     * Class specific logger.
     */
    private final static Log log = LogFactory.getLog(DojoSpringMVCPortlet.class);

    protected String headerPage;
    
    /*
     * Portlet constructor.
     */
    public DojoSpringMVCPortlet() 
    {
        super();
    }
    
    protected void includeDojoConfig(PortletHeaderRequest request, PortletHeaderResponse response, String portalContextPath, StringBuffer headerInfoText)
		throws PortletException
	{	
    	headerInfoText.append( "var djConfig = { " );
    	headerInfoText.append( "isDebug: true, debugAtAllCosts: false" );
    	headerInfoText.append( ", baseScriptUri: '" );
    	headerInfoText.append( portalContextPath ).append( "/javascript/dojo/" );
    	headerInfoText.append( "'" );
    	headerInfoText.append( " };" ).append( CRLF );
	}
    
    protected String getDojoJSPath( String portalContextPath )
    {
    	return portalContextPath + "/javascript/dojo/dojo.js";
    }
	
    protected void includeDojoRequires(PortletHeaderRequest request, PortletHeaderResponse response, StringBuffer headerInfoText)
    	throws PortletException
    {
    	if ( this.headerPage != null )
    	{
    		include( request, response, this.headerPage, headerInfoText );
    	}
    }
    protected void includeDojoWidgetRequires(PortletHeaderRequest request, PortletHeaderResponse response, StringBuffer headerInfoText)
        throws PortletException
    {

    }
    protected void includeDojoCustomWidgetRequires(PortletHeaderRequest request, PortletHeaderResponse response, StringBuffer headerInfoText)
        throws PortletException
    {
        
    }
    
    protected void includeDojoWriteIncludes(PortletHeaderRequest request, PortletHeaderResponse response, StringBuffer headerInfoText)
        throws PortletException
    {
    	headerInfoText.append( "dojo.hostenv.writeIncludes();" ).append( CRLF );
    }
    protected void includeDojoRegisterWidgetPackage(PortletHeaderRequest request, PortletHeaderResponse response, StringBuffer headerInfoText)
        throws PortletException
    {
        headerInfoText.append( "dojo.widget.manager.registerWidgetPackage('jetspeed.ui.widget');" ).append( CRLF );
    }
    
    protected boolean addJavascriptBlock(HeaderResource headerResource, StringBuffer javascriptText)
    {
    	return addJavascriptElement( headerResource, null, javascriptText );
    }
    protected boolean addJavascriptInclude(HeaderResource headerResource, String src)
    {
    	return addJavascriptElement( headerResource, src, null );
    }
    protected boolean addJavascriptElement(HeaderResource headerResource, String src, StringBuffer javascriptText)
    {
    	if ( ( javascriptText != null && javascriptText.length() > 0 ) || ( src != null && src.length() > 0 ) )
    	{
    		Map headerInfoMap = new HashMap(8);
    		headerInfoMap.put("language", "JavaScript");
    		headerInfoMap.put("type", "text/javascript");
    		if ( src != null && src.length() > 0 )
    		{
    			headerInfoMap.put("src", src);
    			headerResource.addHeaderInfo("script", headerInfoMap, "");
    		}
    		else
    		{
    			headerResource.addHeaderInfo("script", headerInfoMap, CRLF + javascriptText.toString());
    		}
    		return true ;
    	}
    	return false ;
    }
    
    

    /*
     * Portlet lifecycle method.
     */
    protected void initFrameworkPortlet() throws PortletException, BeansException
    {
        super.initFrameworkPortlet();

        // access jetspeed heaader resource component
        synchronized (this) 
        {
            this.headerPage = this.getInitParameter("HeaderPage");
        }
    }

    /* (non-Javadoc)
     * @see javax.portlet.GenericPortlet#doDispatch(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
     */
    protected void doRenderService(RenderRequest request, RenderResponse response) throws Exception
    {
        // dispatch normally
        super.doRenderService(request, response);
    }

    /*
     * Include Dojo header content using header resource component.
     *
     * @param request render request
     * @param response render response
     */    
    public void doHeader(PortletHeaderRequest request, PortletHeaderResponse response)    
    throws PortletException
    {
        String portalContextPath = request.getPortalContextPath();

        // use header resource component to ensure header logic is included only once
        HeaderResource headerResource = response.getHeaderResource();
        StringBuffer headerInfoText = new StringBuffer();
        Map headerInfoMap = null;
        boolean isJetspeedDesktop = request.isDesktopEncoder();
        boolean addedDojoRequires = false;
        
        
        // add dojo if not already in use as desktop
        if (!isJetspeedDesktop) 
        {
            // dojo configuration
            headerInfoText.setLength(0);
            includeDojoConfig( request, response, portalContextPath, headerInfoText );
            addJavascriptBlock( headerResource, headerInfoText );
            
            // dojo script
            addJavascriptInclude( headerResource, getDojoJSPath( portalContextPath ) );
            
            // dojo includes
            headerInfoText.setLength(0);
            includeDojoRequires( request, response, headerInfoText );
            if ( addJavascriptBlock( headerResource, headerInfoText ) )
            {
            	addedDojoRequires = true;
            }
            	
            headerInfoText.setLength(0);
            includeDojoWidgetRequires( request, response, headerInfoText );
            if ( addJavascriptBlock( headerResource, headerInfoText ) )
            {
            	addedDojoRequires = true;
            }
            
            headerInfoText.setLength(0);
            includeDojoCustomWidgetRequires( request, response, headerInfoText );
            if ( addJavascriptBlock( headerResource, headerInfoText ) )
            {
            	addedDojoRequires = true;
            }
        }
        
        // close DOJO if not already in use as desktop
        if (!isJetspeedDesktop) 
        {
            // complete dojo includes
        	if ( addedDojoRequires )
        	{
        		headerInfoText.setLength(0);
        		includeDojoWriteIncludes( request, response, headerInfoText );
        		addJavascriptBlock( headerResource, headerInfoText );
        	}
        
            headerInfoText.setLength(0);
            includeDojoRegisterWidgetPackage( request, response, headerInfoText );
            addJavascriptBlock( headerResource, headerInfoText );        
        }
        
        if (!isJetspeedDesktop)
        {
            headerInfoText.setLength(0);
            headerInfoText.append("\r\n");
            headerInfoText.append("html, body\r\n");
            headerInfoText.append("{\r\n");
            headerInfoText.append("   width: 100%;\r\n");
            headerInfoText.append("   height: 100%;\r\n");
            headerInfoText.append("   margin: 0 0 0 0;\r\n");
            headerInfoText.append("}\r\n");
            headerInfoMap = new HashMap(8);
            headerResource.addHeaderInfo("style", headerInfoMap, headerInfoText.toString());
        }
    }
    
    
    public void include(PortletHeaderRequest request, PortletHeaderResponse response, String headerPagePath, StringBuffer headerText) throws PortletException
    {
        response.include(request, response, headerPagePath);
        headerText.append(response.getContent());
    }


	// debugging

	protected void dumpAttributes(RenderRequest request)
	{
		Enumeration attrIter = request.getAttributeNames();
		log.info( "request-attributes:");
		while ( attrIter.hasMoreElements() )
		{
			Object attrNm = attrIter.nextElement();
			Object attrVal = request.getAttribute(ObjectUtils.toString(attrNm));
			String attrValDesc = ( attrVal instanceof String ) ? (String)attrVal : (( attrVal == null ) ? "null" : attrVal.getClass().getName() );
			log.info( "   key=" + ObjectUtils.toString(attrNm,"null") + " value=" + attrValDesc);
		}
	}
	protected void dumpSession(RenderRequest request)
	{
		Enumeration attrIter = request.getPortletSession().getAttributeNames();
		log.info( "session-attributes:");
		while ( attrIter.hasMoreElements() )
		{
			Object attrNm = attrIter.nextElement();
			Object attrVal = request.getPortletSession().getAttribute(ObjectUtils.toString(attrNm));
			String attrValDesc = ( attrVal instanceof String ) ? (String)attrVal : (( attrVal == null ) ? "null" : attrVal.getClass().getName() );
			log.info( "   key=" + ObjectUtils.toString(attrNm,"null") + " value=" + attrValDesc);
		}
	}
	protected void dumpNameValue( Map m  )
	{
		if ( m == null )
		{
			log.info( "   <null>" );
			return;
		}
		if ( m.size() == 0 )
		{
			log.info( "   <empty>" );
			return;
		}
		Iterator entryIter = m.entrySet().iterator();
		while ( entryIter.hasNext() )
		{
			Map.Entry e = (Map.Entry)entryIter.next();
			Object eKey = e.getKey();
			Object eVal = e.getValue();
			String eKeyDesc = ( eKey instanceof String ) ? (String)eKey : (( eKey == null ) ? "null" : eKey.getClass().getName() );
			String eValDesc = ( eVal instanceof String ) ? (String)eVal : (( eVal == null ) ? "null" : eVal.getClass().getName() );
			log.info( "   key=" + eKeyDesc + " value=" + eValDesc);
		}
	}
}
