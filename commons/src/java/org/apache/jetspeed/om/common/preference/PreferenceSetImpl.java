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

package org.apache.jetspeed.om.common.preference;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import javax.portlet.PreferencesValidator;

import org.apache.jetspeed.om.common.AbstractSupportSet;
import org.apache.pluto.om.common.Preference;

/**
 * 
 * PreferenceSetImpl
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class PreferenceSetImpl extends AbstractSupportSet implements PreferenceSetComposite, Serializable
{

    protected HashMap prefMap = new HashMap();

    private String preferenceType;

    private PreferencesValidator validator;

    /**
     * @param wrappedSet
     */
    public PreferenceSetImpl(Set wrappedSet)
    {
        super(wrappedSet);
    }

    public PreferenceSetImpl()
    {
        prefMap = new HashMap();
    }

    /**
     * @see org.apache.pluto.om.common.PreferenceSet#get(java.lang.String)
     */
    public Preference get(String name)
    {
        return (Preference) prefMap.get(name);
    }

    /**
     * @see org.apache.pluto.om.common.PreferenceSetCtrl#add(java.lang.String, java.util.Collection)
     */
    public Preference add(String name, Collection values)
    {
        PreferenceImpl pref = new PreferenceImpl();

        pref.setType(preferenceType);

        pref.setName(name);
        pref.setValues(values);
        add(pref);
        return pref;
    }

    /**
     * @see org.apache.pluto.om.common.PreferenceSetCtrl#remove(java.lang.String)
     */
    public Preference remove(String name)
    {
        Preference pref = (Preference) prefMap.get(name);
        remove(pref);
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

        prefMap.put(pref.getName(), pref);
        return super.add(pref);
    }

    /**
     * @see java.util.Collection#remove(java.lang.Object)
     */
    public boolean remove(Object o)
    {
        Preference pref = (Preference) o;
        prefMap.remove(pref.getName());
        return super.remove(o);
    }

    public Set getNames()
    {
        return prefMap.keySet();
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

        return super.addAll(c);
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

}
