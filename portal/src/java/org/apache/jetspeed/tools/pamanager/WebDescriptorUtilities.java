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
package org.apache.jetspeed.tools.pamanager;

import java.io.FileReader;
import java.util.Locale;

import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.om.common.servlet.MutableWebApplication;
import org.apache.jetspeed.om.servlet.impl.SecurityRoleImpl;
import org.apache.jetspeed.om.servlet.impl.WebApplicationDefinitionImpl;

/**
 * Utilities for manipulating the web.xml deployment descriptor
 *
 * @author <a href="mailto:ate@douma.nu">Ate Douma </a>*
 * @version $Id$
 */
public class WebDescriptorUtilities {

    protected final static Log log = LogFactory.getLog(WebDescriptorUtilities.class);

    /**
     * Load a web.xml file into a Web Application tree
     *
     * @param pathWebXML
     *            The path to the web.xml file
     * @param contexRoot
     *            The context root of the web application
     * @param locale
     *            The locale of the display name of the web application
     * @param displayName
     *            The display name of the web application
     * @return The Java object tree representing web.xml
     */
    public static MutableWebApplication loadDescriptor(String pathWebXML, String contextRoot, Locale locale, String displayName)
            throws PortletApplicationException {
        try {

            FileReader reader = new java.io.FileReader(pathWebXML);

            // TODO move config to digester-rules.xml. Example:
            // http://www.onjava.com/pub/a/onjava/2002/10/23/digester.html?page=3
            Digester digester = new Digester();
            digester.setValidating(false);

            digester.register
            ("-//Sun Microsystems, Inc.//DTD Web Application 2.2//EN",
                      WebDescriptorUtilities.class.getResource("web-app_2_2.dtd").toString());                      
            digester.register
              ("-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN",
                      WebDescriptorUtilities.class.getResource("web-app_2_3.dtd").toString());                      
            digester.addObjectCreate("web-app", WebApplicationDefinitionImpl.class);

            digester.addObjectCreate("web-app/security-role", SecurityRoleImpl.class);
            digester.addBeanPropertySetter("web-app/security-role/description", "description");
            digester.addBeanPropertySetter("web-app/security-role/role-name", "roleName");
            digester.addSetNext("web-app/security-role", "addSecurityRole");

            WebApplicationDefinitionImpl wd = (WebApplicationDefinitionImpl) digester.parse(reader);

            wd.setContextRoot(contextRoot);
            wd.addDescription(locale, displayName);
            return wd;

        } catch (Throwable t) {
            t.printStackTrace();
            String msg = "Could not digest \"" + pathWebXML + "\".  " + t.toString();
            log.error(msg, t);
            throw new PortletApplicationException(msg, t);
        }
    }
}