/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.om.page.impl;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.jetspeed.om.page.BaseFragmentElement;
import org.apache.jetspeed.om.page.FragmentProperty;
import org.apache.jetspeed.page.FragmentPropertyList;

/**
 * FragmentPropertyList
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class FragmentPropertyListImpl extends AbstractList implements FragmentPropertyList
{
    private BaseFragmentElementImpl fragment;

    private List properties;
    private List removedProperties;

    public FragmentPropertyListImpl(BaseFragmentElementImpl fragment)
    {
        super();
        this.properties = new ArrayList();
        this.fragment = fragment;
    }

    /* (non-Javadoc)
     * @see java.util.List#add(int,java.lang.Object)
     */
    /* (non-Javadoc)
	 * @see org.apache.jetspeed.om.page.impl.FragmentPropertyList#add(int, java.lang.Object)
	 */
    public synchronized void add(int index, Object element)
    {
        // implement for modifiable AbstractList:
        FragmentPropertyImpl add = (FragmentPropertyImpl)element;
        if ((add.getName() == null) || (add.getValue() == null))
        {
            throw new IllegalArgumentException("Property name and value must be set.");
        }
        // find existing matching property
        FragmentProperty addMatch = getMatchingProperty(add);
        if (addMatch != null)
        {
            // modify existing property
            addMatch.setValue(add.getValue());
        }
        else
        {
            // try to recycle removed properties
            add = recycleProperty(add);
            // add new property
            properties.add(index, add);
        }
    }

    /* (non-Javadoc)
     * @see java.util.List#get(int)
     */
    /* (non-Javadoc)
	 * @see org.apache.jetspeed.om.page.impl.FragmentPropertyList#get(int)
	 */
    public synchronized Object get(int index)
    {
        // implement for modifiable AbstractList
        return properties.get(index);
    }

    /* (non-Javadoc)
     * @see java.util.List#remove(int)
     */
    /* (non-Javadoc)
	 * @see org.apache.jetspeed.om.page.impl.FragmentPropertyList#remove(int)
	 */
    public synchronized Object remove(int index)
    {
        // implement for modifiable AbstractList:
        // save removed element 
        FragmentPropertyImpl removed = (FragmentPropertyImpl)properties.remove(index);
        return removedProperty(removed);
    }

    /* (non-Javadoc)
     * @see java.util.List#set(int,java.lang.Object)
     */
    /* (non-Javadoc)
	 * @see org.apache.jetspeed.om.page.impl.FragmentPropertyList#set(int, java.lang.Object)
	 */
    public synchronized Object set(int index, Object element)
    {
        // implement for modifiable AbstractList:
        FragmentPropertyImpl set = (FragmentPropertyImpl)element;
        if ((set.getName() == null) || (set.getValue() == null))
        {
            throw new IllegalArgumentException("Property name and value must be set.");
        }
        // find existing matching property
        FragmentProperty setMatch = getMatchingProperty(set);
        if (setMatch != null)
        {
            // modify existing property
            setMatch.setValue(set.getValue());
            // remove property if not matching
            if (properties.get(index) != setMatch)
            {
                return remove(index);
            }
            return null;
        }
        else
        {
            // try to recycle removed properties
            set = recycleProperty(set);
            // replace property
            FragmentPropertyImpl replaced = (FragmentPropertyImpl)properties.set(index, set);
            return removedProperty(replaced);
        }
    }

    /* (non-Javadoc)
     * @see java.util.List#size()
     */
    /* (non-Javadoc)
	 * @see org.apache.jetspeed.om.page.impl.FragmentPropertyList#size()
	 */
    public synchronized int size()
    {
        // implement for modifiable AbstractList
        return properties.size();
    }
    
    /* (non-Javadoc)
	 * @see org.apache.jetspeed.om.page.impl.FragmentPropertyList#getFragmentImpl()
	 */
    public BaseFragmentElement getFragmentElement()
    {
        return fragment;
    }
    
    /* (non-Javadoc)
	 * @see org.apache.jetspeed.om.page.impl.FragmentPropertyList#getProperties()
	 */
    public List getProperties()
    {
        return properties;
    }
    
    /* (non-Javadoc)
	 * @see org.apache.jetspeed.om.page.impl.FragmentPropertyList#getRemovedProperties()
	 */
    public List getRemovedProperties()
    {
        return removedProperties;
    }
    
    /* (non-Javadoc)
	 * @see org.apache.jetspeed.om.page.impl.FragmentPropertyList#getMatchingProperty(org.apache.jetspeed.om.page.FragmentProperty)
	 */
    public synchronized FragmentProperty getMatchingProperty(FragmentProperty match)
    {
        Iterator matchIter = properties.iterator();
        while (matchIter.hasNext())
        {
            FragmentPropertyImpl test = (FragmentPropertyImpl)matchIter.next();
            if (test.match(match))
            {
                return test;
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
	 * @see org.apache.jetspeed.om.page.impl.FragmentPropertyList#clearProperties()
	 */
    public synchronized void clearProperties()
    {
        properties.clear();
        if (removedProperties != null)
        {
            removedProperties.clear();
        }
    }

    /**
     * Recycle removed property.
     * 
     * @param original original property
     * @return recycled or original property
     */
    protected FragmentPropertyImpl recycleProperty(FragmentPropertyImpl original)
    {
        if ((removedProperties != null) && !removedProperties.isEmpty())
        {
            FragmentPropertyImpl recycle = (FragmentPropertyImpl)removedProperties.remove(removedProperties.size()-1);
            recycle.setName(original.getName());
            recycle.setScope(original.getScope());
            recycle.setScopeValue(original.getScopeValue());
            recycle.setValue(original.getValue());
            return recycle;
        }
        return original;
    }

    /**
     * Track removed property.
     * 
     * @param removed removed property
     * @return removed property
     */
    protected FragmentPropertyImpl removedProperty(FragmentPropertyImpl removed)
    {
        if ((removed != null) && (removed.getIdentity() != 0))
        {
            if (removedProperties == null)
            {
                removedProperties = new ArrayList();
            }
            removedProperties.add(removed);
        }
        return removed;
    }
}