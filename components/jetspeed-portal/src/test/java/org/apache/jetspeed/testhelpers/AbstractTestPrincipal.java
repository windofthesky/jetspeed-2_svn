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
package org.apache.jetspeed.testhelpers;

import java.util.Collections;
import java.util.Map;

import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.JetspeedPrincipalType;
import org.apache.jetspeed.security.SecurityAttributeType;
import org.apache.jetspeed.security.SecurityAttributeTypes;
import org.apache.jetspeed.security.impl.TransientJetspeedPrincipal;

public class AbstractTestPrincipal extends TransientJetspeedPrincipal
{
    private static final SecurityAttributeTypes attributeTypes = new SecurityAttributeTypes()
    {

        public Map<String, SecurityAttributeType> getAttributeTypeMap()
        {
            return Collections.emptyMap();
        }

        public Map<String, SecurityAttributeType> getAttributeTypeMap(String category)
        {
            return Collections.emptyMap();
        }

        public boolean isExtendable()
        {
            return false;
        }

        public boolean isReadOnly()
        {
            return true;
        }
    };
    
    private JetspeedPrincipalType type;
    
    private static final long serialVersionUID = 1L;
    

    public AbstractTestPrincipal(final String type, String name)
    {
        super(type, name);
        this.type = new JetspeedPrincipalType()
        {               
            public SecurityAttributeTypes getAttributeTypes()
            {
                return attributeTypes;
            }

            public String getClassName()
            {
                return null;
            }

            public String getName()
            {
                return type;
            }

            public Class<JetspeedPrincipal> getPrincipalClass()
            {
                return null;
            }
        };
    }

    @Override
    public synchronized JetspeedPrincipalType getType()
    {
        return type;
    }
}
