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
package org.apache.jetspeed.cps.rewriter.html;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Enumeration;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.cps.rewriter.ParserAdaptor;
import org.apache.jetspeed.cps.rewriter.Rewriter;
import org.apache.jetspeed.cps.rewriter.RewriterException;

/**
 * HTML Parser Adaptor for the Swing 'HotJava' parser.
 * This is the old implementation
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 * @deprecated
 */
public class SwingParserAdaptor2 implements ParserAdaptor
{
    protected final static Log log = LogFactory.getLog(SwingParserAdaptor2.class);

    private SwingParserAdaptor2.Callback callback = null;
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
    public SwingParserAdaptor2()
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
            HTMLEditorKit.Parser parser = new SwingParserAdaptor2.ParserGetter().getParser();                    
            callback = new SwingParserAdaptor2.Callback(writer);
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
            HTMLEditorKit.Parser parser = new SwingParserAdaptor2.ParserGetter().getParser();        
            callback = new SwingParserAdaptor2.Callback(null);
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

        public HTMLEditorKit.Parser getParser(){
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
        private boolean emit = true;
        private boolean simpleTag = false;

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
             if (false == emit)                               
                 return;                                      
             if (values[0] == '>')                            
                 return;     
             if (false == rewriter.enterText(values, param))
                return;                    

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
        public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet attrs, int param) 
        {
            simpleTag = true;
            
            if (false == rewriter.enterSimpleTagEvent(tag.toString(), new SwingAttributes(attrs)))
                return;

            if (false == isValidFragmentTag(tag))
                return;

            try
            {
                appendTagToResult(tag,attrs);        
                if (tag.toString().equalsIgnoreCase("param") ||
                    tag.toString().equalsIgnoreCase("object") ||
                    tag.toString().equalsIgnoreCase("embed"))
                {
                    write(lineSeparator);
                }
                simpleTag = false;
                String appended = rewriter.exitSimpleTagEvent(tag.toString(), new SwingAttributes(attrs));
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
        public void handleStartTag(HTML.Tag tag,  MutableAttributeSet attrs, int position) 
        {
            if (false == rewriter.enterStartTagEvent(tag.toString(), new SwingAttributes(attrs)))
                return;

            if (tag == HTML.Tag.HEAD)
            {
                emit = false;
                return;
            }

           if (false == isValidFragmentTag(tag))
                return;

            try
            {
                appendTagToResult(tag,attrs);
                formatLine(tag);
                String appended = rewriter.exitStartTagEvent(tag.toString(), new SwingAttributes(attrs));
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
        


        boolean isValidFragmentTag(HTML.Tag tag)
        {
            if (rewriter.shouldRemoveTag(tag.toString()))
            {
                return false;                                
            }
            /*
            if (false == emit)
                return false;

            if (tag == HTML.Tag.HTML)  // always strip out HTML tag for fragments
                return false;

            if (tag == HTML.Tag.BODY)
                return false;

            if (tag == HTML.Tag.FRAMESET)  // always strip out FRAMESET tag for fragments
                return false;

            if (tag == HTML.Tag.FRAME)  
                return false;

            if (tag == HTML.Tag.NOFRAMES)  
                return false;
              */
            return true;
        }


        /*
         * Hot Java event callback for handling an end tag.
         *
         * @param tag The HTML tag being handled.
         * @param position the position of the tag.
         *
         */
        public void handleEndTag(HTML.Tag tag, int position) 
        {
            if (false == rewriter.enterEndTagEvent(tag.toString()))
                return;

            if (tag == HTML.Tag.HEAD)
            {
                emit = true;
                return;
            }

            if (false == isValidFragmentTag(tag))
                return;
                
            try
            {                            
                addToResult("</").addToResult(tag).addToResult(">");
    
                formatLine(tag);
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
         * Hot Java event callback for handling errors.
         *
         * @param str The error message from Swing.
         * @param param A parameter passed to handler.
         *
         */
        public void handleError(java.lang.String str,int param) 
        {
            // ignored
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
            // STRIP COMMENTS: addToResult(values);
            // this is questionable, we may need to turn this on for scripts inside comments
        }

        /*
         * Hot Java event callback for end of line strings.
         *
         * @param str The end-of-line string.
         *
         */
        public void handleEndOfLineString(java.lang.String str) 
        {
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
            } catch (Exception e)
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

            } catch (Exception e)
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
            } catch (Exception e)
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
            if ((tag == HTML.Tag.A) && 
                (attrs.getAttribute(HTML.Attribute.HREF) != null))
            {

                // ---- CHECKING <A HREF
                addProxiedConvertedAttribute( tag, HTML.Attribute.HREF, attrs);

            } 
            else if (((tag == HTML.Tag.IMG || 
                         tag == HTML.Tag.INPUT
                        ) && 
                        (attrs.getAttribute(HTML.Attribute.SRC) != null)
                       ))
            {

                // ---- CHECKING <IMG SRC & <INPUT SRC
                addConvertedAttribute( tag,
                                       HTML.Attribute.SRC, 
                                       attrs, 
                                       rewriter.getUseProxy());    

            } else if (((tag == HTML.Tag.OPTION) ) && 
                       (attrs.getAttribute(HTML.Attribute.VALUE) != null))
            {
                // ---- CHECKING <OPTION 
                addProxiedConvertedAttribute( tag, HTML.Attribute.VALUE, attrs );

            } else if (((tag == HTML.Tag.LINK) ) && 
                       (attrs.getAttribute(HTML.Attribute.HREF) != null))
            {

                // ---- CHECKING <LINK
                addConvertedAttribute( tag,
                                       HTML.Attribute.HREF,
                                       attrs,
                                       rewriter.getUseProxy());

            } else if ( tag == HTML.Tag.APPLET )
            {

                // ---- CHECKING <APPLET CODEBASE=
                addConvertedAttribute( tag,
                                       HTML.Attribute.CODEBASE,
                                       attrs,
                                       rewriter.getUseProxy());

            } else if ( tag == HTML.Tag.FRAME )
            {

                // ---- CHECKING <FRAME SRC=
                addProxiedConvertedAttribute( tag, HTML.Attribute.SRC, attrs);

            } else if ( tag == HTML.Tag.SCRIPT )
            {
                // ---- CHECKING <SCRIPT SRC=
                if (attrs.getAttribute(HTML.Attribute.SRC) != null)
                {

                    // script is external
                    String s = attrs.getAttribute(HTML.Attribute.SRC).toString();
                    if (s.indexOf("%3E") == -1)
                    {
                        addConvertedAttribute( tag,
                                               HTML.Attribute.SRC, 
                                               attrs,
                                               rewriter.getUseProxy());
                    }

                } else
                {
                    // script is inline
                    //parserOff = true;
                }

            } else if (tag == HTML.Tag.FORM)
            {

                // ---- CHECKING <FORM ACTION=
                inForm = true; // buggy <form> handling in jdk 1.3 

                if (attrs.getAttribute(HTML.Attribute.ACTION) == null)
                {
                    // always post
                    attrs.addAttribute(HTML.Attribute.METHOD, "POST");                      
                    //self referencing <FORM>
                    
                    // attrs.addAttribute(HTML.Attribute.ACTION,
                    //                   baseURL);

                } else
                {
                    // always post
                    attrs.addAttribute(HTML.Attribute.METHOD, "POST");                      
                    addProxiedConvertedAttribute( tag, HTML.Attribute.ACTION, attrs);

                }

            } else if (((tag == HTML.Tag.AREA) ) && 
                       (attrs.getAttribute(HTML.Attribute.HREF) != null))
            {

                // ---- CHECKING <AREA
                addProxiedConvertedAttribute( tag, HTML.Attribute.HREF,
                                              attrs );

            } else if (((tag == HTML.Tag.BODY) ) && 
                       (attrs.getAttribute(HTML.Attribute.BACKGROUND) != null))
            {

                // ---- CHECKING <BODY
                addConvertedAttribute( tag,
                                       HTML.Attribute.BACKGROUND,
                                       attrs,
                                       rewriter.getUseProxy());

            } else if (tag == HTML.Tag.TD)
            {
                // ---- CHECKING <TD BACKGROUND=
                if (! (attrs.getAttribute(HTML.Attribute.BACKGROUND) == null))
                {
                    addConvertedAttribute( tag,
                                           HTML.Attribute.BACKGROUND,
                                           attrs,
                                           rewriter.getUseProxy());
                }
            }

            /*
              if ( removeScript && (tag == HTML.Tag.SCRIPT)) {
                ignoreLevel ++;
              */
        }

        /*
         * Converts the given attribute's URL compatible element to a proxied URL.
         * Uses the proxy parameter to determine if the URL should be written back as a
         * proxied URL, or as a fullpath to the original host.
         *
         * @param attr The HTML attribute to be proxied.
         * @param attrs The mutable HTML attribute set for the current HTML element.
         * @param proxy If set true, the URL is written back as a proxied URL, otherwise
         * it is written back as a fullpath back to the original host.
         *
         */
        private void addConvertedAttribute( HTML.Tag tag,
                                            HTML.Attribute attr,
                                            MutableAttributeSet attrs,
                                            boolean proxy ) 
        {
            if (proxy)
            {
                addProxiedConvertedAttribute(tag, attr,attrs);
            } else
            {
                if ( attrs.getAttribute( attr ) != null )
                {
                    attrs.addAttribute( attr,
                                        generateNewUrl( tag, attrs, attr, false ) );
                }
            }
        }


        /**
         *
         * Converts the given attribute's URL compatible element to a proxied URL.
         * This method will always add the proxy host prefix to the rewritten URL.
         *
         * @param attr The HTML attribute to be proxied.
         * @param attrs The mutable HTML attribute set for the current HTML element.
         *
         */
        private void addProxiedConvertedAttribute( HTML.Tag tag,
                                                   HTML.Attribute attr,
                                                   MutableAttributeSet attrs ) {



            if ( attrs.getAttribute( attr ) != null )
            {
                String attrSource =  attrs.getAttribute( attr ).toString();

                // special case: mailto should not be sent to the proxy server
                if (attrSource.startsWith("mailto:"))
                {
                    attrs.addAttribute( attr,
                                        generateNewUrl( tag, attrs, attr, true ) );
                } else if (attrSource.startsWith("javascript:"))
                {
                    attrs.addAttribute( attr,
                                        attrSource);
                } else
                {
                    attrs.addAttribute( attr,
                                        generateNewUrl( tag, attrs, attr, true ) );
                }
            }
        }

        /*
         * Calls the rewriter's URL generator callback, which will translate the old url
         * into a new fullpath URL, either relative to the proxy server, or a fullpath
         * to the original web server, depending on the 'proxied' parameter.
         * 
         * @param oldURL The original URL, before it is tranlated.
         * @param proxied Boolean indicator denotes if the URL should be written back
         *        as a proxied URL (true), or as a fully addressable address to the 
         *       original web server.
         * @return The translated new URL.
         *         
         */
        private String generateNewUrl(HTML.Tag tag,
                                      MutableAttributeSet attrs,
                                      HTML.Attribute attr,
                                      boolean proxied)
        {
            String oldURL =  attrs.getAttribute( attr ).toString();
            // System.out.println("Generating new url: " + oldURL);
            return rewriter.rewriteUrl(oldURL, tag.toString(), attr.toString());
        }


    }

}
