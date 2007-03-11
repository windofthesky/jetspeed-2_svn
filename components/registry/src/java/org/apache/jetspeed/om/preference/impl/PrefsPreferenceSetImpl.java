/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.portlet.PreferencesValidator;

import org.apache.jetspeed.om.common.preference.PreferenceSetComposite;
import org.apache.jetspeed.om.common.preference.PreferencesValidatorFactory;
import org.apache.pluto.om.common.Preference;

/**
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * 
 *  
 */
public class PrefsPreferenceSetImpl implements PreferenceSetComposite
{
    protected Preferences prefsRootNode;
    protected PreferenceSetComposite defaults;
    protected PreferencesValidatorFactory validatorFactory;

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
     * @param portletEntity
     *                  PortletEntity for which to build the PreferenceSet for.
     * @param validatorFactory
     *                  Factory for providing access to a PreferencesValidator instance                
     * @throws BackingStoreException
     *                   if an error is encountered while accessing the Preferences
     *                   backing store.
     */
    public PrefsPreferenceSetImpl( Preferences prefsRootNode, PreferencesValidatorFactory validatorFactory ) throws BackingStoreException
    {
        this(prefsRootNode);
        this.validatorFactory = validatorFactory;
    }

    public PrefsPreferenceSetImpl( Preferences prefsRootNode,  PreferenceSetComposite defaults) throws BackingStoreException
    {
        this(prefsRootNode);        
        this.defaults = defaults;
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
            if(defaults != null)
            {
                Set names = defaults.getNames();                    
                names.addAll(new HashSet(Arrays.asList(prefsRootNode.childrenNames())));
                return names;                
            }
            else
            {
              return new HashSet(Arrays.asList(prefsRootNode.childrenNames()));
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
            Preference pref = null;
            if (prefsRootNode.nodeExists(key))
            {
                pref = new PrefsPreference(prefsRootNode.node(key), key);
            }
            else if(defaults != null)
            {
                pref = defaults.get(key);
            }
            
            return pref;
        }
        catch (IllegalStateException ise)
        {
            // node has been removed
            return null;
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
        if ( validatorFactory != null )
        {
            return validatorFactory.getPreferencesValidator();
        }
        return null;
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
        	Preferences nodeToRemove = prefsRootNode.node(key);
        	
            if (nodeToRemove == null)
            	return null;
            PrefsPreference pref = new PrefsPreference(nodeToRemove, key);
            nodeToRemove.removeNode();
            return pref;
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
            int length = prefsRootNode.childrenNames().length;
            
            if(defaults != null)
            {
                length += defaults.size();
            }
                  
            return length;
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
        int beginSize = 0;
        int pointer;
        String[] childrenNames;
        protected PrefsPreference currentPref;

        protected PortletPrefsIterator()
        {
            super();
            try
            {
                childrenNames = prefsRootNode.childrenNames();
                if (childrenNames != null)
                	beginSize =  childrenNames.length;
                
                if(defaults != null)
                {
                    Vector v = new Vector();

                    Iterator itr = defaults.getNames().iterator();
                    while( itr.hasNext())
                    {
                        String name = (String) itr.next();
                        if(!arrayContains(childrenNames, name))
                        {
                        	v.add(name);
                        }
                    }
                    int j = v.size();
                    if (j>0)
                    {
                    	int i = childrenNames.length;
                        String[] tempArray = new String[j+i];
                        System.arraycopy(childrenNames, 0, tempArray, 0, i);
                        for (int x = 0; x < j; x++)
                            tempArray[i+x] = (String)v.get(x);
                        childrenNames = tempArray;
                        beginSize = i+j;
                    }
                }
                pointer = 0;
            }
            catch (IllegalStateException ise)
            {
                // node has been removed
                childrenNames = new String[0];
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
        	try
        	{
        		return pointer < beginSize;
        	}
        	catch (Exception e)
        	{
            	throw new ConcurrentModificationException("Underlying PreferenceSet has changed.");
        	}
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
			try
			{
	            currentPref = (PrefsPreference) get(childrenNames[pointer]);
	            pointer++;
	            return currentPref;
	    	}
	    	catch (Exception e)
	    	{
	        	throw new ConcurrentModificationException("Underlying PreferenceSet has changed.");
	    	}
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
            beginSize--;

        }
    }
    
    protected boolean arrayContains(String[] array, String value)
    {
        for(int i=0; i<array.length; i++)
        {
            if(array[i].equals(value))
            {
                return true;
            }
        }
        
        return false;
    }
   
}