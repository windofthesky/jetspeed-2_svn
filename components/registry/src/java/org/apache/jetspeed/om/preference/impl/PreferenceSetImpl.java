/*
 * Copyright 2000-2004 The Apache Software Foundation.
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

package org.apache.jetspeed.om.preference.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.portlet.PreferencesValidator;

import org.apache.jetspeed.om.common.preference.PreferenceComposite;
import org.apache.jetspeed.om.common.preference.PreferenceSetComposite;
import org.apache.pluto.om.common.Preference;

/**
 * 
 * PreferenceSetImpl
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class PreferenceSetImpl implements PreferenceSetComposite, Serializable
{

    private String preferenceType;

    private PreferencesValidator validator;

    protected Collection innerCollection;

    public PreferenceSetImpl(Collection c)
    {
        innerCollection = c;
    }

    public PreferenceSetImpl()
    {
        innerCollection = new ArrayList();
    }

    /**
     * @see org.apache.pluto.om.common.PreferenceSet#get(java.lang.String)
     */
    public Preference get(String name)
    {
        Iterator itr = innerCollection.iterator();
        while (itr.hasNext())
        {
            Preference p = (Preference) itr.next();
            if (p.getName().equals(name))
            {
                return p;
            }
        }

        return null;
    }

    /**
     * @see org.apache.pluto.om.common.PreferenceSetCtrl#add(java.lang.String, java.util.List)
     */
    public Preference add(String name, List values)
    {
        DefaultPreferenceImpl pref = new DefaultPreferenceImpl();

        pref.setType(preferenceType);

        pref.setName(name);
        pref.setValues(values);
        innerCollection.add(pref);
        return pref;
    }

    /**
     * @see org.apache.pluto.om.common.PreferenceSetCtrl#remove(java.lang.String)
     */
    public Preference remove(String name)
    {
        Preference pref = get(name);
        if (pref != null)
        {
            remove(pref);
        }

        return pref;
    }

    /**
     * @see org.apache.pluto.om.common.PreferenceSetCtrl#remove(org.apache.pluto.om.common.Preference)
     */
    public void remove(Preference preference)
    {
        remove((Object) preference);
    }

    /**
     * @see java.util.Collection#add(java.lang.Object)
     */
    public boolean add(Object o)
    {

        PreferenceComposite pref = (PreferenceComposite) o;
        if (preferenceType == null)
        {
            preferenceType = pref.getType();
        }

        return innerCollection.add(pref);
    }

    /**
     * @see java.util.Collection#remove(java.lang.Object)
     */
    public boolean remove(Object o)
    {
        Preference pref = (Preference) o;

        return innerCollection.remove(o);
    }

    public Set getNames()
    {
        HashSet nameSet = new HashSet();
        Iterator itr = innerCollection.iterator();
        while (itr.hasNext())
        {
            Preference pref = (Preference) itr.next();
            nameSet.add(pref.getName());
        }

        return nameSet;
    }

    /**
         * @return The type of preference this Set is holding which
         * wil either be "user preference" or "default preference"     
         */
    public String getPreferenceType()
    {
        return preferenceType;
    }

    /**
         * @param string
         */
    public void setPreferenceType(String string)
    {
        preferenceType = string;
    }

    /**
     * @see java.util.Collection#addAll(java.util.Collection)
     */
    public boolean addAll(Collection c)
    {
        // Auto-initialize the preference "type" this preference set will contain
        if (preferenceType == null && c.size() > 0)
        {
            Object[] prefArray = c.toArray();
            PreferenceComposite pref = (PreferenceComposite) prefArray[0];
            preferenceType = pref.getType();
        }

        return innerCollection.addAll(c);
    }

    /** 
     * <p>
     * getPreferencesValidator
     * </p>
     * 
     * @see org.apache.pluto.om.common.PreferenceSet#getPreferencesValidator()
     * @return
     */
    public PreferencesValidator getPreferencesValidator()
    {
        return validator;
    }

    /**
     * 
     * <p>
     * setPreferenceValidator
     * </p>
     * Sets the validaotr that will be used within this prefrence set.
     * 
     * @param validator
     *
     */
    public void setPreferenceValidator(PreferencesValidator validator)
    {
        if (validator == null)
        {
            throw new IllegalArgumentException("PreferenceSetImpl.setPreferenceValidator() cannot have a null validator argument.");
        }

        this.validator = validator;
    }

    /**
     * @see org.apache.jetspeed.om.common.preference.PreferenceSetComposite#size()
     */
    public int size()
    {        
        return innerCollection.size();
    }

    /**
     * @see org.apache.pluto.om.common.PreferenceSet#iterator()
     */
    public Iterator iterator()
    {        
        return innerCollection.iterator();
    }

    /**
     * @return
     */
    public Collection getInnerCollection()
    {
        return innerCollection;
    }

    /**
     * @param collection
     */
    public void setInnerCollection(Collection collection)
    {
        innerCollection = collection;
    }

}
