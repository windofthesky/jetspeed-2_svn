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
package org.apache.jetspeed.page.document;

import java.util.HashMap;
import java.util.Map;

import org.apache.jetspeed.page.document.DocumentHandler;
import org.apache.jetspeed.page.document.DocumentHandlerFactory;
import org.apache.jetspeed.page.document.DocumentTypeAlreadyRegisteredException;
import org.apache.jetspeed.page.document.FolderHandler;
import org.apache.jetspeed.page.document.UnsupportedDocumentTypeException;
import org.apache.jetspeed.util.ArgUtil;

/**
 * <p>
 * DocumentHandlerFactoryImpl
 * </p>
 * <p>
 * 
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 *  
 */
public class DocumentHandlerFactoryImpl implements DocumentHandlerFactory
{

    private Map handlers;
    private FolderHandler folderHanlder;

    /**
     *  
     */
    public DocumentHandlerFactoryImpl( Map handlers )
    {
        super();
        
        ArgUtil.assertNotNull(Map.class, handlers, this);        
        
        this.handlers = handlers;        
    }
    
    public DocumentHandlerFactoryImpl()
    {
        this(new HashMap());
             
    }

    /**
     * <p>
     * getDocumentHandler
     * </p>
     * 
     * @see org.apache.jetspeed.page.document.DocumentHandlerFactory#getDocumentHandler(java.lang.String)
     * @param documentType
     * @return
     */
    public DocumentHandler getDocumentHandler( String documentType ) throws UnsupportedDocumentTypeException
    {        
        if(handlers.containsKey(documentType))
        {
            return (DocumentHandler)handlers.get(documentType);
        }
        else
        {
            throw new UnsupportedDocumentTypeException("There are no DocumentHandlers defined for the type: "+documentType);
        }
    }


    /**
     * <p>
     * addDocumentHandler
     * </p>
     *
     * @see org.apache.jetspeed.page.document.DocumentHandlerFactory#registerDocumentHandler(org.apache.jetspeed.page.documenthandler.DocumentHandler)
     * @param documentHandler
     * @throws DocumentTypeAlreadyRegisteredException
     */
    public void registerDocumentHandler( DocumentHandler documentHandler ) throws DocumentTypeAlreadyRegisteredException
    {
        if(handlers.containsKey(documentHandler.getType()))
        {
            throw new DocumentTypeAlreadyRegisteredException(documentHandler.getType()+" has already been registered.");
        }
        
        handlers.put(documentHandler.getType(), documentHandler);
    }
    /**
     * <p>
     * getDocumentHandlerForPath
     * </p>
     *
     * @see org.apache.jetspeed.page.document.DocumentHandlerFactory#getDocumentHandlerForPath(java.lang.String)
     * @param documentPath
     * @return
     */
    public DocumentHandler getDocumentHandlerForPath( String documentPath ) throws UnsupportedDocumentTypeException
    {
        int dotIndex = documentPath.indexOf('.');
        
        if(dotIndex > -1)
        {
            try
            {
                return getDocumentHandler(documentPath.substring(dotIndex));
            }
            catch (UnsupportedDocumentTypeException e)
            {
                int lastSlash = documentPath.lastIndexOf(Node.PATH_SEPARATOR);
                if(lastSlash < 0)
                {
                    lastSlash = 0;
                }
                return getDocumentHandler(documentPath.substring(lastSlash));
            }
        }
        else
        {
            throw new UnsupportedDocumentTypeException("The path provided has no extension and may be a folder.");
        }
    }
}
