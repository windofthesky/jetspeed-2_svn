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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TestDOMUtils extends TestCase
{

    public void testW3CDOMElement() throws Exception
    {
        
        // Tests with element created from document builder...
        
        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
        Document doc = docBuilder.newDocument();
        Element element = doc.createElement("script");
        element.setAttribute("id", "my-test-javascript");
        element.setAttribute("type", "text/javascript");
        element.setTextContent("alert('<Hello, World!>');");
        Element child = doc.createElement("source");
        child.setAttribute("id", "my-test-javascript-source");
        child.setTextContent("source is available.");
        element.appendChild(child);
        
        String stringified = DOMUtils.stringifyElement(element);
        System.out.println("stringified: " + stringified);
        assertTrue("element name is different.", stringified.contains("<script "));
        assertTrue("id attribute does not exist.", stringified.contains("id=\"my-test-javascript\""));
        assertTrue("type attribute does not exist.", stringified.contains("type=\"text/javascript\""));
        assertTrue("the text content is wrong.", stringified.contains("alert("));
        assertTrue("the text content is wrong.", stringified.contains("Hello, World!"));
        assertTrue("the child element is wrong.", stringified.contains("<source id=\"my-test-javascript-source\">source is available.</source>"));
        
        // Tests with element having CDATA child node
        // setTextContent() should replace the CDATA node.
        
        element = doc.createElement("script");
        element.setAttribute("id", "my-test-javascript");
        element.setAttribute("type", "text/javascript");
        CDATASection cdataSection = doc.createCDATASection("alert('<Hello, World!>');");
        element.appendChild(cdataSection);
        child = doc.createElement("source");
        child.setAttribute("id", "my-test-javascript-source");
        child.setTextContent("source is available.");
        element.appendChild(child);
        
        stringified = DOMUtils.stringifyElement(element);
        System.out.println("stringified: " + stringified);
        assertTrue("the text content is wrong.", stringified.contains("<![CDATA[alert("));
        assertTrue("the text content is wrong.", stringified.contains("Hello, World!"));
        assertTrue("the child element is wrong.", stringified.contains("<source id=\"my-test-javascript-source\">source is available.</source>"));
    }
}
