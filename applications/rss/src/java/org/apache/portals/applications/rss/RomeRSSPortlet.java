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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.portals.applications.transform.TransformCacheEntry;
import org.apache.portals.applications.util.Streams;
import org.w3c.dom.Document;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.SyndFeedOutput;
import com.sun.syndication.io.XmlReader;

/**
 * Rome RSS Portlet
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id$
 */
public class RomeRSSPortlet extends AbstractRssPortlet
{
    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see javax.portlet.GenericPortlet#doView(javax.portlet.RenderRequest,
     *      javax.portlet.RenderResponse)
     */
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {
        response.setContentType("text/html");
        
        PortletPreferences prefs = request.getPreferences();        
        String url = prefs.getValue("url", "http://www.npr.org/rss/rss.php?topicId=4");
        String stylesheet = getPortletConfig().getInitParameter("stylesheet");
        String realStylesheet = getPortletConfig().getPortletContext().getRealPath(stylesheet);

        URL xslt = getPortletConfig().getPortletContext().getResource(stylesheet);
        
        String key = cache.constructKey(url, stylesheet); // TODO: use the entire parameter list
        TransformCacheEntry entry = cache.get(key);
        if (entry != null)
        {
            byte[] bytes = (byte[])entry.getDocument();
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            Streams.drain(bais, response.getPortletOutputStream());
            bais.close();
        }
        else
        {        
            try
            {
                URL feedUrl = new URL(url);
                SyndFeedInput input = new SyndFeedInput();
                SyndFeed feed = input.build(new XmlReader(feedUrl));
                SyndFeedOutput output = new SyndFeedOutput();            
                //output.output(feed, response.getWriter());
                Document document = output.outputW3CDom(feed);
    
                Map parameters = new HashMap();
                
                parameters.put("itemdisplayed", prefs.getValue("itemdisplayed", "15"));
                parameters.put("openinpopup", prefs.getValue("openinpopup", "true"));
                parameters.put("showdescription", prefs.getValue("showdescription", "true"));
                parameters.put("showtitle", prefs.getValue("showtitle", "true"));
                parameters.put("showtextinput", prefs.getValue("showtextinput", "true"));
                
                
                // TODO: don't use a transform, instead use Velocity template and populate via Rome model
                
                ByteArrayOutputStream baos = new ByteArrayOutputStream();                
                transform.transform(realStylesheet, document, baos, parameters); 
                byte[] bytes = baos.toByteArray();
                ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                Streams.drain(bais, response.getPortletOutputStream());                
                cache.put(key, bytes, 15);
            }
            catch (Exception e)
            {
                response.getPortletOutputStream().write(new String("Failed to process RSS Feed: " + url + ", " + e).getBytes());
            }
        }
    }
}