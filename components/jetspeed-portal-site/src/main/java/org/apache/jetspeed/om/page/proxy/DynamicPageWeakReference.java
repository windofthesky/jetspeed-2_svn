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

import org.apache.jetspeed.om.page.DynamicPage;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.PageNotFoundException;
import org.apache.jetspeed.page.document.NodeException;

/**
 * This class references PSML DynamicPage instances weakly so that
 * site views do not hold onto instances that would otherwise
 * be reaped from the heap.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class DynamicPageWeakReference
{
    private PageManager pageManager;
    private String path;
    private volatile WeakReference<DynamicPage> referentDynamicPage;
    
    /**
     * Construct dynamic page reference capturing page manager.
     * 
     * @param pageManager
     * @param dynamicPage
     */
    public DynamicPageWeakReference(PageManager pageManager, DynamicPage dynamicPage)
    {
        this.pageManager = pageManager;
        this.path = dynamicPage.getPath();
        this.referentDynamicPage = new WeakReference<DynamicPage>(dynamicPage);
    }
    
    /**
     * Get or retrieve referent dynamic page.
     * 
     * @return dynamic page
     */
    public DynamicPage getDynamicPage()
    {
        DynamicPage dynamicPage = referentDynamicPage.get();
        if ((dynamicPage != null) && !dynamicPage.isStale())
        {
            return dynamicPage;
        }
        else
        {
            try
            {
                referentDynamicPage = new WeakReference<DynamicPage>(pageManager.getDynamicPage(path));
                return referentDynamicPage.get();
            }
            catch (PageNotFoundException pnfe)
            {
                throw new RuntimeException("DynamicPage "+path+" has been removed: "+pnfe, pnfe);
            }
            catch (NodeException ne)
            {
                throw new RuntimeException("DynamicPage "+path+" can not be accessed: "+ne, ne);
            }
        }
    }
}
