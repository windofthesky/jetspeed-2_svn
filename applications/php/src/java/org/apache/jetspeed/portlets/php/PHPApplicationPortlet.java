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
package org.apache.jetspeed.portlets.php;

import java.io.IOException;
import java.security.Principal;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This portlet is executes a PHP application in a portlet.
 *
 * @author <a href="mailto:rogerrut@apache.org">Roger Ruttimann</a>
 * @version $Id$
 */

public class PHPApplicationPortlet extends GenericPortlet {

	/**
	 * INIT parameters required by the PHP Portlet:application and ServletContextProvider
	 *
     * Name of class implementing {@link PHPServletContextProvider}
     */
    public static final String PARAM_SERVLET_CONTEXT_PROVIDER = "ServletContextProvider";
    
     /**
      * Name of the application for this portlet
	 */
	public static final String PARAM_APPLICATION	=	"application";

		   
    // Local variables
    
    private PHPServletContextProvider servletContextProvider;
    private static final Log log = LogFactory.getLog(PHPApplicationPortlet.class);
    
    // Servlet INFO needed for portlet    
    
    ServletConfigImpl servletConfig = null;
    
    // PHP engine 
    com.itgroundwork.portlet.php.servlet phpServletImpl = null;
    
     // Parameters
    private String applicationName = null;
    
    // caching status    
    private String lastContextPath = null;
    private String lastQuery = null;
    private String lastURI = null;
    
    //ID to identify portlet
    private String portletID = null;
 
    public void init(PortletConfig config) throws PortletException
    {    
        super.init(config);
        
        // Initialize config
        servletConfig = new ServletConfigImpl(config);
        
        //Get the INIT PARAMETERS for this portlet. If the values are missing
        // throw an exception
        applicationName					=	config.getInitParameter(PARAM_APPLICATION);
        String contextProviderClassName =	config.getInitParameter(PARAM_SERVLET_CONTEXT_PROVIDER);
        
        if (applicationName == null)
            throw new PortletException("Portlet " + config.getPortletName()
                    + " is incorrectly configured. Init parameter "
                    + PARAM_APPLICATION + " not specified");
          
        if (contextProviderClassName == null)
            throw new PortletException("Portlet " + config.getPortletName()
                    + " is incorrectly configured. Init parameter "
                    + PARAM_SERVLET_CONTEXT_PROVIDER + " not specified");
        
        if (contextProviderClassName != null)
        {
            try
            {
                Class clazz = Class.forName(contextProviderClassName);
                if (clazz != null)
                {
                    Object obj = clazz.newInstance();
                    if (PHPServletContextProvider.class.isInstance(obj))
                        servletContextProvider = (PHPServletContextProvider) obj;
                    else
                        throw new PortletException("class not found");
                }
            } catch (Exception e)
            {
                if (e instanceof PortletException)
                    throw (PortletException) e;
                e.printStackTrace();
                throw new PortletException("Cannot load", e);
            }
        }
     }	
    
    public void doView(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
	{
      	ServletContext		servletContext	= servletContextProvider.getServletContext(this);
    	HttpServletRequest	httpRequest		= servletContextProvider.getHttpServletRequest(this, request);
    	HttpServletResponse httpResponse	= servletContextProvider.getHttpServletResponse(this, response);
    	
    	// if any of the above is null throw an error
    	if ( servletContext == null ||httpResponse == null || httpRequest == null )
    	{
    		httpResponse.getWriter().println("<p><b>Fatal error: Servlet Request/Response is missing!</b></p>");
    		return;
    	}
    	// Make sure the application Name is configured before doing any processing
    	if (this.applicationName == null || applicationName.length() == 0)
    	{
    		httpResponse.getWriter().println("<p><b>Configuration error!</b><br>Please make sure that the application Name is configured</p>");
    		return;
    	}
    	
    	//initialize PHP engine
    	if ( phpServletImpl == null)
    	{
    		try
			{
    		phpServletImpl = new com.itgroundwork.portlet.php.servlet();
    		if (phpServletImpl != null )
    			phpServletImpl.init(servletConfig);
			}
    		catch(ServletException se)
			{
    			httpResponse.getWriter().println("<p><b>Initializationof PHP servlet failed!</b> Error: " + se.getMessage() + "</p>");
			}
    	}
    	
    	/**
         * The portlet can have the following three stages
         * 1) First time accessed from the portal. No query parameters. Show info page
         * 2) Has the php-file query but no user. Add user than redirect it to itself
         * 2) User and php-file are defined run the php-servlet
         */
    	
      	String userName = "anon";	//default not logged in
    	Principal userPrincipal = request.getUserPrincipal();
    	if (userPrincipal != null )
    		userName = userPrincipal.getName();
        
        boolean bShowInfoPage = false;
        String	cookieValue = null;
        
        String reqQuery = httpRequest.getQueryString();
        String contextPath = servletContext.getRealPath(httpRequest.getServletPath());
        
        // Take portal out of the context. This will point to the root of the application
        String rootContextPath = contextPath.substring(0, contextPath.lastIndexOf("portal") ) ;
        
        // First time call or reinvoked from outside won't have the query string properly initialized
        if ( reqQuery == null || (reqQuery.indexOf("php-file=") == -1))
        {
        	// Check if the php was initialized (called once to the php servlet)
        	if (lastContextPath != null)
        	{
        		//com.itgroundwork.portlet.php.servlet phpServletImpl = new com.itgroundwork.portlet.php.servlet();
    	        try
    			{	
    		        if (phpServletImpl != null )
    		        {    		    		 				
		 				phpServletImpl.setAdjustedURI(lastURI);
		 				phpServletImpl.setAdjustedQuery(lastQuery);
		 				
		 				phpServletImpl.service(httpRequest, httpResponse,lastContextPath);
		 				
    				}
    			}
	    		catch( ServletException se)
				{
	    			httpResponse.getWriter().println("<P><B>Error in PHP servlet " + se.getMessage() + "</B></P><BR>");
				}
	    		
    			// Show the portlet				    
			    return;
        	}
        	else
        	{
        		bShowInfoPage = true;
        	}
        }
        else
        {        	
	    	// Search for the start php file
	    	String queryArg = "php-file=";
	    	        		    	
    		int index = reqQuery.indexOf(queryArg);
    		int sepIndex = reqQuery.indexOf('&', index);
    		String phpStartFile;
    		
    		// get name of the file to start
    		if (sepIndex != -1)
    		{
    			// Multiple query strings
    			phpStartFile = reqQuery.substring(index + queryArg.length(), sepIndex );
    		}
    		else
    		{
    			// Only query string
    			phpStartFile = reqQuery.substring(index + queryArg.length() );
    		}
    		
    		// Create an instance of the PHP servlet
    		try
			{		
				if (phpServletImpl != null )
				{
					// Make sure that the request is destined for this implementation of the portlet
					queryArg = "appname=";
					int i = reqQuery.indexOf(queryArg);
		    		int ii = reqQuery.indexOf('&', i);
		    		String reqAppName = null;
						
		    		if (ii != -1)
		    		{
		    			// Multiple query strings
		    			reqAppName = reqQuery.substring(i + queryArg.length(), ii );
		    		}
		    		else
		    		{
		    			// Only query string
		    			reqAppName = reqQuery.substring(i + queryArg.length() );
		    		}
		    	
		    		if ((applicationName.compareToIgnoreCase(reqAppName) == 0) || (reqAppName.compareToIgnoreCase("test") == 0) )
		    		{
		    			// New request for this portlet render it for the new content
						String adjURI = "/PHP/" + phpStartFile;
						
						String phpContext = rootContextPath;					
						phpContext += phpStartFile;
						
						rootContextPath = phpContext;
												
						// Call into the php library
						phpServletImpl.setAdjustedURI(adjURI);
						phpServletImpl.setAuthenticatedUser(userName);
						phpServletImpl.setAdjustedQuery(reqQuery);
						phpServletImpl.service(httpRequest, httpResponse,rootContextPath);
						
						// Save last executed request info so that we remember when it was last called
						lastQuery = reqQuery;
						lastContextPath = rootContextPath;
						lastURI = adjURI;
						
		    		}
		    		else
		    		{
		    			// Not for this application just refresh the content
		    			if ( lastURI == null || lastURI.length() == 0)
		    			{
		    				bShowInfoPage = true;
		    			}
		    			else
		    			{
		    				phpServletImpl.setAdjustedURI(lastURI);
		    				phpServletImpl.setAdjustedQuery(lastQuery);
		 				
		    				phpServletImpl.service(httpRequest, httpResponse,lastContextPath);
		    			}
		    		}
				}
				else
				{
					httpResponse.getWriter().println("<br/><b>Error in PHP servlet. Couldn't create instance of net.php.servlet</b>" );
				}
			}
    		catch( ServletException se)
			{
    			httpResponse.getWriter().println("<P><B>Error in PHP servlet.Servlet Exception: " + se.getMessage() + "</B></P><BR>");
    			throw new PortletException(se);
			}
			catch (IOException e)
			{
				httpResponse.getWriter().println("<P><B>Error in PHP servlet. IO Exception " + e.getMessage() + "</B></P><BR>");
			}
        }
        
        if (bShowInfoPage == true)
        {
        	// Display the info page
        	PortletContext context = getPortletContext();
        	PortletRequestDispatcher rd = context.getRequestDispatcher("/WEB-INF/php-setup.jsp");
            rd.include(request, response);        
            
            // Add the information the demo application
        	String portletIDQuery = "&php-portletid=" + this.portletID;
        	
        	String uri =  httpRequest.getRequestURI();
        	
        	StringBuffer reqURL = HttpUtils.getRequestURL(httpRequest);
        	String rootURL = reqURL.toString().substring(0, reqURL.toString().lastIndexOf("portal")+6 );
        	       	
        	// Test page
        	StringBuffer servletURL = new StringBuffer();
        	StringBuffer href = new StringBuffer();
        	
        	servletURL.append("http://").append(httpRequest.getServerName()).append(":").append(httpRequest.getServerPort());
        	href.append(servletURL.toString()).append("/PHP/test.php");
        	href.append("?caller-context=").append(uri).append("&appname=test&calling-user=").append(userName);
        	String testURL = href.toString();
        	        	
        	// URL to Machine database sample application
        	href.delete(0,href.length());
        	href.append(servletURL.toString()).append("/PHP/").append(applicationName).append("/index.php");
        	href.append("?caller-context=").append(uri).append("&appname=").append(applicationName).append("&calling-user=").append(userName).append(portletIDQuery);
        	String mdbURL = href.toString();
        	
        	// URL to database install
        	href.delete(0,href.length());
        	href.append(servletURL.toString()).append("/PHP/").append(applicationName).append("/install.php");
        	href.append("?caller-context=").append(uri).append("&appname=").append(applicationName).append("&calling-user=").append(userName).append(portletIDQuery);
        	String mdInstall = href.toString();
        	      	
          	httpResponse.getWriter().println("<p><b>Welcome to the PHP DEMO portlet</b><p>");
        	httpResponse.getWriter().println("<p>The links below start some PHP applications inside a portlet<br>");
        	httpResponse.getWriter().println("The test portlet takes over the whole screen but remains still inside the portlet<br>");
        	httpResponse.getWriter().println("The Machine Database demo shows you several screens allowing to update data in the database. The database connection info is defined in hosts/conf/config.php<BR>Current configuration: Database=hosts, User =root</P>");
        	httpResponse.getWriter().println("<ul><li><a href=" +testURL + ">PHP test page</li>");
        	httpResponse.getWriter().println("<li><a href=" +mdbURL + ">Enter Machine database</li>");
        	httpResponse.getWriter().println("<li><a href=" + mdInstall + ">Machine DB Install</a></li></ul></P>");
        }
	}
}
