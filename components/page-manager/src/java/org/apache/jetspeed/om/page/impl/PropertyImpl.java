/*
 * Copyright 2005 The Apache Software Foundation.
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
package org.apache.jetspeed.om.page.impl;

import org.apache.jetspeed.om.page.Property;

/**
 * PropertyImpl
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class PropertyImpl implements Property
{
    private String layout;
    private String name;
    private String value;

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Property#getLayout()
     */
    public String getLayout()
    {
        return layout;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Property#setLayout(java.lang.String)
     */
    public void setLayout(String layoutName)
    {
        this.layout = layout;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Property#getName()
     */
    public String getName()
    {
        return name;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Property#setName(java.lang.String)
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Property#getValue()
     */
    public String getValue()
    {
        return value;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Property#getIntValue()
     */
    public int getIntValue()
    {
        return -1; // NYI
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Property#setValue(java.lang.String)
     */
    public void setValue(String value)
    {
        this.value = value;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Property#clone()
     */
    public Object clone() throws CloneNotSupportedException
    {
        return null; // NYI
    }
}
