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

package org.apache.cornerstone.framework.jmx;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import javax.management.Descriptor;
import javax.management.modelmbean.ModelMBeanInfo;
import javax.management.modelmbean.RequiredModelMBean;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.cornerstone.framework.core.BaseObject;
import org.apache.log4j.Logger;

/**
 * This wrapper-object is the Main object used for converting a regular java.lang.Object
 * into JMX Model MBean object.  The user of this class passes in a java.lang.Object, and
 * then in turn this wrapper object converts it into a Model MBean.  This Model Mbean can
 * using the getRequireModelMBean method. 
 *
 */

public class BaseModelMBean extends BaseObject
{
    public static final String REVISION = "$Revision$";

    public static final String CLASS = "class";
    public static final String CONFIG_PERSIST_POLICY = "persistPolicy";
    public static final String CONFIG_PERSIST_PERIOD_IN_SECONDS = "persisPeriodInSeconds";
    public static final String CONFIG_PERSIST_LOCATION_DEFAULT_DIR ="persistLocationDefaultDir";
    public static final String CONFIG_CURRENCY_TIME_LIMIT = "currencyTimeLimit";
    public static final String CONFIG_DEFAULT_NOTIFICATION = "defaultNotification";
    public static final String NOTIFICATION_TYPE = "default";
    public static final String CONFIG_DEFAULT_NOTIFICATION_DESCRITPTION = "defaultNotificationDescription";
    public static final String GET_METHOD_PREFIX = "get";
    public static final String SET_METHOD_PREFIX = "set";
    public static final String DEFAULT_METHOD_PARAMETER_NAME = "par";
    public static final String READ_METHOD_DESCRITPION = "This is a read ";
    public static final String WRITE_METHOD_DESCRITPION = "This is a write ";
    public static final String MAIN_METHOD = "main";
    public static final String OBJECT_REFERENCE = "ObjectReference";
     
    /**
     * Constructor for converting a java.lang.Object into Required model MBean.
     * 
     * @param objToBeModelledMBean
     */
    public BaseModelMBean(Object objToBeModelledMBean)
    {
        _modelMBeanInfoBuilder = (IModelMBeanInfoBuilder) ModelMBeanInfoBuilderFactory.getSingleton().createInstance();
        _modelMBeanDescriptorBuilder = (IModelMBeanDescriptorBuilder) ModelMBeanDescriptorBuilderFactory.getSingleton().createInstance();
                
        // obtain all the attributes on the object
        //
        //
        Class cls = objToBeModelledMBean.getClass();
        String className = cls.getName();
        
        PropertyDescriptor[] propDescriptors = PropertyUtils.getPropertyDescriptors(cls);

        for (int i= 0; i < propDescriptors.length; i++)
        {
            Descriptor attributeDescriptor = null;
            
            String currentProperty = propDescriptors[i].getName();
            
            boolean isReadableProperty = false; 
            boolean isWriteableProperty = false;
            
            // ignore the property if it is of type
            if ( currentProperty.equals(CLASS)) continue;
            
            String currentPropertyType = propDescriptors[i].getPropertyType().getName();
            
            Method readMethod = propDescriptors[i].getReadMethod();
            String readMethodReturnType = null;
            String readMethodName = null;
            if ( readMethod != null )
            {
                readMethodReturnType = readMethod.getReturnType().getName();
                readMethodName = readMethod.getName();
                isReadableProperty =true;
            }

            Method writeMethod = propDescriptors[i].getWriteMethod();
            String writeMethodName = null;
            if ( writeMethod != null )
            {
                writeMethodName = writeMethod.getName();
                isWriteableProperty = true;
            }

            // TODO: remove this code
            //Class[] clsParameters = writeMethod.getParameterTypes();
            //String[] writeMethodParameters = convertClassArrayToStringArrayOfClassNames(clsParameters);            
            //String[] defaultMethodParameterNames = generateDefaultNamesForMethodParameters(clsParameters);

            attributeDescriptor = _modelMBeanDescriptorBuilder.buildAttributeDescriptor(     
                currentProperty, 
                currentProperty, 
                getConfigProperty(CONFIG_PERSIST_POLICY), 
                getConfigProperty(CONFIG_PERSIST_PERIOD_IN_SECONDS), 
                null, 
                readMethodName, 
                writeMethodName, 
                getConfigProperty(CONFIG_CURRENCY_TIME_LIMIT)
            );

            // add the descriptor of the attribute to the model of the MBean
            _modelMBeanInfoBuilder.addModelMBeanAttribute(
                currentProperty,
                currentPropertyType,
                isReadableProperty,
                isWriteableProperty,
                false,
                currentProperty,
                attributeDescriptor
            );
        }

        // Determine the operations that exist on the objToBeModelledMBean
        Method[] methods = cls.getDeclaredMethods();
        
        // build operations attributes
        for (int i= 0; i < methods.length; i++)
        {
            String currentMethodName = methods[i].getName();
            
            // ignore main method
            if ( currentMethodName.equals(MAIN_METHOD)) continue;
            
            _modelMBeanInfoBuilder.addModelMBeanMethod(
                currentMethodName,
                currentMethodName,
                methods[i]
            );
        }

        // build ctor attributes
        Constructor[] constructors = cls.getDeclaredConstructors();
        
        for ( int i = 0; i < constructors.length; i++ )
        {
            String constructorName = constructors[i].getName();
            _modelMBeanInfoBuilder.addModelMBeanConstructor(constructorName,constructors[i]);
        }

        String[] notificationTypes = {NOTIFICATION_TYPE};
        _modelMBeanInfoBuilder.addModelMBeanNotification(
            notificationTypes,
            getConfigProperty(CONFIG_DEFAULT_NOTIFICATION),
            getConfigProperty(CONFIG_DEFAULT_NOTIFICATION_DESCRITPTION)                        
        );

        // build the mbean
        Descriptor mbeanDescriptor = null;

        mbeanDescriptor = _modelMBeanDescriptorBuilder.buildMBeanDescriptor(
            className,
            className,
            getConfigProperty(CONFIG_PERSIST_POLICY),
            getConfigProperty(CONFIG_PERSIST_PERIOD_IN_SECONDS),
            getConfigProperty(CONFIG_PERSIST_LOCATION_DEFAULT_DIR),
            className,
            null,
            null
        );

        ModelMBeanInfo modelMBeanInfo = null;
        try
        {
            modelMBeanInfo = _modelMBeanInfoBuilder.buildModelMBeanInfo(
                mbeanDescriptor, 
                className, 
                className
            );
                                            
            _requiredModelMBean = new RequiredModelMBean();        
            _requiredModelMBean.setManagedResource(objToBeModelledMBean,OBJECT_REFERENCE);
            _requiredModelMBean.setModelMBeanInfo(modelMBeanInfo);
        }
        catch(Exception e)
        {
            _Logger.error("An error occured in ModeMBean Constuction", e);
        }
    }

    /**
     * This method returns the Model MBean as required by the JMX "On the Fly"
     * specification.
     * 
     * @return
     */

    public RequiredModelMBean getRequiredModelMBean()
    {
        return _requiredModelMBean;
    }
    
    /**
     * Sets the RequiredModelMbean
     * 
     * @param requiredModelMBean
     */

    public void setRequiredModelMBean(RequiredModelMBean requiredModelMBean)
    {
        _requiredModelMBean = requiredModelMBean;
    }

    protected String[] convertClassArrayToStringArrayOfClassNames(Class[] clsArray)
    {
        String[] strArrayOfClassNames = new String[clsArray.length];
        for ( int i = 0; i < clsArray.length; i++ )
        {
            strArrayOfClassNames[i] = clsArray[i].getName();            
        }
        return     strArrayOfClassNames;
    }

    protected String[] generateDefaultNamesForMethodParameters(Class[] clsArray)
    {
        String[] strArrayOfDefaultparameterNames = new String[clsArray.length];
        for ( int i = 0; i < clsArray.length; i++ )
        {
            strArrayOfDefaultparameterNames[i] = DEFAULT_METHOD_PARAMETER_NAME + i;            
        }
        return     strArrayOfDefaultparameterNames;
    }

    protected String buildMethodNameWithPrefix(String fieldName,String prefix)
    {
        String absoluteMethodName = "";
        
        String firstCharOfField = fieldName.substring(0,1);
        String restOfFieldName = fieldName.substring(1,fieldName.length());
        absoluteMethodName = prefix + firstCharOfField.toUpperCase() + restOfFieldName;    
        return absoluteMethodName;        
    }

    private static Logger _Logger = Logger.getLogger(BaseModelMBean.class);
    protected IModelMBeanInfoBuilder _modelMBeanInfoBuilder = null;
    protected IModelMBeanDescriptorBuilder _modelMBeanDescriptorBuilder = null;
    protected RequiredModelMBean _requiredModelMBean = null;    
}