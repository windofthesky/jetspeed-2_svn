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

package org.apache.cornerstone.framework.sysman;

import javax.management.Descriptor;
import javax.management.modelmbean.DescriptorSupport;

import org.apache.cornerstone.framework.api.sysman.IModelMBeanDescriptorBuilder;
import org.apache.cornerstone.framework.singleton.Singleton;
import org.apache.log4j.Logger;

/**
 * The Main builder for creating various desriptor objects for
 * Operations, attricutes, notifcation, and mbeans of a particular
 * Object that wishes to be modelled as an Model MBean.
 *
 *
 *
 */

public class ModelMBeanDescriptorBuilder extends Singleton implements IModelMBeanDescriptorBuilder
{
    public static final String REVISION = "$Revision$";
    
    // Model MBean Descriptor fields
    //
    //
    public static final String NAME = "name";
    public static final String DESCRIPTOR_TYPE = "descriptorType";
    public static final String DESCRIPTOR_TYPE_ATTRIBUTE = "attribute";
    public static final String DESCRIPTOR_TYPE_OPERATION = "operation";
    public static final String DESCRIPTOR_TYPE_MBEAN = "mbean";
    public static final String DESCRIPTOR_TYPE_NOTIFICATION = "notification";
    public static final String DISPLAY_NAME = "displayName";
    public static final String PERSIST_POLICY = "persistPolicy";
    public static final String PERSIST_POLICY_ON_UPDATE = "OnUpdate";
    public static final String PERSIST_POLICY_ON_TIMER = "OnTimer";
    public static final String PERSIST_POLICY_NO_MORE_OFTEN_THAN = "NoMoreOftenThan";
    public static final String PERSIST_POLICY_DEFAULT_AS_NEVER = "Never";
    public static final String PERSIST_POLICY_AS_ALWAYS = "Always";
    public static final String PERSIST_PERIOD = "persistPeriod";
    public static final String PERSIST_PERIOD_IN_SECONDS = "10";
    public static final String PERSIST_LOCATION = "persistLocation";
    public static final String PERSIST_LOCATION_DEFAULT_DIR = ".";
    public static final String LOG = "log";
    public static final String LOG_ALL = "t";
    public static final String LOG_NONE = "f";
    public static final String LOG_FILE = "logFile";
    public static final String PERSIST_NAME = "persist_name";
    
    // Model MBean Attribute level desriptor
    //
    //
    public static final String GET_METHOD = "getMethod";
    public static final String SET_METHOD = "setMethod";
    public static final String CURRENCY_TIME_LIMIT = "currencyTimeLimit";
    public static final String CURRENCY_TIME_LIMIT_NEVER = "-1";
    public static final String CURRENCY_TIME_LIMIT_ALWAYS = "0";
    public static final String CURRENCY_TIME_LIMIT_10 = "10";
    
    // Model MBean Operations fields
    //
    //
    public static final String ROLE = "role";
    public static final String ROLE_GETTER = "getter";
    public static final String ROLE_SETTER = "setter";
    public static final String ROLE_OPERATION = "operation";
    public static final String ROLE_CONSTRUCTOR = "constructor";
    public static final String TARGET_OBJECT = "targetObject";
    public static final String TARGET_TYPE = "targetType";
    public static final String TARGET_TYPE_OBJECT_REFERENCE = "ObjectReference";
    public static final String CLASS = "class";
    public static final String DEFAULT = "default";
    
    /**
    * Returns a Singleton instance of ModelMBeanInfoBuilder
    * 
    * @return ModelMBeanInfoBuilder singleton
    */
    public static ModelMBeanDescriptorBuilder getSingleton()
    {
        return _singleton;
    }
    
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
    )
    {
        Descriptor desc = new DescriptorSupport();

        if( name != null ) desc.setField(NAME,name );
        desc.setField(DESCRIPTOR_TYPE,DESCRIPTOR_TYPE_ATTRIBUTE);
        if( displayName != null ) desc.setField(DISPLAY_NAME, displayName );
        if( getter != null ) desc.setField(GET_METHOD, getter );
        if( setter != null ) desc.setField(SET_METHOD, setter );
        if( currency != null ) desc.setField(CURRENCY_TIME_LIMIT, currency );
        if( persistPolicy != null ) desc.setField(PERSIST_POLICY, persistPolicy );
        if( persistPeriod != null ) desc.setField(PERSIST_PERIOD, persistPeriod );
        if( defaultValue != null ) desc.setField(DEFAULT, defaultValue );

        return desc;
    }

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
    )
    {
        Descriptor desc = new DescriptorSupport();

        if( name != null ) desc.setField(NAME,name );
        desc.setField(DESCRIPTOR_TYPE,DESCRIPTOR_TYPE_OPERATION);
        if( displayName != null ) desc.setField(DISPLAY_NAME, displayName );
        if( role != null ) desc.setField(ROLE, role );
        if( targetObject != null ) desc.setField(TARGET_OBJECT, targetObject );
        if( targetType != null ) desc.setField(TARGET_TYPE, targetType );
        if( ownerClass != null ) desc.setField(CLASS, ownerClass );
        if( currency != null ) desc.setField(CURRENCY_TIME_LIMIT, currency );

        return desc;
    }

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
    )
    {
        Descriptor desc = new DescriptorSupport();

        if( name != null ) desc.setField(NAME,name );
        desc.setField(DESCRIPTOR_TYPE,DESCRIPTOR_TYPE_MBEAN);
        if( displayName != null ) desc.setField(DISPLAY_NAME, displayName );
        if( persistLocation != null ) desc.setField(PERSIST_LOCATION, persistLocation );
        if( persistName != null ) desc.setField(PERSIST_NAME, persistName );
        if( log != null ) desc.setField(LOG, log );
        if( persistPolicy != null ) desc.setField(PERSIST_POLICY, persistPolicy );
        if( persistPeriod != null ) desc.setField(PERSIST_PERIOD, persistPeriod );
        if( logFile != null ) desc.setField(LOG_FILE, logFile );

        return desc;
    }

    private static Logger _Logger = Logger.getLogger(ModelMBeanDescriptorBuilder.class);
    private static ModelMBeanDescriptorBuilder _singleton = new ModelMBeanDescriptorBuilder();
}