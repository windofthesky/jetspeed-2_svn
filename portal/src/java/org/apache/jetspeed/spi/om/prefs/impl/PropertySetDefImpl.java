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
import java.util.Collection;

import org.apache.jetspeed.spi.om.prefs.PropertySetDef;

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
     * @see org.apache.jetspeed.spi.om.prefs.PropertySetDef#getPropertySetDefId()
     */
    public int getPropertySetDefId()
    {
        return this.propertySetDefId;
    }

    /**
     * @see org.apache.jetspeed.spi.om.prefs.PropertySetDef#setPropertySetDefId(int)
     */
    public void setPropertySetDefId(int propertySetDefId)
    {
        this.propertySetDefId = propertySetDefId;
    }

    private String propertySetName;

    /**
     * @see org.apache.jetspeed.spi.om.prefs.PropertySetDef#getPropertySetName()
     */
    public String getPropertySetName()
    {
        return this.propertySetName;
    }

    /**
     * @see org.apache.jetspeed.spi.om.prefs.PropertySetDef#setPropertySetName(java.lang.String)
     */
    public void setPropertySetName(String propertySetName)
    {
        this.propertySetName = propertySetName;
    }

    private short propertySetType;

    /**
     * @see org.apache.jetspeed.spi.om.prefs.PropertySetDef#getPropertySetType()
     */
    public short getPropertySetType()
    {
        return this.propertySetType;
    }

    /**
     * @see org.apache.jetspeed.spi.om.prefs.PropertySetDef#setPropertySetType(short)
     */
    public void setPropertySetType(short propertySetType)
    {
        this.propertySetType = propertySetType;
    }

    private Collection propertyKeys;

    /**
     * @see org.apache.jetspeed.spi.om.prefs.PropertySetDef#getPropertyKeys()
     */
    public Collection getPropertyKeys()
    {
        return this.propertyKeys;
    }

    /**
     * @see org.apache.jetspeed.spi.om.prefs.PropertySetDef#setPropertyKeys(java.util.Collection)
     */
    public void setPropertyKeys(Collection propertyKeys)
    {
        this.propertyKeys = propertyKeys;
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
