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

import org.apache.jetspeed.om.registry.RegistryEntry;
import org.apache.jetspeed.om.registry.InvalidEntryException;
import java.util.Map;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Provides base functionality within a Registry.
 *
 * @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
 * @version $Id$
 */
public class BaseRegistry implements LocalRegistry
{
    protected static final boolean DEBUG = false;

    protected Map entries = new TreeMap();

    /** @see Registry#getEntryCount */
    public int getEntryCount()
    {
        return this.entries.size();
    }

    /** @see Registry#getEntry */
    public RegistryEntry getEntry( String name ) throws InvalidEntryException
    {

        RegistryEntry entry = null;

        if (name != null)
        {
            entry = (RegistryEntry)this.entries.get( name ) ;
        }

        if (entry == null)
        {
            throw new InvalidEntryException( InvalidEntryException.ENTRY_DOES_NOT_EXIST+" "+name );
        }

        return entry;
    }

    /**
    @see Registry#setEntry
    */
    public void setEntry( RegistryEntry entry ) throws InvalidEntryException
    {
        synchronized (this)
        {

            if ( this.hasEntry( entry.getName() ) == false )
            {
                throw new InvalidEntryException( InvalidEntryException.ENTRY_DOES_NOT_EXIST+" "+entry.getName());
            }

            this.entries.put( entry.getName(), entry );
        }
    }

    /**
    @see Registry#addEntry
    */
    public void addEntry( RegistryEntry entry ) throws InvalidEntryException
    {

        synchronized (this)
        {
            if ( this.hasEntry( entry.getName() ) )
            {
                throw new InvalidEntryException( InvalidEntryException.ENTRY_ALREADY_PRESENT );
            }

            this.entries.put( entry.getName(), entry );
        }
    }

    /**
       @see Registry#hasEntry
    */
    public boolean hasEntry( String name )
    {
        return this.entries.containsKey( name );
    }

    /**
    @see Registry#removeEntry
    */
    public void removeEntry( String name )
    {
        synchronized(this)
        {
            this.entries.remove( name );
        }
    }

    /**
    @see Registry#removeEntry
    */

    public void removeEntry( RegistryEntry entry )
    {
        synchronized(this)
        {
            this.entries.remove( entry.getName() );
        }
    }

    /**
       @see Registry#getEntries
     */
    public Enumeration getEntries()
    {
        Vector v = null;

        synchronized (this)
        {
            // this is ne
            v = new Vector(this.entries.values());
        }

        return v.elements();
    }

    /**
       @see Registry#listEntryNames
     */
    public Iterator listEntryNames()
    {
        return entries.keySet().iterator();
    }

    /**
       @see Registry#toArray
     */
    public RegistryEntry[] toArray()
    {

        Enumeration enum = getEntries();
        Vector v = new Vector();

        while( enum.hasMoreElements() )
        {
            v.addElement( enum.nextElement() );
        }

        RegistryEntry[] entries = new RegistryEntry[ v.size() ];
        v.copyInto( entries );
        return entries;

    }

    /**
     * Creates a new RegistryEntry instance compatible with the current
     * Registry instance implementation
     *
     * @return the newly created RegistryEntry
     */
    public RegistryEntry createEntry()
    {
        return new BaseRegistryEntry();
    }


    // RegistryService specific methods

    /**
     * This method is used  to only set the entry in the local
     * memory cache of the registry without any coherency check with
     * persistent storage
     *
     * @param entry the RegistryEntry to store
     */
    public void setLocalEntry( RegistryEntry entry ) throws InvalidEntryException
    {
        synchronized (this)
        {

            if ( this.hasEntry( entry.getName() ) == false )
            {
                throw new InvalidEntryException( InvalidEntryException.ENTRY_DOES_NOT_EXIST+" "+entry.getName());
            }

            this.entries.put( entry.getName(), entry );
        }
    }

    /**
     * This method is used to only add the entry in the local
     * memory cache of the registry without any coherency check with
     * persistent storage
     *
     * @param entry the RegistryEntry to store
     */
    public void addLocalEntry( RegistryEntry entry ) throws InvalidEntryException
    {

        synchronized (this)
        {
            if ( this.hasEntry( entry.getName() ) )
            {
                throw new InvalidEntryException( InvalidEntryException.ENTRY_ALREADY_PRESENT );
            }

            this.entries.put( entry.getName(), entry );
        }
    }

    /**
     * This method is used to only remove the entry from the local
     * memory cache of the registry without any coherency check with
     * persistent storage
     *
     * @param name the name of the RegistryEntry to remove
     */
    public void removeLocalEntry( String name )
    {
        synchronized(this)
        {
            this.entries.remove( name );
        }
    }

    /**
     * This method is used to only remove the entry from the local
     * memory cache of the registry without any coherency check with
     * persistent storage
     *
     * @param entry the RegistryEntry to remove
     */
    public void removeLocalEntry( RegistryEntry entry )
    {
        synchronized(this)
        {
            this.entries.remove( entry.getName() );
        }
    }

}
