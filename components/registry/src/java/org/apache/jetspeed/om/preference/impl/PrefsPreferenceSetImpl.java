/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.jetspeed.om.preference.impl;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.portlet.PreferencesValidator;

import org.apache.jetspeed.om.common.preference.PreferenceSetComposite;
import org.apache.pluto.om.common.Preference;

/**
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * 
 *  
 */
public class PrefsPreferenceSetImpl implements PreferenceSetComposite
{

    protected Preferences prefsRootNode;
    protected PreferencesValidator validator;

    /**
     * @param portletEntity
     *                  PortletEntity for which to build the PreferenceSet for.
     * @throws BackingStoreException
     *                   if an error is encountered while accessing the Preferences
     *                   backing store.
     */
    public PrefsPreferenceSetImpl( Preferences prefsRootNode ) throws BackingStoreException
    {
        super();
        this.prefsRootNode = prefsRootNode;

    }

    /**
     * <p>
     * getNames
     * </p>
     * 
     * @see org.apache.jetspeed.om.common.preference.PreferenceSetComposite#getNames()
     * @return
     */
    public Set getNames()
    {
        try
        {
            return new HashSet(Arrays.asList(prefsRootNode.childrenNames()));
        }
        catch (BackingStoreException e)
        {
            String msg = "Preference backing store failed: " + e.toString();
            IllegalStateException ise = new IllegalStateException(msg);
            ise.initCause(e);
            throw ise;
        }
    }

    /**
     * <p>
     * setPreferenceValidator
     * </p>
     * 
     * @see org.apache.jetspeed.om.common.preference.PreferenceSetComposite#setPreferenceValidator(javax.portlet.PreferencesValidator)
     * @param validator
     */
    public void setPreferenceValidator( PreferencesValidator validator )
    {
        this.validator = validator;

    }

    /**
     * <p>
     * get
     * </p>
     * 
     * @see org.apache.pluto.om.common.PreferenceSet#get(java.lang.String)
     * @param arg0
     * @return
     */
    public Preference get( String key )
    {
        try
        {
            if (prefsRootNode.nodeExists(key))
            {
                return new PrefsPreference(prefsRootNode.node(key), key);
            }
            else
            {
                return null;
            }
        }
        catch (BackingStoreException e)
        {
            String msg = "Preference backing store failed: " + e.toString();
            IllegalStateException ise = new IllegalStateException(msg);
            ise.initCause(e);
            throw ise;
        }
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
     * <p>
     * add
     * </p>
     * 
     * @see org.apache.pluto.om.common.PreferenceSetCtrl#add(java.lang.String,
     *          java.util.List)
     * @param name
     * @param values
     * @return
     */
    public Preference add( String name, List values )
    {
        Iterator valuesItr = values.iterator();

        PrefsPreference pref = new PrefsPreference(prefsRootNode.node(name), name);
        while (valuesItr.hasNext())
        {
            pref.addValue((String) valuesItr.next());
        }

        return pref;
    }

    /**
     * <p>
     * remove
     * </p>
     * 
     * @see org.apache.pluto.om.common.PreferenceSetCtrl#remove(org.apache.pluto.om.common.Preference)
     * @param arg0
     */
    public void remove( Preference pref )
    {
        remove(pref.getName());
    }

    /**
     * <p>
     * remove
     * </p>
     * 
     * @see org.apache.pluto.om.common.PreferenceSetCtrl#remove(java.lang.String)
     * @param arg0
     * @return
     */
    public Preference remove( String key )
    {
        try
        {
            if (prefsRootNode.nodeExists(key))
            {
                Preferences nodeToRemove = prefsRootNode.node(key);
                PrefsPreference pref = new PrefsPreference(nodeToRemove, key);
                nodeToRemove.removeNode();
                return pref;
            }
            else
            {
                return null;
            }
        }
        catch (BackingStoreException e)
        {
            String msg = "Preference backing store failed: " + e.toString();
            IllegalStateException ise = new IllegalStateException(msg);
            ise.initCause(e);
            throw ise;
        }
    }

    /**
     *  
     */
    public void flush() throws BackingStoreException
    {
        prefsRootNode.flush();
    }
    
    /**
     * 
     * <p>
     * clear
     * </p>
     *
     * @throws BackingStoreException
     */
    public void clear() throws BackingStoreException
    {
        prefsRootNode.removeNode();
    }

    /**
     * <p>
     * size
     * </p>
     * 
     * @see org.apache.jetspeed.om.common.preference.PreferenceSetComposite#size()
     * @return
     */
    public int size()
    {
        try
        {
            return prefsRootNode.childrenNames().length;
        }
        catch (IllegalStateException ise)
        {
            // node has been removed
            return 0;
        }
        catch (BackingStoreException e)
        {
            IllegalStateException ise = new IllegalStateException(e.toString());
            ise.initCause(e);
            throw ise;
        }
    }

    /**
     * <p>
     * iterator
     * </p>
     * 
     * @see org.apache.pluto.om.common.PreferenceSet#iterator()
     * @return
     */
    public Iterator iterator()
    {
		return new PortletPrefsIterator();
    }

    protected class PortletPrefsIterator implements Iterator
    {
        int beginSize;
        int pointer;
        String[] childrenNames;
        protected PrefsPreference currentPref;

        protected PortletPrefsIterator()
        {
            super();
            try
            {
                beginSize = size();
                childrenNames = prefsRootNode.childrenNames();
                pointer = 0;
            }
            catch (BackingStoreException e)
            {
                String msg = "Preference backing store failed: " + e.toString();
                IllegalStateException ise = new IllegalStateException(msg);
                ise.initCause(e);
                throw ise;
            }

        }

        /**
         * <p>
         * hasNext
         * </p>
         * 
         * @see java.util.Iterator#hasNext()
         * @return
         */
        public boolean hasNext()
        {
            if (beginSize != size())
            {
                throw new ConcurrentModificationException("Underlying PreferenceSet has changed.");
            }
            return pointer < beginSize;
        }

        /**
         * <p>
         * next
         * </p>
         * 
         * @see java.util.Iterator#next()
         * @return
         */
        public Object next()
        {
            if (beginSize != size())
            {
                throw new ConcurrentModificationException("Underlying PreferenceSet has changed.");
            }

            currentPref = (PrefsPreference) get(childrenNames[pointer]);
            pointer++;
            return currentPref;
        }

        /**
         * <p>
         * remove
         * </p>
         * 
         * @see java.util.Iterator#remove()
         *  
         */
        public void remove()
        {
            if (currentPref == null)
            {
                throw new IllegalStateException(" next() must be called at least once before invoking remove().");
            }

            PrefsPreferenceSetImpl.this.remove(currentPref);
            beginSize = size();

        }
    }
   
}