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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.apache.jetspeed.om.registry.Parameter;
import org.apache.jetspeed.om.registry.SkinEntry;

/**
 * The BaseSkinEntry is a bean like implementation of the SkinEntry
 * interface suitable for Castor XML serialization
 *
 * @see org.apache.jetspeed.om.registry.SkinEntry
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
 * @version $Id$
 */
public class BaseSkinEntry extends BaseRegistryEntry implements SkinEntry
{

    private Vector parameter = new Vector();

    private transient Map nameIdx = null;

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

        BaseSkinEntry obj = (BaseSkinEntry)object;

        Iterator i = parameter.iterator();
        Iterator i2 = obj.parameter.iterator();
        while(i.hasNext())
        {
            BaseParameter c1 = (BaseParameter)i.next();
            BaseParameter c2 = null;

            if (i2.hasNext())
            {
                c2 = (BaseParameter)i2.next();
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

    /** @return an enumeration of this entry parameter names */
    public Iterator getParameterNames()
    {
        synchronized (parameter)
        {
            if (nameIdx == null)
            {
                buildNameIndex();
            }
        }

        return nameIdx.keySet().iterator();
    }

    /** Search for a named parameter and return the associated
     *  parameter object. The search is case sensitive.
     *
     *  @return the parameter object for a given parameter name
     *  @param name the parameter name to look for
     */
    public Parameter getParameter( String name )
    {
        synchronized (parameter)
        {
            if (nameIdx == null)
            {
                buildNameIndex();
            }
        }

        if (name != null)
        {
            Integer pos = (Integer)nameIdx.get(name);

            if (pos != null)
            {
                return (Parameter)parameter.elementAt(pos.intValue());
            }
        }

        return null;
    }


    /** Returns a map of parameter values keyed on the parameter names
     *  @return the parameter values map
     */
    public Map getParameterMap()
    {
        Hashtable params = new Hashtable();
        Enumeration en = parameter.elements();
        while(en.hasMoreElements())
        {
            Parameter param = (Parameter)en.nextElement();
            params.put(param.getName(),param.getValue());
        }

        return params;

    }

    /** Adds a new parameter for this entry
     *  @param name the new parameter name
     *  @param value the new parameter value
     */
    public void addParameter( String name, String value )
    {
        if (name != null)
        {
            Parameter p = getParameter(name);
            if (p == null)
            {
                p = new BaseParameter();
                p.setName(name);
            }

            p.setValue(value);

            addParameter(p);

        }
    }

    /** Adds a new parameter for this entry
     *  @param parameter the new parameter to add
     */
    public void addParameter( Parameter param )
    {
        synchronized (parameter)
        {
            parameter.addElement( param );
            nameIdx.put( param.getName(), new Integer( parameter.size()-1 ) );
        }
    }

    /** Removes all parameter values associated with the
     *  name
     *
     * @param name the parameter name to remove
     */
    public void removeParameter( String name )
    {
        if (name == null) return;

        synchronized (parameter)
        {
            Iterator i = parameter.iterator();
            while(i.hasNext())
            {
                Parameter param = (Parameter)i.next();
                if (param.getName().equals(name))
                {
                    i.remove();
                }
            }

            buildNameIndex();
        }
    }

    /** This method recreates the paramter name index for quick retrieval
     *  of parameters by name. Shoule be called whenever a complete index
     *  of parameter should be rebuilt (eg removing a parameter or setting
     *  a parameters vector)
     */
    private void buildNameIndex()
    {
        Hashtable idx = new Hashtable();

        Iterator i = parameter.iterator();
        int count = 0;
        while( i.hasNext() )
        {
            Parameter p = (Parameter)i.next();
            idx.put( p.getName(), new Integer(count) );
            count++;
        }

        this.nameIdx = idx;
    }

    // Castor serialization accessor methods

    /** Needed for Castor 0.8.11 XML serialization for retrieving the
     *  parameters objects associated to this object
     */
    public Vector getParameters()
    {
        return this.parameter;
    }

    public void setParameters(Vector parameters)
    {
        this.parameter = parameters;
    }

}