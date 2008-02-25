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

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;

/**
 * PortletIterator - seamless iterator over nested vectors of portlet collections
 * 
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PortletIterator implements Iterator
{
    protected PortletEntry entry;
    protected String method;
    protected Vector vector ;
    protected int index = 0;    

    public PortletIterator(PortletEntry entry, String method)
    {
        this.entry = entry;
        this.method = method;
        this.vector = getVector();
    }

    public boolean hasNext()
    {
        int size = vector.size();

        if (size == 0)
            return false;

        if (index >= size)
        {
            entry = getParentEntry(entry);
            if (entry  == null)
                return false;
            vector = getVector();

            if (vector == null)
            {
                return false;
            }
            index = 0;
            if (vector.size() == 0)
                return false;
        }
        return true;
    }

    public void remove() throws IllegalStateException, UnsupportedOperationException
    {
        throw new UnsupportedOperationException("The remove() method is not supported");
    }

    protected PortletEntry getParentEntry(PortletEntry entry)
    {
//        String parentName = entry.getParent();
        String parentName = null;
        if (parentName == null || parentName.equals(""))
            return null;

        PortletEntry parent = null;
//        parent = (PortletEntry)JetspeedRegistry.getEntry( RegistryService.PORTLET, entry.getParent() );        
        return parent;
    }

    public Object next() throws NoSuchElementException
    {
        Object o = vector.elementAt(index);
        index++;
        return o;
    }

    protected Vector getVector()
    {
        try
        {
            this.vector = (Vector)this.entry.getClass().getMethod(this.method, null).invoke(this.entry, null);
        }
        catch (Exception e)
        {
            this.vector = null;
        }

        return this.vector;
    }
}
