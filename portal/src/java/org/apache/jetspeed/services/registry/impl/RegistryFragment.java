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

package org.apache.jetspeed.services.registry.impl;

import org.apache.jetspeed.services.registry.RegistryService;
import org.apache.jetspeed.om.registry.RegistryEntry;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * Bean like implementation of a multi-object registry usable
 * by Castor XML serialization
 *
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
 * @version $Id$
 */
public class RegistryFragment extends Hashtable implements java.io.Serializable
{

    /** this flag is used to mark this fragment has some changes that are
     * not yet persisted to disk
     */
    private transient boolean dirty = false;

    /** this flag is used to mark that this fragment needs to updated to
     * incorporated changes from its disk state
     */
    private transient boolean changed = false;

    /** @return true if this fragment has some unpersisted changes
     */
    public boolean isDirty()
    {
        return this.dirty;
    }

    /** Sets the dirty flag indicating wether this fragment has some
     * uncommitted changes
     *
     * @param value the new dirty state for this fragment
     */
    public void setDirty(boolean value)
    {
        this.dirty = value;
    }

    /** @return true if this fragment has some persisted changes that need loading
     */
    public boolean hasChanged()
    {
        return this.changed;
    }

    /** Sets the changed flag indicating wether this fragment has some
     * changes to load
     *
     * @param value the new dirty state for this fragment
     */
    public void setChanged(boolean value)
    {
        this.changed = value;
    }

    /** @return the entries stored in this Fragment that are suitable
     *  for the requested registry
     *
     *  @param name a valid Registry name.
     */
    public Vector getEntries(String name)
    {

        if (name != null)
        {
            Vector registry = (Vector) get(name);

            if (registry != null)
            {
                return registry;
            }
        }

        return new Vector();
    }

    /** Add a new entry in the fragment. It does not check for name
     *  duplication
     *  @param name a valid Registry name.
     *  @param entry the entry to add
     */
    public void addEntry(String name, RegistryEntry entry)
    {
        if ((name != null) && (entry != null))
        {
            Vector registry = (Vector) get(name);

            if (registry != null)
            {
                registry.add(entry);
            }
        }
    }

    /** Remove an existing entry in the fragment.
     *  @param name a valid Registry name.
     *  @param entryName the name of the entry to remove
     */
    public void removeEntry(String name, String entryName)
    {
        if ((name != null) && (entryName != null))
        {
            Vector registry = (Vector) get(name);
            if (registry != null)
            {
                Iterator i = registry.iterator();
                while (i.hasNext())
                {
                    RegistryEntry regEntry = (RegistryEntry) i.next();
                    if (entryName.equals(regEntry.getName()))
                    {
                        i.remove();
                    }
                }
            }
        }
    }

    /** Modify an existing entry in the fragment.
     *  @param name a valid Registry name.
     *  @param entry the entry to add
     */
    public void setEntry(String name, RegistryEntry entry)
    {
        if (entry != null)
        {
            removeEntry(name, entry.getName());
            addEntry(name, entry);
        }
    }

    // Castor serialization support methods

    public Vector getPortlets()
    {
        return (Vector) get(RegistryService.PORTLET);
    }

    public void setPortlets(Vector portlets)
    {
        if (portlets != null)
        {
            put(RegistryService.PORTLET, portlets);
        }
    }

    public Vector getControls()
    {
        return (Vector) get(RegistryService.PORTLET_CONTROL);
    }

    public void setControls(Vector controls)
    {
        if (controls != null)
        {
            put(RegistryService.PORTLET_CONTROL, controls);
        }
    }

    public Vector getControllers()
    {
        return (Vector) get(RegistryService.PORTLET_CONTROLLER);
    }

    public void setControllers(Vector controllers)
    {
        if (controllers != null)
        {
            put(RegistryService.PORTLET_CONTROLLER, controllers);
        }
    }

    public Vector getMedias()
    {
        return (Vector) get(RegistryService.MEDIA_TYPE);
    }

    public void setMedias(Vector medias)
    {
        if (medias != null)
        {
            put(RegistryService.MEDIA_TYPE, medias);
        }
    }

    public Vector getSkins()
    {
        return (Vector) get(RegistryService.SKIN);
    }

    public void setSkins(Vector skins)
    {
        if (skins != null)
        {
            put(RegistryService.SKIN, skins);
        }
    }

    public Vector getSecurityEntries()
    {
        return (Vector) get(RegistryService.SECURITY);
    }

    public void setSecurityEntries(Vector securityEntries)
    {
        if (securityEntries != null)
        {
            put(RegistryService.SECURITY, securityEntries);
        }
    }

    public Vector getClients()
    {
        return (Vector) get(RegistryService.CLIENT);
    }

    public void setClients(Vector clients)
    {
        if (clients != null)
        {
            put(RegistryService.CLIENT, clients);
        }
    }
}
