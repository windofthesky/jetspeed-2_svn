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

import org.apache.jetspeed.container.impl.HeadElementImpl;
import org.apache.jetspeed.portlet.HeadElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TestHeadElementUtils extends TestCase
{
    
    private Document document;
    
    @Override
    public void setUp() throws Exception
    {
        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
        document = docBuilder.newDocument();
    }

    public void testScriptHeadElements() throws Exception
    {
        Element elem = document.createElement("script");
        elem.setAttribute("language", "javascript");
        elem.setAttribute("src", "http://localhost:8080/javascript/util.js");
        elem.setAttribute("customAttribute", "<test>");
        HeadElement headElem = new HeadElementImpl(elem);
        
        String headElemString = HeadElementUtils.toString(headElem, false, true, false);
        System.out.println("headElemString: " + headElemString);
        assertTrue(headElem.getTextContent() == null || "".equals(headElem.getTextContent()));
        assertFalse(headElemString.endsWith("</script>"));
        
        headElemString = HeadElementUtils.toString(headElem, true, true, false);
        System.out.println("headElemString: " + headElemString);
        assertEquals(HeadElementUtils.toHtmlString(headElem), headElemString);
        assertTrue(headElem.getTextContent() == null || "".equals(headElem.getTextContent()));
        assertTrue(headElemString.endsWith("</script>"));
        
        elem = document.createElement("script");
        elem.setAttribute("language", "javascript");
        elem.setTextContent("\r\nif (true) {\r\n\talert(\"<test/>\");\r\n}\r\n");
        headElem = new HeadElementImpl(elem);

        headElemString = HeadElementUtils.toString(headElem, true, true, false);
        System.out.println("headElemString: " + headElemString);
        assertEquals(HeadElementUtils.toHtmlString(headElem), headElemString);
        assertFalse(headElem.getTextContent() == null || "".equals(headElem.getTextContent()));
        assertTrue(headElemString.endsWith("</script>"));

        headElemString = HeadElementUtils.toXhtmlString(headElem);
        System.out.println("headElemString: " + headElemString);
        assertFalse(headElem.getTextContent() == null || "".equals(headElem.getTextContent()));
        assertTrue(headElemString.contains("<![CDATA["));
        assertTrue(headElemString.contains("]]>"));
        assertTrue(headElemString.endsWith("</script>"));
    }
    
    public void testMetaElements() throws Exception
    {
        Element elem = document.createElement("meta");
        elem.setAttribute("name", "keywords");
        elem.setAttribute("content", "HTML,CSS,XML,JavaScript");
        HeadElement headElem = new HeadElementImpl(elem);
        
        String headElemString = HeadElementUtils.toString(headElem, false, true, false);
        System.out.println("headElemString: " + headElemString);
        assertEquals(HeadElementUtils.toHtmlString(headElem), headElemString);
        assertTrue(headElemString.startsWith("<meta "));
        assertTrue(headElemString.endsWith("/>"));
        assertTrue(headElemString.contains("name=\"keywords\""));
        assertTrue(headElemString.contains("content=\"HTML,CSS,XML,JavaScript\""));
    }
    
    public void testHandlerElements() throws Exception
    {
        Element elem = document.createElement("handler");
        elem.setAttribute("type", "text/x-vbscript");
        elem.setAttribute("src", "http://localhost:8080/javascript/calc.vbs");
        Element child = document.createElement("handler");
        child.setAttribute("type", "text/javascript");
        child.setTextContent("\r\n//some inline javascript\r\n");
        elem.appendChild(child);
        
        HeadElement headElem = new HeadElementImpl(elem);
        String headElemString = HeadElementUtils.toString(headElem, true, true, false);
        System.out.println("headElemString: " + headElemString);
        assertEquals(HeadElementUtils.toHtmlString(headElem), headElemString);
        assertTrue(headElemString.startsWith("<handler "));
        assertTrue(headElemString.indexOf("<handler ", 1) > 0);
        assertTrue(headElemString.endsWith("</handler>"));
        assertTrue(headElemString.substring(0, headElemString.length() - "</handler>".length()).trim().endsWith("</handler>"));
    }
    
}
