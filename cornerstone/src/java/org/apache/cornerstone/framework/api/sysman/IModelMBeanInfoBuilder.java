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