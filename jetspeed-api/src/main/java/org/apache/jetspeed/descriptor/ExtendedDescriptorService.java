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
package org.apache.jetspeed.descriptor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.pluto.spi.optional.PortletAppDescriptorService;


/**
 * Extends Pluto Descriptor service for loading portlet applications in a Jetspeed format.
 * Additionally, has two APIs to load extended Jetspeed descriptor information (jetspeed-portlet.xml) 
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public interface ExtendedDescriptorService extends PortletAppDescriptorService
{
    /**
     * Create a new portlet application definition
     */
    PortletApplication createPortletApplicationDefinition();
    
    /**
     * Retrieve the PortletApp deployment descriptor
     * (portlet.xml).
     * @return Object representation of the descriptor.
     * @throws IOException if an IO error occurs.
     */
    PortletApplication read(InputStream in) throws IOException;

    /**
     * Write the PortletApp deployment descriptor
     * (portlet.xml).
     * @param portletDescriptor
     * @param out
     * @throws IOException if an IO error occurs.
     */
    void write(PortletApplication portletDescriptor, OutputStream out) throws IOException;

    void readExtended(InputStream in, PortletApplication app) throws IOException;
}
