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

import org.apache.jetspeed.components.portletregistry.RegistryException;
import org.apache.jetspeed.util.FileSystemHelper;


/**
 * PortletApplicationManagement
 *
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public interface PortletApplicationManagement
{
  public static final String LOCAL_PA_PREFIX = "jetspeed-";
  
  void startPortletApplication(String contextName, FileSystemHelper warStruct, ClassLoader paClassLoader) throws RegistryException;
  void stopPortletApplication(String contextName) throws RegistryException;
  void startLocalPortletApplication(String contextName, FileSystemHelper warStruct, ClassLoader paClassLoader) throws RegistryException;
  void stopLocalPortletApplication(String contextName) throws RegistryException;
  public void unregisterPortletApplication(String paName) throws RegistryException;
}
