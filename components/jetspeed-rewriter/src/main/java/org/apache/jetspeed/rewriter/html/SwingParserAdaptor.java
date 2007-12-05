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
package org.apache.jetspeed.rewriter.html;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Enumeration;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.rewriter.ParserAdaptor;
import org.apache.jetspeed.rewriter.Rewriter;
import org.apache.jetspeed.rewriter.RewriterException;

/**
 * HTML Parser Adaptor for the Swing 'HotJava' parser.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class SwingParserAdaptor implements ParserAdaptor
{
    protected final static Log log = LogFactory.getLog(SwingParserAdaptor.class);

    private SwingParserAdaptor.Callback callback = null;
    private String lineSeparator;
    private boolean skippingImplied = false;
    private Rewriter rewriter;
    
    /*
     * Construct a swing (hot java) parser adaptor
     * Receives a Rewriter parameter, which is used as a callback when rewriting URLs.
     * The rewriter object executes the implementation specific URL rewriting.
     *
     * @param rewriter The rewriter object that is called back during URL rewriting
     */
    public SwingParserAdaptor()
    {
        lineSeparator = System.getProperty("line.separator", "\r\n");         
    }

    /*
     * Parses and an HTML document, rewriting all URLs as determined by the Rewriter callback
     *
     *
     * @param reader The input stream reader 
     *
     * @throws MalformedURLException 
     *
     * @return An HTML-String with rewritten URLs.
     */    
    public void rewrite(Rewriter rewriter, Reader reader, Writer writer)
        throws RewriterException
    {
        try
        {
            this.rewriter = rewriter;            
            HTMLEditorKit.Parser parser = new SwingParserAdaptor.ParserGetter().getParser();                    
            callback = new SwingParserAdaptor.Callback(writer);
            parser.parse(reader, callback, true);
        } 
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RewriterException(e);
        }
    }

    public void parse(Rewriter rewriter, Reader reader)
        throws RewriterException    
    {
        try
        {
            this.rewriter = rewriter;            
            HTMLEditorKit.Parser parser = new SwingParserAdaptor.ParserGetter().getParser();        
            callback = new SwingParserAdaptor.Callback(null);
            parser.parse(reader, callback, true);
        } 
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RewriterException(e);
        }
    }
    
    /*
     * This Class is needed, because getParser is protected and therefore 
     *  only accessibly by a subclass
     */
    class ParserGetter extends HTMLEditorKit
    {

        public HTMLEditorKit.Parser getParser()
        {
            return super.getParser();
        }
    } 
    
    /*
     *  Swing Parser Callback from the HTMLEditorKit.
     * This class handles all SAX-like events during parsing.
     *
     */
    class Callback extends HTMLEditorKit.ParserCallback
    {
        // either handling of <FORM> is buggy, or I made some weird mistake ... 
        // ... JDK 1.3 sends double "</form>"-tags on closing <form>
        private boolean inForm = false; 
        private boolean inScript = false; 
        private boolean strip = false;
        private boolean simpleTag = false;
        private String stripTag = null;
        private Writer writer = null;

        private Callback (Writer writer) 
        {
            this.writer = writer;
        }

        //
        // -------------- Hot Java event callbacks... --------------------
        //

        /*
         *  Hot Java event callback for text (all data in between tags)
         * 
         * @param values The array of characters containing the text.
         */
        public void handleText(char[] values,int param) 
        {
             if (strip)
             {                               
                 return;
             }                                      
             if (values[0] == '>')
             {                            
                 return;
             }     
             if (false == rewriter.enterText(values, param))
             {
                return;
             }                    

            addToResult(values);
        }

        private void write(String text)
            throws IOException
        {
            if (writer != null)
            {
                writer.write(text);
            }
        }
        
        /*
         * Hot Java event callback for handling a simple tag (without begin/end)
         *
         * @param tag The HTML tag being handled.
         * @param attrs The mutable HTML attribute set for the current HTML element.         
         * @param position the position of the tag.         
         *
         */
        public void handleSimpleTag(HTML.Tag htmlTag, MutableAttributeSet attrs, int param) 
        {
            String tag = htmlTag.toString();
            
            if (false == rewriter.enterSimpleTagEvent(tag, new SwingAttributes(attrs)))
            {
                return;
            }

            if (strip)
            {
                return;
            }
            
            if (rewriter.shouldStripTag(tag))
            {
                return;            
            }
            
            if (rewriter.shouldRemoveTag(tag))
            {
                return;
            }
            
            try
            {
                simpleTag = true;                
                appendTagToResult(htmlTag, attrs);
                write(lineSeparator);
/*
                if (tag.toString().equalsIgnoreCase("param") ||
                    tag.toString().equalsIgnoreCase("object") ||
                    tag.toString().equalsIgnoreCase("embed"))
                {
                    write(lineSeparator);
                }
*/                
                simpleTag = false;
                String appended = rewriter.exitSimpleTagEvent(tag, new SwingAttributes(attrs));
                if (null != appended)
                {
                    write(appended);
                }
            }
            catch (Exception e)
            {
                log.error("Simple tag parsing error", e);                    
            }
        }

        /*
         * Hot Java event callback for handling a start tag.
         *
         * @param tag The HTML tag being handled.
         * @param attrs The mutable HTML attribute set for the current HTML element.         
         * @param position the position of the tag.         
         *
         */
        public void handleStartTag(HTML.Tag htmlTag,  MutableAttributeSet attrs, int position) 
        {
            String tag = htmlTag.toString();
            
            if (false == rewriter.enterStartTagEvent(tag, new SwingAttributes(attrs)))
            {
                return;
            }
            
            if (strip)
            {
                return;
            }
            
            if (rewriter.shouldStripTag(tag))
            {
                stripTag = tag;
                strip = true;
                return;            
            }
            
            if (rewriter.shouldRemoveTag(tag))
            {
                return;
            }
            
            try
            {
                appendTagToResult(htmlTag, attrs);
                formatLine(htmlTag);
                String appended = rewriter.exitStartTagEvent(tag, new SwingAttributes(attrs));
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
        


        /*
         * Hot Java event callback for handling an end tag.
         *
         * @param tag The HTML tag being handled.
         * @param position the position of the tag.
         *
         */
        public void handleEndTag(HTML.Tag htmlTag, int position) 
        {
            String tag = htmlTag.toString();
            if (false == rewriter.enterEndTagEvent(tag.toString()))
            {
                return;
            }
            
            if (strip)
            {
                if (tag.equalsIgnoreCase(stripTag))
                {
                    strip = false;
                    stripTag = null;
                }
                return;
            }
            
            if (rewriter.shouldRemoveTag(tag))
            {
                return;                                
            }
             
            try
            {                            
                addToResult("</").addToResult(tag).addToResult(">");
    
                // formatLine(htmlTag);
                write(lineSeparator);
                
                String appended = rewriter.exitEndTagEvent(tag);
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
         * Hot Java event callback for handling errors.
         *
         * @param str The error message from Swing.
         * @param param A parameter passed to handler.
         *
         */
        public void handleError(java.lang.String str,int param) 
        {
            // System.out.println("Handling error: " + str);
        }

        /*
         * Hot Java event callback for HTML comments.
         *
         * @param values The character array of text comments.
         * @param param A parameter passed to handler.
         *
         */
        public void handleComment(char[] values,int param) 
        {
            if (strip || rewriter.shouldRemoveComments())
            {
                return;             
            }
            addToResult("<!-- ").addToResult(values).addToResult(" -->").addToResult(lineSeparator);
        }

        /*
         * Hot Java event callback for end of line strings.
         *
         * @param str The end-of-line string.
         *
         */
        public void handleEndOfLineString(java.lang.String str) 
        {
            if (strip)
            {                               
                return;
            }                                      
            
            addToResult(lineSeparator);
            addToResult(str);
        }


        /*
         * Prints new lines to make the output a little easier to read when debugging.
         *
         * @param tag The HTML tag being handled.         
         *
         */
        private void formatLine(HTML.Tag tag)
        {
            try
            {
                if (tag.isBlock() || 
                    tag.breaksFlow() || 
                    tag == HTML.Tag.FRAME ||
                    tag == HTML.Tag.FRAMESET ||
                    tag == HTML.Tag.SCRIPT)
                {
                    write(lineSeparator);
                }
                
            }                    
            catch (Exception e)
            {
                log.error("Format Line tag parsing error", e);                    
            }
            
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
        private Callback addToResult(Object txt)
        {
            // to allow for implementation using Stringbuffer or StringWriter
            // I don't know yet, which one is better in this case
            //if (ignoreLevel > 0 ) return this;

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
         * Used to write all character content to the output stream.
         * Returns a reference to itself so that these calls can be chained.
         *
         * @param txt Any character text to be written out directly to stream.
         * @return A handle to the this, the callback, for chaining results.
         *
         */
        private Callback addToResult(char[] txt)
        {
            //if (ignoreLevel > 0) return this;

            try
            {
                if (writer != null)
                {
                    writer.write(txt);
                }

            } 
            catch (Exception e)
            { /* ignore */
            }
            return this;
        }

        /* 
         * Accessor to the Callback's content-String
         *
         * @return Cleaned and rewritten HTML-Content
         */        
        public void getResult() 
        {
            try
            {
                if (writer != null)
                {
                    writer.flush();
                }
            } 
            catch (Exception e)
            { /* ignore */
            }

            // WARNING: doesn't work, if you remove " " + ... but don't know why
            //String res = " " + result.toString(); 

            // return res;
        }

        /*
         * Flushes the output stream. NOT IMPLEMENTED
         *
         */
        public void flush() throws javax.swing.text.BadLocationException 
        {
            // nothing to do here ...
        }

        /*
         * Writes output to the final stream for all attributes of a given tag.
         *
         * @param tag The HTML tag being output.
         * @param attrs The mutable HTML attribute set for the current HTML tag.
         *
         */
        private void appendTagToResult(HTML.Tag tag, MutableAttributeSet attrs) 
        {
            convertURLS(tag, attrs);
            Enumeration e = attrs.getAttributeNames();
            addToResult("<").addToResult(tag);
            while (e.hasMoreElements())
            {
                Object attr = e.nextElement();
                String value = attrs.getAttribute(attr).toString();
                addToResult(" ").addToResult(attr).addToResult("=\"").
                addToResult(value).addToResult("\"");
            }        
            if (simpleTag)
                addToResult("/>");
            else             
                addToResult(">");
        }


        /*
         * Determines which HTML Tag/Element is being inspected, and calls the 
         * appropriate converter for that context.  This method contains all the
         * logic for determining how tags are rewritten. 
         *
         * @param tag TAG from the Callback-Interface.
         * @param attrs The mutable HTML attribute set for the current HTML element.
         */

        private void convertURLS( HTML.Tag tag, MutableAttributeSet attrs ) 
        {
            rewriter.enterConvertTagEvent(tag.toString(), new SwingAttributes(attrs));

            /*
              if ( removeScript && (tag == HTML.Tag.SCRIPT)) {
                ignoreLevel ++;
              */
        }


    }
    
}