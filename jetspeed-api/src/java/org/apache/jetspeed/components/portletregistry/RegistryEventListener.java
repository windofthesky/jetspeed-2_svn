/*
 * Copyright 2005 The Apache Software Foundation.
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
package org.apache.jetspeed.components.portletregistry;

import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;

/**
 * This interface describes the page manager event listener
 * that is notified when a managed node is updated or removed
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public interface RegistryEventListener
{
    /**
     * applicationUpdated - invoked when the definition of a portlet application is
     *           updated by the registry or when the
     *           state modification is otherwise detected
     *
     * @param MutablePortletApplicaiton new managed application 
     */
    void applicationUpdated(MutablePortletApplication app);

    /**
     * portletUpdated - invoked when the definition of a portlet definition is
     *           updated by the registry or when the
     *           state modification is otherwise detected
     *
     * @param PortletDefinitionComposite new managed portlet definition 
     */
    void portletUpdated(PortletDefinitionComposite def);

    /**
     * applicationRemoved - invoked when the definition of a portlet application is
     *           removed by the registry
     *           
     * @param MutablePortletApplicaiton removed portlet application 
     */
    void applicationRemoved(MutablePortletApplication app);

    /**
     * portletUpdated - invoked when the definition of a portlet definition is
     *           removed by the registry 
     *
     * @param PortletDefinitionComposite new managed portlet definition if known
     */
    void portletRemoved(PortletDefinitionComposite def);
    
}
