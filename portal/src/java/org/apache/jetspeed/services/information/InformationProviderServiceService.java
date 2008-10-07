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
package org.apache.jetspeed.services.information;

import java.util.Map;

import javax.servlet.ServletConfig;

import org.apache.pluto.services.information.InformationProviderService;

/**
 * InformationServiceProviderService
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public interface InformationProviderServiceService extends InformationProviderService
{
    public static final String SERVICE_NAME = "InformationProviderService";

    public abstract void init(ServletConfig config, Map properties) throws Exception;
}