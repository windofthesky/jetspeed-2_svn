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

import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.om.profile.Entry;

import java.io.Serializable;
/**
 * This interface represents a loaded PSML document in memory, providing
 * all facilities for finding and updating specific parts of the 
 * document.
 *
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
 * @version $Id$
 */
public interface PSMLDocument extends Serializable, Cloneable
{
    /**
     * Return the name of this document
     */
    public String getName();
        
    /**
     * Sets a new name for this document
     * 
     * @param name the new document name
     */
    public void setName(String name);

    /**
     * Return the portlet set PSML description of this document
     *
     * @return a PSML object model hierarchy, or null if none is 
     * defined for this document
     */
    public Portlets getPortlets();

    /**
     * Sets a new PSML object model for this document
     * 
     * @param portlets the PSML object model
     */
    public void setPortlets(Portlets portlets);

    /** Returns the first entry in the current PSML resource corresponding 
     *  to the given portlet name
     * 
     *  @param name the portlet name to seek
     *  @return the found entry description or null
     */
    public Entry getEntry(String name);

    /** Returns the first entry in the current PSML resource corresponding 
     *  to the given entry id
     * 
     *  @param entryId the portlet's entry id to seek
     *  @return the found entry description or null
     */
    public Entry getEntryById(String entryId);

    /** Returns the first portlets element in the current PSML resource corresponding 
     *  to the given name
     * 
     *  @param name the portlets name to seek
     *  @return the found portlets description or null
     */
    public Portlets getPortlets(String name);

    /** Returns the first portlets element in the current PSML resource corresponding 
     *  to the given name
     * 
     *  @param portletId the portlet's entry id to seek
     *  @return the found portlets description or null
     */
    public Portlets getPortletsById(String portletId);

    /** Returns the first portlets element in the current PSML resource 
     *  found at the specified position. The position is computed using
     *  a left-most tree traversal algorithm of the existing portlets (thus
     *  not counting other entry objects)
     * 
     *  @param position the sought position
     *  @return the found portlets object or null if we did not find such an
     *  object
     */
    public Portlets getPortlets(int position);

    /** 
     * Removes the first entry in the current PSML resource corresponding 
     * to the given entry id
     * 
     * @param entryId the portlet's entry id to remove
     * @return true if the entry was removed
     */
    public boolean removeEntryById(String entryId);
    
    /**
     * Create a clone of this object
     */
    public Object clone()
        throws java.lang.CloneNotSupportedException;

}

