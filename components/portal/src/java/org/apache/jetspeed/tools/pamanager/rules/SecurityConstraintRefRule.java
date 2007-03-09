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
package org.apache.jetspeed.tools.pamanager.rules;

import org.apache.commons.digester.Rule;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;

/**
 * This class helps load the portlet's metadata onto the digester stack
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class SecurityConstraintRefRule extends Rule
{
    private MutablePortletApplication app = null;

    public SecurityConstraintRefRule(MutablePortletApplication app)
    {
        this.app = app;
    }
    
    public void body(String namespace, String name, String text) throws Exception
    {
        Object obj = digester.peek();
        if (obj instanceof MutablePortletApplication)
        {
           ((MutablePortletApplication) obj).setJetspeedSecurityConstraint(text);
        }
        else if (obj instanceof PortletDefinitionComposite)
        {
            ((PortletDefinitionComposite) obj).setJetspeedSecurityConstraint(text);
        }
    }
    
}