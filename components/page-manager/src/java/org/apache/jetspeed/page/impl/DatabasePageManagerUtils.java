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
package org.apache.jetspeed.page.impl;

import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ojb.broker.util.collections.RemovalAwareCollection;
import org.apache.ojb.broker.util.collections.RemovalAwareList;


/**
 * DatabasePageManagerUtils
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class DatabasePageManagerUtils
{
    protected static Log log = LogFactory.getLog(DatabasePageManagerUtils.class);    
    
    /**
     * OJB 1.0.3 requires collections to be removal aware.
     * Thus we can't seem to get away with just creating ArrayLists
     * This issue on occurs when persisting newly create object collections
     * When persisting objects retrieved with OJB, this issue does not occur
     * 
     * @see JS2-590
     * @return
     */
    public static final Collection createCollection()
    {
        return java.util.Collections.synchronizedCollection(new RemovalAwareCollection());
    }
    
    public static final List createList()
    {
        return java.util.Collections.synchronizedList(new RemovalAwareList());
    }
    
}
