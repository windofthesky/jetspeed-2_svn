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
package org.apache.jetspeed.components;

import java.util.Collection;

/**
 * ContainerManagement interfaces defines operations to lookup containers, list containers,
 * and to manage the life cycle of the underlying Jetspeed IoC containers.
 *
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: ContainerManagement.java 225607 2005-07-27 20:25:36Z weaver $
 */
public interface ContainerManagement
{
    /**
     * Retrieve a Jetspeed IoC container by name
     *
     * @param containerName the name of the IoC container
     * @return the IoC implementation facade
     */
    Object getContainer(String containerName);

    /**
     * Retrieve the root Jetspeed IoC container
     *
     * @return the root container
     */
    Object getRootContainer();


    /**
     * Retrieve all IoC containers configured in Jetspeed
     *
     * @return a list of containers. Needs to be cast to implementing container class
     */
    Collection getContainers();

    /**
     * Stop the Jetspeed IoC container
     */
    void stop();

    /**
     * Start the Jetspeed IoC container
     */
    void start();   
}
