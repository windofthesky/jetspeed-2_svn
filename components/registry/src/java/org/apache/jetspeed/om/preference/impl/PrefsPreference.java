/*
 * Created on Jun 11, 2004
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.om.preference.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.MutablePortletEntity;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.common.preference.PreferenceComposite;
import org.apache.jetspeed.om.impl.PreferenceDescriptionImpl;
import org.apache.pluto.om.common.Description;

/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
public class PrefsPreference implements PreferenceComposite
{
    protected static final String VALUES_PATH = "values";
  
    public static final String PORTLET_PREFERENCES_ROOT = "preferences";
    protected static final String LOCALE_TOKEN = "_";
    protected Preferences prefValueNode;
    protected Preferences prefNode;
    protected String name;
    public static final String[] DEFAULT_OPEN_NODES = new String[] {MutablePortletEntity.PORTLET_ENTITY_ROOT, PortletDefinitionComposite.PORTLETS_PREFS_ROOT};

    
    
    public PrefsPreference(Preferences prefNode, String name)
    {
        super();
        this.prefNode = prefNode;
        if(prefNode == null)
        {
        	throw new IllegalArgumentException("prefNode cannot be null for PrefsPreferences(Preference).");
        }
                
        this.name = name;
        if(this.name == null)
        {
            throw new IllegalArgumentException("Preference does requires a \"name\" property.");
        }
        
        this.prefValueNode = prefNode.node(VALUES_PATH);
    }
    
    public PrefsPreference(PortletDefinitionComposite portlet, String name)
    {
        this(createPrefenceNode(portlet).node(name), name);
    }

    /**
     * <p>
     * addDescription
     * </p>
     * 
     * @see org.apache.jetspeed.om.common.preference.PreferenceComposite#addDescription(java.util.Locale,
     *          java.lang.String)
     * @param locale
     * @param Description
     */
    public void addDescription( Locale locale, String description )
    {
        String localePath = locale.toString();
        prefNode.node("description").put(localePath, description);
    }

    /**
     * <p>
     * getDescription
     * </p>
     * 
     * @see org.apache.jetspeed.om.common.preference.PreferenceComposite#getDescription(java.util.Locale)
     * @param locale
     * @return
     */
    public Description getDescription( Locale locale )
    {
        String localePath = locale.toString();
        String value = prefNode.node("description").get(localePath, null);
        PreferenceDescriptionImpl desc = new PreferenceDescriptionImpl();
        desc.setDescription(value);
        desc.setLocale(locale);
        desc.setLanguage(locale.getLanguage());
        return desc;
    }

    /**
     * <p>
     * getValueAt
     * </p>
     * 
     * @see org.apache.jetspeed.om.common.preference.PreferenceComposite#getValueAt(int)
     * @param index
     * @return
     */
    public String getValueAt( int index )
    {
        return prefValueNode.get(String.valueOf(index), null);
    }

    /**
     * <p>
     * setValueAt
     * </p>
     * 
     * @see org.apache.jetspeed.om.common.preference.PreferenceComposite#setValueAt(int,
     *          java.lang.String)
     * @param index
     * @param value
     */
    public void setValueAt( int index, String value )
    {
        prefValueNode.put(String.valueOf(index), value);

    }

    /**
     * <p>
     * addValue
     * </p>
     * 
     * @see org.apache.jetspeed.om.common.preference.PreferenceComposite#addValue(java.lang.String)
     * @param value
     */
    public void addValue( String value )
    {
       String stringKey = null;
        try
        {
            String[] keys = prefValueNode.keys();
            if (keys.length > 0)
            {
                Arrays.sort(keys);
                stringKey = keys[keys.length - 1];
                int nextIndex = Integer.parseInt(stringKey) + 1;
                setValueAt(nextIndex, value);
            }
            else
            {
                setValueAt(0, value);
            }
        }
        catch (NumberFormatException e)
        {
            throw new IllegalArgumentException("Could not convert preference key "+stringKey+" to an int.");
        }
        catch (BackingStoreException e)
        {            
			String msg = "Preference backing store failed: "+e.toString();
            IllegalStateException ise = new IllegalStateException(msg);
            ise.initCause(e);
            throw ise;
        }
    }

    /**
     * <p>
     * getValueArray
     * </p>
     * 
     * @see org.apache.jetspeed.om.common.preference.PreferenceComposite#getValueArray()
     * @return
     */
    public String[] getValueArray()
    {
        try
        {
            String[] keys = prefValueNode.keys();
            if (keys.length > 0)
            {
                String[] values = new String[keys.length];
                Arrays.sort(keys);
                for (int i = 0; i < keys.length; i++)
                {
                    values[i] = prefValueNode.get(String.valueOf(i), null);
                }
                return values;
            }
            else
            {
                return new String[0];
            }
        }
        catch (BackingStoreException e)
        {
            String msg = "Preference backing store failed: "+e.toString();
            IllegalStateException ise = new IllegalStateException(msg);
            ise.initCause(e);
            throw ise;
        }
    }

    /**
     * <p>
     * setValues
     * </p>
     * 
     * @see org.apache.jetspeed.om.common.preference.PreferenceComposite#setValues(java.lang.String[])
     * @param stringValues
     */
    public void setValues( String[] stringValues )
    {
        try
        {
            if (stringValues != null)
            {
                prefValueNode.clear();
                for (int i = 0; i < stringValues.length; i++)
                {
                    setValueAt(i, stringValues[i]);
                }
            }
        }
        catch (BackingStoreException e)
        {
            String msg = "Preference backing store failed: "+e.toString();
            IllegalStateException ise = new IllegalStateException(msg);
            ise.initCause(e);
            throw ise;
        }
    }

    /**
     * <p>
     * getType
     * </p>
     * 
     * @see org.apache.jetspeed.om.common.preference.PreferenceComposite#getType()
     * @return
     */
    public String getType()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * <p>
     * setType
     * </p>
     * 
     * @see org.apache.jetspeed.om.common.preference.PreferenceComposite#setType(java.lang.String)
     * @param string
     */
    public void setType( String string )
    {
        // TODO Auto-generated method stub

    }

    /**
     * <p>
     * setName
     * </p>
     * 
     * @see org.apache.pluto.om.common.PreferenceCtrl#setName(java.lang.String)
     * @param arg0
     */
    public void setName( String name )
    {
        this.name = name;

    }

    /**
     * <p>
     * setValues
     * </p>
     * 
     * @see org.apache.pluto.om.common.PreferenceCtrl#setValues(java.util.List)
     * @param arg0
     */
    public void setValues( List arg0 )
    {
        if (arg0 != null)
        {
            setValues((String[]) arg0.toArray(new String[arg0.size()]));
        }

    }

    /**
     * <p>
     * setReadOnly
     * </p>
     * 
     * @see org.apache.pluto.om.common.PreferenceCtrl#setReadOnly(java.lang.String)
     * @param arg0
     */
    public void setReadOnly( String readOnly )
    {
        prefNode.put("read_only", readOnly);

    }
    
    public void setReadOnly( boolean readOnly )
    {
        if(readOnly)
        {
            prefNode.put("read_only", "true");
        }
        else
        {
            prefNode.put("read_only", "false");
        }

    }

    /**
     * <p>
     * getName
     * </p>
     * 
     * @see org.apache.pluto.om.common.Preference#getName()
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     * <p>
     * getValues
     * </p>
     * 
     * @see org.apache.pluto.om.common.Preference#getValues()
     * @return
     */
    public Iterator getValues()
    {
        return Arrays.asList(getValueArray()).iterator();
    }

    /**
     * <p>
     * isReadOnly
     * </p>
     * 
     * @see org.apache.pluto.om.common.Preference#isReadOnly()
     * @return
     */
    public boolean isReadOnly()
    {
        return Boolean.valueOf(prefNode.get("read_only", "false")).booleanValue();
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
        return getValueArray().length > 0;
    }

    protected Locale parseLocal( String localString )
    {
        StringTokenizer lcTk = new StringTokenizer(localString, LOCALE_TOKEN);
        String lang = null;
        String country = null;
        String variant = null;
        while (lcTk.hasMoreTokens())
        {
            if (lang == null)
            {
                lang = lcTk.nextToken();
            }
            else if (country == null)
            {
                country = lcTk.nextToken();
            }
            else if (variant == null)
            {
                variant = lcTk.nextToken();
            }
        }

        return new Locale(lang, country, variant);
    }
    
  
    /**
     * <p>
     * clone
     * </p>
     * 
     * @see java.lang.Object#clone()
     * @return @throws
     *              java.lang.CloneNotSupportedException
     */
    public String[] cloneValues()
    {
        synchronized (prefValueNode)
        {
            String[] currentValues = getValueArray();
            String[] clonedValues = new String[currentValues.length];

            System.arraycopy(currentValues, 0, clonedValues, 0, currentValues.length);
        }

        return cloneValues();
    }

    /**
     * <p>
     * equals
     * </p>
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     * @param obj
     * @return
     */
    public boolean equals( Object obj )
    {
        if (obj != null && obj instanceof PrefsPreference && name != null)
        {
            PrefsPreference pref = (PrefsPreference) obj;
            return pref.name != null && this.name.equals(pref.name);

        }
        else
        {
            return false;
        }

    }

    /**
     * <p>
     * hashCode
     * </p>
     * 
     * @see java.lang.Object#hashCode()
     * @return
     */
    public int hashCode()
    {        
        return (getClass().getName()+"::"+name).hashCode();
    }

    /**
     * <p>
     * toString
     * </p>
     * 
     * @see java.lang.Object#toString()
     * @return
     */
    public String toString()
    {
        return "Preference{"+name+"} values="+getValueArray().toString();
    }
    
    /**
     *
     */
    public void flush() throws BackingStoreException
    {
    	prefValueNode.flush();
    }
    /**
     * <p>
     * getDescriptions
     * </p>
     *
     * @see org.apache.jetspeed.om.common.preference.PreferenceComposite#getDescriptions()
     * @return
     */
    public Iterator getDescriptions()
    {
        try
        {
            Preferences descNode = prefNode.node("description");
           
            String[] keys = descNode.keys();
            ArrayList descs = new ArrayList(keys.length);
            for(int i=0; i < keys.length; i++)
            {
            	PreferenceDescriptionImpl desc = new PreferenceDescriptionImpl();
            	String localeKey = keys[i];
                desc.setDescription(descNode.get(localeKey, null));
            	Locale locale = parseLocal(localeKey);
                desc.setLocale(locale);
                desc.setLanguage(locale.getLanguage());
                descs.add(desc);
            }
            
            return descs.iterator();
        }
        catch (BackingStoreException e)
        {
            String msg = "Preference backing store failed: "+e.toString();
            IllegalStateException ise = new IllegalStateException(msg);
            ise.initCause(e);
            throw ise;
        }
    }
    
    public static Preferences createPrefenceNode(PortletDefinitionComposite portlet)
    {
        MutablePortletApplication app = (MutablePortletApplication) portlet.getPortletApplicationDefinition();
        if(app == null)
        {
            throw new IllegalArgumentException("createPrefencePath() requires a PortletDefinition whose Application is not null.");
        }
        String portletDefPrefPath = MutablePortletApplication.PREFS_ROOT + "/" + app.getName() + "/"
        + PortletDefinitionComposite.PORTLETS_PREFS_ROOT + "/" + portlet.getName() + "/"
        + PrefsPreference.PORTLET_PREFERENCES_ROOT;
        
        return Preferences.systemRoot().node(portletDefPrefPath);
    }
}