/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.spi.om.prefs.impl;

import java.sql.Timestamp;

import org.apache.jetspeed.spi.om.prefs.Property;
import org.apache.jetspeed.spi.om.prefs.PropertyKey;

/**
 * <p>{@link Property} interface implementation.</p>
 * <p>Represents a property key/value pair.</p>
 *
 * @author <a href="david@sensova.com">David Le Strat</a>
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
     * @see org.apache.jetspeed.spi.om.prefs.Property#getPropertyValue(short)
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
     * @see org.apache.jetspeed.spi.om.prefs.Property#setPropertyValue(short, java.lang.String)
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
     * @see org.apache.jetspeed.spi.om.prefs.Property#getPropertyValueId()
     */
    public int getPropertyValueId()
    {
        return this.propertyValueId;
    }

    /**
     * @see org.apache.jetspeed.spi.om.prefs.Property#setPropertyValueId(int)
     */
    public void setPropertyValueId(int propertyValueId)
    {
        this.propertyValueId = propertyValueId;
    }

    private int nodeId;

    /**
     * @see org.apache.jetspeed.spi.om.prefs.Property#getNodeId()
     */
    public int getNodeId()
    {
        return this.nodeId;
    }

    /**
     * @see org.apache.jetspeed.spi.om.prefs.Property#setNodeId(int)
     */
    public void setNodeId(int nodeId)
    {
        this.nodeId = nodeId;
    }

    private int propertyKeyId;

    /**
     * @see org.apache.jetspeed.spi.om.prefs.Property#getPropertyKeyId()
     */
    public int getPropertyKeyId()
    {
        return this.propertyKeyId;
    }

    /**
     * @see org.apache.jetspeed.spi.om.prefs.Property#setPropertyKeyId(int)
     */
    public void setPropertyKeyId(int propertyKeyId)
    {
        this.propertyKeyId = propertyKeyId;
    }

    private PropertyKey propertyKey;

    /**
     * @see org.apache.jetspeed.spi.om.prefs.Property#getPropertyKey()
     */
    public PropertyKey getPropertyKey()
    {
        return this.propertyKey;
    }

    /**
     * @see org.apache.jetspeed.spi.om.prefs.Property#setPropertyKey(org.apache.jetspeed.spi.om.prefs.PropertyKey)
     */
    public void setPropertyKey(PropertyKey propertyKey)
    {
        this.propertyKey = propertyKey;
    }

    private boolean booleanPropertyValue;

    /**
     * @see org.apache.jetspeed.spi.om.prefs.Property#getBooleanPropertyValue()
     */
    public boolean getBooleanPropertyValue()
    {
        return this.booleanPropertyValue;
    }

    /**
     * @see org.apache.jetspeed.spi.om.prefs.Property#setBooleanPropertyValue(boolean)
     */
    public void setBooleanPropertyValue(boolean booleanPropertyValue)
    {
        this.booleanPropertyValue = booleanPropertyValue;
    }

    private Timestamp datePropertyValue;

    /**
     * @see org.apache.jetspeed.spi.om.prefs.Property#getDatePropertyValue()
     */
    public Timestamp getDatePropertyValue()
    {
        return this.datePropertyValue;
    }

    /**
     * @see org.apache.jetspeed.spi.om.prefs.Property#setDatePropertyValue(java.sql.Timestamp)
     */
    public void setDatePropertyValue(Timestamp datePropertyValue)
    {
        this.datePropertyValue = datePropertyValue;
    }

    private long longPropertyValue;

    /**
     * @see org.apache.jetspeed.spi.om.prefs.Property#getLongPropertyValue()
     */
    public long getLongPropertyValue()
    {
        return this.longPropertyValue;
    }

    /**
     * @see org.apache.jetspeed.spi.om.prefs.Property#setLongPropertyValue(long)
     */
    public void setLongPropertyValue(long longPropertyValue)
    {
        this.longPropertyValue = longPropertyValue;
    }

    private double doublePropertyValue;

    /**
     * @see org.apache.jetspeed.spi.om.prefs.Property#getDoublePropertyValue()
     */
    public double getDoublePropertyValue()
    {
        return this.doublePropertyValue;
    }

    /**
     * @see org.apache.jetspeed.spi.om.prefs.Property#setDoublePropertyValue(double)
     */
    public void setDoublePropertyValue(double doublePropertyValue)
    {
        this.doublePropertyValue = doublePropertyValue;
    }

    private String textPropertyValue;

    /**
     * @see org.apache.jetspeed.spi.om.prefs.Property#getTextPropertyValue()
     */
    public String getTextPropertyValue()
    {
        return this.textPropertyValue;
    }

    /**
     * @see org.apache.jetspeed.spi.om.prefs.Property#setTextPropertyValue(java.lang.String)
     */
    public void setTextPropertyValue(String textPropertyValue)
    {
        this.textPropertyValue = textPropertyValue;
    }

    private Timestamp creationDate;

    /**
     * @see org.apache.jetspeed.spi.om.prefs.Property#getCreationDate()
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
     * @see org.apache.jetspeed.spi.om.prefs.Property#getModifiedDate()
     */
    public Timestamp getModifiedDate()
    {
        return this.modifiedDate;
    }

    /**
     * @see org.apache.jetspeed.spi.om.prefs.Property#setModifiedDate(java.sql.Timestamp)
     */
    public void setModifiedDate(Timestamp modifiedDate)
    {
        this.modifiedDate = modifiedDate;
    }

}
