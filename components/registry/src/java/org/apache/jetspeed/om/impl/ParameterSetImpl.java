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
