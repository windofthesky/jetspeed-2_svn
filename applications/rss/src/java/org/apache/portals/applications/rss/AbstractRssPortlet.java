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

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.portals.applications.rss.servlets.SpringInitServlet;
import org.apache.portals.applications.transform.Transform;
import org.apache.portals.applications.transform.TransformCache;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.springframework.beans.factory.BeanFactory;


/**
 * AbstractRssPortlet
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public abstract class AbstractRssPortlet extends GenericVelocityPortlet
{
    protected TransformCache cache;
    protected Transform transform;

    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);
        BeanFactory factory = SpringInitServlet.getSpringFactory();
        cache = (TransformCache)factory.getBean("transformCache");
        transform = (Transform)factory.getBean("transform");           
    }

    public void doEdit(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {
        response.setContentType("text/html");        
        doPreferencesEdit(request, response);
    }

    /**
     * Save the prefs
     */
    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException
    {
        processPreferencesAction(request, actionResponse);
        String url = request.getPreferences().getValue("url", "http://www.npr.org/rss/rss.php?topicId=4");
        String stylesheet = getPortletConfig().getInitParameter("stylesheet");
        String key = cache.constructKey(url, stylesheet); // TODO: use the entire parameter list        
        cache.remove(key);
    }
    
}
