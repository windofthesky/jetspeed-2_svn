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
package org.apache.jetspeed.om.registry.base;

import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;


import org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl;
import org.apache.jetspeed.om.registry.CachedParameter;
import org.apache.jetspeed.om.registry.Category;
import org.apache.jetspeed.om.registry.ContentURL;
import org.apache.jetspeed.om.registry.MetaInfo;
import org.apache.jetspeed.om.registry.Parameter;
import org.apache.jetspeed.om.registry.PortletEntry;
import org.apache.jetspeed.om.registry.PortletIterator;
import org.apache.jetspeed.services.registry.JetspeedRegistry;
import org.apache.jetspeed.services.registry.RegistryService;

/**
 * Default bean like implementation of the PortletEntry interface
 * suitable for serialization with Castor
 *
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
 * @version $Id$
 * @deprecated use org.apache.jetspeed.om.common.base.PortletEntityImpl
 */
public class BasePortletEntry extends BasePortletInfoEntry
   implements PortletEntry, java.io.Serializable
{

    private PortletApplicationDefinitionImpl application = null;
    
    private Collection descriptions = null;

    /** @deprecated */
    private String parent = null;

    /** @deprecated */
    private ContentURL url = new BaseContentURL();

    /** @deprecated */
    protected Vector categories = new Vector();

    /** @deprecated */
    private String type = PortletEntry.TYPE_ABSTRACT;

    /** @deprecated */
    private boolean isRef = false;

    /** Holds value of property portletId. */
    private long portletId;
    
    /**
     * Implements the equals operation so that 2 elements are equal if
     * all their member values are equal.
     */
    public boolean equals(Object object)
    {
        if (object==null)
        {
            return false;
        }

        BasePortletEntry obj = (BasePortletEntry)object;

        if (application!=obj.getApplication())
        {
            return false;
        }

        if (parent!=null)
        {
            if (!parent.equals(obj.getParent()))
            {
                return false;
            }
        }
        else
        {
            if (obj.getParent()!=null)
            {
                return false;
            }
        }

        if (type!=null)
        {
            if (!type.equals(obj.getType()))
            {
                return false;
            }
        }
        else
        {
            if (obj.getType()!=null)
            {
                return false;
            }
        }

        if (url!=null)
        {
            if (!url.equals(obj.getContentURL()))
            {
                return false;
            }
        }
        else
        {
            if (obj.getContentURL()!=null)
            {
                return false;
            }
        }

        Iterator i = categories.iterator();
        Iterator i2 = obj.getCategories().iterator();
        while(i.hasNext())
        {
            BaseCategory c1 = (BaseCategory)i.next();
            BaseCategory c2 = null;

            if (i2.hasNext())
            {
                c2 = (BaseCategory)i2.next();
            }
            else
            {
                return false;
            }

            if (!c1.equals(c2))
            {
                return false;
            }
        }

        if (i2.hasNext())
        {
            return false;
        }

        return super.equals(object);
    }

    /** @return the URL associated with this portlet or null */
    public String getURL()
    {
        return this.url.getURL();
    }

    /**
     * Sets the URL for this PortletEntry
     * @param url the new PortletEntry URL
     */
    public void setURL( String url )
    {
        this.url.setURL(url);
    }

    public boolean isCachedOnURL()
    {
        return url.isCacheKey();
    }

    public void setCachedOnURL(boolean cache)
    {
        url.setCachedOnURL(cache);
    }

    public ContentURL getURLEntry()
    {
        return url;
    }

    /** @return the entry name from which this one is derived */
    public String getParent()
    {
        return this.parent;
    }

    /** @return the classname associated to this entry */
    public String getClassname()
    {
        if (isRef && (classname == null) )
        {
            return getParentEntry().getClassname();
        }

        return classname;
    }


    /**
     * Sets the ancestor for this PortletEntry.
     * @param parent the new ancestor entry name. This name should
     * be defined in the system registry
     */
    public void setParent( String parent )
    {
        this.parent = parent;
    }

    /** @return true is this entry is only accessible by the
      * portal administrators.
      */
    public boolean isAdmin()
    {
        if (getSecurity()!=null)
        {
            return "admin".equals(getSecurity().getRole());
        }

        return false;
    }


    /** @return the type of this entry */
    public String getType()
    {
        return this.type;
    }

    /** Sets the type of this entry. The type specifies whether it is
     *  abstract, instance or ref
     *
     *  @param type the new type for the PortletEntry
     */
    public void setType( String type )
    {
        this.isRef = PortletEntry.TYPE_REF.equals(type);
        this.type = type;
    }


    public String getTitle()
    {
        String title = super.getTitle();
        if (title != null)
            return title;
        if (isRef)
        {
           return getParentEntry().getTitle();
        }
        return null;
    }

    public String getDescription()
    {
        String desc = super.getDescription();
        if (desc != null)
            return desc;

        if (isRef)
        {
            return getParentEntry().getDescription();
        }
        return null;
    }

    /** Looks up in the Registry the parent entry for this real entry */
    public PortletEntry getParentEntry()
    {
        PortletEntry parent = null;
        parent = (PortletEntry)JetspeedRegistry.getEntry( RegistryService.PORTLET, getParent() );
        if (parent == null)
        {
            parent = new BasePortletEntry();
            parent.setName(getParent());
//            parent.setType(PortletEntry.TYPE_ABSTRACT);
        }
        return parent;
    }

    public MetaInfo getMetaInfo()
    {
        MetaInfo meta = super.getMetaInfo();
        if (meta == null)
        {
            return getParentEntry().getMetaInfo();
        }
        return meta;
    }

    /** @return an enumeration of this entry parameter names */
    public Iterator getParameterNames()
    {
        if (isRef)
        {
            Hashtable hash = new Hashtable();
            Iterator i = super.getParameterNames();
            while(i.hasNext())
            {
                hash.put(i.next(),"1");
            }
            i = getParentEntry().getParameterNames();
            while(i.hasNext())
            {
                hash.put(i.next(),"1");
            }

            return hash.keySet().iterator();
        }

        return super.getParameterNames();
    }

    /** Search for a named parameter and return the associated
     *  parameter object. The search is case sensitive.
     *
     *  @return the parameter object for a given parameter name
     *  @param name the parameter name to look for
     */
    public Parameter getParameter( String name )
    {
        Parameter p = super.getParameter(name);
        if (isRef && p == null)
        {
            return getParentEntry().getParameter(name);
        }
        return p;
    }

    public CachedParameter getCachedParameter( String name )
    {
        Parameter p = getParameter(name);
        return (CachedParameter)p;
    }

    /** Returns a map of parameter values keyed on the parameter names
     *  @return the parameter values map
     */
    public Map getParameterMap()
    {
        Hashtable params = (Hashtable)super.getParameterMap();

        if (isRef)
        {
            Map map = getParentEntry().getParameterMap();
            map.putAll(params);
            return map;
        }

        return params;
    }

    /**
     * Returns a list of the supported media type names
     *
     * @return an iterator on the supported media type names
     */
    public Iterator listMediaTypes()
    {
        if (isRef)
        {
            Map types = new HashMap();

            Iterator i = super.listMediaTypes();
            while(i.hasNext())
            {
                types.put(i.next(),"1");
            }

            i = getParentEntry().listMediaTypes();
            while(i.hasNext())
            {
                types.put(i.next(),"1");
            }

            return types.keySet().iterator();
        }

        return super.listMediaTypes();
    }

    /**
     * Test if a given media type is supported by this entry.
     * The test is done by a case sensitive name comparison
     *
     * @param name the media type name to test for.
     * @return true is the media type is supported false otherwise
     */
    public boolean hasMediaType(String name)
    {
        if (isRef)
        {
            return super.hasMediaType(name) || getParentEntry().hasMediaType(name);
        }

        return super.hasMediaType(name);
    }

    /** @return the URL associated with this portlet or null */
    public BaseContentURL getContentURL()
    {
        return (BaseContentURL)this.url;
    }

    /**
     * Sets the URL for this PortletEntry
     * @param url the new PortletEntry URL
     */
    public void setContentURL( BaseContentURL url )
    {
        this.url = url;
    }

    /*
     * Categories
     */
    public Vector getCategories()
    {
        return this.categories;
    }

    public void setCategories(Vector v)
    {
        this.categories = v;
    }

    /**
     * Returns a list of the supported media type names
     *
     * @return an iterator on the supported media type names
     */
    public Iterator listCategories()
    {
        return new PortletIterator(this, "getCategories");
    }

    /**
     * Test if a given category exists for this entry
     *
     * @param name the category name
     * @return true is the category exists in the default group
     */
    public boolean hasCategory(String name)
    {
        return hasCategory(name, PortletEntry.DEFAULT_GROUP);
    }

    /**
     * Test if a given category exists for this entry, in the specified group of categories.
     *
     * @param name the category name
     * @param group the category group
     * @return true is the category exists in the specified group
     */
    public boolean hasCategory(String name, String group)
    {
        Iterator it = listCategories();
        while (it.hasNext())
        {
            Category cat = (Category)it.next();
            if (cat.getName().equals(name) && cat.getGroup().equals(group))
                return true;
        }
        return false;
    }


    /**
     * Add a new category to this portlet entry in the default group.
     *
     * @param name the category name
     */
    public void addCategory(String name)
    {
        addCategory(name, PortletEntry.DEFAULT_GROUP);
    }

    /**
     * Add a new category to this portlet entry.
     *
     * @param name the category name
     * @param group the category group name
     */
    public void addCategory(String name, String group)
    {
        if (!hasCategory(name, group))
        {
            Category cat = new BaseCategory();
            cat.setName(name);
            cat.setGroup(group);
            categories.add(cat);
        }
    }

    /**
     * Remove a category from this portlet entry in the default group.
     *
     * @param name the category name
     */
    public void removeCategory(String name)
    {
        removeCategory(name, PortletEntry.DEFAULT_GROUP);
    }

    /**
     * Remove a category from this portlet entry in the specified group.
     *
     * @param name the media type name to remove.
     * @param group the category group name
     */
    public void removeCategory(String name, String group)
    {
        for (int ix = 0; ix < categories.size(); ix++)
        {
            Category cat = (Category)categories.elementAt(ix);
            if (cat.getName().equals(name) && cat.getGroup().equals(group))
            {
                categories.remove(ix);
                return;
            }
        }
    }
    
    /** 
     * @see org.apache.jetspeed.om.registry.PortletEntry#getApplication
     */
    public PortletApplicationDefinitionImpl getApplication()
    {
        return this.application;
    }
    
    /** 
     * @see org.apache.jetspeed.om.registry.PortletEntry#setApplication
     */
    public void setApplication(PortletApplicationDefinitionImpl application)
    {
        this.application = application;
    }
    
    /** 
     * @see org.apache.jetspeed.om.registry.PortletEntry#getDescriptions
     */
    public Collection getDescriptions()
    {
        return descriptions;
    }
    
    /** 
     * @see org.apache.jetspeed.om.registry.PortletEntry#setDescriptions
     */
    public void setDescriptions(Collection descriptions)
    {
        this.descriptions = descriptions;
    }
    
    /**
     * Getter for property portletId.
     * @return Value of property portletId.
     *
     */
    public long getPortletId()
    {
        return this.portletId;
    }    
    
    /**
     * Setter for property portletId.
     * @param portletId New value of property portletId.
     *
     */
    public void setPortletId(long portletId)
    {
        this.portletId = portletId;
    }
    
}


