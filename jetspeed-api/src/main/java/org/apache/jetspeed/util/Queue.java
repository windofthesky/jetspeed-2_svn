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

package org.apache.jetspeed.util;

/**
 * Queue interface similar to the java.util.Stack interface
 *
 * @author <a href="mailto:raphael@apache.org">Rapha\u00ebl Luta</a>
 * @version $Id$
 */
public interface Queue extends java.util.List
{
   /**
    * Adds a new object into the queue
    */
   public void push(Object obj);

   /**
    * Gets the first object in the queue and remove it from the queue
    */
   public Object pop();

   /**
    * Gets the first object in the queue without removing it from the queue
    */
   public Object peek();
}
