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
package org.apache.jetspeed.util.descriptor;

import java.io.Reader;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.impl.LanguageImpl;
import org.apache.jetspeed.om.impl.ParameterDescriptionImpl;
import org.apache.jetspeed.om.impl.PortletDescriptionImpl;
import org.apache.jetspeed.om.impl.PortletDisplayNameImpl;
import org.apache.jetspeed.om.impl.PortletInitParameterImpl;
import org.apache.jetspeed.om.impl.SecurityRoleRefDescriptionImpl;
import org.apache.jetspeed.om.impl.SecurityRoleRefImpl;
import org.apache.jetspeed.om.impl.UserAttributeImpl;
import org.apache.jetspeed.om.portlet.impl.ContentTypeImpl;
import org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl;
import org.apache.jetspeed.tools.pamanager.PortletApplicationException;
import org.apache.pluto.om.common.SecurityRoleRef;
import org.apache.pluto.om.common.SecurityRoleRefSet;
import org.apache.pluto.om.common.SecurityRoleSet;
import org.apache.pluto.om.portlet.PortletDefinition;

/**
 * 
 * Object used to perform operation upon a portlet application descriptor,
 * usually, portlet.xml.
 *
 * @author <a href="mailto:roger.ruttimann@earthlink.net">Roger Ruttimann</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a> 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 *  
 * @version $Id$
 */
public class PortletApplicationDescriptor
{
    protected final static Log log = LogFactory.getLog(PortletApplicationDescriptor.class);
    protected Reader portletXmlReader;
    private String appName;

   
    public PortletApplicationDescriptor(Reader portletXmlReader, String appName)
    {
        this.portletXmlReader = portletXmlReader;
        this.appName = appName;
    }

    /**
     * Maps the content of the portlet application descriptor into
     * a new <code>MutablePortletApplication object</code>
     * 
     * @return MutablePortletApplication newly created MutablePortletApplication with
     * all values of the portlet application descriptor mapped into it.
     */
    public MutablePortletApplication createPortletApplication()
        throws PortletApplicationException
    {
        try
        {
            // TODO move config to digester-rules.xml. Example: http://www.onjava.com/pub/a/onjava/2002/10/23/digester.html?page=3
            Digester digester = new Digester();
            digester.setValidating(false);
                       
            // digester.addRuleSet(new PortletApplicationRuleSet(appName));
            
            digester.addRule("portlet-app", new PortletApplicationRule(appName));
            digester.addSetProperties("portlet-app", "id", "applicationIdentifier");


            digester.addRule("portlet-app/portlet", new PortletRule());
            
            digester.addSetProperties("portlet-app/portlet", "id", "portletIdentifier");
            digester.addBeanPropertySetter("portlet-app/portlet/portlet-name", "name");
            digester.addBeanPropertySetter("portlet-app/portlet/portlet-class", "className");
            digester.addBeanPropertySetter("portlet-app/portlet/expiration-cache", "expirationCache");
           
            
            digester.addObjectCreate("portlet-app/portlet/display-name", PortletDisplayNameImpl.class);
            digester.addSetProperties("portlet-app/portlet/display-name", "lang", "language");
            digester.addBeanPropertySetter("portlet-app/portlet/display-name", "displayName");
            digester.addSetNext("portlet-app/portlet/display-name", "addDisplayName");

            digester.addObjectCreate("portlet-app/portlet/description", PortletDescriptionImpl.class);
            digester.addSetProperties("portlet-app/portlet/description", "lang", "language");
            digester.addBeanPropertySetter("portlet-app/portlet/description", "description");
            digester.addSetNext("portlet-app/portlet/description", "addDescription");

            digester.addObjectCreate("portlet-app/portlet/init-param", PortletInitParameterImpl.class);
            digester.addBeanPropertySetter("portlet-app/portlet/init-param/name", "name");
            digester.addBeanPropertySetter("portlet-app/portlet/init-param/value", "value");
            digester.addSetNext("portlet-app/portlet/init-param", "addInitParameter");

            digester.addObjectCreate("portlet-app/portlet/init-param/description", ParameterDescriptionImpl.class);
            digester.addSetProperties("portlet-app/portlet/init-param/description", "lang", "language");
            digester.addBeanPropertySetter("portlet-app/portlet/init-param/description", "description");
            digester.addSetNext("portlet-app/portlet/init-param/description", "addDescription");

            digester.addObjectCreate("portlet-app/portlet/supports", ContentTypeImpl.class);
            digester.addBeanPropertySetter("portlet-app/portlet/supports/mime-type", "contentType");
            digester.addCallMethod("portlet-app/portlet/supports/portlet-mode", "addPortletMode", 0);
            digester.addSetNext("portlet-app/portlet/supports", "addContentType");

            digester.addObjectCreate("portlet-app/portlet/portlet-info", LanguageImpl.class);
            digester.addBeanPropertySetter("portlet-app/portlet/portlet-info/title", "title");
            digester.addBeanPropertySetter("portlet-app/portlet/portlet-info/short-title", "shortTitle");
            digester.addCallMethod("portlet-app/portlet/portlet-info/keywords", "setKeywords", 0, new Class[]{String.class});
            digester.addSetNext("portlet-app/portlet/portlet-info", "addLanguage");
            
            digester.addRuleSet(new PortletPreferenceRuleSet());

            
            digester.addObjectCreate("portlet-app/user-attribute", UserAttributeImpl.class);
            digester.addBeanPropertySetter("portlet-app/user-attribute/description", "description");
            digester.addBeanPropertySetter("portlet-app/user-attribute/name", "name");
            digester.addSetNext("portlet-app/user-attribute", "addUserAttribute");
            
            digester.addObjectCreate("portlet-app/portlet/security-role-ref", SecurityRoleRefImpl.class);
            digester.addBeanPropertySetter("portlet-app/portlet/security-role-ref/role-name", "roleName");
            digester.addBeanPropertySetter("portlet-app/portlet/security-role-ref/role-link", "roleLink");
            digester.addSetNext("portlet-app/portlet/security-role-ref", "addSecurityRoleRef");

            digester.addObjectCreate("portlet-app/portlet/security-role-ref/description", SecurityRoleRefDescriptionImpl.class);
            digester.addSetProperties("portlet-app/portlet/security-role-ref/description", "lang", "language");
            digester.addBeanPropertySetter("portlet-app/portlet/security-role-ref/description", "description");
            digester.addSetNext("portlet-app/portlet/security-role-ref/description", "addDescription");
            
            PortletApplicationDefinitionImpl pd = (PortletApplicationDefinitionImpl) digester.parse(portletXmlReader);

           
            if(pd.getApplicationIdentifier() == null)
            {
                pd.setApplicationIdentifier(appName);
            }
            
            Iterator portletDefs = pd.getPortletDefinitions().iterator();
            while(portletDefs.hasNext())
            {
                PortletDefinitionComposite portletDef = (PortletDefinitionComposite) portletDefs.next();
                if(portletDef.getPortletIdentifier() == null)
                {
                    portletDef.setPortletIdentifier(portletDef.getName());
                }
            }
            
            return pd;

        }
        catch (Throwable t)
        {
            t.printStackTrace();
            String msg = "Could not unmarshal portlet.xml. " + t.toString();
            log.error(msg, t);
            throw new PortletApplicationException(msg, t);
        }
    }
    


    /**
     * Validate a PortletApplicationDefinition tree AFTER its
     * WebApplicationDefinition has been loaded. Currently, only the security
     * role references of the portlet definitions are validated:
     * <ul>
     * <li>A security role reference should reference a security role through a
     * roleLink. A warning message is logged if a direct reference is used.
     * <li>For a security role reference a security role must be defined in the
     * web application. An error message is logged and a
     * PortletApplicationException is thrown if not.
     * </ul>
     * 
     * @param app
     *            The PortletApplicationDefinition to validate
     * @throws PortletApplicationException
     */
    public void validate(MutablePortletApplication app)
            throws PortletApplicationException
    {
        SecurityRoleSet roles = app.getWebApplicationDefinition()
                .getSecurityRoles();
        Collection portlets = app.getPortletDefinitions();
        Iterator portletIterator = portlets.iterator();
        while (portletIterator.hasNext())
        {
            PortletDefinition portlet = (PortletDefinition) portletIterator
                    .next();
            SecurityRoleRefSet securityRoleRefs = portlet
                    .getInitSecurityRoleRefSet();
            Iterator roleRefsIterator = securityRoleRefs.iterator();
            while (roleRefsIterator.hasNext())
            {
                SecurityRoleRef roleRef = (SecurityRoleRef) roleRefsIterator
                        .next();
                String roleName = roleRef.getRoleLink();
                if (roleName == null || roleName.length() == 0)
                {
                    roleName = roleRef.getRoleName();
                }
                if (roles.get(roleName) == null)
                {
                    String errorMsg = "Undefined security role " + roleName
                            + " referenced from portlet " + portlet.getName();
                    log.error(errorMsg);
                    throw new PortletApplicationException(errorMsg);
                }
            }
        }
    }
}
