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
import org.apache.jetspeed.cps.CPSInitializationException;
import org.apache.jetspeed.om.registry.RegistryEntry;
import org.apache.jetspeed.om.registry.RegistryException;

/**
 * <p>This is an implementation of the <code>RegistryService</code>
 * based on the Java Portlet Standard. It extends the CastorRegistryService for XML serialization mechanisms
 * of all registries except for the Portlet and PortletApplication registries. For these two registries
 * this service provides the implementation for storing portlets and portlet applications in the registry.
 * </p>
 *
 * <p>This service expects the following properties to be set for correct operation:
 * <dl>
 *    <dt>directory</dt><dd>The directory where the Registry will look for
 *    fragment files</dd>
 *    <dt>extension</dt><dd>The extension used for identifying the registry fragment
 *    files. Default .xreg</dd>
 *    <dt>mapping</dt><dd>the Castor object mapping file path</dd>
 *    <dt>registries</dt><dd>a comma separated list of registry names to load
 *     from this file</dd>
 *    <dt>refreshRate</dt><dd>Optional. The manager will check every
 *     refreshRate seconds if the config has changed and if true will refresh
 *     all the registries. A value of 0 or negative will disable the
 *     automatic refresh operation. Default: 300 (5 minutes)</dd>
 *    <dt>verbose</dt><dd>Optional. Control the amount of debug output. The bigger
 *    the more output, you've been warned ! Default: 0</dd>
 * </dl>
 * </p>
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:roger.ruttimann@earthlink.net">Roger Ruttimann</a>
 * @version $Id$
 */
public class StandardRegistryService extends CastorRegistryService
{    
    /**
     * Creates a new RegistryEntry instance compatible with the current
     * Registry instance implementation
     *
     * @param regName the name of the registry to use
     * @return the newly created RegistryEntry
     */
    public RegistryEntry createEntry(String regName)
    {
        // TODO: handle Portlet and Portlet Application registries
        RegistryEntry entry = super.createEntry(regName);
        return entry;
    }
    
    /**
     * Returns a RegistryEntry from the named Registry.
     * This is a convenience wrapper around {@link
     * org.apache.jetspeed.om.registry.Registry#getEntry }
     *
     * @param regName the name of the registry
     * @param entryName the name of the entry to retrieve from the
     *                  registry
     * @return a RegistryEntry object if the key is found or null
     */
    public RegistryEntry getEntry(String regName, String entryName)
    {
        // TODO: handle Portlet and Portlet Application registries
        RegistryEntry entry = super.getEntry(regName, entryName);
        return entry;
        
    }
    
    /**
     * Add a new RegistryEntry in the named Registry.
     * This is a convenience wrapper around {@link
     * org.apache.jetspeed.om.registry.Registry#addEntry }
     *
     * @param regName the name of the registry
     * @param entry the Registry entry to add
     * @exception Sends a RegistryException if the manager can't add
     *            the provided entry
     */
    public void addEntry(String regName, RegistryEntry entry)
    throws RegistryException
    {
        // TODO: handle Portlet and Portlet Application registries
        super.addEntry(regName, entry);
    }
    
    /**
     * Deletes a RegistryEntry from the named Registry
     * This is a convenience wrapper around {@link
     * org.apache.jetspeed.om.registry.Registry#removeEntry }
     *
     * @param regName the name of the registry
     * @param entryName the name of the entry to remove
     */
    public void removeEntry(String regName, String entryName)
    {
       // TODO: handle Portlet and Portlet Application registries
        super.removeEntry(regName, entryName);          
    }
        
    public void init() throws CPSInitializationException
    {

        if (isInitialized())
        {
            return;
        }

        
        super.init();
        
        // get specific properties to this service
        String roger = getConfiguration().getString("roger");
        
    }
    
    /**
     * This is the shutdown method called by the
     * Turbine <code>Service</code> framework
     */
    public void shutdown()
    {
        super.shutdown();
    }
   
}
