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
package org.apache.jetspeed.rewriter;

import java.io.Reader;

import org.apache.jetspeed.rewriter.rules.Ruleset;

/**
 * RewriterService
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface RewriterController 
{
    public String SERVICE_NAME = "rewriter";

    /**
     * Creates a basic rewriter that does not support rulesets configurations.
     * The Rewriter implementation is configured in the service configuration.
     *  
     * @return A new rewriter that does not support rulesets.
     */
    Rewriter createRewriter()
        throws RewriterException;

    /**
     * Creates a rewriter that supports rulesets configurations.
     * The rewriter uses the rulesets configuration to control rewriting.
     * The Rewriter implementation is configured in the service configuration.
     * 
     * @param ruleset The ruleset configuration to control the rewriter.
     * @return A new rewriter that supports rulesets.
     */
    RulesetRewriter createRewriter(Ruleset ruleset)
        throws RewriterException;
    

    /**
     * Creates a Parser Adaptor for the given mime type
     * The Parser Adaptor implementation is configured in the service configuration.
     * Only MimeTypes of "text/html" and "text/xml" are currently supported.
     * 
     * @param mimeType The mimetype to create a parser adaptor for.
     * @return A new parser adaptor
     */
    ParserAdaptor createParserAdaptor(String mimeType)
        throws RewriterException;
    
    /**
     * Loads a XML-based Rewriter Ruleset given a stream to the XML configuration.
     * 
     * @param reader The stream to the XML configuration.
     * @return A Ruleset configuration tree.
     */
    Ruleset loadRuleset(Reader reader);
       
    /**
     * Lookup a Ruleset given a ruleset identifier.
     * 
     * @param id The identifier for the Ruleset.
     * @return A Ruleset configuration tree.
     */
    Ruleset lookupRuleset(String id);
    
}
