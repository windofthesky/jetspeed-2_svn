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

package org.apache.cornerstone.framework.api.registry;

import java.util.Set;

public interface IRegistry
{
    public static final String REVISION = "$Revision$";

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
    public Set getEntryNameSet(String domainName, String interfaceName);
    
    /**
    * Gets the RegistryEntry for a particular registryDomainNamr and registryEntryName pair.
    * 
    * @param String registryDomainName
    * @param String registryEntryName
    * @return IRegistryEntry the registry entry object
    */
    public IRegistryEntry getEntry(String domainName, String interfaceName, String entryName);
    
    /**
    * Registers the registryEntry name having the registryEntry provided under the
    * registryDomainName provided
    * 
    * @param String registryEntryName
    * @param String registryEntry
    * 
    */
    public void register(String domainName,String interfaceName, String entryName, IRegistryEntry entry);
    
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
    public void unregister(String domainName, String interfaceName, String entryName);

    public String getInterfaceShortHand(String domainName, String fullName);
    public void setInterfaceShortHand(String domainName, String shortHand, String fullName);
}