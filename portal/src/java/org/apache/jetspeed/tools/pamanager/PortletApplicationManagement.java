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
package org.apache.jetspeed.tools.pamanager;

import org.apache.pluto.om.portlet.PortletDefinition;

/**
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver</a>
 *
 */
public interface PortletApplicationManagement extends Deployment, Registration // , Lifecycle
{	
    /**
     * 
     * <p>
     * clearPortletEntities
     * </p>
     * 
     * Removes ALL portlet entity and user preference information for a specific
     * {@link org.apache.pluto.om.portlet.PortletDefinition}. The removal 
     * <strong>IS PERMENANT</strong> short of restoring a image of persistence that
     * was taken BEFORE the removal occurred.
     *
     * @param portlet
     */
    void clearPortletEntities(PortletDefinition portlet);
}
