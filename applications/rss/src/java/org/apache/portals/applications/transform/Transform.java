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
package org.apache.portals.applications.transform;

import java.io.OutputStream;
import java.io.Writer;
import java.util.Map;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;


/**
 * This interface is a facade for all Transformation related operations.
 * Transformation service abstracts the XSL Transform manipulation, maintenance,
 * caching and transformation resolution algorithms.
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface Transform
{
    
    /**
     * Performs a transform on an input stream, writing the transformed content to a Writer
     * 
     * @param xsltPath The path to a local XSLT file 
     * @param inputSource The input stream and description containing content to be transformed.
     * @param writer The output stream receiving the transformed content.
     * @param properties Map of XSLT properties passed into transformer
     */    
    public void transform(String xsltPath, InputSource inputSource, Writer writer, Map properties)
        throws TransformException;

    /**
     * Performs a transform on an input stream, writing the transformed content to a Writer
     * 
     * @param xsltPath The path to a local XSLT file 
     * @param inputSource The input stream and description containing content to be transformed.
     * @param outputStream The output stream receiving the transformed content.
     * @param properties Map of XSLT properties passed into transformer
     */    
    public void transform(String xsltPath, InputSource inputSource, OutputStream os, Map properties)
        throws TransformException;
    
        
    /**
     * Performs a transform on an input stream, writing the transformed content to a Writer
     * 
     * @param xsltPath The path to a local XSLT file 
     * @param document The W3C document to be transformed.
     * @param outputStream The output stream receiving the transformed content.
     * @param properties Map of XSLT properties passed into transformer
     */    
    public TransformObjectPublisher getPublisher();

    public void transform(String xsltPath, Document document, OutputStream os, Map parameters)
        throws TransformException;
    
}
