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

import org.apache.fulcrum.Service;

/**
 * <P>Common Service</P>
 *
 * Marks a common service. Could be useful when we replace Fulcrum with another service manager.
 * 
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 * 
 */
public interface CommonService extends Service
{
    /**
     * Load an implementation class from the configuration.
     * 
     * @param configurationName
     * @return
     * @throws CPSInitializationException
     */
    public Class loadModelClass(String configurationName)
    throws CPSInitializationException;
    
    /**
     * Creates objects given the class. 
     * Throws exceptions if the class is not found in the default class path, 
     * or the class is not an instance of CmsObject.
     * 
     * @param classe the class of object
     * @return the newly created object
     * @throws ContentManagementException
     */    
    public Object createObject(Class classe);
    
}