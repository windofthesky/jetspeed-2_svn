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

package org.apache.jetspeed.om.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.jetspeed.util.HashCodeBuilder;
import org.apache.pluto.om.common.Description;
import org.apache.pluto.om.common.Preference;

/**
 * 
 * BasePreference
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class BasePreference implements PreferenceComposite, Serializable
{
    private String name;
    protected Collection values;
    private boolean modifiable;
    /** a collection of <code>PreferenceValueObjects</code>
     * that can be persisted in a unique fashion.
     */
    private List valueObjects;

    /** Localized Descriptions */
    private MutableDescriptionSet descriptions;

    /** Unique key for O/R tools*/
    protected long id;

    /** FK to parent portlet */
    protected long portletId;

    public BasePreference()
    {
        values = new ArrayList();
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
        if (values == null)
        {
            values = convertValueObjectsToStrings(this.valueObjects);
        }
        return values.iterator();
    }

    /**
     * @see org.apache.pluto.om.common.Preference#isModifiable()
     */
    public boolean isModifiable()
    {
        return modifiable;
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
        convertStringsToValueObjects(values, valueObjects);
    }

    /**
     * @see org.apache.pluto.om.common.PreferenceCtrl#setDescription(java.lang.String)
     */
    public void setDescription(String description)
    {
        // TODO: Is this stile needed as we are using localized text???
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
    public void setModifiable(boolean modifiable)
    {
        this.modifiable = modifiable;
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

    protected final static ArrayList convertValueObjectsToStrings(Collection valueObjs)
    {
        ArrayList values = new ArrayList(valueObjs.size());
        Iterator itr = valueObjs.iterator();
        while (itr.hasNext())
        {
            values.add(itr.next().toString());
        }

        return values;
    }

    protected static final void convertStringsToValueObjects(Collection stringValues, List valueObjects)
    {
        if (valueObjects == null)
        {
            valueObjects = new ArrayList(stringValues.size());
        }

        Iterator itr = stringValues.iterator();
        int count = 0;
        while (itr.hasNext())
        {
            String strValue = (String) itr.next();
            if (valueObjects.size() > count)
            {
                PreferenceValueObject valueObj = (PreferenceValueObject) valueObjects.get(count);
                valueObj.setValue(strValue);
            }
            else
            {
                PreferenceValueObject valueObj = new PreferenceValueObject();
                valueObj.setValue(strValue);
                valueObjects.add(valueObj);
            }
            count++;
        }

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

}