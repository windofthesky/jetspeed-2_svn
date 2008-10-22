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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jetspeed.om.portlet.Description;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.UserAttributeRef;

/**
 * This class helps load the jetspeed portlet extension user attributes.
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class UserAttributeRefRule extends Rule
{
    protected final static Log log = LogFactory.getLog(UserAttributeRefRule.class);

    private PortletApplication app;

    public UserAttributeRefRule(PortletApplication app)
    {
        this.app = app;
    }

    public void end(String namespace, String name) throws Exception
    {
        UserAttributeRef uar1 = (UserAttributeRef) digester.peek(0);
        UserAttributeRef uar2 = app.addUserAttributeRef(uar1.getName());
        if (uar1.getNameLink() != null)
        {
            uar2.setNameLink(uar1.getNameLink());
        }
        if (uar1.getDescriptions() != null)
        {
            for (Description d : uar1.getDescriptions())
            {
                Description target = uar2.addDescription(d.getLang());
                target.setDescription(d.getDescription());
            }
        }
    }
}