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

package org.apache.jetspeed.om.profile.psml;

import java.util.Vector;
import java.util.Iterator;

import org.apache.jetspeed.om.profile.ConfigElement;
import org.apache.jetspeed.om.profile.Parameter;


/**
 * Base simple bean-like implementation of the ConfigElement interface
 * suitable for Castor XML serialization.
 * 
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public /*abstract*/ class PsmlConfigElement implements ConfigElement, java.io.Serializable
{

    private String name = null;
    
    private Vector parameters = new Vector();


    public PsmlConfigElement()
    {}
         
    /** @see org.apache.jetspeed.om.registry.RegistryEntry#getName */
    public String getName()
    {
        return this.name;
    }
                                
    /** @see org.apache.jetspeed.om.registry.RegistryEntry#setName */
    public void setName( String name )
    {
        this.name = name;
    }

    /** @return the parameters */
    public Vector getParameters()
    {
        return this.parameters;
    }
                                
    /** Sets the parameters for this element
     * @param parameters 
     */
    public void setParameters(Vector parameters)
    {
        this.parameters = parameters;
    }

    public String getParameterValue(String name)
    {
        if (parameters == null)
            return null;

        for (int ix=0; ix < parameters.size(); ix++)
        {
            Parameter param = (Parameter)parameters.elementAt(ix);
            if (param.getName().equals(name))
                return param.getValue();
        }
        return null;
   }

    public Parameter getParameter(String name)
    {
        if (parameters == null)
            return null;

        for (int ix=0; ix < parameters.size(); ix++)
        {
            Parameter param = (Parameter)parameters.elementAt(ix);
            if (param.getName().equals(name))
                return param;
        }
        return null;
   }

    public Iterator getParameterIterator()
    {
        return parameters.iterator();
    }

    public Parameter getParameter(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > parameters.size())) 
        {
            throw new IndexOutOfBoundsException();
        }
        
        return (Parameter) parameters.elementAt(index);
    } //-- Parameter getParameter(int) 

    public int getParameterCount()
    {
        return parameters.size();
    } //-- int getParameterCount() 

    public void removeAllParameter()
    {
        parameters.removeAllElements();
    } //-- void removeAllParameter() 

    public Parameter removeParameter(int index)
    {
        Object obj = parameters.elementAt(index);
        parameters.removeElementAt(index);
        return (Parameter) obj;
    } //-- Parameter removeParameter(int) 

    public void setParameter(int index, Parameter vParameter)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > parameters.size())) 
        {
            throw new IndexOutOfBoundsException();
        }
        parameters.setElementAt(vParameter, index);
    } //-- void setParameter(int, Parameter) 

    public Parameter[] getParameter()
    {
        int size = parameters.size();
        Parameter[] mArray = new Parameter[size];
        for (int index = 0; index < size; index++) 
        {
            mArray[index] = (Parameter) parameters.elementAt(index);
        }
        return mArray;
    } //-- Parameter[] getParameter() 

    public void addParameter(Parameter vParameter)
        throws java.lang.IndexOutOfBoundsException
    {
        parameters.addElement(vParameter);
    } //-- void addParameter(Parameter) 

    /**
     * Create a clone of this object
     */
    public Object clone()
        throws java.lang.CloneNotSupportedException
    {
        Object cloned = super.clone();

        // clone the vector's Parameter contents
        if (this.parameters != null)
        {
            ((PsmlConfigElement)cloned).parameters = new Vector(this.parameters.size());
            Iterator it = this.parameters.iterator();
            while (it.hasNext())
            {
                ((PsmlConfigElement)cloned).parameters.add((Parameter) ((Parameter)it.next()).clone());
            }
        }
        
        return cloned;

    }   // clone

}
