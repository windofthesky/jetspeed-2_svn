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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.om.common.MutableDescription;

import org.apache.jetspeed.om.common.preference.PreferenceComposite;
import org.apache.jetspeed.om.impl.DescriptionImpl;
import org.apache.jetspeed.om.impl.DescriptionSetImpl;
import org.apache.jetspeed.om.impl.PreferenceDescriptionImpl;
import org.apache.jetspeed.util.HashCodeBuilder;
import org.apache.pluto.om.common.Description;


/**
 * <p>
 * AbstractPreference
 * </p>
 * 
 * 
 * @
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $ $
 *
 */
public abstract class AbstractPreference implements PreferenceComposite, Cloneable
{
    protected String ojbConcreteClass = AbstractPreference.class.getName();
    
    private static final Log log = LogFactory.getLog(AbstractPreference.class);

    protected String name;

    protected boolean readOnly;

    protected int id;

    protected long parentId;

    /** a collection of <code>PreferenceValueObjects</code>
         * that can be persisted in a unique fashion.
         */
    protected List values;

    /** Localized Descriptions */
    protected Collection descriptions;
    protected DescriptionSetImpl descCollWrapper = new DescriptionSetImpl(DescriptionImpl.TYPE_PREFERENCE);

    /** The type of preference this is either the portlet default or user defined */
    protected String type;

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
        if (values != null)
        {
            log.debug("Number of values for prefernece " + name + ", " + values.size());
            return PreferenceValueImpl.convertValueObjectsToStrings(values).iterator();
        }
        else
        {
            log.debug("No values defined for preference " + name);
            return null;
        }

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
         * @see org.apache.pluto.om.common.PreferenceCtrl#setValues(java.util.List)
         */
    public void setValues(List values)
    {
        log.debug("setValues(List) was invoked.");
        System.out.println("Values List " + values);
        if (values == null)
        {
            this.values = null;
            // nothing more to do
            return;
        }

        if (values instanceof List)
        {
            this.values = (List) values;
        }
        else
        {
            this.values = new ArrayList(values);
        }

        if (this.values != null && this.values.size() > 0)
        {
            if (!(this.values.get(0) instanceof PreferenceValueImpl))
            {
                this.values = null;
                throw new ClassCastException("setValues() can only hold PreferemceValueImpl objects");
            }
        }

    }

    public void setValues(String[] stringValues)
    {
        log.debug("setValues(String[]) was invoked.");
        if (this.values == null)
        {
            this.values = new ArrayList();
        }

        //values.clear();
        PreferenceValueImpl.convertStringsToValueObjects(Arrays.asList(stringValues), this.values);
    }

    /**
         * @see org.apache.pluto.om.common.PreferenceCtrl#setDescription(java.lang.String)
         */
    public void setDescription(String description)
    {
        // TODO: Is this still needed as we are using localized text???
       // addDescription(Jetspeed.getDefaultLocale(), description);
	   addDescription(Locale.getDefault(), description);
    }

    /**
         * @see org.apache.pluto.om.common.PreferenceCtrl#setModifiable(boolean)
         */
    public void setReadOnly(boolean readOnly)
    {
        this.readOnly = readOnly;
    }

    /**
         * @see java.lang.Object#equals(java.lang.Object)
         */
    public boolean equals(Object obj)
    {
        if (obj != null && obj.getClass().equals(getClass()))
        {
            AbstractPreference pref = (AbstractPreference) obj;
            boolean sameParent = (pref.parentId == parentId);
            boolean sameName = (name != null && pref.getName() != null && name.equals(pref.getName()));
            return sameParent && sameName;

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
            descriptions = new ArrayList();
        }
        descCollWrapper.setInnerCollection(descriptions);
        try
        {
            MutableDescription descObj = new PreferenceDescriptionImpl();
                
            descObj.setLocale(locale);
            descObj.setDescription(description);
            descCollWrapper.addDescription(descObj);
        }
        catch (Exception e)
        {
            String msg = "Unable to instantiate Description implementor, " + e.toString();
            log.error(msg, e);
            throw new IllegalStateException(msg);
        }
    }

    /**
         * @see org.apache.jetspeed.om.common.PreferenceComposite#getDescription(java.util.Locale)
         */
    public Description getDescription(Locale locale)
    {
        if (descriptions != null)
        {
            descCollWrapper.setInnerCollection(descriptions);
            return descCollWrapper.get(locale);
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
        if (values != null)
        {
            return (String[]) PreferenceValueImpl.convertValueObjectsToStrings(values).toArray(new String[values.size()]);
        }

        return null;
    }

    /**
         * @see org.apache.jetspeed.om.common.PreferenceComposite#setValueAt(int, java.lang.String)
         */
    public void setValueAt(int index, String value)
    {
        log.debug("setValueAt(int, String) was invoked.");
        if (values == null)
        {
            values = new ArrayList();
        }
        values.set(index, new PreferenceValueImpl(value));
    }

    public void addValue(String value)
    {
        if (values == null)
        {
            values = new ArrayList();
        }

        values.add(new PreferenceValueImpl(value));
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

    protected List getStringValues()
    {
        if (values != null)
        {
            return PreferenceValueImpl.convertValueObjectsToStrings(this.values);
        }

        return null;

    }

    /**
     * @see java.lang.Object#clone()
     */
    public Object clone() 
    {
        try
        {
            AbstractPreference clone = (AbstractPreference) getClass().newInstance();
            clone.name = name;
            clone.ojbConcreteClass= getClass().getName();
            clone.id = id;
            clone.descriptions = descriptions;
            clone.descCollWrapper = descCollWrapper;
            clone.parentId = parentId;
            clone.readOnly = readOnly;
            clone.type = type;
            clone.values = values;
            return clone;
        }
        catch (Exception e)
        {
          throw new  IllegalStateException("Unable to clone this preference: "+e.toString());
        }
       
    }

}
