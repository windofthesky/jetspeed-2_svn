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
package org.apache.jetspeed.capability;

import java.util.Vector;


/**
 * This entry describes all the properties that should be present in
 * a RegistryEntry describing a MediaType
 *
 * FIXME: we should add some additionnal attrbutes for separating 2 versions
 * of the same mime type
 *
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
 * @version $Id$
 */
public interface MediaTypeEntry 
{
    /**
     * Set MediaType ID -- Assigns ID
     * @param id
     */
    public void setMediatypeId(int id);

    /**
     * Get MediaType ID -- Return ID
     * @return MediaTypeID
     */
    public int getMediatypeId();
    
    /** @return the character set associated with this MediaType */
    public String getCharacterSet();

    /** Sets the character set associated with this MediaType */
    public void setCharacterSet( String charSet);

    /**
     * Returns all supported capablities as <CODE>CapabilityMap</CODE>.
     * The <CODE>CapabilityMap</CODE> contains all capabilities in arbitrary
     * order.
     *
     * @return a vector of capabilities
     * 
     */
    public Vector getCapabilities();
    
    /**
     * Set the capabilities
     * @param vector of capabilities
     */
    public void setCapabilities(Vector capabilities);
    
   /**
   * Returns all supported mimetypes as <CODE>MimeTypeMap</CODE>.
   * The <CODE>MimeTypeMap</CODE> contains all mimetypes in decreasing
   * order of importance.
   *
   * @return the MimeTypeMap
   * @see MimeTypeMap
   */
  public Vector getMimetypes();
  
  /**
   * Set mime types
   * @param mimetypes
   */
  public void setMimetypes(Vector mimetypes);
  
  /**
   *    removes the MimeType to the MimeType map 
   * @param name
   */
  
  public void removeMimetype(String name);
  
  /**
   * removes the MimeType to the MimeType map 
   * @param name
   */
  public void addMimetype(String name);

    
}
