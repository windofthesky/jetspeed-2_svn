/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.tools.pamanager;

import java.io.FileReader;
import java.io.File;
import java.util.Iterator;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.xml.Unmarshaller;
import org.xml.sax.InputSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.PortalContext;

import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.pluto.om.portlet.PortletDefinitionList;
import org.apache.jetspeed.tools.castor.om.common.portlet.PortletDefinitionDescriptor;

/**
 * Utilities for manipulating the Portlet.xml deployment descriptor
 *
 * @author <a href="mailto:roger.ruttimann@earthlink.net">Roger Ruttimann</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a> 
 *  
 * @version $Id$
 */
public class PortletDescriptorUtilities
{
    protected final static Log log = LogFactory.getLog(PortletDescriptorUtilities.class);
    
    public static final String XML_MAPPING_FILE = "deployment.descriptor.mapping.xml";
    
    /**
     * Load a portlet.xml file into a Portlet Application tree
     *
     * @param pathPortletXML The path to the portlet.xml file
     * @return Application The Java object tree representing portlet.xml
     */
    public static MutablePortletApplication loadPortletApplicationTree(String pathPortletXML, String appName)
        throws PortletApplicationException
    {
        try
        {
            Mapping mapping = loadMapping();             
            java.io.FileReader reader = new java.io.FileReader(pathPortletXML);
            
            Unmarshaller unmarshaller = new Unmarshaller(mapping);
            
            MutablePortletApplication app = 
                    (MutablePortletApplication) unmarshaller.unmarshal(reader);
                        
            // Post-Processing of the tree before it gets saved to the database

            // 1) Set the URL for the application
            //app.setUrl(appName + "/");
            //((WebApplicationComposite)app.getWebApplicationDefinition()).setContextRoot(appName + "/");

            // 2) The app ID in the portlet.xml is optional. Set it to the
            //    application Name if it was not defined
            
            app.setName(appName);

            // More post-processing comes here
            PortletDefinitionList portletsList = app.getPortletDefinitionList();
            Iterator it = portletsList.iterator();
            int count = 0;
            while (it.hasNext())
            {
                PortletDefinitionDescriptor portlet = (PortletDefinitionDescriptor)it.next();
                portlet.postLoad(null);
            }
            
            return app;
        }
        catch (Throwable t)
        {
            String msg = "Could not unmarshal: " + pathPortletXML;
            log.error(msg, t);
            throw new PortletApplicationException(msg, t);
        }
    }
    
    /**
     * Loads the Castor XML Mapping file from the configuration directory
     * with instructions for mapping portlet.xml to Portlet Application tree
     * 
     * @return Mapping The Castor mapping directives
     */
    private static Mapping loadMapping()
        throws Exception
    {
        Mapping mapping = null;
        PortalContext pc = Jetspeed.getContext();
        String mappingFile = pc.getConfigurationProperty(XML_MAPPING_FILE);
        String realPath = Jetspeed.getRealPath(mappingFile);
            
        File map = new File(realPath);
        if (map.exists() && map.isFile() && map.canRead())
        {
            mapping = new Mapping();
            InputSource is = new InputSource(new FileReader(map));
            is.setSystemId(mappingFile);
            mapping.loadMapping(is);
        }
        else
        {
            String msg = "Mapping not found or not a file or unreadable: " + mappingFile;
            throw new PortletApplicationException(msg);
        }
            
        return mapping;            
    }
}
