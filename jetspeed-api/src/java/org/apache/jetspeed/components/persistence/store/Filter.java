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
package org.apache.jetspeed.components.persistence.store;

import java.util.Collection;

/**
 * <p>
 * Filter
 * </p>
 * 
 * 
 * @
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $ $
 *
 */
public interface Filter
{
	/**
	 * @see org.apache.ojb.broker.query.Criteria#addBetween(java.lang.String, java.lang.Object, java.lang.Object)
	 */
	public abstract void addBetween(String arg0, Object arg1, Object arg2);

	/**
	 * @see org.apache.ojb.broker.query.Criteria#addEqualTo(java.lang.String, java.lang.Object)
	 */
	public abstract void addEqualTo(String arg0, Object arg1);

	/**
	 * @see org.apache.ojb.broker.query.Criteria#addGreaterOrEqualThan(java.lang.String, java.lang.Object)
	 */
	public abstract void addGreaterOrEqualThan(String arg0, Object arg1);

	/**
	 * @see org.apache.ojb.broker.query.Criteria#addGreaterThan(java.lang.String, java.lang.Object)
	 */
	public abstract void addGreaterThan(String arg0, Object arg1);

	/**
	 * @see org.apache.ojb.broker.query.Criteria#addIn(java.lang.String, java.util.Collection)
	 */      
	public abstract void addIn(String attribute, Collection values);

	/**
	 * @see org.apache.ojb.broker.query.Criteria#addLessOrEqualThan(java.lang.String, java.lang.Object)
	 */
	public abstract void addLessOrEqualThan(String arg0, Object arg1);

	/**
	 * @see org.apache.ojb.broker.query.Criteria#addLike(java.lang.Object, java.lang.Object)
	 */
	public abstract void addLike(Object arg0, Object arg1);

	/**
	 * @see org.apache.ojb.broker.query.Criteria#addNotBetween(java.lang.String, java.lang.Object, java.lang.Object)
	 */
	public abstract void addNotBetween(String arg0, Object arg1, Object arg2);

	/**
	 * @see org.apache.ojb.broker.query.Criteria#addNotEqualTo(java.lang.String, java.lang.Object)
	 */
	public abstract void addNotEqualTo(String arg0, Object arg1);

	/**
	 * @see org.apache.ojb.broker.query.Criteria#addNotLike(java.lang.String, java.lang.Object)
	 */
	public abstract void addNotLike(String arg0, Object arg1);

	/**
	 * @see org.apache.ojb.broker.query.Criteria#addNotNull(java.lang.String)
	 */
	public abstract void addNotNull(String arg0);

	/**
	 * @see org.apache.ojb.broker.query.Criteria#addOrCriteria(org.apache.ojb.broker.query.Criteria)
	 */
	public abstract void addOrFilter(Filter arg0);

	/**
	 * @see org.apache.ojb.broker.query.Criteria#addOrderByAscending(java.lang.String)
	 */
	public abstract void addOrderByAscending(String arg0);

	/**
	 * @see org.apache.ojb.broker.query.Criteria#addOrderByDescending(java.lang.String)
	 */
	public abstract void addOrderByDescending(String arg0);



	

}
