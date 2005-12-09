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
 * Determine the page to display and add it to the RequestContext
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
 * @version $Id$
 *
 * @see ValveContext
 */
public interface PageProfilerValve extends Valve
{
    String PROFILE_LOCATOR_REQUEST_ATTR_KEY = "org.apache.jetspeed.profiler.ProfileLocator";
    String PROFILE_LOCATORS_PER_PRINCIPAL = "org.apache.jetspeed.profiler.ProfileLocatorsPrincipal";
}
