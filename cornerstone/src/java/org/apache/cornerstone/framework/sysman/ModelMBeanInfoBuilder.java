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

package org.apache.cornerstone.framework.sysman;

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

import org.apache.cornerstone.framework.api.sysman.IModelMBeanInfoBuilder;
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