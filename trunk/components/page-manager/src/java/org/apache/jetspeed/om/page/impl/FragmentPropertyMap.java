/*
 * Copyright 2005 The Apache Software Foundation.
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
package org.apache.jetspeed.om.page.impl;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.jetspeed.om.page.Fragment;

/**
 * FragmentPropertyMap
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
class FragmentPropertyMap extends AbstractMap
{
    private FragmentImpl fragment;
    private FragmentPropertiesEntrySet entrySet;

    FragmentPropertyMap(FragmentImpl fragment)
    {
        super();
        this.fragment = fragment;
        // populate fragment properties using property members
        entrySet = new FragmentPropertiesEntrySet();
        Iterator keyIter = fragment.getPropertyMemberKeys().iterator();
        while (keyIter.hasNext())
        {
            String key = (String)keyIter.next();
            entrySet.add(new FragmentPropertiesEntry(key, fragment.getPropertyMember(key)));
        }
    }

    /* (non-Javadoc)
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    public Object put(Object key, Object value)
    {
        // implement for modifiable AbstractMap:
        // set map entry value or add new map entry
        // using iterator to find key entry
        FragmentPropertiesEntry entry = new FragmentPropertiesEntry(key, value);
        Iterator entryIter = entrySet.iterator();
        while (entryIter.hasNext())
        {
            FragmentPropertiesEntry testEntry = (FragmentPropertiesEntry) entryIter.next();
            if (testEntry.equals(entry))
            {
                Object oldValue = testEntry.getValue();
                testEntry.setValue(entry.getValue());
                return oldValue;
            }
        }
        entrySet.add(entry);
        return null;
    }

    /* (non-Javadoc)
     * @see java.util.Map#entrySet()
     */
    public Set entrySet()
    {
        // implement for modifiable AbstractMap
        return entrySet;
    }

    private class FragmentPropertiesEntrySet extends AbstractSet
    {
        private Collection entries = new ArrayList(5);

        /* (non-Javadoc)
         * @see java.util.Set#add(java.lang.Object)
         */
        public boolean add(Object o)
        {
            // implement for modifiable AbstractSet:
            FragmentPropertiesEntry entry = (FragmentPropertiesEntry)o;
            if (!entries.contains(entry))
            {
                // set fragment explicit property member
                fragment.setPropertyMember(entry.getKey().toString(), entry.getValue().toString());
                // add entry to set
                entries.add(o);
                return true;
            }
            return false;
        }

        /* (non-Javadoc)
         * @see java.util.Set#iterator()
         */
        public Iterator iterator()
        {
            // implement for modifiable AbstractSet:
            return new Iterator()
                {
                    private Iterator iter = entries.iterator();
                    private FragmentPropertiesEntry last;
                    
                    /* (non-Javadoc)
                     * @see java.util.Iterator#hasNext()
                     */
                    public boolean hasNext()
                    {
                        // implement for modifiable AbstractSet:
                        return iter.hasNext();
                    }

                    /* (non-Javadoc)
                     * @see java.util.Iterator#next()
                     */
                    public Object next()
                    {
                        // implement for modifiable AbstractSet:
                        last = (FragmentPropertiesEntry)iter.next();
                        return last;
                    }

                    /* (non-Javadoc)
                     * @see java.util.Iterator#remove()
                     */
                    public void remove()
                    {
                        // implement for modifiable AbstractSet:
                        // clear fragment explicit property associated with entry
                        if (last == null)
                        {
                            throw new IllegalStateException("No preceding call to next() or remove() already invoked");
                        }
                        FragmentPropertyMap.this.fragment.clearPropertyMember(last.getKey().toString());
                        last = null;
                        // remove entry using iterator
                        iter.remove();
                    }
                };
        }

        /* (non-Javadoc)
         * @see java.util.Set#size()
         */
        public int size()
        {
            // implement for modifiable AbstractSet:
            return entries.size();
        }
    }

    private class FragmentPropertiesEntry implements Map.Entry
    {
        private Object key;
        private Object value;

        public FragmentPropertiesEntry(Object key, Object value)
        {
            this.key = key;
            this.value = value;
        }

        /* (non-Javadoc)
         * @see java.util.Map.Entry#getKey()
         */
        public Object getKey()
        {
            return key;
        }

        /* (non-Javadoc)
         * @see java.util.Map.Entry#getValue()
         */
        public Object getValue()
        {
            return value;
        }
    
        /* (non-Javadoc)
         * @see java.util.Map.Entry#setValue(java.lang.Object)
         */
        public Object setValue(Object newValue)
        {
            // set fragment explicit property associated with entry
            FragmentPropertyMap.this.fragment.setPropertyMember(key.toString(), newValue.toString());
            // set entry value
            Object oldValue = value;
            value = newValue;
            return oldValue;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        public boolean equals(Object o)
        {
            if (o instanceof FragmentPropertiesEntry)
            {
                if (key != null)
                {
                    return key.equals(((FragmentPropertiesEntry)o).getKey());
                }
                return (((FragmentPropertiesEntry)o).getKey() == null);
            }
            return false;
        }
        
        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        public int hashCode()
        {
            if (key != null)
            {
                return key.hashCode();
            }
            return 0;
        }
    }
}
