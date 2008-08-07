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
package org.apache.jetspeed.security.impl;

import org.apache.jetspeed.security.GroupPrincipal;

/**
 * <p>{@link GroupPrincipal} interface implementation.</p>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class GroupPrincipalImpl extends BasePrincipalImpl implements GroupPrincipal
{
    /** The serial version uid. */
    private static final long serialVersionUID = 6061115481776568899L;

    public GroupPrincipalImpl(String name)
    {
        super(name);
    }
    
    public GroupPrincipalImpl(long id, String groupName)
    {
        this(id, groupName, true, false);
    }

    public GroupPrincipalImpl(long id, String groupName, boolean isEnabled, boolean isMapping)
    {
        super(id, groupName, isEnabled, isMapping);
    }
    
    /**
     * <p>Compares this principal to the specified object.  Returns true
     * if the object passed in matches the principal represented by
     * the implementation of this interface.</p>
     * @param another Principal to compare with.
     * @return True if the principal passed in is the same as that
     * encapsulated by this principal, and false otherwise.

     */
    public boolean equals(Object another)
    {
        if (!(another instanceof GroupPrincipalImpl))
            return false;
        GroupPrincipalImpl principal = (GroupPrincipalImpl)another;
        return this.getName().equals(principal.getName());
    }
    
}
