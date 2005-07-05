/*
 * Copyright 2004 The Apache Software Foundation.
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
package org.apache.jetspeed.tools.pamanager.rules;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.impl.UserAttributeRefImpl;

/**
 * This class helps load the jetspeed portlet extension user attributes.
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class UserAttributeRefRuleSet extends RuleSetBase
{
    protected final static Log log = LogFactory.getLog(UserAttributeRefRuleSet.class);

    private MutablePortletApplication app;

    public UserAttributeRefRuleSet(MutablePortletApplication app)
    {
        this.app = app;
    }

    /**
     * @see org.apache.commons.digester.RuleSet#addRuleInstances(org.apache.commons.digester.Digester)
     */
    public void addRuleInstances(Digester digester)
    {       
        digester.addObjectCreate("portlet-app/user-attribute-ref", UserAttributeRefImpl.class);
        digester.addBeanPropertySetter("portlet-app/user-attribute-ref/name", "name");
        digester.addBeanPropertySetter("portlet-app/user-attribute-ref/name-link", "nameLink");
        digester.addRule("portlet-app/user-attribute-ref", new UserAttributeRefRule(app));
    }

}