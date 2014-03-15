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

import org.apache.ojb.broker.ManageableCollection;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.apache.ojb.broker.util.collections.IRemovalAwareCollection;
import org.apache.ojb.broker.util.collections.RemovalAwareCollection;
import org.apache.ojb.broker.util.collections.RemovalAwareList;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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
     * See issue JS2-590
     */
    
    @SuppressWarnings("unchecked")
    public static <T> Collection<T> createCollection()
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
    public static class SynchronizedRemovalAwareCollection<T> implements Collection<T>, ManageableCollection, IRemovalAwareCollection
    {
        private static final long serialVersionUID = 1L;

        private RemovalAwareCollection collection = new RemovalAwareCollection();

        public synchronized boolean add(T e)
        {
            return collection.add(e);
        }

        public synchronized boolean addAll(Collection<? extends T> c)
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

        public synchronized Iterator<T> iterator()
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

        public synchronized T[] toArray()
        {
            return (T[])collection.toArray();
        }

        public synchronized <A> A[] toArray(A[] a)
        {
            return (A[])collection.toArray(a);
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

    public static <T> Collection<T> createSynchronizedCollection()
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
        return new SynchronizedRemovalAwareCollection<T>();
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> createList()
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
    public static class SynchronizedRemovalAwareList<T> implements List<T>, ManageableCollection, IRemovalAwareCollection
    {
        private static final long serialVersionUID = 1L;

        private RemovalAwareList list = new RemovalAwareList();

        public synchronized void add(int index, T element)
        {
            list.add(index, element);
        }

        public synchronized boolean add(T e)
        {
            return list.add(e);
        }

        public synchronized boolean addAll(Collection<? extends T> c)
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

        public synchronized T get(int index)
        {
            return (T)list.get(index);
        }

        public synchronized int indexOf(Object o)
        {
            return list.indexOf(o);
        }

        public synchronized boolean isEmpty()
        {
            return list.isEmpty();
        }

        public synchronized Iterator<T> iterator()
        {
            return list.iterator();
        }

        public synchronized int lastIndexOf(Object o)
        {
            return list.lastIndexOf(o);
        }

        public synchronized ListIterator<T> listIterator()
        {
            return list.listIterator();
        }

        public synchronized ListIterator<T> listIterator(int index)
        {
            return list.listIterator(index);
        }

        public synchronized T remove(int index)
        {
            return (T)list.remove(index);
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

        public synchronized T set(int index, T element)
        {
            return (T)list.set(index, element);
        }

        public synchronized int size()
        {
            return list.size();
        }

        public synchronized List<T> subList(int fromIndex, int toIndex)
        {
            return list.subList(fromIndex, toIndex);
        }

        public synchronized T[] toArray()
        {
            return (T[])list.toArray();
        }

        public synchronized <A> A[] toArray(A[] a)
        {
            return (A[])list.toArray(a);
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

    public static <T> List<T> createSynchronizedList()
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
        return new SynchronizedRemovalAwareList<T>();
    }
}
