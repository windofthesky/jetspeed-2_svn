/*
 * Copyright 2004 The Apache Software Foundation.
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
package org.apache.portals.bridges.jsf;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * <p>
 * Helper Map implementation for use with different Attribute Maps.
 * </p>
 * <p>
 * See MyFaces project for servlet implementation.
 * </p>
 * 
 * @author <a href="dlestrat@apache.org">David Le Strat </a>
 *  
 */
public abstract class AbstractAttributeMap implements Map
{
    private Set keySet;

    private Collection values;

    private Set entrySet;

    /**
     * @see java.util.Map#clear()
     */
    public void clear()
    {
        List names = new ArrayList();
        for (Enumeration e = getAttributeNames(); e.hasMoreElements();)
        {
            names.add(e.nextElement());
        }

        for (Iterator it = names.iterator(); it.hasNext();)
        {
            removeAttribute((String) it.next());
        }
    }

    /**
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    public boolean containsKey(Object key)
    {
        return getAttribute(key.toString()) != null;
    }

    /**
     * @see java.util.Map#containsValue(java.lang.Object)
     */
    public boolean containsValue(Object findValue)
    {
        if (findValue == null)
        {
            return false;
        }

        for (Enumeration e = getAttributeNames(); e.hasMoreElements();)
        {
            Object value = getAttribute((String) e.nextElement());
            if (findValue.equals(value))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * @see java.util.Map#entrySet()
     */
    public Set entrySet()
    {
        return (entrySet != null) ? entrySet : (entrySet = new EntrySet());
    }

    /**
     * @see java.util.Map#get(java.lang.Object)
     */
    public Object get(Object key)
    {
        return getAttribute(key.toString());
    }

    /**
     * @see java.util.Map#isEmpty()
     */
    public boolean isEmpty()
    {
        return !getAttributeNames().hasMoreElements();
    }

    /**
     * @see java.util.Map#keySet()
     */
    public Set keySet()
    {
        return (keySet != null) ? keySet : (keySet = new KeySet());
    }

    /**
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    public Object put(Object key, Object value)
    {
        String localKey = key.toString();
        Object retval = getAttribute(localKey);
        setAttribute(localKey, value);
        return retval;
    }

    /**
     * @see java.util.Map#putAll(java.util.Map)
     */
    public void putAll(Map t)
    {
        for (Iterator it = t.entrySet().iterator(); it.hasNext();)
        {
            Entry entry = (Entry) it.next();
            setAttribute(entry.getKey().toString(), entry.getValue());
        }
    }

    /**
     * @see java.util.Map#remove(java.lang.Object)
     */
    public Object remove(Object key)
    {
        String localKey = key.toString();
        Object retval = getAttribute(localKey);
        removeAttribute(localKey);
        return retval;
    }

    /**
     * @see java.util.Map#size()
     */
    public int size()
    {
        int size = 0;
        for (Enumeration e = getAttributeNames(); e.hasMoreElements();)
        {
            size++;
            e.nextElement();
        }
        return size;
    }

    /**
     * @see java.util.Map#values()
     */
    public Collection values()
    {
        return (values != null) ? values : (values = new Values());
    }

    /**
     * <p>
     * Gets an attribute given a key.
     * </p>
     * 
     * @param key The key.
     * @return The attribute.
     */
    abstract protected Object getAttribute(String key);

    /**
     * <p>
     * Sets an attribute given a key/value.
     * </p>
     * 
     * @param key The key.
     * @param value The value.
     */
    abstract protected void setAttribute(String key, Object value);

    /**
     * <p>
     * Removes an attribute.
     * </p>
     * 
     * @param key The key.
     */
    abstract protected void removeAttribute(String key);

    /**
     * <p>
     * Gets the attributes names.
     * </p>
     * 
     * @return An enumeration of attributes names.
     */
    abstract protected Enumeration getAttributeNames();

    private class KeySet extends AbstractSet
    {
        public Iterator iterator()
        {
            return new KeyIterator();
        }

        public boolean isEmpty()
        {
            return AbstractAttributeMap.this.isEmpty();
        }

        public int size()
        {
            return AbstractAttributeMap.this.size();
        }

        public boolean contains(Object o)
        {
            return AbstractAttributeMap.this.containsKey(o);
        }

        public boolean remove(Object o)
        {
            return AbstractAttributeMap.this.remove(o) != null;
        }

        public void clear()
        {
            AbstractAttributeMap.this.clear();
        }
    }

    private class KeyIterator implements Iterator
    {
        protected final Enumeration _e = getAttributeNames();

        protected Object _currentKey;

        public void remove()
        {
            // remove() may cause ConcurrentModificationException.
            // We could throw an exception here, but not throwing an exception
            //   allows one call to remove() to succeed
            if (_currentKey == null)
            {
                throw new NoSuchElementException("You must call next() at least once");
            }
            AbstractAttributeMap.this.remove(_currentKey);
        }

        public boolean hasNext()
        {
            return _e.hasMoreElements();
        }

        public Object next()
        {
            return _currentKey = _e.nextElement();
        }
    }

    private class Values extends KeySet
    {
        public Iterator iterator()
        {
            return new ValuesIterator();
        }

        public boolean contains(Object o)
        {
            return AbstractAttributeMap.this.containsValue(o);
        }

        public boolean remove(Object o)
        {
            if (o == null)
            {
                return false;
            }

            for (Iterator it = iterator(); it.hasNext();)
            {
                if (o.equals(it.next()))
                {
                    it.remove();
                    return true;
                }
            }

            return false;
        }
    }

    private class ValuesIterator extends KeyIterator
    {
        public Object next()
        {
            super.next();
            return AbstractAttributeMap.this.get(_currentKey);
        }
    }

    private class EntrySet extends KeySet
    {
        public Iterator iterator()
        {
            return new EntryIterator();
        }

        public boolean contains(Object o)
        {
            if (!(o instanceof Entry))
            {
                return false;
            }

            Entry entry = (Entry) o;
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (key == null || value == null)
            {
                return false;
            }

            return value.equals(AbstractAttributeMap.this.get(key));
        }

        public boolean remove(Object o)
        {
            if (!(o instanceof Entry))
            {
                return false;
            }

            Entry entry = (Entry) o;
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (key == null || value == null || !value.equals(AbstractAttributeMap.this.get(key)))
            {
                return false;
            }

            return AbstractAttributeMap.this.remove(((Entry) o).getKey()) != null;
        }
    }

    /**
     * Not very efficient since it generates a new instance of
     * <code>Entry</code> for each element and still internaly uses the
     * <code>KeyIterator</code>. It is more efficient to use the
     * <code>KeyIterator</code> directly.
     */
    private class EntryIterator extends KeyIterator
    {
        public Object next()
        {
            super.next();
            // Must create new Entry every time--value of the entry must stay
            // linked to the same attribute name
            return new EntrySetEntry(_currentKey);
        }
    }

    private class EntrySetEntry implements Entry
    {
        private final Object _currentKey;

        public EntrySetEntry(Object currentKey)
        {
            _currentKey = currentKey;
        }

        public Object getKey()
        {
            return _currentKey;
        }

        public Object getValue()
        {
            return AbstractAttributeMap.this.get(_currentKey);
        }

        public Object setValue(Object value)
        {
            return AbstractAttributeMap.this.put(_currentKey, value);
        }
    }
}