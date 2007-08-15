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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.jetspeed.components.util.RegistrySupportedTestCase;

public class AbstractRequestContextTestCase extends RegistrySupportedTestCase
{

    protected String[] getConfigurations()
    {
//        File webapp = new File("../../src/webapp");
//        System.setProperty(JetspeedEngineConstants.APPLICATION_ROOT_KEY, webapp.getAbsolutePath());        
        String[] confs = super.getConfigurations();
        List confList = new ArrayList(Arrays.asList(confs));
        confList.add("rc2.xml");
        return (String[]) confList.toArray(new String[confList.size()]);
    }
}