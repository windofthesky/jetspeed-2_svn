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