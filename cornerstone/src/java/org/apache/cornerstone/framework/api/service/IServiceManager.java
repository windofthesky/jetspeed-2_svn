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

package org.apache.cornerstone.framework.api.service;

public interface IServiceManager
{
    public static final String REVISION = "$Revision$";

//    /**
//     * Gets new instance of service by its class name.
//     * @param className class name of service
//     * @return new instance of service class
//     */
//    public IService createServiceByClassName(String className)
//        throws ServiceException;
//
//    /**
//     * Gets new instance of service by its factory class name.
//     * @param factoryClassName class name of service factory
//     * @return new instance created by calling createInstance() on
//     *   service factory.
//     */
//    public IService createServiceByFactoryClassName(String factoryClassName)
//        throws ServiceException;

    /**
     * Gets new instance of service by its registry name.
     * @param name registry name of service
     * @return new instance of service class
     */
    public IService createServiceByName(String name)
        throws ServiceException;
}