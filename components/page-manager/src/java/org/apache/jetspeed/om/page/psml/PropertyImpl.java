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

package org.apache.jetspeed.om.page.psml;

import org.apache.jetspeed.om.page.Property;

/**
 * Bean like implementation of the Parameter interface suitable for
 * Castor serialization.
 *
 * @see org.apache.jetspeed.om.registry.PsmlParameter
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PropertyImpl implements Property, java.io.Serializable
{

    private String name;
    private String value;
    private String layout;

    public PropertyImpl()
    {
    }

    public String getLayout()
    {
        return this.layout;
    }

    public void setLayout(String layout)
    {
        this.layout = layout;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getValue()
    {
        return this.value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }


    public boolean equals(Object object)
    {
        boolean isEqual = true;

        if (object instanceof Property)
        {
            if (this.name!=null)
            {
                isEqual&=this.name.equals(((Property)object).getName());
            }
            else
            {
                isEqual&=((Property)object).getName()==null;
            }

            if (this.value!=null)
            {
                isEqual&=this.value.equals(((Property)object).getValue());
            }
            else
            {
                isEqual&=((Property)object).getValue()==null;
            }

            if (this.layout!=null)
            {
                isEqual&=this.layout.equals(((Property)object).getLayout());
            }
            else
            {
                isEqual&=((Property)object).getLayout()==null;
            }
        }
        else
        {
            isEqual = false;
        }

        return isEqual;
    }

    public Object clone()
        throws java.lang.CloneNotSupportedException
    {
        return super.clone();
    }
    /**
     * <p>
     * getIntValue
     * </p>
     *
     * @see org.apache.jetspeed.om.page.Property#getIntValue()
     * @return
     */
    public int getIntValue()
    {        
        return Integer.parseInt(value);
    }
}