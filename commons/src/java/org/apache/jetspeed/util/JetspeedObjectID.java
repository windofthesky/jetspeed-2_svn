/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import org.apache.pluto.om.portlet.PortletDefinition;

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
public class JetspeedObjectID implements PortalObjectID, java.io.Serializable
{
    private String stringOID = null;
    private long oid;

    public JetspeedObjectID(long oid)
    {
        stringOID = String.valueOf(oid);
        this.oid = oid;
    }

    public JetspeedObjectID(long oid, String stringOID)
    {
        this.stringOID = stringOID;
        this.oid  = oid;
    }

    public boolean equals(Object object)
    {
        boolean result = false;

        if (object instanceof JetspeedObjectID)
        {
            result = (oid == ((JetspeedObjectID) object).oid);
        }
        else if (object instanceof String)
        {
            result = stringOID.equals(object);
        }
        else if (object instanceof Long)
        {
            result = (oid == ((Long) object).longValue());
        }                
        else if (object instanceof Integer)
        {
            result = (oid == ((Integer) object).intValue());
        }        
        return (result);
    }

    public int hashCode()
    {
        return (int)oid; // TODO: this could be a problem slicing a long to an int
    }

    public String toString()
    {
        return (stringOID);
    }

    public long longValue()
    {
        return oid;
    }

/*    
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException
    {
        oid = stream.readLong();
        stringOID = String.valueOf(oid);
    }

    private void writeObject(ObjectOutputStream stream) throws IOException
    {
        stream.write((int)oid);
    }
*/
    static public JetspeedObjectID createFromString(String idStr)
    {
        char[] id = idStr.toCharArray();
        long _id = 1;
        for (int i = 0; i < id.length; i++)
        {
            if ((i % 2) == 0)
                _id *= id[i];
            else
                _id ^= id[i];
            _id = Math.abs(_id);
        }
        return new JetspeedObjectID(_id, idStr);
    }

    /**
     * @param portletDefinition
     * @param instanceName
     * @return
     */
    public static JetspeedObjectID createPortletEntityId(PortletDefinition portletDefinition, String instanceName)
    {
        // return createFromString(portletDefinition.getId().toString());
        return createFromString(portletDefinition.getName() + ":" + portletDefinition.getId().toString() + ":" + instanceName);
    }
    
    public long getOID()
    {
        return oid;
    }
}
