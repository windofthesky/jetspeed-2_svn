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
package org.apache.jetspeed.portlets.perl;

import javax.portlet.GenericPortlet;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletSession;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jetspeed.container.JetspeedPortletContext;

/**
* This portlet is executes a Perl/cgi files in a portlet.
*
* @author <a href="mailto:rogerrut@apache.org">Roger Ruttimann</a>
* @version $Id$
*/

public class PerlPortlet extends GenericPortlet {
	/**
	 * INIT parameters required by the Perl Portlet:PerlScript, ScriptPath, DemoMode
	 *
	 * Name of the scrip to to execute
	 */
	public static final String PARAM_PERL_SCRIPT	=	"PerlScript";

	/**
	 * Name of the Script Path where the perl scripts (among others) are located
	 */
	public static final String PARAM_SCRIPT_PATH	=	"ScriptPath";
	
	/**
	 * DemoMode on or off
	 */
	public static final String PARAM_DEMO_MODE	=	"DemoMode";
	
	   
    // Local variables
    private String perlScript	=	"perl-demo.cgi";
    private String	scriptPath	=	"cgi-bin";
    
    // Switch that shows basic information about the perl script to run
    private boolean bDemoMode	=	false;
    
    private static final Log log = LogFactory.getLog(PerlPortlet.class);
    
       
    // caching status -- cache the last query    
    private String lastQuery = null;
    
    
    public void init(PortletConfig config) throws PortletException
    {
    
        super.init(config);
        
        // Get the INIT PARAMETERS for this portlet. If the values are missing
        // throw an exception
        scriptPath		=	config.getInitParameter(PARAM_SCRIPT_PATH);
        perlScript		=	config.getInitParameter(PARAM_PERL_SCRIPT);
        String demoMode =	config.getInitParameter(PARAM_DEMO_MODE);
        
        if (demoMode != null && demoMode.compareToIgnoreCase("on") == 0)
        	bDemoMode = true;
        
        if (scriptPath == null)
            throw new PortletException("Portlet " + config.getPortletName()
                    + " is incorrectly configured. Init parameter "
                    + PARAM_SCRIPT_PATH + " not specified");
        
        if (perlScript == null)
            throw new PortletException("Portlet " + config.getPortletName()
                    + " is incorrectly configured. Init parameter "
                    + PARAM_PERL_SCRIPT + " not specified");
     }	
    
    /**
     * doView
     * Executes the perl script that is defined by the property PerlScript.
     * If the incoming request has the query perl-script= defined this script will be executed.
     */
    public void doView(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
	{
    	// Set the content type
    	response.setContentType("text/html");
    	
    	// Get a writer object that can be used to generate the output
    	HttpServletResponse httpResponse = (HttpServletResponse)((HttpServletResponseWrapper) response).getResponse();
    	
    	//PrintWriter writer = response.getWriter();
    	PrintWriter writer = httpResponse.getWriter();
    	   	
    	String query		= null;
    	String scriptName	= null;
    	
    	/**
    	 * The Perl parameters are either passed by a session attribute (invoked through an action) or as a query string (invoked from a href).
    	 * The portlet checks first if a session attribute (SELECTED_VIEW) was defined and then checks for the query string
    	 */
    	PerlParameters perlParam = null;
    	try
		{
    		perlParam = (PerlParameters)request.getPortletSession().getAttribute("SELECTED_VIEW", PortletSession.APPLICATION_SCOPE);
		}
    	catch (Exception e )
		{
    		perlParam = null;
		}
    	
    	if (perlParam != null)
    	{
    		query = perlParam.getQueryString();
    		perlScript = perlParam.getPerlScript();
    		
    	}
    	else
    	{
    		// Extract the Query string
	    	String queryString = ((HttpServletRequest)((HttpServletRequestWrapper) request).getRequest()).getQueryString();
	    	
	    	// Check if the call is to run the default script or for a different one (perl-script= defined) 
	    	if (queryString == null || queryString.indexOf("perl-script=") == -1)
	    	{
	    		// Execute the perl script defined as the default
	    		scriptName = perlScript;
	    	}
	    	else
	    	{
	    		// Check if the perl script to run is overwritten
	    		scriptName = queryString.substring(queryString.indexOf("perl-script=")+12);
	    		int del	= scriptName.indexOf('&');
	    		if ( del != -1 )
	    			scriptName = scriptName.substring(0, del);
	    	}
	    	
	    	// Use the cached query string if the cgi is
	    	if ( queryString == null )
	    	{
	    		query = "";
	    	}
	    	else
	    	{
	    		query = queryString;
	    	}
    	}
    	
    	// Open the perl script and extract the perl executable path. It's the same way as apache HTTP executes PERL
    	
    	String contextPath	=	((JetspeedPortletContext)this.getPortletContext()).getServletContext().getRealPath( ((HttpServletRequestWrapper) request).getServletPath());
    	String perlExecutable = null;
    	
    	String rootContextPath = contextPath.substring(0, contextPath.lastIndexOf("container") ) ;
    	rootContextPath += scriptPath;
    	
    	rootContextPath += "/";
    	String inputPath = rootContextPath;
    	rootContextPath += perlScript;
    	
    	// command to execute
    	String command = null;
    	
    	
    	// Open the script and read the first line to get the executable !/usr/bin/perl OR !c:\bin\perl\perl.exe
    	try
		{
    		BufferedReader in= new BufferedReader(new FileReader(rootContextPath));
    		String lnExecutable = in.readLine();
    		
    		if (lnExecutable != null )
    		{
    			int ix = lnExecutable.indexOf('!');
    			if ( ix != -1 )
    			{
    				perlExecutable = lnExecutable.substring(ix+1, lnExecutable.indexOf(' ',ix));
    			} 
    		}
    		//Close file
    		in.close();
    		
    		StringBuffer commandBuffer = new StringBuffer();
    		if (perlExecutable == null)
    			commandBuffer.append(rootContextPath);
    		else
    			commandBuffer.append(perlExecutable).append(' ').append(rootContextPath);
    		
    		command = new String(commandBuffer.toString());
    		
		}
    	catch(FileNotFoundException e)
		{
    		writer.println("<P><B>File doesn't exist (" + rootContextPath + ")</B></P>");
		}
    	catch(IOException e)
		{
    		writer.println("<P><B>IO Exception (" + e.getMessage() + ")</B></P>");
		}
		catch(Exception e)
		{
			writer.println("<P><B>IO Exception (" + e.getMessage() + ")</B></P>");
		}
		
		String envQuery = "QUERY_STRING=" + query;
		String[] env = null;
		env = new String[]{"REQUEST_METHOD=GET", envQuery};
		
		if ( bDemoMode == true)
		{
			// Script info. This is for the Demo only
			writer.println("<P>The portlet executes the perl script defined by the init-params. If you don't get an output make sure that the perl executable defined in the script is valid.");
			writer.println("The executable is defined on the first line of your script.</P>Examples<ul><li><B>UNIX/Linux:</B>!/usr/bin/perl</li><li><B>Windows:</B>!c:\\bin\\perl\\perl.exe</li></ul>");
			writer.println("<B><P>Perl Script:</B>" + rootContextPath + "<BR>");
			writer.println("<B>Perl executable:</B>" + perlExecutable + "</P>");
		}   	
    	
		//Execute the perl script from the command line
		if (command != null )
		{
			// Execute command in a separate process. The perl output is written to the stdout
			try
			{		
				// Start process
				Process proc = Runtime.getRuntime().exec(command,env);
				
				// Get stdout of process and create a buffered reader
				InputStream in = proc.getInputStream();
				BufferedReader perlResult = new BufferedReader(new InputStreamReader(in));
				StringBuffer page = new StringBuffer();
				
				//Wait until proc is done
				boolean bProcDone = false;
				while (bProcDone == false)
				{
					try
					{
						proc.exitValue() ;
						bProcDone = true;
					}
					catch(IllegalThreadStateException e)
					{
						bProcDone = false; //Not done yet
						
						// Read the buffer otherwise the process will be blocked because it can't write to the stdout (max size of buffer)
						int ln;
						while ((ln = perlResult.read()) != -1)
						{
							char c  = (char)ln;
							if (c != '\n' && c != '\r')
							page.append((char)ln);
						}
					}
				}
				
				// Perl execution done read the remaining  buffer
				int ln = -1;
				
				while ((ln = perlResult.read()) != -1)
				{
					char c = (char)ln;
					if (c != '\n' && c != '\r')
					page.append((char)ln);
				}
				// Close stream
				perlResult.close();		
				
				// Write the page
				writer.println(page.toString());
			}
			catch(IOException ioe)
			{
				writer.println("<P><B>Exception while reading perl output" + ioe.getMessage() + "</B></P>");
			}
		}	
	}   
}
	
