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
    
    protected static final String DOJO_REQUIRES_CORE_INIT_PARAM = "dojo.requires.core";
    protected static final String DOJO_REQUIRES_MODULES_INIT_PARAM = "dojo.requires.modules";
    
    private String dojoRequiresCoreList = null;
    private String dojoRequiresAddOnList = null;
    
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
            this.dojoRequiresCoreList = this.getInitParameter( DOJO_REQUIRES_CORE_INIT_PARAM );
            this.dojoRequiresAddOnList = this.getInitParameter( DOJO_REQUIRES_MODULES_INIT_PARAM );
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
        // use header resource component to ensure header logic is included only once
        HeaderResource headerResource = response.getHeaderResource();
        
        headerResource.dojoEnable();
        includeHeaderContent( headerResource );
        
        /*if ( this.headerPage != null )
        {
                include( request, response, this.headerPage, headerInfoText );
        }*/
    }
    
    protected void includeHeaderContent( HeaderResource headerResource )
    {
        // do nothing - intended for derived classes
        if ( this.dojoRequiresCoreList != null )
        {
            headerResource.dojoAddCoreLibraryRequires( this.dojoRequiresCoreList );
        }
        if ( this.dojoRequiresAddOnList != null )
        {
            headerResource.dojoAddModuleLibraryRequires( this.dojoRequiresAddOnList );
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
