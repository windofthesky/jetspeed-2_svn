/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.jetspeed.util.descriptor;

import java.io.Reader;

import org.apache.commons.digester.Digester;
import org.apache.jetspeed.om.common.servlet.MutableWebApplication;
import org.apache.jetspeed.om.servlet.impl.SecurityRoleImpl;
import org.apache.jetspeed.om.servlet.impl.WebApplicationDefinitionImpl;
import org.apache.jetspeed.tools.pamanager.PortletApplicationException;
import org.apache.jetspeed.util.JetspeedLocale;

/**
 * Utilities for manipulating the web.xml deployment descriptor
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma </a>*
 * @version $Id: WebDescriptorUtilities.java,v 1.2 2004/05/12 22:25:04 taylor
 *                Exp $
 */
public class WebApplicationDescriptor
{

    protected Reader webXmlReader;
    protected String contextRoot;
    public WebApplicationDescriptor(Reader webXmlReader, String contextRoot )
    {
        if(webXmlReader == null)
        {
            throw new IllegalArgumentException("webXmlReader cannot be null");
        }
        this.webXmlReader = webXmlReader;
        this.contextRoot = contextRoot;
    }
    

    /**
     * Load a web.xml file into a Web Application tree
     * 
     * @param pathWebXML
     *                  The path to the web.xml file
     * @param contexRoot
     *                  The context root of the web application
     * @param locale
     *                  The locale of the display name of the web application
     * @param displayName
     *                  The display name of the web application
     * @return The Java object tree representing web.xml
     */
    public MutableWebApplication createWebApplication() throws PortletApplicationException
    {
        try
        {

            // TODO move config to digester-rules.xml. Example:
            // http://www.onjava.com/pub/a/onjava/2002/10/23/digester.html?page=3
            Digester digester = new Digester();
            digester.setClassLoader(this.getClass().getClassLoader());
            digester.setValidating(false);
  
            digester.register("-//Sun Microsystems, Inc.//DTD Web Application 2.2//EN", WebApplicationDescriptor.class
                    .getResource("web-app_2_2.dtd").toString());
            digester.register("-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN", WebApplicationDescriptor.class
                    .getResource("web-app_2_3.dtd").toString());
                    
            digester.addObjectCreate("web-app", WebApplicationDefinitionImpl.class);

            digester.addObjectCreate("web-app/security-role", SecurityRoleImpl.class);
            digester.addBeanPropertySetter("web-app/security-role/description", "description");
            digester.addBeanPropertySetter("web-app/security-role/role-name", "roleName");
            digester.addSetNext("web-app/security-role", "addSecurityRole");

            WebApplicationDefinitionImpl wd = (WebApplicationDefinitionImpl) digester.parse(webXmlReader);

            wd.setContextRoot(contextRoot);
            //wd.addDescription(locale, displayName);
            wd.addDescription(JetspeedLocale.getDefaultLocale(), contextRoot);
            return wd;

        }
        catch (Throwable t)
        {            
            String msg = "Could not digester web.xml." + t.toString();            
            throw new PortletApplicationException(msg, t);
        }
    }

}
