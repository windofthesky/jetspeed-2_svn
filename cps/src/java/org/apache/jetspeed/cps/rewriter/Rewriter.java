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
package org.apache.jetspeed.cps.rewriter;

import java.io.Reader;
import java.io.Writer;

/**
 * Rewriter
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface Rewriter
{
    /**
     * Parses the reader of content receiving call backs for rewriter events.
     * This method does not rewrite, but only parses. Useful for readonly operations.
     * The configured parser can parse over different stream formats returning a
     * normalized (org.sax.xml) attribute and element based events.
     *
     * @param adaptor the parser adaptor which handles generating SAX-like events called back on this object. 
     * @param reader the input stream over the content to be parsed.
     * @exception RewriteException when a parsing error occurs or unexpected content is found.
     */
    void parse(ParserAdaptor adaptor, Reader reader)
            throws RewriterException;

    /**
     * Parses the reader of content receiving call backs for rewriter events.
     * The content is rewritten to the output stream.
     * The configured parser can parse over different stream formats returning a
     * normalized (org.sax.xml) attribute and element based events. 
     *
     * @param adaptor the parser adaptor which handles generating SAX-like events called back on this object. 
     * @param reader the input stream over the content to be parsed.
     * @param writer the output stream where content is rewritten to.
     * @exception RewriteException when a parsing error occurs or unexpected content is found.
     */                               
    void rewrite(ParserAdaptor adaptor, Reader reader, Writer writer)
        throws RewriterException;
                                       

    /** 
     * This event is the inteface between the Rewriter and ParserAdaptor for rewriting URLs.
     * The ParserAdaptor calls back the Rewriter when it finds a URL that is a candidate to be
     * rewritten. The Rewriter rewrites the URL and returns it as the result of this function. 
     * 
     * @param url the URL to be rewritten
     * @param tag The tag being processed
     * @param attribute The current attribute being processsed
     */
    String rewriteUrl(String url, String tag, String attribute);

    /**
     * Returns true if the tag should be removed, otherwise false.
     * Removing a tag only removes the tag but not the contents in 
     * between the start and end tag.
     * 
     * @return true if the tag should be removed.
     */
    boolean shouldRemoveTag(String tag);

    /**
     * Returns true if the tag should be stripped, otherwise false.
     * Stripping tags removes the start and end tag, plus all tags
     * and content in between the start and end tag.
     * 
     * @return true if the tag should be stripped.
     */
    boolean shouldStripTag(String tag);

    /**
     * Returns true if all comments should be removed.
     * 
     * @return true If all comments should be removed.
     */    
    boolean shouldRemoveComments();
    
    /**
     * Sets the base URL for rewriting. This URL is the base 
     * from which other URLs are generated.
     * 
     * @param base The base URL for this rewriter
     */
    void setBaseUrl(String base);
    
    /**
     * Gets the base URL for rewriting. This URL is the base 
     * from which other URLs are generated.
     * 
     * @return The base URL for this rewriter
     */
    String getBaseUrl();

    /**
     * Gets whether this rewriter require a proxy server.
     * 
     * @return true if it requires a proxy
     */
    boolean getUseProxy();
    
    /**
     * Set whether this rewriter require a proxy server.
     * 
     * @param useProxy true if it requires a proxy
     */    
    void setUseProxy(boolean useProxy);
    
    /**
     * Rewriter event called back on the leading edge of processing a simple tag by the ParserAdaptor.
     * Returns false to indicate to the ParserAdaptor to short-circuit processing on this tag.
     * 
     * @param tag The name of the tag being processed.
     * @param attrs The attribute list for the tag.
     * @return Should return true to continue processing the tag in the ParserAdaptor, false to indicate that processing is completed.
     */
    boolean enterSimpleTagEvent(String tag, MutableAttributes attrs);
    
    /**
     * Rewriter event called back on the trailing edge of a simple tag by the ParserAdaptor.
     * Returns a String that can be appended to the rewritten output for the given tag, or null to indicate no content available. 
     *  
     * @param tag The name of the tag being processed.
     * @param attrs The attribute list for the tag.
     * @return Returns a String that can be appended to the rewritten output for the given tag, or null to indicate no content available.
     */    
    String exitSimpleTagEvent(String tag, MutableAttributes attrs);

    /**
     * Rewriter event called back on the leading edge of processing a start tag by the ParserAdaptor.
     * Returns false to indicate to the ParserAdaptor to short-circuit processing on this tag.
     * 
     * @param tag The name of the tag being processed.
     * @param attrs The attribute list for the tag.
     * @return Should return true to continue processing the tag in the ParserAdaptor, false to indicate that processing is completed.
     */
    boolean enterStartTagEvent(String tag, MutableAttributes attrs);
    
    /**
     * Rewriter event called back on the trailing edge of a start tag by the ParserAdaptor.
     * Returns a String that can be appended to the rewritten output for the given tag, or null to indicate no content available. 
     *  
     * @param tag The name of the tag being processed.
     * @param attrs The attribute list for the tag.
     * @return Returns a String that can be appended to the rewritten output for the given tag, or null to indicate no content available.
     */        
    String exitStartTagEvent(String tag, MutableAttributes attrs);

    /**
     * Rewriter event called back on the leading edge of processing an end tag by the ParserAdaptor.
     * Returns false to indicate to the ParserAdaptor to short-circuit processing on this tag.
     * 
     * @param tag The name of the tag being processed.
     * @param attrs The attribute list for the tag.
     * @return Should return true to continue processing the tag in the ParserAdaptor, false to indicate that processing is completed.
     */
    boolean enterEndTagEvent(String tag);

    /**
     * Rewriter event called back on the trailing edge of a end tag by the ParserAdaptor.
     * Returns a String that can be appended to the rewritten output for the given tag, or null to indicate no content available. 
     *  
     * @param tag The name of the tag being processed.
     * @param attrs The attribute list for the tag.
     * @return Returns a String that can be appended to the rewritten output for the given tag, or null to indicate no content available.
     */            
    String exitEndTagEvent(String tag);

    /**
     * Rewriter event called back when text is found for 
     * Returns false to indicate to the ParserAdaptor to short-circuit processing on this tag.
     * 
     * @param values an array of characters containing the text.
     * @param param 
     * @return Should return true to continue processing the tag in the ParserAdaptor, false to indicate that processing is completed.
     */
    boolean enterText(char[] values, int param);

    /**
     * Rewriter event called back just before tag conversion (rewriter callbacks) begins by the ParserAdaptor.
     * 
     * @param tag The name of the tag being processed.
     * @param attrs The attribute list for the tag.
     */
    void enterConvertTagEvent(String tag, MutableAttributes attrs);
    
}
