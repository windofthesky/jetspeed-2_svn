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

import org.apache.jetspeed.spi.om.prefs.PropertyKey;

/**
 * <p>{@link PropertyKey} interface implementation.</p>
 * <p>Represents a property key.</p>
 *
 * @author <a href="david@sensova.com">David Le Strat</a>
 */
public class PropertyKeyImpl implements PropertyKey
{

    /**
     * <p>Property key implementation default constructor.</p>
     */
    public PropertyKeyImpl()
    {
    }

    /**
     * <p>Property key constructor given the associated
     * property set definition, the property key name and type.</p>
     * @param propertySetDefId The property set definition.
     * @param propertyKeyName The property key name.
     * @param propertyKeyType The property key type.
     */
    public PropertyKeyImpl(int propertySetDefId,
                           String propertyKeyName,
                           short propertyKeyType)
    {
        this.propertySetDefId = propertySetDefId;
        this.propertyKeyName =
            ((propertyKeyName != null) ? propertyKeyName.toLowerCase() : propertyKeyName);
        this.propertyKeyType = propertyKeyType;
        this.creationDate = new Timestamp(System.currentTimeMillis());
        this.modifiedDate = this.creationDate;
    }

    private int propertyKeyId;

    /**
     * @see org.apache.jetspeed.spi.om.prefs.PropertyKey#getPropertyKeyId()
     */
    public int getPropertyKeyId()
    {
        return this.propertyKeyId;
    }

    /**
     * @see org.apache.jetspeed.spi.om.prefs.PropertyKey#setPropertyKeyId(int)
     */
    public void setPropertyKeyId(int propertyKeyId)
    {
        this.propertyKeyId = propertyKeyId;
    }

    private int propertySetDefId;

    /**
     * @see org.apache.jetspeed.spi.om.prefs.PropertyKey#getPropertySetDefId()
     */
     public int getPropertySetDefId()
     {
        return this.propertySetDefId;
     }

    /**
     * @see org.apache.jetspeed.spi.om.prefs.PropertyKey#setPropertySetDefId(int)
     */
    public void setPropertySetDefId(int propertySetDefId)
    {
        this.propertySetDefId = propertySetDefId;
    }

    private String propertyKeyName;

    /**
     * @see org.apache.jetspeed.spi.om.prefs.PropertyKey#getPropertyKeyName()
     */
    public String getPropertyKeyName()
    {
        return this.propertyKeyName;
    }

    /**
     * @see org.apache.jetspeed.spi.om.prefs.PropertyKey#setPropertyKeyName(java.lang.String)
     */
    public void setPropertyKeyName(String propertyKeyName)
    {
        this.propertyKeyName = propertyKeyName;
    }

    private short propertyKeyType;

    /**
     * @see org.apache.jetspeed.spi.om.prefs.PropertyKey#getPropertyKeyType()
     */
    public short getPropertyKeyType()
    {
        return this.propertyKeyType;
    }

    /**
     * @see org.apache.jetspeed.spi.om.prefs.PropertyKey#setPropertyKeyType(short)
     */
    public void setPropertyKeyType(short propertyKeyType)
    {
        this.propertyKeyType = propertyKeyType;
    }

    private Timestamp creationDate;

    /**
     * @see org.apache.jetspeed.spi.om.prefs.PropertyKey#getCreationDate()
     */
    public Timestamp getCreationDate()
    {
        return this.creationDate;
    }

    /**
     * @see org.apache.jetspeed.spi.om.prefs.PropertyKey#setCreationDate(java.sql.Timestamp)
     */
    public void setCreationDate(Timestamp creationDate)
    {
        this.creationDate = creationDate;
    }

    private Timestamp modifiedDate;

    /**
     * @see org.apache.jetspeed.spi.om.prefs.PropertyKey#getModifiedDate()
     */
    public Timestamp getModifiedDate()
    {
        return this.modifiedDate;
    }

    /**
     * @see org.apache.jetspeed.spi.om.prefs.PropertyKey#setModifiedDate(java.sql.Timestamp)
     */
    public void setModifiedDate(Timestamp modifiedDate)
    {
        this.modifiedDate = modifiedDate;
    }

}
