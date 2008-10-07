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

import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.JetspeedPrincipalType;
import org.apache.jetspeed.security.SecurityAttributeTypes;

/**
 * @version $Id$
 */
public class JetspeedPrincipalTypeImpl implements JetspeedPrincipalType
{
    private String name;
    private Class<JetspeedPrincipal> principalClass;
    private SecurityAttributeTypes attributeTypes;

    @SuppressWarnings("unchecked") 
    public JetspeedPrincipalTypeImpl(String name, String className, SecurityAttributeTypes attributeTypes) throws ClassNotFoundException
    {
        this.name = name;
        this.principalClass = (Class<JetspeedPrincipal>)Class.forName(className);
        if (!JetspeedPrincipal.class.isAssignableFrom(principalClass))
        {
            throw new ClassCastException("Not a subclass of JetspeedPrincipal: "+principalClass);
        }
        this.attributeTypes = attributeTypes;
    }

    public SecurityAttributeTypes getAttributeTypes()
    {
        return attributeTypes;
    }

    public String getClassName()
    {
        return principalClass.getName();
    }
    
    public Class<JetspeedPrincipal> getPrincipalClass()
    {
        return principalClass;
    }

    public String getName()
    {
        return name;
    }
}
