/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.cornerstone.framework.registry;

import java.util.*;
import org.apache.cornerstone.framework.api.registry.IRegistry;
import org.apache.cornerstone.framework.api.registry.IRegistryEntry;
import org.apache.cornerstone.framework.constant.Constant;
import org.apache.cornerstone.framework.core.BaseObject;
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

public class BaseRegistry extends BaseObject implements IRegistry
{
    public static final String REVISION = "$Revision$";

    /**
    * Gets the Singleton of the Registry
    * 
    * @return Registry the registry singleton object
    */
    public static IRegistry getSingleton()
    {
        return _Singleton;
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
    public Set getEntryNameSet(String domainName, String interfaceName)
    {
        Map interfaceMap = (Map) _domainMap.get(domainName);
        if (interfaceMap == null)
            return new HashSet();

        Map entryMap = (Map) interfaceMap.get(interfaceName);
        if (entryMap == null)
            return new HashSet();
        else
        	return entryMap.keySet();
    }

    /**
     * Gets the RegistryEntry for a particular registryDomainNamr and registryEntryName pair.
     * 
     * @param String registryDomainName
     * @param String registryEntryName
     * @return IRegistryEntry the registry entry object
     */
    public IRegistryEntry getEntry(String domainName, String interfaceName, String entryName)
    {
        String interfaceShortHand = getInterfaceShortHand(domainName, interfaceName);
        if (interfaceShortHand != null)
            interfaceName = interfaceShortHand;

        Map interfaceMap = (Map) _domainMap.get(domainName);
        if (interfaceMap == null)
            return null;

        Map entryMap = (Map) interfaceMap.get(interfaceName);
        if (entryMap == null)
            return null;
        else
        	return (IRegistryEntry) entryMap.get(entryName);
    }

    /**
     * Registers the registryEntry name having the registryEntry provided under the
     * registryDomainName provided
     * 
     * @param String registryEntryName
     * @param String registryEntry
     * 
     */
    public void register(String domainName, String interfaceName, String entryName, IRegistryEntry entry)
    {
        // register the registryEntryName in the registryEntryRegistry file
        Map interfaceMap = (Map) _domainMap.get(domainName);
        if (interfaceMap == null)
        {
            interfaceMap = new HashMap();
            _domainMap.put(domainName, interfaceMap);
        }

        Map entryMap = (Map) interfaceMap.get(interfaceName);
        if (entryMap == null)
        {
        	entryMap = new HashMap();
            interfaceMap.put(interfaceName, entryMap);
        }

        entryMap.put(entryName, entry);
        _Logger.info(domainName + Constant.SLASH + interfaceName + Constant.SLASH + entryName + " registered");
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
    public void unregister(String domainName, String interfaceName, String entryName)
    {
        Map interfaceMap = (Map)(_domainMap.get(domainName));
        if (interfaceMap != null)
        {
            Map entryMap = (Map) interfaceMap.get(interfaceName);
            if (entryMap != null)
            	entryMap.remove(entryName);
        }
    }

    public String getInterfaceShortHand(String domainName, String fullName)
    {
        String key = domainName + Constant.SLASH + fullName;
    	return _interfaceShortHandMap.getProperty(key);
    }

    public void setInterfaceShortHand(String domainName, String shortHand, String fullName)
    {
        String key = domainName + Constant.SLASH + fullName;
        _interfaceShortHandMap.setProperty(key, shortHand);
        if (_Logger.isInfoEnabled()) _Logger.info("shortHand: '" + shortHand + "' => '" + fullName + "'");
    }

    private static Logger _Logger = Logger.getLogger(BaseRegistry.class); 
    private static BaseRegistry _Singleton = new BaseRegistry();
    protected Map _domainMap = new HashMap();
    protected Properties _interfaceShortHandMap = new Properties();
}