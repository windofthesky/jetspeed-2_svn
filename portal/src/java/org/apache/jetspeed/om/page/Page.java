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
package org.apache.jetspeed.om.page;

/**
 * This interface represents a complete page document used by Jetspeed
 * to layout a user-customizable portal page.
 *
 * @version $Id$
 */
public interface Page extends BaseElement, java.io.Serializable, Cloneable
{

    /**
     * Returns the name of the default skin that applies to this
     * page. This name should reference an entry in the Skin
     * registry
     *
     * @return the page default skin name
     */
    public String getDefaultSkin();

    /**
     * Modifies the default skin for this page.
     * This new skin must reference an entry in the Skin
     * registry.
     * Additionnally, replacing the default skin will not affect any
     * children fragments with their own specific skins
     *
     * @param skinName the name of the new skin for the page
     */
    public void setDefaultSkin(String skinName);

    /**
     * Returns the name of the default decorator that applies in this page
     * to fragments of the specified type
     *
     * @param fragmentType the type of fragment considered
     * @return the decorator name for the selected type
     */
    public String getDefaultDecorator(String fragmentType);

    /**
     * Modifies the default decorator for the specified fragment type.
     *
     * @param decoratorName the name of the new decorator for the type
     * @param fragmentType the type of fragment considered
     */
    public void setDefaultDecorator(String decoratorName, String fragmentType);

    /**
     * Retrieves the top level fragment of this page. This Fragment should
     * never be null.
     *
     * @return the base Fragment object for this page.
     */
    public Fragment getRootFragment();

    /**
     * Sets the top level fragment of this page. This Fragment should
     * never be null.
     *
     * @return the base Fragment object for this page.
     */    
    public void setRootFragment(Fragment fragment);

    /**
     * Retrieves the fragment contained within this page, with the
     * specified Id.
     *
     * @param id the fragment id to look for
     * @return the found Fragment object or null if not found
     */
    public Fragment getFragmentById(String id);

    /**
     * Create a clone of this object
     */
    public Object clone()
        throws java.lang.CloneNotSupportedException;
}

