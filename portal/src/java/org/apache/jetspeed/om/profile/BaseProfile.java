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

package org.apache.jetspeed.om.profile;

import org.apache.jetspeed.services.psml.PsmlManager;

/**
Provides base functionality within a Registry.

@author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
@version $Id$
*/

public class BaseProfile  extends BaseProfileLocator implements Profile
{

    protected PSMLDocument document = null;

    public BaseProfile()
    {}

    public BaseProfile( ProfileLocator locator )
    {
        this.setAnonymous(locator.getAnonymous());
        this.setCountry(locator.getCountry());
        this.setGroup(locator.getGroup());
        this.setLanguage(locator.getLanguage());
        this.setMediaType(locator.getMediaType());
        this.setName(locator.getName());
        this.setRole(locator.getRole());
        this.setUser(locator.getUser());
    }

    /**
     * @see Object#clone
     * @return an instance copy of this object
     */
    public Object clone() throws java.lang.CloneNotSupportedException
    {
        Object cloned = super.clone();

        // clone the document
        ((BaseProfile)cloned).document = ((this.document == null) ? null : (PSMLDocument) this.document.clone());

        return cloned;
    }

    /**
       Gets the root set of portlets for this profile object.

       @return The root portlet set for this profile.
     */
    public PSMLDocument getDocument()
    {
        synchronized (this)
        {
            if ((this.document == null) || (this.document.getPortlets() == null))
            {
                this.document = PsmlManager.getDocument(this);
            }
        }
  
        return this.document;
    }

    /*
     * Sets the psml document attached to this profile
     *
     * @param The PSML document for this profile.
     */
    public void setDocument(PSMLDocument document)
    {
        this.document = document;
    }

    /**
     provide useful info for ease of debugging
    */
    public String toString()
    {
        return "BaseProfile["+ getId() + "]"; /*
               getUser().getUserName()+","+
               getGroup().getName()+","+
               getRole().getName()+","+
               (getAnonymous() ? "anon,":"")+
               getMediaType()+","+
               getCountry()+","+
               getLanguage()+","+
               getName()+"]"; */
    }


}
