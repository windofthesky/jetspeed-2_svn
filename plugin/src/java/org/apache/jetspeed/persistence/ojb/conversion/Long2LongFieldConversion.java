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
package org.apache.jetspeed.persistence.ojb.conversion;

import org.apache.ojb.broker.accesslayer.conversions.ConversionException;
import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;

/**
 * Long2LongFieldConversion
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class Long2LongFieldConversion implements FieldConversion
{

    private static final Long NULL_LONG = null;
    private static final Long ZERO = new Long(0);

    public Object javaToSql(Object obj) throws ConversionException
    {
        if (obj instanceof Long)
        {
            Long instance = (Long) obj;
            if (instance.equals(ZERO))
            {
                return NULL_LONG;
            }
            else
            {
                return obj;
            }
        }
        else
        {
            return obj;
        }
    }

    public Object sqlToJava(Object obj) throws ConversionException
    {
        return obj;
    }
}
