/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.services.page.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.cps.BaseCommonService;
import org.apache.jetspeed.cps.CPSInitializationException;
import org.apache.jetspeed.cps.CommonPortletServices;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.Property;
import org.apache.jetspeed.persistence.PersistencePlugin;
import org.apache.jetspeed.persistence.PersistenceService;
import org.apache.jetspeed.services.page.PageManagerService;

/**
 * DatabasePageManagerService
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class DatabasePageManagerService
    extends BaseCommonService
    implements PageManagerService
{
    protected final static Log log = LogFactory.getLog(DatabasePageManagerService.class);
    
    private PersistencePlugin plugin;

    private PersistencePlugin originalPlugin;

    private String originalAlias;
    
    private Class fragmentClass = null;
    private Class pageClass = null;
    
    /* (non-Javadoc)
     * @see org.apache.fulcrum.Service#init()
     */
    public void init() throws CPSInitializationException
    {
        if (!isInitialized())
        {
            PersistenceService ps = (PersistenceService) CommonPortletServices.getPortalService(PersistenceService.SERVICE_NAME);
            String pluginName = getConfiguration().getString("persistence.plugin.name", "jetspeed");

            plugin = ps.getPersistencePlugin(pluginName);

            pageClass = this.loadModelClass("page.impl");
            fragmentClass = this.loadModelClass("fragment.impl");
            
            setInit(true);
        }
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
           // page = (Page)createObject(this.pageClass);
            
            // set the id
           // page.setId(JetspeedIdGenerator.getNextPeid());
            
            // create the default fragment
            // Fragment fragment = createObject(this.fragmentClass)
            // fragment.setId(JetspeedIdGenerator.getNextPeid());
            // fragment.setType(Fragment.LAYOUT);
            // page.setRootFragment(f);            
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create page object for " + this.pageClass;
            log.error(message, e);
            //throw new JetspeedException(message, e);
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
            // factory create the page
            // fragment = (Fragment)createObject(this.fragmentClass);

            // fragment.setId(JetspeedIdGenerator.getNextPeid());
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
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.services.page.PageManagerService#getPage(java.lang.String)
     */
    public Page getPage(String id)
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.services.page.PageManagerService#listPages()
     */
    public List listPages()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.services.page.PageManagerService#registerPage(org.apache.jetspeed.om.page.Page)
     */
    public void registerPage(Page page) throws JetspeedException
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.services.page.PageManagerService#updatePage(org.apache.jetspeed.om.page.Page)
     */
    public void updatePage(Page page) throws JetspeedException
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.services.page.PageManagerService#removePage(org.apache.jetspeed.om.page.Page)
     */
    public void removePage(Page page)
    {
        // TODO Auto-generated method stub
    }
    
    /**
     * Load an implementation class from the configuration.
     * 
     * @param configurationName
     * @return
     * @throws CPSInitializationException
     */
    private Class loadModelClass(String configurationName)
    throws CPSInitializationException
    {
        String className = getConfiguration().getString(configurationName, null);
        if (null == className)
        {
            throw new CPSInitializationException(configurationName + " implementation configuration not found.");
        }

        try
        {
            return Class.forName(className);
        }
        catch (ClassNotFoundException e)
        {
            throw new CPSInitializationException("Could not preload " + className + " implementation class.", e);
        }            
    }
    
    /**
     * Creates page model objects given the class. 
     * Throws exceptions if the class is not found in the default class path, 
     * or the class is not an instance of CmsObject.
     * 
     * @param classe the class of object
     * @return the newly created object
     * @throws ContentManagementException
     */
    private Object createObject(Class classe)
    throws JetspeedException    
    {
        Object object = null;
        try
        {
            object = classe.newInstance();
        }
        catch (Exception e)
        {
            throw new JetspeedException("Syndication Model Factory failed to create model object: " + 
                                        classe.getName(), e);            
        }
        
        return object;        
    }
}
