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
package org.apache.jetspeed.pipeline.valve;

/**
 * Return all recycle objects in the ValveContext.  This includes
 * returning poolable object.
 *
 * <br/>
 * Read from the ValveContext:
 * <ul>
 * </ul>
 *
 * <br/>
 * Written into the ValveContext:
 * <ul>
 * </ul>
 *
 * <br>
 * Note: The primary purpose of this interface is primary for documention.
 * 
 * @author <a href="mailto:paul@apache.org">Paul Spencer</a>
 * @version $Id: CleanupValve.java 186726 2004-06-05 05:13:09Z taylor $
 *
 * @see ValveContext
 */
public interface CleanupValve extends Valve
{
}
