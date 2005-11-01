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

/**
 * Bean like implementation of the Parameter interface suitable for
 * Castor serialization.
 *
 * @see org.apache.jetspeed.om.registry.PsmlParameter
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PropertyImpl implements java.io.Serializable
{
    private String name;
    private String value;

    public PropertyImpl()
    {
    }

    public String getLayout()
    {
        // property layout name deprecated
        return null;
    }

    public void setLayout(String layout)
    {
        // property layout name deprecated
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
