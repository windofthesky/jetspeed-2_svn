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

package org.apache.jetspeed.om.page.psml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Property;

/**
 * @version $Id$
 */
public class FragmentImpl extends AbstractBaseElement implements Fragment, java.io.Serializable
{

    private String type = null;

    private String state = null;

    private String decorator = null;

    private String skin = null;

    private List fragments = new Vector();

    private List properties = new Vector();
    
    private String name;

    public FragmentImpl()
    {}

    public String getType()
    {
        return this.type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getState()
    {
        return this.state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public String getDecorator()
    {
        return this.decorator;
    }

    public void setDecorator(String decoratorName)
    {
        this.decorator = decoratorName;
    }

    public String getSkin()
    {
        return this.skin;
    }

    public void setSkin(String skin)
    {
        this.skin = skin;
    }

    public boolean isReference()
    {
        return false;
    }

    public List getFragments()
    {
        return this.fragments;
    }

    public List getLayoutProperties()
    {
        List layouts = new ArrayList();
        Iterator i = this.properties.iterator();

        while(i.hasNext())
        {
            Property p = (Property)i.next();
            if (!layouts.contains(p.getLayout()))
            {
                layouts.add(p.getLayout());
            }
        }

        return layouts;
    }

    public List getProperties(String layoutName)
    {
        List props = new ArrayList();
        Iterator i = this.properties.iterator();

        if (layoutName == null)
        {
            layoutName = "";
        }

        while(i.hasNext())
        {
            Property p = (Property)i.next();
            if (layoutName.equals(p.getLayout()))
            {
                props.add(p);
            }
        }

        return props;
    }
    
    public String getPropertyValue(String layout, String propName)
    {
        Iterator itr = getProperties(layout).iterator();
        while(itr.hasNext())
        {
            Property aProp = (Property) itr.next();
            if(aProp.getName().equals(propName))
            {
                return aProp.getValue();
            }
        }
        
        return null;
    }
    
    public void setPropertyValue(String layout, String propName, String value)
    {
        Iterator itr = getProperties(layout).iterator();
        while(itr.hasNext())
        {
            Property aProp = (Property) itr.next();
            if(aProp.getName().equals(propName))
            {
                aProp.setValue(value);
                return;
            }
        }
        
        PropertyImpl newProp = new PropertyImpl();
        newProp.setLayout(layout);
        newProp.setName(propName);
        newProp.setValue(value);
        addProperty(newProp);
    }

    public void addProperty(Property p)
    {
        this.properties.add(p);
    }

    public void removeProperty(Property p)
    {
        Iterator i = this.properties.iterator();

        while(i.hasNext())
        {
            Property p2 = (Property)i.next();

            if (p2.equals(p))
            {
                i.remove();
            }
        }
    }

    public void clearProperties(String layoutName)
    {
        if (layoutName == null)
        {
            this.properties.clear();
            return;
        }

        Iterator i = this.properties.iterator();

        while(i.hasNext())
        {
            Property p = (Property)i.next();

            if (layoutName.equals(p.getLayout()))
            {
                i.remove();
            }
        }
    }

    public Vector getProperties()
    {
        return (Vector)this.properties;
    }

    public void setProperties(Vector props)
    {
        this.properties=props;
    }

    public void setFragments(List fragments)
    {
        this.fragments = fragments;
    }

    public Object clone()
        throws java.lang.CloneNotSupportedException
    {
        Object cloned = super.clone();

        // TBD: copy the properties and fragment structures

        return cloned;

    }   // clone

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
        if(obj != null && obj instanceof Fragment)
        {
            Fragment aFragment = (Fragment) obj;
            return getId().equals(aFragment.getId());
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
        if(getId() != null)
        {
            return (Fragment.class.getName()+":"+getId()).hashCode();
        }
        else
        {
            return super.hashCode();
        }
    }
    /**
     * <p>
     * getName
     * </p>
     *
     * @see org.apache.jetspeed.om.page.Fragment#getName()
     * @return
     */
    public String getName()
    {
        return name;
    }
    /**
     * <p>
     * setName
     * </p>
     *
     * @see org.apache.jetspeed.om.page.Fragment#setName(java.lang.String)
     * @param name
     */
    public void setName( String name )
    {
       this.name = name;

    }
}
