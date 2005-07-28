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
package org.apache.portals.bridges.frameworks.spring;

import org.apache.portals.bridges.frameworks.model.ModelBean;


/**
 * BeanModelImpl
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class ModelBeanImpl implements ModelBean
{

    private String beanName;
    private int beanType;
    private boolean requiresExternalSupport = false;
    private String lookupKey;

    public ModelBeanImpl(String beanName, int beanType)
    {
        this.beanName = beanName;
        this.beanType = beanType;
    }
    
    public ModelBeanImpl(String beanName, int beanType, String lookupKey, boolean requiresExternalSupport)
    {
        this.beanName = beanName;
        this.beanType = beanType;
        this.lookupKey = lookupKey;
        this.requiresExternalSupport = requiresExternalSupport;
    }
    
    /* (non-Javadoc)
     * @see org.apache.portals.bridges.velocity.model.ModelBean#getBeanName()
     */
    public String getBeanName()
    {
        return beanName;
    }

    /* (non-Javadoc)
     * @see org.apache.portals.bridges.velocity.model.ModelBean#getBeanType()
     */
    public int getBeanType()
    {
        return beanType;
    }

    /**
     * @return Returns the lookupKey.
     */
    public String getLookupKey()
    {
        return lookupKey;
    }
    /**
     * @param lookupKey The lookupKey to set.
     */
    public void setLookupKey(String lookupKey)
    {
        this.lookupKey = lookupKey;
    }
    /**
     * @return Returns the requiresExternalSupport.
     */
    public boolean isRequiresExternalSupport()
    {
        return requiresExternalSupport;
    }
    /**
     * @param requiresExternalSupport The requiresExternalSupport to set.
     */
    public void setRequiresExternalSupport(boolean requiresExternalSupport)
    {
        this.requiresExternalSupport = requiresExternalSupport;
    }
    
    public boolean isRequiresLookup()
    {
        return (lookupKey!= null);
    }
    
}
