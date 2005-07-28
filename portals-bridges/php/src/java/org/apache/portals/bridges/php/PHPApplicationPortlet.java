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
package org.apache.portals.bridges.php;

import java.io.IOException;
import java.security.Principal;
import java.util.Enumeration;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.portals.bridges.common.ScriptPostProcess;
import org.apache.portals.bridges.common.ServletContextProvider;
import org.apache.portals.bridges.php.PHPParameters;

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
	 * Start page for this portlet it must be the path to the script (e.g hosts/index.php)
	 */
	public static final String START_PAGE	=	"StartPage";

		   
    // Local variables
    
	private ServletContextProvider servletContextProvider;
    private static final Log log = LogFactory.getLog(PHPApplicationPortlet.class);
    
    // Servlet INFO needed for portlet    
    
    ServletConfigImpl servletConfig = null;
    
    // PHP engine 
    com.itgroundwork.portlet.php.servlet phpServletImpl = null;
    
     // INIT Parameters
    private String startPage = null;
    
    // caching status    
    private boolean bUseCachedParameters = false;
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
        startPage											=	config.getInitParameter(START_PAGE);
        String contextProviderClassName	=	config.getInitParameter(PARAM_SERVLET_CONTEXT_PROVIDER);
        
        if (startPage == null)
            throw new PortletException("Portlet " + config.getPortletName()
                    + " is incorrectly configured. Init parameter "
                    + START_PAGE + " not specified");
          
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
                    if (ServletContextProvider.class.isInstance(obj))
                        servletContextProvider = (ServletContextProvider) obj;
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
    
    /**
     * processAction()
     * Checks action initiated by the php portlet (invoking other php scripts)
     * @param actionRequest
     * @param actionResponse
     * @throws PortletException
     * @throws IOException
     */
    public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) throws PortletException, IOException
	{
    	String phpParameter = actionRequest.getParameter(PHPParameters.ACTION_PARAMETER_PHP);
     	/*
    	 * If the phpParameter is not empty create a PHPParameters object and attach it to the session
    	 */
    	if ( phpParameter != null && phpParameter.length() > 0)
    	{
    		// Perl Parameter Object
    		PHPParameters phpScript = new PHPParameters();
    		
    		// Separate the values before and after the Query Mark ?
    		int ixQuery = phpParameter.indexOf('?');
    		if ( ixQuery != -1)
    		{
    			phpScript.setScriptName(phpParameter.substring(0,ixQuery));
    			
    			String queryArguments = phpParameter.substring(ixQuery+1);
    			System.out.println("ProcessRequest -- Script " + phpParameter.substring(0,ixQuery) + " Query string " + queryArguments);
    			
    			int ixQuerySeparator = queryArguments.indexOf('&');
    			while ( ixQuerySeparator != -1)
    			{
    				phpScript.addQueryArgument(queryArguments.substring(0, ixQuerySeparator));
    				queryArguments = queryArguments.substring(ixQuerySeparator+1);
    				ixQuerySeparator = queryArguments.indexOf('&');
    			}
    			
    			phpScript.addQueryArgument(queryArguments);
    			
    			// Add the PerlParameters to the session
    			actionRequest.getPortletSession().setAttribute(PHPParameters.PHP_PARAMETER, phpScript, PortletSession.APPLICATION_SCOPE);
    		}
    		else
    		{
    			// No query string just the script name
    			phpScript.setScriptName(phpParameter);
    			
    			// Get all the parameters from the request and add them as query arguments
    			Enumeration names = actionRequest.getParameterNames();
    			String name, value;
    			while (names.hasMoreElements())
    			{
    				name = (String)names.nextElement();
    				// ACTION_PARAMETER_PHP already processed just ignore it
    				if (name.compareToIgnoreCase(PHPParameters.ACTION_PARAMETER_PHP) != 0)
    				{
    					value = actionRequest.getParameter(name);
    					
       					phpScript.addQueryArgument(name + "=" + value);
    				}
    			}
    			// Add the PerlParameters to the session
    			actionRequest.getPortletSession().setAttribute(PHPParameters.PHP_PARAMETER, phpScript, PortletSession.APPLICATION_SCOPE);
     		}
    	}
	}
    
    /**
     * doView
     * Renders a PHP file in the portlet. 
     * The script parameters are in a PHPParameters object that is passed in the session
     */
    
    public void doView(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
	{
    	/*
    	 *  Extract the PHPParameter object from the session. If this is not provided use the values for startPage and application from the INIT Parameters
    	 */
    	String	reqQuery;
    	String	phpScript;
    	
    	PHPParameters phpParam = null;
    	try
		{
    		phpParam = (PHPParameters)request.getPortletSession().getAttribute(PHPParameters.PHP_PARAMETER, PortletSession.APPLICATION_SCOPE);
		}
    	catch (Exception e )
		{
    		phpParam = null;
		}
    	
    	if (phpParam != null)
    	{
    		// We got real parameters
    		bUseCachedParameters = false;
    		reqQuery = phpParam.getQueryString();
    		phpScript = phpParam.getScriptName();
    	}
    	else
    	{
    		/*
    		 * No parameters were send to this page. Either it is the initial invocation (use init param) or 
    		 * iit was a refresh (use cached arguments)
    		 * 
    		 * Setting the bUseCacheParameters means that nothing was provided from outside
    		 */
    		bUseCachedParameters = true;
    		reqQuery = "";
    		phpScript = this.startPage;
    	}
    	
      	//ServletContext		servletContext	= servletContextProvider.getServletContext(this);
    	HttpServletRequest	httpRequest		= servletContextProvider.getHttpServletRequest(this, request);
    	HttpServletResponse httpResponse	= servletContextProvider.getHttpServletResponse(this, response);
    	
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
    	
      	
    	// Make sure we have an user
      	String userName = "anon";	//default not logged in
    	Principal userPrincipal = request.getUserPrincipal();
    	if (userPrincipal != null )
    		userName = userPrincipal.getName();
        
        boolean bShowInfoPage = false;
        String	cookieValue = null;
        
         // Build the context path
        String contextPath =		request.getContextPath();
        PortletContext portletApplication = getPortletContext(); 
        String path = portletApplication.getRealPath("/WEB-INF");
        String rootContextPath = path + "/";
        
        /*
         * At this point we have all the information to run the PHP servlet:
         * 		rootContextPath	contains the file path to the PortletApplication root (e.g /home/user/tomcat/webapps/MyApplication/ )
         * 		phpScript				php script to execute. Includes the full path to the application (e.g hosts/index.php)
         * 		reqQuery				Query arguments passed to the scripts
         */
        
        // Run parameters
        String runURI, runQuery, runContext;
        
        // First time call or invoked from another portlet
        if ( bUseCachedParameters == true)
        {
        	//If it is the first call create an URI
        	if ( lastURI == null || lastURI.length() == 0)
        	{
        		// Build the URI with the start page and the context
        		lastURI = contextPath + "/" + phpScript;
        		lastContextPath = rootContextPath + phpScript;
        	}
        	
        	// Assign run values
        	runURI = lastURI;
        	runQuery = lastQuery;
        	runContext = lastContextPath;
        }
        else
        {      
			// New request for this portlet render it for the new content
			String adjURI = contextPath + "/" + phpScript;
			
			String phpContext = rootContextPath + phpScript;			
			
			// Assign run values
        	runURI = adjURI;
        	runQuery = reqQuery;
        	runContext = phpContext;
        }

		// Invoke the PHP servlet and run it
		try
		{		
			if (phpServletImpl != null )
			{	
				    if (runQuery == null )
				    	runQuery = "";
				    
				    //  Call into the php library. 		
				    // Cache the page in the servlet and don't write the output to the response since some post processing
				    // is needed before the page can be send back to the client
				    phpServletImpl.setUseInternalPage();
				    
				    // Set the servlet parameters
					phpServletImpl.setAdjustedURI(runURI);
					phpServletImpl.setAuthenticatedUser(userName);
					phpServletImpl.setAdjustedQuery(runQuery);
					
					// execute the PHP script
					phpServletImpl.service(httpRequest, httpResponse, runContext);
					
					//Save last executed request info so that we remember when it was last called
					lastQuery = runQuery;
					lastContextPath = runContext;
					lastURI = runURI;
					
					//PostProcess:
					//	replace all relative links with actions
					
					//Any HREFs and Form actions should be extended with the ActionURL
					PortletURL actionURL = response.createActionURL();
					
					// Get the buffered page from the PHP servlet
					StringBuffer page = phpServletImpl.getSourcePage();
					
					// Call into the PostProcess object which is the same for PERL and other script engine
					// supported in the future.
					ScriptPostProcess processor = new ScriptPostProcess();
					processor.setInitalPage(page);
					processor.postProcessPage(actionURL, PHPParameters.ACTION_PARAMETER_PHP);
					String finalPage = processor.getFinalizedPage();
					
			        //Write the page to the HttpResponse
					httpResponse.getWriter().println(finalPage);				
			}
			else
			{
				httpResponse.getWriter().println("<br/><b>Error in PHP servlet. Couldn't create instance of com.itgroundwork.portlet.php.servlet. Make sure the jar is included in the same app as the portas-bridges-php jar file</b>" );
			}
		}
		catch( ServletException se)
		{
			httpResponse.getWriter().println("<P><B>Error in PHP servlet.Servlet Exception: " + se.getMessage() + "</B>RunQuery=" + runQuery+ " RunContext=" + runContext + " RunURI=" + runURI + " </P><BR>");
			throw new PortletException(se);
		}
		catch (IOException e)
		{
			httpResponse.getWriter().println("<P><B>Error in PHP servlet. IO Exception " + e.getMessage() + "</B>RunQuery=" + runQuery+ " RunContext=" + runContext + "Run URI=" + runURI + "</P><BR>");
		}
	}
}