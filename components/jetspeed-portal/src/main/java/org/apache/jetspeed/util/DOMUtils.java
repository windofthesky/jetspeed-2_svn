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
package org.apache.jetspeed.util;

import java.io.IOException;
import java.io.StringWriter;

import org.dom4j.dom.DOMCDATA;
import org.dom4j.dom.DOMComment;
import org.dom4j.dom.DOMElement;
import org.dom4j.dom.DOMText;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DOMUtils
{
    
    public static final int DEFAULT_ELEMENT_STRINGIFYING_BUFFER_SIZE = 80;
    public static final int DEFAULT_INDENT = 0;
    public static final String DEFAULT_INDENT_WITH = "\t";
    
    private DOMUtils()
    {
    }
    
    public static org.w3c.dom.Element createSerializableElement(String tagName)
    {
        return new DOMElement(tagName)
        {
            private static final long serialVersionUID = 1L;

            public void setTextContent(String textContent)
            {
                setText(textContent);
            }
            
            public String getTextContent()
            {
                return getText();
            }
        };
    }
    
    public static org.w3c.dom.Element convertToSerializableElement(org.w3c.dom.Element element)
    {
        return (org.w3c.dom.Element) convertElement(element);
    }
    
    public static org.dom4j.Element convertElement(org.w3c.dom.Element element)
    {
        DOMElement domElement = (DOMElement) createSerializableElement(element.getNodeName());
        
        NamedNodeMap attrs = element.getAttributes();
        
        for (int i = 0; i < attrs.getLength(); i++) 
        {
            Attr attr = (Attr) attrs.item(i);
            domElement.setAttribute(attr.getName(), attr.getValue());
        }
        
        NodeList children = element.getChildNodes();
        boolean hasChildren = (children.getLength() > 0);

        if (hasChildren) 
        {
            for (int i = 0; i < children.getLength(); i++) 
            {
                Node child = children.item(i);

                switch (child.getNodeType()) 
                {
                case Node.ELEMENT_NODE:
                    domElement.add(convertElement((org.w3c.dom.Element) child));
                    break;
                case Node.TEXT_NODE:
                    domElement.add(new DOMText(child.getNodeValue()));
                    break;
                case Node.COMMENT_NODE:
                    domElement.add(new DOMComment(child.getNodeValue()));
                    break;
                case Node.CDATA_SECTION_NODE:
                    domElement.add(new DOMCDATA(child.getNodeValue()));
                    break;
                default:
                    // Do not support entity reference node and processing instruction node.
                }
            }
        }
        
        return domElement;
    }

    public static String stringifyElement(org.w3c.dom.Element element)
    {
        return stringifyElement(element, DEFAULT_ELEMENT_STRINGIFYING_BUFFER_SIZE, DEFAULT_INDENT, DEFAULT_INDENT_WITH);
    }

    public static String stringifyElement(org.w3c.dom.Element element, int initialBufferSize, int indent, String indentWith)
    {
        String stringified = null;
        StringWriter writer = new StringWriter(initialBufferSize);
        
        try
        {
            DOMElementWriter domWriter = new DOMElementWriter();
            domWriter.write(element, writer, indent, indentWith);
        }
        catch (IOException e)
        {
        }

        stringified = writer.toString();
        return stringified;
    }
    
    public static String stringifyElement(org.dom4j.Element element)
    {
        return stringifyElement(element, DEFAULT_ELEMENT_STRINGIFYING_BUFFER_SIZE, DEFAULT_INDENT_WITH);
    }
    
    public static String stringifyElement(org.dom4j.Element element, int initialBufferSize, String indentWith)
    {
        OutputFormat outputFormat = new OutputFormat();
        outputFormat.setIndent(indentWith);
        return stringifyElement(element, initialBufferSize, outputFormat);
    }
    
    public static String stringifyElement(org.dom4j.Element element, int initialBufferSize, OutputFormat outputFormat)
    {
        String stringified = null;
        StringWriter writer = new StringWriter(initialBufferSize);
        XMLWriter xmlWriter = null;
        
        try
        {
            xmlWriter = new XMLWriter(writer, outputFormat);
            xmlWriter.write(element);
            xmlWriter.flush();
            xmlWriter.close();
        }
        catch (IOException e)
        {
        }
        finally
        {
            if (xmlWriter != null)
            {
                try { xmlWriter.close(); } catch (IOException ce) { }
            }
        }
        
        stringified = writer.toString();
        return stringified;
    }
    
}
