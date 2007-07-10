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
package org.apache.jetspeed.aggregator;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Maintains a context attributes for the current Thread
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public final class CurrentWorkerContext
{
    private static ThreadLocal currentWorkerContext = 
        new ThreadLocal() {
            protected synchronized Object initialValue() {
                return new Hashtable();
            }
        };
    
    private static ThreadLocal currentWorkerContextUsed =
        new ThreadLocal() {
            protected synchronized Object initialValue() {
                return new boolean [] { false };
            }
        };
    
    private CurrentWorkerContext()
    {
    }

    /**
     * Returns an Enumeration containing the names of the attributes available to this Thread. 
     * This method returns an empty Enumeration  if the thread has no attributes available to it.
     */
    public static Enumeration getAttributeNames()
    {
        return ((Hashtable) currentWorkerContext.get()).keys();
    }

    /** 
     * @return an attribute in the current Thread
     * @param attrName Locale for this Thread 
     */
    public static Object getAttribute(String name)
    {
        return ((Hashtable) currentWorkerContext.get()).get(name);
    }

    /**
     * Stores an attribute in this Thread.
     * <br>
     * @param name - a String specifying the name of the attribute
     * @param o - the Object to be stored
     */
    public static void setAttribute(String name, Object o)
    {
        if (o != null) {
            ((Hashtable) currentWorkerContext.get()).put(name, o);
        } else {
            removeAttribute(name);
        }
    }

    /**
     * Removes an attribute from this Thread.
     * <br>
     * @param name - a String specifying the name of the attribute
     */
    public static void removeAttribute(String name)
    {
        ((Hashtable) currentWorkerContext.get()).remove(name);
    }

    /**
     * Removes all attributes from this Thread.
     */
    public static void removeAllAttributes()
    {
        ((Hashtable) currentWorkerContext.get()).clear();
    }
    
    public static void setCurrentWorkerContextUsed(boolean used)
    {
        ((boolean []) currentWorkerContextUsed.get())[0] = used;
    }

    public static boolean getCurrentWorkerContextUsed()
    {
        return ((boolean []) currentWorkerContextUsed.get())[0];
    }
}
