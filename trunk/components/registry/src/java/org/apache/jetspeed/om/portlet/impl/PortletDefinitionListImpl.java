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



import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.portlet.PortletDefinitionList;

/**
 * 
 * PortletDefinitionListImpl
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class PortletDefinitionListImpl implements PortletDefinitionList, Serializable
{

    protected Collection innerCollection;

    /**
     * 
     */
    public PortletDefinitionListImpl()
    {
        super();
        innerCollection = new ArrayList();
    }

    public PortletDefinitionListImpl(Collection collection)
    {
        super();
        innerCollection = collection;
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinitionList#iterator()
     */
    public Iterator iterator()
    {
        return innerCollection.iterator();
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinitionList#get(org.apache.pluto.om.common.ObjectID)
     */
    public PortletDefinition get(ObjectID id)
    {
        Iterator itr = innerCollection.iterator();
        while (itr.hasNext())
        {
            PortletDefinition pd = (PortletDefinition) itr.next();
            if (pd.getId().equals(id))
            {
                return pd;
            }
        }

        return null;
    }

    /**
     * Retrieves a <code>PortletDefinition</code> from this 
     * collection by the PortletDefinitions proper name
     * @param name Proper name of PortletDefinition to locate.
     * @return PortletDefinition matching <code>name</code> or <code>null</code>
     * if no PortletDefinition within this PortletApplication has that name.
     */
    public PortletDefinition get(String name)
    {
        Iterator itr = innerCollection.iterator();
        while (itr.hasNext())
        {
            PortletDefinition pd = (PortletDefinition) itr.next();
            if (pd.getName().equals(name))
            {
                return pd;
            }
        }

        return null;
    }

    /**
     * @see java.util.Collection#add(java.lang.Object)
     */
    public boolean add(Object o)
    {
        PortletDefinition pd = (PortletDefinition) o;        
        return innerCollection.add(pd);
    }

    /**
     * @see java.util.Collection#remove(java.lang.Object)
     */
    public boolean remove(Object o)
    {
        PortletDefinition pd = (PortletDefinition) o;        
        return innerCollection.remove(pd);
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
