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
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

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

//standard java stuff
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;

import java.io.Reader;

import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;

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

    /**
     * Default WebContent source attribute members.
     */
    private String defaultViewSource;
    private String defaultEditSource;

    /**
     * Action Parameter
     */

    /** WebContent Messages 
     *  TODO: this is a simple implementation
     *  until we introduce a more sophisticated caching algorithm 
     * 
     * */
    public static final String CURRENT_URL = "webcontent.url.current";
    public static final String CURRENT_URL_PARAMS = "webcontent.url.current.params";
    public static final String CURRENT_URL_METHOD = "webcontent.url.current.method";
    public static final String LAST_URL = "webcontent.url.last";
    public static final String LAST_URL_PARAMS = "webcontent.url.last.params";
    public static final String LAST_WINDOW_STATE = "webcontent.window.last.state";
    public static final String CACHE = "webcontent.cache";
    public static final String HTTP_STATE = "webcontent.http.state";
    
    // Class Data
    
    protected final static Log log = LogFactory.getLog(WebContentPortlet.class);

    /** Default encoding */
    public String defaultEncoding = "UTF-8";

    /* WebContent rewriter */
    RulesetRewriter rewriter = null;

    RewriterController rewriteController = null;

    
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

        // Check if an action parameter was defined        
        String webContentURL = actionRequest.getParameter(WebContentRewriter.ACTION_PARAMETER_URL);
        String webContentMethod = actionRequest.getParameter(WebContentRewriter.ACTION_PARAMETER_METHOD);
        
        if (webContentURL == null || actionRequest.getPortletMode() == PortletMode.EDIT)
        {
            processPreferencesAction(actionRequest, actionResponse);            
            webContentURL = actionRequest.getPreferences().getValue("SRC", "http://portals.apache.org");
        }

        /*
         * If the webContentParameter is not empty attach the URL to the session
         */
        if (webContentURL != null && webContentURL.length() > 0)
        {
            // Map BOZO String sessionObj = new String(webContentParameter);
            // getParameterMap() includes the URL (as ACTION_PARAMETER_URL), but all actual params as well
            Map params = actionRequest.getParameterMap() ;
            Map webContentParams = params != null ? new HashMap(params) : new HashMap() ;
            if (webContentMethod == null) webContentMethod = "" ;   // default to GET

            webContentParams.remove(WebContentRewriter.ACTION_PARAMETER_URL);
            webContentParams.remove(WebContentRewriter.ACTION_PARAMETER_METHOD);

            PortletMessaging.publish(actionRequest, CURRENT_URL, webContentURL);
            PortletMessaging.publish(actionRequest, CURRENT_URL_PARAMS, webContentParams);
            PortletMessaging.publish(actionRequest, CURRENT_URL_METHOD, webContentMethod);
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
        
        // Find the source URL to execute
        String sourceURL = null;
        Map sourceParams = null;
        String sourceMethod = null;
        String lastURL = null;
        Map lastParams = null;
        
        // Check if the source was defined in the session
        try
        {
            sourceURL = (String)PortletMessaging.receive(request, CURRENT_URL);
            sourceParams = (Map)PortletMessaging.receive(request, CURRENT_URL_PARAMS);
            sourceMethod = (String)PortletMessaging.receive(request, CURRENT_URL_METHOD);

            lastURL = (String)PortletMessaging.receive(request, LAST_URL);
            lastParams = (Map)PortletMessaging.receive(request, LAST_URL_PARAMS);
            // TODO: This is just a kludge. Filtering of bad uRL's should be
            // more sophisticated
            if (sourceURL.startsWith("/") || sourceURL.startsWith("..")) sourceURL = null;
        }
        catch (Exception e)
        {
            sourceURL = null;
        }

        // Check if the page was rendered at least once
        if (sourceURL == null && lastURL != null)
        {
            // Use the cache
            sourceURL = lastURL;
        }
        
        if (sourceURL == null)
        {
            // Use the URL defined in the preferences
            sourceURL =  request.getPreferences().getValue("SRC", "");
        }

        // If all above fails throw an error asking the user to define an URL in
        // edit mode
        if (sourceURL == null)
            throw new PortletException("WebContent source not specified. Go to edit mode and specify an URL.");

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
                throw new PortletException("WebContentProtlet failed to create rewriter controller. Error:"
                        + e.getMessage());
            }
        }

        String newWindowState = request.getWindowState().toString();
        String lastWindowState = (String)PortletMessaging.receive(request, LAST_WINDOW_STATE);

        // if *everything* is the same, we can use the cache
        boolean useCache = (lastURL != null && sourceURL.equals(lastURL) && lastParams != null && sourceParams != null && sourceParams.equals(lastParams) && lastWindowState != null && newWindowState != null && newWindowState.equals(lastWindowState));
        
        // Set the content type
        response.setContentType("text/html");
        byte[] content = useCache ? (byte[])request.getPortletSession().getAttribute(CACHE, PortletSession.PORTLET_SCOPE) : null;
        if (content != null)
        {
            // BOZO - no caching until we get everything else worked out (back button, etc)
            useCache = false;
            content = null;
            log.info("WebContentPortlet.doView() - cache available, but is being ignored, for now.");            
        }
        if (content == null)
        {
            // System.out.println("WebContentPortlet.doView() >>>fetching content from: "+sourceURL+", using method: "+sourceMethod+"<<<");
            
            // Draw the content            
            content = doWebContent(sourceURL, sourceParams, sourceMethod, request, response);
            request.getPortletSession().setAttribute(CACHE, content, PortletSession.PORTLET_SCOPE);
        }
        // else System.out.println("WebContentPortlet.doView() - Using *cached* content");

        // drain the stream to the portlet window
        ByteArrayInputStream bais = new ByteArrayInputStream(content);
        drain(new InputStreamReader(bais, this.defaultEncoding), response.getWriter());
        bais.close();
        
        // Done just save the last set of parameters
        PortletMessaging.publish(request, LAST_URL, sourceURL);
        PortletMessaging.publish(request, LAST_URL_PARAMS, sourceParams != null?sourceParams:new HashMap());
        PortletMessaging.publish(request, LAST_WINDOW_STATE, newWindowState);
    }

    public void doEdit(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {
        response.setContentType("text/html");
        doPreferencesEdit(request, response);
    }
        
    /*
     * Privaye helpers for generating WebContent
     */
    protected byte[] doWebContent(String sourceAttr, Map sourceParams, String sourceMethod, RenderRequest request, RenderResponse response)
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
            httpMethod = getHttpMethod(httpClient, getURLSource(sourceAttr, sourceParams, request, response), sourceParams, sourceMethod, request);
            doPreemptiveAuthentication(httpClient, httpMethod, request, response);
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
                    return doWebContent( redirectLocation, new HashMap(), "get", request, response ) ;
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
                else if (retryCount++ < 2)
                {
                    // System.out.println("WebContentPortlet.doHttpWebContent() - retrying: "+httpMethod.getPath()+", response code: "+responseCode);
                    
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
     * Get WebContent source preference value
     */
    private String getSourcePreference(RenderRequest request, String name, String defaultValue)
    {
        PortletPreferences prefs = request.getPreferences();
        return ((prefs != null) ? prefs.getValue(name, defaultValue) : defaultValue);
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
    
    protected HttpMethodBase getHttpMethod(HttpClient client, String uri, Map params, String method, RenderRequest request) throws IOException
    {
        HttpMethodBase httpMethod = null;
        String useragentProperty = request.getProperty("User-Agent");
        if (method == null || !method.equalsIgnoreCase("post"))
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
