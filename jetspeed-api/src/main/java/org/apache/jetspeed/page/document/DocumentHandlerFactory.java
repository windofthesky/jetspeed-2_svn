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
package org.apache.jetspeed.page.document;

/**
 * <p>
 * DocumentHandlerFactory
 * </p>
 * <p>
 *  Factory for generating <code>DocumentHandlers</code> for specific document types
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public interface DocumentHandlerFactory
{
    /**
     * 
     * <p>
     * getDocumentHandler
     * </p>     
     *
     * @param documentType document type to retreive a handler for.  Examples: .psml, .link
     * @return DocumentHanlder for the <code>documentType</code> indicated.  Never returns <code>null.</code>
     * @throws UnsupportedDocumentTypeException If no handler has been registered for the
     * <code>documentType</code> argument.
     */
    DocumentHandler getDocumentHandler(String documentType) throws UnsupportedDocumentTypeException;
    
    /**
     * 
     * <p>
     * getDocumentHandlerForPath
     * </p>
     *
     * @param documentPath
     * @return
     * @throws UnsupportedDocumentTypeException
     */
    DocumentHandler getDocumentHandlerForPath( String documentPath) throws UnsupportedDocumentTypeException;
    
    /**
     * 
     * <p>
     * addDocumentHandler
     * </p>
     *
     * @param documentHandler
     */
    void registerDocumentHandler(DocumentHandler documentHandler) throws DocumentTypeAlreadyRegisteredException;
    
    /**
     * <p>
     * getConstraintsEnabled
     * </p>
     *
     * @return enabled indicator
     */
    boolean getConstraintsEnabled();

    /**
     * <p>
     * setConstraintsEnabled
     * </p>
     *
     * @param enabled indicator
     */
    void setConstraintsEnabled(boolean enabled);

    /**
     * <p>
     * getPermissionsEnabled
     * </p>
     *
     * @return enabled indicator
     */
    boolean getPermissionsEnabled();

    /**
     * <p>
     * setPermissionsEnabled
     * </p>
     *
     * @param enabled indicator
     */
    void setPermissionsEnabled(boolean enabled);
    
    /**
     * shutdown - gracefully shutdown handlers and disconnect
     * from other singleton components, (e.g. shared caches) 
     */
    public void shutdown();
}
