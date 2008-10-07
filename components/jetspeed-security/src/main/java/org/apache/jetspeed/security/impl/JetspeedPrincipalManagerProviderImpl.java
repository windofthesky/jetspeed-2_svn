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

package org.apache.jetspeed.security.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.jetspeed.security.JetspeedPrincipalManager;
import org.apache.jetspeed.security.JetspeedPrincipalManagerProvider;
import org.apache.jetspeed.security.JetspeedPrincipalType;

/**
 * @version $Id$
 *
 */
public class JetspeedPrincipalManagerProviderImpl implements JetspeedPrincipalManagerProvider
{
    private Map<String, JetspeedPrincipalType> nameMap;
    private Map<String, JetspeedPrincipalType> classNameMap;
    private Map<String, JetspeedPrincipalManager> managersMap;
    
    public JetspeedPrincipalManagerProviderImpl(Set<JetspeedPrincipalManager> managers)
    {
        nameMap = new HashMap<String, JetspeedPrincipalType>();
        classNameMap = new HashMap<String, JetspeedPrincipalType>();
        managersMap = new HashMap<String, JetspeedPrincipalManager>();
        for (JetspeedPrincipalManager m : managers)
        {
            JetspeedPrincipalType type = m.getPrincipalType();
            if (nameMap.containsKey(type.getName()))
            {
                throw new IllegalArgumentException("Duplicate JetspeedPrincipalType.name "+type.getName());
            }
            if (classNameMap.containsKey(type.getClassName()))
            {
                throw new IllegalArgumentException("Duplicate JetspeedPrincipalType.className "+type.getClassName());
            }
            nameMap.put(type.getName(), type);
            classNameMap.put(type.getClassName(), type);
            managersMap.put(type.getName(), m);
        }
        this.nameMap = Collections.unmodifiableMap(nameMap);
    }

    public JetspeedPrincipalManager getManager(JetspeedPrincipalType type)
    {
        return managersMap.get(type.getName());
    }

    public JetspeedPrincipalType getPrincipalType(String name)
    {
        return nameMap.get(name);
    }

    public JetspeedPrincipalType getPrincipalTypeByClassName(String className)
    {
        return classNameMap.get(className);
    }

    public Map<String, JetspeedPrincipalType> getPrincipalTypeMap()
    {
        return nameMap;
    }
    
    public void destroy()
    {
        nameMap = null;
        classNameMap = null;
        managersMap = null;
    }
}
