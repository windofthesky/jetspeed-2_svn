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

import org.apache.jetspeed.om.page.BaseFragmentElement;

import java.util.AbstractList;

/**
 * FragmentList
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
class FragmentList extends AbstractList<BaseFragmentElement>
{
    private FragmentImpl fragment;

    FragmentList(FragmentImpl fragment)
    {
        super();
        this.fragment = fragment;
    }

    /* (non-Javadoc)
     * @see java.util.List#add(int,java.lang.Object)
     */
    public void add(int index, BaseFragmentElement element)
    {
        // implement for modifiable AbstractList:
        // add and maintain base fragments implementation reference
        fragment.accessFragments().add(index, (AbstractBaseFragmentElement)element);
        if (fragment.getBaseFragmentsElement() != null)
        {
            ((AbstractBaseFragmentElement)element).setBaseFragmentsElement(fragment.getBaseFragmentsElement());
        }
    }

    /* (non-Javadoc)
     * @see java.util.List#get(int)
     */
    public BaseFragmentElement get(int index)
    {
        // implement for modifiable AbstractList
        return fragment.accessFragments().get(index);
    }

    /* (non-Javadoc)
     * @see java.util.List#remove(int)
     */
    public BaseFragmentElement remove(int index)
    {
        // implement for modifiable AbstractList
        return fragment.accessFragments().remove(index);
    }

    /* (non-Javadoc)
     * @see java.util.List#set(int,java.lang.Object)
     */
    public BaseFragmentElement set(int index, BaseFragmentElement element)
    {
        // implement for modifiable AbstractList:
        // set and maintain base fragments implementation reference
        AbstractBaseFragmentElement o = fragment.accessFragments().set(index, (AbstractBaseFragmentElement)element);
        if (fragment.getBaseFragmentsElement() != null)
        {
            ((AbstractBaseFragmentElement)element).setBaseFragmentsElement(fragment.getBaseFragmentsElement());
        }
        return o;
    }

    /* (non-Javadoc)
     * @see java.util.List#size()
     */
    public int size()
    {
        // implement for modifiable AbstractList
        return fragment.accessFragments().size();
    }
}
