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

// Java imports
import java.util.Iterator;
import java.util.Vector;

import org.apache.jetspeed.om.registry.SecurityAccess;

/**
 * Interface for manipulatin the Security Access on the registry entries
 *
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
 * @version $Id$
 */
public class BaseSecurityAccess implements SecurityAccess, java.io.Serializable
{

    /** Holds value of property action. */
    private String action;

    /** Holds value of property allows. */
    private Vector allows = new Vector();

    /** Holds value of property ownerAllows. */
    private Vector ownerAllows = new Vector();

    /** Holds the combination of allows and ownerAllows. */
    private transient Vector allAllows = new Vector();

    /** Creates new BaseSecurityAccess */
    public BaseSecurityAccess()
    {
    }

    /**
     * Implements the equals operation so that 2 elements are equal if
     * all their member values are equal.
     */
    public boolean equals(Object object)
    {
        if (object == null)
        {
            return false;
        }

        BaseSecurityAccess obj = (BaseSecurityAccess) object;

        if (action != null)
        {
            if (!action.equals(obj.action))
            {
                return false;
            }
        }
        else
        {
            if (obj.action != null)
            {
                return false;
            }
        }

        Iterator i = allows.iterator();
        Iterator i2 = obj.allows.iterator();
        while (i.hasNext())
        {
            BaseSecurityAllow c1 = (BaseSecurityAllow) i.next();
            BaseSecurityAllow c2 = null;

            if (i2.hasNext())
            {
                c2 = (BaseSecurityAllow) i2.next();
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

        i = ownerAllows.iterator();
        i2 = obj.ownerAllows.iterator();
        while (i.hasNext())
        {
            BaseSecurityAllowOwner c1 = (BaseSecurityAllowOwner) i.next();
            BaseSecurityAllowOwner c2 = null;

            if (i2.hasNext())
            {
                c2 = (BaseSecurityAllowOwner) i2.next();
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

    /** Getter for property action.
     * @return Value of property action.
     */
    public String getAction()
    {
        return action;
    }

    /** Setter for property action.
     * @param action New value of property action.
     */
    public void setAction(String action)
    {
        this.action = action;
    }

    /** Getter for property allows.
     * @return Value of property allows.
     */
    public Vector getAllows()
    {
        return allows;
    }

    /** Setter for property allows.
     * @param allows New value of property allows.
     */
    public void setAllows(Vector allows)
    {
        this.allows = allows;
        if (this.allAllows != null)
        {
            allAllows.removeAllElements();
        }
    }

    /** Getter for property ownerAllows.
     * @return Value of property ownerAllows.
     */
    public Vector getOwnerAllows()
    {
        return this.ownerAllows;
    }

    /** Setter for property ownerAllows.
     * @param ownerAllows New value of property ownerAllows.
     */
    public void setOwnerAllows(Vector ownerAllows)
    {
        this.ownerAllows = ownerAllows;
        if (this.allAllows != null)
        {
            allAllows.removeAllElements();
        }
    }

    /**
     * Return a vector contain all allows elements.  If the vector is null
     * or empty, then create and populate it with elements from the allows
     * and ownerAllows vectors.
     *
     * @return vector containing all allows
     */
    public Vector getAllAllows()
    {
        int elementCount = 0;
        if (this.allAllows == null)
        {
            allAllows = new Vector();
        }

        if (allAllows.isEmpty() == true)
        {
            if (this.allows != null)
            {
                elementCount += this.allows.size();
                allAllows.ensureCapacity(elementCount);
                allAllows.addAll(this.allows);
            }

            if (this.ownerAllows != null)
            {
                elementCount += this.ownerAllows.size();
                allAllows.ensureCapacity(elementCount);
                allAllows.addAll(this.ownerAllows);
            }
    }
        return this.allAllows;
    }
}
