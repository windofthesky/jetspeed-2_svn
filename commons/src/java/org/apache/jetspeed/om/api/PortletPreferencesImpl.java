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
package org.apache.jetspeed.om.api;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;

import javax.portlet.PortletPreferences;
import javax.portlet.UnmodifiableException;
import javax.portlet.ValidatorException;

import org.apache.jetspeed.om.common.PreferenceComposite;
import org.apache.jetspeed.om.common.PreferenceSetImpl;
import org.apache.jetspeed.om.common.PreferenceSetComposite;
import org.apache.pluto.om.common.Preference;

/**
 * PortletPreferencesImpl
 * <br/>
 * This is a concrete implentation of the <code>javax.portlet.PortletPreferencesObject</code>
 * <br/>
 * You should use this object to read and write user preferences for portlets
 * back to the container. 
 * 
 * @see javax.portlet.PortletPreferencesObject
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class PortletPreferencesImpl extends PreferenceSetImpl implements PortletPreferences, PreferenceSetComposite
{
    private static final String NULL_KEY_MSG = "The preference \"key\" argument cannot be null";

    private PreferenceSetComposite defaultPreferences;
    private PreferenceSetComposite originalPreferences;

    /**
     * Creates an instance of PortletPreferencesImpl using the
     * the PreferenceSet from the registry that matches with the portlet
     * this PortletPreferencesImpl is associated with.   The PreferenceSet
     * IS NEVER modified by this PortletPreferencesImpl, it is just used
     * as guide for a users PortletPreferences.
     * 
     * @param prefSet PreferenceSet from the registry that will be used as a
     * base for the PortletPreferenceImpl.
     */
    public PortletPreferencesImpl(PreferenceSetComposite defaultPreferences)
    {
        this();
        this.defaultPreferences = defaultPreferences;
    }

    public PortletPreferencesImpl()
    {
        super();
    }

    /**
     * 
     * @param defaultPreferences
     */
    public void setDefaultPreferences(PreferenceSetComposite defaultPreferences)
    {
        this.defaultPreferences = defaultPreferences;
    }

    /**
     * <p>
     * copied from: <code>javax.portlet.PortletPreferences</code>
     * </p>
     * <p> 
     * Returns true, if the value of this key can be modified by the user.
     * </p>
     * <p>
     * Modifiable preferences can be changed by the portlet in any standard portlet mode (EDIT, HELP, VIEW). Per default every preference is modifiable.
     * </p>
     * <p>
     * Non-modifiable preferences cannot be changed by the portlet in any standard portlet mode, but may be changed by administrative modes. 
     * Preferences are non-modifiable, if they are defined in the deployment descriptor with modifiable set to zero.
     * </p>
     * 
     * @return true, if the value of this key can be changed, or if the key is not known
     * @throws java.lang.IllegalArgumentException - if key is null.
     * 
     * 
     * @see javax.portlet.PortletPreferences#isModifiable(java.lang.String)
     */
    public boolean isModifiable(String key)
    {
        // Key cannot be null
        if (key == null)
        {
            throw new IllegalArgumentException(NULL_KEY_MSG);
        }

        Preference pref = defaultPreferences.get(key);
        if (pref == null || pref.isModifiable())
        {
            return true;
        }
        else
        {
            return false;
        }

    }

    /**
     * <p>
     * copied from: javax.portlet.PortletPreferences
     * </p>
     * <p>
     * Returns the first String value associated with the specified key in this preference. Returns the specified default if there is no value associated with the key, or if the backing store is inaccessible.
     * </p>
     * <p>
     * Some implementations may store default values in their backing stores. If there is no value associated with the specified key but there is such a stored default, it is returned in preference to the 
     * specified default.
     * </p>
     * @param key - key for which the associated value is to be returned
     * @param def - the value to be returned in the event that there is no value available associated with this key.
     * @return the value associated with key, or def if no value is associated with key, or the backing store is inaccessible.
     * @throws java.lang.IllegalArgumentException - if key is null. (A null value for def is permitted.)
     * 
     * 
     * @see javax.portlet.PortletPreferences#getValue(java.lang.String, java.lang.String)
     */
    public String getValue(String key, String def)
    {
        // Key cannot be null
        if (key == null)
        {
            throw new IllegalArgumentException(NULL_KEY_MSG);
        }

        PreferenceComposite pref = (PreferenceComposite) get(key);

        if ((pref == null || pref.getValueAt(0) == null)
            && defaultPreferences.get(key) != null
            && defaultPreferences.get(key).getValues() != null)
        {
            pref = (PreferenceComposite) defaultPreferences.get(key);
        }

        if (pref != null)
        {
            return pref.getValueAt(0);
        }

        return def;
    }

    /**
     * {@inheritDoc} 
     * 
     * @see javax.portlet.PortletPreferences#getValues(java.lang.String, java.lang.String[])
     */
    public String[] getValues(String key, String[] def)
    {
        if (key == null)
        {
            throw new IllegalArgumentException(NULL_KEY_MSG);
        }

        PreferenceComposite pref = (PreferenceComposite) get(key);

        if (pref == null)
        {
            pref = (PreferenceComposite) defaultPreferences.get(key);
        }

        if (pref != null)
        {
            return pref.getValueArray();
        }

        return def;
    }

    /**
     * @see javax.portlet.PortletPreferences#setValue(java.lang.String, java.lang.String)
     */
    public void setValue(String key, String value) throws UnmodifiableException
    {
        if (key == null)
        {
            throw new IllegalArgumentException(NULL_KEY_MSG);
        }

        if (!isModifiable(key))
        {
            throw new UnmodifiableException("Preference \"" + key + "\" is not a modifiable preference attribute.");
        }

        PreferenceComposite pref = (PreferenceComposite) get(key);

        if (pref == null)
        {
            pref = new PortletPreference();
        }

        pref.setValueAt(0, value);

    }

    /**
     * @see javax.portlet.PortletPreferences#setValues(java.lang.String, java.lang.String[])
     */
    public void setValues(String key, String[] values) throws UnmodifiableException
    {
        if (key == null)
        {
            throw new IllegalArgumentException(NULL_KEY_MSG);
        }

        if (!isModifiable(key))
        {
            throw new UnmodifiableException("Preference \"" + key + "\" is not a modifiable preference attribute.");
        }

        PreferenceComposite pref = (PreferenceComposite) get(key);

        if (pref == null)
        {
            pref = new PortletPreference();
        }

        pref.setValues(Arrays.asList(values));

    }

    /**
     * @see javax.portlet.PortletPreferences#getNames()
     */
    public Enumeration getNames()
    {
        Set dftNames = defaultPreferences.getPreferenceNames();
        Set prefNames = super.getPreferenceNames();
        prefNames.addAll(dftNames);

        return Collections.enumeration(prefNames);
    }

    /**
     * @see javax.portlet.PortletPreferences#reset(java.lang.String)
     */
    public void reset(String key) throws UnmodifiableException
    {
        if (key == null)
        {
            throw new IllegalArgumentException(NULL_KEY_MSG);
        }

        if (!isModifiable(key))
        {
            throw new UnmodifiableException("Preference \"" + key + "\" is not a modifiable preference attribute.");
        }

        // This will automatically cause a fall back to stored defaults
        remove(key);

    }

    /**
     * @see javax.portlet.PortletPreferences#store()
     */
    public void store() throws IOException, ValidatorException
    {
        // TODO Auto-generated method stub

    }

}
