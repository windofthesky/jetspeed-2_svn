/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.cache.impl;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

public class EhCacheEventListener implements CacheEventListener
{

	public Object clone() throws CloneNotSupportedException
	{
		return null;
	}


	public void dispose()
	{
		// TODO Auto-generated method stub

	}

	public void notifyElementEvicted(Ehcache cache, Element arg1)
	{
//		System.out.println("notifyElementEvicted cache=" + cache.getName() + " - element = " + arg1.getObjectKey().toString());

	}

	public void notifyElementExpired(Ehcache cache, Element arg1)
	{
//		System.out.println("notifyElementExpired cache=" + cache.getName() + " - element = " + arg1.getObjectKey().toString());

	}

	public void notifyElementPut(Ehcache cache, Element arg1)
			throws CacheException
	{
//		System.out.println("notifyElementPut cache=" + cache.getName() + " - element = " + arg1.getObjectKey().toString());

	}

	public void notifyElementRemoved(Ehcache cache, Element arg1)
			throws CacheException
	{
//		System.out.println("notifyElementRemoved cache=" + cache.getName() + " - element = " + arg1.getObjectKey().toString());

	}

	public void notifyElementUpdated(Ehcache cache, Element arg1)
			throws CacheException
	{
//		System.out.println("notifyElementUpdated cache=" + cache.getName() + " - element = " + arg1.getObjectKey().toString());
	}

	public void notifyRemoveAll(Ehcache cache)
	{
//		System.out.println("notifyRemoveAll cache=" + cache.getName() );
	}

}
