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

import org.apache.commons.digester.Rule;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;

/**
 * This class helps load the portlet's metadata onto the digester stack
 * 
 * @author <a href="mailto:jford@apache.org">Jeremy Ford </a>
 * @version $Id: PortletRule.java 186744 2004-06-08 01:36:30Z dlestrat $
 */
public class PortletRule extends Rule
{
    protected final static Log log = LogFactory.getLog(PortletRule.class);

    private MutablePortletApplication app;

    public PortletRule(MutablePortletApplication app)
    {
        this.app = app;
    }

    public void body(String namespace, String name, String text) throws Exception
    {
        log.debug("Found portlet name " + name);
        PortletDefinitionComposite def = (PortletDefinitionComposite) app.getPortletDefinitionByName(text);
        digester.push(def);
    }
}