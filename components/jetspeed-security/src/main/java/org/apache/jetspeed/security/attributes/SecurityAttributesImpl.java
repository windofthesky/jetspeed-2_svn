/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.security.attributes;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.apache.jetspeed.security.BasePrincipal;


public class SecurityAttributesImpl implements SecurityAttributes
{
    private Map<String, SecurityAttribute> attributes;
    private Principal principal;

    public SecurityAttributesImpl() 
    {}
    
    public SecurityAttributesImpl(Principal principal, Map<String, SecurityAttribute> attribs)
    {
        this.principal = principal;
        this.attributes = attribs;
    }
    
    public Map<String, SecurityAttribute> getAttributes()
    {
        return attributes;
    }

    public Map<String, SecurityAttribute> getAttributes(String kind)
    {
        Map<String, SecurityAttribute> result = new HashMap<String, SecurityAttribute>();
        for (Map.Entry<String, SecurityAttribute> e : this.attributes.entrySet())
        {
            SecurityAttribute attr = e.getValue();
            if (attr.getType().equals(kind))
            {
                result.put(attr.getName(), attr);
            }
        }
        return result;
    }
    
    public Principal getPrincipal()
    {
        return principal;
    }

    public SecurityAttribute createAttribute(String name, String value)
    {
        return new SecurityAttributeImpl(principal, name, SecurityAttributes.SECURITY_ATTRIBUTE, value);
    }

    public SecurityAttribute createUserInformation(String name, String value)
    {
        return new SecurityAttributeImpl(principal, name, SecurityAttributes.USER_INFORMATION, value);
    }
}
