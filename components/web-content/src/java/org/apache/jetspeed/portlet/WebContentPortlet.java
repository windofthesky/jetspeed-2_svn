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
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.jetspeed.rewriter.JetspeedRewriterController;
import org.apache.jetspeed.rewriter.RewriterController;
import org.apache.jetspeed.rewriter.RewriterException;
import org.apache.jetspeed.rewriter.RulesetRewriter;
import org.apache.jetspeed.rewriter.WebContentRewriter;
import org.apache.jetspeed.rewriter.html.SwingParserAdaptor;
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
    public static final String LAST_URL = "webcontent.url.last";
    public static final String LAST_STATE = "webcontent.last.state";
    public static final String CACHE = "webcontent.cache";

    /** Default encoding */
    public String defaultEncoding = "UTF-8";

    /* SSO settings */
    boolean isSSOEnabled = false;

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
        String webContentParameter = actionRequest.getParameter(WebContentRewriter.ACTION_PARAMETER_URL);
        
        if (webContentParameter == null || actionRequest.getPortletMode() == PortletMode.EDIT)
        {
            processPreferencesAction(actionRequest, actionResponse);            
            webContentParameter = actionRequest.getPreferences().getValue("SRC", "http://portals.apache.org");
        }
        

        /*
         * If the webContentParameter is not empty attach the URL to the session
         */
        if (webContentParameter != null && webContentParameter.length() > 0)
        {
            String sessionObj = new String(webContentParameter);
            PortletMessaging.publish(actionRequest, CURRENT_URL, sessionObj);
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
        String lastURL = null;
        boolean useCache = false;
        
        // Check if the source was defined in the session
        try
        {
            sourceURL = (String)PortletMessaging.receive(request, CURRENT_URL);

            lastURL  = (String)PortletMessaging.receive(request, LAST_URL);
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
            useCache = true;
        }
        
        if (sourceURL == null)
        {
            // Use the URL defined in the preferences
            sourceURL =  request.getPreferences().getValue("SRC", "");
        }

        if (lastURL != null && sourceURL.equals(lastURL))
        {
            useCache = true;
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

        String lastState = (String)PortletMessaging.receive(request, LAST_STATE);
        String newState = request.getWindowState().toString();
        if (lastState == null || newState == null || !lastState.equals(newState))
        {
            useCache = false;
        }

        // Set the content type
        response.setContentType("text/html");
        byte[] content;
        byte[] cache = (byte[])request.getPortletSession().getAttribute(CACHE, PortletSession.PORTLET_SCOPE);
        if (useCache && cache != null)
        {
            content = cache;            
        }
        else
        {
            // Draw the content            
            content = doWebContent(request, sourceURL, response);
            request.getPortletSession().setAttribute(CACHE, content, PortletSession.PORTLET_SCOPE);
        }

        // drain the stream to the portlet window
        ByteArrayInputStream bais = new ByteArrayInputStream(content);
        drain(new InputStreamReader(bais, this.defaultEncoding), response.getWriter());
        bais.close();
        
        // Done just save the last URL
        lastURL = sourceURL;
        PortletMessaging.publish(request, LAST_URL, lastURL);
        PortletMessaging.publish(request, LAST_STATE, newState);
    }

    public void doEdit(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {
        response.setContentType("text/html");
        doPreferencesEdit(request, response);
    }
        
    /*
     * Privaye helpers for generating WebContent
     */
    protected byte[] doWebContent(RenderRequest request, String sourceAttr, RenderResponse response)
            throws PortletException
    {
        // Initialization
        Writer htmlWriter = null;

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();

        try
        {
            htmlWriter = new OutputStreamWriter(byteOutputStream, this.defaultEncoding);

            // Set the action URL in the rewriter
           PortletURL action = response.createActionURL();
           ((WebContentRewriter) rewriter).setActionURL(action);

            URL baseURL = new URL(sourceAttr);
            String baseurl = baseURL.getProtocol() + "://" + baseURL.getHost();

            rewriter.setBaseUrl(baseurl);
            String source = getURLSource(sourceAttr, request, response);
            // System.out.println("Rewriting SOURCE: " + source);
            rewriter.rewrite(rewriteController.createParserAdaptor("text/html"), getRemoteReader(source), htmlWriter);
            htmlWriter.flush();
            
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

        // Page has been rewritten
        // TODO: Write it to cache
        return byteOutputStream.toByteArray();
    }

    public String getURLSource(String source, RenderRequest request, RenderResponse response)
    {
        return source;    
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
        { SwingParserAdaptor.class, SaxParserAdaptor.class};
        RewriterController rwc = new JetspeedRewriterController(contextPath + "conf/rewriter-rules-mapping.xml", Arrays
                .asList(rewriterClasses), Arrays.asList(adaptorClasses));

        FileReader reader = new FileReader(contextPath + "conf/default-rewriter-rules.xml");

        Ruleset ruleset = rwc.loadRuleset(reader);
        reader.close();
        rewriter = rwc.createRewriter(ruleset);
        return rwc;
    }

    /*
     * getReaderForURL() Streams the page from the uRL into the reader
     */
    protected Reader getReader(String url) throws PortletException
    {
        URL pageUrl = null;
        URLConnection pageConn = null;

        // Open the connection to the page
        try
        {
            pageUrl = new URL(url);
            pageConn = pageUrl.openConnection();

            if (this.isSSOEnabled == true)
            {
                /*
                 * TODO: SSO should provide username & password
                 * 
                 * String username, password; // set HTTP Basic Authetication
                 * header if username and password are set if (username != null &&
                 * password !=null) {
                 * pageConn.setRequestProperty("Authorization", "Basic " +
                 * Base64.encodeAsString(username + ":" + password)); }
                 */
            }
        }
        catch (MalformedURLException urle)
        {
            throw new PortletException("Malformed URL. Error: " + urle.getMessage());
        }
        catch (IOException ioe)
        {
            throw new PortletException("Failed connecting to URL. Error: " + ioe.getMessage());
        }
        catch (Exception e)
        {
            throw new PortletException("Failed connecting to URL. Error: " + e.getMessage());
        }

        long pageExpiration = pageConn.getExpiration();
        String encoding = defaultEncoding;
        String contentType = pageConn.getContentType();
        String tempString = null;
        String noCache = "no-cache";

        if (contentType != null)
        {
            StringTokenizer st = new StringTokenizer(contentType, "; =");
            while (st.hasMoreTokens())
            {
                if (st.nextToken().equalsIgnoreCase("charset"))
                {
                    try
                    {
                        encoding = st.nextToken();
                        break;
                    }
                    catch (Exception e)
                    {
                        break;
                    }
                }
            }
        }

        Reader rdr = null;

        try
        {
            // Assign a reader
            rdr = new InputStreamReader(pageConn.getInputStream(), encoding);
        }
        catch (UnsupportedEncodingException ueex)
        {
            throw new PortletException("Encoding " + encoding + " not supported. Error: " + ueex.getMessage());
        }
        catch (IOException ioex)
        {
            throw new PortletException("Failed open stream to site " + url + " Error: " + ioex.getMessage());
        }

        return rdr;
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

    private Reader getRemoteReader(String uri) throws PortletException
    {
        try
        {
            HttpClient client = new HttpClient();
            GetMethod get = new GetMethod(uri);
            int status = client.executeMethod(get);
            BufferedInputStream bis = new BufferedInputStream(get.getResponseBodyAsStream());
            bis.mark(BLOCK_SIZE);
            String encoding = getContentCharSet(bis);
            if (encoding == null)
            {
                encoding = get.getResponseCharSet();
            }
            return new InputStreamReader(bis, encoding);
        }
        catch (IOException e)
        {
            throw new PortletException(e);
        }
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
                                is.reset();
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

        is.reset();

        return null;
    }
}
