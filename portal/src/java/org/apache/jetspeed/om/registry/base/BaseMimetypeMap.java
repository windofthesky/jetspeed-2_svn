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

import java.util.Iterator;
import java.util.Vector;

import org.apache.jetspeed.om.registry.MimetypeMap;
import org.apache.jetspeed.util.MimeType;

/**
 * Simple bean-like implementation of the CapabilityMap
 *
 * @author <a href="shesmer@raleigh.ibm.com">Stephan Hesmer</a>
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
 * @version $Id$
 */
public class BaseMimetypeMap implements MimetypeMap, java.io.Serializable
{
    private Vector mimetypesVector = new Vector();

    private transient Vector mimes;

    public BaseMimetypeMap()
    {
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

        BaseMimetypeMap obj = (BaseMimetypeMap)object;

        Iterator i = mimetypesVector.iterator();
        Iterator i2 = obj.mimetypesVector.iterator();
        while(i.hasNext())
        {
            String c1 = (String)i.next();
            String c2 = null;

            if (i2.hasNext())
            {
                c2 = (String)i2.next();
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

        return true;
    }

    public Iterator getMimetypes()
    {
        if (mimes == null)
        {
            buildMimetable();
        }

        return mimes.iterator();
    }

    public MimeType getPreferredMimetype()
    {
        if (mimes == null)
        {
            buildMimetable();
        }

        return (MimeType)mimes.get(0);
    }

    public void addMimetype(String name)
    {
        if (!mimetypesVector.contains(name))
        {
            mimetypesVector.add(name);
            buildMimetable();
        }
    }

    public void removeMimetype(String name)
    {
        mimetypesVector.remove(name);
        buildMimetable();
    }

    protected void buildMimetable()
    {
        Vector types = new Vector();
        Iterator i = mimetypesVector.iterator();

        while(i.hasNext())
        {
            String mime = (String)i.next();
            types.add(new MimeType(mime));
        }

        this.mimes = types;
    }

    // castor related method definitions

    public Vector getMimetypesVector()
    {
        return mimetypesVector;
    }

}
