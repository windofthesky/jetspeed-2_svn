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
package org.apache.jetspeed.descriptor;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.pluto.container.impl.PortletAppDescriptorServiceImpl;
import org.apache.pluto.container.om.portlet.PortletApplicationDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * JAXB implementation of the xml2java binding, overriding unmarshalling with custom namespace handling.
 * 
 * @version $Id$
 */
public class JetspeedPortletAppDescriptorServiceImpl extends PortletAppDescriptorServiceImpl
{
    private static Logger log = LoggerFactory.getLogger(JetspeedPortletAppDescriptorServiceImpl.class);
    
    private boolean allowEmptyNamespace;
    
    public JetspeedPortletAppDescriptorServiceImpl()
    {
    }
    
    public JetspeedPortletAppDescriptorServiceImpl(boolean allowEmptyNamespace)
    {
        this.allowEmptyNamespace = allowEmptyNamespace;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public PortletApplicationDefinition read(String name, String contextPath, InputStream in) throws IOException 
    {
        if (!allowEmptyNamespace)
        {
            return super.read(name, contextPath, in);
        }
        else
        {
            JAXBElement app = null;
            
            try
            {
                JAXBContext jc = JAXBContext.newInstance(
                        "org.apache.pluto.container.om.portlet10.impl" + ":" +
                        "org.apache.pluto.container.om.portlet.impl" + ":" +
                        "org.apache.jetspeed.descriptor.om.portlet10.impl", 
                        PortletAppDescriptorServiceImpl.class.getClassLoader());
                
                Unmarshaller u = jc.createUnmarshaller();
                u.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
                
                app = (JAXBElement) u.unmarshal(in);
            }
            catch (JAXBException jaxbEx)
            {
                final IOException ioe = new IOException(jaxbEx.getMessage());
                ioe.initCause(jaxbEx);
                throw ioe;
            }
            catch (Exception me)
            {
                final IOException ioe = new IOException(me.getLocalizedMessage());
                ioe.initCause(me);
                throw new IOException(me.getLocalizedMessage());
            }
            
            PortletApplicationDefinition pad = null;
            
            if (app.getValue() instanceof org.apache.pluto.container.om.portlet10.impl.PortletAppType)
            {
                pad = ((org.apache.pluto.container.om.portlet10.impl.PortletAppType)app.getValue()).upgrade();
            }
            else if (app.getValue() instanceof org.apache.jetspeed.descriptor.om.portlet10.impl.PortletAppType)
            {
                if (log.isWarnEnabled())
                {
                    log.warn("The portlet descriptor of {} ({}) will be treated as Portlet 1.0 schema based due to the empty namespace uri.", 
                             name, contextPath);
                }
                pad = ((org.apache.jetspeed.descriptor.om.portlet10.impl.PortletAppType)app.getValue()).upgrade();
            }
            else
            {
                pad = (PortletApplicationDefinition)app.getValue();
            }
            
            pad.setName(name);
            pad.setContextPath(contextPath);
            
            return pad;
        }
    }
    
}
