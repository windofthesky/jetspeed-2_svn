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

import javax.servlet.http.HttpServlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import javax.servlet.http.HttpUtils;

import java.util.Hashtable;

/**
 * Servlet that redirects calls to PHP files to the PHPPOrtlet that
 * initiated the PHP application.
 *
 * @author <a href="mailto:rogerrut@apache.org">Roger Ruttimann</a>
 * @version $Id$
 */
public class phpRedirectorServlet extends HttpServlet {
	
	// Class variables
	Hashtable session2Context = new Hashtable();
	
	// Implement API's
	
	public void init(ServletConfig config)
	  throws ServletException {
	    super.init(config);
	}
	
	public void destroy() {
	    
	    super.destroy();
	  }
	
	public void service(HttpServletRequest request, HttpServletResponse response)
	  throws ServletException {
	    String servletPath = request.getServletPath();
	    String contextPath = getServletContext().getRealPath(servletPath);
	    
    	// Extract the php file and add it to the query string
    	StringBuffer reqURL = HttpUtils.getRequestURL(request);
    	
	    String cookieValue = null;
	    String requestQuery = request.getQueryString();
	    
	    String CALLER_CONTEXT = "caller-context=";
	    String USERNAME = "calling-user=";
	    String APPNAME = "appname=";
	    String PORTLETID = "php-portletid=";
	    String	applicationName = null;
	    String	callingUser = null;
	    String	callerContextPath = null;
	    String	portletID = null;
	    
	    /**
	     * The redirectorServlet only works with requests coming from the portal. The portal context
	     * is defined in the query string "caller-context". It will be added to a lookup map for any
	     * further requests with the same session id. If the session doesn't exist and no caller context
	     * query is defined just display an error message
	     */
	    
	    // Read the header info
	    HttpSession currentSession = request.getSession();
        if (currentSession != null)
		{
        	cookieValue = currentSession.getId();		
		}
        	
		// if the cookie is undefined throw an error.
		if ( cookieValue == null)
		{
			try
			{
	    		// Cookie missing can't continue
	    		response.getWriter().println("<br/><b>Error in phpRedirectorServlet servlet. Didn't find cookie in request header</b>" );
			}
	    	catch (IOException ioe)
			{
	    		//Write to the console
	    		System.out.println("Exception while writing to HTTPServletRespone obj " + ioe.getMessage());
	    		
			}
	    	
	    	throw new ServletException("no cookie");
	    	
		}
	    
	    /*
	     * Extract the application name out of the URL. The appname and the cookies are the unique
	     * keys to extract the context info
	     */
		
		String tmpAppName = reqURL.substring(reqURL.indexOf("PHP/")+4, reqURL.indexOf(".php"));
		int nIndexSlash = tmpAppName.lastIndexOf("/");
		if ( nIndexSlash == -1 ) 
			applicationName = tmpAppName;	//Just file name no namespace for application
		else
			applicationName = tmpAppName.substring(0, nIndexSlash);
		
    
	    // Redirect the incoming request to the portlet handling class
	    if (requestQuery == null || requestQuery.indexOf(CALLER_CONTEXT) == -1 )
	    {	
	    	// Check if a context exists for a given session
	    	UserContextInfo info = (UserContextInfo)session2Context.get(cookieValue+applicationName);
	    	
	    	callerContextPath = info.getCallerContext();
	    	callingUser = info.getUserName();
	    	//applicationName = info.getApplicationName();
	    		    	
	    	
	    	// Unknown request -- Not an initial call (context) or navigate inside a portlet (session id)
	    	if ( callerContextPath == null)
	    	{
		    	try
				{
		    		// Won't work in this context. Display an error
		    		response.getWriter().println("<br/><b>Error in phpRedirectorServlet servlet. Couldn't find query string for " + CALLER_CONTEXT + "</b>" );
				}
		    	catch (IOException ioe)
				{
		    		//Write to the console
		    		System.out.println("Exception while writing to HTTPServletRespone obj " + ioe.getMessage());
		    		
				}
		    	
		    	// Can't continue
		    	//return;
		    	throw new ServletException("no context path found. SessionID[" + cookieValue +"]");
	    	}
	    }
	    else
	    {
		    //Extract the context path
	    	callerContextPath = requestQuery.substring(requestQuery.indexOf(CALLER_CONTEXT) + CALLER_CONTEXT.length() );
	    	int indSep = callerContextPath.indexOf('&');
	    	if ( indSep != -1)
	    	{
	    		callerContextPath = callerContextPath.substring(0, indSep);
	    	}
	    	
	    	// Extract the user name
	    	callingUser = requestQuery.substring(requestQuery.indexOf(USERNAME) + USERNAME.length() );
	    	if (callingUser != null)
	    	{
	    		indSep = callingUser.indexOf('&');
		    	if ( indSep != -1)
		    	{
		    		callingUser = callingUser.substring(0, indSep);
		    	}
	    	}
	    	
		    	
	    	//Extract PortletID Name from the query string
		    portletID = requestQuery.substring(requestQuery.indexOf(PORTLETID) + PORTLETID.length() );
	    	if (portletID != null)
	    	{
	    		indSep = portletID.indexOf('&');
		    	if ( indSep != -1)
		    	{
		    		portletID = portletID.substring(0, indSep);
		    	}
	    	}
		    	
	    	// Store the information in a struct that gets stored in the hasmap
	    	UserContextInfo info = new UserContextInfo(callerContextPath, callingUser, applicationName, portletID);
	    	
	    	//Store the caller context and the session ID in the Session2Context hash. This will be used to
	    	// redirect future calls to the servlet to the same portlet based on the session ID and the appname
	    	session2Context.put( cookieValue+applicationName, info);
	    }
	    
	    // Got caller context path.
	    //	Update URL and query and send it back to portlet
	   	    	
    	String URI = request.getRequestURI();
    	
    	String phpFile = reqURL.substring( reqURL.lastIndexOf(applicationName), reqURL.indexOf(".php") + 4);
    	
    	if (requestQuery == null || requestQuery.length() == 0)
    		requestQuery = "php-file=";
    	else
    		requestQuery += "&php-file=";
    	
    	requestQuery += phpFile;
    	
    	//Add user to query if it isn't already defined
    	if ( requestQuery.indexOf(USERNAME) == -1)
    	{
	    	requestQuery += "&";
	    	requestQuery += USERNAME;
	    	requestQuery += callingUser;
    	}
    	
    	// Add user to query if it isn't already defined
    	if ( requestQuery.indexOf(APPNAME) == -1)
    	{
	    	requestQuery += "&";
	    	requestQuery += APPNAME;
	    	requestQuery += applicationName;
    	}
    		
    	/** Build the portal url
    	 * 	Use caller server/port add caller context and query
    	 */
    	
	    StringBuffer redirectURLBuffer = new StringBuffer();  
	    redirectURLBuffer.append("http://").append(request.getServerName()).append(":").append(request.getServerPort()).append(callerContextPath).append("?").append(requestQuery);
	     	    
	    try
		{
	    	response.sendRedirect(redirectURLBuffer.toString() );
		}
	    catch (IOException ioe)
		{
	    	// Write to the console
	    	System.out.println("Exception while redirecting to: " + redirectURLBuffer + ioe.getMessage());
		}
 
	}	
}


/**
 *
 * UserContextInfo class.
 * Stores information about the initial session
 */
class UserContextInfo
{
	private String callerContext = null;
	private	String userName = null;
	private String applicationName = null;
	private String portletID	= null;
	
	public UserContextInfo(String callerContext, String userName, String appName, String portletID)
	{
		this.callerContext		=	callerContext;
		this.userName			=	userName;
		this.applicationName	=	appName;
		this.portletID 			=	portletID;
	}
	
	public void setCallerContext(String callerContext)
	{
		this.callerContext = callerContext;
	}
	
	public String getCallerContext()
	{
		return this.callerContext;
	}
	
	public void setUserName(String userName)
	{
		this.userName = userName;
	}
	
	public String getUserName()
	{
		return this.userName;
	}
	
	public void setApplicationName(String appName)
	{
		this.applicationName = appName;
	}
	
	public String getApplicationName()
	{
		return this.applicationName;
	}
	
	public void setPortletID(String portletID)
	{
		this.portletID = portletID;
	}
	
	public String getPortletID()
	{
		return this.portletID;
	}
	
}
