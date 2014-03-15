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

import org.apache.jetspeed.om.page.FragmentProperty;

import java.util.ArrayList;

/**
 * DatabasePageManagerCachedFragmentPropertyList
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id: $
*/
public class DatabasePageManagerCachedFragmentPropertyList extends ArrayList<FragmentProperty>
{
    private static final long serialVersionUID = 1L;
    
    private String baseFragmentsElementPath;
    private String principalScope;
    private String principalKey;

    public DatabasePageManagerCachedFragmentPropertyList(String baseFragmentsElementPath)
    {
        this.baseFragmentsElementPath = baseFragmentsElementPath;
    }
    
    public DatabasePageManagerCachedFragmentPropertyList(String principalScope, String principalKey)
    {
        this.principalScope = principalScope;
        this.principalKey = principalKey;
    }
    
    /**
     * @return the baseFragmentsElementPath
     */
    public String getBaseFragmentsElementPath()
    {
        return baseFragmentsElementPath;
    }

    /**
     * @return the principalScope
     */
    public String getPrincipalScope()
    {
        return principalScope;
    }

    /**
     * @return the principalKey
     */
    public String getPrincipalKey()
    {
        return principalKey;
    }
}
