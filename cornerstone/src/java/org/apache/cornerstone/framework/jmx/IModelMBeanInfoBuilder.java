/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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

package org.apache.cornerstone.framework.jmx;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import javax.management.Descriptor;
import javax.management.modelmbean.ModelMBeanInfo;

/**
 * The main interface for adding model mbean information on
 * Methods, attributes, notifications, constructors.
 * The models are added as needed basis, and then the MBEanInof
 * final model is obtained from the ModelMBeanInfo buildModelMBeanInfo
 * method.  Note the usage of the add methods is based on "add-as-needed" basis. 
 *
 */

public interface IModelMBeanInfoBuilder
{
    /**
     * This method adds a mbean model to the model builder
     * this method assumes a default descripotor for the 
     * method.
     * 
     * @param name
     * @param description
     * @param method
     */
    public void addModelMBeanMethod(
        String name, 
        String description, 
        Method method
    );

    /**
     * 
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
    );

    /**
     * This is method allows the addition of a Notificaiton model to the Builder
     * 
     * @param type
     * @param className
     * @param description
     * @param desc
     */                                        

    public void addModelMBeanNotification(
        String[] type, 
        String className, 
        String description, 
        Descriptor desc
    );

    /**
     * This is method allows the addition of a Notificaiton model to the Builder
     * It assumes a default descriptor.
     * 
     * @param types
     * @param name
     * @param description
     */

    public void addModelMBeanNotification(
        String[] types, 
        String name, 
        String description
    );

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
    );

    /**
     * This adds a constructor model to the Model Builder.
     * @see the ModelMBeanDescriptorBuilder for information on how
     * to build a descriptor that needs to be passed into this method.
     * 
     * @param c
     * @param description
     * @param desc
     */                                    

    public void addModelMBeanConstructor(
        Constructor c, 
        String description, 
        Descriptor desc
    );

    /**
     * This adds a constructor model to the Model Builder.
     * This method assumes a default Descripor for the constructor.
     *  
     * @param description
     * @param constructorMethod
     * 
     */

    public void addModelMBeanConstructor(
        String description, 
        Constructor constructorMethod
    );

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

    public ModelMBeanInfo buildModelMBeanInfo(
        Descriptor desc, 
        String className, 
        String description
    ) throws Exception;    
}