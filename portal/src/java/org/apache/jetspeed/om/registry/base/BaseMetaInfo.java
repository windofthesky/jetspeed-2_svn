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

import org.apache.jetspeed.om.registry.MetaInfo;

/**
 * Bean like implementation of the Metainfo interface suitable for
 * Castor serialization.
 *
 * @see org.apache.jetspeed.om.registry.MetaInfo
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
 * @version $Id$
 */
public class BaseMetaInfo implements MetaInfo, java.io.Serializable
{
    private String title;

    private String description;

    private String image;

    public BaseMetaInfo()
    {}

    public BaseMetaInfo(String title, String description, String image)
    {
        this.title = title;
        this.description = description;
        this.image = image;
    }

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

        BaseMetaInfo obj = (BaseMetaInfo)object;

        if (title!=null)
        {
            if (!title.equals(obj.getTitle()))
            {
                return false;
            }
        }
        else
        {
            if (obj.getTitle()!=null)
            {
                return false;
            }
        }

        if (description!=null)
        {
            if(!description.equals(obj.getDescription()))
            {
                return false;
            }
        }
        else
        {
            if (obj.getDescription()!=null)
            {
                return false;
            }
        }

        if (image!=null)
        {
            if(!image.equals(obj.getImage()))
            {
                return false;
            }
        }
        else
        {
            if (obj.getImage()!=null)
            {
                return false;
            }
        }

        return true;
    }

    /** @return the title for this entry */
    public String getTitle()
    {
        return this.title;
    }

    /** Sets the title for this entry
     * @param title the new title for this entry
     */
    public void setTitle( String title )
    {
        this.title = title;
    }

    /** @return the description for this entry */
    public String getDescription()
    {
        return this.description;
    }

    /** Sets the description for this entry
     * @param description the new description for this entry
     */
    public void setDescription( String description )
    {
        this.description = description;
    }

    /** @return the image link for this entry */
    public String getImage()
    {
        return this.image;
    }

    /** Sets the image URL attached to this entry
     * @param image the image URL to link to this entry
     */
    public void setImage( String image )
    {
        this.image = image;
    }

}
