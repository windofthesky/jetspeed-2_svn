/* Copyright 2004 Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.portlet;

import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletSession;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.portals.messaging.PortletMessaging;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.rewriter.JetspeedRewriterController;
import org.apache.jetspeed.rewriter.RewriterController;
import org.apache.jetspeed.rewriter.RewriterException;
import org.apache.jetspeed.rewriter.RulesetRewriter;
import org.apache.jetspeed.rewriter.WebContentRewriter;
import org.apache.jetspeed.rewriter.html.neko.NekoParserAdaptor;
import org.apache.jetspeed.rewriter.rules.Ruleset;
import org.apache.jetspeed.rewriter.xml.SaxParserAdaptor;
import org.apache.jetspeed.portlet.webcontent.WebContentHistoryList;
import org.apache.jetspeed.portlet.webcontent.WebContentHistoryPage;


/**
 * WebContentPortlet
 * 
 * TODO: Preferences, cache stream instead of URL *
 * 
 * @author <a href="mailto:rogerrutr@apache.org">Roger Ruttimann </a>
 * @version $Id$
 */

public class WebContentPortlet extends GenericVelocityPortlet
{

    /**
     * WebContentPortlet Allows navigation inside the portlet and caches the
     * latest URL
     */

    /**
     * Configuration constants.
     */
    public static final String VIEW_SOURCE_PARAM = "viewSource";
    public static final String EDIT_SOURCE_PARAM = "editSource";
    
    // ...browser action buttons
    public static final String BROWSER_ACTION_PARAM = "wcBrowserAction"; 
    public static final String BROWSER_ACTION_PREVIOUS_PAGE = "previousPage"; 
    public static final String BROWSER_ACTION_REFRESH_PAGE = "refreshPage"; 
    public static final String BROWSER_ACTION_NEXT_PAGE = "nextPage"; 

    /**
     * Default WebContent source attribute members.
     */
    private String defaultViewSource;
    private String defaultEditSource;

    /**
     * Action Parameter
     */

    // WebContent session data 

    public static final String HISTORY = "webcontent.history";
    public static final String HTTP_STATE = "webcontent.http.state";
    
    // Class Data
    
    protected final static Log log = LogFactory.getLog(WebContentPortlet.class);
    public final static String defaultEncoding = "UTF-8";

    // Data Members
    
    private RulesetRewriter rewriter = null;
    private RewriterController rewriteController = null;

    
    public WebContentPortlet()
    {
        super();
    }

    /**
     * Initialize portlet configuration.
     */
    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);

        defaultEditSource = config.getInitParameter(EDIT_SOURCE_PARAM);
    }

    /**
     * processAction() Checks action initiated by the WebContent portlet which
     * means that a user has clicked on an URL
     * 
     * @param actionRequest
     * @param actionResponse
     * @throws PortletException
     * @throws IOException
     */
    public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) throws PortletException,
            IOException
    {
        // check to see if it is a meta-navigation command
        String browserAction = actionRequest.getParameter(BROWSER_ACTION_PARAM);
        if (browserAction != null)
        {
            if (!browserAction.equalsIgnoreCase(BROWSER_ACTION_REFRESH_PAGE))
            {
                // for Refresh, there is nothing special to do - current history page will be re-displayed
                WebContentHistoryList history = (WebContentHistoryList)PortletMessaging.receive(actionRequest, HISTORY);
                
                if (browserAction.equalsIgnoreCase(BROWSER_ACTION_PREVIOUS_PAGE))
                {
                    if (history.hasPreviousPage())
                        history.getPreviousPage();
                }
                else if (browserAction.equalsIgnoreCase(BROWSER_ACTION_NEXT_PAGE))
                {
                    if (history.hasNextPage())
                        history.getNextPage();
                }
            }
            
            return ;   // proceed to doView() with adjusted history
        }
        
        // Check if an action parameter was defined        
        String webContentURL = actionRequest.getParameter(WebContentRewriter.ACTION_PARAMETER_URL);
        String webContentMethod = actionRequest.getParameter(WebContentRewriter.ACTION_PARAMETER_METHOD);
        Map webContentParams = new HashMap(actionRequest.getParameterMap()) ;
        
        // defaults
        if (webContentMethod == null) webContentMethod = "" ;   // default to GET
        
        // parameter map includes the URL (as ACTION_PARAMETER_URL), but all actual params as well
        webContentParams.remove(WebContentRewriter.ACTION_PARAMETER_URL);
        webContentParams.remove(WebContentRewriter.ACTION_PARAMETER_METHOD);
        
        if (webContentURL == null || actionRequest.getPortletMode() == PortletMode.EDIT)
        {
            processPreferencesAction(actionRequest, actionResponse);            
            webContentURL = actionRequest.getPreferences().getValue("SRC", "http://portals.apache.org");

            // parameters are for the EDIT mode form, and should not be propagated to the subsequent GET in doView
            webContentParams.clear();
        }

        /*
         * If the webContentParameter is not empty attach the URL to the session
         */
        if (webContentURL != null && webContentURL.length() > 0)
        {
            // new page visit - make it the current page in the history
            WebContentHistoryList history = (WebContentHistoryList)PortletMessaging.receive(actionRequest, HISTORY);
            if (history == null)
                history = new WebContentHistoryList();
            history.visitPage(new WebContentHistoryPage(webContentURL,webContentParams,webContentMethod));
            PortletMessaging.publish(actionRequest, HISTORY, history);
        }
    }

    /**
     * doView Renders the URL in the following order 1) SESSION_PARAMETER
     * 2)cached version 3) defined for preference SRC
     */
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {
        String viewPage = (String)request.getAttribute(PARAM_VIEW_PAGE);
        if (viewPage != null)
        {
            super.doView(request, response);
            return;
        }
        
        // view the current page in the history
        WebContentHistoryList history = (WebContentHistoryList)PortletMessaging.receive(request, HISTORY);
        if (history == null)
            history = new WebContentHistoryList();
        WebContentHistoryPage currentPage = history.getCurrentPage();
        if (currentPage == null)
        {
            String sourceURL = request.getPreferences().getValue("SRC", "");
            if (sourceURL == null)
            {
                // BOZO - switch to edit mode automatically here, instead of throwing exception!
                throw new PortletException("WebContent source not specified. Go to edit mode and specify an URL.");
            }
            currentPage = new WebContentHistoryPage(sourceURL);
        }

        // Initialize the controller if it's not already done
        if (rewriteController == null)
        {
            PortletContext portletApplication = getPortletContext(); 
            String path = portletApplication.getRealPath("/WEB-INF");
            String contextPath = path + "/";
            try
            {
                // Create rewriter adaptor
                rewriteController = getController(contextPath);
            }
            catch (Exception e)
            {
                // Failed to create rewriter controller
                String msg = "WebContentPortlet failed to create rewriter controller.";
                log.error(msg,e);
                throw new PortletException(e.getMessage());
            }
        }

        // get content from current page
        response.setContentType("text/html");
        byte[] content = doWebContent(currentPage.getUrl(), currentPage.getParams(), currentPage.isPost(), request, response);
        
        // write the meta-control navigation header
        PrintWriter writer = response.getWriter();
        writer.print("<block>");
        if (history.hasPreviousPage())
        {
            PortletURL prevAction = response.createActionURL() ;
            prevAction.setParameter(BROWSER_ACTION_PARAM, BROWSER_ACTION_PREVIOUS_PAGE);
            writer.print(" [<a href=\"" + prevAction.toString() +"\">Previous Page</a>] ");
        }
        PortletURL refreshAction = response.createActionURL() ;
        refreshAction.setParameter(BROWSER_ACTION_PARAM, BROWSER_ACTION_REFRESH_PAGE);
        writer.print(" [<a href=\"" + refreshAction.toString() +"\">Refresh Page</a>] ");
        if (history.hasNextPage())
        {
            PortletURL nextAction = response.createActionURL() ;
            nextAction.setParameter(BROWSER_ACTION_PARAM, BROWSER_ACTION_NEXT_PAGE);
            writer.print(" [<a href=\"" + nextAction.toString() +"\">Next Page</a>] ");
        }
        writer.print("</block><hr/>");

        // drain the stream to the portlet window
        ByteArrayInputStream bais = new ByteArrayInputStream(content);
        drain(new InputStreamReader(bais, this.defaultEncoding), writer);
        bais.close();
        
        // done, cache results in the history and save the history
        history.visitPage(currentPage);
        PortletMessaging.publish(request, HISTORY, history);
    }

    public void doEdit(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {
        response.setContentType("text/html");
        doPreferencesEdit(request, response);
    }
        
    /*
     * Privaye helpers for generating WebContent
     */
    protected byte[] doWebContent(String sourceAttr, Map sourceParams, boolean isPost, RenderRequest request, RenderResponse response)
        throws PortletException
    {
        HttpMethod httpMethod = null ;
        
        try
        {
            // Set the action and base URLs in the rewriter
            PortletURL action = response.createActionURL();
            ((WebContentRewriter) rewriter).setActionURL(action);
            URL baseURL = new URL(sourceAttr);
            rewriter.setBaseUrl(baseURL.toString());
            
            // ...set up URL and HttpClient stuff
            HttpClient httpClient = getHttpClient(request) ;
            httpMethod = getHttpMethod(httpClient, getURLSource(sourceAttr, sourceParams, request, response), sourceParams, isPost, request);
            doPreemptiveAuthentication(httpClient, httpMethod, request, response);
            
            // ...get, cache, and return the content
            return doHttpWebContent(httpClient, httpMethod, 0, request, response);
        }
        catch (PortletException pex)
        {
            // already reported
            throw pex;
        }
        catch (Exception ex)
        {
            throw new PortletException("Exception while rewritting HTML content. Error: " + ex.getMessage());
        }
        finally
        {
            // release the http connection
            if (httpMethod != null)
                httpMethod.releaseConnection();
        }
    }

    protected byte[] doHttpWebContent(HttpClient httpClient, HttpMethod httpMethod, int retryCount, RenderRequest request, RenderResponse response)
            throws PortletException
    {
        try
        {
            // Get the input stream from the provided httpClient/httpMethod
            // System.out.println("WebContentPortlet.doHttpWebContent() - from path: "+httpMethod.getPath());
            
            // ...set up URL and HttpClient stuff
            httpClient.executeMethod(httpMethod);
            
            // ...reset base URL with fully resolved path (e.g. if a directory, path will end with a /, which it may not have in the call to this method)
            rewriter.setBaseUrl( rewriter.getBaseRelativeUrl( httpMethod.getPath() )) ;
            // System.out.println("...reset base URL from final path: "+httpMethod.getPath());
            
            // ...save updated state
            Cookie[] cookies = httpClient.getState().getCookies();
            PortletMessaging.publish(request, HTTP_STATE, cookies);
            // System.out.println("...saving: "+(cookies != null ? cookies.length : 0)+", cookies...");
            // for(int i=0,limit = cookies != null ? cookies.length : 0; i<limit; i++) System.out.println("...cookie["+i+"] is: "+cookies[i]);

            // ...check for manual redirects
            int responseCode = httpMethod.getStatusCode();
            if (responseCode >= 300 && responseCode <= 399)
            {
                // redirection that could not be handled automatically!!! (probably from a POST)
                Header locationHeader = httpMethod.getResponseHeader("location");
                String redirectLocation = locationHeader != null ? locationHeader.getValue() : null ;
                if (redirectLocation != null)
                {
                    // System.out.println("WebContentPortlet.doHttpWebContent() >>>handling redirect to: "+redirectLocation+"<<<");
                    
                    // one more time (assume most params are already encoded & new URL is using GET protocol!)
                    return doWebContent( redirectLocation, new HashMap(), false, request, response ) ;
                }
                else
                {
                    // The response is a redirect, but did not provide the new location for the resource.
                    throw new PortletException("Redirection code: "+responseCode+", but with no redirectionLocation set.");
                }
            }
            else if ( responseCode >= 400 )
            {
                if ( responseCode == 401 )
                {
                    if (httpMethod.getHostAuthState().isAuthRequested() && retryCount++ < 1 && doRequestedAuthentication( httpClient, httpMethod, request, response))
                    {
                        // try again, now that we are authorizied
                        return doHttpWebContent(httpClient, httpMethod, retryCount, request, response);
                    }
                    else
                    {
                        // could not authorize
                        throw new PortletException("Site requested authorization, but we are unable to provide credentials");
                    }
                }
                else if (retryCount++ < 3)
                {
                    log.info("WebContentPortlet.doHttpWebContent() - retrying: "+httpMethod.getPath()+", response code: "+responseCode);
                    
                    // retry
                    return doHttpWebContent(httpClient, httpMethod, retryCount, request, response);
                }
                else
                {
                    // bad
                    throw new PortletException("Failure reading: "+httpMethod.getPath()+", response code: "+responseCode);
                }
            }
            
            // System.out.println("...response code: "+responseCode+", fetching content as stream and rewriting.");
            
            // ...ok - *now* create the input stream and reader
            BufferedInputStream bis = new BufferedInputStream(httpMethod.getResponseBodyAsStream());
            String encoding = ((HttpMethodBase)httpMethod).getResponseCharSet();
            if (encoding == null)
                encoding = getContentCharSet(bis);
            Reader htmlReader = new InputStreamReader(bis, encoding);
            
            // get the output buffer
            if (encoding == null)
                encoding = this.defaultEncoding ;
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            Writer htmlWriter = new OutputStreamWriter(byteOutputStream, encoding);

            // rewrite and flush output
            rewriter.rewrite(rewriteController.createParserAdaptor("text/html"), htmlReader, htmlWriter);
            htmlWriter.flush();

            // Page has been rewritten
            // TODO: Write it to cache
            return byteOutputStream.toByteArray();
        }
        catch (UnsupportedEncodingException ueex)
        {
            throw new PortletException("Encoding " + defaultEncoding + " not supported. Error: " + ueex.getMessage());
        }
        catch (RewriterException rwe)
        {
            throw new PortletException("Failed to rewrite HTML page. Error: " + rwe.getMessage());
        }
        catch (Exception e)
        {
            throw new PortletException("Exception while rewritting HTML page. Error: " + e.getMessage());
        }
    }
    
    protected String getURLSource(String source, Map params, RenderRequest request, RenderResponse response)
    {
        return source;    
    }
    
    protected boolean doPreemptiveAuthentication(HttpClient clent,HttpMethod method, RenderRequest request, RenderResponse response)
    {
        // derived class responsibilty - return true, if credentials have been set
        return false ;
    }
    
    protected boolean doRequestedAuthentication(HttpClient clent,HttpMethod method, RenderRequest request, RenderResponse response)
    {
        // derived class responsibilty - return true, if credentials have been set
        return false ;
    }

    /*
     * Generate a rewrite controller using the basic rules file
     */
    private RewriterController getController(String contextPath) throws Exception
    {
        Class[] rewriterClasses = new Class[]
        { WebContentRewriter.class, WebContentRewriter.class};
        
        Class[] adaptorClasses = new Class[]
        { NekoParserAdaptor.class, SaxParserAdaptor.class};
        RewriterController rwc = new JetspeedRewriterController(contextPath + "conf/rewriter-rules-mapping.xml", Arrays
                .asList(rewriterClasses), Arrays.asList(adaptorClasses));

        FileReader reader = new FileReader(contextPath + "conf/default-rewriter-rules.xml");

        Ruleset ruleset = rwc.loadRuleset(reader);
        reader.close();
        rewriter = rwc.createRewriter(ruleset);
        return rwc;
    }

    protected HttpClient getHttpClient(RenderRequest request) throws IOException
    {
        // derived class hook (e.g. to set up Basic Authentication)
        HttpClient client = new HttpClient();
        
        // reuse existing state, if we have been here before
        Cookie[] cookies = (Cookie[])PortletMessaging.receive(request, HTTP_STATE);
        if (cookies != null)
        {
            // ...so far, just saving cookies - may need a more complex Serializable object here
            client.getState().addCookies(cookies);

            // System.out.println("WebContentPortlet.getHttpClient() - reusing: "+cookies.length+", cookies...");
            // for(int i=0,limit = cookies.length; i<limit; i++) System.out.println("...cookie["+i+"] is: "+cookies[i]);
        }
 
        return client ;
    }
    
    protected HttpMethodBase getHttpMethod(HttpClient client, String uri, Map params, boolean isPost, RenderRequest request) throws IOException
    {
        HttpMethodBase httpMethod = null;
        String useragentProperty = request.getProperty("User-Agent");
        if (!isPost)
        {
            // System.out.println("WebContentPortlet.getHttpMethod() - HTTP GET from URL: "+uri);
            
            // http GET
            httpMethod = new GetMethod(uri);
            if (params != null && !params.isEmpty())
            {
                ArrayList pairs = new ArrayList();
                Iterator iter = params.entrySet().iterator();
                while (iter.hasNext())
                {
                    Map.Entry entry = (Map.Entry)iter.next() ;
                    String name = (String)entry.getKey() ;
                    String[] values = (String [])entry.getValue() ;
                    if (values != null)
                        for (int i = 0,limit = values.length; i < limit; i++)
                        {
                            // System.out.println("...adding >>>GET parameter: "+name+", with value: "+values[i]+"<<<");
                            pairs.add(new NameValuePair(name, values[i]));
                        }
                }
                httpMethod.setQueryString((NameValuePair[])pairs.toArray(new NameValuePair[pairs.size()]));
            }
            
            // automatically follow redirects (NOTE: not supported in POST - will throw exeception if you ask for it, then sees a redirect!!)
            httpMethod.setFollowRedirects(true);
        }
        else
        {
            // System.out.println("WebContentPortlet.getHttpMethod() - HTTP POST to URL: "+uri);
            
            // http POST
            PostMethod postMethod = (PostMethod)( httpMethod = new PostMethod(uri)) ; 
            if (params != null && !params.isEmpty())
            {
                Iterator iter = params.entrySet().iterator();
                while (iter.hasNext())
                {
                    Map.Entry entry = (Map.Entry)iter.next();
                    String name = (String)entry.getKey(); 
                    String[] values = (String[])entry.getValue();
                    if (values != null)
                        for (int i=0,limit=values.length; i<limit; i++)
                        {
                            // System.out.println("...adding >>>POST parameter: "+name+", with value: "+values[i]+"<<<");
                            
                            postMethod.addParameter(name, values[i]);
                        }
                }   
            }
        }
        
        // propagate User-Agent, so target site does not think we are a D.O.S. attack
        httpMethod.addRequestHeader( "User-Agent", useragentProperty );
        
        // BOZO - DON'T do this.   default policy seems to be more flexible!!!
        //httpMethod.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        
        // ...ready to use!
        return httpMethod ;
    }


    static final int BLOCK_SIZE = 4096;

    private void drain(InputStream reader, OutputStream writer) throws IOException
    {
        byte[] bytes = new byte[BLOCK_SIZE];
        try
        {
            int length = reader.read(bytes);
            while (length != -1)
            {
                if (length != 0)
                {
                    writer.write(bytes, 0, length);
                }
                length = reader.read(bytes);
            }
        }
        finally
        {
            bytes = null;
        }
    }

    private void drain(Reader r, Writer w) throws IOException
    {
        char[] bytes = new char[BLOCK_SIZE];
        try
        {
            int length = r.read(bytes);
            while (length != -1)
            {
                if (length != 0)
                {
                    w.write(bytes, 0, length);
                }
                length = r.read(bytes);
            }
        }
        finally
        {
            bytes = null;
        }

    }

    private void drain(Reader r, OutputStream os) throws IOException
    {
        Writer w = new OutputStreamWriter(os);
        drain(r, w);
        w.flush();
    }

    private String getContentCharSet(InputStream is) throws IOException
    {
        if (!is.markSupported())
        {
            return null;
        }

        byte[] buf = new byte[BLOCK_SIZE];
        try
        {
            is.mark(BLOCK_SIZE);
            is.read(buf, 0, BLOCK_SIZE);
            String content = new String(buf, "ISO-8859-1");
            String lowerCaseContent = content.toLowerCase();
            int startIndex = lowerCaseContent.indexOf("<head");
            if (startIndex == -1)
            {
                startIndex = 0;
            }
            int endIndex = lowerCaseContent.indexOf("</head");
            if (endIndex == -1)
            {
                endIndex = content.length();
            }
            content = content.substring(startIndex, endIndex);

            StringTokenizer st = new StringTokenizer(content, "<>");
            while (st.hasMoreTokens())
            {
                String element = st.nextToken();
                String lowerCaseElement = element.toLowerCase();
                if (lowerCaseElement.startsWith("meta") && lowerCaseElement.indexOf("content-type") > 0)
                {
                    StringTokenizer est = new StringTokenizer(element, " =\"\';");
                    while (est.hasMoreTokens())
                    {
                        if (est.nextToken().equalsIgnoreCase("charset"))
                        {
                            if (est.hasMoreTokens())
                            {
                                return est.nextToken();
                            }
                        }
                    }
                }
            }
        }
        catch (IOException e)
        {
        }
        finally
        {
            is.reset();
        }

        return null;
    }
}
