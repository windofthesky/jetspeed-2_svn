/* Copyright 2004 Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.prefs.om.impl;

import java.sql.Timestamp;

import org.apache.jetspeed.prefs.om.PropertyKey;

/**
 * <p>{@link PropertyKey} interface implementation.</p>
 * <p>Represents a property key.</p>
 *
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class PropertyKeyImpl implements PropertyKey
{
    private long propertyKeyId;
    private int propertyKeyType;

    /**
     * <p>Property key implementation default constructor.</p>
     */
    public PropertyKeyImpl()
    {
    }

    /**
     * <p>Property key constructor given the associated
     * property set definition, the property key name and type.</p>
     * @param propertyKeyName The property key name.
     * @param propertyKeyType The property key type.
     */
    public PropertyKeyImpl(String propertyKeyName,
                           int propertyKeyType)
    {
        this.propertyKeyName = propertyKeyName;
        this.propertyKeyType = propertyKeyType;
        this.creationDate = new Timestamp(System.currentTimeMillis());
        this.modifiedDate = this.creationDate;
    }


    /**
     * @see org.apache.jetspeed.prefs.om.PropertyKey#getPropertyKeyId()
     */
    public long getPropertyKeyId()
    {
        return this.propertyKeyId;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.PropertyKey#setPropertyKeyId(int)
     */
    public void setPropertyKeyId(long propertyKeyId)
    {
        this.propertyKeyId = propertyKeyId;
    }

    private String propertyKeyName;

    /**
     * @see org.apache.jetspeed.prefs.om.PropertyKey#getPropertyKeyName()
     */
    public String getPropertyKeyName()
    {
        return this.propertyKeyName;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.PropertyKey#setPropertyKeyName(java.lang.String)
     */
    public void setPropertyKeyName(String propertyKeyName)
    {
        this.propertyKeyName = propertyKeyName;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.PropertyKey#getPropertyKeyType()
     */
    public int getPropertyKeyType()
    {
        return this.propertyKeyType;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.PropertyKey#setPropertyKeyType(int)
     */
    public void setPropertyKeyType(int propertyKeyType)
    {
        this.propertyKeyType = propertyKeyType;
    }

    private Timestamp creationDate;

    /**
     * @see org.apache.jetspeed.prefs.om.PropertyKey#getCreationDate()
     */
    public Timestamp getCreationDate()
    {
        return this.creationDate;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.PropertyKey#setCreationDate(java.sql.Timestamp)
     */
    public void setCreationDate(Timestamp creationDate)
    {
        this.creationDate = creationDate;
    }

    private Timestamp modifiedDate;

    /**
     * @see org.apache.jetspeed.prefs.om.PropertyKey#getModifiedDate()
     */
    public Timestamp getModifiedDate()
    {
        return this.modifiedDate;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.PropertyKey#setModifiedDate(java.sql.Timestamp)
     */
    public void setModifiedDate(Timestamp modifiedDate)
    {
        this.modifiedDate = modifiedDate;
    }

    /**
     * <p>Convert <code>PropertyKey</code> to string.</p>
     * @return The Property string value.
     */
    public String toString()
    {
        String toStringPropertyKey = "[[propertyKeyName, " + this.propertyKeyName + "], "
            + "[propertyKeyType, " + this.propertyKeyType + "], "
            + "[creationDate, " + this.creationDate + "], "
            + "[modifiedDate, " + this.modifiedDate + "]]";
        return toStringPropertyKey;
    }

}
