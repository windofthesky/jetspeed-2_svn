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
package org.apache.jetspeed;

/**
 * PortalReservedParameters. The constants here define HTTP request parameters 
 * reserved for use by the Jetspeed Portal.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public final class PortalReservedParameters
{
    public final static String PORTLET = "portlet";
    public final static String PORTLET_ENTITY = "entity";        
    public final static String PAGE = "page";
    public final static String PIPELINE = "pipeline";
    public final static String DEFAULT_PIPELINE = "jetspeed-pipeline";
    public final static String PORTLET_PIPELINE = "portlet-pipeline";
    public final static String ACTION_PIPELINE = "action-pipeline";
    
    // Session and Request attribute keys
    public static final String PAGE_ATTRIBUTE_KEY = "org.apache.jetspeed.Page";
}
