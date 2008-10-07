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
package org.apache.jetspeed.cps;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.BaseService;

/**
 * <P>Base Common Service</P>
 *
 * Marks a common service. Could be useful when we replace Fulcrum with another service manager.
 * 
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 * 
 */
public abstract class BaseCommonService extends BaseService implements CommonService
{
    private static Map modelClasses = new HashMap();
    protected final static Log log = LogFactory.getLog(BaseCommonService.class);

    /**
     * Load an implementation class from the configuration.
     * 
     * @param configurationName
     * @return
     * @throws CPSInitializationException
     */
    public Class loadModelClass(String configurationName)
    throws CPSInitializationException
    {
        String className = getConfiguration().getString(configurationName, null);
        if (null == className)
        {
            throw new CPSInitializationException(configurationName + " implementation configuration not found.");
        }

        try
        {
            Class classe = (Class)modelClasses.get(className);
            if (null == classe)
            {
                classe = Class.forName(className);
                modelClasses.put(className, classe);
            }
            return classe;
            
        }
        catch (ClassNotFoundException e)
        {
            throw new CPSInitializationException("Could not preload " + className + " implementation class.", e);
        }            
    }

    /**
     * Creates objects given the class. 
     * Throws exceptions if the class is not found in the default class path, 
     * or the class is not an instance of CmsObject.
     * 
     * @param classe the class of object
     * @return the newly created object
     * @throws ContentManagementException
     */    
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