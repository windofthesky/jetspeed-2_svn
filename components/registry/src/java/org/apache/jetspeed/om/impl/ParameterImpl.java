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

package org.apache.jetspeed.om.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.om.common.MutableDescription;
import org.apache.jetspeed.om.common.ParameterComposite;
import org.apache.jetspeed.util.HashCodeBuilder;
import org.apache.pluto.om.common.Description;
import org.apache.pluto.om.common.DescriptionSet;


/**
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 */
public class ParameterImpl implements ParameterComposite, Serializable
{

    private String name;
    private String value;
    private String description;

    protected long parameterId;

    protected long parentId;

    private Collection descriptions;
    private DescriptionSetImpl descCollWrapper = new DescriptionSetImpl(DescriptionImpl.TYPE_PARAMETER);
    
    private static final Log log = LogFactory.getLog(ParameterImpl.class);

    /**
     * @see org.apache.pluto.om.common.Parameter#getName()
     */
    public String getName()
    {
        return name;
    }

    /**
     * @see org.apache.pluto.om.common.Parameter#getValue()
     */
    public String getValue()
    {
        return value;
    }

    /**
     * @see org.apache.pluto.om.common.ParameterCtrl#setName(java.lang.String)
     */
    public void setName(String name)
    {
        this.name = name;

    }

    /**
     * @see org.apache.pluto.om.common.ParameterCtrl#setValue(java.lang.String)
     */
    public void setValue(String value)
    {
        this.value = value;

    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        if (obj != null && obj.getClass().equals(getClass()))
        {
            ParameterImpl p = (ParameterImpl) obj;            
            boolean sameParent = (p.parentId == parentId);
            boolean sameName  = (name != null && p.getName() != null && name.equals(p.getName()));
            return sameParent && sameName;            
        }

        return false;

    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        HashCodeBuilder hash = new HashCodeBuilder(17, 77);
        return hash.append(name).toHashCode();
    }

    /**
     * @see org.apache.pluto.om.common.Parameter#getDescription(java.util.Locale)
     */
    public Description getDescription(Locale arg0)
    {
        if (descriptions != null)
        {
            return new DescriptionSetImpl(descriptions).get(arg0);
        }
        return null;

    }

    /**
     * @see org.apache.pluto.om.common.ParameterCtrl#setDescriptionSet(org.apache.pluto.om.common.DescriptionSet)
     */
    public void setDescriptionSet(DescriptionSet arg0)
    {
        this.descriptions = ((DescriptionSetImpl) arg0).getInnerCollection();
    }

    /**
     * @see org.apache.jetspeed.om.common.ParameterComposite#addDescription(java.util.Locale, java.lang.String)
     */
    public void addDescription(Locale locale, String desc)
    {
        if (descriptions == null)
        {
			descriptions = new ArrayList();
        }
        descCollWrapper.setInnerCollection(descriptions);
        try
        {
            MutableDescription descObj = new ParameterDescriptionImpl();
                
			descObj.setLocale(locale);
			descObj.setDescription(desc);
			descCollWrapper.addDescription(descObj);
        }
        catch (Exception e)
        {
            String msg = "Unable to instantiate Description implementor, " + e.toString();
            log.error(msg, e);
            throw new IllegalStateException(msg);
        }

    }

    public void addDescription(Description desc)
    {
        if (descriptions == null)
        {
            descriptions = new ArrayList();
        }

        descCollWrapper.setInnerCollection(descriptions);
        descCollWrapper.addDescription(desc);
    }

    /**
     * Remove when Castor is mapped correctly
     * 
     * @deprecated
     * @param desc
     */
    public void setDescription(String desc)
    {
        System.out.println("Setting description..." + desc);
        addDescription(Locale.getDefault(), desc);
        System.out.println("Description Set " + desc);
    }

    /**
     *  Remove when Castor is mapped correctly
     * @deprecated
     * @return
     */
    public String getDescription()
    {

        Description desc = getDescription(Locale.getDefault());

        if (desc != null)
        {
            return desc.getDescription();
        }

        return null;
    }

}
