/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.tools.pamanager;

import javax.servlet.ServletContext;

import org.apache.jetspeed.exception.RegistryException;
import org.apache.jetspeed.util.FileSystemHelper;


/**
 * Jetspeed Deployment Registration
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 */
public interface DeploymentRegistration
{
    boolean registerPortletApplication(
            FileSystemHelper fileSystem, 
            String portletApplicationName)
        throws RegistryException;    
    
    boolean registerPortletApplication(String portletApplicationName, String contextName, ServletContext context)
        throws RegistryException;
    
}
