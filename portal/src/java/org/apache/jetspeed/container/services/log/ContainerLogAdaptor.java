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
package org.apache.jetspeed.container.services.log;


import org.apache.jetspeed.cps.CommonPortletServices;
import org.apache.pluto.services.log.LogService;

/**
 * Adapts Jetspeed logging onto Pluto's container logging facility.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public abstract class ContainerLogAdaptor
{
    /**
     * Utility method for accessing the service 
     * implementation
     *
     * @return a UniqueIdService implementation instance
     */
    protected static ContainerLogAdaptorService getServiceImpl()
    {
        return (ContainerLogAdaptorService)CommonPortletServices
            .getInstance().getService(ContainerLogAdaptorService.SERVICE_NAME);
    }

    /**
     * Return a log adaptor class to what Pluto expects as a container service
     * @return LogService The Pluto compliant service
     */
    public static LogService getService()
    {
        return (LogService)getServiceImpl();
    }
    
}
