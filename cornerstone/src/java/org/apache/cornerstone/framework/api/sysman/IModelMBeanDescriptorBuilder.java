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

package org.apache.cornerstone.framework.api.sysman;

import javax.management.Descriptor;

/**
 * This is the main interface for building JMX "On the fly" Model Mbeans
 * Descriptors.
 *
 */
public interface IModelMBeanDescriptorBuilder
{
    /**
     * This method returns the attribute description for the
     * specified attribute name, type, etc.
     *  
     * @param name this is the name of the attribute
     * @param displayName the name used to display the attribute
     * @param persistPolicy the persist policy @see ModelMBeanDescriptorBuilder
     * @param persistPeriod the period of persistance @see ModelMBeanDescriptorBuilder
     * @param defaultValue
     * @param getter the name of the getter method for this attribute
     * @param setter the name of the setter method for this attribute
     * @param currency @see ModelMBeanDescriptorBuilder
     * @return the attribute descriptor.
     */
    public Descriptor buildAttributeDescriptor(
        String name, 
        String displayName, 
        String persistPolicy, 
        String persistPeriod, 
        Object defaultValue, 
        String getter, 
        String setter, 
        String currency
    );

    /**
     * This builds a descriptor for methods on an object.
     * 
     * @param name the name of the mbean
     * @param displayName
     * 
     * @param role the role of this operation, 
     * ie is it a "getter", "setter", "operation"
     * or "constructor"
     * 
     * @param targetObject object on which to execute this operation
     * @param targetType possible values are : "ObjectReference"
     * 
     * @param ownerClass
     * @param currency
     * @return
     */                                                
    public Descriptor buildOperationDescriptor(
        String name, 
        String displayName, 
        String role, 
        Object targetObject, 
        Object targetType, 
        String ownerClass, 
        String currency
    );

    /**
     * Builds a descriptor for the Model MBean
     * 
     * @param name the name of the model mbean
     * @param displayName the display name for the model mbean
     * @param persistPolicy @see ModelMBeanDescriptorBuilder
     * @param persistPeriod @see ModelMBeanDescriptorBuilder
     * @param persistLocation where you want to persist the mbean
     * @param persistName the filename of the persistant state
     * @param log can take two values t - log all notifications
     * f - logs none @see ModelMBeanDescriptorBuilder
     * @param logFile the fully qualified path for a log file
     * 
     * @return the mbean descriptor
     * 
     */                                            
    public Descriptor buildMBeanDescriptor(
        String name, 
        String displayName, 
        String persistPolicy, 
        String persistPeriod, 
        String persistLocation, 
        String persistName, 
        String log, 
        String logFile
    );
}