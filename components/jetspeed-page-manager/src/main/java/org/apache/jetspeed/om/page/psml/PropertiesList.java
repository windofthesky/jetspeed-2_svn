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
package org.apache.jetspeed.om.page.psml;

import org.apache.jetspeed.om.page.FragmentProperty;

import java.util.AbstractList;
import java.util.List;

/**
 * PropertiesList
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class PropertiesList extends AbstractList<FragmentProperty>
{
    private List<FragmentProperty> filteredProperties;
    private List<FragmentProperty> properties;
   
    PropertiesList(List<FragmentProperty> filteredProperties, List<FragmentProperty> properties)
    {
        this.filteredProperties = filteredProperties;
        this.properties = properties;
    }
    
    /* (non-Javadoc)
     * @see java.util.List#add(int,java.lang.Object)
     */
    public void add(int index, FragmentProperty element)
    {
        // implement for modifiable AbstractList
        filteredProperties.add(element);
        properties.add(element);
    }

    /* (non-Javadoc)
     * @see java.util.List#get(int)
     */
    public FragmentProperty get(int index)
    {
        // implement for modifiable AbstractList
        return filteredProperties.get(index);
    }

    /* (non-Javadoc)
     * @see java.util.List#remove(int)
     */
    public FragmentProperty remove(int index)
    {
        // implement for modifiable AbstractList
        FragmentProperty removedFragmentProperty = filteredProperties.remove(index);
        if (removedFragmentProperty != null)
        {
            properties.remove(removedFragmentProperty);
        }
        return removedFragmentProperty;
    }

    /* (non-Javadoc)
     * @see java.util.List#set(int,java.lang.Object)
     */
    public FragmentProperty set(int index, FragmentProperty element)
    {
        // implement for modifiable AbstractList
        FragmentProperty removedFragmentProperty = filteredProperties.set(index, element);
        properties.add(element);
        if (removedFragmentProperty != null)
        {
            properties.remove(removedFragmentProperty);
        }
        return removedFragmentProperty;
    }

    /* (non-Javadoc)
     * @see java.util.List#size()
     */
    public int size()
    {
        // implement for modifiable AbstractList
        return filteredProperties.size();
    }
}

