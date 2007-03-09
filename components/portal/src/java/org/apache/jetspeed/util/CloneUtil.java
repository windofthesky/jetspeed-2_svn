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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Utility for object cloning
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 */
public class CloneUtil
{
    /**
     * Provides a deep clone serializing/de-serializng <code>objToClone</code>     
     * 
     * @param objToClone The object to be cloned
     * @return The cloned object
     */
    public static final Object deepClone(Object objToClone)
    {
        try
        {
            ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream(100);
            ObjectOutputStream objectoutputstream = new ObjectOutputStream(bytearrayoutputstream);
            objectoutputstream.writeObject(objToClone);
            byte abyte0[] = bytearrayoutputstream.toByteArray();
            objectoutputstream.close();
            ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(abyte0);
            ObjectInputStream objectinputstream = new ObjectInputStream(bytearrayinputstream);
            Object clone = objectinputstream.readObject();
            objectinputstream.close();
            return clone;
        }
        catch (Exception exception)
        {
            // nothing
        }
        return null;
    }

}
