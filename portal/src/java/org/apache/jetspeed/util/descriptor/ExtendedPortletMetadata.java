/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.tools.pamanager.rules.MetadataRuleSet;
import org.apache.jetspeed.tools.pamanager.rules.PortletRule;
import org.apache.jetspeed.tools.pamanager.rules.UserAttributeRefRuleSet;

/**
 * This class is used to load extended MetaData, like that of the Dublin Core, 
 * into an exsting PortletApplicationDefinition's object graph.
 * 
 * @author <a href="mailto:jford@apache.org">Jeremy Ford </a>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id: JetspeedDescriptorUtilities.java,v 1.10 2004/06/08 01:35:01
 *                dlestrat Exp $
 */
public class ExtendedPortletMetadata
{
    protected final static Log log = LogFactory.getLog(ExtendedPortletMetadata.class);

    protected Reader extendedMetaData;
    protected MutablePortletApplication portletApp;
    
    /**
     * 
     * @param extendedMetaData Reader that contains the extended metadata, usually jetspeed-portlet.xml
     * @param portletApp the MutablePortletApplication we are adding the extended metadata to.
     */
    public ExtendedPortletMetadata( Reader extendedMetaData, MutablePortletApplication portletApp )
    {
        this.extendedMetaData = extendedMetaData;
        this.portletApp = portletApp;
    }

    /**
     * Performs the actual loading and mapping of the metadata into the PortletApplicationDefinition.
     * 
     */
    public void load() throws MetaDataException
    {
        boolean result = false;
        try
        {
            Digester digester = new Digester();
            digester.setValidating(false);
            digester.setNamespaceAware(true);
            digester.push(portletApp);

            digester.addRuleSet(new MetadataRuleSet("portlet-app/"));
            digester.addRule("portlet-app/portlet/portlet-name", new PortletRule(portletApp));
            digester.addRuleSet(new MetadataRuleSet("portlet-app/portlet/"));

            digester.addRuleSet(new UserAttributeRefRuleSet(portletApp));

            digester.parse(extendedMetaData);

            result = true;
        }
        catch (Throwable t)
        {
            throw new MetaDataException("Unable to marshall extended metadata.  " + t.toString(), t);
        }
    }
}

