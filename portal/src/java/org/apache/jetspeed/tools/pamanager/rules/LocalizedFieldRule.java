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

import java.util.Locale;

import org.apache.commons.digester.Rule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.om.common.LocalizedField;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;

import org.xml.sax.Attributes;

/**
 * This class helps load internationalized fields
 * 
 * @author <a href="mailto:jford@apache.org">Jeremy Ford </a>
 * @version $Id$
 */
public class LocalizedFieldRule extends Rule
{
    protected final static Log log = LogFactory.getLog(LocalizedFieldRule.class);

    /**
     * Handle the beginning of an XML element.
     * 
     * @param attributes
     *            The attributes of this element
     * @exception Exception
     *                if a processing error occurs
     */
    public void begin(String namespace, String name, Attributes attributes) throws Exception
    {

        if (digester.getLogger().isDebugEnabled())
            digester.getLogger().debug("Setting localized field " + name);
        
        Object obj = digester.peek();
        if (null == obj)
        {
            digester.push(null);
            return;
        }
        GenericMetadata metadata = null;
        if (obj instanceof MutablePortletApplication)
        {
            metadata = ((MutablePortletApplication) obj).getMetadata();
        }
        if (obj instanceof PortletDefinitionComposite)
        {
            metadata = ((PortletDefinitionComposite) obj).getMetadata();
        }
        if (metadata != null)
        {
            LocalizedField child = metadata.createLocalizedField();

            if (name.equals("metadata"))
            {
                String nameAttr = attributes.getValue("name");
                child.setName(nameAttr);
            }
            else
            {
                child.setName(name);
            }
            String language = attributes.getValue("xml:lang");
            Locale locale = null;
            if (language == null)
            {
                locale = new Locale("en");
            }
            else
            {
                locale = new Locale(language);
            }

            child.setLocale(locale);
            digester.push(child);
        }
        else
        {
            digester.push(null);
        }
    }

    public void body(String namespace, String name, String text) throws Exception
    {
        LocalizedField child = (LocalizedField) digester.peek(0);
        if (child != null)
        {
            child.setValue(text);
        }
    }

    public void end(String namespace, String name) throws Exception
    {
        LocalizedField child = (LocalizedField) digester.pop();
        if (child != null)
        {
            Object obj = digester.peek();
            if (null == obj)
            {
                digester.push(null);
                return;
            }
            GenericMetadata metadata = null;
            if (obj instanceof MutablePortletApplication)
            {
                metadata = ((MutablePortletApplication) obj).getMetadata();
            }
            if (obj instanceof PortletDefinitionComposite)
            {
                metadata = ((PortletDefinitionComposite) obj).getMetadata();
            }
            if (null != metadata)
            {
                metadata.addField(child);
            }    
        }
    }
}