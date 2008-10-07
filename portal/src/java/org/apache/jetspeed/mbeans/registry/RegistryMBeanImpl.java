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
package org.apache.jetspeed.mbeans.registry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.components.portletregistry.PortletRegistryComponent;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;

/**
 * 
 * RegistryMBean 
 * @author <a href="mailto:jford@apache.org">Jeremy Ford</a> 
 * @version $Id$
 *
 */
public class RegistryMBeanImpl implements RegistryMBean
{
    private String name = "myname";
    private String classname = "myclassname";

    /* (non-Javadoc)
     * @see org.apache.jetspeed.mbeans.registry.RegistryMBean#getName()
     */
    public String getName()
    {
        // TODO Auto-generated method stub
        return name;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.mbeans.registry.RegistryMBean#getClassName()
     */
    public String getClassname()
    {
        // TODO Auto-generated method stub
        return classname;
    }

    /**
     * @see org.apache.jetspeed.mbeans.registry.RegistryMBean#getPortletList()
     */
    public List getPortlets()
    {
        PortletRegistryComponent regsitry =
            (PortletRegistryComponent) Jetspeed.getComponentManager().getComponent(PortletRegistryComponent.class);
        List portlets = regsitry.getAllPortletDefinitions();
        ArrayList list = new ArrayList(portlets.size());
        Iterator itr = portlets.iterator();
        while (itr.hasNext())
        {
            list.add(((PortletDefinitionComposite) itr.next()).getName());
        }

        return list;
    }

}
