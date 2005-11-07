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

import org.apache.jetspeed.security.impl.SecurityValveImpl;

/**
 * Authenticates the User or redirects to Login if necessary, add
 * authenticated Subject to RequestContext
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
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 * 
 * @see ValveContext
 */
public interface SecurityValve extends Valve
{
    final String IP_ADDRESS = SecurityValveImpl.class.getName() + ".ipaddress";    
}
