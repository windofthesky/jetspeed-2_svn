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

import org.apache.cornerstone.framework.api.sysman.IModelMBeanDescriptorBuilder;
import org.apache.cornerstone.framework.factory.BaseFactory;
import org.apache.cornerstone.framework.init.Cornerstone;
import org.apache.log4j.Logger;

/**
 * Factory for creating the ModelMBeanDescriptorBuilder
 *
 */

public class ModelMBeanDescriptorBuilderFactory extends BaseFactory
{
    public static final String REVISION = "$Revision$";

    /**
    * Returns a singleton instance of ModelMBeanInfoBuilderFactory
    * @return ModelMBeanInfoBuilderFactory singleton
    */
    public static ModelMBeanDescriptorBuilderFactory getSingleton()
    {
        return _singleton;
    }
    
    /**
    * Creates an instance of the ModelMBeanInfoBuilder
    * @return ModelMBeanInfoBuilder Singleton instance
    */
    public Object createInstance()
    {
        ModelMBeanDescriptorBuilder modelMBeanDescriptorBuilder = (ModelMBeanDescriptorBuilder) Cornerstone.getSingletonManager().getSingleton("com.cisco.salesit.framework.sysmgmt.ModelMBeanDescriptorBuilder");

        if ( modelMBeanDescriptorBuilder != null )
        {
            _Logger.info("created modelMBeanDescriptorBuilder");
        }
        else
        {
            _Logger.error("couldn't create modelMBeanDescriptorBuilder");
        }
        
        return (IModelMBeanDescriptorBuilder)modelMBeanDescriptorBuilder;
    }

    private static Logger _Logger = Logger.getLogger(ModelMBeanDescriptorBuilderFactory.class);
    private static ModelMBeanDescriptorBuilderFactory _singleton = new ModelMBeanDescriptorBuilderFactory();
}