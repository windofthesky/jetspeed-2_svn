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
    /**
     * Adds search index document for the argument object.
     * An object handler for the object type is responsible for parsing.
     * <BR/>
     * <P>
     * <EM>Note: Normally, it is more efficient to invoke {@link #add(Collection)}.</EM>
     * </P>
     * @param o
     * @return
     * @see {@link org.apache.jetspeed.search.ObjectHandler}
     * @see {@link org.apache.jetspeed.search.ParsedObject}
     */
    boolean add(Object o);
    
    /**
     * Adds search index documents for the argument object collection.
     * Each object handler for the object type of each item is responsible for parsing.
     * @param objects
     * @return
     */
    boolean add(Collection objects);
    
    /**
     * Removes search index document for the argument object.
     * An object handler for the object type is responsible for parsing.
     * <BR/>
     * <P>
     * <EM>Note: Normally, it is more efficient to invoke {@link #remove(Collection)}.</EM>
     * </P>
     * @param o
     * @return
     */
    boolean remove(Object o);
    
    /**
     * Removes search index documents for the argument object collection.
     * Each object handler for the object type of each item is responsible for parsing.
     * @param objects
     * @return
     */
    boolean remove(Collection objects);
    
    /**
     * Updates the search index document for the argument object.
     * An object handler for the object type is responsible for parsing.
     * <BR/>
     * <P>
     * <EM>Note: Normally, it is more efficient to invoke {@link #update(Collection)}.</EM>
     * </P>
     * @param o
     * @return
     */
    boolean update(Object o);
    
    /**
     * Updates the search index documents for the argument object collection.
     * Each object handler for the object type of each item is responsible for parsing.
     * @param objects
     * @return
     */
    boolean update(Collection objects);
    
    /**
     * Requests optimization
     * @return
     */
    boolean optimize();
    
    /**
     * Searches documents by the query.
     * The default field name and the default top hits count can be used in a specific implementation.
     * @param query
     * @return
     */
    SearchResults search(String query);
    
    /**
     * Searches documents by the query against the default field name.
     * The default top hits count can be used in a specific implementation.
     * @param query
     * @param defaultFieldName
     * @return
     */
    SearchResults search(String query, String defaultFieldName);
    
    /**
     * Searches documents by the query against the default field name.
     * The returned item count will not be more than topHitsCount.
     * @param query
     * @param defaultFieldName
     * @param topHitsCount
     * @return
     */
    SearchResults search(String query, String defaultFieldName, int topHitsCount);
}
