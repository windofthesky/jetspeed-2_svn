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

import java.util.HashSet;
import java.util.Set;

import org.apache.pluto.om.common.Preference;

/**
 * UserPreferencesImpl
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
public class UserPreferencesImpl extends PreferenceSetImpl implements PreferenceSetComposite
{
    private static final String NULL_KEY_MSG = "The preference \"key\" argument cannot be null";

    private PreferenceSetComposite defaultPreferences;
    /**
     * Creates an instance of UserPreferencesImpl using the
     * the PreferenceSet from the registry that matches with the portlet
     * this UserPreferencesImpl is associated with.   The PreferenceSet
     * IS NEVER modified by this UserPreferencesImpl, it is just used
     * as guide for a users PortletPreferences.
     * 
     * @param prefSet PreferenceSet from the registry that will be used as a
     * base for the PortletPreferenceImpl.
     */
    public UserPreferencesImpl(PreferenceSetComposite defaultPreferences)
    {
        this();
        this.defaultPreferences = defaultPreferences;
    }

    public UserPreferencesImpl()
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
     *  
     * <p>
     * get
     * </p>
     * 
     * @see org.apache.pluto.om.common.PreferenceSet#get(java.lang.String)
     * @param name
     * @return
     */
    public Preference get(String name)
    {

        Preference pref = super.get(name);

        // Use default
        if (pref == null)
        {
            pref = defaultPreferences.get(name);
        }

        return pref;
    }

    /** 
     * <p>
     * getPreferenceNames
     * </p>
     * 
     * @see org.apache.jetspeed.om.common.preference.PreferenceSetComposite#getPreferenceNames()
     * @return
     */
    public Set getNames()
    {
        Set names = super.getNames();
      
        names.addAll(defaultPreferences.getNames());

        return names;
    }

}
