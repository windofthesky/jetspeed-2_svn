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
package org.apache.portals.applications.rss;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.portals.applications.transform.TransformCacheEntry;
import org.apache.portals.applications.util.Streams;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;


/**
 * RSSPortlet
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id$
 */
public class RSSPortlet extends AbstractRssPortlet implements EntityResolver
{

    private Document document = null;

    private Map stylesheets = null;
    
    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);

        // load stylesheets available
        stylesheets = new HashMap();

        Enumeration e = this.getPortletConfig().getInitParameterNames();
        while (e.hasMoreElements())
        {
            String name = (String) e.nextElement();
            String base = "text/html";

            if (name.startsWith("stylesheet"))
            {
                int idx = -1;
                if ((idx = name.indexOf(".")) > -1)
                {
                    base = name.substring(idx + 1, name.length());
                }
                stylesheets.put(base, getPortletConfig().getInitParameter(name));
            }
        }
    }

    public InputSource resolveEntity(String publicId, String systemId)
    {
        try
        {
            //access Jetspeed cache and get a java.io.Reader
            Reader rdr = openURL(publicId);
            InputSource is = new InputSource(rdr);
            is.setPublicId(publicId);
            is.setSystemId(systemId);
            return is;
        }
        catch (IOException x)
        {
            System.err.println("Entity Resolution error: ( " + publicId + " Taking " + systemId + " from cache throwed Exception: " + x);

        }
        return null;
    }

    private Reader openURL(String urlPath) throws IOException
    {
        URL url = new URL(urlPath);
        URLConnection conn = url.openConnection();

        String enc = conn.getContentEncoding();
        if (enc == null)
        {
            enc = "ISO-8859-1";
        }

        BufferedInputStream is = new BufferedInputStream(conn.getInputStream());
        is.mark(20480);
        BufferedReader asciiReader = new BufferedReader(new InputStreamReader(is, "ASCII"));
        String decl = asciiReader.readLine();
        String key = "encoding=\"";
        if (decl != null)
        {
            int off = decl.indexOf(key);
            if (off > 0)
            {
                enc = decl.substring(off + key.length(), decl.indexOf('"', off + key.length()));
            }
        }
        //Reset the bytes read
        is.reset();
        Reader rdr = new InputStreamReader(is, enc);
        return rdr;
    }    
    
    public Document getDocument(String url)
    throws Exception
    {
        DocumentBuilder parser = null;
        
        // read content, clean it, parse it and cache the DOM try { final
        DocumentBuilderFactory docfactory = DocumentBuilderFactory.newInstance(); //Have it non-validating
        docfactory.setValidating(false); 
        parser = docfactory.newDocumentBuilder(); 
        parser.setEntityResolver(this);

        Reader rdr = openURL(url);
        InputSource isrc = new InputSource(rdr);
        isrc.setSystemId(url);
        this.document = parser.parse(isrc);
        return document;        
    }
    
    
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {
        response.setContentType("text/html");
        
        
        PortletPreferences prefs = request.getPreferences();
            
        // TODO: use stylesheet based on mimetype            
        String stylesheet = getPortletConfig().getInitParameter("stylesheet");
        String realStylesheet = getPortletConfig().getPortletContext().getRealPath(stylesheet);
        String url = prefs.getValue("url", "http://news.bbc.co.uk/rss/sportonline_uk_edition/football/internationals/england/squad_profiles/rss091.xml");        

        String key = cache.constructKey(url, stylesheet); // TODO: use the entire parameter list
        TransformCacheEntry entry = cache.get(key);
        if (entry != null)
        {
            byte[] bytes = (byte[])entry.getDocument();
            Streams.drain(new StringReader(new String(bytes,"UTF-8")),  response.getWriter());
        }
        else
        {
            try
            {
                Document document = getDocument(url);
                InputSource source = new InputSource(url);
                source.setSystemId(url); 
                source.setEncoding("UTF-8");                
                
                Map parameters = new HashMap();            
                parameters.put("itemdisplayed", prefs.getValue("itemdisplayed", "15"));
                parameters.put("openinpopup", prefs.getValue("openinpopup", "true"));
                parameters.put("showdescription", prefs.getValue("showdescription", "true"));
                parameters.put("showtitle", prefs.getValue("showtitle", "true"));
                parameters.put("showtextinput", prefs.getValue("showtextinput", "true"));
                           
                StringWriter sw= new StringWriter();
                transform.transform(realStylesheet, source, sw, parameters); //response.getPortletOutputStream(), parameters);
                Streams.drain(new StringReader(sw.toString()), response.getWriter());
                try
                {
                	// Java 1.5 only
                    // String t = document.getDocumentElement().getElementsByTagName("title").item(0).getTextContent();
                    String t = document.getDocumentElement().getElementsByTagName("title").item(0).getNodeValue();
                    NodeList nodes = document.getDocumentElement().getElementsByTagName("title");
                    if (nodes != null)
                    {
                        Node node = nodes.item(0);
                        if (node != null)
                        {
                            Node title = node.getFirstChild();
                            if (title != null)
                                response.setTitle(title.getNodeValue());
                        }
                    }
                }
                catch(Exception e)
                {
                	
                }
                
                cache.put(key, sw.toString().getBytes("UTF-8"), 15);                
            }
            catch (Exception ex)
            {
                response.getPortletOutputStream().write(new String("Failed to process RSS Feed: " + url + ", " + ex).getBytes());
            }
        }
    }
    
}