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
package org.apache.jetspeed.search;

import java.util.Collection;

/**
 * @author <a href="mailto: jford@apache.org">Jeremy Ford</a>
 * @version $Id$
 */
public interface SearchEngine
{
    boolean add(Object o);
    
    boolean add(Collection objects);
    
    boolean remove(Object o);
    
    boolean remove(Collection objects);
    
    boolean update(Object o);
    
    boolean update(Collection objects);
    
    boolean optimize();
    
    SearchResults search(String query);
    
    SearchResults search(String query, int topHitsCount);
}
