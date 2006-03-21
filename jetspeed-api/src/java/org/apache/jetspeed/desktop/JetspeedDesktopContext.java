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
package org.apache.jetspeed.desktop;

/**
 * Jetspeed Desktop 
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public interface JetspeedDesktopContext
{
    String DESKTOP_ATTRIBUTE = "jetspeedDesktop";
    
    /**
     * Get an absolute portal from a relative URL.
     * This request adds in the servlet path such as "/desktop"
     * 
     * @param relativePath
     * @return The full portal URL from a relative path
     */     
    String getPortalUrl(String relativePath);
    
    /**
     * Gets an absolute resource URL to a portal resource, minus the servlet path
     * 
     * @param relativePath
     * @return
     */
    String getPortalResourceUrl(String relativePath);
    
}
