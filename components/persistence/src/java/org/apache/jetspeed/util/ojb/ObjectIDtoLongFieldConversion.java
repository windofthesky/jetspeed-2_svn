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
package org.apache.jetspeed.util.ojb;

import org.apache.jetspeed.util.JetspeedObjectID;
import org.apache.ojb.broker.accesslayer.conversions.ConversionException;
import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;

/**
  * <p style="font-weight: bold">
 * ObjectRelationalBridge field conversion.
 * </p>
 * 
 * Converts between <code>long</code> and <code>ObjectID</code>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 */
public class ObjectIDtoLongFieldConversion implements FieldConversion
{

    /**
     * @see org.apache.ojb.broker.accesslayer.conversions.FieldConversion#javaToSql(java.lang.Object)
     */
    public Object javaToSql(Object arg0) throws ConversionException
    {
        if (arg0 instanceof JetspeedObjectID)
        {
            JetspeedObjectID oid = (JetspeedObjectID) arg0;

            return new Long(oid.longValue());
        }
        else
        {
            return arg0;
        }

    }

    /**
     * @see org.apache.ojb.broker.accesslayer.conversions.FieldConversion#sqlToJava(java.lang.Object)
     */
    public Object sqlToJava(Object arg0) throws ConversionException
    {
        if (arg0 instanceof Number)
        {
            
            return new JetspeedObjectID(((Number)arg0).longValue());
        }
        else
        {
            return arg0;
        }

    }

}
