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
package org.apache.jetspeed.services.registry.impl;

import java.io.FileReader;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

import java.util.StringTokenizer;
import org.xml.sax.InputSource;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xml.serialize.OutputFormat;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.cps.CPSInitializationException;

/**
 ** CastorConfiguration class normalizes access to string attributes set within the Castor Configuration,
 ** an XML file containing the JDBC driver parameters and mapping file location.
 ** The location of the mapping file must be normalized depending on whether we are running in system test
 ** mode or in deployed mode. Likewise, the location of the database on disk may need to be rewritten.
 ** Currently, we only rewrite the location for Hypersonic SQL DB, since the URL is web application relative.
 **
 ** @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 ** @since 2.0
 ** @version $Id$
 **/
public class CastorConfiguration
{
    protected final static Log log = LogFactory.getLog(CastorConfiguration.class);
    
    boolean debug = false;
    private String applicationRoot = null;
    private String configurationName = null;
    private InputSource inputSource = null;
    
    public static final String DEFAULT_MAPPING_XML = "/WEB-INF/conf/registry-xml-mapping.xml";
    public static final String DEFAULT_DB_URL = "/WEB-INF/db/hsql/Registry";
    public static final String DEFAULT_DRIVER_TYPE = "jdbc:hsqldb:";
    public static final String CONFIG_DIRECTORY = "WEB-INF/conf/";            
    
    private static final String JAX_TRANSFORM_PROPERTY = "javax.xml.transform.TransformerFactory";
    private String jaxTransformFactoryProp = "org.apache.xalan.processor.TransformerFactoryImpl";
    private String jaxDomFactoryProp = "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl";
    private static final String JAX_DOM_PARSER_PROPERTY = "javax.xml.parsers.DocumentBuilderFactory";

    /**
     * Creates a Castor Configuration object given the application root.
     * 
     * @param applicationRoot Represents either the testing root or the web application root.
     */
    public CastorConfiguration(String applicationRoot)
    {
        this.applicationRoot = applicationRoot;
    }
    
    /*
     * Loads a configuration given the configuration file path. This method loads the XML configuration
     * into a DOM, normalizes the JDBC URL and Castor Mapping nodes to be application root-relative,
     * and then writes the resulting DOM to a memory-based InputSource. This input source is then used
     * as input to Castor to load the database configuration with the normalized paths to the mapping file 
     * and JDBC data source.
     * 
     * @param configPath Path to the Castor Configuration file.
     * @return InputSource The input source to the memory image of the normalized XML configuration.
     */    
    public InputSource load(String configPath)
        throws CPSInitializationException
    {
        this.configurationName = configPath;
        boolean normalizedUrl = false;
        boolean normalizedMapping = false;
        
        try
        {        
            System.setProperty( JAX_DOM_PARSER_PROPERTY, jaxDomFactoryProp);            
            
            InputSource is = new InputSource(new FileReader(configPath));
            is.setSystemId(configPath);
    
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setValidating(false);
            DocumentBuilder docBuilder = domFactory.newDocumentBuilder();
            Document document = docBuilder.parse(is);
            
            NodeList nodes = document.getChildNodes();
            for (int ix = 0; ix < nodes.getLength(); ix++)
            {
                boolean hsql = false;
                Node node = nodes.item(ix);                
                if (node.getNodeName().equalsIgnoreCase("database"))
                {
                    Node engine = node.getAttributes().getNamedItem("engine");
                    if (engine == null || !engine.getNodeValue().equalsIgnoreCase("hsql"))
                    {
                        hsql = false;
                    }
                    else
                    {
                        hsql = true;
                    }                    
                    NodeList elements = node.getChildNodes();
                    for (int iy = 0; iy < elements.getLength(); iy++)
                    {
                        Node element = elements.item(iy);
                        if (hsql && element.getNodeName().equalsIgnoreCase("driver"))
                        {
                            NamedNodeMap attributes = element.getAttributes();
                            Node url = attributes.getNamedItem("url");
                            normalizedUrl = normalizeDatabaseUrl(url);
                        }
                        else if (element.getNodeName().equalsIgnoreCase("mapping"))
                        {
                            NamedNodeMap attributes = element.getAttributes();
                            Node href = attributes.getNamedItem("href");
                            //normalizedMapping = normalizeMappingFile(href);
                        }

                    }                    
                }
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
    //        Source source = new DOMSource(document);
      //      Result result = new StreamResult(baos);
            OutputFormat format = new OutputFormat();
            format.setIndenting(true);
            format.setIndent(4);
            XMLSerializer serializer = new XMLSerializer(baos, format);
            serializer.serialize(document);             
/*
    Had this working for a while, still works fine in Unit tests, but when deployed to Tomcat, it fails with
    error saying that Xalan wants a formatter associated with it. Wish I could use a standard way, but Im going back
    to using Serializers for now...
     
            String defaultTransformer = System.getProperty(JAX_TRANSFORM_PROPERTY, null);
            if (null == defaultTransformer)
            {            
                System.setProperty( JAX_TRANSFORM_PROPERTY, jaxTransformFactoryProp);
            }
            
            // Write the DOM document to the output stream            
            Transformer xformer = TransformerFactory.newInstance().newTransformer();
            xformer.transform(source, result);
*/

            inputSource = new InputSource(new ByteArrayInputStream(baos.toByteArray()));
            inputSource.setSystemId(makeSystemId());
            //System.out.println("output = " + baos.toString());
            //System.out.println("*** appl root = " + applicationRoot);
            //System.out.println("*** config path = " + configPath);
            
        }
        catch (Throwable t)
        {        
            t.printStackTrace();            
            throw new CPSInitializationException(t.toString());
        }
        return inputSource;
    }
    
    /*
     * Makes the System Id for the input source.
     * This system id is used as the base URL when calculated resources off of this input source
     * We make it to be the application root + the 'conf' directory in Jetspeed
     */
     private String makeSystemId()
     {
         StringBuffer buffer = new StringBuffer();
         buffer.append(getApplicationRoot());
         if (!this.applicationRoot.endsWith("/"))
         {
              buffer.append("/");                       
         }
         buffer.append(CONFIG_DIRECTORY);
         return buffer.toString();
     }
     
    /*
     * Accessor to get the input source for this configuration
     * 
     * @return InputSource The input source to the memory image of the normalized XML configuration.
     */
    public InputSource getInputSource()
    {
        return inputSource;
    }

    /*
     * Accessor to get the application root for this configuration
     * 
     * @return String Represents either the testing root or the web application root.
     */
    private String getApplicationRoot()
    {
        return this.applicationRoot;
    }

    /*
     * Normalizes the mapping file HREF to be either web application relative or test application relative
     * based on the application root. The node is rewritten back to the DOM to include the application 
     * root and appended mapping file name.
     * 
     * @param href The DOM Node being manipulated.
     * @return boolean Returns true if normalization occurred.
     */    
    protected boolean normalizeMappingFile(Node href)
    {
        boolean normalized = false;
        StringBuffer mappingFile = new StringBuffer();
        mappingFile.append(getApplicationRoot());
        if (null == href)
        {
            mappingFile.append(DEFAULT_MAPPING_XML);
        }
        else 
        {
            String value = href.getNodeValue();
            if (!value.startsWith("/"))
            {
                mappingFile.append("/");                
            }
            mappingFile.append(value);
            normalized = true;    
        }
        href.setNodeValue(mappingFile.toString());
        if (log.isDebugEnabled())
        {            
            log.debug("Castor Configuration, normalizing Mapping File: " + href.getNodeValue());                    
        }
        
        return normalized;                                                                                            
    }

    /*
     * Normalizes the JDBC URL to be either web application relative or test application relative
     * based on the application root. The node is rewritten back to the DOM to include the application 
     * root and appended JDBC URL. Note that this only will be applied for Hypersonic SQL.
     * 
     * @param url The DOM Node being manipulated.
     * @return boolean Returns true if normalization occurred.
     */    
    protected boolean normalizeDatabaseUrl(Node url)
    {           
        boolean normalized = false;     
        StringBuffer mappingFile = new StringBuffer();
        if (null == url)
        {
            mappingFile.append(DEFAULT_DRIVER_TYPE); 
            mappingFile.append(getApplicationRoot());            
            mappingFile.append(DEFAULT_DB_URL);
        }
        else 
        {
            String value = url.getNodeValue();
            StringTokenizer tokenizer = new StringTokenizer(value, ":");
            if (tokenizer.countTokens() != 3)
            {
                mappingFile.append(DEFAULT_DRIVER_TYPE); 
                mappingFile.append(getApplicationRoot());            
                mappingFile.append(DEFAULT_DB_URL);                
            }
            else
            {                
                mappingFile.append(tokenizer.nextToken());
                mappingFile.append(":");
                mappingFile.append(tokenizer.nextToken());
                mappingFile.append(":");
                String dbUrl = tokenizer.nextToken();
                if (dbUrl.startsWith("./"))
                {
                    mappingFile.append(dbUrl);
                }
                else
                {               
                    mappingFile.append(getApplicationRoot());                                
                    if (!dbUrl.startsWith("/"))
                    {
                        mappingFile.append("/");                
                    }
                    mappingFile.append(dbUrl);
                }
                normalized = true;                    
            }
        }
        url.setNodeValue(mappingFile.toString());
        if (log.isDebugEnabled())
        {
            log.debug("Castor Configuration, normalizing JDBC URL: " + url.getNodeValue());                    
        }
        
        return normalized;                                                                                            
    }
    
}
