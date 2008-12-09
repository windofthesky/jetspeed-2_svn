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
package org.apache.jetspeed.util;

import java.io.Serializable;

/**
 * JetspeedLongObjectID
 *
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class JetspeedLongObjectID implements Serializable
{
    private Long oid;
    
    public JetspeedLongObjectID(long oid)
    {
        this.oid = new Long(oid);
    }

    public JetspeedLongObjectID(Long oid)
    {
        this.oid = oid;
        if ( oid == null )
        {
            // really cannot have a null here
            throw new NullPointerException();
        }
    }

    public long getOID()
    {
        return oid.longValue();
    }
    
    public Long getLong()
    {
        return oid;
    }
    
    public boolean equals(Object object)
    {
        if (object instanceof PortalObjectID)
        {
            return ((PortalObjectID)object).getOID() == oid.longValue();
        }
        else if (object instanceof Long)
        {
            return ((Long)object).longValue() == oid.longValue();
        }
        else if (object instanceof Integer)
        {
            return ((Integer)object).longValue() == oid.longValue();
        }
        return false;
    }

    public int hashCode()
    {
        return oid.hashCode();
    }
    
    public String toString()
    {
        return oid.toString();
    }
}
