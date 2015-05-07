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

package org.apache.jetspeed.security.spi.impl;

import org.apache.jetspeed.cache.DistributedCacheObject;
import org.apache.jetspeed.security.spi.PersistentJetspeedPermission;

/**
 * @version $Id$
 *
 */
public class PersistentJetspeedPermissionImpl implements PersistentJetspeedPermission, DistributedCacheObject
{
    private static final long serialVersionUID = 9200223005769593282L;
    private Long id;
    private String type;
    private String name;
    private String actions;

    public PersistentJetspeedPermissionImpl()
    {
    }

    public PersistentJetspeedPermissionImpl(String type, String name)
    {
        this.type = type;
        this.name = name;
    }

    public Long getId()
    {
        return id;
    }
    
    public String getType()
    {
        return type;
    }

    public String getName()
    {
        return name;
    }

    public String getActions()
    {
        return actions;
    }
    
    public void setActions(String actions)
    {
        this.actions = actions;
    }

    @Override
    public void notifyChange(int action)
    {
    }
}
