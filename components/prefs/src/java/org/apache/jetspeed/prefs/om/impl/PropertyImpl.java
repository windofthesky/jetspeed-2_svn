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

import org.apache.jetspeed.prefs.om.Property;
import org.apache.jetspeed.prefs.om.PropertyKey;

/**
 * <p>{@link Property} interface implementation.</p>
 * <p>Represents a property key/value pair.</p>
 *
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class PropertyImpl implements Property
{
    
    /**
     * <p>Property implementation default constructor.</p>
     */
    public PropertyImpl()
    {
    }

    /**
     * Property constructor given a property key id, node id
     * and the appropriate value object type and value:
     *
     * <ul>
     *     <li>0=Boolean,</li>
     *     <li>1=Long,</li>
     *     <li>2=Double,</li>
     *     <li>3=String,</li>
     *     <li>4=Timestamp</li>
     * </ul>
     * @param propertyKeyId The property key id.
     * @param nodeId The node id.
     * @param valueObjectType The value object type.
     * @param valueObject The value object.
     */
    public PropertyImpl(int propertyKeyId, int nodeId, short valueObjectType, Object valueObject)
    {
        this.propertyKeyId = propertyKeyId;
        this.nodeId = nodeId;
        this.creationDate = new Timestamp(System.currentTimeMillis());
        this.modifiedDate = this.creationDate;

        setPropertyValue(valueObjectType, (String) valueObject);
    }

    /**
     * @see org.apache.jetspeed.prefs.om.Property#getPropertyValue(short)
     */
    public final String getPropertyValue(short valueObjectType)
    {
        String stringValue = null;

        if (BOOLEAN_TYPE == valueObjectType)
        {
            stringValue = (Boolean.valueOf(this.booleanPropertyValue)).toString();
        }
        else if (LONG_TYPE == valueObjectType)
        {
            stringValue = Long.toString(this.longPropertyValue);
        }
        else if (DOUBLE_TYPE == valueObjectType)
        {
            stringValue = Double.toString(this.doublePropertyValue);
        }
        else if (STRING_TYPE == valueObjectType)
        {
            stringValue = this.textPropertyValue;
        }
        else if (TIMESTAMP_TYPE == valueObjectType)
        {
            stringValue = this.datePropertyValue.toString();
        }
        return stringValue;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.Property#setPropertyValue(short, java.lang.String)
     */
    public final void setPropertyValue(short valueObjectType, String valueObject)
    {
        if (null != valueObject)
        {
            if (BOOLEAN_TYPE == valueObjectType)
            {
                this.booleanPropertyValue = (Boolean.valueOf(valueObject)).booleanValue();
            }
            else if (LONG_TYPE == valueObjectType)
            {
                this.longPropertyValue = (Long.valueOf(valueObject)).longValue();
            }
            else if (DOUBLE_TYPE == valueObjectType)
            {
                this.doublePropertyValue = (Double.valueOf(valueObject)).doubleValue();
            }
            else if (STRING_TYPE == valueObjectType)
            {
                this.textPropertyValue = (String) valueObject;
            }
            else if (TIMESTAMP_TYPE == valueObjectType)
            {
                this.datePropertyValue = Timestamp.valueOf(valueObject);
            }
        }
    }

    private int propertyValueId;

    /**
     * @see org.apache.jetspeed.prefs.om.Property#getPropertyValueId()
     */
    public int getPropertyValueId()
    {
        return this.propertyValueId;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.Property#setPropertyValueId(int)
     */
    public void setPropertyValueId(int propertyValueId)
    {
        this.propertyValueId = propertyValueId;
    }

    private int nodeId;

    /**
     * @see org.apache.jetspeed.prefs.om.Property#getNodeId()
     */
    public int getNodeId()
    {
        return this.nodeId;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.Property#setNodeId(int)
     */
    public void setNodeId(int nodeId)
    {
        this.nodeId = nodeId;
    }

    private int propertyKeyId;

    /**
     * @see org.apache.jetspeed.prefs.om.Property#getPropertyKeyId()
     */
    public int getPropertyKeyId()
    {
        return this.propertyKeyId;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.Property#setPropertyKeyId(int)
     */
    public void setPropertyKeyId(int propertyKeyId)
    {
        this.propertyKeyId = propertyKeyId;
    }

    private PropertyKey propertyKey;

    /**
     * @see org.apache.jetspeed.prefs.om.Property#getPropertyKey()
     */
    public PropertyKey getPropertyKey()
    {
        return this.propertyKey;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.Property#setPropertyKey(org.apache.jetspeed.prefs.om.PropertyKey)
     */
    public void setPropertyKey(PropertyKey propertyKey)
    {
        this.propertyKey = propertyKey;
    }

    private boolean booleanPropertyValue;

    /**
     * @see org.apache.jetspeed.prefs.om.Property#getBooleanPropertyValue()
     */
    public boolean getBooleanPropertyValue()
    {
        return this.booleanPropertyValue;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.Property#setBooleanPropertyValue(boolean)
     */
    public void setBooleanPropertyValue(boolean booleanPropertyValue)
    {
        this.booleanPropertyValue = booleanPropertyValue;
    }

    private Timestamp datePropertyValue;

    /**
     * @see org.apache.jetspeed.prefs.om.Property#getDatePropertyValue()
     */
    public Timestamp getDatePropertyValue()
    {
        return this.datePropertyValue;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.Property#setDatePropertyValue(java.sql.Timestamp)
     */
    public void setDatePropertyValue(Timestamp datePropertyValue)
    {
        this.datePropertyValue = datePropertyValue;
    }

    private long longPropertyValue;

    /**
     * @see org.apache.jetspeed.prefs.om.Property#getLongPropertyValue()
     */
    public long getLongPropertyValue()
    {
        return this.longPropertyValue;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.Property#setLongPropertyValue(long)
     */
    public void setLongPropertyValue(long longPropertyValue)
    {
        this.longPropertyValue = longPropertyValue;
    }

    private double doublePropertyValue;

    /**
     * @see org.apache.jetspeed.prefs.om.Property#getDoublePropertyValue()
     */
    public double getDoublePropertyValue()
    {
        return this.doublePropertyValue;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.Property#setDoublePropertyValue(double)
     */
    public void setDoublePropertyValue(double doublePropertyValue)
    {
        this.doublePropertyValue = doublePropertyValue;
    }

    private String textPropertyValue;

    /**
     * @see org.apache.jetspeed.prefs.om.Property#getTextPropertyValue()
     */
    public String getTextPropertyValue()
    {
        return this.textPropertyValue;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.Property#setTextPropertyValue(java.lang.String)
     */
    public void setTextPropertyValue(String textPropertyValue)
    {
        this.textPropertyValue = textPropertyValue;
    }

    private Timestamp creationDate;

    /**
     * @see org.apache.jetspeed.prefs.om.Property#getCreationDate()
     */
    public Timestamp getCreationDate()
    {
        return this.creationDate;
    }

    /**
     * @see org.apache.jetspeed.ospi.om.prefs.Property#setCreationDate(java.sql.Timestamp)
     */
    public void setCreationDate(Timestamp creationDate)
    {
        this.creationDate = creationDate;
    }

    private Timestamp modifiedDate;

    /**
     * @see org.apache.jetspeed.prefs.om.Property#getModifiedDate()
     */
    public Timestamp getModifiedDate()
    {
        return this.modifiedDate;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.Property#setModifiedDate(java.sql.Timestamp)
     */
    public void setModifiedDate(Timestamp modifiedDate)
    {
        this.modifiedDate = modifiedDate;
    }

}
