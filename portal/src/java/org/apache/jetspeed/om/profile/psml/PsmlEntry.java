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

// Java imports
import org.apache.jetspeed.om.SecurityReference;
import org.apache.jetspeed.om.profile.Entry;

/**
 * Default bean like implementation of the Entry interface
 * suitable for serialization with Castor
 * 
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PsmlEntry extends PsmlIdentityElement implements Entry
{

    private String parent = null;

    /** Holds value of property securityRef. */
    private SecurityReference securityRef;    
    
    /** @return the entry name from which this one is derived */
    public String getParent()
    {
        return this.parent;
    }
                                
    /**
     * Sets the ancestor for this Entry.
     * @param parent the new ancestor entry name. This name should
     * be defined in the system registry
     */
    public void setParent( String parent )
    {
        this.parent = parent;
    }
             
    /** Getter for property securityRef.
     * @return Value of property securityRef.
     */
    public SecurityReference getSecurityRef()
    {
        return securityRef;
    }    

    /** Setter for property securityRef.
     * @param securityRef New value of property securityRef.
     */
    public void setSecurityRef(SecurityReference securityRef)
    {
        this.securityRef = securityRef;
    }
    
    /** This method recreates the paramter name index for quick retrieval
     *  of parameters by name. Shoule be called whenever a complete index
     *  of parameter should be rebuilt (eg removing a parameter or setting 
     *  a parameters vector)
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
     */

    /**
     * Create a clone of this object
     */
    public Object clone()
        throws java.lang.CloneNotSupportedException
    {
        Object cloned = super.clone();

        // clone the securityRef
        ((PsmlEntry)cloned).securityRef = ((this.securityRef == null) ? null : (SecurityReference) this.securityRef.clone());

        return cloned;

    }   // clone
}

