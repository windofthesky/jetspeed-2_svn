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

package org.apache.jetspeed.om.preference.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.jetspeed.om.common.MutableDescription;
import org.apache.jetspeed.om.common.MutableDescriptionSet;
import org.apache.jetspeed.om.common.preference.PreferenceComposite;
import org.apache.jetspeed.om.impl.DescriptionImpl;
import org.apache.jetspeed.om.impl.DescriptionSetImpl;
import org.apache.jetspeed.util.HashCodeBuilder;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerAware;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.apache.pluto.om.common.Description;
import org.apache.pluto.om.common.Preference;

/**
 * 
 * <p>
 * AbstractPreferenceImpl
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class PreferenceImpl implements PreferenceComposite, Serializable, PersistenceBrokerAware
{
    private String name;
    protected Collection values;
    private boolean readOnly;
    /** a collection of <code>PreferenceValueObjects</code>
     * that can be persisted in a unique fashion.
     */
    private List valueObjects;

    /** Localized Descriptions */
    private MutableDescriptionSet descriptions;

    /** Unique key for O/R tools*/
    protected long id;

    /** FK to parent portlet */
    protected long parentId;

    /** The type of preference this is either the portlet default or user defined */
    private String type;

    public PreferenceImpl()
    {
        //values = new ArrayList();
        super();
    }

    /**
     * @see org.apache.pluto.om.common.Preference#getName()
     */
    public String getName()
    {
        return name;
    }

    /**
     * @see org.apache.pluto.om.common.Preference#getValues()
     */
    public Iterator getValues()
    {

        return values.iterator();
    }

    /**
     * @see org.apache.pluto.om.common.Preference#isModifiable()
     */
    public boolean isReadOnly()
    {
        return readOnly;
    }

    /**
     * @see org.apache.pluto.om.common.PreferenceCtrl#setName(java.lang.String)
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @see org.apache.pluto.om.common.PreferenceCtrl#setValues(java.util.Collection)
     */
    public void setValues(Collection values)
    {
        this.values = values;
    }

    /**
     * @see org.apache.pluto.om.common.PreferenceCtrl#setDescription(java.lang.String)
     */
    public void setDescription(String description)
    {
        // TODO: Is this still needed as we are using localized text???
        //this.description = description;
        if (descriptions == null)
        {
            descriptions = new DescriptionSetImpl(MutableDescription.TYPE_PREFERENCE);
        }

        descriptions.addDescription(new DescriptionImpl(Locale.getDefault(), description, MutableDescription.TYPE_PREFERENCE));
    }

    /**
     * @see org.apache.pluto.om.common.PreferenceCtrl#setModifiable(boolean)
     */
    public void setReadOnly(boolean modifiable)
    {
        this.readOnly = modifiable;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        if (obj != null && obj instanceof Preference)
        {
            Preference pref = (Preference) obj;
            return pref.getName().equals(this.getName());
        }

        return false;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        HashCodeBuilder hasher = new HashCodeBuilder(23, 83);
        hasher.append(name);
        return hasher.toHashCode();
    }

    /**
     * @see org.apache.jetspeed.om.common.PreferenceComposite#addDescription(java.util.Locale, java.lang.String)
     */
    public void addDescription(Locale locale, String description)
    {
        if (descriptions == null)
        {
            descriptions = new DescriptionSetImpl(MutableDescription.TYPE_PREFERENCE);
        }
        descriptions.addDescription(new DescriptionImpl(locale, description, MutableDescription.TYPE_PREFERENCE));
    }

    /**
     * @see org.apache.jetspeed.om.common.PreferenceComposite#getDescription(java.util.Locale)
     */
    public Description getDescription(Locale locale)
    {
        if (descriptions != null)
        {
            return descriptions.get(locale);
        }
        return null;
    }

    /**
     * Remove when Castor is properly mapped
     * @deprecated
     * @return
     */
    public String getDescription()
    {
        Description desc = getDescription(Locale.getDefault());
        if (desc != null)
        {
            return desc.getDescription();
        }

        return null;
    }

    /**
     * @see org.apache.jetspeed.om.common.PreferenceComposite#getValueAt(int)
     */
    public String getValueAt(int index)
    {
        return getValueArray()[index];
    }

    /**
     * @see org.apache.jetspeed.om.common.PreferenceComposite#getValueArray()
     */
    public String[] getValueArray()
    {
        if (valueObjects != null)
        {
            return (String[]) valueObjects.toArray(new String[valueObjects.size()]);
        }

        return null;
    }

    /**
     * @see org.apache.jetspeed.om.common.PreferenceComposite#setValueAt(int, java.lang.String)
     */
    public void setValueAt(int index, String value)
    {
        if (valueObjects == null)
        {
            valueObjects = new ArrayList();
        }

        valueObjects.set(index, value);

    }

    /**
     * @return
     */
    public String getType()
    {
        return type;
    }

    /**
     * @param string
     */
    public void setType(String string)
    {
        type = string;
    }

    /** 
     * <p>
     * setReadOnly
     * </p>
     * 
     * Why???
     * 
     * @see org.apache.pluto.om.common.PreferenceCtrl#setReadOnly(java.lang.String)
     * @param arg0
     */
    public void setReadOnly(String arg0)
    {
        setReadOnly(new Boolean(arg0).booleanValue());

    }

    /** 
     * <p>
     * isValueSet
     * </p>
     * 
     * @see org.apache.pluto.om.common.Preference#isValueSet()
     * @return
     */
    public boolean isValueSet()
    {
        return values != null && values.size() > 0;
    }

    /** 
     * <p>
     * afterDelete
     * </p>
     * 
     * @see org.apache.ojb.broker.PersistenceBrokerAware#afterDelete(org.apache.ojb.broker.PersistenceBroker)
     * @param arg0
     * @throws org.apache.ojb.broker.PersistenceBrokerException
     */
    public void afterDelete(PersistenceBroker arg0) throws PersistenceBrokerException
    {

    }

    /** 
     * <p>
     * afterInsert
     * </p>
     * 
     * @see org.apache.ojb.broker.PersistenceBrokerAware#afterInsert(org.apache.ojb.broker.PersistenceBroker)
     * @param arg0
     * @throws org.apache.ojb.broker.PersistenceBrokerException
     */
    public void afterInsert(PersistenceBroker arg0) throws PersistenceBrokerException
    {

    }

    /** 
     * <p>
     * afterLookup
     * </p>
     * 
     * @see org.apache.ojb.broker.PersistenceBrokerAware#afterLookup(org.apache.ojb.broker.PersistenceBroker)
     * @param arg0
     * @throws org.apache.ojb.broker.PersistenceBrokerException
     */
    public void afterLookup(PersistenceBroker arg0) throws PersistenceBrokerException
    {
        //TODO we should move this out to a field conversion so as to remove the requirement to implement PersistenceBrokerAware 
        values = PreferenceValueImpl.convertValueObjectsToStrings(this.valueObjects);

    }

    /** 
     * <p>
     * afterUpdate
     * </p>
     * 
     * @see org.apache.ojb.broker.PersistenceBrokerAware#afterUpdate(org.apache.ojb.broker.PersistenceBroker)
     * @param arg0
     * @throws org.apache.ojb.broker.PersistenceBrokerException
     */
    public void afterUpdate(PersistenceBroker arg0) throws PersistenceBrokerException
    {

    }

    /** 
     * <p>
     * beforeDelete
     * </p>
     * 
     * @see org.apache.ojb.broker.PersistenceBrokerAware#beforeDelete(org.apache.ojb.broker.PersistenceBroker)
     * @param arg0
     * @throws org.apache.ojb.broker.PersistenceBrokerException
     */
    public void beforeDelete(PersistenceBroker arg0) throws PersistenceBrokerException
    {

    }

    /** 
     * <p>
     * beforeInsert
     * </p>
     * 
     * @see org.apache.ojb.broker.PersistenceBrokerAware#beforeInsert(org.apache.ojb.broker.PersistenceBroker)
     * @param arg0
     * @throws org.apache.ojb.broker.PersistenceBrokerException
     */
    public void beforeInsert(PersistenceBroker arg0) throws PersistenceBrokerException
    {
        if (this.valueObjects == null)
        {
            this.valueObjects = new ArrayList(values.size());
        }
        PreferenceValueImpl.convertStringsToValueObjects(values, valueObjects);

    }

    /** 
     * <p>
     * beforeUpdate
     * </p>
     * 
     * @see org.apache.ojb.broker.PersistenceBrokerAware#beforeUpdate(org.apache.ojb.broker.PersistenceBroker)
     * @param arg0
     * @throws org.apache.ojb.broker.PersistenceBrokerException
     */
    public void beforeUpdate(PersistenceBroker arg0) throws PersistenceBrokerException
    {
        if (this.valueObjects == null)
        {
            this.valueObjects = new ArrayList(values.size());
        }
        PreferenceValueImpl.convertStringsToValueObjects(values, valueObjects);

    }

}
