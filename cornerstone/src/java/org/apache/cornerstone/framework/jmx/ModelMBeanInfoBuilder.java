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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.management.Descriptor;
import javax.management.MBeanFeatureInfo;
import javax.management.MBeanParameterInfo;
import javax.management.modelmbean.ModelMBeanAttributeInfo;
import javax.management.modelmbean.ModelMBeanConstructorInfo;
import javax.management.modelmbean.ModelMBeanInfo;
import javax.management.modelmbean.ModelMBeanInfoSupport;
import javax.management.modelmbean.ModelMBeanNotificationInfo;
import javax.management.modelmbean.ModelMBeanOperationInfo;
import org.apache.log4j.Logger;

public class ModelMBeanInfoBuilder implements IModelMBeanInfoBuilder
{
    public static final String REVISION = "$Revision$";

    public static final String JAVAX_MODELBEAN_INFO = "javax.management.modelmbean.ModelMBeanInfo";
    public static final String DESCRIPTION = "description";

    public static final String MODEL_MBEAN_ATTRIBUTE_INFO = "ModelMBeanAttributeInfo";
    public static final String MODEL_MBEAN_OPERATION_INFO = "ModelMBeanOperationInfo";
    public static final String MODEL_MBEAN_CONSTRUCTOR_INFO = "ModelMBeanConstructorInfo";
    public static final String MODEL_MBEAN_NOTIFICATION_INFO = "ModelMBeanNotificationInfo";

    public ModelMBeanInfoBuilder()
    {
    }
    
    /**
     * This method adds a method model to the model mbean
     * This method require a descriptor to be built using the
     * IModelMBeanDescriptorBuilder, @see IModelMBeanDescriptorBuilder
     * 
     * @param name the name of the method
     * @param paramTypes the parameters types of the 
     * @param paramNames the parameter names
     * @param paramDescs the paramtere descriptions
     * @param description description of the method
     * @param rtype the type of the return
     * @param type @see MBeanOperationInfo.INFO
     * @param desc the manually buildt descriptor using
     * ModelMBeanDescriptorBuilder @see ModelMBeanDescriptorBuilder
     * 
     */

    public void addModelMBeanMethod(
        String name, 
        String[] paramTypes, 
        String[] paramNames, 
        String[] paramDescs, 
        String description, 
        String rtype, 
        int type, 
        Descriptor desc
    )
    {
        MBeanParameterInfo[] params = null;

        if( paramTypes != null )
        {
            params = new MBeanParameterInfo[ paramTypes.length ];
            for( int i = 0; i < paramTypes.length; i++ )
            {
                params[i] = new MBeanParameterInfo(paramNames[i], paramTypes[i], paramDescs[i]);
            }
        }

        _operations.put(name, new ModelMBeanOperationInfo(name, description, params, rtype, type, desc));
    }

    /**
     * This method adds a mbean model to the model builder
     * this method assumes a default descripotor for the 
     * method.
     * 
     * @param name
     * @param description
     * @param method
     */

    public void addModelMBeanMethod(String name, String description, Method method)
    {
        ModelMBeanOperationInfo mBeanOperationInfo = new ModelMBeanOperationInfo(description,method);
        _operations.put(name,mBeanOperationInfo);
    }

    /**
     * This is method allows the addition of a Notificaiton model to the Builder
     * 
     * @param type
     * @param className
     * @param description
     * @param desc
     */    

    public void addModelMBeanNotification(String[] type, String className, String description, Descriptor desc)
    {
        ModelMBeanNotificationInfo modelMBenaNotificationInfo = new ModelMBeanNotificationInfo(
            type, className, description, desc
        );
 
        _notifications.put( className,  modelMBenaNotificationInfo );
    }
    
    /**
     * This is method allows the addition of a Notificaiton model to the Builder
     * It assumes a default descriptor.
     * 
     * @param types
     * @param name
     * @param description
     */

    public void addModelMBeanNotification(String[] types, String name, String description)
    {
        ModelMBeanNotificationInfo modelMBeanNotificationInfo = new    ModelMBeanNotificationInfo(
            types, name, description
        );
        _notifications.put(name, modelMBeanNotificationInfo);
    }

    /**
     * Adds an attribute Model to the Model Builder witha  manually created
     * Attribute Descriptor Object.  @see ModelMBeanDescriptorBuilder for building
     * Descriptors.
     * 
     * @param fname
     * @param ftype
     * @param read
     * @param write
     * @param is
     * @param description
     * @param desc
     */    

    public void addModelMBeanAttribute(
        String fname,
        String ftype, 
        boolean read, 
        boolean write, 
        boolean is, 
        String description, 
        Descriptor desc
    )
    {
        ModelMBeanAttributeInfo modelMBeanAttributeInfo = new ModelMBeanAttributeInfo(
            fname, ftype, description, read, write, is,    desc
        );  
                                    
        _attributes.put( fname, modelMBeanAttributeInfo);
    }

    /**
     * This adds a constructor model to the Model Builder.
     * @see the ModelMBeanDescriptorBuilder for information on how
     * to build a descriptor that needs to be passed into this method.
     * 
     * @param c
     * @param description
     * @param desc
     */    

    public void addModelMBeanConstructor(Constructor c, String description, Descriptor desc)
    {
        ModelMBeanConstructorInfo modelMBeanConstructorInfo = new ModelMBeanConstructorInfo(description, c, desc);  
        _constructors.put(c, modelMBeanConstructorInfo);
    }

    /**
     * This adds a constructor model to the Model Builder.
     * This method assumes a default Descripor for the constructor.
     *  
     * @param description
     * @param constructorMethod
     * 
     */

    public void addModelMBeanConstructor(String description, Constructor constructorMethod)
    {
        ModelMBeanConstructorInfo modelMBeanConstructorInfo = new ModelMBeanConstructorInfo(description, constructorMethod);
        _constructors.put(constructorMethod,modelMBeanConstructorInfo);    
    }

    /**
     * This builds the main model for the model mbean.
     * @see ModelMBeanDescriptorBuilder on how to build a
     * descriptor for the MBean.
     * 
     * @param desc
     * @param className
     * @param description
     * @return
     * @throws Exception
     */    

    public ModelMBeanInfo buildModelMBeanInfo(Descriptor desc, String className, String description) throws Exception
    {
        ModelMBeanOperationInfo[] ops = new ModelMBeanOperationInfo[_operations.size()];
        ops = convertMapToModelMBeanOperationInfoArray(_operations);

        ModelMBeanAttributeInfo[] atts = new ModelMBeanAttributeInfo[_attributes.size()];
        atts = convertMapToModelMBeanAttributeInfoArray(_attributes);

        ModelMBeanConstructorInfo[] cons = new ModelMBeanConstructorInfo[_constructors.size()];
        cons = convertMapToModelMBeanConstructorInfoArray(_constructors);

        ModelMBeanNotificationInfo[] notifs = new ModelMBeanNotificationInfo[_notifications.size()];
        notifs = convertMapToModelMBeanNotificationInfoArray(_notifications);

        return new ModelMBeanInfoSupport(
            JAVAX_MODELBEAN_INFO, //className, 
            description, 
            atts,
            cons, 
            ops, 
            notifs, 
            desc
        );
    }

    protected MBeanFeatureInfo[] convertMapToMBeanFeatureInfoArray(Map table , String castType)
    {
        MBeanFeatureInfo[] mBeanFeatureInfoArray = null;
        ModelMBeanAttributeInfo[] modelMBeanAttributeInfoArray = null;
        Collection collection = table.values();
        Iterator iterator = collection.iterator();

        int currentArrayIndex = 0;
    
        while( iterator.hasNext())
        {
            if (castType.equals(MODEL_MBEAN_ATTRIBUTE_INFO))
            {
                modelMBeanAttributeInfoArray[currentArrayIndex] = (ModelMBeanAttributeInfo) iterator.next();
            }
            else if (castType.equals(MODEL_MBEAN_OPERATION_INFO))
            {
                mBeanFeatureInfoArray[currentArrayIndex] = (ModelMBeanOperationInfo) iterator.next();
            }
            else if (castType.equals(MODEL_MBEAN_NOTIFICATION_INFO))
            {
                mBeanFeatureInfoArray[currentArrayIndex] = (ModelMBeanOperationInfo) iterator.next();
            }
            else if (castType.equals(MODEL_MBEAN_CONSTRUCTOR_INFO))
            {
                mBeanFeatureInfoArray[currentArrayIndex] = (ModelMBeanConstructorInfo) iterator.next();
            }

            currentArrayIndex++;
        }

        return mBeanFeatureInfoArray;
    }

    protected ModelMBeanAttributeInfo[] convertMapToModelMBeanAttributeInfoArray(Map table)
    {
        ModelMBeanAttributeInfo[] modelMBeanAttributeInfoArray = new ModelMBeanAttributeInfo[table.size()];
        Collection collection = table.values();
        Iterator iterator = collection.iterator();
        
        int currentArrayIndex = 0;
        
        while( iterator.hasNext())
        {
            modelMBeanAttributeInfoArray[currentArrayIndex] = (ModelMBeanAttributeInfo) iterator.next();
            currentArrayIndex++;
        }

        return modelMBeanAttributeInfoArray;
    }

    protected ModelMBeanOperationInfo[] convertMapToModelMBeanOperationInfoArray(Map table)
    {
        ModelMBeanOperationInfo[] modelMBeanOperationInfoArray = new ModelMBeanOperationInfo[table.size()];
        Collection collection = table.values();
        Iterator iterator = collection.iterator();

        int currentArrayIndex = 0;

        while(iterator.hasNext())
        {
            modelMBeanOperationInfoArray[currentArrayIndex] = (ModelMBeanOperationInfo) iterator.next();
            currentArrayIndex++;
        }

        return modelMBeanOperationInfoArray;
    }

    protected ModelMBeanConstructorInfo[] convertMapToModelMBeanConstructorInfoArray(Map table)
    {
        ModelMBeanConstructorInfo[] modelMBeanConstructorInfoArray = new ModelMBeanConstructorInfo[table.size()];
        Collection collection = table.values();
        Iterator iterator = collection.iterator();

        int currentArrayIndex = 0;

        while( iterator.hasNext())
        {
            modelMBeanConstructorInfoArray[currentArrayIndex] = (ModelMBeanConstructorInfo) iterator.next();
            currentArrayIndex++;
        }

        return modelMBeanConstructorInfoArray;
    }

    protected ModelMBeanNotificationInfo[] convertMapToModelMBeanNotificationInfoArray(Map table)
    {
        ModelMBeanNotificationInfo[] modelMBeanNotificationInfoArray = new ModelMBeanNotificationInfo[table.size()];
        Collection collection = table.values();
        Iterator iterator = collection.iterator();

        int currentArrayIndex = 0;

        while( iterator.hasNext())
        {
            modelMBeanNotificationInfoArray[currentArrayIndex] = (ModelMBeanNotificationInfo)iterator.next();
            currentArrayIndex++;
        }

        return modelMBeanNotificationInfoArray;
    }

    private static Logger _Logger = Logger.getLogger(ModelMBeanInfoBuilder.class);    
    protected Map _attributes = new HashMap();
    protected Map _notifications = new HashMap();
    protected Map _constructors = new HashMap();
    protected Map _operations = new HashMap();
}