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

package org.apache.cornerstone.framework.registry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.cornerstone.framework.api.registry.IRegistry;
import org.apache.cornerstone.framework.api.registry.IRegistryEntry;
import org.apache.cornerstone.framework.singleton.Singleton;
import org.apache.log4j.Logger;

/**
 * This abstracts the registryEntry registry entry into one container.
 * Namely, a hashmap and it implements the IServiceRegistry interface
 * which allows the user to register and unregister, getRegistryEntry, of
 * registry entries under a particular domain.  Note that the registry can have many
 * domains, and domains are cosidered the first level sub-folders under the main
 * registry folder
 * 
 * For example:
 * You can have the main registry directory as /registry
 * and then subsequent subfolders as /registry/actions, /registry/service etc.
 * the action and service sub folders are the Domains in this case.
 * 
 */

public class BaseRegistry extends Singleton implements IRegistry
{
    public static final String REVISION = "$Revision$";

    /**
    * Gets the Singleton of the Registry
    * 
    * @return Registry the registry singleton object
    */
    public static BaseRegistry getSingleton()
    {
        return _Singleton;
    }

    /**
    * Gets the Map of all RegistryDomain names as keys
    * 
    * @return Map the registry Map
    */
    public Set getRegistryDomainSet()
    {
        return _registryDomainMap.keySet();
    }
    
    /**
     * Gets the Map of all RegistryEntry objects for a particular registryDomainName
     * ie each registryEntry, has one registryEntry properties file, and hence
     * it will have one entry in this map represented as
     * a RegistryEntry object.
     * 
     * @param String registryDomainName
     * @return Map the registry Map
     * 
     */
    public Set getRegistryEntryNameSet(String registryDomainName)
    {
        Map registryEntryMap = (Map) _registryDomainMap.get(registryDomainName);
        if (registryEntryMap == null)
            return new HashSet();
        else
            return registryEntryMap.keySet();
    }

    /**
     * Gets the RegistryEntry for a particular registryDomainNamr and registryEntryName pair.
     * 
     * @param String registryDomainName
     * @param String registryEntryName
     * @return IRegistryEntry the registry entry object
     */
    public IRegistryEntry getRegistryEntry(String registryDomainName, String registryEntryName)
    {
        Map registryEntryMap = (Map) _registryDomainMap.get(registryDomainName);
        if (registryEntryMap == null)
            return null;
        else        
            return (IRegistryEntry)registryEntryMap.get(registryEntryName);
    }

    /**
     * Registers the registryEntry name having the registryEntry provided under the
     * registryDomainName provided
     * 
     * @param String registryEntryName
     * @param String registryEntry
     * 
     */
    public void register(String registryDomainName, String registryEntryName, IRegistryEntry registryEntry)
    {
        // register the registryEntryName in the registryEntryRegistry file
        Map currentRegistryEntryMap = (Map) _registryDomainMap.get(registryDomainName);
        if (currentRegistryEntryMap == null)
        {
            currentRegistryEntryMap = new HashMap();
            _registryDomainMap.put(registryDomainName, currentRegistryEntryMap);
        }
        currentRegistryEntryMap.put(registryEntryName, registryEntry);
        _Logger.info(registryDomainName + ":" + registryEntryName + " registered");
    }

    /**
     * Un-Registers the registryEntry name and registryDomainName pair
     * 
     * NOTE: this only unregisters from the in memory registration
     * ie it removes the registry entry from the registry map in the memory, and
     * does not delete the actual regustry file corresponding to this registry
     * 
     * @param String registryDomainName 
     * @param String registryEntryName
     * 
     */    
    public void unregister(String registryDomainName,String registryEntryName)
    {
        // remove the registry registryEntryName fron the list
        Map registryEntryMap = (Map)(this._registryDomainMap.get(registryDomainName));
        if (registryEntryMap != null)
            registryEntryMap.remove(registryEntryName);
    }

    protected Map _registryDomainMap = new HashMap();

    private static BaseRegistry _Singleton = new BaseRegistry();
    private static Logger _Logger = Logger.getLogger(BaseRegistry.class); 
}