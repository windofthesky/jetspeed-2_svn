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
 *     "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache" or
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

package org.apache.jetspeed.om.page;

import java.util.Map;
import java.util.List;
import java.util.Set;

/**
 * <p>A <code>Fragment</code> is the basic element handled by the aggregation
 * engine to compose the final portal page. It represents a reserved screen
 * area whose layout is managed by a specified component.</p>
 * <p>The component that is responsible for the layout policy of the fragment
 * is defined by two properties:<p>
 * <ul>
 *   <li><b>type</b>: defines the general class of layout component, enabling
 *       the engine to retrieve its exact defintion from its component
 *       repository.
 *   </li>
 *   <li><b>name</b>: this is the exact name of the component. This name must
 *       be unique within a portal instance for a specific component type.
 *   </li>
 * </ul>
 * <p>In addition to specifying the component responsible for the layout,
 * the fragment also stores contextual information used for rendering:</p>
 * <p>Finally the fragment also holds layout and rendering properties that
 *   may be used by a parent fragment to layout all its inner fragments in
 *   an appropriate fashion. These properties are always defined for a
 *   specific named component.</p>
 *
 * @version $Id$
 */
public interface Fragment extends Cloneable, java.io.Serializable
{
    /**
     * A fragment of type PORTLET is considered to be a compliant portlet
     * in the sense of the JSR 168.
     */
     public String PORTLET = "portlet";

    /**
     * A fragment of type LAYOUT is a specific JSR 168 compliant portlet
     * that knows how to layout a Page and depends on the Jetspeed
     * layout service.
     */
     public String LAYOUT = "layout";

    /**
     * Returns a unique id number for this Fragment. This ID unique
     * number is guaranteed to be unique within the portal and can be
     * used as a unique key for this Fragment.
     *
     * @return the id of the fragment as a String.
     * This id may not be null.
     */
    public String getId();

    /**
     * Sets a new unique id for this Fragment. This id cannot be null and
     * must be unique within the portal.
     *
     * @param id the new id for this Fragment.
     */
    public void setId(String id);

    /**
     * Returns the name of an ACL that governs access to the Fragment
     * and some of the navigation/editing functionalities related to this
     * fragment.
     * The ACL should be a valid reference to a security policy in the
     * portal security repository. If it's invalid or null, the portal must
     * default to the parent fragment ACL, the page default ACL or the system
     * default.
     *
     * @return the name of an ACL in the security repository.
     */
    public String getAcl();

    /**
     * Returns the name of an ACL that governs access to the Fragment
     * and some of the navigation/editing functionalities related to this
     * fragment.
     * The ACL should be a valid reference to a security policy in the
     * portal security repository. If it's invalid or null, the portal must
     * default to the parent fragment ACL, the page default ACL or the system
     * default.
     *
     * @return the name of an ACL in the security repository.
     */
    public void setAcl(String aclName);

    /**
     * Returns the administrative name of this fragment. This name should map
     * to a component name in the component repository defined by the type
     * attribute.
     * If the name is not mapped to any component, the fragment is discarded
     * from the rendering process, as well as any inner fragment.
     *
     * @return the administrative name
     */
    public String getName();

    /**
     * Binds an administrative name to this fragment
     *
     * @param name the administrative name
     */
    public void setName(String name);

    /**
     * Defines the title for this fragment
     *
     * @param title the new fragment title
     */
    public void setTitle(String title);

    /**
     * Returns the title of this fragment
     */
    public String getTitle();

    /**
     * Returns the type of the class bound to this fragment
     */
    public String getType();

    /**
     * Binds a type to this fragment
     *
     * @param type the type
     */
    public void setType(String type);

    /**
     * Returns the name of the skin associated to this fragment
     */
    public String getSkin();

    /**
     * Defines the skin for this fragment. This skin should be
     * known by the portal.
     *
     * @param skinName the name of the new skin applied to this fragment
     */
    public void setSkin(String skinName);

    /**
     * Returns the name of the decorator bound to this fragment
     */
    public String getDecorator();

    /**
     * Defines the decorator for this fragment. This decorator should be
     * known by the portal.
     *
     * @param decoratorName the name of the decorator applied to this fragment
     */
    public void setDecorator(String decoratorName);

    /**
     * Returns the display state of this fragment. This state may have the
     * following values:
     * "Normal","Minimized","Maximized","Hidden".
     */
    public String getState();

    /**
     * Sets the display state of this fragment.
     * Valid states are: "Normal","Minimzed","Maximized","Hidden"
     *
     * @param decoratorName the name of the decorator applied to this fragment
     */
    public void setState(String state);

    /**
     * Returns all fragments used in this node. This may be
     * a page fragment or even directly a portlet fragment
     *
     * @return a collection containing Fragment objects
     */
    public List getFragments();

    /**
     * Returns all properties describing this fragment. Only the
     * implementation of the "classname" knows how to handle the
     * properties
     *
     * @return a collection containing Property objects
     */
    public Set getLayoutProperties();

    /**
     * Returns all properties describing this fragment. Only the
     * implementation of the "classname" knows how to handle the
     * properties
     *
     * @return a collection containing Property objects
     */
    public Map getProperties(String layoutName);

    /**
     * Test if this fragment is actually a reference to an external fragment.
     *
     * @return true is this element is a reference
     */
    public boolean isReference();

    /**
     * Creates a clone of this object
     */
    public Object clone()
        throws java.lang.CloneNotSupportedException;
}