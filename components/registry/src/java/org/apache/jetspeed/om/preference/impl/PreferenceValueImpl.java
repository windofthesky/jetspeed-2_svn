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
package org.apache.jetspeed.om.preference.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.jetspeed.om.common.preference.PreferenceValue;

/**
 * 
 * PreferenceValueObject
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class PreferenceValueImpl implements Serializable, PreferenceValue
{

    private String value;
    
    protected long id;
    protected long preferenceId;

    public PreferenceValueImpl()
    {
        super();
    }

    public PreferenceValueImpl(String value)
    {
        this();
        this.value = value;
    }

    /**
     * @return
     */
    public String getValue()
    {
        return value;
    }

    /**
     * @param string
     */
    public void setValue(String string)
    {
        value = string;
    }

    /**
     * @return same as <code>getValue()</code>
     */
    public String toString()
    {
        return getValue();
    }

    protected final static ArrayList convertValueObjectsToStrings(Collection valueObjs)
    {
        ArrayList values = new ArrayList(valueObjs.size());
        Iterator itr = valueObjs.iterator();
        while (itr.hasNext())
        {
            values.add(itr.next().toString());
        }

        return values;
    }

    protected final static void convertStringsToValueObjects(Collection stringValues, List valueObjects)
    {
        if (valueObjects == null)
        {
            throw new IllegalArgumentException("valueObjects arg cannot be null");
        }
        if (stringValues == null)
        {
            throw new IllegalArgumentException("stringValues arg cannot be null");
        }

        Iterator itr = stringValues.iterator();
        int count = 0;
        try
        {
            while (itr.hasNext())
            {
                String strValue = (String) itr.next();
                if (count < valueObjects.size())
                {
                    PreferenceValueImpl valueObj = (PreferenceValueImpl) valueObjects.get(count);
                    valueObj.setValue(strValue);
                }
                else
                {
                    PreferenceValueImpl valueObj = new PreferenceValueImpl();

                    valueObj.setValue(strValue);
                    valueObjects.add(valueObj);
                }
                count++;
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Unable to instantiate value class.", e);
        }

    }

}
