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
import java.io.Writer;

/**
 * Interface for HTML Parser Adaptors.
 * Adaptors normalize the interface over HTML and XML adaptor implementations.
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface ParserAdaptor
{
    /**
     * Parses a document from the reader, without actually rewriting URLs.
     * During parsing the events are called back on the given rewriter to handle the normalized events.
     *
     * @param reader the input stream over the content to be parsed.
     * @exception RewriteException when a parsing error occurs or unexpected content is found.
     */        
    void parse(Rewriter rewriter, Reader reader)
            throws RewriterException;

    /**
     * Parses and rewrites a document from the reader, rewriting URLs via the rewriter's events to the writer.
     * During parsing the rewriter events are called on the given rewriter to handle the rewriting.
     *
     * @param reader the input stream over the content to be parsed.
     * @param writer the output stream where content is rewritten to.
     * @exception RewriteException when a parsing error occurs or unexpected content is found.
     */            
    void rewrite(Rewriter rewriter, Reader reader, Writer writer)
        throws RewriterException;
    
}
