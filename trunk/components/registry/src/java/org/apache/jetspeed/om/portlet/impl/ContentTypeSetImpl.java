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
package org.apache.jetspeed.om.portlet.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.portlet.PortletMode;

import org.apache.jetspeed.om.common.portlet.ContentTypeSetComposite;
import org.apache.pluto.om.portlet.ContentType;

/**
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a> 
 */
public class ContentTypeSetImpl implements ContentTypeSetComposite, Serializable
{

    protected Collection innerCollection;

    public ContentTypeSetImpl()
    {
        innerCollection = new ArrayList();
    }

    public ContentTypeSetImpl(Collection collection)
    {
        innerCollection = collection;
    }

    public boolean supportsPortletMode(PortletMode mode)
    {
        // Always support "VIEW".  Some portlet vendors do not indicate view
        // in the deployment descriptor.
        if(mode.equals(PortletMode.VIEW))
        {
            return true;
        }
        
        Iterator itr = innerCollection.iterator();
        while (itr.hasNext())
        {
            ContentType p = (ContentType) itr.next();
            if (p.supportsPortletMode(mode))
            {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * @see org.apache.pluto.om.portlet.ContentTypeSet#get(java.lang.String)
     */
    public ContentType get(String contentType)
    {
        Iterator itr = innerCollection.iterator();
        while (itr.hasNext())
        {
            ContentType p = (ContentType) itr.next();
            if (p.getContentType().equals(contentType))
            {
                return p;
            }
        }

        return null;
    }

    /**
     * @see java.util.Collection#add(java.lang.Object)
     */
    public boolean add(Object o)
    {
        ContentType cType = (ContentType) o;
        
        return innerCollection.add(cType);
    }

    /**
     * @see java.util.Collection#remove(java.lang.Object)
     */
    public boolean remove(Object o)
    {
        ContentType cType = (ContentType) o;
        
        return innerCollection.remove(cType);
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.ContentTypeSetComposite#addContentType(org.apache.pluto.om.portlet.ContentType)
     */
    public void addContentType(ContentType contentType)
    {
        add(contentType);
    }

    /**
     * @see org.apache.pluto.om.portlet.ContentTypeSet#iterator()
     */
    public Iterator iterator()
    {        
        return innerCollection.iterator();
    }

    /**
     * @return
     */
    public Collection getInnerCollection()
    {
        return innerCollection;
    }

    /**
     * @param collection
     */
    public void setInnerCollection(Collection collection)
    {
        innerCollection = collection;
    }

}
