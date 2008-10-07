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
package org.apache.jetspeed.rewriter.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.rewriter.ParserAdaptor;
import org.apache.jetspeed.rewriter.Rewriter;
import org.apache.jetspeed.rewriter.RewriterException;
import org.apache.jetspeed.rewriter.MutableAttributes;
import org.apache.jetspeed.util.Streams;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SaxParserAdaptor
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class SaxParserAdaptor implements ParserAdaptor
{
    protected final static Log log = LogFactory.getLog(SaxParserAdaptor.class);
    private String lineSeparator;

    private Rewriter rewriter;

    
    

    public SaxParserAdaptor()
    {
        lineSeparator = System.getProperty("line.separator", "\r\n");
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.syndication.services.crawler.rewriter.ParserAdaptor#parse(org.apache.jetspeed.syndication.services.crawler.rewriter.Rewriter, java.io.Reader)
     */
    public void parse(Rewriter rewriter, Reader reader)
        throws RewriterException
    {
        try
        {
            this.rewriter = rewriter;        
            SAXParser sp = getParser();            
            sp.parse(new InputSource(reader), new SaxFormatHandler(null));                                                    
        } 
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RewriterException(e);
        }
                 
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.syndication.services.crawler.rewriter.ParserAdaptor#rewrite(org.apache.jetspeed.syndication.services.crawler.rewriter.Rewriter, java.io.Reader, java.io.Writer)
     */
    public void rewrite(Rewriter rewriter, Reader reader, Writer writer)
        throws RewriterException
    {
        // TODO Auto-generated method stub
    }
    
    /**
     * Get a Parser from the SAX Parser factory
     *
     * @return A SAXParser
     */
    protected SAXParser getParser()
        throws ParserConfigurationException, SAXException
    {
        SAXParserFactory spf = SAXParserFactory.newInstance ();
        spf.setValidating(false);

        return spf.newSAXParser ();
    }

    /**
     * Inner class to handle SAX parsing of XML files
     */
    public class SaxFormatHandler extends DefaultHandler
    {    
        private int elementCount = 0;
        private boolean emit = true;
        private Writer writer = null;

        public SaxFormatHandler(Writer writer)
        {
            super();
            this.writer = writer;
        }
        
        private void write(String text)
            throws IOException
        {
            if (writer != null)
            {
                writer.write(text);
            }
        }

        public void characters(char[] values, int start, int length)
        {
            if (false == emit)                               
                return;                                      

            if (false == rewriter.enterText(values, start))
               return;                    

            if (writer != null)
            {
                try
                {
                    writer.write(values);
                }
                catch(IOException e)
                {                
                }
            }            
        }
            
        public void startElement(String uri, String localName, String qName, MutableAttributes attributes) 
            throws SAXException
        {
//            System.out.println("qName = " + qName);
//            System.out.println("localName = " + localName);
//            System.out.println("uri = " + uri);
            String tag = qName;
            
            if (false == rewriter.enterStartTagEvent(tag.toString(), attributes))
                return;

            try
            {
                appendTagToResult(tag, attributes);
                write(lineSeparator);                
                String appended = rewriter.exitStartTagEvent(tag.toString(), attributes);
                if (null != appended)
                {
                    write(appended);
                }
            }                    
            catch (Exception e)
            {
                log.error("Start tag parsing error", e);                    
            }
        }
    
        public void endElement(String uri, String localName, String qName) 
            throws SAXException
        {
            String tag = qName;
            elementCount++;
            if (false == rewriter.enterEndTagEvent(tag.toString()))
                return;
                
            try
            {                            
                addToResult("</").addToResult(tag).addToResult(">");
    
                write(lineSeparator);                
                String appended = rewriter.exitEndTagEvent(tag.toString());
                if (null != appended)
                {
                    write(appended);
                }
            }                    
            catch (Exception e)
            {
                log.error("End tag parsing error", e);                                    
            }                    
            
        }

        /*
         * Writes output to the final stream for all attributes of a given tag.
         *
         * @param tag The HTML tag being output.
         * @param attrs The mutable HTML attribute set for the current HTML tag.
         */
        private void appendTagToResult(String tag, MutableAttributes attrs) 
        {
            convertURLS(tag, attrs);
            addToResult("<").addToResult(tag);
            for (int ix = 0; ix < attrs.getLength(); ix++)
            {
                String value = attrs.getValue(ix);
                addToResult(" ").addToResult(value).addToResult("=\"").
                addToResult(value).addToResult("\"");
            }        
            addToResult(">");
        }
    
        /*
         * Used to write tag and attribute objects to the output stream.
         * Returns a reference to itself so that these calls can be chained.
         *
         * @param txt Any text to be written out to stream with toString method.
         *            The object being written should implement its toString method.
         * @return A handle to the this, the callback, for chaining results.
         *
         */
        private SaxFormatHandler addToResult(Object txt)
        {
            // to allow for implementation using Stringbuffer or StringWriter
            // I don't know yet, which one is better in this case
            // if (ignoreLevel > 0 ) return this;

            try
            {
                write(txt.toString());
            } 
            catch (Exception e)
            {
                System.err.println("Error parsing:" + e);
            }
            return this;
        }

        /*
         * Determines which HTML Tag/Element is being inspected, and calls the 
         * appropriate converter for that context.  This method contains all the
         * logic for determining how tags are rewritten. 
         *
         * TODO: it would be better to drive this logic off a state table that is not
         * tied to the Hot Java parser.
         *
         * @param tag TAG from the Callback-Interface.
         * @param attrs The mutable HTML attribute set for the current HTML element.
         */

        private void convertURLS(String tag, MutableAttributes attrs) 
        {
            rewriter.enterConvertTagEvent(tag.toString(), attrs);
        }
             
        public InputSource resolveEntity (String publicId, String systemId)
        {
            
            try 
            {
                Map dtds = getDtds();   
                byte[] dtd = (byte[])dtds.get(systemId);
                if (dtd == null)
                {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    URL url = new URL(systemId);
                    Streams.drain(url.openStream(), baos);
                    dtd = baos.toByteArray();
                    dtds.put(systemId, dtd);                    
                }
                                
                if (dtd != null)
                {
                    ByteArrayInputStream bais = new ByteArrayInputStream(dtd);
                    InputSource is = new InputSource(bais);
                    is.setPublicId( publicId );
                    is.setSystemId( systemId );
                                        
                    return is;
                }
            } 
            catch(Throwable t ) // java.io.IOException x  
            {
                t.printStackTrace();
                log.error("failed to get URL input source", t);
            }
            
            // forces to get dtd over internet
            return null;
        }
    
    }

    // DTD Map     
    static private Map dtds = new HashMap();
    
    public static Map getDtds()
    {
        return dtds;
    }

    public static void clearDtdCache()
    {
        dtds.clear();
    }
    
}
