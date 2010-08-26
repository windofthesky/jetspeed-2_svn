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
package org.apache.jetspeed.page;

import java.util.List;

import org.apache.jetspeed.om.page.BaseFragmentElement;
import org.apache.jetspeed.om.page.FragmentProperty;

public interface FragmentPropertyList extends List
{
	/* (non-Javadoc)
	 * @see java.util.List#add(int,java.lang.Object)
	 */
	public abstract void add(int index, Object element);

	/* (non-Javadoc)
	 * @see java.util.List#get(int)
	 */
	public abstract Object get(int index);

	/* (non-Javadoc)
	 * @see java.util.List#remove(int)
	 */
	public abstract Object remove(int index);

	/* (non-Javadoc)
	 * @see java.util.List#set(int,java.lang.Object)
	 */
	public abstract Object set(int index, Object element);

	/* (non-Javadoc)
	 * @see java.util.List#size()
	 */
	public abstract int size();

	/**
	 * Get fragment property list owner.
	 * 
	 * @return fragment owner
	 */
	public abstract BaseFragmentElement getFragmentElement();

	/**
	 * Get underlying fragment properties list.
	 * 
	 * @return fragment property list
	 */
	public abstract List getProperties();

	/**
	 * Get underlying removed fragment properties list.
	 * 
	 * @return removed fragment property list
	 */
	public abstract List getRemovedProperties();

	/**
	 * Find matching property.
	 * 
	 * @param match match property
	 * @return matching property
	 */
	public abstract FragmentProperty getMatchingProperty(
			FragmentProperty match);

	/**
	 * Clear all transient properties.
	 */
	public abstract void clearProperties();

}