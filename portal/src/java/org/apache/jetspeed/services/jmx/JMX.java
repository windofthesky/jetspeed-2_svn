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
package org.apache.jetspeed.services.jmx;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.commons.modeler.ManagedBean;
import org.apache.jetspeed.cps.CommonPortletServices;



/**
 * 
 * RegistryMBean 
 * @author <a href="mailto:jford@apache.org">Jeremy Ford</a> 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class JMX
{
    /**
     * Commodity method for getting a reference to the service
     * singleton
     */
    
    /**
     * Makes sure the service is started.  Usually called to force the JMX service
     * to initialize so the remote requests, not directyly using the service, can be
     * remotely serviced.
     */
    public static void startJMX()
    {
        getService();
    }
    
    public static MBeanServer getMBeanServer()
    {
        return getService().getMBeanServer();
    }
    
    private static JetspeedJMXService getService()
    {
        return (JetspeedJMXService) CommonPortletServices.getPortalService(JetspeedJMXService.SERVICE_NAME);
    }
    public static String[] getManagedBeans()
    {
        return getService().getManagedBeans();
    }

    public static ManagedBean getManagedBean(String name)
    {
        return getService().getManagedBean(name);
    }

    public static Object getAttribute(ObjectName beanName, String attribute)
    {
        return getService().getAttribute(beanName, attribute);

    }
    
    public static ObjectName resolveObjectName(String simpleName)
    {
        return (ObjectName) getService().resolveObjectName(simpleName);
    }



}
