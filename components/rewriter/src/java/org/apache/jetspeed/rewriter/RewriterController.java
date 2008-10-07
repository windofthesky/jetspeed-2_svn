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
