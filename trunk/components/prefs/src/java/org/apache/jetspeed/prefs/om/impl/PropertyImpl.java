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

/**
 * <p>
 * {@link Property} interface implementation.
 * </p>
 * <p>
 * Represents a property key/value pair.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class PropertyImpl implements Property
{
    /** The serial version uid. */
    private static final long serialVersionUID = 7037975617489867366L;

    private long nodeId;

    private String propertyName;

    private String propertyValue;

    private long propertyValueId;

    /**
     * <p>
     * Property implementation default constructor.
     * </p>
     */
    public PropertyImpl()
    {
    }

    /**
     * Property constructor given a property key id, node id and the appropriate
     * value.
     * 
     * @param nodeId The node id.
     * @param propertyName The property name.
     * @param valueObject The value object.
     */
    public PropertyImpl(long nodeId, String propertyName, Object valueObject)
    {
        this.nodeId = nodeId;
        this.propertyName = propertyName;
        this.creationDate = new Timestamp(System.currentTimeMillis());
        this.modifiedDate = this.creationDate;

        setPropertyValue((String) valueObject);
    }

    /**
     * @see org.apache.jetspeed.prefs.om.Property#getPropertyValue()
     */
    public final String getPropertyValue()
    {
        return propertyValue;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.Property#setPropertyValue(java.lang.String)
     */
    public final void setPropertyValue(String valueObject)
    {
        this.propertyValue = valueObject;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.Property#getPropertyValueId()
     */
    public long getPropertyValueId()
    {
        return this.propertyValueId;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.Property#setPropertyValueId(int)
     */
    public void setPropertyValueId(long propertyValueId)
    {
        this.propertyValueId = propertyValueId;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.Property#getNodeId()
     */
    public long getNodeId()
    {
        return this.nodeId;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.Property#setNodeId(long)
     */
    public void setNodeId(long nodeId)
    {
        this.nodeId = nodeId;
    }

    /**
     * @return Returns the propertyName.
     */
    public String getPropertyName()
    {
        return propertyName;
    }

    /**
     * @param propertyName The propertyName to set.
     */
    public void setPropertyName(String propertyName)
    {
        this.propertyName = propertyName;
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

    /**
     * <p>
     * Convert <code>Property</code> to string.
     * </p>
     * 
     * @return The Property string value.
     */
    public String toString()
    {
        String toStringProperty = "[[nodeId, " + this.nodeId + "], " + "[propertyValue, " + getPropertyValue() + "], "
                + "[creationDate, " + this.creationDate + "], " + "[modifiedDate, " + this.modifiedDate + "]]";
        return toStringProperty;
    }

}
