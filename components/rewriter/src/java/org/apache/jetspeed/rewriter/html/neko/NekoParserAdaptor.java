/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.rewriter.html.neko;

import java.io.Reader;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.parser.XMLInputSource;

import org.apache.jetspeed.rewriter.ParserAdaptor;
import org.apache.jetspeed.rewriter.Rewriter;
import org.apache.jetspeed.rewriter.RewriterException;

import org.xml.sax.SAXException ;

import org.cyberneko.html.parsers.SAXParser;
import org.cyberneko.html.filters.DefaultFilter;
import org.cyberneko.html.filters.Purifier;
import org.cyberneko.html.filters.Writer;


/**
 * <p>
 * NekoParserAdapter
 * </p>
 * <p>
 *  
 * </p>
 * @author <a href="mailto:dyoung@phase2systems.com">David L Young</a>
 * @version $Id: $
 *
 */
public class NekoParserAdaptor implements ParserAdaptor
{
    protected final static Log log = LogFactory.getLog(NekoParserAdaptor.class);
    
    /*
     * Construct a cyberneko HTML parser adaptor
     */
    public NekoParserAdaptor()
    {
        super();
    }
    
    /**
     * <p>
     * parse
     * </p>
     *
     * @see org.apache.jetspeed.rewriter.ParserAdaptor#parse(org.apache.jetspeed.rewriter.Rewriter, java.io.Reader)
     * @param rewriter
     * @param reader
     * @throws RewriterException
     */
    public void parse(Rewriter rewriter, Reader reader)
            throws RewriterException
    {
        // not sure what this means to parse without rewriting
        rewrite(rewriter,reader,null);
    }

    /**
     * <p>
     * rewrite
     * </p>
     *
     * @see org.apache.jetspeed.rewriter.ParserAdaptor#rewrite(org.apache.jetspeed.rewriter.Rewriter, java.io.Reader, java.io.Writer)
     * @param rewriter
     * @param reader
     * @param writer
     * @throws RewriterException
     */
    public void rewrite(Rewriter rewriter, java.io.Reader reader, java.io.Writer writer)
            throws RewriterException
    {
        // use a cyberneko SAXParser
        SAXParser parser = new SAXParser() ;

        // setup filter chain
        XMLDocumentFilter[] filters = {
            new Purifier(),                                                                                  // [1] standard neko purifications (tag balancing, etc)
            new CallbackElementRemover( rewriter ),                                                          // [2] accept / reject tags based on advice from rewriter
            writer != null ? new org.cyberneko.html.filters.Writer( writer, null ) : new DefaultFilter()     // [3] propagate results to specified writer (or do nothing -- Default -- when writer is null)
        };
        
        String filtersPropName = "http://cyberneko.org/html/properties/filters";
   
        try
        {
            parser.setProperty(filtersPropName, filters);
        }
        catch (SAXException e)
        {
            // either no longer supported (SAXNotSupportedException), or no logner recognized (SAXNotRecognizedException)
            log.error(filtersPropName + " is, unexpectedly, no longer defined for the cyberneko HTML parser",e);
            throw new RewriterException("cyberneko parser version not supported",e);
        }

        try
        {
            // parse from reader
            parser.parse(new XMLInputSource( null, null, null, reader, null )) ;
        }
        catch (IOException e)
        {
            String msg = "cyberneko HTML parsing failure";
            log.error(msg,e);
            throw new RewriterException(msg,e);
        }

    }

}
