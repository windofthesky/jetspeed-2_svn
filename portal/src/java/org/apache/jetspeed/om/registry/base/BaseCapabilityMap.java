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

import org.apache.jetspeed.om.registry.CapabilityMap;

/**
 * Simple bean-like implementation of the CapabilityMap
 *
 * @author <a href="shesmer@raleigh.ibm.com">Stephan Hesmer</a>
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
 * @version $Id$
 */
public class BaseCapabilityMap implements CapabilityMap, java.io.Serializable
{
    private Vector caps = new Vector();

    public BaseCapabilityMap()
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

        BaseCapabilityMap obj = (BaseCapabilityMap)object;

        Iterator i = caps.iterator();
        Iterator i2 = obj.caps.iterator();
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

    public Iterator getCapabilities()
    {
        return caps.iterator();
    }

    public void addCapability(String name)
    {
        if (!caps.contains(name))
        {
            caps.add(name);
        }
    }

    public void removeCapability(String name)
    {
        caps.remove(name);
    }

    /**
     * Checks if the argument capability is included in this map
     *
     * @param capabiltiy a capability descriptor
     * @return true if the capability is supported
     */
    public boolean contains(String capability)
    {
        return caps.contains(capability);
    }

    /**
     * Checks if the all the elements of argument capability map
     * are included in the current one
     *
     * @param map a CapabilityMap implementation to test
     * @return true is all the elements the argument map are included in the
     * current map.
     */
    public boolean containsAll(CapabilityMap map)
    {
        Iterator i = map.getCapabilities();

        while(i.hasNext())
        {
            String capability = (String)i.next();
            if (!contains(capability))
            {
                return false;
            }
        }

        return true;
    }

    // castor related method definitions

    public Vector getCaps()
    {
        return caps;
    }

}
