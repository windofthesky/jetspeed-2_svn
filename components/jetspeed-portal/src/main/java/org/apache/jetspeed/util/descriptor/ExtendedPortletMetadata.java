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
package org.apache.jetspeed.util.descriptor;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.om.common.portlet.CustomPortletMode;
import org.apache.jetspeed.om.common.portlet.CustomWindowState;
import org.apache.jetspeed.om.common.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.impl.CustomPortletModeImpl;
import org.apache.jetspeed.om.portlet.impl.CustomWindowStateImpl;
import org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl;
import org.apache.jetspeed.tools.pamanager.rules.JetspeedServicesRuleSet;
import org.apache.jetspeed.tools.pamanager.rules.MetadataRuleSet;
import org.apache.jetspeed.tools.pamanager.rules.PortletRule;
import org.apache.jetspeed.tools.pamanager.rules.SecurityConstraintRefRule;
import org.apache.jetspeed.tools.pamanager.rules.UserAttributeRefRuleSet;
import org.xml.sax.Attributes;

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
    private static class CollectionRule extends Rule
    {
        private Collection collection;
        
        public CollectionRule(Collection collection)
        {
            this.collection = collection;
        }

        public void begin(String arg0, String arg1, Attributes arg2) throws Exception
        {
            digester.push(collection);
        }

        public void end(String arg0, String arg1) throws Exception
        {
            digester.pop();
        }        
    }
    
    protected final static Log log = LogFactory.getLog(ExtendedPortletMetadata.class);

    protected Reader extendedMetaData;
    protected PortletApplication portletApp;
    
    /**
     * 
     * @param extendedMetaData Reader that contains the extended metadata, usually jetspeed-portlet.xml
     * @param portletApp the PortletApplication we are adding the extended metadata to.
     */
    public ExtendedPortletMetadata( Reader extendedMetaData, PortletApplication portletApp )
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
        try
        {
            Digester digester = new Digester();
            digester.setClassLoader(this.getClass().getClassLoader());
            digester.setValidating(false);
            digester.setNamespaceAware(true);
            digester.push(portletApp);

            digester.addRuleSet(new MetadataRuleSet("portlet-app/"));
            digester.addRuleSet(new JetspeedServicesRuleSet(portletApp));
            digester.addRule("portlet-app/security-constraint-ref", new SecurityConstraintRefRule(portletApp));                        
            
            digester.addRule("portlet-app/portlet/portlet-name", new PortletRule(portletApp));
            digester.addRuleSet(new MetadataRuleSet("portlet-app/portlet/"));
            
            digester.addRule("portlet-app/portlet/security-constraint-ref", new SecurityConstraintRefRule(portletApp));        
            
            digester.addRuleSet(new UserAttributeRefRuleSet(portletApp));
            
            ArrayList mappedPortletModes = new ArrayList();
            digester.addRule("portlet-app/custom-portlet-mode",new CollectionRule(mappedPortletModes));
            digester.addObjectCreate("portlet-app/custom-portlet-mode",CustomPortletModeImpl.class);
            
            digester.addBeanPropertySetter("portlet-app/custom-portlet-mode/name", "customName");
            digester.addBeanPropertySetter("portlet-app/custom-portlet-mode/mapped-name", "mappedName");
            digester.addSetNext("portlet-app/custom-portlet-mode", "add");
            
            ArrayList mappedWindowStates = new ArrayList();
            digester.addRule("portlet-app/custom-window-state",new CollectionRule(mappedWindowStates));
            digester.addObjectCreate("portlet-app/custom-window-state",CustomWindowStateImpl.class);
            
            digester.addBeanPropertySetter("portlet-app/custom-window-state/name", "customName");
            digester.addBeanPropertySetter("portlet-app/custom-window-state/mapped-name", "mappedName");
            digester.addSetNext("portlet-app/custom-window-state", "add");
            
            digester.parse(extendedMetaData);
            
            if (mappedPortletModes.size() > 0)
            {
                PortletApplicationDefinitionImpl pa = (PortletApplicationDefinitionImpl)portletApp;
                ArrayList customModes = new ArrayList(pa.getCustomPortletModes());
                Iterator mappedModesIter = mappedPortletModes.iterator();
                while ( mappedModesIter.hasNext() )
                {
                    CustomPortletModeImpl mappedMode = (CustomPortletModeImpl)mappedModesIter.next();
                    if (!mappedMode.getMappedMode().equals(mappedMode.getCustomMode()))
                    {
                        int index = customModes.indexOf(mappedMode);
                        if ( index > -1 )
                        {
                            CustomPortletMode customMode = (CustomPortletMode)customModes.get(index);
                            mappedMode.setDescription(customMode.getDescription());
                            customModes.set(index,mappedMode);
                        }
                    }
                }
                pa.setCustomPortletModes(customModes);
            }
            if ( mappedWindowStates.size() > 0)
            {
                PortletApplicationDefinitionImpl pa = (PortletApplicationDefinitionImpl)portletApp;
                ArrayList customStates = new ArrayList(pa.getCustomWindowStates());
                Iterator mappedStatesIter = mappedWindowStates.iterator();
                while ( mappedStatesIter.hasNext() )
                {
                    CustomWindowStateImpl mappedState = (CustomWindowStateImpl)mappedStatesIter.next();
                    if (!mappedState.getMappedState().equals(mappedState.getCustomState()))
                    {
                        int index = customStates.indexOf(mappedState);
                        if ( index > -1 )
                        {
                            CustomWindowState customState = (CustomWindowState)customStates.get(index);
                            mappedState.setDescription(customState.getDescription());
                            customStates.set(index,mappedState);
                        }
                    }
                }
                pa.setCustomWindowStates(customStates);
            }
        }
        catch (Throwable t)
        {
            throw new MetaDataException("Unable to marshall extended metadata.  " + t.toString(), t);
        }
    }
}

