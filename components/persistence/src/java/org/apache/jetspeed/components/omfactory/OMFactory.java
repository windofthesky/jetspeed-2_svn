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
package org.apache.jetspeed.components.omfactory;

/**
 * <p>
 * OMDFactory
 * </p>
 * 
 * 
 * @
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $ $
 *
 */
public interface OMFactory
{
		
	Object newInstance(Class interfase) throws InstantiationException, IllegalAccessException, ClassNotFoundException;
	
	/**
	 * 
	 * <p>
	 * newInstance
	 * </p>
	 * <p>
	 *  Use this is if an interface has more than one implementation
	 * </p>
	 * 
	 * @param key
	 * @return
	 *
	 */
	Object newInstance(String key) throws  InstantiationException, IllegalAccessException, ClassNotFoundException;
	
	Class getImplementation(Class interfase) throws ClassNotFoundException;
	
	Class getImplementation(String key) throws ClassNotFoundException;

}
