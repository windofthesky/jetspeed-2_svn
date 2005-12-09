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
package org.apache.portals.applications.transform.impl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TemplatesHandler;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.apache.portals.applications.transform.Transform;
import org.apache.portals.applications.transform.TransformException;
import org.apache.portals.applications.transform.TransformObjectPublisher;
import org.apache.portals.applications.util.Streams;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * TransformComponent
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id$
 */
public class JetspeedTransform implements Transform
{
    public JetspeedTransform()
    {
        // TODO: make all JAX factories configurable
        synchronized (mutex)
        {
            if (transformerFactory == null)
            {
                System.setProperty(JAX_TRANSFORM_PROPERTY, jaxTransformFactoryProp);
                System.setProperty(JAX_SAX_PARSER_PROPERTY, jaxSaxFactoryProp);
                System.setProperty(JAX_DOM_PARSER_PROPERTY, jaxDomFactoryProp);

                TransformerFactory tFactory = TransformerFactory.newInstance();
                domFactory = DocumentBuilderFactory.newInstance();
                domFactory.setValidating(false);
                saxFactory = SAXParserFactory.newInstance();
                saxFactory.setValidating(false);
                //if (!tFactory.getFeature(SAXTransformerFactory.FEATURE)) { throw new TransformException(
                //        "Invalid SAX Tranformer. Doesn't support SAX"); }
                transformerFactory = ((SAXTransformerFactory) tFactory);
            }
        }
        
        publisher = new TransformObjectPublisher();        
    }
    
    private static DocumentBuilderFactory domFactory = null;

    private static SAXParserFactory saxFactory = null;

    private static SAXTransformerFactory transformerFactory = null;

    //
    // JAXP Service Configuration
    //
    private final static String CONFIG_JAX_FACTORY_SAX = "jax.factory.sax";

    private final static String jaxSaxFactoryProp = "org.apache.xerces.jaxp.SAXParserFactoryImpl";

    private final static String CONFIG_JAX_FACTORY_DOM = "jax.factory.dom";

    private final static String jaxDomFactoryProp = "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl";

    private final static String CONFIG_JAX_FACTORY_TRANSFORM = "jax.factory.transform";

    private final static String jaxTransformFactoryProp = "org.apache.xalan.processor.TransformerFactoryImpl";

    private final static Object mutex = new Object();

    //
    // JAXP System Wide Properties
    //
    private static final String JAX_TRANSFORM_PROPERTY = "javax.xml.transform.TransformerFactory";

    private static final String JAX_SAX_PARSER_PROPERTY = "javax.xml.parsers.SAXParserFactory";

    private static final String JAX_DOM_PARSER_PROPERTY = "javax.xml.parsers.DocumentBuilderFactory";

    // DTD Map
    static private Map dtds = new HashMap();

    private TransformObjectPublisher publisher = null;

    public void transform(String xslt, InputSource inputSource, OutputStream os, Map parameters)
            throws TransformException
    {
        if (xslt == null)
        {
            try
            { // if no stylesheet specified simply drain the stream
                Streams.drain(inputSource.getByteStream(), os);
            }
            catch (IOException e)
            {
                throw new TransformException(e);
            }
        }
        else
        {
            transformStream(xslt, inputSource, new StreamResult(os), parameters);
        }
    }

    public  void transform(String xslt, InputSource inputSource, Writer writer, Map parameters)
            throws TransformException
    {
        if (xslt == null)
        {
            try
            { // if no stylesheet specified simply drain the stream
                Streams.drain(inputSource.getCharacterStream(), writer);
            }
            catch (IOException e)
            {
                throw new TransformException(e);
            }
        }
        else
        {
            transformStream(xslt, inputSource, new StreamResult(writer), parameters);
        }
    }

    private static void transformStream(String xslt, InputSource inputSource, StreamResult streamResult, Map parameters)
            throws TransformException
    {
        if (xslt == null) { throw new TransformException("Invalid Transform, no stylesheet set!"); }

        //
        // create a new document builder to load the XML file for transformation
        //
        DocumentBuilder docBuilder = null;
        try
        {
            docBuilder = domFactory.newDocumentBuilder();
            docBuilder.setEntityResolver(new TransformDTDEntityResolver(dtds));

        }
        catch (ParserConfigurationException e)
        {
            throw new TransformException("Failed to load JAX Document Builder: " + e.toString());
        }

        try
        {
            // Create a ContentHandler to handle parsing of the stylesheet.
            TemplatesHandler templatesHandler = transformerFactory.newTemplatesHandler();

            // Create an XMLReader and set its ContentHandler.
            XMLReader reader = XMLReaderFactory.createXMLReader();
            reader.setContentHandler(templatesHandler);

            // Set it to solve Entities via cache
            reader.setEntityResolver(new TransformDTDEntityResolver(dtds));

            //
            // Get the stylesheet's content from the deployment
            //                         
            java.io.FileInputStream is = new java.io.FileInputStream(xslt);
            InputStreamReader ssreader = new InputStreamReader(is);

            // Parse the stylesheet.
            final InputSource xstyle = new InputSource(ssreader);
            xstyle.setSystemId(xslt);
            reader.parse(xstyle);

            //Get the Templates object from the ContentHandler.
            Templates templates = templatesHandler.getTemplates();

            // Create a ContentHandler to handle parsing of the XML source.
            TransformerHandler handler = transformerFactory.newTransformerHandler(templates);

            // Reset the XMLReader's ContentHandler.
            reader.setContentHandler(handler);

            //
            // Parse the Document into a DOM tree
            // 
            //
            org.w3c.dom.Document doc = docBuilder.parse(inputSource);

            // reader.setProperty("http://xml.org/sax/properties/lexical-handler",
            // handler);

            final Transformer processor = handler.getTransformer();

            //
            // Get the transform variables (parameters)
            //
            Iterator keys = parameters.keySet().iterator();
            while (keys.hasNext())
            {
                String name = (String) keys.next();
                String value = (String) parameters.get(name);
                processor.setParameter(name, value); 
            }

            //
            // do the transformation now
            //
            processor.transform(new DOMSource(doc), streamResult);

        }
        catch (Exception e)
        {
            throw new TransformException("Error in Transformation: " + e.toString());
        }
    }

    private static void transformStream(String xslt, Document document, StreamResult streamResult, Map parameters)
            throws TransformException
    {
        if (xslt == null) { throw new TransformException("Invalid Transform, no stylesheet set!"); }

        synchronized (mutex)
        {
            if (transformerFactory == null)
            {
                System.setProperty(JAX_TRANSFORM_PROPERTY, jaxTransformFactoryProp);
                System.setProperty(JAX_SAX_PARSER_PROPERTY, jaxSaxFactoryProp);
                System.setProperty(JAX_DOM_PARSER_PROPERTY, jaxDomFactoryProp);

                TransformerFactory tFactory = TransformerFactory.newInstance();
                domFactory = DocumentBuilderFactory.newInstance();
                domFactory.setValidating(false);
                saxFactory = SAXParserFactory.newInstance();
                saxFactory.setValidating(false);
                if (!tFactory.getFeature(SAXTransformerFactory.FEATURE)) { throw new TransformException(
                        "Invalid SAX Tranformer. Doesn't support SAX"); }
                transformerFactory = ((SAXTransformerFactory) tFactory);
            }
        }

        //
        // create a new document builder to load the XML file for transformation
        //
        DocumentBuilder docBuilder = null;
        try
        {
            docBuilder = domFactory.newDocumentBuilder();
            docBuilder.setEntityResolver(new TransformDTDEntityResolver(dtds));

        }
        catch (ParserConfigurationException e)
        {
            throw new TransformException("Failed to load JAX Document Builder: " + e.toString());
        }

        try
        {
            // Create a ContentHandler to handle parsing of the stylesheet.
            TemplatesHandler templatesHandler = transformerFactory.newTemplatesHandler();

            // Create an XMLReader and set its ContentHandler.
            XMLReader reader = XMLReaderFactory.createXMLReader();
            reader.setContentHandler(templatesHandler);

            // Set it to solve Entities via cache
            reader.setEntityResolver(new TransformDTDEntityResolver(dtds));

            //
            // Get the stylesheet's content from the deployment
            //                         
            java.io.FileInputStream is = new java.io.FileInputStream(xslt);
            InputStreamReader ssreader = new InputStreamReader(is);

            // Parse the stylesheet.
            final InputSource xstyle = new InputSource(ssreader);
            xstyle.setSystemId(xslt);
            reader.parse(xstyle);

            //Get the Templates object from the ContentHandler.
            Templates templates = templatesHandler.getTemplates();

            // Create a ContentHandler to handle parsing of the XML source.
            TransformerHandler handler = transformerFactory.newTransformerHandler(templates);

            // Reset the XMLReader's ContentHandler.
            reader.setContentHandler(handler);

            //
            // Parse the Document into a DOM tree
            // 
            //
            // org.w3c.dom.Document doc = docBuilder.parse(inputSource);

            // reader.setProperty("http://xml.org/sax/properties/lexical-handler",
            // handler);

            final Transformer processor = handler.getTransformer();

            //
            // Get the transform variables (parameters)
            //
            Iterator keys = parameters.keySet().iterator();
            while (keys.hasNext())
            {
                String name = (String) keys.next();
                String value = (String) parameters.get(name);
                processor.setParameter(name, value); 
            }

            //
            // do the transformation now
            //
            processor.transform(new DOMSource(document), streamResult);

        }
        catch (Exception e)
        {
            throw new TransformException("Error in Transformation: " + e.toString());
        }

    }

    public void transform(String xslt, Document document, OutputStream os, Map parameters)
            throws TransformException
    {
        if (xslt == null)
        {
            throw new TransformException("xslt is null");
        }
        else
        {
            transformStream(xslt, document, new StreamResult(os), parameters);
        }
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.syndication.services.transform.TransformService#getPublisher()
     */
    public TransformObjectPublisher getPublisher()
    {
        return publisher;
    }
    

}