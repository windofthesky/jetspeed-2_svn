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
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;

import org.apache.jetspeed.rewriter.JetspeedRewriterController;
import org.apache.jetspeed.rewriter.RewriterController;
import org.apache.jetspeed.rewriter.RewriterException;
import org.apache.jetspeed.rewriter.RulesetRewriter;
import org.apache.jetspeed.rewriter.RulesetRewriterImpl;
import org.apache.jetspeed.rewriter.WebContentRewriter;
import org.apache.jetspeed.rewriter.html.SwingParserAdaptor;
import org.apache.jetspeed.rewriter.rules.Ruleset;
import org.apache.jetspeed.rewriter.xml.SaxParserAdaptor;

//standard java stuff
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

    /** WebContent Session Parameter */
    public static final String SESSION_PARAMETER = "WCSP";

    /** Default encoding */
    public String defaultEncoding = "iso-8859-1";

    /* Internal Cache */
    private String lastURL = null;

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

        defaultViewSource = config.getInitParameter(VIEW_SOURCE_PARAM);
        if (defaultViewSource == null) defaultViewSource = "http://www.apache.org";

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
        
        if (actionRequest.getPortletMode() == PortletMode.EDIT)
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
            actionRequest.getPortletSession().setAttribute(WebContentPortlet.SESSION_PARAMETER, sessionObj,
                    PortletSession.APPLICATION_SCOPE);
        }
    }

    /**
     * doView Renders the URL in the following order 1) SESSION_PARAMETER
     * 2)cached version 3) defined for preference SRC
     */
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {
        // Find the source URL to execute
        String sourceURL = null;

        // Check if the source was defined in the session
        try
        {
            sourceURL = (String) request.getPortletSession().getAttribute(WebContentPortlet.SESSION_PARAMETER,
                    PortletSession.APPLICATION_SCOPE);

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
            sourceURL = defaultViewSource;
        }

        // If all above fails throw an error asking the user to define an URL in
        // edit mode
        if (sourceURL == null)
                throw new PortletException("WebContent source not specified. Go to edit mode and specify an URL.");

        // Initialize the controller if it's not already done
        if (rewriteController == null)
        {
            // Extract context path
            String pathTranslated = ((HttpServletRequest) ((HttpServletRequestWrapper) request).getRequest())
                    .getPathTranslated();
            String contextPath = request.getContextPath();

            contextPath = pathTranslated.substring(0, pathTranslated.indexOf("webapps") + 7) + contextPath
                    + "/WEB-INF/";

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

        // Set the content type
        response.setContentType("text/html");
        // Draw the content
        byte[] content = doWebContent(request, sourceURL, response);
        ByteArrayInputStream bais = new ByteArrayInputStream(content);
        drain(bais, response.getPortletOutputStream());
        bais.close();

        // Done just save the last URL
        lastURL = sourceURL;

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

        // Rewriter
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();

        Reader htmlReader = getReader(sourceAttr);

        try
        {
            htmlWriter = new OutputStreamWriter(byteOutputStream, this.defaultEncoding);

            // Set the action URL in the rewriter
            ((WebContentRewriter) rewriter).setActionURL(response.createActionURL());

            URL baseURL = new URL(sourceAttr);
            String baseurl = baseURL.getProtocol() + "://" + baseURL.getHost();
            //          TODO: Remove debug
            System.out.println("BaseURL: " + baseurl);

            ((WebContentRewriter) rewriter).setBaseURL(baseurl);

            // drain(getReader(sourceAttr), byteOutputStream);
            rewriter.rewrite(rewriteController.createParserAdaptor("text/html"), getReader(sourceAttr), htmlWriter);
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

}