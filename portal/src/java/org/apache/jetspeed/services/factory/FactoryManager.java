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
package org.apache.jetspeed.services.factory;

import org.apache.jetspeed.cps.CommonPortletServices;
import org.apache.pluto.factory.Factory;
import org.apache.pluto.services.factory.FactoryManagerService;


/**
 * Convenience static accessor for {@link FactoryManagementService}
 *  
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 * @see FactoryManagementService
 */
public abstract class FactoryManager
{
    /**
     * Utility method for accessing the service 
     * implementation
     *
     * @return a FactoryManagerService implementation instance
     */
    protected static FactoryManagementService getServiceImpl()
    {
        return (FactoryManagementService)CommonPortletServices
            .getInstance().getService(FactoryManagementService.SERVICE_NAME);
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.services.factory.FactoryManagerService#getFactory(java.lang.Class)
     */    
    public static Factory getFactory (Class theClass)
    {
        return getServiceImpl().getFactory(theClass);
    }

    /**
     * Return an adaptor class to what Pluto expects as a container service
     * @return FactoryManagerService The Pluto compliant service
     */
    public static FactoryManagerService getService()
    {
        return (FactoryManagerService)getServiceImpl();
    }

}
