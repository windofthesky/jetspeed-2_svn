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
package org.apache.jetspeed.deployment;

import java.io.File;


/**
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * 
 * Interface that provides for firing and dispatching deployment realted events.
 *
 */
public interface DeploymentManager
{
    DeploymentStatus deploy(File aFile) throws DeploymentException;
    /**
     * 
     * <p>
     * fireDeploymentEvent
     * </p>
     * Fires all deployment events registered to this DeploymentManager.
     *
     */
    void fireDeploymentEvent();
    
    /**
     * 
     * <p>
     * dispatch
     * </p>
     * 
     * dispatches the DeploymentEvent to all registered deployment event listeners.
     *
     * @param event {@link DeploymentEvent} to dispatch.
     */
    void dispatch( DeploymentEvent event );
}
