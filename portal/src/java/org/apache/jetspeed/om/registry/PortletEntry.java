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
package org.apache.jetspeed.om.registry;

import java.util.Collection;
import java.util.Iterator;

import org.apache.jetspeed.om.common.portlet.BasePortletApplicationDefinition;

/**
 * This entry describes all the properties that should be present in
 * a RegistryEntry describing a Portlet.
 * <p>Each PortletEntry must have a type, which may be:
 * <dl>
 *   <dt>abstract</dt><dd>The entry description is unsuitable for instanciating
 *   a Portlet</dd>
 *   <dt>instance</dt><dd>This entry may be used to create a Portlet and does not
 *   depend on any other portlet entries</dd>
 *   <dt>ref</dt><dd>This entry may be used to instanciate Portlets but depends on
 *   another PortletEntry definition whose registry name can be retrieved by getParent()
 *   </dd>
 * </dl></p>
 *
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
 * @version $Id$
 * @deprecated use org.apache.jetspeed.om.common.PortletEntity
 */
public interface PortletEntry extends PortletInfoEntry
{

    public static final String TYPE_REF        = "ref";
    public static final String TYPE_INSTANCE   = "instance";
    public static final String TYPE_ABSTRACT   = "abstract";

    public final static String DEFAULT_GROUP = "Jetspeed";
    public final static String DEFAULT_CATEGORY_REF = "General";
    public final static String DEFAULT_CATEGORY_ABSTRACT = "Abstract";

    /**
      * helper to get an instance of a cached parameter.
      *
      * @param name The parameter name.
      * @return The cached parameter entry.
      * @deprecated
      */
    public CachedParameter getCachedParameter( String name );


    /**
     * Returns a list of the categories
     *
     * @return an iterator on the categories
     * @deprecated
     */
    public Iterator listCategories();

    /**
     * Test if a given category exists for this entry
     *
     * @param name the category name
     * @return true is the category exists in the default group
     * @deprecated
     */
    public boolean hasCategory(String name);

    /**
     * Test if a given category exists for this entry, in the specified group of categories.
     *
     * @param name the category name
     * @param group the category group
     * @return true is the category exists in the specified group
     * @deprecated
     */
    public boolean hasCategory(String name, String group);

    /**
     * Add a new category to this portlet entry in the default group.
     *
     * @param name the category name
     * @deprecated
     */
    public void addCategory(String name);

    /**
     * Add a new category to this portlet entry.
     *
     * @param name the category name
     * @param group the category group name
     * @deprecated
     */
    public void addCategory(String name, String group);

    /**
     * Remove a category from this portlet entry in the default group.
     *
     * @param name the category name
     * @deprecated
     */
    public void removeCategory(String name);

    /**
     * Remove a category from this portlet entry in the specified group.
     *
     * @param name the media type name to remove.
     * @param group the category group name
     * @deprecated
     */
    public void removeCategory(String name, String group);

    /** Getter for property name.
     * @return Value of property name.
     *
     */
    public String getName();    

    /** Setter for property name.
     * @param name New value of property name.
     *
     */
    public void setName(String name);
    
    /** Getter for property application.
     * @return Value of property application.
     *
     */
    public BasePortletApplicationDefinition getApplication();
    
    /**
     * Setter for property application.
     * @param application New value of property application.
     *
     */
    public void setApplication(BasePortletApplicationDefinition application);
    
    /**
     * Getter for property descriptions.
     * @return Value of property descriptions.
     *
     */
    public Collection getDescriptions();
    
    /**
     * Setter for property descriptions.
     * @param descriptions New value of property descriptions.
     *
     */
    public void setDescriptions(Collection descriptions);
    
}

