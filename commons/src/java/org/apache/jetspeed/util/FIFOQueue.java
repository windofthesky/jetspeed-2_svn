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

package org.apache.jetspeed.util;

/**
 * Simple FIFO implementation of Queue interface extending Vector
 * as storage backend.
 *
 * @author <a href="mailto:raphael@apache.org">Rapha\u00ebl Luta</a>
 * @version $Id$
 */
public class FIFOQueue extends java.util.Vector implements Queue
{
   /**
    * Adds a new object into the queue
    */
   public synchronized void push(Object obj)
   {
       this.add(obj);
   }

   /**
    * Gets the first object in the queue and remove it from the queue
    */
   public synchronized Object pop()
   {

       if (this.size() == 0)
       {
           return null;
       }

       return this.remove(0);
   }

   /**
    * Gets the first object in the queue without removing it from the queue
    */
   public synchronized Object peek()
   {

       if (this.size() == 0)
       {
           return null;
       }

       return this.get(0);
   }
}
