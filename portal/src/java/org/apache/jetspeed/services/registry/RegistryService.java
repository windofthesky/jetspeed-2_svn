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
package org.apache.jetspeed.services.registry;

import java.util.Enumeration;

import org.apache.fulcrum.Service;
import org.apache.jetspeed.om.registry.RegistryEntry;
import org.apache.jetspeed.om.registry.RegistryException;
import org.apache.jetspeed.om.registry.Registry;

/**
 * <P>This service is a facade for all registry related operations</P>
 *
 * @see org.apache.jetspeed.om.registry.Registry
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a> 
 * @version $Id$
 *
 */
public interface RegistryService extends Service
{
    /** Default Skin Registry name */
    public static String SKIN = "Skin";
    
    
    /** Default Security Registry name */
    public static String SECURITY = "Security";
    
    
    /** Default PortletController Registry name */
    public static String PORTLET_CONTROLLER = "PortletController";
    
    
    /** Default PortletControl Registry name */
    public static String PORTLET_CONTROL = "PortletControl";
    
    
    /** Default MediaType Registry name */
    public static String MEDIA_TYPE = "MediaType";
    
    
    /** Default Client Registry name */
    public static String CLIENT = "Client";
    
    
    /** Default Portlet Registry name */
    public static String PORTLET = "Portlet";
    
    
    
    /** The name of this service */
    public String SERVICE_NAME = "Registry";
    
    /**
     * Returns a Registry object for further manipulation
     *
     * @param regName the name of the registry to fetch
     * @return a Registry object if found by the manager or null
     */
    public Registry get(String regName);
    
    /**
     * Creates a new RegistryEntry instance compatible with the current
     * Registry instance implementation
     *
     * @param regName the name of the registry to use
     * @return the newly created RegistryEntry
     */
    public RegistryEntry createEntry(String regName);
    
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
    public RegistryEntry getEntry(String regName, String entryName);
    
    /** Add a new RegistryEntry in the named Registry.
     * This is a convenience wrapper around {@link
     * org.apache.jetspeed.om.registry.Registry#addEntry }
     * @param regName the name of the registry
     * @param entry the Registry entry to add
     * @throws RegistryException a RegistryException if the manager can't add the provided entry
     */
    public void addEntry(String regName, RegistryEntry entry)
    throws RegistryException;
    
    /**
     * Deletes a RegistryEntry from the named Registry
     * This is a convenience wrapper around {@link
     * org.apache.jetspeed.om.registry.Registry#removeEntry }
     *
     * @param regName the name of the registry
     * @param entryName the name of the entry to remove
     */
    public void removeEntry(String regName, String entryName);
    
    /**
     *  List all the registry currently available to this service
     *
     * @return an Enumeration of registry names.
     */
    public Enumeration getNames();
    
}
