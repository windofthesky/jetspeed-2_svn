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
import java.util.Collection;

import org.apache.jetspeed.prefs.om.PropertySetDef;

/**
 * <p>{@link PropertySetDef} interface implementation.</p>
 * <p>Represents a property set definition.</p>
 *
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class PropertySetDefImpl implements PropertySetDef
{

    /**
     * <p>Property set definition implementation default constructor.</p>
     */
    public PropertySetDefImpl()
    {
    }

    /**
     * <p>Property set def constructor given an set definition name
     * and type.</p>
     * @param propertySetName The property set name.
     * @param propertySetType The property set type.
     */
    public PropertySetDefImpl(String propertySetName, short propertySetType)
    {
        this.propertySetName =
            ((propertySetName != null) ? propertySetName.toLowerCase() : propertySetName);      
        this.propertySetType = propertySetType;
        this.creationDate = new Timestamp(System.currentTimeMillis());
        this.modifiedDate = this.creationDate;
    }

    private int propertySetDefId;

    /**
     * @see org.apache.jetspeed.prefs.om.PropertySetDef#getPropertySetDefId()
     */
    public int getPropertySetDefId()
    {
        return this.propertySetDefId;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.PropertySetDef#setPropertySetDefId(int)
     */
    public void setPropertySetDefId(int propertySetDefId)
    {
        this.propertySetDefId = propertySetDefId;
    }

    private String propertySetName;

    /**
     * @see org.apache.jetspeed.prefs.om.PropertySetDef#getPropertySetName()
     */
    public String getPropertySetName()
    {
        return this.propertySetName;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.PropertySetDef#setPropertySetName(java.lang.String)
     */
    public void setPropertySetName(String propertySetName)
    {
        this.propertySetName = propertySetName;
    }

    private short propertySetType;

    /**
     * @see org.apache.jetspeed.prefs.om.PropertySetDef#getPropertySetType()
     */
    public short getPropertySetType()
    {
        return this.propertySetType;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.PropertySetDef#setPropertySetType(short)
     */
    public void setPropertySetType(short propertySetType)
    {
        this.propertySetType = propertySetType;
    }

    private Collection nodes;

    /**
     * @see org.apache.jetspeed.prefs.om.PropertySetDef#getNodes()
     */
    public Collection getNodes()
    {
        return this.nodes;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.PropertySetDef#setNodes(java.util.Collection)
     */
    public void setNodes(Collection nodes)
    {
        this.nodes = nodes;
    }

    private Collection propertyKeys;

    /**
     * @see org.apache.jetspeed.prefs.om.PropertySetDef#getPropertyKeys()
     */
    public Collection getPropertyKeys()
    {
        return this.propertyKeys;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.PropertySetDef#setPropertyKeys(java.util.Collection)
     */
    public void setPropertyKeys(Collection propertyKeys)
    {
        this.propertyKeys = propertyKeys;
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

}
