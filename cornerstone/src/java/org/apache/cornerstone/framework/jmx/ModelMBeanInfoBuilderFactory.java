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

import org.apache.cornerstone.framework.factory.BaseFactory;
import org.apache.log4j.Logger;

/**
 * This creates an instance of the ModelMBeanInfoBuilder
 * 
 */

public class ModelMBeanInfoBuilderFactory extends BaseFactory
{
    public static final String REVISION = "$Revision$";

    /**
    * Returns a singleton instance of ModelMBeanInfoBuilderFactory
    * @return ModelMBeanInfoBuilderFactory singleton
    */
    public static ModelMBeanInfoBuilderFactory getSingleton()
    {
        return _Singleton;
    }

    /**
    * Creates an instance of the ModelMBeanInfoBuilder
    * @return ModelMBeanInfoBuilder Singleton instance
    */
    public Object createInstance()
    {
        IModelMBeanInfoBuilder modelMBeanInfoBuilder = new ModelMBeanInfoBuilder(); 
        
        //TODO: remove this...(IModelMBeanInfoBuilder)SingletonManager.getSingleton("com.cisco.salesit.framework.sysmgmt.ModelMBeanInfoBuilder");
    
        if ( modelMBeanInfoBuilder != null )
        {
            _Logger.info("created modelMBeanInfoBuilder");
        }else
        {
            _Logger.error("couldn't create modelMBeanInfoBuilder");
        }
            
        return (IModelMBeanInfoBuilder)modelMBeanInfoBuilder;
    }

    private static Logger _Logger = Logger.getLogger(ModelMBeanInfoBuilderFactory.class);
    private static ModelMBeanInfoBuilderFactory _Singleton = new ModelMBeanInfoBuilderFactory();
}