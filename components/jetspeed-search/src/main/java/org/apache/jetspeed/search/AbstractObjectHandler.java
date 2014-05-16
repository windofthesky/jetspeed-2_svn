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

import java.util.HashSet;
import java.util.Set;

/**
 * Abstract handler that new handlers can dervie from
 * 
 * @author <a href="mailto:jford@apache.org">Jeremy Ford</a>
 * @version $Id$
 */
public abstract class AbstractObjectHandler implements ObjectHandler
{
    protected final HashSet fields = new HashSet();
    protected final HashSet keywords = new HashSet();
    

    /** 
     * @see org.apache.jetspeed.search.ObjectHandler#getFields()
     */
    public Set getFields()
    {
       return fields;
    }
    
    /**
     * @see org.apache.jetspeed.search.ObjectHandler#getKeywords()
     */
    public Set getKeywords()
    {
        return keywords;
    }

}
