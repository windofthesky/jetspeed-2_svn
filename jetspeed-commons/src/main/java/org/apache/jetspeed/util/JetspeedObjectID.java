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
 * 
 * JetspeedObjectID
 ** Wraps around the internal Object IDs. By holding both
 ** the string and the integer version of an Object ID this class
 ** helps speed up the internal processing.
 * 
 * @version $Id$
 *
 */
public class JetspeedObjectID implements Serializable
{
    private String stringOID = null;

    public JetspeedObjectID(String stringOID)
    {
        this.stringOID = stringOID;
        if ( stringOID == null )
        {
            // really cannot have a null here
            throw new NullPointerException();
        }
    }

    public boolean equals(Object object)
    {
        if (object instanceof JetspeedObjectID)
        {
            return ((JetspeedObjectID)object).stringOID.equals(stringOID);
        }
        else if (object instanceof String)
        {
            return ((String)object).equals(stringOID);
        }
        return false;
    }

    public int hashCode()
    {
        return stringOID.hashCode();
    }

    public String toString()
    {
        return (stringOID);
    }

    static public JetspeedObjectID createFromString(String idStr)
    {
        return new JetspeedObjectID(idStr);
    }

}
