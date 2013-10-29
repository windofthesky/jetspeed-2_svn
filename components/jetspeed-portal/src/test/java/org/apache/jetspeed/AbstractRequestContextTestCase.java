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
package org.apache.jetspeed;


import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.components.util.TransactionCacheEnabledSpringTestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AbstractRequestContextTestCase extends TransactionCacheEnabledSpringTestCase
{
    protected PortletRegistry portletRegistry;

    protected void setUp() throws Exception
    {       
        super.setUp();
        portletRegistry = scm.lookupComponent("portletRegistry");
    }   
    
    protected String[] getConfigurations()
    {
        String[] confs = super.getConfigurations();
        List<String> confList = new ArrayList<String>(Arrays.asList(confs));
        confList.add("jetspeed-base.xml");
        confList.add("jetspeed-properties.xml");
        confList.add("page-manager.xml");
        confList.add("registry.xml");
        confList.add("search.xml");
        confList.add("JETSPEED-INF/spring/RequestDispatcherService.xml");        
        confList.add("rc2.xml");
        confList.add("static-bean-references.xml");
        confList.add("security-managers.xml");
        confList.add("security-providers.xml");
        confList.add("security-spi.xml");
        confList.add("security-atn.xml");
        confList.add("security-spi-atn.xml");
        confList.add("security-atz.xml");
        confList.add("JETSPEED-INF/spring/JetspeedPrincipalManagerProviderOverride.xml");
        return confList.toArray(new String[confList.size()]);
    }

    protected String getBeanDefinitionFilterCategories()
    {
        return super.getBeanDefinitionFilterCategories()+",xmlPageManager,security,dbSecurity";
    }
}