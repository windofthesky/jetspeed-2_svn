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
import org.apache.jetspeed.om.common.JetspeedServiceReference;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.impl.JetspeedServiceReferenceImpl;


/**
 * This class helps load the jetspeed portlet extension service declarations.
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 */
public class JetspeedServiceRule extends Rule
{
    private MutablePortletApplication app;

    public JetspeedServiceRule(MutablePortletApplication app)
    {
        this.app = app;

    }

    public void end(String namespace, String name) throws Exception
    {
        Object o = digester.peek(0);
        System.out.println("namespace = " + namespace);
        System.out.println("name = " + name);
        System.out.println("object = " + o);
        
        JetspeedServiceReference service = (JetspeedServiceReference) digester.peek(0);
        System.out.println("service = " + service);
        
        app.addJetspeedService(service);
    }

}
