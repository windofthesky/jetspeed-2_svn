/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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