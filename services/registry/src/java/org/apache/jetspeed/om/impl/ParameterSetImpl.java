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
package org.apache.jetspeed.om.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

//TODO: import org.apache.jetspeed.exception.JetspeedRuntimeException;
import org.apache.jetspeed.om.common.ParameterComposite;
import org.apache.pluto.om.common.Parameter;
import org.apache.pluto.om.common.ParameterSet;
import org.apache.pluto.om.common.ParameterSetCtrl;

/**
 * 
 * ParameterSetImpl
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public abstract class ParameterSetImpl implements ParameterSet, ParameterSetCtrl, Serializable
{
    protected Collection innerCollection;

    /**
     * @param wrappedSet
     */
    public ParameterSetImpl(Collection collection)
    {
        super();
        this.innerCollection = collection;
    }

    public ParameterSetImpl()
    {
        super();
        this.innerCollection = new ArrayList();
    }

    /**
     * @see org.apache.pluto.om.common.ParameterSet#iterator()
     */
    public Iterator iterator()
    {
        return innerCollection.iterator();
    }

    /**
     * @see org.apache.pluto.om.common.ParameterSet#get(java.lang.String)
     */
    public Parameter get(String name)
    {
        Iterator itr = innerCollection.iterator();
        while (itr.hasNext())
        {
            Parameter p = (Parameter) itr.next();
            if (p.getName().equals(name))
            {
                return p;
            }
        }

        return null;
    }

    /**
     * @see org.apache.pluto.om.common.ParameterSetCtrl#add(java.lang.String, java.lang.String)
     */
    public Parameter add(String name, String value)
    {
        ParameterComposite p = newParameterInstance();
        p.setName(name);
        p.setValue(value);
        add(p);
        return p;
    }

    /**
     * @see org.apache.pluto.om.common.ParameterSetCtrl#remove(java.lang.String)
     */
    public Parameter remove(String name)
    {
        Iterator itr = innerCollection.iterator();
        Parameter removeMe = null;
        while (itr.hasNext())
        {
            Parameter p = (Parameter) itr.next();
            if (p.getName().equals(name))
            {
                removeMe = p;
                break;
            }
        }

        if (removeMe != null)
        {
            innerCollection.remove(removeMe);
        }

        return removeMe;
    }

    /**
     * @see org.apache.pluto.om.common.ParameterSetCtrl#remove(org.apache.pluto.om.common.Parameter)
     */
    public void remove(Parameter parameter)
    {
        remove((Object) parameter);
    }

    /**
     * @see java.util.Collection#add(java.lang.Object)
     * <strong>NOTE: </code>This method will effectively convert any class
     * implementing the <code>org.apache.jetspeed.common.ParameterComposite</code>
     * that is NOT of the type returned by the <code>getParameterClass()</code> method it is
     *  to converted to the correct Parameter implementation.
     */
    public boolean add(Object o)
    {
        ParameterComposite p = (ParameterComposite) o;

        return innerCollection.add(p);
    }

    /**
     * @see java.util.Collection#remove(java.lang.Object)
     */
    public boolean remove(Object o)
    {
        Parameter p = (Parameter) o;

        return innerCollection.remove(p);
    }

    /**
     * Creates a Parameter class this Collection will be working with.
     * <br>
     */
    protected abstract ParameterComposite newParameterInstance();

    /**
     * @return
     */
    public Collection getInnerCollection()
    {
        return innerCollection;
    }

    /**
     * @param collection
     */
    public void setInnerCollection(Collection collection)
    {
        innerCollection = collection;
    }

}
