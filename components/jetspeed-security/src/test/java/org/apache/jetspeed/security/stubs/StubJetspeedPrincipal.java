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
package org.apache.jetspeed.security.stubs;

import java.sql.Timestamp;
import java.util.Map;

import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.JetspeedPrincipalType;
import org.apache.jetspeed.security.SecurityAttributes;
import org.apache.jetspeed.security.SecurityException;


/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 */

public class StubJetspeedPrincipal implements JetspeedPrincipal
{

    /* (non-Javadoc)
     * @see org.apache.jetspeed.security.JetspeedPrincipal#getCreationDate()
     */
    public Timestamp getCreationDate()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.security.JetspeedPrincipal#getId()
     */
    public Long getId()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.security.JetspeedPrincipal#getInfoMap()
     */
    public Map<String, String> getInfoMap()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.security.JetspeedPrincipal#getModifiedDate()
     */
    public Timestamp getModifiedDate()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.security.JetspeedPrincipal#getName()
     */
    public String getName()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.security.JetspeedPrincipal#getSecurityAttributes()
     */
    public SecurityAttributes getSecurityAttributes()
    {
        
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.security.JetspeedPrincipal#getType()
     */
    public JetspeedPrincipalType getType()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isEnabled()
    {
        return true;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.security.JetspeedPrincipal#isExtendable()
     */
    public boolean isExtendable()
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isMapped()
    {
        return true;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.security.JetspeedPrincipal#isReadOnly()
     */
    public boolean isReadOnly()
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.security.JetspeedPrincipal#isRemovable()
     */
    public boolean isRemovable()
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.security.JetspeedPrincipal#isTransient()
     */
    public boolean isTransient()
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.security.JetspeedPrincipal#setEnabled(boolean)
     */
    public void setEnabled(boolean enabled) throws SecurityException
    {
        // TODO Auto-generated method stub

    }

    public Long getDomainId()
    {
        // TODO Auto-generated method stub
        return 1L;
    }

}
