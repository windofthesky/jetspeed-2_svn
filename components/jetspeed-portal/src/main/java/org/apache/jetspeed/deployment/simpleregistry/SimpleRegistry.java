/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
/**
 * Created on Jan 13, 2004
 *
 * 
 * @author
 */
package org.apache.jetspeed.deployment.simpleregistry;

import java.util.Collection;

/**
 * <p>
 * SimpleRegistry
 * </p>
 * <p>
 *   This is an interface for creating simple registry systems.  A good example would be an
 *   in memory registry that gets populate at runtime and is lost on shutdown.
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public interface SimpleRegistry
{
	/**
	 * Registers the entry.
	 * 
	 * <code>entry.getId()</code> is null
	 * @throws SimpleRegistryException if this <code>entry</code> is
	 * already registered.
	 * @param entry
	 */
	public void register(Entry entry) throws SimpleRegistryException;
	
	/**
	 * De-registers the entry
	 * @param entry
	 * <code>entry.getId()</code> is null
	 */
	public void deRegister(Entry entry);
	
	/**
	 * Verifies whether or not this entry is registered.
	 * @param entry
	 * 
	 * @return boolean <code>true</code> is the <code>entry</code> is registered
	 * otherwise <code>false</code>.
	 * <code>entry.getId()</code> is null
	 */
	public boolean isRegistered(Entry entry);
	
	/**
	 * Provides a Collection of <code>org.apache.jetspeed.cps.simpleregistry.Entry</code>
	 * objects that are currently registered to this registry
	 * @return
	 */	
	public Collection getRegistry();

}
