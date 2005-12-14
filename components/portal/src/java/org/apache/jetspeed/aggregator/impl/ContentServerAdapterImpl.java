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
package org.apache.jetspeed.aggregator.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.jetspeed.aggregator.ContentServerAdapter;
import org.apache.jetspeed.contentserver.ContentFilter;
import org.apache.jetspeed.headerresource.HeaderResource;
import org.apache.jetspeed.headerresource.HeaderResourceFactory;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.request.RequestContext;

/**
 * <p>
 * The Content Server Adapter encapsulates all aggregated related
 * activities related to aggregation, lessening the coupling of the
 * aggregator to the content server, which can be disabled.
 * </p>
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class ContentServerAdapterImpl implements ContentServerAdapter
{
    private HeaderResourceFactory headerResourceFactory;
    private List fallBackContentPathes;
    
    public ContentServerAdapterImpl(HeaderResourceFactory headerResourceFactory, 
                                    List fallBackContentPathes)
    {
        this.headerResourceFactory = headerResourceFactory;
        this.fallBackContentPathes = fallBackContentPathes;
    }
    
    public void prepareContentPaths(RequestContext context, ContentPage page)
    {
        ContentFragment root = page.getRootContentFragment();
        
        String layoutDecorator = root.getDecorator();
        if (layoutDecorator == null)
        {
            layoutDecorator = page.getDefaultDecorator(root.getType());
        }

        String defaultPortletDecorator = page.getDefaultDecorator(ContentFragment.PORTLET);
       
        List contentPathes = (List) context.getSessionAttribute(ContentFilter.SESSION_CONTENT_PATH_ATTR);

        if (contentPathes == null)
        {
            contentPathes = new ArrayList(2);
            context.setSessionAttribute(ContentFilter.SESSION_CONTENT_PATH_ATTR, contentPathes);
        }
        String mediaType = context.getCapabilityMap().getPreferredMediaType().getName();
        if (contentPathes.size() < 1)
        {
            // define the lookup order

            contentPathes.add(root.getType() + "/" + mediaType + "/" + layoutDecorator);
            // Start added by jamesliao, 27-05-2005
            contentPathes.add(ContentFragment.PORTLET + "/" + mediaType + "/" + defaultPortletDecorator);
            // End
            
            Iterator defaults = fallBackContentPathes.iterator();
            while (defaults.hasNext())
            {
                String path = (String) defaults.next();
                contentPathes.add(path.replaceAll("\\{mediaType\\}", mediaType));
            }

        }
        else
        {
            contentPathes.set(0, root.getType() + "/" + mediaType + "/" + layoutDecorator);
            // Start added by jamesliao, 27-05-2005, override the previous portlet-decorator
            contentPathes.set(1, ContentFragment.PORTLET + "/" + mediaType + "/" + defaultPortletDecorator);
            // End
        }
        
        if (layoutDecorator != null)
        {
            addStyle(context, layoutDecorator, ContentFragment.LAYOUT);
        }                
    }
    
    public void addStyle( RequestContext context, 
                           String decoratorName, 
                           String decoratorType )
    {
        // TODO: is this factory necessary or can we assemble header resource with Spring
/*
        HeaderResource headerResource = headerResourceFactory.getHeaderResouce(context);
        
        if (decoratorType.equals(ContentFragment.LAYOUT))
        {
            headerResource.addStyleSheet("content/css/styles.css");
        }
        else
        {
            headerResource.addStyleSheet("content/" + decoratorName + "/css/styles.css");
        }
*/
    }    
}
