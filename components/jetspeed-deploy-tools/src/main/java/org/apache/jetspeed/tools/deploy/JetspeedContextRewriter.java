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
package org.apache.jetspeed.tools.deploy;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Utilities for manipulating the context.xml deployment descriptor
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class JetspeedContextRewriter
{
    private Document document;
    private String portletApplication;
    public JetspeedContextRewriter(Document doc, String portletApplication)
    {
        this.document = doc;
        this.portletApplication = portletApplication;
    }

    public void processContextXML()
        throws Exception
    {
        if (document != null)
        {
            try
            {
                // get root Context
                Element root = null;
                if (!document.hasChildNodes())
                {
                    root = document.createElement("Context");
                    document.appendChild(root);
                }
                else
                {
                    root = document.getDocumentElement();
                }   
                
                if (root.hasAttribute("path"))
                {
                    // set Context path
                    String pathAttribute = root.getAttribute("path");
                    if ((pathAttribute.equals("")) || !pathAttribute.equals("/" + portletApplication))
                    {
                        root.setAttribute("path", "/" + portletApplication);
                    }
                }
                
                // Security measurement: restrict/reduce deployment of non-privileged Tomcat applications only
                if (root.hasAttribute("privileged"))
                {
                    root.setAttribute("privileged", "false");
                }
                
                if (root.hasAttribute("docBase"))
                {
                    // set Context docBase
                    String docBaseAttribute = root.getAttribute("docBase");
                    if ((docBaseAttribute.equals("")) || !docBaseAttribute.equals(portletApplication))
                    {
                        root.setAttribute("docBase", portletApplication);
                    }
                }
            }
            catch (Exception e)
            {
                throw new Exception("Unable to process context.xml for infusion " + e.toString(), e);
            }
        }
    }
}
