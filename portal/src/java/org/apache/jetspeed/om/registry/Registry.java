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
package org.apache.jetspeed.om.registry;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * Represents all items within Jetspeed that hold configuration information.
 *
 * @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
 * @author <a href="raphael@apache.org">Raphaël Luta</a>
 * @version $Id$
 */
public interface Registry
{

    /**
     * Get the number of entries within the Registry.
     *
     * @return the number of elements in this Registry instance
     */
    public int getEntryCount();

    /**
     * Creates a new RegistryEntry instance compatible with the current
     * Registry instance implementation
     *
     * @return the newly created RegistryEntry
     */
    public RegistryEntry createEntry();

    /**
     * Get the entry in the registry with the specified name
     *
     * @throws RegistryException if the given 'name' does not exist within the
     *                         Registry
     */
    public RegistryEntry getEntry( String name ) throws RegistryException;

    /**
     * Set the entry in the registry with the specified name and Entry
     *
     * @throws RegistryException if the given 'name' does not exist within the
     *                          Registry
     */
    public void setEntry( RegistryEntry entry ) throws RegistryException;

    /**
     * Add the given entry to the registry with the given name.
     *
     * @throws RegistryException if the given 'name' already exists within the
     *                         Registry
     */
    public void addEntry( RegistryEntry entry ) throws RegistryException;

    /**
     * Tests if an entry with the specified name exists within the Registry
     *
     * @param name the name of the entry that we are looking for
     * @return true if an entry with this name exists in the Registry
     */
    public boolean hasEntry( String name );

    /**
     * Removes the given entry from the Registry
     *
     * @param entry the RegistryEntry to remove
     */
    public void removeEntry( RegistryEntry entry );

    /**
     * Removes the given entry from the Registry
     *
     * @param name the name of the entry to remove from the Registry
     */
    public void removeEntry( String name );

    /**
     * Get all entries within this Registry
     *
     * @return an Enumeration of all unordered current entries
     */
    public Enumeration getEntries();

    /**
     * List all the entry names within this Registry
     *
     * @return an Iterator over an unordered list of current entry names
     */
    public Iterator listEntryNames();

    /**
     * Get all entries within this Registry as an array
     *
     * @return an unordered array of current registry entries
     */
    public RegistryEntry[] toArray();

}


