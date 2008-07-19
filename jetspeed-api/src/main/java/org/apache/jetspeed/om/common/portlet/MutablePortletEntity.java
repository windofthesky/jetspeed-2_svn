/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.om.common.portlet;

import org.apache.jetspeed.om.page.Fragment;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.pluto.om.entity.PortletEntityCtrl;


/**
 * MutablePortletEntity
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface MutablePortletEntity 
    extends 
        PortletEntity, 
        PortletEntityCtrl
{

    public static final String PORTLET_ENTITY_ROOT = "portlet_entity";
    
    Long getOid();
    
    String getPortletUniqueName();
    
    void setFragment(Fragment fragment);

    /**
     * <p>
     * Persistence callback to allow a PortletEntity instance to persist children
     * objects (like portlet preferences) <em>within</em> the same transaction.
     * </p>
     * <p>
     * This method must be called <em>always</em> from the #store() method. Using a callback from
     * the persistence manager might not be reliable when the PortletEntity <em>itself</em>
     * isn't changed but children might.
     * </p>
     * <p>
     * Notably condition when this might happen is the Pluto 1.0.1 preferences handling calling
     * the portletEntity store() method
     * */
    void storeChildren();
}
