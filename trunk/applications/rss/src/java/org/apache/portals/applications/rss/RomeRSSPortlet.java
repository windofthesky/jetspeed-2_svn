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

import java.io.IOException;
import java.net.URL;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;

import org.apache.velocity.context.Context;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

/**
 * Rome RSS Portlet
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id$
 */
public class RomeRSSPortlet extends GenericVelocityPortlet
{

    protected Log log = LogFactory.getLog(RomeRSSPortlet.class);

    /**
     * @see javax.portlet.Portlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);

    }

    /**
     * @see javax.portlet.GenericPortlet#doView(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
     */
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {

        response.setContentType("text/html");
        Context velocityContext = this.getContext(request);
        PortletPreferences prefs = request.getPreferences();
        String url = prefs.getValue("url", "http://www.npr.org/rss/rss.php?topicId=4");
        try
        {
            URL feedUrl = new URL(url);
            SyndFeedInput input = new SyndFeedInput();

            SyndFeed feed = input.build(new XmlReader(feedUrl));

            RssInfo rssInfo = new RssInfo(feed, new Integer(prefs.getValue("itemdisplayed", "15")).intValue(), new Boolean(prefs
                    .getValue("openinpopup", "true")).booleanValue(), new Boolean(prefs.getValue("showdescription", "true"))
                    .booleanValue(), new Boolean(prefs.getValue("showtitle", "true")).booleanValue(), new Boolean(prefs.getValue(
                    "showtextinput", "true")).booleanValue());

            velocityContext.put("rssInfo", rssInfo);

            super.doView(request, response);

        }
        catch (Exception e)
        {
            throw new PortletException(new String("Failed to process RSS Feed: " + url + ", " + e));
        }

    }

    /**
     * 
     * @see javax.portlet.GenericPortlet#doEdit(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
     */
    public void doEdit(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {
        response.setContentType("text/html");
        doPreferencesEdit(request, response);
    }

    /**
     * 
     * @see javax.portlet.Portlet#processAction(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException, java.io.IOException
    {
        String add = request.getParameter("Save");
        if (add != null)
        { 
            processPreferencesAction(request, actionResponse);
        }
    }

}