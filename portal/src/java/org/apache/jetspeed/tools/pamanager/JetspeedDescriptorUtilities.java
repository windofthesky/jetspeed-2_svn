/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.tools.pamanager;

import java.io.FileReader;

import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.tools.pamanager.rules.MetadataRuleSet;
import org.apache.jetspeed.tools.pamanager.rules.PortletRule;

/**
 * This class helps load Jetspeed descriptors
 *
 * @author <a href="mailto:jford@apache.org">Jeremy Ford</a>
 * @version $Id$
 */
public class JetspeedDescriptorUtilities
{
    protected final static Log log = LogFactory.getLog(JetspeedDescriptorUtilities.class);
    /**
     * Load a portlet.xml file into a Portlet Application tree
     *
     * @param pathPortletXML The path to the portlet.xml file
     * @return Application The Java object tree representing portlet.xml
     */
    public static boolean loadPortletDescriptor(String pathPortletXML, MutablePortletApplication app)
    throws PortletApplicationException
    {
        boolean result = false;
        try
        {
            log.info("Loading Jetspeed descriptor");
            FileReader reader = new java.io.FileReader(pathPortletXML);
        
            Digester digester = new Digester();
            //digester.setLogger(log);
            digester.setValidating(false);
            digester.setNamespaceAware(true);
            digester.push(app.getMetadata());
            
            digester.addRuleSet(new MetadataRuleSet("portlet-app/"));
            /*
            digester.addRule("portlet-app/title", new LocalizedFieldRule());
            digester.addRule("portlet-app/contributor", new LocalizedFieldRule());
            digester.addRule("portlet-app/creator", new LocalizedFieldRule());
            digester.addRule("portlet-app/coverage", new LocalizedFieldRule());
            digester.addRule("portlet-app/description", new LocalizedFieldRule());
            digester.addRule("portlet-app/format", new LocalizedFieldRule());
            digester.addRule("portlet-app/identifier", new LocalizedFieldRule());
            digester.addRule("portlet-app/language", new LocalizedFieldRule());
            digester.addRule("portlet-app/publisher", new LocalizedFieldRule());
            digester.addRule("portlet-app/relation", new LocalizedFieldRule());
            digester.addRule("portlet-app/right", new LocalizedFieldRule());
            digester.addRule("portlet-app/source", new LocalizedFieldRule());
            digester.addRule("portlet-app/subject", new LocalizedFieldRule());
            digester.addRule("portlet-app/type", new LocalizedFieldRule());
            digester.addRule("portlet-app/metadata", new LocalizedFieldRule());
            */
            
            //	digester.addSetNext("portlet-app", "setMetadata");
            
			//digester.addRule("portlet-app/portlet", new PortletRule(app));
            
            /*
	            This will continually push an object onto the digester stack it will never pop it off.
	            The only negative effect is increased memory/stack size.  
	            
	            This is not a problem because the rules will work with the last object on the stack
	            to pop the object off the stack, a new rule mapped to portlet-app/portlet would
	            need to be added that all it did was pop the current object at the portlet end element
            */
			digester.addRule("portlet-app/portlet/portlet-name", new PortletRule(app));
			
			/*
			digester.addRule("portlet-app/portlet/title", new LocalizedFieldRule());
			digester.addRule("portlet-app/portlet/contributor", new LocalizedFieldRule());
			digester.addRule("portlet-app/portlet/creator", new LocalizedFieldRule());
			digester.addRule("portlet-app/portlet/coverage", new LocalizedFieldRule());
			digester.addRule("portlet-app/portlet/description", new LocalizedFieldRule());
			digester.addRule("portlet-app/portlet/format", new LocalizedFieldRule());
			digester.addRule("portlet-app/portlet/identifier", new LocalizedFieldRule());
			digester.addRule("portlet-app/portlet/language", new LocalizedFieldRule());
			digester.addRule("portlet-app/portlet/publisher", new LocalizedFieldRule());
			digester.addRule("portlet-app/portlet/relation", new LocalizedFieldRule());
			digester.addRule("portlet-app/portlet/right", new LocalizedFieldRule());
			digester.addRule("portlet-app/portlet/source", new LocalizedFieldRule());
			digester.addRule("portlet-app/portlet/subject", new LocalizedFieldRule());
			digester.addRule("portlet-app/portlet/type", new LocalizedFieldRule());
			digester.addRule("portlet-app/portlet/metadata", new LocalizedFieldRule());
            */
			digester.addRuleSet(new MetadataRuleSet("portlet-app/portlet/"));
            
            digester.parse(reader);
            result = true;

            log.info("finished");

        }
        catch (Throwable t)
        {
            String msg = "Could not unmarshal \"" + pathPortletXML+"\".  "+t.toString();
            log.error(msg, t);
            //throw new PortletApplicationException(msg, t);
        }

        return result;
    }
}

