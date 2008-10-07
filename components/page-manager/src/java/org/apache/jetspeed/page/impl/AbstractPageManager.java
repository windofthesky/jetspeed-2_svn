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
package org.apache.jetspeed.page.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.idgenerator.IdGenerator;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.Property;
import org.apache.jetspeed.om.page.psml.FragmentImpl;
import org.apache.jetspeed.om.page.psml.PageImpl;
import org.apache.jetspeed.om.page.psml.PropertyImpl;
import org.apache.jetspeed.page.PageManager;
import org.picocontainer.Startable;

/**
 * AbstractPageManagerService
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public abstract class AbstractPageManager 
    implements PageManager, Startable    
{
    private final static Log log = LogFactory.getLog(AbstractPageManager.class);
    
    protected Class fragmentClass = FragmentImpl.class;
    protected Class pageClass = PageImpl.class;
    protected Class propertyClass = PropertyImpl.class;
    protected IdGenerator generator = null;

    public AbstractPageManager(IdGenerator generator)
    {    
        this.generator = generator;
    }
    
    public AbstractPageManager(IdGenerator generator, List modelClasses)
    {
        this.generator = generator;        
        if (modelClasses.size() > 0)
        {
            this.fragmentClass = (Class)modelClasses.get(0);
            if (modelClasses.size() > 1)
            {
                this.pageClass  = (Class)modelClasses.get(1);
                if (modelClasses.size() > 2)
                {
                    this.propertyClass  = (Class)modelClasses.get(2);
                }                
            }
        }                                 
    }
    
    public void start()
    {
    }
    
    public void stop()
    {
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.services.page.PageManagerService#newPage()
     */
    public Page newPage()
    {
        Page page = null;
        try
        {
            // factory create the page
            page = (Page)createObject(this.pageClass);            
            page.setId(generator.getNextPeid());
            
            // create the default fragment
            Fragment fragment = (Fragment)createObject(this.fragmentClass);
            fragment.setId(generator.getNextPeid());
            fragment.setType(Fragment.LAYOUT);
            page.setRootFragment(fragment);            
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create page object for " + this.pageClass;
            log.error(message, e);
        }
        return page;        
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.services.page.PageManagerService#newFragment()
     */
    public Fragment newFragment()
    {
        Fragment fragment = null;
        try
        {
            fragment = (Fragment)createObject(this.fragmentClass);
            fragment.setId(generator.getNextPeid());
            fragment.setType(Fragment.LAYOUT);
            
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create page object for " + this.pageClass;
            log.error(message, e);
            // throw new JetspeedException(message, e);
        }
        return fragment;        
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.services.page.PageManagerService#newProperty()
     */
    public Property newProperty()
    {
        Property property = null;
        try
        {
            property = (Property)createObject(this.propertyClass);
            
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create fragment-property object for " + this.propertyClass;
            log.error(message, e);
            // throw new JetspeedException(message, e);
        }
        return property;        
    }

    public Object createObject(Class classe)
    {
        Object object = null;
        try
        {
            object = classe.newInstance();
        }
        catch (Exception e)
        {
            log.error("Factory failed to create object: " + classe.getName(), e);            
        }
        
        return object;        
    }
    
}
