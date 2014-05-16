/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.jetspeed.tools.deploy;

import org.w3c.dom.Document;

//import org.jdom.Document;

/**
 * @author Nicolas Dutertry
 * @version $Id$
 */
public class JetspeedWebApplicationRewriterFactory {
    
    /** 
     * Returns an instance of JetspeedWebApplicationRewriter.
     * 
     * @param doc
     * @return JetspeedWebApplicationRewriter
     * @throws Exception
     */
    public JetspeedWebApplicationRewriter getInstance(Document doc) throws Exception
    {
        return getInstance(doc, null, null);
    }
    
    /** 
     * Returns an instance of JetspeedWebApplicationRewriter.
     * 
     * @param doc
     * @return JetspeedWebApplicationRewriter
     * @throws Exception
     */
    public JetspeedWebApplicationRewriter getInstance(Document doc, String portletApplication) throws Exception
    {
        return getInstance(doc, portletApplication, null);
    }
    
    /** 
     * Returns an instance of JetspeedWebApplicationRewriter.
     * 
     * @param doc
     * @param portletApplication
     * @param forcedVersion
     * @return JetspeedWebApplicationRewriter
     * @throws Exception
     */
    public JetspeedWebApplicationRewriter getInstance(Document doc, String portletApplication, String forcedVersion) throws Exception
    {
        String version = forcedVersion;
        if(version == null)
        {
            version = doc.getDocumentElement().getAttribute("version");
            if (version.equals(""))
            {
                version = "2.3";
            }
        }

        try
        {
            // Check version is a valid number
            Double.parseDouble(version);
        }
        catch(NumberFormatException e)
        {
            throw new Exception("Unable to create JetspeedWebApplicationRewriter for version " + version, e);
        }
        
        if(version.equals("2.3"))
        {
            return new JetspeedWebApplicationRewriter2_3(doc, portletApplication);
        }
        else if(version.equals("2.4"))
        {
            return new JetspeedWebApplicationRewriter2_4(doc, portletApplication);
        }
        else if(version.equals("2.5"))
        {
            return new JetspeedWebApplicationRewriter2_5(doc, portletApplication);
        }
        else if(version.equals("3.0"))
        {
            return new JetspeedWebApplicationRewriter3_0(doc, portletApplication);
        }
        else if(version.compareTo("3.1") >= 0)
        {
            return new JetspeedWebApplicationRewriter3_1(doc, portletApplication);
        }
        else
        {
            throw new Exception("Unable to create JetspeedWebApplicationRewriter for version " + version);
        }
    }
}
