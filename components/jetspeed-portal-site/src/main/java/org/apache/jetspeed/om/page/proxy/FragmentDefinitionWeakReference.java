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

import org.apache.jetspeed.om.page.FragmentDefinition;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.PageNotFoundException;
import org.apache.jetspeed.page.document.NodeException;

/**
 * This class references PSML FragmentDefinition instances weakly so
 * that site views do not hold onto instances that would otherwise
 * be reaped from the heap.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class FragmentDefinitionWeakReference
{
    private PageManager pageManager;
    private String path;
    private volatile WeakReference<FragmentDefinition> referentFragmentDefinition;
    
    /**
     * Construct fragment definition reference capturing page manager.
     * 
     * @param pageManager
     * @param fragmentDefinition
     */
    public FragmentDefinitionWeakReference(PageManager pageManager, FragmentDefinition fragmentDefinition)
    {
        this.pageManager = pageManager;
        this.path = fragmentDefinition.getPath();
        this.referentFragmentDefinition = new WeakReference<FragmentDefinition>(fragmentDefinition);
    }
    
    /**
     * Get or retrieve referent fragment definition.
     * 
     * @return fragment definition
     */
    public FragmentDefinition getFragmentDefinition()
    {
        FragmentDefinition fragmentDefinition = referentFragmentDefinition.get();
        if ((fragmentDefinition != null) && !fragmentDefinition.isStale())
        {
            return fragmentDefinition;
        }
        else
        {
            try
            {
                referentFragmentDefinition = new WeakReference<FragmentDefinition>(pageManager.getFragmentDefinition(path));
                return referentFragmentDefinition.get();
            }
            catch (PageNotFoundException pnfe)
            {
                throw new RuntimeException("FragmentDefinition "+path+" has been removed: "+pnfe, pnfe);
            }
            catch (NodeException ne)
            {
                throw new RuntimeException("FragmentDefinition "+path+" can not be accessed: "+ne, ne);
            }
        }
    }
}
