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

import java.util.Set;

import org.apache.cornerstone.framework.api.registry.IRegistry;
import org.apache.cornerstone.framework.api.registry.IRegistryEntry;

public class OverwritableRegistry extends BaseRegistry
{
    public static final String REVISION = "$Revision$";

    public static IRegistry getSingleton()
    {
    	return _Singleton;
    }

    public void init()
    {
        _parent = null;
    }

    public void setParent(IRegistry overwrittenRegistry)
    {
    	_parent = overwrittenRegistry;
    }

    /* (non-Javadoc)
     * @see org.apache.cornerstone.framework.api.registry.IRegistry#getRegistryEntry(java.lang.String, java.lang.String, java.lang.String)
     */
    public IRegistryEntry getEntry(String domainName, String interfaceName, String entryName)
    {
        IRegistryEntry localEntry = super.getEntry(domainName, interfaceName, entryName);
        if (localEntry == null)
            return _parent.getEntry(domainName, interfaceName, entryName);
        else
            return localEntry;
    }

    /* (non-Javadoc)
     * @see org.apache.cornerstone.framework.api.registry.IRegistry#getRegistryEntryNameSet(java.lang.String, java.lang.String)
     */
    public Set getEntryNameSet(String domainName, String interfaceName)
    {
        Set localEntryNameSet = super.getEntryNameSet(domainName, interfaceName);
        Set parentEntryNameSet = _parent.getEntryNameSet(domainName, interfaceName);
        Set entryNameSet = localEntryNameSet;
        entryNameSet.addAll(parentEntryNameSet);
        return entryNameSet;
    }

    private static OverwritableRegistry _Singleton = new OverwritableRegistry();
    protected IRegistry _parent;    // the registry overwritten by this registry
}