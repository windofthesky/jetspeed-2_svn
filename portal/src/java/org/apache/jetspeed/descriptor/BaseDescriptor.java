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
package org.apache.jetspeed.descriptor;

/**
 * The base class from which all descriptor beans are derived.
 *
 * Summit's XML configuration files are parse into descriptor beans
 * and the descriptor beans are processed to configure Summit.
 *
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @version $Id$
 */
public class BaseDescriptor
{
    /**
     * Display name to use for this descriptor.
     */
    private String name;

    /**
     * Id to use for this descriptor.
     */
    private String id;
    
    /**
     * Give object that have not been given an explicit unique id
     * one that will keep betwixt happy.
     */
    private static int uniqueId;
    
    /**
     * Sets the name attribute
     *
     * @param name the new name value
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Gets the name attribute
     *
     * @return the name attribute 
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the id attribute of the BaseDescriptor object
     *
     * @param id the new id value
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Gets the id attribute
     *
     * @return the id attribute
     */
    public String getId()
    {
        if (id == null)
        {
            id = Integer.toString(uniqueId++);
        }
        
        return id;
    }
    
    /**
     * Return a string suitable for display/debugging
     *
     * @return the name attribute as a default
     */
    public String toString()
    {
        return name;
    }

    /**
     * Whether the passed object is the same as this one. In this case
     * the id is the unique qualifier. So two objects are equal
     * if they have equal id's
     * @param o any object
     * @return true if o is the same as this object, false otherwise
     */
    public boolean equals(Object o)
    {
        if (o == null)
        {
            return false;
        }
        
        if (getClass() != o.getClass())
        {
            return false;
        }
        
        if (getId() != null)
        {
            return getId().equals(((BaseDescriptor) o).getId());
        }
        else
        {
            return ((BaseDescriptor) o).getId() == null;
        }
    }
    
    /**
     * Provides the hashCode of this object, which is determined by simply
     * delegating the responsibility to the name property
     * @return the hashCode of the name if not null, otherwise delegate to the
     * parent class
     */
    public int hashCode()
    {
        if (getId() != null)
        {
            return getId().hashCode();
        }
        else
        {
            return super.hashCode();
        }
    }
}
