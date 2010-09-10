/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.om.page.proxy;

import java.lang.ref.WeakReference;

import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.document.DocumentNotFoundException;
import org.apache.jetspeed.page.document.NodeException;

/**
 * This class references PSML Link instances weakly so that
 * site views do not hold onto instances that would otherwise
 * be reaped from the heap.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class LinkWeakReference
{
    private PageManager pageManager;
    private String path;
    private volatile WeakReference<Link> referentLink;
    
    /**
     * Construct link reference capturing page manager.
     * 
     * @param pageManager
     * @param link
     */
    public LinkWeakReference(PageManager pageManager, Link link)
    {
        this.pageManager = pageManager;
        this.path = link.getPath();
        this.referentLink = new WeakReference<Link>(link);
    }
    
    /**
     * Get or retrieve referent link.
     * 
     * @return link
     */
    public Link getLink()
    {
        Link link = referentLink.get();
        if ((link != null) && !link.isStale())
        {
            return link;
        }
        else
        {
            try
            {
                referentLink = new WeakReference<Link>(pageManager.getLink(path));
                return referentLink.get();
            }
            catch (DocumentNotFoundException dnfe)
            {
                throw new RuntimeException("Link "+path+" has been removed: "+dnfe, dnfe);
            }
            catch (NodeException ne)
            {
                throw new RuntimeException("Link "+path+" can not be accessed: "+ne, ne);
            }
        }
    }
}
