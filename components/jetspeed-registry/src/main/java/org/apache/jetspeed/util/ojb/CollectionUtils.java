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
package org.apache.jetspeed.util.ojb;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.ojb.broker.ManageableCollection;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.apache.ojb.broker.util.collections.IRemovalAwareCollection;
import org.apache.ojb.broker.util.collections.RemovalAwareCollection;
import org.apache.ojb.broker.util.collections.RemovalAwareList;

/**
 * CollectionUtils
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class CollectionUtils
{
    /**
     * OJB 1.0.3 requires collections to be removal aware.
     * Thus we can't seem to get away with just creating ArrayLists
     * This issue on occurs when persisting newly create object collections
     * When persisting objects retrieved with OJB, this issue does not occur
     * 
     * @see JS2-590
     * @return
     */
    
    @SuppressWarnings("unchecked")
    public static final Collection createCollection()
    {
        // highly concurrent applications will require using
        // createSynchronizedCollection() here instead of this OJB
        // native type which is not synchronized.
        return new RemovalAwareCollection();
    }

    /**
     * Synchronized OJB removal aware collection.
     */
    @SuppressWarnings("unchecked")
    public static class SynchronizedRemovalAwareCollection implements Collection, ManageableCollection, IRemovalAwareCollection
    {
        private static final long serialVersionUID = 1L;

        private RemovalAwareCollection collection = new RemovalAwareCollection();

        public synchronized boolean add(Object e)
        {
            return collection.add(e);
        }

        public synchronized boolean addAll(Collection c)
        {
            return collection.addAll(c);
        }

        public synchronized void clear()
        {
            collection.clear();
        }

        public synchronized boolean contains(Object o)
        {
            return collection.contains(o);
        }

        public synchronized boolean containsAll(Collection c)
        {
            return collection.containsAll(c);
        }

        public synchronized boolean isEmpty()
        {
            return collection.isEmpty();
        }

        public synchronized Iterator iterator()
        {
            return collection.iterator();
        }

        public synchronized boolean remove(Object o)
        {
            return collection.remove(o);
        }

        public synchronized boolean removeAll(Collection c)
        {
            return collection.removeAll(c);
        }

        public synchronized boolean retainAll(Collection c)
        {
            return collection.retainAll(c);
        }

        public synchronized int size()
        {
            return collection.size();
        }

        public synchronized Object[] toArray()
        {
            return collection.toArray();
        }

        public synchronized Object[] toArray(Object[] a)
        {
            return collection.toArray(a);
        }

        public synchronized void afterStore(PersistenceBroker broker) throws PersistenceBrokerException
        {
            collection.afterStore(broker);
        }

        public synchronized void ojbAdd(Object anObject)
        {
            collection.ojbAdd(anObject);
        }

        public synchronized void ojbAddAll(ManageableCollection otherCollection)
        {
            collection.ojbAddAll(otherCollection);
        }

        public synchronized Iterator ojbIterator()
        {
            return collection.ojbIterator();
        }
    }

    @SuppressWarnings("unchecked")
    public static final Collection createSynchronizedCollection()
    {
        // if OJB collections are to be synchronized, collection-class
        // attributes for collection-descriptor need to be set in the
        // OJB mappings to ensure that collections are synchronized
        // when read from the database, (unsynchronized removal aware
        // collections are the default):
        //
        // <collection-descriptor ... collection-class="org.apache.jetspeed.util.ojb.CollectionUtils$SynchronizedRemovalAwareCollection">
        //
        // here, return synchronized manageable/removal aware
        // collection; note that simply wrapping a RemovalAwareCollection
        // using Collections.synchronizedCollection() will not work since
        // OJB collections that are removal aware must implement the
        // ManageableCollection, IRemovalAwareCollection interfaces.
        return new SynchronizedRemovalAwareCollection();
    }
    
    @SuppressWarnings("unchecked")
    public static final List createList()
    {
        // highly concurrent applications will require using
        // createSynchronizedList() here instead of this OJB
        // native type which is not synchronized.
        return new RemovalAwareList();
    }

    /**
     * Synchronized OJB removal aware list.
     */
    @SuppressWarnings("unchecked")
    public static class SynchronizedRemovalAwareList implements List, ManageableCollection, IRemovalAwareCollection
    {
        private static final long serialVersionUID = 1L;

        private RemovalAwareList list = new RemovalAwareList();

        public synchronized void add(int index, Object element)
        {
            list.add(index, element);
        }

        public synchronized boolean add(Object e)
        {
            return list.add(e);
        }

        public synchronized boolean addAll(Collection c)
        {
            return list.addAll(c);
        }

        public synchronized boolean addAll(int index, Collection c)
        {
            return list.addAll(index, c);
        }

        public synchronized void clear()
        {
            list.clear();
        }

        public synchronized boolean contains(Object o)
        {
            return list.contains(o);
        }

        public synchronized boolean containsAll(Collection c)
        {
            return list.containsAll(c);
        }

        public synchronized Object get(int index)
        {
            return list.get(index);
        }

        public synchronized int indexOf(Object o)
        {
            return list.indexOf(o);
        }

        public synchronized boolean isEmpty()
        {
            return list.isEmpty();
        }

        public synchronized Iterator iterator()
        {
            return list.iterator();
        }

        public synchronized int lastIndexOf(Object o)
        {
            return list.lastIndexOf(o);
        }

        public synchronized ListIterator listIterator()
        {
            return list.listIterator();
        }

        public synchronized ListIterator listIterator(int index)
        {
            return list.listIterator(index);
        }

        public synchronized Object remove(int index)
        {
            return list.remove(index);
        }

        public synchronized boolean remove(Object o)
        {
            return list.remove(o);
        }

        public synchronized boolean removeAll(Collection c)
        {
            return list.removeAll(c);
        }

        public synchronized boolean retainAll(Collection c)
        {
            return list.retainAll(c);
        }

        public synchronized Object set(int index, Object element)
        {
            return list.set(index, element);
        }

        public synchronized int size()
        {
            return list.size();
        }

        public synchronized List subList(int fromIndex, int toIndex)
        {
            return list.subList(fromIndex, toIndex);
        }

        public synchronized Object[] toArray()
        {
            return list.toArray();
        }

        public synchronized Object[] toArray(Object[] a)
        {
            return list.toArray(a);
        }

        public synchronized void afterStore(PersistenceBroker broker) throws PersistenceBrokerException
        {
            list.afterStore(broker);
        }

        public synchronized void ojbAdd(Object anObject)
        {
            list.ojbAdd(anObject);
        }

        public synchronized void ojbAddAll(ManageableCollection otherCollection)
        {
            list.ojbAddAll(otherCollection);
        }

        public synchronized Iterator ojbIterator()
        {
            return list.ojbIterator();
        }            
    }

    @SuppressWarnings("unchecked")
    public static final List createSynchronizedList()
    {
        // if OJB lists are to be synchronized, collection-class
        // attributes for collection-descriptor need to be set in the
        // OJB mappings to ensure that lists are synchronized when
        // read from the database, (unsynchronized removal aware
        // lists are the default):
        //
        // <collection-descriptor ... collection-class="org.apache.jetspeed.util.ojb.CollectionUtils$SynchronizedRemovalAwareList">
        //
        // here, return synchronized manageable/removal aware list;
        // note that simply wrapping a RemovalAwareList using
        // Collections.synchronizedList() will not work since
        // OJB lists that are removal aware must implement the
        // ManageableCollection, IRemovalAwareCollection interfaces.
        return new SynchronizedRemovalAwareList();
    }
}
